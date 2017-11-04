package edu.illinois.cs465.walkingrewardapp;

/**
 * Created by computerpp on 11/5/2016.
 * The generic list code is from https://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.illinois.cs465.walkingrewardapp.Data.Challenge;

public class ViewGoalActivity extends AppCompatActivity implements View.OnClickListener {
    private Challenge goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goal);

        goal = (Challenge) getIntent().getExtras().getSerializable("goal");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(goal.getTitle());
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }

        setTextView();

        FloatingActionButton selectButton = (FloatingActionButton) findViewById(R.id.view_goal_select);
        selectButton.setOnClickListener(this);
    }

    private void setTextView() {
        TextView title = (TextView) findViewById(R.id.view_goal_restaurant);
        title.setText(goal.getRestaurant());

        TextView description = (TextView) findViewById(R.id.view_goal_description);
        description.setText(goal.getDescription());

        TextView steps = (TextView) findViewById(R.id.view_goal_steps);
        steps.setText(goal.getStringTimeLimitMinutes() + ", " + Integer.toString(goal.getStepsRequired()) + " steps");

        ImageView imageView = (ImageView) findViewById(R.id.restaurant_icon);
        imageView.setImageResource(goal.getImage());

        if(goal.equals(Library.getCurrentGoal())) {
            FloatingActionButton stopButton = (FloatingActionButton) findViewById(R.id.view_goal_select);
            stopButton.setImageResource(R.drawable.stop);
            stopButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(getApplicationContext(), WalkingActivity.class));
                break;
            case R.id.action_change_goal:
                startActivity(new Intent(getApplicationContext(), ChooseGoalActivity.class));
                break;
            case R.id.action_my_rewards:
                startActivity(new Intent(getApplicationContext(), RewardsActivity.class));
                break;
            case R.id.action_view_statistics:
                startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startThisGoal() {
        Intent intent = new Intent(this, WalkingActivity.class);
        intent.putExtra("goal_object", goal);
        intent.putExtra("start_message", "Start your goal now");
        startActivity(intent);
        Library.setCurrentGoal(goal);
    }


    private void abandonGoal() {
        //give up
        Library.setCurrentGoal(null);

        //go back to the home page
        Intent intent = new Intent(getApplicationContext(), WalkingActivity.class);
        intent.putExtra("show_dialog", true);
        startActivity(intent);
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.view_goal_select) {
            if(goal.equals(Library.getCurrentGoal())) {
                //they want to give up.  quitter
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.walk)
                        .setTitle("Abandon Goal?")
                        .setMessage("Are you sure you want to abandon your current progress?")
                        .setPositiveButton("Abandon Goal", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                abandonGoal();
                            }

                        })
                        .setNegativeButton("Keep Going", null)
                        .show();
            }
            else if(Library.getCurrentGoal() != null) {
                //they are already in a challenge.  confirm that they want to give up.
                new AlertDialog.Builder(this)
                        .setIcon(R.drawable.walk)
                        .setTitle("Abandon Goal?")
                        .setMessage("Are you sure you want to abandon your current progress and start a new goal?")
                        .setPositiveButton("Start New", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startThisGoal();
                            }

                        })
                        .setNegativeButton("Keep Going", null)
                        .show();
            }
            else
                startThisGoal();
        }
    }
}
