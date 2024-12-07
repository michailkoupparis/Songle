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
import android.widget.CheckBox;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;

import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
/* Test for difficulty Level.
*  First it checks the information buttons next to each level.
*  After the button is checked a dialog should pop up containing information about each level.
*  This test, test the dialog for the Very Hard Level if it pops up after its button is pressed and if the description is right.
*  Then it check all possible situations the game will reach if a user tries to change the difficulty.
*  If it chooses the same difficulty as before a toast message appears and the difficulty does not change.
*  If it selects a different a warning pops ups that he will loose the process of the current song
*  if the user selects to change then the difficulty preference change and the corresponding checkbox is now marked
*  and the previous level checkbox is now unmarked,
*  if he decides to not change the difficulty,  the difficulty preference does not change and the checkboxes remain unchanged.
*/
@LargeTest
@RunWith(AndroidJUnit4.class)
public class DifficultyTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void difficultyTest() {
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
        // to proceed to the Setting activity

        Boolean first_time = Home.first_time;

        if (first_time){
            // Press cancel, and so close the tutorial and return to the home screen (Home)
            ViewInteraction closeTutorial = onView(
                    allOf(withId(R.id.cancel_dialog), withText("CANCEL"), isDisplayed()));
            closeTutorial.perform(click());
        }


        // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
        if(!checkWifi()){
            close_check_WifiDialog();
        }

        // Go to Settings Activity Where the Difficulty can be changed
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.settings_button), withText("SETTINGS"), isDisplayed()));
        appCompatButton.perform(click());

        // Check if the Settings Activity is reached
        ViewInteraction textView = onView(
                allOf(withId(R.id.home_title), withText("SETTINGS"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("SETTINGS")));

        // Press the information button for the Very Hard Difficulty
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.inf_very_hard), isDisplayed()));
        appCompatImageButton.perform(click());
        // Check if the information dialog pops ups and if it has the right content (the very hard description)
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.text_dialog), withText("Display only 25% of words on map and all are unclassified."),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("Display only 25% of words on map and all are unclassified.")));

        // Close the dialog and return to the Settings Screen
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
        appCompatButton2.perform(click());

        // Get the current activity
        Activity activity_now = getCurrentActivity();

        // Find the current difficulty level
        SharedPreferences difficulty_pref =
                activity_now.getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, Context.MODE_PRIVATE);

        String difficulty = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");

        // Press the Normal Check Box to Change the difficulty Level to Normal
        ViewInteraction appCompatCheckBox = onView(
                allOf(withId(R.id.normal), withText("NORMAL"), isDisplayed()));
        appCompatCheckBox.perform(click());


        // Here it check if the difficulty level was normal before
        // If it was then it changes the level to easy
        // If not after it change the level to normal it again try to change it normal
        // That way all possible situations a user can reached are tested
        // The toast message after a user tries to select the same difficulty level
        // Also the warning dialog with both the user selecting to change the level or regret and do not change
        // Last in both cases the test, test if the previous marked checkboxs are now unmarked since only one should be marked.

        if (difficulty.equals("3")) {
            // Since the level the user chooses is the same as before test if the toast message appears
            onView(withText("This Difficulty is Already Selected!")).
                    inRoot(withDecorView(not(is(activity_now.getWindow().getDecorView())))).
                    check(matches(isDisplayed()));

            // Change the difficulty to easy to test the behaviour of the app when a different level is selected
            ViewInteraction change_to_easy = onView(
                    allOf(withId(R.id.easy), withText("EASY"), isDisplayed()));
            change_to_easy.perform(click());

            // Check if the warning dialog pops up
            ViewInteraction diff_warning = onView(
                    allOf(withId(R.id.text_dialog), withText("Current song progress will be lost by changing level!!!"),

                            isDisplayed()));
            diff_warning.check(matches(withText("Current song progress will be lost by changing level!!!")));

            // Press Do not Change to test the behaviour of the app when the user regrets his decision
            ViewInteraction dont_change = onView(
                    allOf(withId(R.id.btn_cancel_dialog), withText("Dont Change !"), isDisplayed()));
            dont_change.perform(click());

            // Check if the difficulty level did not change
            String difficulty2 = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");
            assertEquals("Difficulty not Change", difficulty2, difficulty);
            // Check if the same CheckBox as Before is still checked
            CheckBox previous_true = activity_now.findViewById(findID(difficulty));
            assertTrue("CheckBox remain True", previous_true.isChecked());

            // Press the Easy check Box again this time for changing the level
            ViewInteraction appCompatCheckBox2 = onView(
                    allOf(withId(R.id.easy), withText("EASY"), isDisplayed()));
            appCompatCheckBox2.perform(click());

            // Check if the warning dialog pops up
            ViewInteraction diff_warning2 = onView(
                    allOf(withId(R.id.text_dialog), withText("Current song progress will be lost by changing level!!!"),

                            isDisplayed()));
            diff_warning2.check(matches(withText("Current song progress will be lost by changing level!!!")));

            // The user does not regret and changes the level to Easy
            ViewInteraction change = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("Change !"), isDisplayed()));
            change.perform(click());

            // Check if the level is now Easy (4)
            String difficulty3 = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");
            assertEquals("Difficulty Change", difficulty3, "4");
            // Check if the Easy CheckBox is now Checked
            CheckBox easy = activity_now.findViewById(R.id.easy);
            assertTrue("CheckBox changed to True", easy.isChecked());

            // Also check if the previous checked box is now unchecked
            assertFalse("CheckBox changed to True", previous_true.isChecked());

        }
        else{

            // If the level was not Normal Before a Warning Dialog Should pop Up
            // for informing the user that if he change the level the proccess it will be lost
            // and offering the chance of regret or proceed
            // Check If this is the case
            ViewInteraction diff_warning = onView(
                    allOf(withId(R.id.text_dialog), withText("Current song progress will be lost by changing level!!!"),

                            isDisplayed()));
            diff_warning.check(matches(withText("Current song progress will be lost by changing level!!!")));

            //Press Do not Change to test the behaviour of the app when the user regrets his decision
            ViewInteraction dont_change = onView(
                    allOf(withId(R.id.btn_cancel_dialog), withText("Dont Change !"), isDisplayed()));
            dont_change.perform(click());

            // The Difficulty Level Saved as preference across the app should remain the same as before
            String difficulty2 = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");
            assertEquals("Difficulty not Change", difficulty2, difficulty);
            // The Same CheckBox As Before should still be checked
            CheckBox previous_true = activity_now.findViewById(findID(difficulty));
            assertTrue("CheckBox remain True", previous_true.isChecked());

            // Now press the Normal CheckBox again and this time change the level
            ViewInteraction appCompatCheckBox2 = onView(
                    allOf(withId(R.id.normal), withText("NORMAL"), isDisplayed()));
            appCompatCheckBox2.perform(click());

            // Check if the warning dialog pops up
            ViewInteraction diff_warning2 = onView(
                    allOf(withId(R.id.text_dialog), withText("Current song progress will be lost by changing level!!!"),

                            isDisplayed()));
            diff_warning2.check(matches(withText("Current song progress will be lost by changing level!!!")));

            // Press the Ok (change) button and change the level to Normal
            ViewInteraction change = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("Change !"), isDisplayed()));
            change.perform(click());
            // Check if the Difficulty Level Saved as preference across the app is now 3(Normal)
            String difficulty3 = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");
            assertEquals("Difficulty Change", difficulty3, "3");
            // Check if the Normal CheckBox is now Checked
            CheckBox normal = activity_now.findViewById(R.id.normal);
            assertTrue("CheckBox changed to True", normal.isChecked());

            // Press again the Normal CheckBox to test if the toast message which should appear if the same level
            // is selected appears
            ViewInteraction change_to_normal = onView(
                    allOf(withId(R.id.normal), withText("NORMAL"), isDisplayed()));
            change_to_normal.perform(click());

            onView(withText("This Difficulty is Already Selected!")).
                    inRoot(withDecorView(not(is(activity_now.getWindow().getDecorView())))).
                    check(matches(isDisplayed()));

            // Also check if the previous checked box is now unchecked
            assertFalse("CheckBox changed to True", previous_true.isChecked());
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

    // Method for finding description of difficulty given the corresponding number (e.g 1-> R.id.very_hard)
    private int findID(String diffulty_num) {
        int difficulty = 1;
        switch (diffulty_num) {
            case "5":
                difficulty = R.id.very_easy;
                break;
            case "4":
                difficulty = R.id.easy;
                break;
            case "3":
                difficulty = R.id.normal;
                break;
            case "2":
                difficulty = R.id.hard;
                break;
            case "1":
                difficulty = R.id.very_hard;
                break;
        }
        return difficulty;
    }

}
