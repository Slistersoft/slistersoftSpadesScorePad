package com.slistersoft.slistersoftspadesscorepad;

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

    String createGamesTableStatement = "CREATE TABLE " + TABLE_GAMES + "("
            + GAMES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + GAMES_COLUMN_T1NAME + " TEXT, "
            + GAMES_COLUMN_T2NAME + " TEXT, "
            + GAMES_COLUMN_T1SCORE + " NUMERIC, "
            + GAMES_COLUMN_T2SCORE + " NUMERIC, "
            + GAMES_COLUMN_ISGAMECOMPLETE + " NUMERIC, "
            + GAMES_COLUMN_DATESTARTED + " INTEGER"
            + ")";

}
