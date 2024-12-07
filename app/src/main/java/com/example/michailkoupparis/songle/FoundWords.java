package com.example.michailkoupparis.songle;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Where Found Words are displayed
// Also here user can give or get the solution and get hints
public class FoundWords extends AppCompatActivity {

    private String song_num = "";
    private NetworkReceiverLyrics receiver_lyrics = new NetworkReceiverLyrics();
    private Set<String> found_mark_names = new HashSet<>();
    private List<String[]> lyrics = new ArrayList<String[]>();
    private  ListView word_displayer;
    private  List<String> listItems = new ArrayList<String>();
    private ArrayAdapter<String> adapter ;
    private Context context ;
    private Boolean setting_song_change ;
    private  String dif_level ;
    private Set<String> found_songs  = new HashSet<>();
    private List<String> songs_titles = new ArrayList<String>();
    private List<String> songs_artists = new ArrayList<>();
    public static final String Hint_PENALTY_TAG = "hint_penalty_tag";
    public static final String Hint_PENALTY_KEY = "hint_penalty_key";
    private Boolean mode_was_anywhere;
    private Boolean wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_words);

        // Get the current game mode_was_anywhere
        SharedPreferences mode_pref =
                getSharedPreferences(Settings.MODE_WAS_ANYWHERE_TAG, MODE_PRIVATE);

        mode_was_anywhere = mode_pref.getBoolean(Settings.MODE_WAS_ANYWHERE_KEY, true);

        // Get the current song number
        SharedPreferences song_number_pref =
                getSharedPreferences(SongSelection.SONG_PREF_TAG, MODE_PRIVATE);

        song_num = song_number_pref.getString(SongSelection.SONG_PREF_KEY, "01");

        // Get the difficulty level
        SharedPreferences dif_pref =
                getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, MODE_PRIVATE);

        dif_level = dif_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "01");

        // See if settings are changed
        SharedPreferences pref_change=
                getSharedPreferences(Home.CHANGE_TAG, MODE_PRIVATE);

        setting_song_change = pref_change.getBoolean(Home.CHANGE_KEY, false);

        // Get all the songs found already
        found_songs = WelcomeScreen.song_founds;

        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver_lyrics = new NetworkReceiverLyrics();
        // For downloading the xml file which has current song's lyrics
        this.registerReceiver(receiver_lyrics, filter);

        // Check if there is wifi connection
        wifi = Home.wifi;


        // If there is wifi retrieve song's titles and artists
        if (wifi) {
            songs_titles = Home.song_attributes.getTitles();
            songs_artists = Home.song_attributes.getArtists();
        }
        else{
            songs_titles = new ArrayList<>();
            songs_artists = new ArrayList<>();
        }

        // Initialise word container for representing found words
        context = FoundWords.this;
        word_displayer = (ListView) findViewById(R.id.words_container);

        // Hint button , when is pressed a dialog for hints pop ups
        ImageButton hint_btn = (ImageButton) findViewById(R.id.btn_hint);

        hint_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                ViewDialogHint alert = new ViewDialogHint();
                alert.showDialogHint(FoundWords.this);
            }
        });

        // Edit Text where user can guess the solution
        final EditText solution_container = (EditText) findViewById(R.id.solution_container);

        // Submit solution Button listener
        Button submit_solution = (Button) findViewById(R.id.try_solution);
        submit_solution.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Get the song number
                SharedPreferences song_number_pref =
                        getSharedPreferences(SongSelection.SONG_PREF_TAG, MODE_PRIVATE);

                song_num = song_number_pref.getString(SongSelection.SONG_PREF_KEY, "01");

                // Get the solution the user gave and then the current song's tittle
                String solution = solution_container.getText().toString();
                int song_number = Integer.parseInt(song_num);

                // Check if there was internet and the Song titles are downloaded
                // If yes the songs_titles will not be empty
                // If no then a Message Indicate will just pop up
                if (!songs_titles.isEmpty()) {
                    String this_song = songs_titles.get(song_number - 1);

                    // Check if the solution is correct
                    if (solution.equals(this_song) && !songs_titles.isEmpty()) {

                        // Change the song to the next song in line since current song is found
                        Integer next_song = Integer.parseInt(song_num) + 1;
                        String next_song_update = "";
                        if (next_song > songs_titles.size()) {
                            next_song_update = "01";
                        } else {
                            if (next_song < 10) {
                                next_song_update = "0" + next_song + "";
                            } else {
                                next_song_update = next_song + "";

                            }
                        }
                        SharedPreferences.Editor editor_song_number =
                                getSharedPreferences(SongSelection.SONG_PREF_TAG, MODE_PRIVATE).edit();
                        editor_song_number.putString(SongSelection.SONG_PREF_KEY, next_song_update);
                        editor_song_number.apply();

                        // Total Score is for holding the points the user gets for the correct answer
                        int total_score = 0;
                        // Penalty is for holding if the user took hint
                        int penalty = 0;
                        // Bonus depends on the mode_was_anywhere
                        int bonus = 0;
                        // if song is not found already add it to found songs and not update score else just tell the user he founds the solution
                        if (!found_songs.contains(solution)) {
                            // Add the song to found songs and save it to the internal file holding found songs
                            found_songs.add(solution);
                            WelcomeScreen.saveFile(WelcomeScreen.WELCOME_CONTEXT, solution + "\n");

                            // Check if hint is taken
                            SharedPreferences hint_penalty_pref =
                                    getSharedPreferences(Hint_PENALTY_TAG, MODE_PRIVATE);

                            Boolean hint_taken = hint_penalty_pref.getBoolean(Hint_PENALTY_KEY, false);

                            if (hint_taken) {
                                penalty = 5;
                            }

                            // Check for bonus according mode_was_anywhere
                            if (!mode_was_anywhere) {
                                bonus = 10;
                            }

                            // Get the score for this difficulty level
                            int dif_level_score = Integer.parseInt(dif_level);
                            dif_level_score = Math.abs(dif_level_score * 10 - 60);

                            // Calculate total score and updates total score
                            total_score = dif_level_score + bonus - penalty;

                            SharedPreferences score_previous =
                                    getSharedPreferences(Home.SCORE_TAG, MODE_PRIVATE);

                            int prev_score = score_previous.getInt(Home.SCORE_KEY, 0);

                            SharedPreferences.Editor editor_score =
                                    getSharedPreferences(Home.SCORE_TAG, MODE_PRIVATE).edit();
                            editor_score.putInt(Home.SCORE_KEY, total_score + prev_score);
                            editor_score.apply();
                        }

                        // Since it found the song clear word found list and let the app known a new song is in progress
                        SharedPreferences.Editor editor_change_pref =
                                getSharedPreferences(Home.CHANGE_TAG, MODE_PRIVATE).edit();
                        editor_change_pref.putBoolean(Home.CHANGE_KEY, true);
                        editor_change_pref.apply();
                        setting_song_change = true;

                        SharedPreferences.Editor editor =
                                getSharedPreferences(MapsActivity.FOUND_MARKERS_TAG, MODE_PRIVATE).edit();
                        editor.putStringSet(MapsActivity.FOUND_MARKERS_KEY, new HashSet<String>());
                        editor.apply();

                        // Call the Dialog for correct solution
                        ViewDialogSolutionFound alert = new ViewDialogSolutionFound();
                        alert.showDialogSolutionFound(FoundWords.this, this_song, total_score);

                    } else {
                        // Call the dialog for wrong solution
                        ViewDialogWrongAnswer alert = new ViewDialogWrongAnswer();
                        alert.showDialogWrongAnswer(FoundWords.this);
                    }
                }
                else{
                    Toast.makeText(FoundWords.this, "Wifi was Disabled and the Game \n" +
                                    "was not able to download Songs!!" +
                                    "\nEnable Internet and Go back to Home to Download them",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Button for getting the solution if you are stuck
        Button give_solution_button = (Button) findViewById(R.id.solve);
        give_solution_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Calls the dialog to warn the user
                ViewDialogGetSolution alert = new ViewDialogGetSolution();
                alert.showDialogGetSolution(FoundWords.this);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // get the mode_was_anywhere
        SharedPreferences mode_pref =
                getSharedPreferences(Settings.MODE_WAS_ANYWHERE_TAG, MODE_PRIVATE);

        mode_was_anywhere = mode_pref.getBoolean(Settings.MODE_WAS_ANYWHERE_KEY, false);

        // get the song number
        SharedPreferences song_number_pref =
                getSharedPreferences(SongSelection.SONG_PREF_TAG, MODE_PRIVATE);

        song_num = song_number_pref.getString(SongSelection.SONG_PREF_KEY, "01");

        // get the difficulty level
        SharedPreferences dif_pref =
                getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, MODE_PRIVATE);

        dif_level = dif_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "01");

        // Get if some preference is changed
        SharedPreferences pref_change=
                getSharedPreferences(Home.CHANGE_TAG, MODE_PRIVATE);

        setting_song_change = pref_change.getBoolean(Home.CHANGE_KEY, false);

        // Get the song attributes
        found_songs = WelcomeScreen.song_founds;

        // CLear the lost of found words
        word_displayer.setAdapter(null);
    }

    // On back press return to the home screen
    public void onBackPressed() {
        Intent activityChangeIntent = new Intent(FoundWords.this, Home.class);
        startActivity(activityChangeIntent);
        finish();
    }

    // For downloading Current Song's lyrics
    public class NetworkReceiverLyrics extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null
                    && networkInfo.getType() ==
                    ConnectivityManager.TYPE_WIFI) {
                // Wi´Fi is connected, so use Wi´FI
                new DownloadTxtTask().execute("http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/"+song_num+"/lyrics.txt");
            } else if (networkInfo != null
                    && networkInfo.getType() ==
                    ConnectivityManager.TYPE_MOBILE){
                new DownloadTxtTask().execute("http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/"+song_num+"/lyrics.txt");
            }
            else {
                // No Wi´Fi and no permission, or no network connection
            }
        }
    }

    private class DownloadTxtTask extends AsyncTask<String, Void, List<String> > {
        @Override
        protected List<String> doInBackground(String... urls) {
            try {
                return loadTxtFromNetwork(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> result) {

            // If preferences are changed before accessing the map and collecting new words then clear the previous found mark names
            if (setting_song_change){
                found_mark_names = new HashSet<>();
            }
            // else retrieve the found mark names collected in the MapActivity screen
            else {
                SharedPreferences found_pref =
                        getSharedPreferences(MapsActivity.FOUND_MARKERS_TAG, MODE_PRIVATE);

                found_mark_names = found_pref.getStringSet(MapsActivity.FOUND_MARKERS_KEY, new HashSet<String>());
            }

            // Go through the lyrics of the song
            for (String s: result){
                // For each row find the words and add them to lyrics
                String[] words = s.split("\\s+");
                for (int i = 0; i < words.length; i++) {

                    words[i] = words[i].replaceAll("[^\\w]", "");
                }
                lyrics.add(words);

            }

            // Go through the found markers and find for which word they correspond
            for (String name : found_mark_names) {
                // Find the corresponding line and column on the lyrics text
                String[] place = name.split(":");
                Integer line_num = Integer.parseInt(place[0]);
                int column_num = Integer.parseInt(place[1]);

                // Add item to adapter
                listItems.add(lyrics.get(line_num - 1)[column_num - 1]);
            }
            // Display the words in the user screen on a list
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listItems);
            word_displayer.setAdapter(adapter) ;
        }
    }

    private List<String> loadTxtFromNetwork(String urlString) throws IOException {
        List<String> result = new ArrayList<>() ;
        try (InputStream stream = downloadUrl(urlString)) {
            // parse the stream as text and save it as a list of strings (one entry for each line)
            result = parseText(stream);

        }
        return result;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Also available: HttpsURLConnection
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    // Method for parsing the txt file containing the current song's lyrics
    // and saved to a List of Strings each String is a row of the file
    private List<String> parseText (InputStream in) throws IOException {

        List<String> all_lines = new ArrayList<>();
        try{
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            BufferedReader parser = new BufferedReader(isr);
            String line;
            try {
                while ((line = parser.readLine()) != null) {
                    all_lines.add(line);
                }
            } finally {
                parser.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return all_lines;
    }


    // Dialog for warning user that he does not get points for given solutions
    private class ViewDialogGetSolution {

        private void showDialogGetSolution(Activity activity){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.warning_dialog);



            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(R.string.sure_solve);

            // Button for choosing to get the solution
            Button ok_button = (Button) dialog.findViewById(R.id.btn_ok_dialog);
            ok_button.setText(R.string.yes_solve_song);
            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Check if there was internet and the Song titles are downloaded
                    // If yes the songs_titles will not be empty
                    // If no then a Message Indicate will just pop up
                    if (!songs_titles.isEmpty()) {
                        int song_number = Integer.parseInt(song_num);
                        String this_song = songs_titles.get(song_number - 1);

                        // if song is not found already add it to found songs
                        if (!found_songs.contains(this_song)) {
                            found_songs.add(this_song);
                            WelcomeScreen.saveFile(WelcomeScreen.WELCOME_CONTEXT, this_song + "\n");
                        }

                        // Change the song number to the next song in line since current song is found
                        SharedPreferences song_number_pref =
                                getSharedPreferences(SongSelection.SONG_PREF_TAG, MODE_PRIVATE);

                        song_num = song_number_pref.getString(SongSelection.SONG_PREF_KEY, "01");

                        Integer next_song = Integer.parseInt(song_num) + 1;
                        String next_song_update = "";
                        if (next_song > songs_titles.size()) {
                            next_song_update = "01";
                        } else {
                            if (next_song < 10) {
                                next_song_update = "0" + next_song + "";
                            } else {
                                next_song_update = next_song + "";

                            }
                        }
                        SharedPreferences.Editor editor_song_number =
                                getSharedPreferences(SongSelection.SONG_PREF_TAG, MODE_PRIVATE).edit();
                        editor_song_number.putString(SongSelection.SONG_PREF_KEY, next_song_update);
                        editor_song_number.apply();

                        // Since it found the song clear word found list and let the app know that a new song is in progress
                        SharedPreferences.Editor editor_change_pref =
                                getSharedPreferences(Home.CHANGE_TAG, MODE_PRIVATE).edit();
                        editor_change_pref.putBoolean(Home.CHANGE_KEY, true);
                        editor_change_pref.apply();

                        SharedPreferences.Editor editor =
                                getSharedPreferences(MapsActivity.FOUND_MARKERS_TAG, MODE_PRIVATE).edit();
                        editor.putStringSet(MapsActivity.FOUND_MARKERS_KEY, new HashSet<String>());
                        editor.apply();

                        // Call the dialog to display the correct solution
                        ViewDialogSolutionFound alert = new ViewDialogSolutionFound();
                        alert.showDialogSolutionFound(FoundWords.this, this_song, 0);

                    }
                    else{
                        Toast.makeText(FoundWords.this, "Wifi was Disabled and the Game \n" +
                                        "was not able to download Songs!!" +
                                        "\nEnable Internet and Go back to Home to Download them",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // For allowing the user to change mind and continue trying
            Button cancelButton = (Button) dialog.findViewById(R.id.btn_cancel_dialog);
            cancelButton.setText(R.string.no_dont_solve_song);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }

    // Dialog for display solution after the user give the correct answer or gets the solution
    // and also display the points awarded
    private class ViewDialogSolutionFound {

        private void showDialogSolutionFound(Activity activity,String song_name, int points){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.solution_found);

            // Since the song is found hint must be false to not penalize new song without a reason.
            SharedPreferences.Editor editor_hint =
                    getSharedPreferences(Hint_PENALTY_TAG, MODE_PRIVATE).edit();
            editor_hint.putBoolean(Hint_PENALTY_KEY, false);
            editor_hint.apply();

            // Get the current game mode
            SharedPreferences mode_pref =
                    getSharedPreferences(Settings.MODE_TAG, MODE_PRIVATE);

            String mode = mode_pref.getString(Settings.MODE_KEY, "near");

            if (mode.equals("near")) {
                // Since the song is found and mode now is near
                // mode_was_anywhere must be false to not penalize new song without a reason.
                SharedPreferences.Editor editor_mode_change =
                        getSharedPreferences(Settings.MODE_WAS_ANYWHERE_TAG, MODE_PRIVATE).edit();
                editor_mode_change.putBoolean(Settings.MODE_WAS_ANYWHERE_KEY, false);
                editor_mode_change.apply();
            }

            // Display Solution And Awarded Points
            TextView solutionView = (TextView) dialog.findViewById(R.id.solution_dialog);
            solutionView.setText("Solution was:\n "+ song_name+"\n\n Awarded Points: "+points);

            // The user selects to either continue on the next song and so the app sent him to the map to collect words
            // Or go to Song Selection and select whatever song he wants

            Button next_song = (Button) dialog.findViewById(R.id.btn_nextSong);
            next_song.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent activityChangeIntent = new Intent(FoundWords.this, MapsActivity.class);
                    activityChangeIntent.putExtra(Home.CONTINUE_OR_NEW_GAME, "NEW GAME");
                    startActivity(activityChangeIntent);
                }
            });


            Button cancelButton = (Button) dialog.findViewById(R.id.btn_selectDifferent);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent activityChangeIntent = new Intent(FoundWords.this, SongSelection.class);
                    startActivity(activityChangeIntent);
                }
            });
            dialog.show();

        }
    }

    // Dialog for Informing the user that he gave the wrong answer
    private class ViewDialogWrongAnswer {

        private void showDialogWrongAnswer(Activity activity){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.wrong_answer);

            Button ok_button = (Button) dialog.findViewById(R.id.btn_ok_dialog);
            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  dialog.dismiss();

                }
            });


            dialog.show();

        }
    }

    // Dialog For warning the user that the hints cost points
    private class ViewDialogHint {

        private void showDialogHint(Activity activity){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.hint);

            // Get if a hint is taken for this song
            SharedPreferences hint_penelty_pref =
                    getSharedPreferences(Hint_PENALTY_TAG, MODE_PRIVATE);

            Boolean hint_taken = hint_penelty_pref.getBoolean(Hint_PENALTY_KEY, false);

            // Get song number
            SharedPreferences song_number_pref =
                    getSharedPreferences(SongSelection.SONG_PREF_TAG, MODE_PRIVATE);

            song_num = song_number_pref.getString(SongSelection.SONG_PREF_KEY, "01");
            final int song_number = Integer.parseInt(song_num);
            // Check if there was internet and the Song titles are downloaded
            // If yes the songs_artists will not be empty
            // If no then a Message Indicate will just pop up
            if(!songs_artists.isEmpty()) {
                // Get the artist of the song to displayed it as a hint
                String this_artist = songs_artists.get(song_number - 1);
                // if the hint is already taken display y=the artist as hint
                if (hint_taken) {

                    TextView hintView = (TextView) dialog.findViewById(R.id.hint_container);
                    hintView.setText(this_artist);

                }
            }
            else{
                Toast.makeText(FoundWords.this, "Wifi was Disabled and the Game \n" +
                                "was not able to download Songs!!" +
                                "\nEnable Internet and Go back to Home to Download them",
                        Toast.LENGTH_SHORT).show();
            }

            // Button for showing the artist
            Button show_button = (Button) dialog.findViewById(R.id.btn_ok_dialog);
            show_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if there was internet and the Song titles are downloaded
                    // If yes the songs_artists will not be empty
                    // If no then a Message Indicate will just pop up
                    if(!songs_artists.isEmpty()) {
                        SharedPreferences hint_penelty_pref =
                                getSharedPreferences(Hint_PENALTY_TAG, MODE_PRIVATE);

                        Boolean hint_taken = hint_penelty_pref.getBoolean(Hint_PENALTY_KEY, false);

                        // if the hint is already taken display a message indicating so
                        if (hint_taken) {
                            Toast.makeText(FoundWords.this, "Hint Was Already Taken!!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Display the artist and inform the app that a hint is taken
                            TextView hintView = (TextView) dialog.findViewById(R.id.hint_container);
                            String this_artist = songs_artists.get(song_number - 1);
                            hintView.setText(this_artist);
                            SharedPreferences.Editor editor_hint =
                                    getSharedPreferences(Hint_PENALTY_TAG, MODE_PRIVATE).edit();
                            editor_hint.putBoolean(Hint_PENALTY_KEY, true);
                            editor_hint.apply();
                        }
                    }
                    else{
                        Toast.makeText(FoundWords.this, "Wifi was Disabled and the Game \n" +
                                        "was not able to download Songs!!" +
                                        "\nEnable Internet and Go back to Home to Download them",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });

            // Cancel button for allowing the user to regret taking a hint
            Button cancel_button = (Button) dialog.findViewById(R.id.btn_cancel_dialog);
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                }
            });

            dialog.show();

        }
    }

    // for Collected Words Test
    public  ListView collectedWordsContainer(){
        return word_displayer;
    }

}
