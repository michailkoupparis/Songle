package com.example.michailkoupparis.songle;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
/* Test for Game's Attributes Displayed at Home Screen (Home Activity)
 This test checks if the score, distance walked, current difficulty, current song in play
 displayed in the bottom of the Home Screen match those saved in the games preferences.*/

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HomeScreenDisplayTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void homeDisplayTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Find if is the first time the game is reached
        // If yes then the tutorial will pop up first and it has to be closed to check
        // the Home Screen text views

        Boolean first_time = Home.first_time;

        if (first_time){
            // Press cancel, and so close the tutorial and return to the home screen (Home)
            ViewInteraction closeTutorial = onView(
                    allOf(withId(R.id.cancel_dialog), withText("CANCEL"), isDisplayed()));
            closeTutorial.perform(click());
        }

        Context context = mActivityTestRule.getActivity();

        // Find if there is wifi connection when the home screen is reached
        // If there is not an internet connection a dialog will pop up first and it has to be closed to check
        // the Home Screen text views

        Boolean wifi = Home.wifi;

        if (!wifi){
            // If there is not wifi connection close the warning to be able to reach the Home Screen
            ViewInteraction close_wifi_warning = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
            close_wifi_warning.perform(click());
        }
        // Check if the Home Screen Displays the Score Correctly, which is saved in the shared preferences
        // First get the current activity which is the Home activity
        Activity activity_now = getCurrentActivity();

        // Find the current score in the game
        SharedPreferences score_pref =
                context.getSharedPreferences(Home.SCORE_TAG, Context.MODE_PRIVATE);

        Integer score = score_pref.getInt(Home.SCORE_KEY, 0);
        String rightScoreView = "SCORE:   " + score;

        TextView scoreView = (TextView) activity_now.findViewById(R.id.scoreView);
        assertEquals("Score in Home Screen not the Same as Current Score",
                rightScoreView, scoreView.getText());

        // Check if the Home Screen Displays the Current Distance Correctly, which is saved in the shared preferences

        // Find the current distance in the game
        SharedPreferences distance_pref =
                context.getSharedPreferences(Home.DISTANCE_TAG, Context.MODE_PRIVATE);

        Float distance = distance_pref.getFloat(Home.DISTANCE_KEY, 0);
        String dist = String.format("%.2f", distance);
        String rightDistanceView = "DISTANCE WALKED:   " + dist + "meters" ;

        TextView distanceView = (TextView) activity_now.findViewById(R.id.distanceView);
        assertEquals("Distance in Home Screen not the Same as Current Distance",
                rightDistanceView, distanceView.getText());

        // Check if the Home Screen Displays the Current Difficulty Correctly, which is saved in the shared preferences

        // Find the current difficulty in the game
        SharedPreferences difficulty_pref =
                context.getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, Context.MODE_PRIVATE);

        String difficulty = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");
        difficulty = "SELECTED DIFFICULTY:   "   + setDifficulty(difficulty) ;

        TextView difficultyView = (TextView) activity_now.findViewById(R.id.difficultyView);

        assertEquals("Difficulty in Home Screen not the Same as Current Difficulty",
                difficulty, difficultyView.getText());

        //Check if the Home Screen Displays the Current Song Correctly, which is saved in the shared preferences

        // Find the current song in play

        SharedPreferences song_pref =
                context.getSharedPreferences(SongSelection.SONG_PREF_TAG, Context.MODE_PRIVATE);

        String song = song_pref.getString(SongSelection.SONG_PREF_KEY, "01");
        song = "SONG NUMBER:   "   + song ;

        TextView songView = (TextView) activity_now.findViewById(R.id.songNumberView);
        assertEquals("Song in Home Screen not the Same as Current Song in Play",
                song,songView.getText());
    }


    // Method for returning the current Activity (the Activity now active by the espresso test)
    private Activity getCurrentActivity() {
        final Activity[] activity = new Activity[1];
        onView(isRoot()).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                activity[0] = (Activity) view.getContext();
            }
        });
        return activity[0];
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
}
