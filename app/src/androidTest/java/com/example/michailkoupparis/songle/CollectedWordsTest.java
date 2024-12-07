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
import android.widget.ListView;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
/* Test for the list containing the Collected Words.
* This list is contain in the Found Words Activity.
* So first change the number of song and difficulty to know what song and difficulty are currently used.
* Then it changes the change preference to false which implies that the previous song with the same difficulty is in use.
* And then puts some markers to found markers from which the found words are found.
* Then it goes to Found Words and checks if the list hold those and only those words.
* After that it test if the list is empty when a song or difficulty are changed.
* This is indicated by the change preference.
* So the test return home change the preference to true and calls the Found Words Activity again.
* Then it checks if the list is empty.
* Also it test if no markers are displayed on Found Words if there is no wifi since the lyrics cannot be downloaded.
*/
@LargeTest
@RunWith(AndroidJUnit4.class)
public class CollectedWordsTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void collectedWordsTest() {
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

        Boolean internet = true;
        // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
        if(!checkWifi()){
            close_check_WifiDialog();
            internet =false;
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

        // Update the Change preference so as the game knows this is not a new song or a change difficulty
        SharedPreferences.Editor editor_change =
                activity_home.getSharedPreferences(Home.CHANGE_TAG, Context.MODE_PRIVATE).edit();
        editor_change.putBoolean(Home.CHANGE_KEY, false);
        editor_change.apply();
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

        // Go to Found Words Activity
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.words_found), withText("FOUND WORDS"), isDisplayed()));
        appCompatButton.perform(click());

        // Check if Found Words Activity is reached
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

        // Get the current activity which is the Home activity
        Activity activity_found_words = getCurrentActivity();
        FoundWords foundwords = (FoundWords)  activity_found_words;

        // Get the List holding found words
        ListView wordCollector = foundwords.collectedWordsContainer();

        // If there is no internet the Song's Lyrics will not be downloaded and so the list will be empty
        if (internet){
            // Get all Found Songs that appear in the List View
            List<String> found_words = new ArrayList<>();
            for (int i=0; i<wordCollector.getCount(); i++){

                TextView child = (TextView) wordCollector.getChildAt(i);
                found_words.add((String) child.getText());
            }
            // Check if Collected Words appear in the ListView
            assertTrue("55:1",found_words.contains("Oh"));
            assertTrue("21:7",found_words.contains("time"));
            assertTrue("1:2",found_words.contains("this"));
            assertTrue("30:2",found_words.contains("ooh"));
            assertTrue("15:2",found_words.contains("my"));

            // And check if there are only those
            assertTrue("Only those",found_words.size() == wordCollector.getCount());

        }
        else{
            // Here it checks if the game does not crush when there is no internet connection

            //No wifi then Songs are not Downloaded and the List View should be empty
            assertTrue("No wifi List View should be empty",wordCollector.getCount()==0);
        }

        // Go back to Home to check if difficulty or song are changed and the Found Words is reached the List shoud be empty
        pressBack();

        // If there is no wifi close the Wifi Warning and check if it appears at the Home Screen
        if(!checkWifi()){
            close_check_WifiDialog();
        }

        // Get the current activity which is the Home activity
       activity_home = getCurrentActivity();

        // Update the change preference
        editor_change.putBoolean(Home.CHANGE_KEY, true);
        editor_change.apply();

        // Go to Found Words Activity
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.words_found), withText("FOUND WORDS"), isDisplayed()));
        appCompatButton2.perform(click());

        // Check if Found Words Activity is reached
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textView2), withText("ALL WORDS FOUND ALREADY"),
                        childAtPosition(
                                allOf(withId(R.id.rl),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        textView2.check(matches(withText("ALL WORDS FOUND ALREADY")));

        // Get the current activity which is the Home activity
        activity_found_words = getCurrentActivity();

        // Get the List holding found words
        wordCollector = activity_found_words.findViewById(R.id.words_container);

        //Check if the List is empty
        assertTrue("No wifi List View should be empty",wordCollector.getCount()==0);
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
