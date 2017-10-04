package customClasses;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;

import databaseStuff.DatabaseConstants;

/**
 * Created by slist on 10/3/2017.
 */

public class Hand implements DatabaseConstants {

    private Context callingContext;
    protected Game parentGame;
    public TeamHandData Team1Data, Team2Data;


    //region Constructors

    public Hand(Context callingContext, Game parentGame, TeamHandData Team1Data, TeamHandData Team2Data) {
        this.callingContext = callingContext;
        this.parentGame = parentGame;
        this.Team1Data = Team1Data;
        this.Team2Data = Team2Data;
    }

    public Hand(Context callingContext, long parentGameID) {

    }

    //endregion

    //region Getters and Setters

    public String getCreateTableQuery() {
        return "CREATE TABLE `HANDS` (\n" +
                "\t`HandsID`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                "\t`GamesID`\tINTEGER NOT NULL,\n" +
                "\t`HandNum`\tINTEGER NOT NULL,\n" +
                "\t`TeamNum`\tINTEGER NOT NULL,\n" +
                "\t`Bid`\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\t`IsBlind`\tNUMERIC NOT NULL DEFAULT 0,\n" +
                "\t`IsNil`\tNUMERIC NOT NULL DEFAULT 0,\n" +
                "\t`BooksEarned`\tINTEGER NOT NULL DEFAULT 0,\n" +
                "\tFOREIGN KEY(`GamesID`) REFERENCES `GAMES`(`GamesID`) ON DELETE CASCADE\n" +
                ");";
    }

    //endregion

}


