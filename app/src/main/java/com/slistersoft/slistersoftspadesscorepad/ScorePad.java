package com.slistersoft.slistersoftspadesscorepad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;


@SuppressWarnings("UnusedParameters")
public class ScorePad extends AppCompatActivity {

    ActionBar actionBar = null;

    //Reference to the Custom Functions class that has commonly used methods like toast and msgbox
    CUSTOM_FUNCTIONS custFuncs = new CUSTOM_FUNCTIONS(this);
    EnterBids eb = new EnterBids(this);

    MediaPlayer sound = null;

    public static String
            INTENT_KEY_TEAM1_NAME = "T1NAME",
            INTENT_KEY_TEAM2_NAME = "T2NAME",
            INTENT_KEY_NIL_MIN_BID = "INT_NIL_MIN_BID",
            INTENT_KEY_REGULAR_MIN_BID = "REGULAR_MIN_BID",
            INTENT_KEY_BLIND_MIN_BID = "BLIND_MIN_BID",
            INTENT_KEY_BLIND_BID_DEFICIT_REQUIRED = "BLIND_BID_DEFICIT_REQUIRED",
            INTENT_KEY_T1_SCORE = "t1Score",
            INTENT_KEY_T2_SCORE = "t2Score",
            INTENT_KEY_T1_BID = "T1BID",
            INTENT_KEY_T1_BLIND = "T1BLIND",
            INTENT_KEY_T1_NIL = "T1NIL",
            INTENT_KEY_T2_BID = "T2BID",
            INTENT_KEY_T2_BLIND = "T2BLIND",
            INTENT_KEY_T2_NIL = "T2NIL",
            INTENT_KEY_T1_BOOKS = "T1BOOKS",
            INTENT_KEY_T2_BOOKS = "T2BOOKS";

    static int standardMultiplier = 10, blindMultiplier = 20;

    int INT_NIL_MIN_BID = 0;
    int INT_REGULAR_MIN_BID = 4;
    int INT_BLIND_MIN_BID = 7;
    int INT_BLIND_BID_DEFICIT_REQUIRED = 100;
    int AUDIO_COMMENTARY = 0; //0 Indicates that commentary is off
    int PLAY_TO_SCORE = 350;
    int BLIND_BID_STYLE = 1;
    int BLIND_BID_POINT_BONUS = 200;
    int INT_SANDBAG_LIMIT = 13;
    int INT_SANDBAG_PENALTY = 0;

    int
            t1Score = 0,
            t1OldScore = 0,
            t2Score = 0,
            t2OldScore = 0;

    int
            t1Bid = 0,
            t1Books = 0,
            t1Sandbags = 0,
            t1EarnedPoints = 0,

    t2Bid = 0,
            t2Books = 0,
            t2Sandbags = 0,
            t2EarnedPoints = 0;

    boolean
            t1Blind = false,
            t2Blind = false,
            t1Nil = false ,
            t2Nil = false,
            t1BidEntered = false,
            t2BidEntered = false,
            t1BooksEntered = false,
            t2BooksEntered = false,
            blnFirstHandBid = false,
            blnNoOversMode = false;

    String
            team1Name,
            team2Name;

    TextView
            tvT1TeamNameDisplay = null,
            tvT1ScoreDisplay = null,
            tvT1BidDisplay = null,
            tvT1PointsToWin = null,
            tvT2TeamNameDisplay = null,
            tvT2ScoreDisplay = null,
            tvT2BidDisplay = null,
            tvT2PointsToWin = null,
            tvUndoLabel = null;


    FloatingActionButton
            fabMain = null,
            fabPlaceBid = null,
            fabEnterBooks = null,
            fabUndo = null,
            fabEditScores = null;

    Animation
            animShowFAB = null,
            animHideFAB = null,
            animShowFABMenu = null,
            animHideFABMenu = null,
            animSlideInCardT1 = null,
            animSlideInCardT2 = null;

    TableLayout
            fabTableLayout;

    CardView
        cvT1ScoreCard,
        cvT2ScoreCard;

    private static final int ENTER_BIDS_REQUEST_CODE = 1, ENTER_BOOKS_REQUEST_CODE = 2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.score_pad, menu);

        // Return true to display menu
        return true;
    }


    public void launchBidEnterScreen(View v){

        try{

            updatePreferences();

            if (t1Score == 0 && t2Score == 0 && !blnFirstHandBid){
                custFuncs.MsgBox(getString(R.string.FirstHandBidNotAllowedMsg), true);
            }
            else {

                closeFABMenu();

                //Intent i = new Intent(this, EnterBids.class);
                Intent i = getAndPopulateIntentWithGameData(this, EnterBids.class);
                startActivityForResult(i, ENTER_BIDS_REQUEST_CODE);

            }
        }
        catch (Exception ex){
            custFuncs.MsgBox(getString(R.string.launchBidEnterScreenCatchMsg), true);
            ex.printStackTrace();
        }

    }

    public void launchBookEnteringScreen(View v){

        closeFABMenu();

        if((t1Score == 0 && t2Score == 0) && (!t1BidEntered || !t2BidEntered) &&  blnFirstHandBid){
            custFuncs.MsgBox(this,"You must place bids for both teams before entering the books.", true);
        }
        else{
            updatePreferences();

            try {
                Intent i = getAndPopulateIntentWithGameData(this, EnterBooks.class);
                startActivityForResult(i, ENTER_BOOKS_REQUEST_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void updateBidDisplays(){

        try {
            tvT1BidDisplay.setText(getBidString(t1Bid, t1Blind, t1Nil));
            tvT2BidDisplay.setText(getBidString(t2Bid, t2Blind, t2Nil));

            t1BidEntered = true;
            t2BidEntered = true;

        } catch (Exception e) {
            custFuncs.MsgBox("Error displaying bids.");
            e.printStackTrace();
        }

    }

    private Intent getAndPopulateIntentWithGameData(Context callingContext, Class destinationClass){

        try{
            Intent i = new Intent(callingContext, destinationClass);

            updatePreferences();

            i.putExtra(INTENT_KEY_TEAM1_NAME, team1Name);
            i.putExtra(INTENT_KEY_TEAM2_NAME, team2Name);
            i.putExtra(INTENT_KEY_REGULAR_MIN_BID, INT_REGULAR_MIN_BID);
            i.putExtra(INTENT_KEY_BLIND_BID_DEFICIT_REQUIRED, INT_BLIND_BID_DEFICIT_REQUIRED);
            i.putExtra(INTENT_KEY_BLIND_MIN_BID, INT_BLIND_MIN_BID);
            i.putExtra(INTENT_KEY_NIL_MIN_BID, INT_NIL_MIN_BID);
            i.putExtra(INTENT_KEY_T1_SCORE, t1Score);
            i.putExtra(INTENT_KEY_T2_SCORE, t2Score);


            return i;
        }
        catch (Exception ex){
            custFuncs.MsgBox("Error passing data to child activity intent. Message: " + ex.getMessage(), true);
            return new Intent();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){

            case ENTER_BIDS_REQUEST_CODE:

                if(resultCode == Activity.RESULT_OK){

                    try {
                        t1Bid = data.getIntExtra(INTENT_KEY_T1_BID, 0);
                        t1Blind = data.getBooleanExtra(INTENT_KEY_T1_BLIND, false);
                        t1Nil = data.getBooleanExtra(INTENT_KEY_T1_NIL, false);

                        t2Bid = data.getIntExtra(INTENT_KEY_T2_BID, 0);
                        t2Blind = data.getBooleanExtra(INTENT_KEY_T2_BLIND, false);
                        t2Nil = data.getBooleanExtra(INTENT_KEY_T2_NIL, false);

                        updateBidDisplays();

                    } catch (Exception e) {
                        custFuncs.MsgBox("Error getting bids.");
                        e.printStackTrace();
                    }

                }

                break;

            case ENTER_BOOKS_REQUEST_CODE:

                if(resultCode == Activity.RESULT_OK){

                    t1Books = data.getIntExtra(INTENT_KEY_T1_BOOKS, 0);
                    t2Books = data.getIntExtra(INTENT_KEY_T2_BOOKS, 0);

                    t1BooksEntered = true;
                    t2BooksEntered = true;

                    tallyScores();

                }



        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){

            case R.id.menu_item_share_score:

                shareScore();

                break;

            case R.id.menu_item_preferences:

                try {
                    Intent prefs = new Intent(this.getApplicationContext(), PreferencesActivity.class);
                    startActivity(prefs);

                    break;
                }
                catch (Exception e){
                    custFuncs.MsgBox(this.getApplicationContext(), "Error launching preferences.",true);
                    break;
                }

        }

        return false;
    }

    private void shareScore(){


        try{

            final CharSequence teamArray[] = {team1Name, team2Name};


            //Ask user what team they are on
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.whatTeamAreYouPrompt)
                    .setItems(teamArray, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String losePrefix = getResources().getString(R.string.teamLosePrefix) + "\n\n";
                            String winPrefix = getResources().getString(R.string.teamWonPrefix) + "\n\n";
                            String tiePrefix = getResources().getString(R.string.teamTiePrefix) + "\n\n";
                            String meatAndPotatoes = team1Name + ": " + t1Score + " " + team2Name + ": " + t2Score + "\n\n";
                            String appName = getResources().getString(R.string.app_name);
                            String shortPlayStoreLink = getResources().getString(R.string.shortPlayStoreURL);
                            String conclusion = "And keeping score was easy because I used Slistersoft " + appName + ".\n\nCheck it out on the play store for FREE!\n\n" + shortPlayStoreLink;
                            String contentToSend;

                            int winningTeamNum;

                            if(t1Score > t2Score)
                                winningTeamNum = 1;
                            else if(t2Score > t1Score)
                                winningTeamNum = 2;
                            else
                                winningTeamNum = 0;

                            switch(which){

                                case 0:
                                    //Team 1 Selected
                                    if(winningTeamNum == 1){
                                        contentToSend = winPrefix + meatAndPotatoes + conclusion;
                                        startShareMenu(contentToSend);
                                    }
                                    else if(winningTeamNum == 2){
                                        contentToSend = losePrefix + meatAndPotatoes + conclusion;
                                        startShareMenu(contentToSend);
                                    }
                                    else{
                                        contentToSend = tiePrefix + meatAndPotatoes + conclusion;
                                        startShareMenu(contentToSend);
                                    }


                                    break;

                                case 1:
                                    //Team 2 Selected
                                    if(winningTeamNum == 2){
                                        contentToSend = winPrefix + meatAndPotatoes + conclusion;
                                        startShareMenu(contentToSend);
                                    }
                                    else if(winningTeamNum == 1){
                                        contentToSend = losePrefix + meatAndPotatoes + conclusion;
                                        startShareMenu(contentToSend);
                                    }
                                    else{
                                        contentToSend = tiePrefix + meatAndPotatoes + conclusion;
                                        startShareMenu(contentToSend);
                                    }

                                    break;

                            }

                        }
                    });
            builder.create();
            builder.show();

        }
        catch(Exception e){
            custFuncs.MsgBox(this, "Sorry for some reason your score could not be shared.", true);
        }



    }

    private void startShareMenu(String contentToSend){

        try{
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, contentToSend);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Where do you want to brag?"));
        }
        catch(Exception e){
            custFuncs.MsgBox(this, "Sorry for some reason the share menu won't load. You might not have any apps installed that you can share this to.", true);
        }

    }private void updatePreferences(){

        //Try and load the bidding preferences
        try{

            Resources res = getResources();

            INT_REGULAR_MIN_BID=Integer.parseInt(custFuncs.getPreference(this.getApplicationContext(), this.getResources().getString(R.string.pref_key_minimumRegularBid),this.getResources().getString(R.string.pref_default_minimumRegularBid)));
            INT_BLIND_MIN_BID=Integer.parseInt(custFuncs.getPreference(this.getApplicationContext(), this.getResources().getString(R.string.pref_key_minimumBlindBid),this.getResources().getString(R.string.pref_default_minimumBlindBid)));
            INT_BLIND_BID_DEFICIT_REQUIRED = Integer.parseInt(custFuncs.getPreference(this.getApplicationContext(), this.getResources().getString(R.string.pref_key_blind_deficit),this.getResources().getString(R.string.pref_default_blind_deficit_required)));
            AUDIO_COMMENTARY = Integer.parseInt(custFuncs.getPreference(this.getApplicationContext(),this.getResources().getString(R.string.pref_key_kevin_audio),this.getResources().getString(R.string.pref_default_kevin_audio)));
            PLAY_TO_SCORE = Integer.parseInt(custFuncs.getPreference(this.getApplicationContext(), this.getResources().getString(R.string.pref_key_winning_score),this.getResources().getString(R.string.pref_default_winning_score)));
            BLIND_BID_STYLE=Integer.parseInt(custFuncs.getPreference(this.getApplicationContext(), getString(R.string.pref_key_blindBidStyle),getString(R.string.pref_default_blindBidStyle)));
            BLIND_BID_POINT_BONUS = Integer.parseInt(custFuncs.getPreference(this.getApplicationContext(),getString(R.string.pref_key_blindBidPointBonus),getString(R.string.pref_default_blindBidPointBonus)));
            blnFirstHandBid = custFuncs.getPreference(this.getApplicationContext(),getString(R.string.pref_key_firstHandBid),res.getBoolean(R.bool.pref_default_firstHandBid));
            INT_SANDBAG_LIMIT = Integer.parseInt(custFuncs.getPreference(this.getApplicationContext(),getString(R.string.pref_key_sandbagLimit),getString(R.string.pref_default_sandbagLimit)));
            INT_SANDBAG_PENALTY = Integer.parseInt(custFuncs.getPreference(this.getApplicationContext(),getString(R.string.pref_key_sandbagPenalty),getString(R.string.pref_default_sandbagPenalty)));
            blnNoOversMode = custFuncs.getPreference(this, getString(R.string.pref_key_noOverOption), getResources().getBoolean(R.bool.pref_default_noOverOption));

        }
        catch (Exception e){
            //Don't worry about it just use the default values declared at the top
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        try{

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_score_pad);

            // Show the Up button in the action bar.
            setupActionBar();

            //Receive Team Names
            Intent intent = getIntent();
            team1Name = intent.getStringExtra(MainActivity.TEAM_1);
            team2Name = intent.getStringExtra(MainActivity.TEAM_2);

            //Setup Control References

            //Labels
            tvT1TeamNameDisplay = (TextView)findViewById(R.id.team1Label);
            tvT1ScoreDisplay = (TextView)findViewById(R.id.tvT1Score);
            tvT1BidDisplay = (TextView)findViewById(R.id.tvT1BidDisplay);
            tvT1PointsToWin = (TextView)findViewById(R.id.tvT1PointsToWin);
            tvUndoLabel = (TextView)findViewById(R.id.tvUndoHand);


            tvT2TeamNameDisplay = (TextView)findViewById(R.id.team2Label);
            tvT2ScoreDisplay = (TextView)findViewById(R.id.tvT2Score);
            tvT2BidDisplay = (TextView)findViewById(R.id.tvT2BidDisplay);
            tvT2PointsToWin = (TextView)findViewById(R.id.tvT2PointsToWin);


            //Set Labels
            tvT1TeamNameDisplay.setText(team1Name);
            tvT2TeamNameDisplay.setText(team2Name);

            //Setup FAB Menu

            //Fab Buttons
            fabMain = (FloatingActionButton)findViewById(R.id.fabMain);
            fabPlaceBid = (FloatingActionButton)findViewById(R.id.fabPlaceBids);
            fabEnterBooks = (FloatingActionButton)findViewById(R.id.fabEnterBooks);
            fabUndo = (FloatingActionButton)findViewById(R.id.fabUndoLastHand);
            fabEditScores = (FloatingActionButton)findViewById(R.id.fabEditScores);

            //Layout
            fabTableLayout = (TableLayout)findViewById(R.id.fabItemsTable);

            //Cards
            cvT1ScoreCard = (CardView)findViewById(R.id.T1ScoreCard);
            cvT2ScoreCard = (CardView)findViewById(R.id.T2ScoreCard);

            //Animations
            animShowFAB = AnimationUtils.loadAnimation(this,R.anim.show_main_fab);
            animHideFAB = AnimationUtils.loadAnimation(this,R.anim.hide_main_fab);
            animShowFABMenu = AnimationUtils.loadAnimation(this,R.anim.show_fab_menu);
            animHideFABMenu = AnimationUtils.loadAnimation(this,R.anim.hide_fab_menu);
            animSlideInCardT1 = AnimationUtils.loadAnimation(this, R.anim.slide_in_card);
            animSlideInCardT2 = AnimationUtils.loadAnimation(this, R.anim.slide_in_card);

            updateScores(t1Score, t2Score);

        }
        catch(Exception e){

            custFuncs.MsgBox("Error initializing activity.");

        }


        updatePreferences();

        cvT1ScoreCard.startAnimation(animSlideInCardT1);
        animSlideInCardT2.setStartOffset(150);
        cvT2ScoreCard.startAnimation(animSlideInCardT2);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        try{
            super.onSaveInstanceState(savedInstanceState);

            //Save UI state changes to the savedInstanceState
            //Which is then passed to onCreate after it is killed and restarted

            savedInstanceState.putInt("t1Score", t1Score);
            savedInstanceState.putInt("t2Score", t2Score);

        }
        catch(Exception e){}

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){

        //Reload and apply scores after rotation

        try{
            super.onRestoreInstanceState(savedInstanceState);

            t1Score = savedInstanceState.getInt("t1Score");
            t2Score = savedInstanceState.getInt("t2Score");

            updateScores(t1Score, t2Score);
        }
        catch(Exception e){}

    }

    Thread bidAudioThread = new Thread(new Runnable(){
        public void run(){

            String t1String = "", t2String = "";

            try{


            }
            catch(Exception e){}

		/*Check for board bids
		if(t1String.equals("4") || t2String.equals("4")){
			sound = MediaPlayer.create(getApplicationContext(), R.raw.bidlowcensored);
			sound.start();
		}
		*/

            try{

                t1Bid = Integer.parseInt(t1String);
                t2Bid = Integer.parseInt(t2String);

            }
            catch (Exception e){
                return;
            }

            //Wait until sound is done playing to play the next sound
            while(sound.isPlaying()){
                //Just chill for a sec
            }

            //Check for under bidding
            if(t1Bid + t2Bid < 13){
                //sound = MediaPlayer.create(getApplicationContext(), R.raw.underbid);
                //sound.start();
            }

        }

    });

    private void closeFABMenu(){

        try{
            fabTableLayout.setVisibility(View.GONE);
            fabTableLayout.startAnimation(animHideFABMenu);

            fabMain.startAnimation(animHideFAB);
        }
        catch (Exception e){
            custFuncs.MsgBox(this, e.getMessage(),true);
        }


    }

    private void openFABMenu(){

        try
        {
            fabTableLayout.setVisibility(View.VISIBLE);
            fabTableLayout.startAnimation(animShowFABMenu);

            fabMain.startAnimation(animShowFAB);
        }
        catch (Exception e){
            custFuncs.MsgBox(this, e.getMessage(),true);
        }
    }

    public void toggleFABMenu(View v){

        try{

            if(fabTableLayout.getVisibility() == View.VISIBLE){

                closeFABMenu();

            }
            else{

                openFABMenu();

            }

        }
        catch (Exception ex){
            custFuncs.MsgBox(this, ex.getMessage(),true);
        }

    }

    private boolean validateInput(){

        try{

            updatePreferences();

            int highScore, lowScore, scoreDifference;

            //Check to make sure they actually selected a bid
            if(!t1BidEntered || !t2BidEntered){
                custFuncs.MsgBox(this, "Please enter bids for both teams.", true);
                return false;
            }
            else if(!t1BooksEntered || !t2BooksEntered){
                custFuncs.MsgBox(this, "Please enter the amount of books each team won.", true);
                return false;
            }
            else{

                //Calculate score difference
                if(t1Score > t2Score){
                    highScore = t1Score;
                    lowScore = t2Score;
                }
                else{
                    highScore = t2Score;
                    lowScore = t1Score;
                }

                scoreDifference = highScore - lowScore;

            }

            //Check to make sure there aren't too many or not enough books
            if(t1Books + t2Books > 13 || t1Books + t2Books < 13 ){

				/*
				try{
				sound = MediaPlayer.create(getApplicationContext(), R.raw.bullshitcensored);
				sound.start();
				}
				catch(Exception e){}
				*/

                custFuncs.MsgBox(this, "There are only 13 books in the deck, so both team's books must add up to 13. Double check your books.", true );
                return false;
            }
            //Check for blind mis bidding
            else if((t1Blind && !t1Nil && t1Bid < INT_BLIND_MIN_BID) || (t2Blind && !t2Nil && t2Bid < INT_BLIND_MIN_BID)){
                custFuncs.MsgBox(this, "I know this is crazy but if you are biding blind you must go for at least " + INT_BLIND_MIN_BID +". That's why this is only used when you're super desperate, or if you like to live on the edge.", true);
                return false;
            }
            //Check to see if they are legally desperate enough to blind bid
            else if((t1Blind || t2Blind) && (scoreDifference < INT_BLIND_BID_DEFICIT_REQUIRED)){
                custFuncs.MsgBox(this, "I'm afraid nobody is desperate enough to blind bid just yet. You must be losing by at least " + INT_BLIND_BID_DEFICIT_REQUIRED + " points in order to blind bid.", true);
                return false;
            }
            else
                return true;
        }
        catch(Exception e){
            custFuncs.MsgBox(this, e.getMessage(), true);
            return false;
        }
    }

    private String getBidString(int bid, boolean isBlind, boolean isNil){

        try {
            StringBuilder sb = new StringBuilder();

            if (isBlind)
                sb.append("Blind ");

            if (isNil)
                sb.append("Nil ");

            sb.append("Bid: ");
            sb.append(bid);

            return sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "Bid: Error";
        }

    }

    public void editScores(View v){

        closeFABMenu();

        //Create alert dialog to collect input
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Enter Updated Scores:");

            //Setup layout

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            //Set up labels
            final TextView t1Label = new TextView(this);
            final TextView t2Label = new TextView(this);

            //Set text for labels
            t1Label.setText(team1Name + ":");
            t2Label.setText(team2Name + ":");

            //Set up input boxes
            //Team 1 box
            final EditText t1Box = new EditText(this);

            //Specify input type
            t1Box.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);

            //Set hint
            t1Box.setHint("Current score: " + t1Score);

            //Team 2 Box
            final EditText t2Box = new EditText(this);

            //Specify input type
            t2Box.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);

            //Set hint
            t2Box.setHint("Current score: " + t2Score);

            //Apply text boxes to dialog layout
            layout.addView(t1Label);
            layout.addView(t1Box);

            layout.addView(t2Label);
            layout.addView(t2Box);

            dialog.setView(layout);

            //Set up buttons
            dialog.setPositiveButton("Update Scores", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try{
                        updateScores(Integer.parseInt(t1Box.getText().toString()),Integer.parseInt(t2Box.getText().toString()));
                    }
                    catch(Exception e){}

                }
            });

            dialog.setNegativeButton("Nevermind. It's All Good", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialog.show();
        }
        catch(Exception e){
            custFuncs.MsgBox(this, "Can't show score updater interface for some reason. Sorry.", true);
        }

    }

    public void undoLastHand(View v) {

        closeFABMenu();

        updateScores(t1OldScore, t2OldScore);

        t1Bid = 0;
        t2Bid = 0;

        updateBidDisplays();




    }

    private int getSandbagAmount(int bid, int books){

        int sandbags = books - bid;

        if(sandbags < 0){
            return 0;
        }
        else{
            return sandbags;
        }


    }

    private int getPointsEarned(int booksBid, int booksEarned, int sandbags, boolean isBlind, boolean isNil){

        updatePreferences();

        int successfulNilPoints = Integer.parseInt(this.getString(R.string.successfullNilPoints));
        int successfulBlindNilPoints = Integer.parseInt(this.getString(R.string.successfullBlindNilPoints));

        int nilPoints;


        boolean firstHand;

        firstHand = t1Score == 0 && t2Score == 0 && !blnFirstHandBid;

        if (!firstHand && !blnNoOversMode){
            if (sandbags > INT_SANDBAG_LIMIT)
                booksEarned -= INT_SANDBAG_PENALTY;
        }

        if(blnNoOversMode) {
            booksEarned -= sandbags;
        }

        int multiplier;

        //Determine multipliers
        if (isBlind)
            multiplier = blindMultiplier;
        else
            multiplier = standardMultiplier;

        //Check for nil stuff first

        if(isNil){

            if(isBlind)
                nilPoints = successfulBlindNilPoints;
            else
                nilPoints = successfulNilPoints;

            if(booksEarned == booksBid)
                return (booksEarned * multiplier) + nilPoints;

            else
                return -1 * ((booksBid * multiplier) - (nilPoints * -1));

        }

        else {

            //Check for regular bids
            if (booksEarned == booksBid)

                return booksBid * multiplier;

            else if (firstHand)
                return booksEarned * multiplier;

            else if (booksEarned < booksBid)
                return (booksBid * multiplier) * -1;

            else {
                return (booksEarned - booksBid) + (booksBid * multiplier);
            }
        }

    }

    public void tallyScores() {

        try {

            t1Sandbags = getSandbagAmount(t1Bid, t1Books);
            t2Sandbags = getSandbagAmount(t2Bid, t2Books);

            t1EarnedPoints = getPointsEarned(t1Bid, t1Books,t1Sandbags, t1Blind, t1Nil);
            t2EarnedPoints = getPointsEarned(t2Bid, t2Books,t2Sandbags, t2Blind, t2Nil);

            String scoreSum =

                    getEarnedPointsHumanSummaryString(team1Name, t1Bid, t1Books, t1Sandbags, t1EarnedPoints)

                            + "\n\n" +

                            getEarnedPointsHumanSummaryString(team2Name,t2Bid,t2Books, t2Sandbags, t2EarnedPoints);

            custFuncs.MsgBox(this, scoreSum, false);

            updateScores(t1EarnedPoints + t1Score , t2EarnedPoints + t2Score);

        } catch (Exception e) {
        }

    }

    private String getEarnedPointsHumanSummaryString(String teamName, int bid, int booksEarned, int sandbags, int pointsEarned){

        StringBuilder sb = new StringBuilder();

        sb.append(teamName);
        sb.append(":\nBid: " + bid);
        sb.append("\nEarned Books: " + booksEarned);

        if(blnNoOversMode && (sandbags > INT_SANDBAG_LIMIT)){
            sb.append("\n\nNo Overs Allowed. " + sandbags + " book(s) were deducted.");
        }
        else {

            if (!blnNoOversMode && sandbags > INT_SANDBAG_LIMIT) {
                sb.append("\nSandbag limit of " + INT_SANDBAG_LIMIT + " exceeded. " + INT_SANDBAG_PENALTY + " penalty book(s) were deducted.");
            }

        }

        sb.append("\n\nPoints earned this hand: " + pointsEarned);

        return sb.toString();

    }

    private void clearInputBoxes(){

        try{

            //Clear variables
            t1Bid = 0;
            t1Books = 0;
            t1BidEntered = false;
            t2BooksEntered = false;
            t1EarnedPoints = 0;

            t2Bid = 0;
            t2Books = 0;
            t2BidEntered = false;
            t2BooksEntered = false;
            t2EarnedPoints = 0;

            updateBidDisplays();

        }
        catch(Exception e){}

    }

    private int calculatePointsToWin(int score){

        return PLAY_TO_SCORE - score;

    }

    private String getPointsToWinHumanString(int score){

        StringBuilder sb = new StringBuilder();

        int
                pointsToWin = calculatePointsToWin(score),
                booksToWin = pointsToWin / 10,
                sandBagsToWin = pointsToWin % 10 ;

        sb.append(pointsToWin);
        sb.append(" point");

        if(pointsToWin > 1 || pointsToWin == 0)
            sb.append("s");

        sb.append(" needed to win");

        sb.append(" (");
        sb.append(booksToWin);
        sb.append(" book");

        if(booksToWin <= 0 || booksToWin > 1)
            sb.append("s");

        if(sandBagsToWin > 0){
            sb.append(" and ");
            sb.append(sandBagsToWin);
            sb.append(" sandbag");

            if(sandBagsToWin > 1)
                sb.append("s");
        }

        sb.append(")");

        return  sb.toString();

    }

    private void updateScores(int t1NewScore, int t2NewScore){

        try {

            //get latest winning score pref
            updatePreferences();


            //Save current score for undo ability
            t1OldScore = t1Score;
            t2OldScore = t2Score;

            //Update Scores
            t1Score = t1NewScore;
            t2Score = t2NewScore;

            tvT1ScoreDisplay.setText("Score: " + t1Score);
            tvT2ScoreDisplay.setText("Score: " + t2Score);


            if (t1Score == 0 && t2Score == 0)
                setUndoButtonVisibility(false);
            else
                setUndoButtonVisibility(true);

            //Update points to win labels
            updateScoreToWinLabels(t1NewScore, t2NewScore);


        }
        catch (Exception e){
            return;
        }

        clearInputBoxes();

    }

    private void setUndoButtonVisibility(boolean isUndoButtonsVisible){

        try {
            if(isUndoButtonsVisible) {
                tvUndoLabel.setVisibility(View.VISIBLE);
                fabUndo.setVisibility(View.VISIBLE);
            }
            else{
                tvUndoLabel.setVisibility(View.GONE);
                fabUndo.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void updateScoreToWinLabels(int piT1Score, int piT2Score){

        try {
            tvT1PointsToWin.setText(getPointsToWinHumanString(piT1Score));
            tvT2PointsToWin.setText(getPointsToWinHumanString(piT2Score));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setupActionBar() {

        actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

    }



}