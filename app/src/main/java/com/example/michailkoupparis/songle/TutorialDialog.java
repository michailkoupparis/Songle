package com.example.michailkoupparis.songle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// First Screen of the Help Tutorial with overall game explanation
public class TutorialDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_dialog);

        this.setFinishOnTouchOutside(false);

        // Button Listeners for next button for navigating when user is on the game's tutorial
        // No previous Button because is the first in the navigation

        Button next_dialog_button = (Button) findViewById(R.id.next_dialog);
        next_dialog_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(TutorialDialog.this, HomeTutorial.class);
                startActivity(activityChangeIntent);
                finish();
            }
        });

        // Button Listeners for each screen explanation on the game's tutorial

        Button home_dialog_button = (Button) findViewById(R.id.home_dialog);
        home_dialog_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(TutorialDialog.this, HomeTutorial.class);
                startActivity(activityChangeIntent);
                finish();
            }
        });


        Button map_dialog_button = (Button) findViewById(R.id.map_dialog);
        map_dialog_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(TutorialDialog.this, MapTutorial.class);
                startActivity(activityChangeIntent);
                finish();

            }
        });


        Button setting_dialog_button = (Button) findViewById(R.id.settings_dialog);
        setting_dialog_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(TutorialDialog.this, SettingsTutorial.class);
                startActivity(activityChangeIntent);
                finish();

            }
        });

        final Button found_words_dialog_button = (Button) findViewById(R.id.word_founds_dialog);
        found_words_dialog_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(TutorialDialog.this, FoundWordsTutorial.class);
                startActivity(activityChangeIntent);
                finish();

            }
        });

        Button settings_button = (Button) findViewById(R.id.song_selection_dialog);
        settings_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(TutorialDialog.this, SongSelectionTutorial.class);
                startActivity(activityChangeIntent);
                finish();

            }
        });

        // Cancel button for user to leave the game's tutorial and return to home screen
        Button cancel_button = (Button) findViewById(R.id.cancel_dialog);
        cancel_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(TutorialDialog.this, Home.class);
                startActivity(activityChangeIntent);
                finish();

            }
        });

    }



}
