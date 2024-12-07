package com.example.michailkoupparis.songle;



import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

// Settings Screen
public class Settings extends AppCompatActivity{

    private CheckBox checkBox_very_easy, checkBox_easy, checkBox_normal, checkBox_hard, checkBox_very_hard;
    private CheckBox checkBox_near_mode, checkBox_anywhere_mode;
    public static final String DIFFICULTY_LEVEL_TAG = "difficulty_level_tag";
    public static final String DIFFICULTY_LEVEL_KEY = "difficulty_level_key";
    public static final String MODE_TAG = "mode_tag";
    public static final String MODE_KEY = "mode_key";
    public static final String MODE_WAS_ANYWHERE_TAG = "mode_change_tag";
    public static final String MODE_WAS_ANYWHERE_KEY = "mode_change_key";
    private String difficulty;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set the difficulty level checkBoxes
        checkBox_very_easy = (CheckBox) findViewById(R.id.very_easy);
        checkBox_easy = (CheckBox) findViewById(R.id.easy);
        checkBox_normal = (CheckBox) findViewById(R.id.normal);
        checkBox_hard = (CheckBox) findViewById(R.id.hard);
        checkBox_very_hard = (CheckBox) findViewById(R.id.very_hard);

        // Get the previous selected difficulty
        SharedPreferences difficulty_pref =
                getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, MODE_PRIVATE);

        difficulty = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");

        // Sets the checkbox which corresponds to the selected difficulty to true
        if (difficulty.equals("1")){
            checkBox_very_hard.setChecked(true);
        }
        else if (difficulty.equals("2")){
            checkBox_hard.setChecked(true);
        }
        else if (difficulty.equals("3")){
            checkBox_normal.setChecked(true);
        }
        else if (difficulty.equals("4")){
            checkBox_easy.setChecked(true);
        }
        else if (difficulty.equals("5")){
            checkBox_very_easy.setChecked(true);
        }

        // Set the game mode checkboxes
        checkBox_near_mode = (CheckBox) findViewById(R.id.near_distance);
        checkBox_anywhere_mode= (CheckBox) findViewById(R.id.from_anywhere);

        // Get the previous selected difficulty
        SharedPreferences mode_pref =
                getSharedPreferences(Settings.MODE_TAG, MODE_PRIVATE);

        mode = mode_pref.getString(Settings.MODE_KEY, "near");
        // Sets the checkbox which corresponds to the selected mode to true
        if(mode.equals("near")){
            checkBox_near_mode.setChecked(true);
        }
        else{
            checkBox_anywhere_mode.setChecked(true);
        }
    }

    // On back press return to the home screen
    public void onBackPressed() {
        Intent activityChangeIntent = new Intent(Settings.this, Home.class);
        startActivity(activityChangeIntent);
        finish();
    }

    //Method for setting on CheckBox Listener for Difficulty Level CheckBoxes
    public void onCheckboxClickedDifficulty(final View view) {

        // Get the selected difficulty
        SharedPreferences difficulty_pref =
                getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, MODE_PRIVATE);

        difficulty = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");

        // Boolean same is for checking is the checkbox the user tries to select is the same as the previous one
        Boolean same = false;
        switch (view.getId()) {

            // Checks by the id which checkbox it is
            // If the checkbox was already true it remains true
            // and a message indicate that this checkbox is already true appears on the user's screen
            case R.id.very_easy:
                if (difficulty.equals("5")){
                    Toast.makeText(Settings.this, "This Difficulty is Already Selected!",
                            Toast.LENGTH_SHORT).show();
                    setCheckBox_very_easy();
                    same = true;
                }

                break;

            case R.id.easy:
                if (difficulty.equals("4")){
                    Toast.makeText(Settings.this, "This Difficulty is Already Selected!",
                            Toast.LENGTH_SHORT).show();
                    setCheckBox_easy();
                    same = true;
                }
                break;

            case R.id.normal:
                if (difficulty.equals("3")){
                    Toast.makeText(Settings.this, "This Difficulty is Already Selected!",
                            Toast.LENGTH_SHORT).show();
                    setCheckBox_normal();
                    same = true;
                }
                break;
            case R.id.hard:
                if (difficulty.equals("2")){
                    Toast.makeText(Settings.this, "This Difficulty is Already Selected!",
                            Toast.LENGTH_SHORT).show();
                    setCheckBox_hard();
                    same = true;
                }
                break;

            case R.id.very_hard:
                if (difficulty.equals("1")){
                    Toast.makeText(Settings.this, "This Difficulty is Already Selected!",
                            Toast.LENGTH_SHORT).show();
                    setCheckBox_very_hard();
                    same = true;
                }
                break;

        }
        // If the user selects a different CheckBox a warning Dialog Pops up
        if (!same) {
            ViewDialogDifficulty alert = new ViewDialogDifficulty();
            alert.showDialogDifficulty(Settings.this, view);
        }


    }

    // Dialog for warning the user that if he change the level the progress will be lost
    private class ViewDialogDifficulty {

        private void showDialogDifficulty(Activity activity, final View view) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.warning_dialog);


            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(R.string.sure_change_level);

            // If the user selects to change the difficulty he can press the change button
            Button change_button = (Button) dialog.findViewById(R.id.btn_ok_dialog);
            change_button.setText(R.string.yes_change_level);
            change_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    // Finds the difficultyLevel using the CheckBox ID
                    // and calls the method that sets this checkbox to true and every other to false
                    // also it updates the difficulty level preference to hold the string number which corresponds to that level
                    switch (view.getId()) {

                        case R.id.very_easy:
                            setCheckBox_very_easy();
                            updateChangePreferenceDifficulty("5");
                            break;

                        case R.id.easy:
                            setCheckBox_easy();
                            updateChangePreferenceDifficulty("4");
                            break;

                        case R.id.normal:
                            setCheckBox_normal();
                            updateChangePreferenceDifficulty("3");
                            break;
                        case R.id.hard:
                            setCheckBox_hard();
                            updateChangePreferenceDifficulty("2");
                            break;

                        case R.id.very_hard:
                            setCheckBox_very_hard();
                            updateChangePreferenceDifficulty("1");
                            break;

                    }
                    dialog.dismiss();
                }
            });


            // If the user regrets he can press the cancel button
            Button cancelButton = (Button) dialog.findViewById(R.id.btn_cancel_dialog);
            cancelButton.setText(R.string.no_dont_change_level);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Get the previous difficulty level
                    SharedPreferences difficulty_pref =
                            getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, MODE_PRIVATE);

                    difficulty = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");

                    // Based on the previous difficulty call the method
                    // that sets the checkbox corresponding to that difficulty to true and the others to false
                    switch (difficulty) {
                        case "5":
                            setCheckBox_very_easy();
                            break;

                        case "4":
                            setCheckBox_easy();

                            break;

                        case "3":
                            setCheckBox_normal();
                            break;

                        case "2":
                            setCheckBox_hard();
                            break;

                        case "1":
                            setCheckBox_very_hard();
                            break;
                    }
                    dialog.dismiss();
                }

            });
            dialog.show();
        }
    }

    //Method for setting on CheckBox Listener for Game Mode CheckBoxes
    public void onCheckboxClickedMode(final View view) {

        // Get the previous selected Mode
        SharedPreferences mode_pref =
                getSharedPreferences(Settings.MODE_TAG, MODE_PRIVATE);

        mode = mode_pref.getString(Settings.MODE_KEY, "near");

        // Boolean same is for checking is the checkbox the user tries to select is the same as the previous one
        Boolean same = false;

        // Checks by the id which checkbox it is
        // If the checkbox was already true it remains true
        // and a message indicate that this checkbox is already true appears on the user's screen

        switch(view.getId()) {

            case R.id.near_distance:
                if(mode.equals("near")){
                    Toast.makeText(Settings.this, "This Mode is Already Selected!",
                            Toast.LENGTH_SHORT).show();
                    checkBox_near_mode.setChecked(true);
                    same = true;
                }
                break;

            case R.id.from_anywhere:
                if(mode.equals("anywhere")){
                    Toast.makeText(Settings.this, "This Mode is Already Selected!",
                            Toast.LENGTH_SHORT).show();
                    checkBox_anywhere_mode.setChecked(true);
                    same = true;
                }
                break;
        }

        // If the user selects a different CheckBox a warning Dialog Pops up
        if(!same) {
            ViewDialogMode alert = new ViewDialogMode();
            alert.showDialogMode(Settings.this, view);
        }

    }

    // Dialog for warning the user that each Game Modes has a Different Bonus
    private class ViewDialogMode {

        private void showDialogMode(Activity activity, final View view) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.warning_dialog);


            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(R.string.sure_change_mode);

            // If the user selects to change the game mode he can press the change button
            Button change_button = (Button) dialog.findViewById(R.id.btn_ok_dialog);
            change_button.setText(R.string.yes_change_mode);
            change_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Finds the game mode using the CheckBox ID
                    // and calls the method that sets this checkbox to true and the other to false
                    // also it updates the game mode preference to hold the string which corresponds to that mode

                    switch(view.getId()) {

                        case R.id.near_distance:
                            checkBox_near_mode.setChecked(true);
                            checkBox_anywhere_mode.setChecked(false);
                            SharedPreferences.Editor editor =
                                    getSharedPreferences(Settings.MODE_TAG, MODE_PRIVATE).edit();
                            editor.putString(Settings.MODE_KEY, "near");
                            editor.apply();
                            break;

                        case R.id.from_anywhere:
                            checkBox_near_mode.setChecked(false);
                            checkBox_anywhere_mode.setChecked(true);
                            SharedPreferences.Editor editor_mode_change =
                                    getSharedPreferences(Settings.MODE_WAS_ANYWHERE_TAG, MODE_PRIVATE).edit();
                            editor_mode_change.putBoolean(Settings.MODE_WAS_ANYWHERE_KEY, true);
                            editor_mode_change.apply();
                            SharedPreferences.Editor editor_mode =
                                    getSharedPreferences(Settings.MODE_TAG, MODE_PRIVATE).edit();
                            editor_mode.putString(Settings.MODE_KEY, "anywhere");
                            editor_mode.apply();
                            break;

                    }
                    dialog.dismiss();

                }
            });

            // If the user regrets he can press the cancel button
            Button cancelButton = (Button) dialog.findViewById(R.id.btn_cancel_dialog);
            cancelButton.setText(R.string.no_dont_change_mode);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences mode_pref =
                            getSharedPreferences(Settings.MODE_TAG, MODE_PRIVATE);

                    String mode = mode_pref.getString(Settings.MODE_KEY, "near");

                    // Based on the previous mode it calls the method
                    // that sets the checkbox corresponding to that game mode to true and the others to false

                    switch(mode){
                        case "near":
                            checkBox_near_mode.setChecked(true);
                            checkBox_anywhere_mode.setChecked(false);
                            break;

                        case "anywhere":
                            checkBox_near_mode.setChecked(false);
                            checkBox_anywhere_mode.setChecked(true);
                            break;

                    }
                    dialog.dismiss();
                }

            });
            dialog.show();
        }
    }


    // Method for updating the difficulty level and the change preference
    // Change preference indicates that a preference change which will result to progress to be lost when the map is accessed again.
    // If the user change difficulty this is set to true because otherwise the user can select words in the very easy level
    // then change the level and get the points for a different level having words from the previous level.
    private void updateChangePreferenceDifficulty(String level_now) {
        SharedPreferences.Editor editor =
                getSharedPreferences(Home.CHANGE_TAG, MODE_PRIVATE).edit();
        editor.putBoolean(Home.CHANGE_KEY, true);
        editor.apply();
        SharedPreferences.Editor editor_difficulty =
                getSharedPreferences(DIFFICULTY_LEVEL_TAG, MODE_PRIVATE).edit();
        editor_difficulty.putString(DIFFICULTY_LEVEL_KEY, level_now);
        editor_difficulty.apply();
    }

   // Method for setting true the checkbox corresponding to the very easy level
   private void setCheckBox_very_easy() {
        checkBox_very_easy.setChecked(true);
        checkBox_easy.setChecked(false);
        checkBox_normal.setChecked(false);
        checkBox_hard.setChecked(false);
        checkBox_very_hard.setChecked(false);

    }

    // Method for setting true the checkbox corresponding to the easy level
    public void  setCheckBox_easy(){
        checkBox_very_easy.setChecked(false);
        checkBox_easy.setChecked(true);
        checkBox_normal.setChecked(false);
        checkBox_hard.setChecked(false);
        checkBox_very_hard.setChecked(false);
    }

    // Method for setting true the checkbox corresponding to the normal level
    public void setCheckBox_normal(){
        checkBox_very_easy.setChecked(false);
        checkBox_easy.setChecked(false);
        checkBox_normal.setChecked(true);
        checkBox_hard.setChecked(false);
        checkBox_very_hard.setChecked(false);
    }


    // Method for setting true the checkbox corresponding to the hard level
    public void setCheckBox_hard() {
        checkBox_very_easy.setChecked(false);
        checkBox_easy.setChecked(false);
        checkBox_normal.setChecked(false);
        checkBox_hard.setChecked(true);
        checkBox_very_hard.setChecked(false);
    }

    // Method for setting true the checkbox corresponding to the very hard level
    public void setCheckBox_very_hard() {
        checkBox_very_easy.setChecked(false);
        checkBox_easy.setChecked(false);
        checkBox_normal.setChecked(false);
        checkBox_hard.setChecked(false);
        checkBox_very_hard.setChecked(true);
    }


    // Method for setting the information button listeners.
    // There is one information button next to each difficulty level explaining the difference between levels.
    public void onInfClick(final View view){

        // When the button is clicked a dialog pops up consisting of text explaining the corresponding difficulty level.
        final Dialog dialog = new Dialog(Settings.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.diff_information);

        // Based on the id of the button (on the difficulty it represents) a different text is set on the TextView
        // which appears on the dialog.
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);

        switch (view.getId()) {
            case  R.id.inf_very_easy:
                text.setText(R.string.very_easy_description);
                break;

            case R.id.inf_easy:
                text.setText(R.string.easy_description);
                break;

            case R.id.inf_normal:
                text.setText(R.string.normal_description);
                break;

            case R.id.inf_hard:
                text.setText(R.string.hard_description);
                break;

            case R.id.inf_very_hard:
                text.setText(R.string.very_hard_description);
                break;
        }


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

