package edu.illinois.cs465.walkingrewardapp;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.illinois.cs465.walkingrewardapp.Data.Challenge;
import edu.illinois.cs465.walkingrewardapp.Data.ChallengeList;
import edu.illinois.cs465.walkingrewardapp.Data.ChallengePassedException;

/**
 * Created by computerpp on 11/8/2016.
 */

public class Library {
    private static int totalSteps = 0;
    private static int currentSteps = 0;
    private static int nRewardsEarned = 0;
    private static ChallengeList goals = null;
    private static ChallengeList rewards = null;
    private static Challenge currentGoal = null;
    private static boolean showTutorial = true;

    public static void initializeData(Context appContext) {
        if(goals != null)
            return;

        //initialize the list of goals
        goals = new ChallengeList("goals", appContext);
        //if(goals.size() == 0) {
            goals.add(new Challenge("Chipotle BOGO", "Chipotle", "Walk really far to earn your b" +
                    "uy-one-get-one-free burrito!", 12000, 24 * 60, R.drawable.chipotle, null));
            goals.add(new Challenge("Cheese!", "McDonald's", "Craving a cheeseburger?  Walk 8000 steps to " +
                    "get a free cheeseburger with any meal.", 8000, 24 * 60, R.drawable.mcdonalds, null));
            goals.add(new Challenge("Short Goal!", "McDonald's", "Craving a cheeseburger?  Walk 10 steps to " +
                    "get 10 cents off", 10, 2, R.drawable.mcdonalds, null));
            goals.add(new Challenge("Extra Panera Challenge", "Panera", "This is another challenge " +
                    "from your favorite bakery!", 24000, 24 * 60 * 2, R.drawable.panera, null));
        //}
        goals.removeDuplicates();
        goals.saveToFile();

        //initialize the rewards
        rewards = new ChallengeList("rewards", appContext);

        //TODO: we don't need to include this when launching
        //add a reward, if one's not there already
        try {
            if(rewards.size() == 0) {
                nRewardsEarned += 1;
                rewards.add(new Challenge("Free Sandwich", "Panera", "Want a free sandwich at your " +
                        "favorite coffee shop/bakery/sandwich shop?  Walk 15000 steps in one day " +
                        "and you'll earn it!", 15000, 24 * 60, R.drawable.panera,
                        new SimpleDateFormat("MM/dd/yy").parse("11/02/16")));
            }
        }
        catch(ParseException|NullPointerException ex) {
            ex.printStackTrace();
        }
        rewards.removeDuplicates();
        rewards.saveToFile();
    }

    public static void setCurrentGoal(Challenge c){
        if(c != null) c.startTimer();
        currentGoal = c;
    }

    public static Challenge getCurrentGoal(){
        return currentGoal;
    }

    public static ChallengeList getGoals() {
        return goals;
    }

    public static ChallengeList getRewards() {
        return rewards;
    }

    public static void addReward(Challenge c) {
        //try to mark it as complete
        try {
            c.markAsComplete();
        }
        catch(ChallengePassedException ex) {
            ex.printStackTrace();
            return;
        }

        rewards = getRewards();
        rewards.add(c);
        rewards.saveToFile();
    }

    public static void setTotalSteps(int i){
        totalSteps = i;
    }

    public static int getTotalSteps(){
        return totalSteps;
    }

    public static void setCurrentSteps(int i){
        currentSteps = i;
    }

    public static int getCurrentSteps(){
        return currentSteps;
    }

    public static void setnRewardsEarned(int i){ nRewardsEarned = i; }

    public static int getnRewardsEarned() { return rewards.size(); }

    public static void removeTutorial() {showTutorial = false;}
    public static boolean ShowTutorial() {return showTutorial;}
}
