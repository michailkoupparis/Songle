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
import android.widget.TextView;

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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;

/* Test for text View holding previous selected song in Select Song screen.
* First it test if the previous selected song saved in the share preference is the same as the context od the textView
*  Then it changes the song test if the textview remains the same (it should stay the same for the user to know for which song he played before to be able to sleceted it again before leaving the Activity),
*  then leave the activity and comes back and test if now the textView holds the changed value.*/

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PreviousSongTextTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void previousSongTextTest() {
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
        // to proceed to the Song Selection activity

        Boolean first_time = Home.first_time;

        if (first_time){
            // Press cancel, and so close the tutorial and return to the home screen (Home)
            ViewInteraction closeTutorial = onView(
                    allOf(withId(R.id.cancel_dialog), withText("CANCEL"), isDisplayed()));
            closeTutorial.perform(click());
        }

        Boolean wifi = true;
        // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
        if(!checkWifi()){
            close_check_WifiDialog();
            wifi = false;
        }

        // Go to Song Selection
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.go_to_song_selection), withText("SELECT SONG"), isDisplayed()));
        appCompatButton.perform(click());

        // Check if the Song Selection Screen is reached
        ViewInteraction textView = onView(
                allOf(withId(R.id.song_selection_title), withText("SELECT SONG"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("SELECT SONG")));

        // Get the current activity
        Activity activity_now = getCurrentActivity();

        // Find the previous Selected Song
        SharedPreferences song_pref =
                activity_now.getSharedPreferences(SongSelection.SONG_PREF_TAG, Context.MODE_PRIVATE);

        String song = song_pref.getString(SongSelection.SONG_PREF_KEY, "01");

        // Get the text View to see if it holds the right value
        TextView prev_song = activity_now.findViewById(R.id.current_song_num);

        /// Modify the song number to match the description of the textView
        // Also by making it to integer if is under 10 the 0 before the number is dropped
        Integer song_num = Integer.parseInt(song);
        String right_context = "Previously selected song:   " + song_num;

        //Check if the values are matching
        assertEquals("Text View in Song Selection has previous Song",prev_song.getText(),right_context);

        // If there was not wifi before in the Home Activity the Songs are not Downloaded and so the grid is empty
        if(wifi) {

            String selected = "07";
            // If the song number was not 7
            // Press the Song Number 07 at the grid
            // If not press 9 so as the song will change for sure
            if (!song.equals("07")) {
                ViewInteraction textView2 = onView(
                        allOf(withText("07"),
                                childAtPosition(
                                        withId(R.id.song_selection_grid),
                                        6),
                                isDisplayed()));
                textView2.perform(click());
            }
            else{
                selected = "09";
                ViewInteraction textView2 = onView(
                        allOf(withText("09"),
                                childAtPosition(
                                        withId(R.id.song_selection_grid),
                                        8),
                                isDisplayed()));
                textView2.perform(click());
            }

            // Press Ok to change the song number
            ViewInteraction appCompatButton2 = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("Change"), isDisplayed()));
            appCompatButton2.perform(click());

            // Check if the Text View Remain the same
            assertEquals("Text View in Song Selection has previous Song",prev_song.getText(),right_context);

            // Return to the Home Screen
            pressBack();

            // Come Back to the Song Selection Screen to check if the textView is changed
            ViewInteraction appCompatButton3 = onView(
                    allOf(withId(R.id.go_to_song_selection), withText("SELECT SONG"), isDisplayed()));
            appCompatButton3.perform(click());

            // Get the current activity
            Activity activity_now2 = getCurrentActivity();

            // Get the text View to see if it holds the right value
            TextView prev_song2 = activity_now2.findViewById(R.id.current_song_num);

            /// Modify the song number to match the description of the textView
            // Also by making it to integer if is under 10 the 0 before the number is dropped
            Integer song_num2 = Integer.parseInt(selected);
            String right_context2 = "Previously selected song:   " + song_num2;

            //Check if the values are matching
            assertEquals("Text View in Song Selection has previous Song",prev_song2.getText(),right_context2);


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
