package edu.illinois.cs465.walkingrewardapp;

/**
 * Created by computerpp on 11/5/2016.
 * The generic list code is from https://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Comparator;
import java.util.List;

import java.io.Serializable;

import edu.illinois.cs465.walkingrewardapp.Data.Challenge;
import edu.illinois.cs465.walkingrewardapp.Data.ChallengeList;

public class ChooseGoalActivity extends AppCompatActivity {
    protected void openActivity(Class<?> activity, Serializable parameter) {
        Intent intent = new Intent(this, activity);
        intent.putExtra("data", parameter);
        startActivity(intent);
    }

    private ChallengeList goals;
    private CustomList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_goal);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(NullPointerException ex) {
            ex.printStackTrace();
        }

        goals = Library.getGoals();

        adapter = new CustomList(this, goals);
        ListView list = (ListView)findViewById(R.id.goal_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(ChooseGoalActivity.this, ViewGoalActivity.class);
                intent.putExtra("goal", goals.get(position));
                startActivity(intent);
            }
        });
    }

    protected void openActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    //code from http://www.vogella.com/tutorials/AndroidActionBar/article.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        inflater.inflate(R.menu.menu_sort_goal, menu);

        return true;
    }

    private void sortByRestaurant() {
        adapter.sort(new Comparator<Challenge>() {
            @Override
            public int compare(Challenge lhs, Challenge rhs) {
                return lhs.getRestaurant().compareTo(rhs.getRestaurant());
            }
        });
    }

    private void sortBySteps() {
        adapter.sort(new Comparator<Challenge>() {
            @Override
            public int compare(Challenge lhs, Challenge rhs) {
                return -(lhs.getStepsRequired() - rhs.getStepsRequired());
            }
        });
    }

    //code is from https://developer.android.com/training/implementing-navigation/ancestral.html
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_home:
                openActivity(WalkingActivity.class);
                break;
            case R.id.action_change_goal:
                openActivity(ChooseGoalActivity.class);
                break;
            case R.id.action_my_rewards:
                openActivity(RewardsActivity.class);
                break;
            case R.id.action_view_statistics:
                openActivity(StatisticsActivity.class);
                break;
            case R.id.menu_sort_restaurant:
                sortByRestaurant();
                break;
            case R.id.menu_sort_steps:
                sortBySteps();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
