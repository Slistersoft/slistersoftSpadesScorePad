package databaseStuff;

/**
 * Created by slist on 9/24/2017.
 */

public interface DatabaseConstants {

    int DATABASE_VERSION = 5;
    String DATABASE_NAME = "GameSaves.db";

    //region Game Table Constants
    String TABLE_GAMES = "GAMES";
    String GAMES_COLUMN_ID = "GamesID";
    String GAMES_COLUMN_T1NAME = "T1Name";
    String GAMES_COLUMN_T2NAME = "T2Name";
    String GAMES_COLUMN_DATESTARTED = "DateStarted";
    String GAMES_COLUMN_ISGAMECOMPLETE = "IsGameComplete";
    String GAMES_COLUMN_T1SCORE = "T1Score";
    String GAMES_COLUMN_T2SCORE = "T2Score";
    //endregion

    //region Hand Table Constants
    String TABLE_HANDS = "HANDS";
    String HANDS_COLUMN_ID = "HandsID";
    String HANDS_COLUMN_GAMEID = GAMES_COLUMN_ID;
    //endregion
}
