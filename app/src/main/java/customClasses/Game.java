package customClasses;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.slistersoft.slistersoftspadesscorepad.R;

import java.util.ArrayList;

import activityClasses.ScorePad;
import databaseStuff.DatabaseConstants;
import databaseStuff.GameSaveDatabase;

/**
 * Created by slist on 9/24/2017.
 */

public class Game implements DatabaseConstants {

    //region Fields
    private Context callingContext;
    private long id, dateStarted;
    private String team1Name, team2Name;
    private boolean isGameComplete;
    private int team1Score, team2Score;
    private CUSTOM_FUNCTIONS custFuncs;
    private GameSaveDatabase gameDB;
    public static String INTENT_KEY_GAMEID = "passedGameID";
    //endregion

    //region Constructors
    public Game(Context callingContext, long id, long dateStarted, String team1Name, String team2Name, boolean isGameComplete, int team1Score, int team2Score) {
        this.callingContext = callingContext;
        custFuncs = new CUSTOM_FUNCTIONS(callingContext);
        gameDB = new GameSaveDatabase(callingContext);
        this.id = id;
        this.dateStarted = dateStarted;
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.isGameComplete = isGameComplete;
        this.team1Score = team1Score;
        this.team2Score = team2Score;
    }

    public Game(Context callingContext, String team1Name, String team2Name) {

        this(callingContext, -1, System.currentTimeMillis(), team1Name,team2Name,false,0,0);
    }

    public Game(Context callingContext){

        this(callingContext, "","");

    }

    //endregion

    //region Database Functions

    public void insertNewGameToDB(){

        try {
            ContentValues values = getContentValues();

            setId(gameDB.insertRecordToDB(TABLE_GAMES, values));

            custFuncs.showToast(callingContext.getString(R.string.addGameToDBSuccessMessage), 5000);

        } catch (Exception e) {
            e.printStackTrace();
            custFuncs.showToast(callingContext.getString(R.string.addGameToDBFailMsg), 5000);
        }
    }

    public void saveChangesToDB(){

        try {

            custFuncs.showToast("Updated " + gameDB.updateRecordsInDB(TABLE_GAMES, getContentValues(), GAMES_COLUMN_ID + " = " + getId()) + " rows");
        } catch (Exception e) {
            custFuncs.showToast(callingContext.getString(R.string.updateGameDBErrorMsg));
            e.printStackTrace();
        }

    }

    /**
     * Loads data from Game table and populates game object with all data from that row.
     * @param gameID Game ID to get
     * @return
     */
    public Game getGameFromDB(long gameID){

        Game gameToReturn = null;

        try {

            String query = "SELECT * FROM " + TABLE_GAMES + " WHERE " + GAMES_COLUMN_ID +" = " + gameID;

            Cursor cursor = gameDB.getCursorFromSelectQuery(query);

            if(cursor.moveToFirst()){
                gameToReturn = getGameFromCursor(cursor);
            }
            else{
                custFuncs.showToast("Game ID: " + gameID + " not found in Database", 5000);
            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
            return new Game(callingContext);
        }

        return gameToReturn;

    }

    /**
     *
     * @param gameCursor Cursor object containing a row of data from the Game table
     * @return Returns a Game object populated with data from the cursor.
     */
    private Game getGameFromCursor(Cursor gameCursor){

        return getGameFromCursor(gameCursor, 0);

    }

    /**
     * Builds game object from data in cursor.
     * @param gameCursor Cursor containing ALL COLUMNS in the Games table.
     * @param curserPositionToGet Index in cursor you want to grab from. Use this if the cursor contains multiple rows.
     * @return Game object from specified cursor index.
     */
    private Game getGameFromCursor(Cursor gameCursor, int curserPositionToGet){

        Game game;

        try {

            String t1Name, t2Name;
            int t1Score, t2Score;
            long id, dateStarted;
            boolean gameComplete;

            if (curserPositionToGet <= 0)
                gameCursor.moveToFirst();
            else
                gameCursor.moveToPosition(curserPositionToGet);

            if(gameCursor.getInt(gameCursor.getColumnIndex(GAMES_COLUMN_ISGAMECOMPLETE)) == 1)
                gameComplete = true;
            else
                gameComplete = false;

            id = gameCursor.getLong(gameCursor.getColumnIndex(GAMES_COLUMN_ID));
            dateStarted = gameCursor.getLong(gameCursor.getColumnIndex(GAMES_COLUMN_DATESTARTED));
            t1Name = gameCursor.getString(gameCursor.getColumnIndex(GAMES_COLUMN_T1NAME));
            t2Name = gameCursor.getString(gameCursor.getColumnIndex(GAMES_COLUMN_T2NAME));
            t1Score = gameCursor.getInt(gameCursor.getColumnIndex(GAMES_COLUMN_T1SCORE));
            t2Score = gameCursor.getInt(gameCursor.getColumnIndex(GAMES_COLUMN_T2SCORE));

            game = new Game(callingContext, id, dateStarted, t1Name, t2Name, gameComplete, t1Score, t2Score);

        } catch (Exception e) {
            e.printStackTrace();
            return new Game(callingContext, "Error", "Error");
        }

        return game;

    }

    //endregion

    //region Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static String getCreateTableQuery(){

        String createGamesTableStatement = "CREATE TABLE " + TABLE_GAMES + "("
                + GAMES_COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, "
                + GAMES_COLUMN_T1NAME + " TEXT, "
                + GAMES_COLUMN_T2NAME + " TEXT, "
                + GAMES_COLUMN_T1SCORE + " NUMERIC, "
                + GAMES_COLUMN_T2SCORE + " NUMERIC, "
                + GAMES_COLUMN_ISGAMECOMPLETE + " NUMERIC, "
                + GAMES_COLUMN_DATESTARTED + " INTEGER"
                + ")";

        return createGamesTableStatement;

    }


    /**
     *
     * @return ContentValues object populated with fields from this Game object
     */
    protected ContentValues getContentValues(){

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

    /**
     *
     * @return Gets date started long numeric value from DB
     */
    public long getDateStarted() {
        return dateStarted;
    }

    /**
     * Converts dateStarted into a human friendly date string
     * @return Human friendly date and time string
     */
    public String getHumanDateStartedString(){

        return GameSaveDatabase.getHumanFriendlyDateStringFromEPOCH(getDateStarted());

    }

    public ArrayList<Game> getInCompleteGames(){

        ArrayList<Game> games = new ArrayList<>();

        Cursor gamesCursor = gameDB.getCursorFromSelectQuery("SELECT * FROM " + TABLE_GAMES + " WHERE " + GAMES_COLUMN_ISGAMECOMPLETE + " = 0 ORDER BY " + GAMES_COLUMN_DATESTARTED + " DESC");

        try {
            //Cursor starts before first result so first run of loop will start at first item
            while(gamesCursor.moveToNext()){

                Game game = getGameFromCursor(gamesCursor, gamesCursor.getPosition());
                games.add(game);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return games;

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

    //endregion

    public void launchGame(){

        try {
            Intent i = new Intent(callingContext, ScorePad.class);
            i.putExtra(INTENT_KEY_GAMEID, getId());
            callingContext.startActivity(i);
        } catch (Exception e) {
            custFuncs.MsgBox("Error launching game ID: " + getId());
            e.printStackTrace();
        }


    }
}
