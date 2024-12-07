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
import android.widget.GridView;

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
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
/* Test for no wifi connection
* If there is no wifi a warning will pop up each time the home screen is reached.
* If the user selects to continue without enabling the wifi then views using wifi will be empty.
* This test test if the Song list in Song Selection is empty without wifi.
*  Also it test if no markers are displayed on Map Activity if there is no wifi.
*   !! The test requires the user to have allowed previous the app to access the location.
*  !! Or to be presented while the test is run and accept while its run to be successful.
*  !!! Because the permission Dialog pops up and the test cannot close it.
  * Otherwise the test will end successfully before finish entirely.*/

@LargeTest
@RunWith(AndroidJUnit4.class)
public class No_wifi_Test {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void no_wifi_test() {
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
        // the Home Screen no wifi warning

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

        // Go to the Song Selection Activity
        // There Check the Grid Containing All song Numbers
        // If there is Wifi the Grid must contain all the songs
        // If not it must be empty
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.go_to_song_selection), withText("SELECT SONG"), isDisplayed()));
        appCompatButton2.perform(click());

        // First Get the current activity, the Song Selection then get the grid containign the songs's numbers
        Activity activity_now = getCurrentActivity();

        GridView gridView = activity_now.findViewById(R.id.song_selection_grid);


        // Go back to Home Screen
        pressBack();
        // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
        if(!checkWifi()){
            // Check if the Grid is Empty since there is no wifi, the app cannot downloads the songs
            // and add them to the grid
            assertTrue("Grid View is Empty",gridView.getCount() == 0);
            close_check_WifiDialog();
        }
        else{
            // Check if the grid have all the song numbers
            // Home.song._attributes contains all the song numbers that are in the online file
            // This way if new song are added to the online file the test will not have problems deal with it
            assertTrue("Grid View has All Songs",gridView.getCount() == Home.song_attributes.getNumbers().size());
        }

        // First get the current activity which is the Home activity
        activity_now = getCurrentActivity();

        // Check if location is enabled
        // If the location is enabled a warning pops up
        // To allow the test to continue successfully the warning has to be closed
        LocationManager lm = (LocationManager)
                activity_now.getSystemService(Context.LOCATION_SERVICE);

        // Go to Map Activity
        ViewInteraction appCompatButton4 = onView(
                    allOf(withId(R.id.continue_game), withText("CONTINUE"), isDisplayed()));
        appCompatButton4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        // Sleep until map is ready
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

            // Go back to Home Screen
            pressBack();
            // If there is no wifi close the Wifi Warning and check if it appears
            if (!checkWifi()) {
                close_check_WifiDialog();
            }

            ViewInteraction textView4 = onView(
                    allOf(withId(R.id.home_title), withText("SONGLE"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0),
                                    0),
                            isDisplayed()));
            textView4.check(matches(withText("SONGLE")));
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
