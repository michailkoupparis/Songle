package com.example.michailkoupparis.songle;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Home Screen
public class Home extends AppCompatActivity {

    public static final String CONTINUE_OR_NEW_GAME = "continue_or_new";
    private List<Song> song_list = new ArrayList<Song>();
    private NetworkReceiver receiver_song = new NetworkReceiver();
    // Song Attributes are saved here and can be accessed by all classes nedd to acces them
    public static SongAttributes song_attributes;
    public static final String CHANGE_TAG = "song_number_change_tag";
    public static final String CHANGE_KEY = "song_number_change_key";
    public static final String SCORE_TAG = "score_tag";
    public static final String SCORE_KEY = "score_key";
    public static final String DISTANCE_TAG = "distance_tag";
    public static final String DISTANCE_KEY = "distance_key";
    private static Boolean change;
    public static final String FIRST_TIME_TAG = "first_time_tag";
    public static final String FIRST_TIME_KEY = "first_time_key";
    // For test purposes return if it is the first time the Home Activity is invoked
    // to check if the tutorial pops up
    public static Boolean first_time = true;
    // For allowing other activities know if the Songs have been downloaded
    public static Boolean wifi = true;
    /*
    public static final String WIFI_TAG = "no_wifi_tag";
    public static final String WIFI_KEY = "no_wifi_key";
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // screen Update is for displaying score, distance walked, difficulty level and current song in play on home screen
        screenUpdate();

        // Check if it is the first time user access the game, if yes display the game's tutorial
        SharedPreferences first_time_help_pref =
                getSharedPreferences(FIRST_TIME_TAG, MODE_PRIVATE);

        first_time = first_time_help_pref.getBoolean(FIRST_TIME_KEY, true);

        // If it is the first time user access the game change the preference to false
        // so the next time will no longer be the first and call the game's tutorial
        // and call the game's tutorial
        if(first_time){
            SharedPreferences.Editor editor =
                    getSharedPreferences(FIRST_TIME_TAG, MODE_PRIVATE).edit();
            editor.putBoolean(FIRST_TIME_KEY, false);
            editor.apply();
            Intent activityChangeIntent = new Intent(Home.this, TutorialDialog.class);
            startActivity(activityChangeIntent);
        }

        // Call the receiver which downloads Song Attributes(numbers,tittles,artists,links) and saves them
        IntentFilter filter = new
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver_song = new NetworkReceiver();
        this.registerReceiver(receiver_song, filter);

        // Click Listener for help button which calls the game tutorial
        ImageButton help_button = (ImageButton) findViewById(R.id.help_btn);
        help_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(Home.this, TutorialDialog.class);
                startActivity(activityChangeIntent);
            }
        });

        // Check if the user can continues his previous game
        SharedPreferences change_previous =
                getSharedPreferences(CHANGE_TAG, MODE_PRIVATE);
        change = change_previous.getBoolean(CHANGE_KEY, false);

        // New game Button Click Listener
        final Button new_game_button = (Button) findViewById(R.id.new_game);
        new_game_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // If some preference change or it is the first time don't display game in progress warning
                if (change || first_time) {
                    // Go to the map screen where user can collect words
                    Intent activityChangeIntent = new Intent(Home.this, MapsActivity.class);
                    activityChangeIntent.putExtra(CONTINUE_OR_NEW_GAME, "NEW GAME");
                    startActivity(activityChangeIntent);
                }
                else {
                    // Warn user game is in progress
                    ViewDialogNewGame alert = new ViewDialogNewGame();
                    alert.showDialogNewGame(Home.this);
                }


            }
        });

        // Continue Button Listener
        final Button continue_button = (Button) findViewById(R.id.continue_game);
        continue_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // If some preference change then continue button works the same as new game
                // else the user can continues from where he lefts current song
                Intent activityChangeIntent = new Intent(Home.this, MapsActivity.class);
                if (change) {
                    activityChangeIntent.putExtra(CONTINUE_OR_NEW_GAME, "NEW GAME");

                } else {
                    activityChangeIntent.putExtra(CONTINUE_OR_NEW_GAME, "CONTINUE");

                }
                startActivity(activityChangeIntent);

            }
        });

        // Button listener for accessing Screen where found words are displayed and where user can give solutions
        final Button word_found_button = (Button) findViewById(R.id.words_found);
        word_found_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(Home.this, FoundWords.class);
                startActivity(activityChangeIntent);
            }
        });

        // Button listener for accessing Screen where user can select what song (by number) he wants to play
        final Button select_song_button = (Button) findViewById(R.id.go_to_song_selection);
        select_song_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(Home.this, SongSelection.class);
                startActivity(activityChangeIntent);
            }
        });

        // Button listener for accessing Screen where user can change difficulty and game mode
        Button settings_button = (Button) findViewById(R.id.settings_button);
        settings_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(Home.this, Settings.class);
                startActivity(activityChangeIntent);
            }
        });

        // Image Button listener for accessing Screen where user can find how score is calculated
        final ImageButton score_inf = (ImageButton) findViewById(R.id.score_information);
        score_inf.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(Home.this, ScoreHelp.class);
                startActivity(activityChangeIntent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // On Resume Update score,distance walked, difficulty level and song number on Home Screen
        screenUpdate();
    }

    public void onBackPressed() {
        Intent activityChangeIntent = new Intent(Home.this, Home.class);
        startActivity(activityChangeIntent);
        finish();
    }


    // Network Receiver for Downloading Songs Atributes
    public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null
                    && networkInfo.getType() ==
                    ConnectivityManager.TYPE_WIFI) {
                // Wi´Fi is connected, so use Wi´FI
                new DownloadXmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml");
            } else if (networkInfo != null
                    && networkInfo.getType() ==
                    ConnectivityManager.TYPE_MOBILE) {
                new DownloadXmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/songs.xml");
            } else {
                // No Wi´Fi and no permission, or no network connection
                // If no Wifi display warning to user
                /*
                SharedPreferences.Editor editor =
                        getSharedPreferences(WIFI_TAG, MODE_PRIVATE).edit();
                editor.putBoolean(WIFI_KEY, false);
                editor.apply();
                */
                wifi = false;
                ViewDialogWIFI alert = new ViewDialogWIFI();
                alert.showDialogWIFI(Home.this);
            }
        }
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return "Unable to load content. Check your network connection";
            } catch (XmlPullParserException e) {
                return "Error parsing XML";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Since it manages to download the files change the preference since now the app has access to internet
            /*
            SharedPreferences.Editor editor =
                    getSharedPreferences(WIFI_TAG, MODE_PRIVATE).edit();
            editor.putBoolean(WIFI_KEY, true);
            editor.apply();
            */
            wifi = true;
        }
    }

    private String loadXmlFromNetwork(String urlString) throws
            XmlPullParserException, IOException {
        StringBuilder result = new StringBuilder();
        try (InputStream stream = downloadUrl(urlString)) {
            // Parse stream as XML and save the results
            song_list = parse(stream);
            List<String> numbers = new ArrayList<>();
            List<String> titles = new ArrayList<>();
            List<String> artists = new ArrayList<>();
            List<String> links = new ArrayList<>();

            // Create one list for each attribute and then create a Song Attribute object which will hold all lists
            for (Song song : song_list) {
                numbers.add(song.number);
                titles.add(song.title);
                artists.add(song.artist);
                links.add(song.link);
            }
            song_attributes = new SongAttributes(numbers, titles, artists, links);

        }
        return result.toString();
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

    // Object of song attributes
    public static class Song {
        public final String number;
        public final String artist;
        public final String title;
        public final String link;

        // Song object creator
        private Song(String number, String artist, String title, String link) {
            this.number = number;
            this.artist = artist;
            this.title = title;
            this.link = link;
        }
    }

    // We don’t use namespaces
    private static final String ns = null;

    // Parse the xml file and create a list of Song objects
    List<Song> parse(InputStream in) throws XmlPullParserException,
            IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
                    false);
            parser.setInput(in, null);
            parser.nextTag();
            return readSongs(parser);
        } finally {
            in.close();
        }
    }

    private List<Song> readSongs(XmlPullParser parser) throws
            XmlPullParserException, IOException {
        List<Song> songs = new ArrayList<Song>();
        parser.require(XmlPullParser.START_TAG, ns, "Songs");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the song tag
            if (name.equals("Song")) {
                songs.add(readSong(parser));
            } else {
                skip(parser);
            }
        }
        return songs;
    }

    private Song readSong(XmlPullParser parser) throws
            XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Song");
        String number = null;
        String artist = null;
        String title = null;
        String link = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String name = parser.getName();
            if (name.equals("Number")) {
                number = readNumber(parser);
            } else if (name.equals("Artist")) {
                artist = readArtist(parser);
            } else if (name.equals("Title")) {
                title = readTitle(parser);
            } else if (name.equals("Link")) {
                link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return new Song(number, artist, title, link);
    }

    private String readNumber(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Number");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Number");
        return title;
    }

    private String readArtist(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Artist");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Artist");
        return summary;
    }

    private String readTitle(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Title");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Title");
        return summary;
    }


    private String readLink(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Link");
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "Link");
        return summary;
    }


    private String readText(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException,
            IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    // Method  for displaying score, distance walked, difficulty level and current song in play on home screen
    private void screenUpdate(){

        // Get previous score to be able to displayed it
        SharedPreferences score_previous =
                getSharedPreferences(SCORE_TAG, MODE_PRIVATE);
        Integer score = score_previous.getInt(SCORE_KEY, 0);
        // Display score in home screen
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setTypeface(scoreView.getTypeface(), Typeface.BOLD);
        scoreView.setText("SCORE:   " + score);

        // Get distance walked so far to be able to displayed it
        SharedPreferences distance_previous =
                getSharedPreferences(DISTANCE_TAG, MODE_PRIVATE);
        Float distance = distance_previous.getFloat(DISTANCE_KEY, 0);
        // Display distance walked on home screen
        TextView distance_view = (TextView) findViewById(R.id.distanceView);
        distance_view.setTypeface(distance_view.getTypeface(), Typeface.BOLD);
        String dist = String.format("%.2f", distance);
        distance_view.setText("DISTANCE WALKED:   " + dist + "meters");

        // Get difficulty level to displayed it
        SharedPreferences difficulty_pref =
                getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, MODE_PRIVATE);
        String diff_level = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");
        // Call setDifficulty to find the description of the difficulty given the numerical value(e.g 1 -> Very Hard)
        String difficulty = setDifficulty(diff_level);
        // Display current difficulty to home screen
        TextView dif_level_view = (TextView) findViewById(R.id.difficultyView);
        dif_level_view.setTypeface(dif_level_view.getTypeface(), Typeface.BOLD);
        dif_level_view.setText("SELECTED DIFFICULTY:   " + difficulty);

        // Get Song Number to displayed it
        SharedPreferences song_num_pref =
                getSharedPreferences(SongSelection.SONG_PREF_TAG, MODE_PRIVATE);
        String song_num = song_num_pref.getString(SongSelection.SONG_PREF_KEY, "01");
        // Display song number to home screen
        TextView song_num_view = (TextView) findViewById(R.id.songNumberView);
        song_num_view.setTypeface(song_num_view.getTypeface(), Typeface.BOLD);
        song_num_view.setText("SONG NUMBER:   " + song_num);
    }

    // Method for finding description of difficulty given the corresponding number (e.g 1-> Very Hard)
    private String setDifficulty(String diffulty_num) {
        String difficulty = "";
        switch (diffulty_num) {
            case "5":
                difficulty = "Very Easy";
                break;
            case "4":
                difficulty = "Easy";
                break;
            case "3":
                difficulty = "Normal";
                break;
            case "2":
                difficulty = "Hard";
                break;
            case "1":
                difficulty = "Very Hard";
                break;
        }
        return difficulty;
    }

    // Dialog for warning user that a game is in progress if he press the new game button
    public class ViewDialogNewGame {

        public void showDialogNewGame(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.warning_dialog);


            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(R.string.sure_new_game);

            // Ok button is when the user does not cares if a game is already in progress
            Button ok_button = (Button) dialog.findViewById(R.id.btn_ok_dialog);
            ok_button.setText(R.string.yes_new_game);
            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent activityChangeIntent = new Intent(Home.this, MapsActivity.class);
                    activityChangeIntent.putExtra(Home.CONTINUE_OR_NEW_GAME, "NEW GAME");
                    startActivity(activityChangeIntent);
                }
            });

            // Cancel Button is when the user decides that he does not want to loose the progress
            Button cancel_button = (Button) dialog.findViewById(R.id.btn_cancel_dialog);
            cancel_button.setText(R.string.no_new_game);
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

            dialog.show();

        }

    }

    // Dialog for warning the user for no internet connection
    private class ViewDialogWIFI {

        private void showDialogWIFI(final Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.wifi_location_warning);

            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(R.string.no_wifi);

            // Ok button is for user to cancel the dialog
            Button ok_button = (Button) dialog.findViewById(R.id.btn_ok_dialog);
            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If there is no wifi initialise an empty song attributes object such that when
                    // methods call its function the app will not crush
                    song_attributes = new SongAttributes();
                    dialog.dismiss();
                }
            });

            // Retry button is for trying to download songs again if no internet connection is reached the dialog will pop up again
            Button retry_button = (Button) dialog.findViewById(R.id.btn_retry_dialog);
            retry_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentFilter filter = new
                            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    receiver_song = new NetworkReceiver();
                    activity.registerReceiver(receiver_song, filter);
                    dialog.dismiss();

                }
            });

            dialog.show();

        }

    }



}