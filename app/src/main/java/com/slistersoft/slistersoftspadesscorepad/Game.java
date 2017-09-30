package com.slistersoft.slistersoftspadesscorepad;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by slist on 9/24/2017.
 */

public class Game implements DatabaseConstants {

    private long id, dateStarted;
    private String team1Name, team2Name;
    private boolean isGameComplete;
    private int team1Score, team2Score;

    public Game(long id, long dateStarted, String team1Name, String team2Name, boolean isGameComplete, int team1Score, int team2Score) {
        this.id = id;
        this.dateStarted = dateStarted;
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.isGameComplete = isGameComplete;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
    }

    public Game(String team1Name, String team2Name) {

        this(-1, System.currentTimeMillis(), team1Name,team2Name,false,0,0);
    }

    public Game(){

        this("","");

    }


    /**
     *
     * @return ContentValues object populated with fields from this Game object
     */
    public ContentValues getContentValues(){

        ContentValues cv = new ContentValues();

        try {
            cv.put(GAMES_COLUMN_DATESTARTED, dateStarted);
            cv.put(GAMES_COLUMN_T1NAME, team1Name);
            cv.put(GAMES_COLUMN_T2NAME, team2Name);
            cv.put(GAMES_COLUMN_ISGAMECOMPLETE, getIsGameComplete());
            cv.put(GAMES_COLUMN_T1SCORE, team1Score);
            cv.put(GAMES_COLUMN_T2SCORE, team2Score);

        } catch (Exception e) {
            e.printStackTrace();
            return cv;
        }

        return cv;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(long dateStarted) {
        this.dateStarted = dateStarted;
    }

    public String getTeam1Name() {
        return team1Name;
    }

    public void setTeam1Name(String team1Name) {
        this.team1Name = team1Name;
    }

    public String getTeam2Name() {
        return team2Name;
    }

    /**
     Converts boolean to a 1 or 0 int for entry into the Database since it doesn't support the boolean type.
     */
    public int getIsGameComplete(){

        if(isGameComplete)
            return 1;
        else
            return 0;

    }

    public void setTeam2Name(String team2Name) {
        this.team2Name = team2Name;
    }

    public boolean isGameComplete() {
        return isGameComplete;
    }

    public void setGameComplete(boolean gameComplete) {
        isGameComplete = gameComplete;
    }

    public int getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(int team1Score) {
        this.team1Score = team1Score;
    }

    public int getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(int team2Score) {
        this.team2Score = team2Score;
    }

}
