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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
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
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
/* Test for Hint .
* The user can select to get Hint.
* Hints are taken in the Found Words Activity where the user can try to guess the solution so hints are also there.
* There is a hint button if the user press the button a dialog pops up.
* Then the user can either close the dialog or press SHOW and get the hint.
* The hint then appears in the Dialog.
* If the hint was already taken it appears in the Dialog from the start.
* If there is no internet connection in the Home Activity the Songs are not Downloaded and no hint appears.
* This test checks if the app match this behaviour.
*/
@LargeTest
@RunWith(AndroidJUnit4.class)
public class HintTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void hintTest() {
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

        // Get current Activity
        Activity activity_now = getCurrentActivity();

        // Update the song preference to hold the song in number 17
        SharedPreferences.Editor editor =
                activity_now.getSharedPreferences(SongSelection.SONG_PREF_TAG, Context.MODE_PRIVATE).edit();
        editor.putString(SongSelection.SONG_PREF_KEY, "17");
        editor.apply();

        // Update the hint preference to false such that we know the current value of hint
        SharedPreferences.Editor editor_hint =
                activity_now.getSharedPreferences(FoundWords.Hint_PENALTY_TAG, Context.MODE_PRIVATE).edit();
        editor_hint.putBoolean(FoundWords.Hint_PENALTY_KEY, false);
        editor_hint.apply();

        // Go the Found Words Activity where the user can take Hints
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.words_found), withText("FOUND WORDS"), isDisplayed()));
        appCompatButton.perform(click());


        // Check if the Found Words Activity is reach
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


        // Press the Hint Button to Open the Dialog
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btn_hint),
                        withParent(allOf(withId(R.id.rl),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        // Press the Hint Button to Open the Hint Dialog
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.text_dialog), withText("Showing song artist cost 10 points"),

                        isDisplayed()));
        textView2.check(matches(withText("Showing song artist cost 10 points")));

        // If there was internet in the Home Activity the Songs Artist have been downloaded
        if(wifi) {

            // Press the Show Button to Display the Hint
            ViewInteraction appCompatButton2 = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("SHOW"), isDisplayed()));
            appCompatButton2.perform(click());

            // Check if the Artist of Song 17 now Appears
            ViewInteraction textView3 = onView(
                    allOf(withId(R.id.hint_container), withText("REM"),

                            isDisplayed()));
            textView3.check(matches(withText("REM")));

            // Check if the Share Preference Holding a boolean indicating if a hint is taken is now true
            SharedPreferences hint_pref =
                    activity_now.getSharedPreferences(FoundWords.Hint_PENALTY_TAG, Context.MODE_PRIVATE);
            Boolean hint_taken = hint_pref.getBoolean(FoundWords.Hint_PENALTY_KEY, false);

            assertTrue("Hint is now Taken",hint_taken);

            // Press the cancel button to return to the Found Words Activity
            ViewInteraction appCompatButton4 = onView(
                    allOf(withId(R.id.btn_cancel_dialog), withText("CANCEL"), isDisplayed()));
            appCompatButton4.perform(click());

            // Check If the Dialog is now closed and the Found Words Activity is displayed
            ViewInteraction textView4 = onView(
                    allOf(withId(R.id.textView2), withText("ALL WORDS FOUND ALREADY"),
                            childAtPosition(
                                    allOf(withId(R.id.rl),
                                            childAtPosition(
                                                    withId(android.R.id.content),
                                                    0)),
                                    0),
                            isDisplayed()));
            textView4.check(matches(withText("ALL WORDS FOUND ALREADY")));

            // Press the Hint button again to check if the hint appears since it has been taken before
            ViewInteraction appCompatImageButton2 = onView(
                    allOf(withId(R.id.btn_hint),
                            withParent(allOf(withId(R.id.rl),
                                    withParent(withId(android.R.id.content)))),
                            isDisplayed()));
            appCompatImageButton2.perform(click());

            // Check if the Artist of Song 17 now Appears without pressing  SHOW since it has been taken before
            ViewInteraction textView5 = onView(
                    allOf(withId(R.id.hint_container), withText("REM"),

                            isDisplayed()));
            textView5.check(matches(withText("REM")));
        }
        else{
            // Here it checks if the game does not crush when there is no internet connection

            // If there was no internet in the Home Activity the Songs have not be Downloaded and so
            // the game cannot find Songs Artist to Display them
            // So if the Show button is pressed nothing should appear
            ViewInteraction appCompatButton2 = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("SHOW"), isDisplayed()));
            appCompatButton2.perform(click());

            // Check if after the Show Button is Pressed Nothing Appears
            ViewInteraction textView3 = onView(
                    allOf(withId(R.id.hint_container), withText(""),

                            isDisplayed()));
            textView3.check(matches(withText("")));

            // Close the Hint Dialog
            ViewInteraction appCompatButton4 = onView(
                    allOf(withId(R.id.btn_cancel_dialog), withText("CANCEL"), isDisplayed()));
            appCompatButton4.perform(click());

            // Check if the dialog is closed and now the Found Words Activity is displayed
            ViewInteraction textView4 = onView(
                    allOf(withId(R.id.textView2), withText("ALL WORDS FOUND ALREADY"),
                            childAtPosition(
                                    allOf(withId(R.id.rl),
                                            childAtPosition(
                                                    withId(android.R.id.content),
                                                    0)),
                                    0),
                            isDisplayed()));
            textView4.check(matches(withText("ALL WORDS FOUND ALREADY")));
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
