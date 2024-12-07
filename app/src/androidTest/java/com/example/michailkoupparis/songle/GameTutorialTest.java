package com.example.michailkoupparis.songle;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/* Test for the game's tutorial
 This test checks if the game's tutorial open when it is the first time the game is reached
 Also it checks if the game's tutorial is opened when the help button at the home screen is clicked
 Moreover it checks the navigation in the tutorial and the visual examples */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class GameTutorialTest {


    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<WelcomeScreen>(WelcomeScreen.class);
    public ActivityTestRule<Home> mActivityTestHome = new ActivityTestRule<Home>(Home.class);


    @Test
    public void tutorialTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html



        try {
            Thread.sleep(3000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Find if there is wifi connection when the home screen is reached
        // If there is not an internet connection a dialog will pop up first and it has to be closed to check the tutorial
        // if it is not the first time the game is reached

        Boolean wifi = Home.wifi;

        // Check if it is the first time the game is reached
        // If yes then the tutorial will open automatically else the help button is pressed

        Boolean first_time = Home.first_time;

        // If it is the first time then the tutorial will open up automatically
        if (first_time) {
                // Check if the first screen of the tutorial is open (Tutorial Dialog)
                ViewInteraction tutorialFirstScreen = onView(
                        allOf(childAtPosition(
                                allOf(withId(R.id.parent),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                                isDisplayed()));
                tutorialFirstScreen.check(matches(isDisplayed()));

                // Go to next screen in the tutorial (HomeTutorial)
                ViewInteraction nextDialog = onView(
                        allOf(withId(R.id.next_dialog), isDisplayed()));
                nextDialog.perform(click());

                // Check if the HomeTutorial is now displayed
                ViewInteraction home_tutorialScreen = onView(
                        allOf(childAtPosition(
                                allOf(withId(R.id.parent),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                                isDisplayed()));
                home_tutorialScreen.check(matches(isDisplayed()));

                // Press the eye button to open a visual example
                ViewInteraction open_visual_example = onView(
                        allOf(withId(R.id.buttons_view), isDisplayed()));
                open_visual_example.perform(click());

                // Check if the Visual Example is displayed
                ViewInteraction visualExample = onView(
                        allOf(childAtPosition(
                                allOf(withId(android.R.id.content),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                                0)),
                                0),
                                isDisplayed()));
                visualExample.check(matches(isDisplayed()));

                // Press Ok in the Visual Example and so close the Dialog
                ViewInteraction close_visual_example = onView(
                        allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
                close_visual_example.perform(click());

                // Check if the Dialog is closed ans the HomeTutorial Screen is now been displayed
                ViewInteraction home_tutorialScreen2 = onView(
                        allOf(childAtPosition(
                                allOf(withId(R.id.parent),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                                isDisplayed()));
                home_tutorialScreen2.check(matches(isDisplayed()));

                // Press cancel, and so close the tutorial and return to the home screen (Home)
                ViewInteraction closeTutorial = onView(
                        allOf(withId(R.id.cancel_dialog), withText("CANCEL"), isDisplayed()));
                closeTutorial.perform(click());

        }
        // Else press the help button to open the tutorial
        else{
            if(!wifi) {
                // If there is not wifi connection close the warning to be able to start the tutorial
                ViewInteraction close_wifi_warning = onView(
                        allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
                close_wifi_warning.perform(click());
            }

            ViewInteraction open_tutorial = onView(
                    allOf(withId(R.id.help_btn), isDisplayed()));
            open_tutorial.perform(click());

            // Check if the first screen of the tutorial is open (Tutorial Dialog)
            ViewInteraction tutorial_firstScreen = onView(
                    allOf(childAtPosition(
                            allOf(withId(R.id.parent),
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0)),
                            0),
                            isDisplayed()));
            tutorial_firstScreen.check(matches(isDisplayed()));

            // Go to next screen in the tutorial (HomeTutorial)
            ViewInteraction goToNextDialog = onView(
                    allOf(withId(R.id.next_dialog), isDisplayed()));
            goToNextDialog.perform(click());

            // Check if the HomeTutorial is now displayed
            ViewInteraction home_tutorial_screen = onView(
                    allOf(withId(R.id.parent),
                            childAtPosition(
                                    allOf(withId(android.R.id.content),
                                            childAtPosition(
                                                    withId(R.id.action_bar_root),
                                                    0)),
                                    0),
                            isDisplayed()));
            home_tutorial_screen.check(matches(isDisplayed()));

            // Go to next screen in the tutorial (MapTutorial)
            ViewInteraction goToNextDialog2 = onView(
                    allOf(withId(R.id.next_dialog), isDisplayed()));
            goToNextDialog2.perform(click());

            // Check if the MapTutorial is now displayed
            ViewInteraction map_tutorial = onView(
                    allOf(withId(R.id.parent),
                            childAtPosition(
                                    allOf(withId(android.R.id.content),
                                            childAtPosition(
                                                    withId(R.id.action_bar_root),
                                                    0)),
                                    0),
                            isDisplayed()));
            map_tutorial.check(matches(isDisplayed()));

            // Press the eye button to open a visual example
            ViewInteraction open_visual_example = onView(
                    allOf(withId(R.id.buttons_view), isDisplayed()));
            open_visual_example.perform(click());

            // Check if the Visual Example is displayed
            ViewInteraction visual_example = onView(
                    allOf(childAtPosition(
                            allOf(withId(android.R.id.content),
                                    childAtPosition(
                                            IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                            0)),
                            0),
                            isDisplayed()));
            visual_example.check(matches(isDisplayed()));

            //Press the ok and close the dialog and return to MapTutorial
            ViewInteraction close_tutorial = onView(
                    allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
            close_tutorial.perform(click());

            // Press cancel, close the tutorial and return to the Home Screen
            ViewInteraction close_dialog = onView(
                    allOf(withId(R.id.cancel_dialog), withText("CANCEL"), isDisplayed()));
            close_dialog.perform(click());

            if(!wifi) {
                // if there is no wifi close the warning
                ViewInteraction close_wifi_warning = onView(
                        allOf(withId(R.id.btn_ok_dialog), withText("OK"), isDisplayed()));
                close_wifi_warning.perform(click());
            }

            // Check if the game returned to the Home Screen by checking the screen's title
            // Only the Home Screen has SONGLE as its title
            ViewInteraction textView = onView(
                    allOf(withId(R.id.home_title), withText("SONGLE"),
                            childAtPosition(
                                    childAtPosition(
                                            withId(android.R.id.content),
                                            0),
                                    0),
                            isDisplayed()));
            textView.check(matches(withText("SONGLE")));
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
