package com.example.michailkoupparis.songle;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
/* Test for Giving Solution when the user tries to guess the Solution.
* The user can guess the Solution in the Found Words Activity.
* Found Words Activity where the user can vie the collected words so it can give solutions there also.
* There is an Edit Text View where the user can write the Solution.
* There is a give solution button if the user press the button a dialog pops up.
* After the button is pressed the app check if the context of the Edit Text View is the same as the title of current song.
* First the test change the song and all attributes that contribute to score so as the test checks all possibilities.
* Then it writes a wrong answer and test if its right.
* After that it wrights the right solution and press the submit button.
* Then checks if the Correct Solution Dialog pops up and if it has the right answer and score.
* Then the user can either got to the Next Song or to Song Selection Activity.
* The test then Go to Song Selection option.
* After that the test change the song and all attributes that contribute to score that are not used before
* so as the test checks all possibilities.
* Then checks if the Correct Solution Dialog pops up and if it has the right answer and score.
* At the end it checks if those two songs are added to found songs set.
* After that it adds a song to found songs and checks if there Score is 0.
* Lastly it Checks if the score is updated right.
* If there is no wifi at the Home Activity the Songs are not downloaded and so in the Found Words Activity
* the title cannot be found for the current song.
* So a toast message informing the user appears when it press the Submit Button.
* The test takes this into consideration and if there is no wifi in Home it checks and the toast Message when submit button is pressed.
*/
@LargeTest
@RunWith(AndroidJUnit4.class)
public class GiveSolutionTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void giveSolutionTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Find if is the first time the game is reached
        // If yes then the tutorial will pop up first and it has to be closed and go to
        // the Home Screen.
        Boolean first_time = Home.first_time;
        if (first_time){
            // Press cancel, and so close the tutorial and return to the home screen (Home)
            ViewInteraction closeTutorial = onView(
                    allOf(withId(R.id.cancel_dialog), withText("CANCEL"), isDisplayed()));
            closeTutorial.perform(click());
        }

        // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
        Boolean internet = true;

        if(!checkWifi()){
            close_check_WifiDialog();
            internet = false;
        }

        // Get the current activity which is the Home activity
        Activity activity_home = getCurrentActivity();

        // Find the current score such that the tesg can check later if the score is updated correctly
        SharedPreferences score_pref =
                activity_home.getSharedPreferences(Home.SCORE_TAG, Context.MODE_PRIVATE);

        Integer score_prev = score_pref.getInt(Home.SCORE_KEY, 0);

        // Update the Song such as to be Sure what Song is now in play in the test
        SharedPreferences.Editor editor_song =
                activity_home.getSharedPreferences(SongSelection.SONG_PREF_TAG, Context.MODE_PRIVATE).edit();
        editor_song.putString(SongSelection.SONG_PREF_KEY, "24");
        editor_song.apply();

        // Update the Difficulty such as to be Sure what Difficulty is now in play in the test to calculate the score
        SharedPreferences.Editor editor_difficulty =
                activity_home.getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, Context.MODE_PRIVATE).edit();
        editor_difficulty.putString(Settings.DIFFICULTY_LEVEL_KEY, "3");
        editor_difficulty.apply();

        // Update if the Mode wa anywhere such as to be Sure what Mode is now in play in the test to calculate the score
        SharedPreferences.Editor editor_mode =
                activity_home.getSharedPreferences(Settings.MODE_WAS_ANYWHERE_TAG, Context.MODE_PRIVATE).edit();
        editor_mode.putBoolean(Settings.MODE_WAS_ANYWHERE_KEY, false);
        editor_mode.apply();

        // Update the Hint taken such as to be Sure if a Hint is taken now for this song such as the test be able to calculate the score
        SharedPreferences.Editor editor_hint =
                activity_home.getSharedPreferences(FoundWords.Hint_PENALTY_TAG, Context.MODE_PRIVATE).edit();
        editor_hint.putBoolean(FoundWords.Hint_PENALTY_KEY, false);
        editor_hint.apply();


        // Go to Found Words where the user can guess the Song.
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.words_found), withText("FOUND WORDS"), isDisplayed()));
        appCompatButton.perform(click());

        // Check if the game is now in the  in the Found Words Activity.
        ViewInteraction textView = onView(
                allOf(withId(R.id.textView2), withText("ALL WORDS FOUND ALREADY"),
                        childAtPosition(
                                allOf(withId(R.id.rl),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("ALL WORDS FOUND ALREADY")));


        // Write a wrong answer in the Container where the user can give solution
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.solution_container),
                        withParent(allOf(withId(R.id.rl),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("gh"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.solution_container), withText("gh"),
                        withParent(allOf(withId(R.id.rl),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatEditText2.perform(pressImeActionButton());

        // Press submit Solution to
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.try_solution), withText("SUBMIT SOLUTION"),
                        withParent(allOf(withId(R.id.rl),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatButton2.perform(click());

        // If there was not internet in the Home Screen the Songs have not been downloaded
        // and so the Solution cannot be found to check if the user is right .
        // In this situation a Toast message informing the user pops up.
        if(internet) {
            // Check if the Wrong Answer Dialog Appears
            ViewInteraction textView2 = onView(
                    allOf(withId(R.id.text_dialog), withText("Wrong Answer. Try Again."),

                            isDisplayed()));
            textView2.check(matches(withText("Wrong Answer. Try Again.")));

            // Press ok to close the dialog
            ViewInteraction appCompatButton3 = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
            appCompatButton3.perform(click());

            // Check if the dialog is now closed and the Found Words Activity now appears
            ViewInteraction textView3 = onView(
                    allOf(withId(R.id.textView2), withText("ALL WORDS FOUND ALREADY"),
                            childAtPosition(
                                    allOf(withId(R.id.rl),
                                            childAtPosition(
                                                    withId(android.R.id.content),
                                                    0)),
                                    0),
                            isDisplayed()));
            textView3.check(matches(withText("ALL WORDS FOUND ALREADY")));

            // Wright the right Solution in the Solution Container to check the gmae's behaviour when the correct solution is given
            ViewInteraction appCompatEditText3 = onView(
                    allOf(withId(R.id.solution_container),
                            withParent(allOf(withId(R.id.rl),
                                    withParent(withId(android.R.id.content)))),
                            isDisplayed()));
            appCompatEditText3.perform(replaceText("Never Gonna Give You Up"), closeSoftKeyboard());

            ViewInteraction appCompatEditText4 = onView(
                    allOf(withId(R.id.solution_container), withText("Never Gonna Give You Up"),
                            withParent(allOf(withId(R.id.rl),
                                    withParent(withId(android.R.id.content)))),
                            isDisplayed()));
            appCompatEditText4.perform(pressImeActionButton());

            // Check if the Dialog contains the right solution already and the game has awarded the user the right points for this answer
            // the right score should be 40, 30 for normal level + 10 for near mode -0 for no taking hint
            // Or it will be 0 if the song is already found.
            Integer score = 40;
            Set<String> found_songs = WelcomeScreen.song_founds;
            if(found_songs.contains("Never Gonna Give You Up")){
                score = 0;
            }

            // Submit the solution
            ViewInteraction appCompatButton4 = onView(
                    allOf(withId(R.id.try_solution), withText("SUBMIT SOLUTION"),
                            withParent(allOf(withId(R.id.rl),
                                    withParent(withId(android.R.id.content)))),
                            isDisplayed()));
            appCompatButton4.perform(click());

            // Since the solution is right the Right Solution dialog should pop up
            ViewInteraction textView4 = onView(
                    allOf(withId(R.id.text_dialog), withText("Correct Solution Congratulations!!!!!"),

                            isDisplayed()));
            textView4.check(matches(withText("Correct Solution Congratulations!!!!!")));


            ViewInteraction textView5 = onView(
                    allOf(withId(R.id.solution_dialog),
                            isDisplayed()));

            textView5.check(matches(withText("Solution was:\n Never Gonna Give You Up\n\n Awarded Points: "+score)));

            // Go to song selection and change the song to 19 because now 24 and the next song will be 1 but more songs can be added and then the test will fail
            ViewInteraction appCompatButton5 = onView(
                    allOf(withId(R.id.btn_selectDifferent), withText("Go to Song Selection!"), isDisplayed()));
            appCompatButton5.perform(click());

            // change the song to 19 because now 24 and the next song will be 1 but more songs can be added and then the test will fail
            editor_song.putString(SongSelection.SONG_PREF_KEY, "19");
            editor_song.apply();

            // Update the Difficulty such as to be Sure what Difficulty is now in play in the test to calculate the score
            editor_difficulty.putString(Settings.DIFFICULTY_LEVEL_KEY, "1");
            editor_difficulty.apply();

            // Update the Mode was anywhere such as to be Sure if Anywhere Mode is now in play in the test to calculate the score
            editor_mode.putBoolean(Settings.MODE_WAS_ANYWHERE_KEY, true);
            editor_mode.apply();

            // Update the Hint taken such as to be Sure if a Hint is taken now for this song such as the test be able to calculate the score
            editor_hint.putBoolean(FoundWords.Hint_PENALTY_KEY, true);
            editor_hint.apply();

            // press back to return to Home Screen to access Found Words Again
            pressBack();

            // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
            if(!checkWifi()){
                close_check_WifiDialog();
                internet = false;
            }

            // Go to Found Words activity to check the score in this different situation
            ViewInteraction appCompatButton6 = onView(
                    allOf(withId(R.id.words_found), withText("FOUND WORDS"), isDisplayed()));
            appCompatButton6.perform(click());

            ViewInteraction appCompatEditText6 = onView(
                    allOf(withId(R.id.solution_container),
                            withParent(allOf(withId(R.id.rl),
                                    withParent(withId(android.R.id.content)))),
                            isDisplayed()));
            appCompatEditText6.perform(replaceText("The Touch"), closeSoftKeyboard());

            ViewInteraction appCompatEditText7 = onView(
                    allOf(withId(R.id.solution_container), withText("The Touch"),
                            withParent(allOf(withId(R.id.rl),
                                    withParent(withId(android.R.id.content)))),
                            isDisplayed()));
            appCompatEditText7.perform(pressImeActionButton());

            // Check if the Dialog contains the right solution and the game has awarded the user the right points for this answer
            // the right score should be 45, 50 for very hard level + 0 for anywhere mode -5 for taking hint
            // Or it will be 0 if the song is already found.
            Integer score2 = 45;
            if (found_songs.contains("The Touch")) {
                score2 = 0;
            }

            // Submit the solution
            ViewInteraction appCompatButton7 = onView(
                    allOf(withId(R.id.try_solution), withText("SUBMIT SOLUTION"),
                            withParent(allOf(withId(R.id.rl),
                                    withParent(withId(android.R.id.content)))),
                            isDisplayed()));
            appCompatButton7.perform(click());

            // If there was not internet in the Home Screen the Songs have not been downloaded
            // and so the Solution cannot be found to check if the user is right .
            // In this situation a Toast message informing the user pops up.
            if(internet) {
                // Check if the Correct Answer Dialog Appears
                ViewInteraction textView6 = onView(
                        allOf(withId(R.id.text_dialog), withText("Correct Solution Congratulations!!!!!"),

                                isDisplayed()));
                textView6.check(matches(withText("Correct Solution Congratulations!!!!!")));

                ViewInteraction textView7 = onView(
                        allOf(withId(R.id.solution_dialog), withText("Solution was:\n The Touch\n\n Awarded Points: "+score2),

                                isDisplayed()));
                textView7.check(matches(withText("Solution was:\n The Touch\n\n Awarded Points: "+score2)));

                // Get the found songs to check if the 2 Songs found above
                // are now in the found songs set.
                Set<String> found_songs2 = WelcomeScreen.song_founds;
                assertTrue("Song 24 is in found songs",found_songs2.contains("Never Gonna Give You Up"));
                assertTrue("Song 19 is in found songs",found_songs2.contains("The Touch"));


                // Come Back again to check if found Songs to not earn points
                // Go to song selection
                ViewInteraction appCompatButton8 = onView(
                        allOf(withId(R.id.btn_selectDifferent), withText("Go to Song Selection!"), isDisplayed()));
                appCompatButton8.perform(click());

                // press back to return to Home Screen to access Found Words Again
                pressBack();

                // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
                if(!checkWifi()){
                    close_check_WifiDialog();
                    internet = false;
                }

                // Go to Found Words activity to check the score in this different situation
                ViewInteraction appCompatButton9 = onView(
                        allOf(withId(R.id.words_found), withText("FOUND WORDS"), isDisplayed()));
                appCompatButton9.perform(click());

                ViewInteraction appCompatEditText8 = onView(
                        allOf(withId(R.id.solution_container),
                                withParent(allOf(withId(R.id.rl),
                                        withParent(withId(android.R.id.content)))),
                                isDisplayed()));
                appCompatEditText8.perform(replaceText("Highway to Hell"), closeSoftKeyboard());

                ViewInteraction appCompatEditText9 = onView(
                        allOf(withId(R.id.solution_container), withText("Highway to Hell"),
                                withParent(allOf(withId(R.id.rl),
                                        withParent(withId(android.R.id.content)))),
                                isDisplayed()));
                appCompatEditText9.perform(pressImeActionButton());

                // Add the Song 20 HighWay to Hell to found Songs to check if the user will be awarded 0 points
                WelcomeScreen.saveFile(WelcomeScreen.WELCOME_CONTEXT,"Highway to Hell");

                // If there was not internet in the Home Screen the Songs have not been downloaded
                // and so the Solution cannot be found to check if the user is right .
                // In this situation a Toast message informing the user pops up.
                if(internet) {
                    // Submit the solution
                    ViewInteraction appCompatButton10 = onView(
                            allOf(withId(R.id.try_solution), withText("SUBMIT SOLUTION"),
                                    withParent(allOf(withId(R.id.rl),
                                            withParent(withId(android.R.id.content)))),
                                    isDisplayed()));
                    appCompatButton10.perform(click());

                    // Check if 0 points are warded since the solution is found.
                    ViewInteraction textView8 = onView(
                            allOf(withId(R.id.solution_dialog), withText("Solution was:\n " +
                                            "Highway to Hell\n\n " +
                                            "Awarded Points: 0"),
                                    isDisplayed()));
                    textView8.check(matches(withText("Solution was:\n Highway to Hell\n\n Awarded Points: 0")));

                    // Get current score
                    Integer score_now = score_pref.getInt(Home.SCORE_KEY, 0);
                    Integer right_score = score_prev+score+score2;
                    // Check if the Score is updated correctly
                    assertEquals("Score Update Correct",right_score,score_now);
                }
                // Check if the toast message indicate the problem of wifi to user
                else{
                    onView(withText("Wifi was Disabled and the Game \n" +
                            "was not able to download Songs!!" +
                            "\nEnable Internet and Go back to Home to Download them")).
                            inRoot(withDecorView(not(is(activity_home.getWindow().getDecorView())))).
                            check(matches(isDisplayed()));
                }
            }
            // Check if the toast message indicate the problem of wifi to user
            else{
                onView(withText("Wifi was Disabled and the Game \n" +
                        "was not able to download Songs!!" +
                        "\nEnable Internet and Go back to Home to Download them")).
                        inRoot(withDecorView(not(is(activity_home.getWindow().getDecorView())))).
                        check(matches(isDisplayed()));
            }
        }
        // Check if the toast message indicate the problem of wifi to user
        else{
            onView(withText("Wifi was Disabled and the Game \n" +
                    "was not able to download Songs!!" +
                    "\nEnable Internet and Go back to Home to Download them")).
                    inRoot(withDecorView(not(is(activity_home.getWindow().getDecorView())))).
                    check(matches(isDisplayed()));
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private Boolean checkWifi() {

        Context context = mActivityTestRule.getActivity();

        // Find if there is wifi connection when the home screen is reached

        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo!=null;
    }

    private void close_check_WifiDialog(){
        // If there is not an internet connection a dialog will pop up first each time
        // the Home Screen is reached and so it has to be closed each time

        // If there is not wifi connection close the warning to be able to reach the Home Screen
        // And check if the warning is displayed
        ViewInteraction textView = onView(
                allOf(withId(R.id.text_dialog), withText("The game downloads files online. Internet access is required."),

                        isDisplayed()));
        textView.check(matches(withText("The game downloads files online. Internet access is required.")));

        ViewInteraction close_wifi_warning = onView(
                allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
        close_wifi_warning.perform(click());
    }

    // Method for returning the current Activity (the Activity now active by the espesso test)
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
}
