package edu.illinois.cs465.walkingrewardapp;

/**
 * Created by computerpp on 11/5/2016.
 * Code is from https://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
 */

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import edu.illinois.cs465.walkingrewardapp.Data.Challenge;
import edu.illinois.cs465.walkingrewardapp.Data.ChallengeList;

public class CustomList extends ArrayAdapter<Challenge>{

    private final Activity context;
    private final ChallengeList challenges;

    public CustomList(Activity context, ChallengeList challenges) {
        super(context, R.layout.list_item_image_text, challenges.getChallengeList());
        this.context = context;
        this.challenges = challenges;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Challenge thisChallenge = challenges.get(position);

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_item_image_text, null, true);

        TextView txtRestaurant = (TextView) rowView.findViewById(R.id.restaurant_name);
        txtRestaurant.setText(thisChallenge.getRestaurant());

        TextView txtReward = (TextView) rowView.findViewById(R.id.rewards);
        txtReward.setText(thisChallenge.getDescription());

        TextView txtGoal = (TextView) rowView.findViewById(R.id.goals);
        if(thisChallenge.getTimeCompleted() == null) {
            //the challenge is not completed.  show the number of steps required
            txtGoal.setText(thisChallenge.getStringTimeLimitMinutes() + ", " + Integer.toString(thisChallenge.getStepsRequired()) + " steps");
        }
        else {
            //the challenge is completed.  show the date/time it was completed
            txtGoal.setText("Completed " + thisChallenge.getCompletedTimeString(DateFormat.SHORT));
        }

        ImageView imageView = (ImageView) rowView.findViewById(R.id.restaurant_icon);
        imageView.setImageResource(thisChallenge.getImage());

        if(thisChallenge.equals(Library.getCurrentGoal()))
            rowView.setBackgroundColor(Color.argb(30, 64, 64, 255));

        return rowView;
    }
}