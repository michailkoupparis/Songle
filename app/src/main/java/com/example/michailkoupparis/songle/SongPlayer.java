package com.example.michailkoupparis.songle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Activity for displaying found Songs and be able to play them in youtube
public class SongPlayer extends AppCompatActivity {
    private  List<String> listItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);

        // Initialise the List String holding already found songs
        // Also for each song the number of the song in the Online File will added
        // This way the user can now which song he founds correspond to which number
        listItems = new ArrayList<String>();

        // See if there is wifi connection when the home is reached if there is not then the songs's attributes have not been downloaded
       Boolean wifi = Home.wifi;

        List<String> song_titles = new ArrayList<>();

        // If there is no wifi when the Home is reached the Songs's Atrributes are not downloaded
        // So as the links thus the ListView should be empty
        if (wifi) {
            song_titles = Home.song_attributes.getTitles();
        }

        Set<String> found_songs = WelcomeScreen.song_founds;

        int i = 0;
        for (String song : song_titles) {
            if (found_songs.contains(song)) {
                String num = "" + Home.song_attributes.getNumbers().get(i);
                listItems.add(num + ":  " + song);
            }
            i++;
        }
        // Get the List View where found songs are being displayed and add all the found songs
        final ListView found_songs_displayer = (ListView) findViewById(R.id.found_songs_container);
        ArrayAdapter adapter = new ArrayAdapter<String>(SongPlayer.this, android.R.layout.simple_list_item_1, listItems);
        found_songs_displayer.setAdapter(adapter) ;

        // Set click listeners for the List view
        found_songs_displayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<? > arg0, View arg1, int position, long id) {
                // Get the text of the item
                String entry = (String) found_songs_displayer.getAdapter().getItem(position);
                // Get the num e.g the string before : since each entry is of the type num: song title
                // Here it does not simply take the first two characters because the songs of the game may exceed 100
                Integer i = 0;
                String s_num = "";
                while (entry.charAt(i)!=':'){
                    s_num = s_num + (entry.charAt(i));
                    i++;
                }
                Integer num  = Integer.parseInt(s_num);
                // Get the link of the song represented in this listView item
                String link = Home.song_attributes.getLinks().get(num-1);
                // play the song in youtube or open youtube in a web browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
            }
        });

    }

    // for SongPlayerTest
    public  List<String> listElements(){
     return listItems;
    }
}
