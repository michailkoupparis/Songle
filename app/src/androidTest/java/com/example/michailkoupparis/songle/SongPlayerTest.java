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
import android.widget.ListView;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.allOf;
/* Test for Song Player Activity .
* The Song Player is reach by pressing the found song playilist in the Song Selection Activity.
*  The test checks first if the Song Player Activity is reach properly.
 *  Then if there was wifi it adds to songs to found songs and check if they are appear.
 *  Else if there was not internet connection in the Home Activity the Songs did not downloaded
 *  and so the listView holding the found Songs should be empty, the test check this as well.
 *  The test does not check if the youtube is opened because some devices may ask the user if he wants
 *  to open the link in youtube or a browser and others open youtube directly if available.
 *  The option of browser is available so when there is no youtube app the game does not fail.
*/
@LargeTest
@RunWith(AndroidJUnit4.class)
public class SongPlayerTest {

    @Rule
    public ActivityTestRule<WelcomeScreen> mActivityTestRule = new ActivityTestRule<>(WelcomeScreen.class);

    @Test
    public void songPlayerTest() {
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

        // Go to the Song Selection Activity
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.go_to_song_selection), withText("SELECT SONG"), isDisplayed()));
        appCompatButton.perform(click());

        // Songs are added before the Song Player is reached because the ListView is initialise when the activity is reached
        // Add songs to found Songs and Check later if they appear in the List View
        WelcomeScreen.saveFile(WelcomeScreen.WELCOME_CONTEXT,"Song 2");
        WelcomeScreen.saveFile(WelcomeScreen.WELCOME_CONTEXT,"Ironic");

        System.out.println("854734720783495434378583979778788879098988889998");
        // Go to the Song Player Activity
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.go_to_song_player), withText("Found Songs Playlist"), isDisplayed()));
        appCompatButton2.perform(click());

        // Check if the test is now to the Song Player Activity
        ViewInteraction textView = onView(
                allOf(withId(R.id.song_selection_title), withText("SONG PLAYER"),

                        isDisplayed()));
        textView.check(matches(withText("SONG PLAYER")));

        // Get current Activity which is the Song Player Activity
        Activity activity_song_player = getCurrentActivity();
        // Get the List View which holds the found songs
        ListView found_songs_container = activity_song_player.findViewById(R.id.found_songs_container);

        if (wifi){

            // Get all songs appearing in the Song PLayer list view
            SongPlayer songplayer = (SongPlayer) activity_song_player;
            List<String> found_songs = songplayer.listElements();

            // Check if Added Songs appear in the ListView
            assertTrue("Song 2 appears",found_songs.contains("02:  Song 2"));
            assertTrue("Ironic appears",found_songs.contains("23:  Ironic"));

        }
        else{
            // Here it checks if the game does not crush when there is no internet connection

            //No wifi then Songs are not Downloaded and the List View should be empty
            assertTrue("No wifi List View should be empty",found_songs_container.getCount()==0);
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
