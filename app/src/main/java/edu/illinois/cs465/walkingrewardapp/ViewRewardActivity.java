package edu.illinois.cs465.walkingrewardapp;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import edu.illinois.cs465.walkingrewardapp.Data.Challenge;

/**
 * Created by mengxiongliu on 12/11/2016.
 */

public class ViewRewardActivity extends AppCompatActivity {
    private Button cancelButton;
    private Challenge reward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reward);

        reward = (Challenge) getIntent().getExtras().getSerializable("reward");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(reward.getTitle());
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }

        // replace text in text view with information in the reward object
        setTextView();
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

    private void setTextView() {
        View header = findViewById(R.id.reward_challenge_heading);

        TextView title = (TextView) header.findViewById(R.id.view_goal_restaurant);
        title.setText(reward.getRestaurant());

        TextView description = (TextView) header.findViewById(R.id.view_goal_description);
        description.setText(reward.getDescription());

        ImageView icon = (ImageView) header.findViewById(R.id.restaurant_icon);
        icon.setImageResource(reward.getImage());

        TextView completedDate = (TextView) findViewById(R.id.completed_date);
        completedDate.setText("Completed on " + reward.getCompletedTimeString(DateFormat.FULL));


    }
}
