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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static android.support.test.espresso.Espresso.getIdlingResources;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
/* Test for Map Mode , night or day.
* The test check the time and if the time is between 6 in the morning and 6 in the afternoon
* then the day mode should appears
* otherwie the night mode should appear.
* ******The test requires the user to have allowed previous the app to access the location.
  ******* Or to be presented while the test is run and accept while its run.
  * *  !!! Because the permission Dialog pops up and the test cannot close it.
  * Otherwise the test will end successfully before finish entirely.*/
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MapModeTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void mapModeTest() {
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

        Activity home_activity = getCurrentActivity();
        // Check if location is enabled
        // If the location is enabled a warning pops up
        // To allow the test to continue successfully the warning has to be closed
        LocationManager lm = (LocationManager)
                home_activity.getSystemService(Context.LOCATION_SERVICE);

        // !!!!!!!WARNING!!!!!!!
        // If it is the first time the map is accessed then a dialog will pop up asking the user for location permission
        // For the test to be successful entirely the person you runs have to answer the dialog
        // Otherwise the test will finish before run to end
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.continue_game), withText("CONTINUE"), isDisplayed()));
        appCompatButton.perform(click());

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

            Activity map_activity = getCurrentActivity();
            MapsActivity map = (MapsActivity) map_activity;

            //Get the google Map which is active now
            Integer mMapStyle = map.getmMapStyle();

            // Check the hour and use the map mode according to the hour (night and day modes)
            Calendar cal = Calendar.getInstance(); //Create Calendar-Object
            cal.setTime(new Date());               //Set the Calendar to now
            int hour = cal.get(Calendar.HOUR_OF_DAY); //Get the hour from the calendar
            if(hour <= 18 && hour >= 6) {              // Check if hour is between 8 am and 11pm a
                assertTrue("Day Map in Use",mMapStyle== R.raw.style_day_json);
            }
            else{
                assertTrue("Day Map in Use",mMapStyle == R.raw.style_night_json);
            }
        }
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
