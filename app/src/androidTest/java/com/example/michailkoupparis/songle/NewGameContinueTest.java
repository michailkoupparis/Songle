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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

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
/* Test for continue and new game button.
* When the continue button is pressed the game continues from where he left for the song in play.
* So a set of markers is created and then the continue button is pressed to check if they carry on.
* And also it checks if the found markers are no longer presented in the Map.
* Then the New game is pressed, when it pressed a new game warning pops up.
* First the test check the if the dialog close and the Home Activity is now displayed if
* the user regrets and selects to continue.
* Then it press again the New Game button but this time it selects to not regret its decision.
* So now the test checks if the found markers are empty.
* Then the test checks the behaviour of the buttons when a song or difficulty is changed.
* Because when the song is change the Markers of the Map should be different and so the same is for the found markers.
* Also when difficulty is change the found markers are removed because otherwise the game can be cheated.
* So the test check if when the Continue button is pressed found markers after the map is reaches are empty.
* Also the same for New Game button and this time the warnign sould not pop up since game is not in progress.
* This test also takes consideration along the way for internet connection and location disability.
*  Also it test if no markers are displayed on Map Activity if there is no wifi.
*   !! The test requires the user to have allowed previous the app to access the location.
*  !! Or to be presented while the test is run and accept while its run to be successful.
*  !!! Because the permission Dialog pops up and the test cannot close it.
  * Otherwise the test will end successfully before finish entirely.*/
@LargeTest
@RunWith(AndroidJUnit4.class)
public class NewGameContinueTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void newGameContinueTest() {

        Activity welcome_activity = getCurrentActivity();
        // Update the Change Preference to false which indicates a difficulty level or song does not change
        // which means the previous selected markers should remain the same when the Map is reach after
        // Continue Button is pressed
        // Change is updated now because it found when Home Activity is reach
        SharedPreferences.Editor editor_change =
                welcome_activity.getSharedPreferences(Home.CHANGE_TAG, Context.MODE_PRIVATE).edit();
        editor_change.putBoolean(Home.CHANGE_KEY, false);
        editor_change.apply();

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
        if(!checkWifi()){
            close_check_WifiDialog();
        }


        // Get the current activity which is the Home activity
        Activity activity_home = getCurrentActivity();

        // Update the Song such as to be Sure what Song is now in play in the test
        SharedPreferences.Editor editor_song =
                activity_home.getSharedPreferences(SongSelection.SONG_PREF_TAG, Context.MODE_PRIVATE).edit();
        editor_song.putString(SongSelection.SONG_PREF_KEY, "01");
        editor_song.apply();

        // Update the Difficulty Level such as to be Sure what Difficulty is now in play in the test
        SharedPreferences.Editor editor_level =
                activity_home.getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, Context.MODE_PRIVATE).edit();
        editor_level.putString(Settings.DIFFICULTY_LEVEL_KEY, "1");
        editor_level.apply();


        // Set for Holding found Markers , markers are indicating in the game by their position in the Online file
        // of each song containing its lyrics
        Set<String> found_markers = new HashSet<>();
        found_markers.add("55:1");
        found_markers.add("21:7");
        found_markers.add("1:2");
        found_markers.add("30:2");
        found_markers.add("15:2");

        // Update the Found markers since the song is changed
        SharedPreferences.Editor editor_markers =
                activity_home.getSharedPreferences(MapsActivity.FOUND_MARKERS_TAG, Context.MODE_PRIVATE).edit();
        editor_markers.putStringSet(MapsActivity.FOUND_MARKERS_KEY, found_markers);
        editor_markers.apply();

        // Check if location is enabled
        // If the location is enabled a warning pops up
        // To allow the test to continue successfully the warning has to be closed
        LocationManager lm = (LocationManager)
                activity_home.getSystemService(Context.LOCATION_SERVICE);


        // Press the Continue Button to check if the markers added curry through
        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.continue_game), withText("CONTINUE"), isDisplayed()));
        appCompatButton5.perform(click());

        // Delay for Map to be ready
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // If the permission dialog is not closed the test cannot carry on
        // and so it will finish earlier
        if (MapsActivity.mLocationPermissionAnswered) {

            // If the location is not enable a warning dialog pop ups
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Close the location warning
                ViewInteraction location_warning_close = onView(
                        allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
                location_warning_close.perform(click());
            }

            // Get the found markers on Map
            SharedPreferences markers_pref =
                    activity_home.getSharedPreferences(MapsActivity.FOUND_MARKERS_TAG, Context.MODE_PRIVATE);
            Set<String> markers_on_map = markers_pref.getStringSet(MapsActivity.FOUND_MARKERS_KEY, new HashSet<String>());

            // Check if the found markers went through to the map
            assertEquals("Found Markers remain the Same",markers_on_map,found_markers);

            Activity map_activity = getCurrentActivity();
            MapsActivity map = (MapsActivity) map_activity;

            //Get the google Map which is active now
            Set<String> mMapMarkersTitles = map.getMarkers();

            for(String marker_tile : found_markers){
                assertTrue(marker_tile+"Not in the Map",!mMapMarkersTitles.contains(marker_tile));
            }

            // Go back to check the New Game Button
            pressBack();


            // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
            if(!checkWifi()){
                close_check_WifiDialog();
            }

            // Press New Game
            ViewInteraction appCompatButton3 = onView(
                    allOf(withId(R.id.new_game), withText("NEW GAME"), isDisplayed()));
            appCompatButton3.perform(click());

            // Check if the new game warning appears
            ViewInteraction textView = onView(
                    allOf(withId(R.id.text_dialog), withText("Game is in Progress!!"),

                            isDisplayed()));
            textView.check(matches(withText("Game is in Progress!!")));

            // Press Continue to check the behaviour of the game when the user regrets his decision
            ViewInteraction appCompatButton2 = onView(
                    allOf(withId(R.id.btn_cancel_dialog), withText("Continue!"), isDisplayed()));
            appCompatButton2.perform(click());

            // Check if the dialog is closed and the game is on the Home Activity
            ViewInteraction textView2 = onView(
                    allOf(withId(R.id.home_title), withText("SONGLE"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0),
                                    0),
                            isDisplayed()));
            textView2.check(matches(withText("SONGLE")));

            // Get the activity home again
            activity_home = getCurrentActivity();

            // Press New Game again
            ViewInteraction appCompatButton4 = onView(
                    allOf(withId(R.id.new_game), withText("NEW GAME"), isDisplayed()));
            appCompatButton4.perform(click());

            // Check if the New Game warning appears
            ViewInteraction textView3 = onView(
                    allOf(withId(R.id.text_dialog), withText("Game is in Progress!!"),

                            isDisplayed()));
            textView3.check(matches(withText("Game is in Progress!!")));


            // Check if location is enabled
            // If the location is enabled a warning pops up
            // To allow the test to continue successfully the warning has to be closed
            lm = (LocationManager)
                    activity_home.getSystemService(Context.LOCATION_SERVICE);
             // Go to New Game
            ViewInteraction appCompatButton6 = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("New Game!"), isDisplayed()));
            appCompatButton6.perform(click());

            // Added a sleep statement to match the app's execution delay.
            // The recommended way to handle such scenarios is to use Espresso idling resources:
            // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // If the permission dialog is not closed the test cannot carry on
            // and so it will finish earlier
            if (MapsActivity.mLocationPermissionAnswered) {

                // If the location is not enable a warning dialog pop ups
                if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // Close the location warning
                    ViewInteraction location_warning_close = onView(
                            allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
                    location_warning_close.perform(click());
                }

                // Get the found markers on Map
                markers_on_map = markers_pref.getStringSet(MapsActivity.FOUND_MARKERS_KEY, new HashSet<String>());

                // Check if the found markers are no empty since it is a new game
                assertTrue("Found Markers are empty", markers_on_map.isEmpty());

                // Go back to Home to check the buttons when a Difficulty ro song are changed
                pressBack();

                // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
                if (!checkWifi()) {
                    close_check_WifiDialog();
                }

                // Get the current activity which is the Home activity
                activity_home = getCurrentActivity();

                // Update the Change Preference which indicates a difficulty level or song is change which means the
                // previous selected markers should be empty when the Map is reach
                editor_change.putBoolean(Home.CHANGE_KEY, true);
                editor_change.apply();

                // Press the Continue Button to check if the markers added curry through
                ViewInteraction appCompatButton7 = onView(
                        allOf(withId(R.id.continue_game), withText("CONTINUE"), isDisplayed()));
                appCompatButton7.perform(click());

                // Check if location is enabled
                // If the location is enabled a warning pops up
                // To allow the test to continue successfully the warning has to be closed
                lm = (LocationManager)
                        activity_home.getSystemService(Context.LOCATION_SERVICE);

                // Delay for Map to be ready
                // Added a sleep statement to match the app's execution delay.
                // The recommended way to handle such scenarios is to use Espresso idling resources:
                // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // If the permission dialog is not closed the test cannot carry on
                // and so it will finish earlier
                if (MapsActivity.mLocationPermissionAnswered) {

                    // If the location is not enable a warning dialog pop ups
                    if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        // Close the location warning
                        ViewInteraction location_warning_close = onView(
                                allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
                        location_warning_close.perform(click());
                    }

                    // Get the found markers on Map
                    markers_on_map = markers_pref.getStringSet(MapsActivity.FOUND_MARKERS_KEY, new HashSet<String>());

                    // Check if the found markers are no empty since it is a new game
                    assertTrue("Found Markers are empty", markers_on_map.isEmpty());

                    // Go back to check the New Game Button
                    pressBack();

                    // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
                    if (!checkWifi()) {
                        close_check_WifiDialog();
                    }

                    // Now do the same for the New Game Button and check if it behaves the same as Continue for
                    // change situations and also the new game warnign not appears since game now is not in progress.

                    // Get the current activity which is the Home activity
                    activity_home = getCurrentActivity();

                    // Update the Change Preference which indicates a difficulty level or song is change which means the
                    // previous selected markers should be empty when the Map is reach
                    editor_change.putBoolean(Home.CHANGE_KEY, true);
                    editor_change.apply();

                    // Press the Continue Button to check if the markers added curry through
                    ViewInteraction appCompatButton8 = onView(
                            allOf(withId(R.id.continue_game), withText("CONTINUE"), isDisplayed()));
                    appCompatButton8.perform(click());

                    // Check if location is enabled
                    // If the location is enabled a warning pops up
                    // To allow the test to continue successfully the warning has to be closed
                    lm = (LocationManager)
                            activity_home.getSystemService(Context.LOCATION_SERVICE);

                    // Delay for Map to be ready
                    // Added a sleep statement to match the app's execution delay.
                    // The recommended way to handle such scenarios is to use Espresso idling resources:
                    // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // If the permission dialog is not closed the test cannot carry on
                    // and so it will finish earlier
                    if (MapsActivity.mLocationPermissionAnswered) {

                        // If the location is not enable a warning dialog pop ups
                        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            // Close the location warning
                            ViewInteraction location_warning_close = onView(
                                    allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
                            location_warning_close.perform(click());
                        }

                        // Get the found markers on Map
                        markers_on_map = markers_pref.getStringSet(MapsActivity.FOUND_MARKERS_KEY, new HashSet<String>());

                        // Check if the found markers are no empty since it is a new game
                        assertTrue("Found Markers are empty", markers_on_map.isEmpty());
                    }
                }
            }

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
