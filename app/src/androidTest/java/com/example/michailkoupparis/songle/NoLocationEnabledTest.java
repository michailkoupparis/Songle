package com.example.michailkoupparis.songle;


import android.app.Activity;
import android.content.Context;
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
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
/* Test for no location enabled.
* If there is location is disabled a warning will pop up each time the MAP ACTIVITY is reached.
* If the user selects to continue without enabling the location then the Map is Opened without the User's Location.
* If the retry button is pressed then if the location is enabled the Map is Opened with the User's location
* else the warning dialog remains open.
  ******The test requires the user to have allowed previous the app to access the location
  ******* Or to be presented while the test is run and accept while its run.
  * *  !!! Because the permission Dialog pops up and the test cannot close it
*/

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NoLocationEnabledTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void noLocationEnabledTest() {
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
        // to proceed to the MapsActivity

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

        // Get current Activity
        Activity activity_now = getCurrentActivity();

        // Check if location is enabled
        LocationManager lm = (LocationManager)
                activity_now.getSystemService(Context.LOCATION_SERVICE);

        // Go to the Maps Activity
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.continue_game), withText("CONTINUE"), isDisplayed()));
        appCompatButton2.perform(click());


        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // !!!!!!!WARNING!!!!!!!
        // If it is the first time the map is accessed then a dialog will pop up asking the user for location permission
        // For the test to be successful the person you runs have to answer the dialog

        // If the location is enabled  a warning dialog pop ups
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            // Check if the Location warning is presented
            checkLocationWarning();

            // Press Ok and close the dialog
            ViewInteraction appCompatButton3 = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
            appCompatButton3.perform(click());

            // Check if the Map is presented
            ViewInteraction relativeLayout = onView(
                    allOf(childAtPosition(
                            childAtPosition(
                                    withId(R.id.map),
                                    0),
                            0),
                            isDisplayed()));
            relativeLayout.check(matches(isDisplayed()));

            // Go back to the Home Screen
            pressBack();

            // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
            if (!checkWifi()) {
                close_check_WifiDialog();
            }

            // Now come back again to check the retry button

            // Get current Activity
            Activity activity_now2 = getCurrentActivity();

            // Check if location is enabled
            LocationManager lm2 = (LocationManager)
                    activity_now2.getSystemService(Context.LOCATION_SERVICE);
            // Go to the Maps Activity

            ViewInteraction appCompatButton4 = onView(
                    allOf(withId(R.id.continue_game), withText("CONTINUE"), isDisplayed()));
            appCompatButton4.perform(click());


            // Added a sleep statement to match the app's execution delay.
            // The recommended way to handle such scenarios is to use Espresso idling resources:
            // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!lm2.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                checkLocationWarning();
                System.out.println("99999999997555676556756757575656");

                // Press retry to check its behaviour
                ViewInteraction button = onView(
                        allOf(withId(R.id.btn_retry_dialog), withText("Retry"), isDisplayed()));
                button.perform(click());
                System.out.println("73297887413872874238074038729");

                // If there the location is still enabled then the dialog should stay open
                if (!lm2.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    checkLocationWarning();

                } else {
                    // Else it must closed and now the Map must be presented without the dialog
                    //  So check if the Map is presented
                    ViewInteraction relativeLayout2 = onView(
                            allOf(childAtPosition(
                                    childAtPosition(
                                            withId(R.id.map),
                                            0),
                                    0),
                                    isDisplayed()));
                    relativeLayout2.check(matches(isDisplayed()));
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

    // Method for Checking if Location Warning appears
    private void checkLocationWarning() {

        ViewInteraction textView = onView(
                allOf(withId(R.id.text_dialog), withText("Location is enabled!Distance and near mode requires location!"),

                        isDisplayed()));
        textView.check(matches(withText("Location is enabled!Distance and near mode requires location!")));
    }
}
