package com.example.michailkoupparis.songle;


import android.app.Activity;
import android.content.Context;
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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
/* Test for no wifi retry button .
* If there is no internet connection warning will pop up each time the Home Screen is reached.
* The user can then press the retry button to try and downloads the file of the downloaded there again.
* If he press the retry button and is he did not acquire internet connection the warning should appear again
* Else the warning should close and the Home Screen should now be displayed.
* This test check the behaviour.
*/

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NoWifiRetryTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void noWifiRetryTest() {
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
        // to proceed to check the Wifi Warning

        Boolean first_time = Home.first_time;

        if (first_time){
            // Press cancel, and so close the tutorial and return to the home screen (Home)
            ViewInteraction closeTutorial = onView(
                    allOf(withId(R.id.cancel_dialog), withText("CANCEL"), isDisplayed()));
            closeTutorial.perform(click());
        }

        // If there is no wifi check if the Warning Dialog appears at the Home Screen
        if(!checkWifi()){
            ViewInteraction textView = onView(
                    allOf(withId(R.id.text_dialog), withText("The game downloads files online. Internet access is required."),

                            isDisplayed()));
            textView.check(matches(withText("The game downloads files online. Internet access is required.")));

            // Press the retry button to check its behaviour
            ViewInteraction appCompatButton = onView(
                    allOf(withId(R.id.btn_retry_dialog), withText("Retry"), isDisplayed()));
            appCompatButton.perform(click());

            // If the user enables the wifi before the he presses the retry button
            // Home Screen should be reached
            // Else the dialog should open again
            if (checkWifi()){
                // Check if the home screen is reached since wifi is now enabled
                ViewInteraction textView2 = onView(
                        allOf(withId(R.id.home_title), withText("SONGLE"),
                                childAtPosition(
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0),
                                        0),
                                isDisplayed()));
                textView2.check(matches(withText("SONGLE")));
            }
            else{
                // Check if the dialog re opens since wifi is still disabled
                ViewInteraction textView3 = onView(
                        allOf(withId(R.id.text_dialog), withText("The game downloads files online. Internet access is required."),

                                isDisplayed()));
                textView3.check(matches(withText("The game downloads files online. Internet access is required.")));
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
