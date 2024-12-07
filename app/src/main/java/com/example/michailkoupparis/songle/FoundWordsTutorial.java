package com.example.michailkoupparis.songle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

// For explaining Found Word Screen
public class FoundWordsTutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_words_tutorial);

        this.setFinishOnTouchOutside(false);

        // Button Listeners for next and previous button for navigating when user is on the game's tutorial
        Button next_dialog_button = (Button) findViewById(R.id.next_dialog);
        next_dialog_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(FoundWordsTutorial.this, SongSelectionTutorial.class);
                startActivity(activityChangeIntent);
            }
        });

        Button prev_dialog_button = (Button) findViewById(R.id.previous_dialog);
        prev_dialog_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(FoundWordsTutorial.this, SettingsTutorial.class);
                startActivity(activityChangeIntent);
            }
        });

        // Cancel button for user to leave the game's tutorial and return to home screen
        Button cancel_button = (Button) findViewById(R.id.cancel_dialog);
        cancel_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(FoundWordsTutorial.this, Home.class);
                startActivity(activityChangeIntent);
            }
        });
    }

    // Method for setting example views(eye buttons on click listeners) next to text on the tutorial
    public void setExampleView(View view) {
       // When the user press an eye button to se visual example a  new dialog with visual example pop ups
        ViewDialog alert = new ViewDialog();
        alert.showDialog(this, view);
    }


    // Dialog for representing visual examples on Found Words Tutorial screen
    private class ViewDialog {

        private void showDialog(Activity activity, final View view) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.visual_example);


            ImageView exampleView = (ImageView) dialog.findViewById(R.id.exampleView);

            // there are three different eye button each one represents a different visual explanation
            // Here it finds which one it is and sets its corresponding image
            switch (view.getId()) {

                case R.id.word_container_example:
                    exampleView.setImageResource(R.drawable.word_container_tutorial);
                    break;

                case R.id.get_solution_example:
                    exampleView.setImageResource(R.drawable.solution_tutorial);
                    break;
                case R.id.hint_example:
                    exampleView.setImageResource(R.drawable.hint_tutorial);
                    break;


            }

            // for cancelling the visual explanation
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
}
