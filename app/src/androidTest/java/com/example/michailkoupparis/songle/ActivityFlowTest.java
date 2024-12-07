package com.example.michailkoupparis.songle;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
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
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
/* Test for general games's Activity flow
   Each Activity has a unique Title
   So each time an activity is reached the test check if it has the right title.
   ******The test requires the user to have allowed previous the app to access the location.
  ******* Or to be presented while the test is run and accept while its run.
  * *  !!! Because the permission Dialog pops up and the test cannot close it.
  * Otherwise the test will end successfully before finish entirely.*/
@RunWith(AndroidJUnit4.class)
public class ActivityFlowTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void activityFlowTest() {
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
        // the rest of the activity flow


        Boolean first_time = Home.first_time;

        if (first_time){
            // Press cancel, and so close the tutorial and return to the home screen (Home)
            ViewInteraction closeTutorial = onView(
                    allOf(withId(R.id.cancel_dialog), withText("CANCEL"), isDisplayed()));
            closeTutorial.perform(click());
        }

        // Check if the wifi warning will pop up
        checkWifi();

        // Check if the first activity after the Welcome screen is the Home activity

        ViewInteraction textView = onView(
                allOf(withId(R.id.home_title), withText("SONGLE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("SONGLE")));

        // Check if after the score information is pressed the game reach the ScoreHelp activity
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.score_information), isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.home_title), withText("SCORE"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("SCORE")));

        // Go Back to the Home Screen
        pressBack();
        // Check if the wifi warning will pop up
        checkWifi();

        // Check if after the Song Selection Button in the Home screen is pressed
        // the game reach the SongSelection activity
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.go_to_song_selection), withText("SELECT SONG"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.song_selection_title), withText("SELECT SONG"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView3.check(matches(withText("SELECT SONG")));

        // Check if the game goes to Song Player Activity (Screen) when the found songs playlist in Song Selection is pressed
        ViewInteraction go_to_song_player = onView(
                allOf(withId(R.id.go_to_song_player), withText("Found Songs Playlist"), isDisplayed()));
        go_to_song_player.perform(click());

        ViewInteraction song_player_title = onView(
                allOf(withId(R.id.song_selection_title), withText("SONG PLAYER"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        song_player_title.check(matches(withText("SONG PLAYER")));

        // Return to Song Selection
        pressBack();

        // Check if the game returned to Song Selection
        ViewInteraction song_selection_title = onView(
                allOf(withId(R.id.song_selection_title), withText("SELECT SONG"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        song_selection_title.check(matches(withText("SELECT SONG")));

        // Go back to the Home Activity
        pressBack();
        // Check if the wifi warning will pop up
        checkWifi();

        // Check if after the Settings Button in the Home screen is pressed
        // the game reach the Settings activity
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.settings_button), withText("SETTINGS"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.home_title), withText("SETTINGS"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView4.check(matches(withText("SETTINGS")));

        // Go back to the Home Activity
        pressBack();
        // Check if the wifi warning will pop up
        checkWifi();

        // Check if after the Found Words Button in the Home screen is pressed
        // the game reach the FoundWords activity
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.words_found), withText("FOUND WORDS"), isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.textView2), withText("ALL WORDS FOUND ALREADY"),
                        childAtPosition(
                                allOf(withId(R.id.rl),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        textView5.check(matches(withText("ALL WORDS FOUND ALREADY")));

        // Go back to the Home Activity
        pressBack();
        // Check if the wifi warning will pop up
        checkWifi();

        // First get the current activity which is the Home activity
        Activity activity_now = getCurrentActivity();

        // Check if location is enabled
        // If the location is enabled a warning pops up
        // To allow the test to continue successfully the warning has to be closed
        LocationManager lm = (LocationManager)
                activity_now.getSystemService(Context.LOCATION_SERVICE);

        // !!!!!!!WARNING!!!!!!!
        // If it is the first time the map is accessed then a dialog will pop up asking the user for location permission
        // For the test to be successful entirely the person you runs have to answer the dialog
        // Otherwise the test will finish before run to end
        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.continue_game), withText("CONTINUE"), isDisplayed()));
        appCompatButton4.perform(click());


        // Delay for Map to be loaded
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // If no location a warning dialog pop ups
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Close the location warning
            ViewInteraction location_warning_close = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
            location_warning_close.perform(click());
        }

        // Check if the Location Permission is closed to finish the test
        if (MapsActivity.mLocationPermissionAnswered) {
            // Check if after the Continue Button in the Home screen is pressed
            // the game reach the MapsActivity activity
            ViewInteraction relativeLayout = onView(
                    allOf(childAtPosition(
                            childAtPosition(
                                    withId(R.id.map),
                                    0),
                            0),
                            isDisplayed()));
            relativeLayout.check(matches(isDisplayed()));

            // Check if when your are in Map Activity if you press the Bag Button in the right end of the screen
            // the game reach the FoundWords Activity
            ViewInteraction imageButton = onView(
                    allOf(withId(R.id.bag_of_words),
                            withParent(allOf(withId(R.id.map),
                                    withParent(withId(android.R.id.content)))),
                            isDisplayed()));
            imageButton.perform(click());

            ViewInteraction textView6 = onView(
                    allOf(withId(R.id.textView2), withText("ALL WORDS FOUND ALREADY"),
                            childAtPosition(
                                    allOf(withId(R.id.rl),
                                            childAtPosition(
                                                    withId(android.R.id.content),
                                                    0)),
                                    0),
                            isDisplayed()));
            textView6.check(matches(withText("ALL WORDS FOUND ALREADY")));
            // Go back to the Home Activity
            pressBack();
            // Check if the wifi warning will pop up
            checkWifi();

            // Check if the Home Activity is reached
            ViewInteraction textView7 = onView(
                    allOf(withId(R.id.home_title), withText("SONGLE"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0),
                                    0),
                            isDisplayed()));
            textView7.check(matches(withText("SONGLE")));
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

    private void checkWifi() {

        Context context = mActivityTestRule.getActivity();


        // Find if there is wifi connection when the home screen is reached
        // If there is not an internet connection a dialog will pop up first each time
        // the Home Screen is reached and so it has to be closed each time
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo==null) {
            // If there is not wifi connection close the warning to be able to reach the Home Screen
            ViewInteraction close_wifi_warning = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
            close_wifi_warning.perform(click());
        }
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
