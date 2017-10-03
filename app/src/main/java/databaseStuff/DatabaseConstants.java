package databaseStuff;

/**
 * Created by slist on 9/24/2017.
 */

public interface DatabaseConstants {

    int DATABASE_VERSION = 4;
    String DATABASE_NAME = "GameSaves.db";

    String TABLE_GAMES = "GAMES";
    String GAMES_COLUMN_ID = "id";
    String GAMES_COLUMN_T1NAME = "T1NAME";
    String GAMES_COLUMN_T2NAME = "T2NAME";
    String GAMES_COLUMN_DATESTARTED = "DATESTARTED";
    String GAMES_COLUMN_ISGAMECOMPLETE = "ISGAMECOMPLETE";
    String GAMES_COLUMN_T1SCORE = "T1SCORE";
    String GAMES_COLUMN_T2SCORE = "T2SCORE";
}
