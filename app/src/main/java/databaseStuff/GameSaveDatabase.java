package databaseStuff;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.slistersoft.slistersoftspadesscorepad.CUSTOM_FUNCTIONS;
import com.slistersoft.slistersoftspadesscorepad.Game;
import com.slistersoft.slistersoftspadesscorepad.R;

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
            db.execSQL(Game.getCreateTableQuery());
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
     * Inserts record(s) to the database.
     * @param tableToInsertInto Name of table to insert into.
     * @param valuesToInsert Value(s) to insert.
     * @return long The row ID of the newly inserted row, or -1 if an error occurred
     */
    public long insertRecordToDB(String tableToInsertInto, ContentValues valuesToInsert){

        long insertedRowID = 0;
        try {
            SQLiteDatabase db = getWritableDatabase();
            insertedRowID = db.insertOrThrow(tableToInsertInto, null, valuesToInsert);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return insertedRowID;

    }

    /**
     * Updates data in database.
     * @param tableNameToUpdate Name of table to update
     * @param updatedValues Updated values to save to DB
     * @param whereClause Where clause of what row(s) to update. For example instead of saying Where ID = x just say ID = x
     * @return int Number of rows updated.
     */
    public long updateRecordsInDB(String tableNameToUpdate, ContentValues updatedValues, String whereClause){

        int rowsUpdated = 0;

        try {
            SQLiteDatabase db = getWritableDatabase();
             rowsUpdated = db.update(tableNameToUpdate, updatedValues, whereClause, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowsUpdated;

    }

    /**
     * Converts EPOCH time into human readable date and time format.
     * @param EPOCHDateValue The long numeric value stored in the DB that represents the number of milliseconds since Jan 1, 1970 called the EPOCH or Unix time.
     * @param SimpleDateFormatPattern Date format pattern to return. Most follow official SimpleDateFormat format pattern.
     * @return Human readable date string in specified format.
     */
    public static String getHumanFriendlyDateStringFromEPOCH(long EPOCHDateValue, String SimpleDateFormatPattern){

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
    public static String getHumanFriendlyDateStringFromEPOCH(long EPOCHDateValue){

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

}
