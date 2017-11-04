package edu.illinois.cs465.walkingrewardapp.Data;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by mengxiongliu on 08/11/2016.
 */

public class Challenge implements Serializable {
    private String title, restaurant, description;
    private int stepsRequired, timeLimitMinutes;
    private int image;
    private Date timeStarted, timeCompleted;

    public Challenge(String title, String name, String description, int stepsRequired, int timeLimitMinutes, int image, Date timeCompleted) {
        this.title = title;
        this.restaurant = name;
        this.image = image;
        this.description = description;
        this.stepsRequired = stepsRequired;
        this.timeLimitMinutes = timeLimitMinutes;
        this.timeStarted = null;
        this.timeCompleted = timeCompleted;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public String getDescription() {
        return description;
    }

    public int getStepsRequired() {
        return stepsRequired;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public Date getTimeStarted() {
        return timeStarted;
    }

    public Date getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(Date d) {
        timeCompleted = d;
    }

    public String getCompletedTimeString(int dateFormat) {
        if(timeCompleted == null)
            return null;
        else
            return DateFormat.getDateInstance(dateFormat, Locale.getDefault()).format(timeCompleted);
    }

    public void startTimer() {
        timeStarted = new Date();
    }

    public int getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public String getStringTimeLimitMinutes() {
        return minutesToString(timeLimitMinutes);
    }

    public long timeRemainingMS(){
        if(timeStarted == null)
            return 0;
        Date curr = new Date();
        long diffInMillies = curr.getTime() - timeStarted.getTime();
        return timeLimitMinutes * 60 * 1000 - diffInMillies;
    }

    public Integer minutesRemaining() {
        if(timeStarted == null)
            return -1;
        Date curr = new Date();
        long diffInMs = new Date().getTime() - timeStarted.getTime();
        return (int)TimeUnit.MINUTES.convert(diffInMs, TimeUnit.MILLISECONDS);
    }

    public String stringTimeRemaining() {
        //get the integer number of minutes remaining
        Integer totalMinutes = minutesRemaining();
        if(totalMinutes == null)
            return null;
        return minutesToString(totalMinutes);
    }

    public void markAsComplete() throws ChallengePassedException {
        if(minutesRemaining() < 0)
            throw new ChallengePassedException();
        this.timeCompleted = new Date();
    }

    @NonNull
    private static String minutesToString(Integer totalMinutes) {
        //compute the components (minute, hour, day) for this
        int minutes = totalMinutes % 60;
        int hours = (totalMinutes / 60) % 24;
        int days = totalMinutes / 60 / 24;

        //convert this to a string
        String string = "";
        if(days > 0)
            string += Integer.toString(days) + " day" + (days > 1 ? "s" : "");
        string += " ";
        if(hours > 0)
            string += Integer.toString(hours) + " hour" + (hours > 1 ? "s" : "");
        string += " ";
        if(minutes > 0)
            string += Integer.toString(minutes) + " minute" + (minutes > 1 ? "s" : "");
        string = string.replace("  ", " ").trim();
        return string;
    }

    @Override
    public boolean equals(Object other) {
        if(other == null || other.getClass() != Challenge.class)
            return false;

        if(!((Challenge)other).title.equals(this.title))
            return false;
        if(!((Challenge)other).restaurant.equals(this.restaurant))
            return false;
        if(!((Challenge)other).description.equals(this.description))
            return false;
        if(((Challenge)other).stepsRequired != this.stepsRequired)
            return false;
        if(((Challenge)other).timeLimitMinutes != this.timeLimitMinutes)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return this.title.hashCode() +
                this.restaurant.hashCode() +
                this.description.hashCode() +
                this.stepsRequired +
                this.timeLimitMinutes;
    }
}
