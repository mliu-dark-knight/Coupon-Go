package edu.illinois.cs465.walkingrewardapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import java.util.GregorianCalendar;
import java.util.Calendar;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by mengxiongliu on 05/11/2016.
 */

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton chooseDateButton;
    private DatePickerFragment dialogFragment;
    private TableLayout statisticsTable;

    private HashMap<String, String> staticStatistics = new HashMap<>();

    protected void openActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_statistics);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(NullPointerException ex) {
            ex.printStackTrace();
        }

        chooseDateButton = (FloatingActionButton) findViewById(R.id.choose_date_button);
        chooseDateButton.setOnClickListener(this);

        statisticsTable = (TableLayout) findViewById(R.id.statistics_table);
        initStatistics();
        initGraph();

    }

    private void initGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date d3 = calendar.getTime();

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(d3, 1000),
                new DataPoint(d2, 5000),
                new DataPoint(d1, 3000),
        });
        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);

        graph.getGridLabelRenderer().setHorizontalAxisTitle("date");
        graph.getGridLabelRenderer().setVerticalAxisTitle("steps");

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setHumanRounding(false);
    }


    private void initStatistics() {
        staticStatistics.put("Traveled", Integer.toString(Library.getTotalSteps()) + " steps");
        staticStatistics.put("Earned", Integer.toString(Library.getnRewardsEarned()));
        staticStatistics.put("Used", "1");
        staticStatistics.put("Saved", "$2.24");
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private StatisticsActivity activity;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            GregorianCalendar c = new GregorianCalendar();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DATE);

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void setActivity(StatisticsActivity activity) {
            this.activity = activity;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            activity.onDateSelected(year, month, day);
        }
    }

    public void showDatePickerDialog(View v) {
        dialogFragment = new DatePickerFragment();
        dialogFragment.setActivity(this);
        dialogFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.choose_date_button)
            showDatePickerDialog(v);
    }

    public void onDateSelected(int year, int month, int day) {
        dialogFragment.dismiss();
        Toast.makeText(this, "Select date success", Toast.LENGTH_SHORT).show();

        TextView traveled = (TextView) findViewById(R.id.traveled);
        traveled.setText(staticStatistics.get("Traveled"));
        TextView earned = (TextView) findViewById(R.id.earned);
        earned.setText(staticStatistics.get("Earned"));
        TextView used = (TextView) findViewById(R.id.used);
        used.setText(staticStatistics.get("Used"));
        TextView saved = (TextView) findViewById(R.id.saved);
        saved.setText(staticStatistics.get("Saved"));
    }

    //code from http://www.vogella.com/tutorials/AndroidActionBar/article.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        Library.initializeData(getApplicationContext());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                openActivity(WalkingActivity.class);
                break;
            case R.id.action_change_goal:
                openActivity(ChooseGoalActivity.class);
                break;
            case R.id.action_my_rewards:
                //Toast.makeText(getApplicationContext(), "Thanks for clicking the Rewards button!", Toast.LENGTH_SHORT).show();
                openActivity(RewardsActivity.class);
                break;
            case R.id.action_view_statistics:
                openActivity(StatisticsActivity.class);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}