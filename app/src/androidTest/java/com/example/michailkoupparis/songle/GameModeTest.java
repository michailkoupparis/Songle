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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/* Test for game mode.
*  This test checks all possible situations the game will reach if a user tries to change the game mode.
*  If it chooses the same mode as before a toast message appears and the mode does not change.
*  If it selects a different a warning pops ups that he will points if he selects to change the mode to anywhere.
*  if the user selects to change then the mode preference change and the corresponding checkbox is now marked
*  and the previous mode checkbox is now unmarked,
*  if he decides to not change the mode,  the mode preference does not change and the checkboxes remain unchanged.
*/
@LargeTest
@RunWith(AndroidJUnit4.class)
public class GameModeTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void gameModeTest() {
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

        // Go to the Settings Screen Where the Game Mode can be changed
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.settings_button), withText("SETTINGS"), isDisplayed()));
        appCompatButton.perform(click());

        // Check if the game is now to the Settings Activity-Screen
        ViewInteraction textView = onView(
                allOf(withId(R.id.home_title), withText("SETTINGS"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("SETTINGS")));

        // Get the current activity
        Activity activity_now = getCurrentActivity();

        // Find the current mode level
        SharedPreferences mode_pref =
                activity_now.getSharedPreferences(Settings.MODE_TAG, Context.MODE_PRIVATE);

        String mode = mode_pref.getString(Settings.MODE_KEY, "near");

        // Press the Near Check Box to Change the difficulty Level to Near
        ViewInteraction appCompatCheckBox = onView(
                allOf(withId(R.id.near_distance), withText("You have to be 5 meters near to select a Song word!"), isDisplayed()));
        appCompatCheckBox.perform(click());

        // Here it check if the game mode was near before
        // If it was then it changes the mode to Anywhere
        // If not after it change the mode to Near it again try to change it Near
        // That way all possible situations a user can reached are tested
        // The toast message after a user tries to select the same game mode
        // Also the warning dialog with both the user selecting to change the mode or regret and do not change the mode
        // Last in both cases the test, checks if the previous marked checkboxs are now unmarked since only one should be marked.

        // If the game was in near mode before
        if (mode.equals("near")) {
            // Since the mode  the user chooses is the same as before test if the toast message appears
            onView(withText("This Mode is Already Selected!")).
                    inRoot(withDecorView(not(is(activity_now.getWindow().getDecorView())))).
                    check(matches(isDisplayed()));

            // Press the Anywhere CheckBox
            ViewInteraction appCompatCheckBox2 = onView(
                    allOf(withId(R.id.from_anywhere), withText("You can select a Song word from anywhere."), isDisplayed()));
            appCompatCheckBox2.perform(click());

            //Check if the warning for the Anywhere Mode pops up
            ViewInteraction textView2 = onView(
                    allOf(withId(R.id.text_dialog),
                            withText("If you are on near mode you will be awarded more points! If you change it once you loose the 10 points Bonus!!!!"),
                            isDisplayed()));
            textView2.check(matches(withText("If you are on near mode you will be awarded more points! If you change it once you loose the 10 points Bonus!!!!")));

            // Press the cancel button to test the behaviour of the game when the user regrets his decision
            ViewInteraction appCompatButton2 = onView(
                    allOf(withId(R.id.btn_cancel_dialog), withText("Dont Change !"), isDisplayed()));
            appCompatButton2.perform(click());

            // Check if the mode preference shared across the game did not change
            String mode2 = mode_pref.getString(Settings.MODE_KEY, "near");
            assertEquals("Mode not Change", mode2, mode);
            // Check if the same CheckBox as Before is still checked
            CheckBox previous_true = activity_now.findViewById(findID(mode));
            assertTrue("CheckBox remain True", previous_true.isChecked());

            // Press the Anywhere CheckBox again to check what the game does when the change is now selected
            ViewInteraction appCompatCheckBox3 = onView(
                    allOf(withId(R.id.from_anywhere), withText("You can select a Song word from anywhere."), isDisplayed()));
            appCompatCheckBox3.perform(click());

            //Check if the warning for the Anywhere Mode pops up
            ViewInteraction textView3 = onView(
                    allOf(withId(R.id.text_dialog),
                            withText("If you are on near mode you will be awarded more points! If you change it once you loose the 10 points Bonus!!!!"),
                            isDisplayed()));
            textView3.check(matches(withText("If you are on near mode you will be awarded more points! If you change it once you loose the 10 points Bonus!!!!")));

            // Press the change button to test the behaviour of the game when the user does not regret his decision
            ViewInteraction appCompatButton3 = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("Change !"), isDisplayed()));
            appCompatButton3.perform(click());

            // Check if the level is now Anywhere
            String mode3 = mode_pref.getString(Settings.MODE_KEY, "near");
            assertEquals("Mode Change", mode3, "anywhere");
            // Check if the Anywhere CheckBox is now Checked
            CheckBox anywhere = activity_now.findViewById(R.id.from_anywhere);
            assertTrue("CheckBox changed to True", anywhere.isChecked());

            // Also check if the previous checked box is now unchecked
            assertFalse("CheckBox changed to True", previous_true.isChecked());

        }
        // if the game was in anywhere mode
        else{
            //Check if the warning for the Anywhere Mode pops up
            ViewInteraction textView2 = onView(
                    allOf(withId(R.id.text_dialog),
                            withText("If you are on near mode you will be awarded more points! If you change it once you loose the 10 points Bonus!!!!"),
                            isDisplayed()));
            textView2.check(matches(withText("If you are on near mode you will be awarded more points! If you change it once you loose the 10 points Bonus!!!!")));

            // Press the cancel button to test the behaviour of the game when the user regrets his decision
            ViewInteraction appCompatButton2 = onView(
                    allOf(withId(R.id.btn_cancel_dialog), withText("Dont Change !"), isDisplayed()));
            appCompatButton2.perform(click());

            // Check if the mode preference shared across the game did not change
            String mode2 = mode_pref.getString(Settings.MODE_KEY, "near");
            assertEquals("Difficulty not Change", mode2, mode);
            // Check if the same CheckBox as Before is still checked
            CheckBox previous_true = activity_now.findViewById(findID(mode));
            assertTrue("CheckBox remain True", previous_true.isChecked());

            // Press the Near CheckBox again to check what the game does when the change is now selected
            ViewInteraction appCompatCheckBox3 = onView(
                    allOf(withId(R.id.near_distance), withText("You have to be 5 meters near to select a Song word!"), isDisplayed()));
            appCompatCheckBox3.perform(click());

            //Check if the warning for the Mode pops up
            ViewInteraction textView3 = onView(
                    allOf(withId(R.id.text_dialog),
                            withText("If you are on near mode you will be awarded more points! If you change it once you loose the 10 points Bonus!!!!"),
                            isDisplayed()));
            textView3.check(matches(withText("If you are on near mode you will be awarded more points! If you change it once you loose the 10 points Bonus!!!!")));

            // Press the change button to test the behaviour of the game when the user does not regret his decision
            ViewInteraction appCompatButton3 = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("Change !"), isDisplayed()));
            appCompatButton3.perform(click());

            // Check if the level is now Near
            String mode3 = mode_pref.getString(Settings.MODE_KEY, "near");
            assertEquals("Difficulty Change", mode3, "near");
            // Check if the Near CheckBox is now Checked
            CheckBox near = activity_now.findViewById(R.id.near_distance);
            assertTrue("CheckBox changed to True", near.isChecked());

            // Also check if the previous checked box is now unchecked
            assertFalse("CheckBox changed to True", previous_true.isChecked());

            // Press the Near CheckBox again to check if the toast message pops up
            ViewInteraction appCompatCheckBox4 = onView(
                    allOf(withId(R.id.near_distance), withText("You have to be 5 meters near to select a Song word!"), isDisplayed()));
            appCompatCheckBox4.perform(click());

            // Since the mode  the user chooses is the same as before test if the toast message appears
            onView(withText("This Mode is Already Selected!")).
                    inRoot(withDecorView(not(is(activity_now.getWindow().getDecorView())))).
                    check(matches(isDisplayed()));
        }








       /* For the Map But When the Markers are pressed the Espresso test cannot understand this

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction imageView = onView(
                allOf(withContentDescription("My Location"), isDisplayed()));
        imageView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        pressBack();*/

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

    // Method for finding the R.id of mode scheckBoxes given the corresponding mode (e.g near-> R.id.near)
    private int findID(String mode) {
        int mode_checkbbox = 1;
        switch (mode) {
            case "near":
                mode_checkbbox = R.id.near_distance;
                break;
            case "anywhere":
                mode_checkbbox = R.id.from_anywhere;
                break;

        }
        return mode_checkbbox;
    }

}
