package customClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.slistersoft.slistersoftspadesscorepad.R;

import databaseStuff.DatabaseConstants;
import databaseStuff.GameSaveDatabase;

/**
 * Created by slist on 10/3/2017.
 */

public class Hand implements DatabaseConstants {

    private GameSaveDatabase db;
    private CUSTOM_FUNCTIONS custFuncs;

    private Context callingContext;
    private Game parentGame;
    private long HandsID;
    private int handNum, t1Bid, t1BooksEarned, t2Bid, t2BooksEarned;
    private boolean t1Blind, t1Nil, t2Blind, t2Nil;

    //region Constructors

    public Hand(Context callingContext, Game parentGame, long handsID, int handNum, int t1Bid, int t1BooksEarned, int t2Bid, int t2BooksEarned, boolean t1Blind, boolean t1Nil, boolean t2Blind, boolean t2Nil) {

        this.callingContext = callingContext;
        this.parentGame = parentGame;
        this.db = new GameSaveDatabase(callingContext);
        this.handNum = handNum;
        HandsID = handsID;
        this.t1Bid = t1Bid;
        this.t1BooksEarned = t1BooksEarned;
        this.t2Bid = t2Bid;
        this.t2BooksEarned = t2BooksEarned;
        this.t1Blind = t1Blind;
        this.t1Nil = t1Nil;
        this.t2Blind = t2Blind;
        this.t2Nil = t2Nil;
        this.custFuncs = new CUSTOM_FUNCTIONS(callingContext);

    }

    public Hand(Context callingContext, Game parentGame){

        this(callingContext, parentGame, -1, 1, 0, 0, 0, 0, false, false, false, false);

        setCurrentHandNum(getCurrentHandNumFromDB());

    }


    //endregion

    //region Database Stuff

//    public Hand getCurrentHandFromDB(){
//
//    }

    public void insertNewHandToDB(){

        setHandsID(db.insertRecordToDB(TABLE_HANDS, getContentValues()));

    }

    public void updateHandInDB(){

        if(db.updateRecordsInDB(TABLE_HANDS, getContentValues(), HANDS_COLUMN_ID + " = " + getHandsID()) >= 1){
            custFuncs.showToast(custFuncs.getString(R.string.updateHandInDBSuccessMsg));
        }
        else{
            custFuncs.showToast(custFuncs.getString(R.string.updateHandInDBFailedMsg));
        }

    }

    private int getCurrentHandNumFromDB(){

        try {
            Cursor results = db.getCursorFromSelectQuery("SELECT " + HANDS_COLUMN_HANDNUM + " FROM " + TABLE_HANDS + " WHERE " + HANDS_COLUMN_GAMEID + " = " + parentGame.getId() + " ORDER BY " + HANDS_COLUMN_HANDNUM + " DESC LIMIT 1");

            if(results.moveToFirst()){
                return results.getInt(results.getColumnIndex(HANDS_COLUMN_HANDNUM));
            }
            else{
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }


    //endregion

    //region Getters and Setters

//    private Hand getHandFromCursor(Cursor cursorWithHandData){
//
//
//
//    }

    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE_HANDS + " (" +
                HANDS_COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                HANDS_COLUMN_GAMEID + " INTEGER NOT NULL, " +
                HANDS_COLUMN_HANDNUM + " INTEGER NOT NULL, " +
                HANDS_COLUMN_T1BID + " INTEGER NOT NULL, " +
                HANDS_COLUMN_T1ISBLIND + " NUMERIC NOT NULL, " +
                HANDS_COLUMN_T1ISNIL + " NUMERIC NOT NULL, " +
                HANDS_COLUMN_T1BOOKSEARNED + " INTEGER NOT NULL, " +
                HANDS_COLUMN_T2BID + " INTEGER NOT NULL, " +
                HANDS_COLUMN_T2ISBLIND + " NUMERIC NOT NULL, " +
                HANDS_COLUMN_T2ISNIL + " NUMERIC NOT NULL, " +
                HANDS_COLUMN_T2BOOKSEARNED + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + HANDS_COLUMN_GAMEID + ") REFERENCES " + TABLE_GAMES + "(" + GAMES_COLUMN_ID + ") ON DELETE CASCADE" +
                ");";
    }

    private ContentValues getContentValues(){

        ContentValues cv = new ContentValues();

        cv.put(HANDS_COLUMN_GAMEID, parentGame.getId());
        cv.put(HANDS_COLUMN_HANDNUM, getCurrentHandNum());
        cv.put(HANDS_COLUMN_T1BID, getT1Bid());
        cv.put(HANDS_COLUMN_T1ISBLIND, isT1Blind());
        cv.put(HANDS_COLUMN_T1ISNIL, isT1Nil());
        cv.put(HANDS_COLUMN_T1BOOKSEARNED, getT1BooksEarned());
        cv.put(HANDS_COLUMN_T2BID, getT2Bid());
        cv.put(HANDS_COLUMN_T2ISBLIND, isT2Blind());
        cv.put(HANDS_COLUMN_T2ISNIL, isT2Nil());
        cv.put(HANDS_COLUMN_T2BOOKSEARNED, getT2BooksEarned());

        return cv;

    }

    public int getCurrentHandNum() {
        return this.handNum;
    }

    public void setCurrentHandNum(int currentHandNum) {
        this.handNum = currentHandNum;
    }

    public long getHandsID() {
        return HandsID;
    }

    public void setHandsID(long handsID) {
        HandsID = handsID;
    }

    public int getHandNum() {
        return handNum;
    }

    public void setHandNum(int handNum) {
        this.handNum = handNum;
    }

    public int getT1Bid() {
        return t1Bid;
    }

    public void setT1Bid(int t1Bid) {
        this.t1Bid = t1Bid;
    }

    public int getT1BooksEarned() {
        return t1BooksEarned;
    }

    public void setT1BooksEarned(int t1BooksEarned) {
        this.t1BooksEarned = t1BooksEarned;
    }

    public int getT2Bid() {
        return t2Bid;
    }

    public void setT2Bid(int t2Bid) {
        this.t2Bid = t2Bid;
    }

    public int getT2BooksEarned() {
        return t2BooksEarned;
    }

    public void setT2BooksEarned(int t2BooksEarned) {
        this.t2BooksEarned = t2BooksEarned;
    }

    public boolean isT1Blind() {
        return t1Blind;
    }

    public void setT1Blind(boolean t1Blind) {
        this.t1Blind = t1Blind;
    }

    public boolean isT1Nil() {
        return t1Nil;
    }

    public void setT1Nil(boolean t1Nil) {
        this.t1Nil = t1Nil;
    }

    public boolean isT2Blind() {
        return t2Blind;
    }

    public void setT2Blind(boolean t2Blind) {
        this.t2Blind = t2Blind;
    }

    public boolean isT2Nil() {
        return t2Nil;
    }

    public void setT2Nil(boolean t2Nil) {
        this.t2Nil = t2Nil;
    }

    //endregion

}


