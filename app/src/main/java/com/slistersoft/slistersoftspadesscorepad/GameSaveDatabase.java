package com.slistersoft.slistersoftspadesscorepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by slist on 9/24/2017.
 */

public class GameSaveDatabase extends SQLiteOpenHelper implements DatabaseConstants {

    Context callingContext;
    CUSTOM_FUNCTIONS custFuncs;

    //region Constructors

    public GameSaveDatabase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        callingContext = context;
        custFuncs = new CUSTOM_FUNCTIONS(context);

    }

    //endregion

    //region Overrides and Table Creation
    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.execSQL(createGamesTableStatement);
        } catch (SQLException e) {
            custFuncs.showToast(callingContext.getString(R.string.onCreateErrorMsg));
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        try {
            db.execSQL(getDropTableStatement(TABLE_GAMES));
            onCreate(db);
        } catch (SQLException e) {
            custFuncs.showToast(callingContext.getString(R.string.onUpgradeErrorMsg));
            e.printStackTrace();
        }

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    private String getDropTableStatement(String tableName){

        return "DROP TABLE IF EXISTS " + tableName;

    }
    //endregion

    //region Database Functions
    
    /**
     * Converts EPOCH time into human readable date and time format.
     * @param EPOCHDateValue The long numeric value stored in the DB that represents the number of milliseconds since Jan 1, 1970 called the EPOCH or Unix time.
     * @param SimpleDateFormatPattern Date format pattern to return. Most follow official SimpleDateFormat format pattern.
     * @return Human readable date string in specified format.
     */
    private String getHumanFriendlyDateStringFromEPOCH(long EPOCHDateValue, String SimpleDateFormatPattern){

        String humanDateString;

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(EPOCHDateValue);

            SimpleDateFormat sdf = new SimpleDateFormat(SimpleDateFormatPattern);

            humanDateString = sdf.format(cal.getTime());

        } catch (Exception e) {
            humanDateString = "Error converting date to human format.";
            e.printStackTrace();
        }

        return humanDateString;

    }

    /**
     *Converts EPOCH time into human readable date and time format.
     * @param EPOCHDateValue The long numeric value stored in the DB that represents the number of milliseconds since Jan 1, 1970 called the EPOCH or Unix time.
     * @return Human readable date string in format MM-dd-yyyy 'at' hh:mm a z
     */
    public String getHumanFriendlyDateStringFromEPOCH(long EPOCHDateValue){

       return getHumanFriendlyDateStringFromEPOCH(EPOCHDateValue, "MM-dd-yyyy 'at' hh:mm a z");

    }



    /**
     *
     * @param SQLSelectQuery SQLLite SELECT Query to run on Database.
     * @return Cursor object with query results.
     */
    public Cursor getCursorFromSelectQuery(String SQLSelectQuery){

        SQLiteDatabase db;
        Cursor cursor = null;

        try {
            db = getReadableDatabase();
            cursor = db.rawQuery(SQLSelectQuery, null);
        } catch (Exception e) {
            custFuncs.showToast("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return cursor;

    }

    //endregion

    //region Game Table Functions
    public Game addGameToDB(Game game){

        try {
            ContentValues values = game.getContentValues();

            SQLiteDatabase db = getWritableDatabase();
            game.setId(db.insert(TABLE_GAMES, null, values));

            custFuncs.showToast(callingContext.getString(R.string.addGameToDBSuccessMessage), 5000);

            return game;

        } catch (Exception e) {
            e.printStackTrace();
            custFuncs.showToast(callingContext.getString(R.string.addGameToDBFailMsg), 5000);
            return game;
        }
    }

    public void updateGameInDB(Game updatedGame){

        try {
            SQLiteDatabase db = getWritableDatabase();

            custFuncs.MsgBox("Updated " + db.update(TABLE_GAMES, updatedGame.getContentValues(), GAMES_COLUMN_ID + " = " + updatedGame.getId() , null) + " rows");
        } catch (Exception e) {
            custFuncs.showToast(callingContext.getString(R.string.updateGameDBErrorMsg));
            e.printStackTrace();
        }

    }

    /**
     * Gets cursor with all of the incomplete games
     * @param sortResultsDescending True will return results in descending order by start date. False will return the oldest game(s) on top.
     * @return Cursor object with all of the rows with incomplete games
     */
    private Cursor getIncompleteGames(boolean sortResultsDescending){

        Cursor queryResults = null;
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("SELECT * FROM " + TABLE_GAMES + " WHERE " + GAMES_COLUMN_ISGAMECOMPLETE + " = 0 ");
            sb.append("ORDER BY " + GAMES_COLUMN_DATESTARTED + " ");

            if(sortResultsDescending)
                sb.append("DESC");
            else
                sb.append("ASC");

            queryResults = getCursorFromSelectQuery(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return queryResults;

    }

    /**
     * Checks if there are any incomplete saved games.
     * @return True means there are incomplete saved games. False means there are not any or the data couldn't be retrieved.
     */
    public boolean areThereIncompleteGamesInDB(){

        Cursor inCompleteGames = getIncompleteGames(true);

        if(inCompleteGames == null || inCompleteGames.getCount() <= 0)
            return false;
        else
            return true;

    }

    /**
     * Loads data from Game table and populates game object with all data from that row.
     * @param gameID
     * @return
     */
    public Game getGameFromDB(long gameID){

        Game gameToReturn = null;

        try {

            String query = "SELECT * FROM " + TABLE_GAMES + " WHERE ID = " + gameID;

            Cursor cursor = getCursorFromSelectQuery(query);

            if(cursor.moveToFirst()){
                gameToReturn = getGameFromCursor(cursor);
            }
            else{
                custFuncs.showToast("Game ID: " + gameID + " not found in Database", 5000);
            }

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
            return new Game();
        }

        return gameToReturn;

    }

    /**
     *
     * @param gameCursor Cursor object containing a row of data from the Game table
     * @return Returns a Game object populated with data from the cursor.
     */
    private Game getGameFromCursor(Cursor gameCursor){

        Game game;

        try {

            String t1Name, t2Name;
            int t1Score, t2Score;
            long id, dateStarted;
            boolean gameComplete;

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

            game = new Game(id, dateStarted, t1Name, t2Name, gameComplete, t1Score, t2Score);

        } catch (Exception e) {
            e.printStackTrace();
            return new Game("Error", "Error");
        }

        return game;

    }

    //endregion

    //region Hand Table Functions

    //endregion


}
