package edu.illinois.cs465.walkingrewardapp.Data;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by computerpp on 11/16/2016.
 */

public class ChallengeList implements Serializable {

    private ArrayList<Challenge> challenges;
    private String fileName;
    private Context appContext;

    public ChallengeList(String fileName, Context appContext) {
        this.challenges = new ArrayList<Challenge>();
        this.fileName = fileName;
        this.appContext = appContext;

        //if the file doesn't exist, just return an empty list
        File file = appContext.getFileStreamPath(fileName);
        if(!file.exists())
            return;

        //otherwise, try to read the file
        try {
            FileInputStream fis = appContext.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            this.challenges = (ArrayList<Challenge>) is.readObject();
            is.close();
            fis.close();
        }
        catch(IOException|ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void saveToFile() {
        try {
            FileOutputStream fos = appContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(challenges);
            os.close();
            fos.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<Challenge> getChallengeList() {
        return challenges;
    }

    public Challenge get(int index) {
        return challenges.get(index);
    }

    public int size() {
        return challenges.size();
    }

    public void add(Challenge item) {
        challenges.add(item);
    }

    public void removeDuplicates() {
        Set<Challenge> hs = new HashSet<>();
        hs.addAll(challenges);
        challenges.clear();
        challenges.addAll(hs);
    }
}
