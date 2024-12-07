package com.example.michailkoupparis.songle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Song Selection Screen
public class SongSelection extends AppCompatActivity {


    private Integer song_num;
    List<String> song_numbers;
    public static final String SONG_PREF_TAG = "song_pref_tag";
    public static final String SONG_PREF_KEY = "song_pref_key";
    private Set<String> found_songs  = new HashSet<>();
    private Boolean wifi =true;
    GridView gridView ;    // For displaying Songs by numbers
    View current_song_view ; // For for in grid of current song
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_selection);

        // Button listener for accessing Song Player screen where found songs are displayed and can be played
        Button go_to_song_player_button = (Button) findViewById(R.id.go_to_song_player);
        go_to_song_player_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(SongSelection.this, SongPlayer.class);
                startActivity(activityChangeIntent);
            }
        });

        // See if there is wifi connection when the Home Activity is reach which means the Songs have been downloaded
        wifi = Home.wifi;

        // If yes get all the songs numbers for displaying them else nothing will be displayed
        if (wifi) {
            song_numbers = Home.song_attributes.getNumbers();
        }
        else{
            song_numbers = new ArrayList<>();
        }



        // Get the previous selected song number
        SharedPreferences song_number_pref =
                getSharedPreferences(SONG_PREF_TAG, MODE_PRIVATE);

        song_num = Integer.parseInt(song_number_pref.getString(SONG_PREF_KEY, "01"));


        // Initialise the hashset holding already found songs
        found_songs = WelcomeScreen.song_founds;

        // TextView for letting the user know which song was selected when the screen was accessed
        // such that is he change the song and forgets which ones was selected previous to be able before
        // leaving the screen to re-select that song.
        TextView current_num = (TextView) findViewById(R.id.current_song_num);
        current_num.setText("Previously selected song:   "+song_num);

        // Initialise the grid view and call its adapter
        gridView = (GridView) findViewById(R.id.song_selection_grid);
        gridView.setAdapter(new TextAdapter(this));

        // Sets listeners for the grid cells - (for each song song number button)
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent,final View view,final int position, long id) {

                // if the song number is the same as the one selected a message indicate that will pop up
                if (song_num == position+1){
                    Toast.makeText(SongSelection.this, "This Song is Already Selected",
                            Toast.LENGTH_SHORT).show();
                }
                // Else a dialog for confirmation pops up
                else {
                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(SongSelection.this, parent, view, position);
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        // On resume update the list with found songs
        found_songs = WelcomeScreen.song_founds;


        // Get the previous selected song number
        SharedPreferences song_number_pref =
                getSharedPreferences(SONG_PREF_TAG, MODE_PRIVATE);

        song_num = Integer.parseInt(song_number_pref.getString(SONG_PREF_KEY, "01"));

        // See if there is wifi connection when the Home Activity is reach which means the Songs have been downloaded
        wifi = Home.wifi;

        // If yes get all the songs numbers for displaying them else nothing will be displayed
        if (wifi) {
            song_numbers = Home.song_attributes.getNumbers();
        }
        else{
            song_numbers = new ArrayList<>();
        }


    }

    public void onBackPressed() {
        Intent activityChangeIntent = new Intent(SongSelection.this, Home.class);
        startActivity(activityChangeIntent);
        finish();
    }



    // Method for setting the Grid View
    private class TextAdapter extends BaseAdapter {

        Context context;

        public TextAdapter(Context context) {
            this.context = context;
        }

        @Override
        // One cell for every Song
        public int getCount() {

            return song_numbers.size();
        }

        @Override
        public Object getItem(int position) {

            return song_numbers.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Each song(grid cell) is represented on user's screen by its number
            TextView text = new TextView(this.context);

            text.setText(song_numbers.get(position) );

            text.setGravity(Gravity.CENTER);

            text.setLayoutParams(new GridView.LayoutParams(144, 144));

            // if no wifi then found songs will be empty
            // if it is not add a tick next to every already found song
            if(wifi) {
                if (found_songs.contains(Home.song_attributes.getTitles().get(position))) {
                    text.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.check_icon, 0);
                }
            }

            // Add a deep sky blue background to the selected song number(grid cell)
            if (position == song_num-1) {
                text.setBackgroundColor(Color.parseColor("#00BFFF"));
                current_song_view = text;
            }
            // Add a different background to every other song number(cgrid ell)
            else{
                text.setBackgroundResource(R.drawable.grid_items_borders);

            }

            return text;

        }
    }

    private class ViewDialog {

        private void showDialog(Activity activity, final AdapterView<?> parent,final View view, final int position){
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.warning_dialog);


            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(R.string.sure_change_song);

            // Button for change song
            Button change_button = (Button) dialog.findViewById(R.id.btn_ok_dialog);
            change_button.setText(R.string.yes_change_song);
            change_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // if the user select to change song then the hint penalty has to change since a new song is in play
                    SharedPreferences.Editor editor_hint =
                            getSharedPreferences(FoundWords.Hint_PENALTY_TAG, MODE_PRIVATE).edit();
                    editor_hint.putBoolean(FoundWords.Hint_PENALTY_KEY, false);
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

                    // Since the song is change the change preference is set to true such that when the map is accessed
                    // on continue mode the previous markers will not appear
                    SharedPreferences.Editor editor_change =
                            getSharedPreferences(Home.CHANGE_TAG, MODE_PRIVATE).edit();
                    editor_change.putBoolean(Home.CHANGE_KEY, true);
                    editor_change.apply();

                    // if no wifi then found songs will be empty
                    // if it is not pop a message to let user knows that no points given for already found songs
                    if(wifi) {
                        if (found_songs.contains(Home.song_attributes.getTitles().get(position))) {
                            Toast.makeText(SongSelection.this, "This song is already found no points will be awarded!!",
                                    Toast.LENGTH_SHORT).show();
                            }
                        }

                    // Get the GridView selected/clicked item text
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    // Change the background of the cells such that this cell has now deep sky blue background and every other
                    // the same not deep sky blue background to let the user know which one is selected.
                    view.setBackgroundColor(Color.parseColor("#00BFFF"));
                    current_song_view.setBackgroundResource(R.drawable.grid_items_borders);
                    current_song_view = view;

                    // Update the song preference to hold the current selected song
                    SharedPreferences.Editor editor =
                            getSharedPreferences(SONG_PREF_TAG, MODE_PRIVATE).edit();
                    editor.putString(SONG_PREF_KEY, selectedItem);
                    editor.apply();
                    song_num = Integer.parseInt(selectedItem);


                    dialog.dismiss();

                }
            });

            // Cancel Button to alloiw the user regret changing the song
            Button cancelButton = (Button) dialog.findViewById(R.id.btn_cancel_dialog);
            cancelButton.setText(R.string.no_dont_change_song);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }




}





