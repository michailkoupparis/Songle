package com.example.michailkoupparis.songle;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

public class WelcomeScreen extends AppCompatActivity {

    private static int Welcome_time = 3000;
    public static Context WELCOME_CONTEXT ;
    public static Set<String> song_founds = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        WELCOME_CONTEXT = WelcomeScreen.this;

        // Create a file where all found songs are saved
        // if the file already exist retrieve and save already found songs
        if (fileExist("found_songs_file"+".txt")){
            String song_found = load(WelcomeScreen.this);
            if(song_found != null) {
                for (String s : song_found.split("\n")) {
                    song_founds.add(s);
                }
            }
        }
        else{
            saveFile(WelcomeScreen.this,"");
        }


        // Setting time for Welecome Screen before Home screen is reached
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){

                Intent homeIntent = new Intent(WelcomeScreen.this,Home.class);
                startActivity(homeIntent);
                finish();
            }
        },Welcome_time);
    }

    // Method for saving in internal txt file the songs found already
    public static boolean saveFile(Context context, String mytext){
        try {
            FileOutputStream fos = context.openFileOutput("found_songs_file"+".txt", Context.MODE_PRIVATE);
            Writer out = new OutputStreamWriter(fos);
            // Add new song to already found songs create a string from already found songs and saved it
            if (!mytext.equals("") || !song_founds.contains(mytext)) {
                song_founds.add(mytext);
            }
            String found = "";
            for (String s:song_founds) {
                found = found + s + "\n";
            }
            out.write(found);
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method for retrieving already found songs from the internal storage and save them
    public String load(Context context){
        try {
            FileInputStream fis = context.openFileInput("found_songs_file"+".txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            String s = "";
            String txt = "";
            while ((s = r.readLine()) != null) {
                txt += s + "\n";
            }
            r.close();
            return txt;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method for checking if the txt file alreday exist
    public boolean fileExist(String fname){
        File file = WelcomeScreen.this.getFileStreamPath(fname);
        return file.exists();
    }
}
