package com.example.michailkoupparis.songle;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.GridView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
/* Test for changing the song number at the Song Selection Activity.
* It checks if the warning dialog pops up when the user tries to change the song
* Then it check the case where the user regrets his decision and decides to remain in the same song.
* And the case where it decides to change the song.
* In both Cases the Share Preference holding the current song number is checked to hold the right value.
* Last the grid cell corresponding to the current song is pressed to check
* if the toast message indicating same song selection is displayed*/

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ChangeSongSelectionTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void changeSongSelectionTest() {
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

        // Go to the Song Selection Activity
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.go_to_song_selection), withText("SELECT SONG"), isDisplayed()));
        appCompatButton.perform(click());

        // If there is no wifi when the home activity is reached then the game did not downloaded the Song Numbers
        // and so the grid holding the song numbers is empty and the Song Change cannot be checked
        if (wifi) {
            // Get the current activity
            Activity activity_now = getCurrentActivity();

            // Find the previous Selected Song
            SharedPreferences song_pref =
                    activity_now.getSharedPreferences(SongSelection.SONG_PREF_TAG, Context.MODE_PRIVATE);

            String song = song_pref.getString(SongSelection.SONG_PREF_KEY, "01");

            // Press the grid cell 1 to change the song number to 1 if it is the 1 change it to 7
            String selected_song = "01";
            if (song.equals("01")) {
                selected_song = "07";
                ViewInteraction textView = onView(
                        allOf(withText("07"),
                                childAtPosition(
                                        withId(R.id.song_selection_grid),
                                        6),
                                isDisplayed()));
                textView.perform(click());
            } else {
                ViewInteraction textView = onView(
                        allOf(withText("01"),
                                childAtPosition(
                                        withId(R.id.song_selection_grid),
                                        0),
                                isDisplayed()));
                textView.perform(click());
            }

            // Check if the warning dialog pops up
            ViewInteraction textView3 = onView(
                    allOf(withId(R.id.text_dialog),

                            isDisplayed()));
            textView3.check(matches(withText("Song in progress, if you select to change the song the progress will be lost!!")));

            // Check the behaviour of the game when the user regrets his decision and selects to continue with the same song
            ViewInteraction appCompatButton2 = onView(
                    allOf(withId(R.id.btn_cancel_dialog), withText("Continue"), isDisplayed()));
            appCompatButton2.perform(click());

            // Check if the song saved in the share preference did not change
            String song2 = song_pref.getString(SongSelection.SONG_PREF_KEY, "01");

            assertEquals("Song remain the same", song, song2);

            // Find the grid cell - song number pressed before and pressed it again
            Integer song_num_selected = Integer.parseInt(selected_song);

            ViewInteraction textView4 = onView(
                    allOf(withText(selected_song),
                            childAtPosition(
                                    withId(R.id.song_selection_grid),
                                    song_num_selected - 1),
                            isDisplayed()));
            textView4.perform(click());

            // Check if the warning dialog pops up
            ViewInteraction textView5 = onView(
                    allOf(withId(R.id.text_dialog),

                            isDisplayed()));
            textView5.check(matches(withText("Song in progress, if you select to change the song the progress will be lost!!")));

            // Check the behaviour of the game when the user selects to change the song number
            // Change th song number
            ViewInteraction appCompatButton3 = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("Change"), isDisplayed()));
            appCompatButton3.perform(click());

            // Check if the song saved in the share preference change and holds the right value
            String song3 = song_pref.getString(SongSelection.SONG_PREF_KEY, "01");

            assertEquals("Song remain the same", selected_song, song3);

            // Sleep is added here because the song may be already found and a toast message
            // pops up if the user selects a song that he already found
            // So a sleep added to prevent that toast message interfere with the Same Song toast message
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Now press again the Cell holding the current Song Number
            // and check if  Toast indicating that the cell corresponding to the current song number is pressed
            ViewInteraction textView6 = onView(
                    allOf(withText(selected_song),
                            childAtPosition(
                                    withId(R.id.song_selection_grid),
                                    song_num_selected - 1),
                            isDisplayed()));
            textView6.perform(click());



            // Since the song the user chooses is the same as before test if the toast message appears
            onView(withText("This Song is Already Selected")).
                    inRoot(withDecorView(not(is(activity_now.getWindow().getDecorView())))).
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
