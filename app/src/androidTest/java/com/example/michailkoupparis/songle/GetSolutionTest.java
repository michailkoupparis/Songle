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
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
/* Test for Getting Solution when the user got stacked and whats to ge the Solution.
* The user can get the Solution in the Found Words Activity.
* Found Words Activity where the user can try to guess the solution so get solution is also there.
* There is a get solution button if the user press the button a dialog pops up.
* Then the user can either close the dialog and continue trying or press Solve Anyway and get the solution.
* If he decides to close the dialog then the game return to the Found Words activity, the game check this first.
* Then it presses the Get Solution button again but this time it press the Solve anyway.
* Then another Dialog indicating what the Song was and that the user awarded 0 points.
* The test check if this is true.
* Then the user can either got to the Next Song or to Song Selection Activity.
* First the test checks the Go to Song Selection option.
* If the user chooses this then the Song Selection Activity opens, so the test emulates
* the user choosing whatever songs and comes back to Found Words Activity.
* Then the same procedure it tested for the new Song but in the end The Go to Next Song is tested.
* When this option is selected the map is open so the test first check this.
*   !!! The test requires the user to this point to have allowed previous the app to access the location.
*   !!! Or to be presented while the test is run and accept while its run to be successful.
*   !!! Because the permission Dialog pops up and the test cannot close it.
*   !!! Otherwise the test will finish here before checking the Next Song option.
* Then the bag of words image button is pressed that takes the user to Found Words Activity.
* Now the same procedure as before is used to test the behaviour of get solution but this
* time it checks if the Right solution which will appear is the next song.
* The song next in arithmetic order based on their number on the online file holding the Songs.
* Then it checks if the Found Songs Set contains the 3 new found songs.
*/
@LargeTest
@RunWith(AndroidJUnit4.class)
public class GetSolutionTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void getSolutionTest() {
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
        // Get the current activity which is the Home Activity
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
        Activity home_activity = getCurrentActivity();
        // change the selected song to make sure the test check for the right song
        // Update the Song such as to be Sure what Song is now in play in the test
        SharedPreferences.Editor editor_song =
                home_activity.getSharedPreferences(SongSelection.SONG_PREF_TAG, Context.MODE_PRIVATE).edit();
        editor_song.putString(SongSelection.SONG_PREF_KEY, "14");
        editor_song.apply();

        // Go to Found Words Activity where the user can get the solution
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.words_found), withText("FOUND WORDS"), isDisplayed()));
        appCompatButton.perform(click());

        // Check if the game is now in the Found Words Activity
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

        // Press the Get Solution Button to get the Solution
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.solve), withText("GET SOLUTION"),
                        withParent(allOf(withId(R.id.rl),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatButton2.perform(click());

        // Check if the Get Solution Dialog pops up
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.text_dialog), withText("Are you Sure? No points for given solutions!!!"),

                        isDisplayed()));
        textView2.check(matches(withText("Are you Sure? No points for given solutions!!!")));

        // Press the Continue trying button and check if the dialog is now close
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_cancel_dialog), withText("Continue Trying."), isDisplayed()));
        appCompatButton3.perform(click());

        // Check if the game is now in the Found Words Activity which means the Dialog has closed
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

        // Press the Get Solution Button again to check the behaviour of the game
        // when the user decides to get the solution
        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.solve), withText("GET SOLUTION"),
                        withParent(allOf(withId(R.id.rl),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatButton4.perform(click());

        // Check if the Get Solution Dialog pops up
        ViewInteraction textView4 = onView(
                allOf(withId(R.id.text_dialog), withText("Are you Sure? No points for given solutions!!!"),

                        isDisplayed()));
        textView4.check(matches(withText("Are you Sure? No points for given solutions!!!")));

        // Press the Solve Button to get the Solution
        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.btn_ok_dialog), withText("Solve Anyway!"), isDisplayed()));
        appCompatButton5.perform(click());

        // If there was not internet in the Home Screen the Songs have not been downloaded
        // and so the Solution cannot be found to give it to user.
        // In this situation a Toast message informing the user pops up.
        if (internet) {

            // Check if the Dialog for found Solution pops up
            // Since the user has taken the solution.
            ViewInteraction textView5 = onView(
                    allOf(withId(R.id.text_dialog), withText("Correct Solution Congratulations!!!!!"),

                            isDisplayed()));
            textView5.check(matches(withText("Correct Solution Congratulations!!!!!")));

            // Check if the Right Solution appears and since it has take the solution
            // The game should not award the user any points
            ViewInteraction textView6 = onView(
                    allOf(withId(R.id.solution_dialog), withText("Solution was:\n " +
                                    "I'm Gonna Be (500 Miles)\n\n " +
                                    "Awarded Points: 0"),

                            isDisplayed()));
            textView6.check(matches(withText("Solution was:\n I'm Gonna Be (500 Miles)\n\n Awarded Points: 0")));

            // Now the User can select either to go to the Map and continue on the next song
            // Or go to the song selection and selects whatever song the user wants.

            // Check the behaviour of the game when the User selects to go to Song Selection
            // and selects whatever Song he wants

            // Press the Go to Next Song
            ViewInteraction appCompatButton6 = onView(
                    allOf(withId(R.id.btn_selectDifferent), withText("Go to Song Selection!"), isDisplayed()));
            appCompatButton6.perform(click());

            // Check if the Game is now in the Map Activity
            ViewInteraction textView7 = onView(
                    allOf(withId(R.id.song_selection_title), withText("SELECT SONG"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0),
                                    0),
                            isDisplayed()));
            textView7.check(matches(withText("SELECT SONG")));

            // Update the Song to emulate the behaviour of the game when the user change the song here
            editor_song.putString(SongSelection.SONG_PREF_KEY, "09");
            editor_song.apply();

            // Go to the Home Screen to access the Found Words Activity from there
            pressBack();

            // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
            if(!checkWifi()){
                close_check_WifiDialog();
                internet = false;
            }

            // Go to the Found Words Activity
            ViewInteraction appCompatButton7 = onView(
                    allOf(withId(R.id.words_found), withText("FOUND WORDS"), isDisplayed()));
            appCompatButton7.perform(click());

            // Get the activity to check later if the location is enabled
            Activity activity_now = getCurrentActivity();

            // Press the get Solution to open the Get Solution dialog
            ViewInteraction appCompatButton8 = onView(
                    allOf(withId(R.id.solve), withText("GET SOLUTION"),
                            withParent(allOf(withId(R.id.rl),
                                    withParent(withId(android.R.id.content)))),
                            isDisplayed()));
            appCompatButton8.perform(click());

            // Press the Solve anyway Button to get the solution
            ViewInteraction appCompatButton9 = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("Solve Anyway!"), isDisplayed()));
            appCompatButton9.perform(click());

            // If there is no internet the Dialog must remain open
            // and a Toast Message informing the user that he has to get access to internet and go back to home
            // Else the Solution Dialog should Appear
            if (internet) {
                //Check if the Solution Dialog pops up
                ViewInteraction textView8 = onView(
                        allOf(withId(R.id.text_dialog), withText("Correct Solution Congratulations!!!!!"),

                                isDisplayed()));
                textView8.check(matches(withText("Correct Solution Congratulations!!!!!")));

                // Check if the Song appears is Song 9 (Heart of Glass) and since it has take the solution
                // The game should not award him any points
                ViewInteraction textView9 = onView(
                        allOf(withId(R.id.solution_dialog), withText("Solution was:\n " +
                                        "Heart of Glass\n\n " +
                                        "Awarded Points: 0"),

                                isDisplayed()));
                textView9.check(matches(withText("Solution was:\n Heart of Glass\n\n Awarded Points: 0")));

                // Check the behaviour of the game when the User selects to go to Map and continue to next Song
                ViewInteraction appCompatButton10 = onView(
                        allOf(withId(R.id.btn_nextSong), withText("Go to Next Song!"), isDisplayed()));
                appCompatButton10.perform(click());


                // Check if location is enabled
                // If the location is enabled a warning pops up
                // To allow the test to continue successfully the warning has to be closed
                LocationManager lm = (LocationManager)
                        activity_now.getSystemService(Context.LOCATION_SERVICE);

                // !!!!!!!WARNING!!!!!!!
                // If it is the first time the map is accessed then a dialog will pop up asking the user for location permission
                // For the test to be successful entirely the person you runs have to answer the dialog
                // Otherwise the test will finish before run to end

                // Added a sleep statement to match the app's execution delay.
                // The recommended way to handle such scenarios is to use Espresso idling resources:
                // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
                // for the game to be loaded
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // If the permission dialog is not closed the test cannot carry on
                // and so it will finish earlier
                if(MapsActivity.mLocationPermissionAnswered) {

                    // If the location is not enable a warning dialog pop ups
                    if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        // Close the location warning
                        ViewInteraction location_warning_close = onView(
                                allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
                        location_warning_close.perform(click());
                    }

                    // Check if the Map Activity now appears
                    ViewInteraction relativeLayout = onView(
                            allOf(childAtPosition(
                                    childAtPosition(
                                            withId(R.id.map),
                                            0),
                                    0),
                                    isDisplayed()));
                    relativeLayout.check(matches(isDisplayed()));

                    // Press the Bag of Words Button to return to the Found Words Activity
                    ViewInteraction imageButton = onView(
                            allOf(withId(R.id.bag_of_words),
                                    withParent(allOf(withId(R.id.map),
                                            withParent(withId(android.R.id.content)))),
                                    isDisplayed()));
                    imageButton.perform(click());

                    // Press the Get Solution Button
                    ViewInteraction appCompatButton11 = onView(
                            allOf(withId(R.id.solve), withText("GET SOLUTION"),
                                    withParent(allOf(withId(R.id.rl),
                                            withParent(withId(android.R.id.content)))),
                                    isDisplayed()));
                    appCompatButton11.perform(click());

                    // Press the Solve any way to get the solution
                    ViewInteraction appCompatButton12 = onView(
                            allOf(withId(R.id.btn_ok_dialog), withText("Solve Anyway!"), isDisplayed()));
                    appCompatButton12.perform(click());

                    // Check if the right Solution Dialog appears
                    ViewInteraction textView10 = onView(
                            allOf(withId(R.id.text_dialog), withText("Correct Solution Congratulations!!!!!"),

                                    isDisplayed()));
                    textView10.check(matches(withText("Correct Solution Congratulations!!!!!")));

                    // Check if the right Solution appears which is Song 10(Vogue) since
                    // previous the Song was 9 and the user select to continue on the next song
                    // ans since the solution has been takes the user should not be awarded any points
                    ViewInteraction textView11 = onView(
                            allOf(withId(R.id.solution_dialog), withText("Solution was:\n " +
                                            "Vogue\n\n " +
                                            "Awarded Points: 0"),

                                    isDisplayed()));
                    textView11.check(matches(withText("Solution was:\n Vogue\n\n Awarded Points: 0")));

                    // Get the found songs to check if the 3 Songs for which the solution is taken
                    // are now in the found songs set.
                    Set<String> found_songs = WelcomeScreen.song_founds;
                    assertTrue("Song 14 is in found songs",found_songs.contains("I'm Gonna Be (500 Miles)"));
                    assertTrue("Song 09 is in found songs",found_songs.contains("Heart of Glass"));
                    assertTrue("Song 10 is in found songs",found_songs.contains("Vogue"));
                }
            }
        }
        // If there is no internet the Dialog must remain open
        // and a Toast Message informing the user that he has to get access to internet and go back to home
        // for the Songs to be downloaded.
        else{
            // Check if the Get Solution Dialog remains open
            ViewInteraction textView5 = onView(
                    allOf(withId(R.id.text_dialog), withText("Are you Sure? No points for given solutions!!!"),

                            isDisplayed()));
            textView5.check(matches(withText("Are you Sure? No points for given solutions!!!")));
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
