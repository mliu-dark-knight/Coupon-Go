package edu.illinois.cs465.walkingrewardapp.Data;

/**
 * Created by computerpp on 11/16/2016.
 */

public class ChallengePassedException extends Exception {
    public ChallengePassedException() {
        super("This challenge is no longer able to be completed because it's deadline has passed.");
    }
}
