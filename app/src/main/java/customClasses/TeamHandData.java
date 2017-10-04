package customClasses;

/**
 * Created by slist on 10/3/2017.
 */

public class TeamHandData {

    private long GamesID, HandsID;
    private int HandNum, TeamNum, Bid, Books;
    private boolean IsBlind, IsNil;

    //region Constructors
    public TeamHandData(long GamesID, int handNum, int teamNum, int bid, int books, boolean isBlind, boolean isNil) {
        GamesID = GamesID;
        HandNum = handNum;
        TeamNum = teamNum;
        Bid = bid;
        Books = books;
        IsBlind = isBlind;
        IsNil = isNil;
    }

    //endregion

    //region Getters and Setters


    public long getGamesID() {
        return GamesID;
    }

    public void setGamesID(long gamesID) {
        GamesID = gamesID;
    }

    public long getHandsID() {
        return HandsID;
    }

    public void setHandsID(long handsID) {
        HandsID = handsID;
    }

    public int getHandNum() {
        return HandNum;
    }

    public void setHandNum(int handNum) {
        HandNum = handNum;
    }

    public int getTeamNum() {
        return TeamNum;
    }

    public void setTeamNum(int teamNum) {
        TeamNum = teamNum;
    }

    public int getBid() {
        return Bid;
    }

    public void setBid(int bid) {
        Bid = bid;
    }

    public int getBooks() {
        return Books;
    }

    public void setBooks(int books) {
        Books = books;
    }

    public boolean isBlind() {
        return IsBlind;
    }

    public void setBlind(boolean blind) {
        IsBlind = blind;
    }

    public boolean isNil() {
        return IsNil;
    }

    public void setNil(boolean nil) {
        IsNil = nil;
    }


    //endregion
}
