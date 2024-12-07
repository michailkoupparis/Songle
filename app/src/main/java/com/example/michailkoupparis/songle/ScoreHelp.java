package com.example.michailkoupparis.songle;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

// Activity for explaining the point system
public class ScoreHelp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_explanation);

        // Get the score and Displayed it
        SharedPreferences score_previous =
                getSharedPreferences(Home.SCORE_TAG, MODE_PRIVATE);

        int score = score_previous.getInt(Home.SCORE_KEY, 0);

        TextView scoreView = (TextView) findViewById(R.id.scoreExplanationView);
        scoreView.setText(score+"");

    }

    // On back press return to the home screen
    public void onBackPressed() {
        Intent activityChangeIntent = new Intent(ScoreHelp.this, Home.class);
        startActivity(activityChangeIntent);
        finish();
    }


    // Method for setting example views next to text on the score explanation
    public void setExampleView(View view) {

        ViewDialog alert = new ViewDialog();
        alert.showDialog(ScoreHelp.this, view);
    }


    // Dialog for representing visual examples on Score Explanation screen
    private class ViewDialog {

        private void showDialog(Activity activity, final View view) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.visual_example);


            ImageView exampleView = (ImageView) dialog.findViewById(R.id.exampleView);

            switch (view.getId()) {

                // there are four different eye button each one represents a different visual explanation
                // Here it finds which one it is and sets its corresponding image
                case R.id.difficultyView:
                    exampleView.setImageResource(R.drawable.diff_example);
                    break;
                case R.id.modeView:
                    exampleView.setImageResource(R.drawable.mode_example);
                    break;
                case R.id.hintView:
                    exampleView.setImageResource(R.drawable.hint_example);
                 break;
                case R.id.give_solutionView:
                    exampleView.setImageResource(R.drawable.get_solution_example);
                   break;

            }
            Button ok_button = (Button) dialog.findViewById(R.id.btn_ok_dialog);
            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                }

            });

            dialog.show();
        }
    }

}
