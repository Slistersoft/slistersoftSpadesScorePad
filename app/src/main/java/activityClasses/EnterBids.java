package activityClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import customClasses.CUSTOM_FUNCTIONS;
import com.slistersoft.slistersoftspadesscorepad.R;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedParameters")
public class EnterBids extends AppCompatActivity {

    Context callingContext;

    String t1Name, t2Name;

    int t1Score, t2Score, boardBid, minBlindBid, minNilBid, blindDeficitRequired, t1Bid, t2Bid;

    boolean t1Blind, t2Blind, t1Nil, t2Nil;

    TextView tvT1Name, tvT2Name, tvT1Score, tvT2Score;

    CheckBox cbT1Blind, cbT2Blind, cbT1Nil, cbT2Nil;

    Spinner t1BidSpinner, t2BidSpinner;

    CUSTOM_FUNCTIONS custFuncs;

    public EnterBids() {
        try {
            callingContext = this;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EnterBids(Context callingContext) {
        this.callingContext = callingContext;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_bids);

        custFuncs = new CUSTOM_FUNCTIONS(callingContext);

        //Initialize Controls
        try{
            tvT1Name = (TextView)findViewById(R.id.tvT1Name);
            tvT2Name = (TextView)findViewById(R.id.tvT2Name);
            tvT1Score = (TextView)findViewById(R.id.tvT1Score);
            tvT2Score = (TextView)findViewById(R.id.tvT2Score);

            cbT1Blind = (CheckBox)findViewById(R.id.cbT1Blind);
            cbT1Nil = (CheckBox)findViewById(R.id.cbT1Nil);
            cbT2Blind = (CheckBox)findViewById(R.id.cbT2Blind);
            cbT2Nil = (CheckBox)findViewById(R.id.cbT2Nil);

            t1BidSpinner = (Spinner)findViewById(R.id.t1BidPicker);
            t2BidSpinner = (Spinner)findViewById(R.id.t2BidPicker);

        }
        catch (Exception ex){
            custFuncs.MsgBox("Error initializing controls. Message: " + ex.getMessage());
        }

        getDataFromScorePad();
        populateSpinners(1, boardBid);
        populateSpinners(2, boardBid);

        try{
            tvT1Name.setText(t1Name);
            tvT1Score.setText(getScoreDisplayString(t1Score));

            tvT2Name.setText(t2Name);
            tvT2Score.setText(getScoreDisplayString(t2Score));


        }
        catch (Exception ex){
            custFuncs.MsgBox(ex.getMessage());
        }

    }

    private String getScoreDisplayString(int scoreToDisplay){

        try{
            return "Current Score: " + Integer.toString(scoreToDisplay);
        }
        catch (Exception ex){
            custFuncs.MsgBox(ex.getMessage());
            return "Error";
        }

    }

    public void setBlind(View v){

        switch (v.getId()){
            case R.id.cbT1Blind:

                cbT1Blind.setChecked(isBlindLegal(1, cbT1Blind.isChecked()));

                t1Blind = cbT1Blind.isChecked();

                if (cbT1Blind.isChecked() && cbT1Nil.isChecked()) {
                    populateSpinners(1, minNilBid);
                }

                if (cbT1Blind.isChecked() && !cbT1Nil.isChecked()) {
                    populateSpinners(1, minBlindBid);
                }

                if (!cbT1Blind.isChecked() && !cbT1Nil.isChecked()) {
                    populateSpinners(1, boardBid);
                }

                break;

            case R.id.cbT2Blind:

                cbT2Blind.setChecked(isBlindLegal(2, cbT2Blind.isChecked()));

                t2Blind = cbT2Blind.isChecked();

                if (cbT2Blind.isChecked() && cbT2Nil.isChecked()) {
                    populateSpinners(2, minNilBid);
                }

                if (cbT2Blind.isChecked() && !cbT2Nil.isChecked()) {
                    populateSpinners(2, minBlindBid);
                }

                if (!cbT2Blind.isChecked() && !cbT2Nil.isChecked()) {
                    populateSpinners(2, boardBid);
                }


        }

    }
    
    public void setNil(View v){
        
        switch (v.getId()){
            
            case R.id.cbT1Nil:

                t1Nil = cbT1Nil.isChecked();

                if (cbT1Nil.isChecked()) {
                    populateSpinners(1, minNilBid);
                } 
                else if (cbT1Blind.isChecked()) {
                    cbT1Blind.setChecked(false);
                    cbT1Blind.setChecked(true);
                } else {
                    populateSpinners(1, boardBid);
                }
                
                break;
            
            case R.id.cbT2Nil:

                t2Nil = cbT2Nil.isChecked();

                if (cbT2Nil.isChecked()) {
                    populateSpinners(2, minNilBid);
                }
                else if (cbT2Blind.isChecked()) {
                    cbT2Blind.setChecked(false);
                    cbT2Blind.setChecked(true);
                } else {
                    populateSpinners(2, boardBid);
                }
                
                break;
            
        }
        
    }

    private boolean isBlindLegal(int teamNum, boolean isBlind, boolean suppressMessages) {

        if(isBlind){

            String winningMsg = "Don't get greedy now! You are winning. You can only blind bid if you are losing by at least " + blindDeficitRequired + " points.";
            String notLosingBadEnoughMsg = "Sorry but you aren't technically desperate enough to blind bid yet. You need to be losing by at least " + blindDeficitRequired + " points before you are allowed to blind bid";

            //Check to see if blind restrictions are even set
            if(blindDeficitRequired == 0){
                return true; //The preff is set to always allow blind bid so return true to allow the blind bid
            }
            else if(custFuncs.amIWinning(teamNum, t1Score, t2Score)){
                //Shame them for bidding blind when they are winning
                if(!suppressMessages)
                    custFuncs.MsgBox(this,winningMsg,true);
                return false;
            }
            else if(getScoreDifference() >= blindDeficitRequired)
                return true;

            else if(getScoreDifference() <= blindDeficitRequired){
                if(!suppressMessages)
                    custFuncs.MsgBox(this, notLosingBadEnoughMsg, true);
                return false;
            }
            else
                return false;

        }
        else
            return false;

    }


    private boolean isBlindLegal(int teamNum, boolean isBlind){

        return isBlindLegal(teamNum, isBlind, false);

    }

    protected boolean isBlindLegal(int teamNum, int t1S, int t2S, int blindDeficit){

        t1Score = t1S;
        t2Score = t2S;
        blindDeficitRequired = blindDeficit;

        return isBlindLegal(teamNum, true, true);

    }

    private int getScoreDifference(){

        int winningTeam;

        if(custFuncs.amIWinning(1, t1Score, t2Score))
            winningTeam = 1;
        else if(custFuncs.amIWinning(2, t1Score, t2Score))
            winningTeam = 2;
        else
            winningTeam = 0; //0 means they are tied

        switch (winningTeam){

            case 0: return 0;

            case 1: return t1Score - t2Score;

            case 2: return t2Score - t1Score;

            default: return 0;

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                cancelBids(null);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void getDataFromScorePad(){

        try{

            Intent i = getIntent();

            t1Score = i.getIntExtra(ScorePad.INTENT_KEY_T1_SCORE, 0);
            t2Score = i.getIntExtra(ScorePad.INTENT_KEY_T2_SCORE, 0);
            t1Name = i.getStringExtra(ScorePad.INTENT_KEY_TEAM1_NAME);
            t2Name = i.getStringExtra(ScorePad.INTENT_KEY_TEAM2_NAME);
            boardBid = i.getIntExtra(ScorePad.INTENT_KEY_REGULAR_MIN_BID, 4);
            minBlindBid = i.getIntExtra(ScorePad.INTENT_KEY_BLIND_MIN_BID, 7);
            blindDeficitRequired = i.getIntExtra(ScorePad.INTENT_KEY_BLIND_BID_DEFICIT_REQUIRED, 100);
            minNilBid = i.getIntExtra(ScorePad.INTENT_KEY_NIL_MIN_BID, 0);
        }
        catch (Exception ex){
            custFuncs.MsgBox("Error getting data from parent activity. Message: " + ex.getMessage());
        }




    }

    private void populateSpinners(int teamNumToPopulate, int minBid){

        try{
            List<String> spinnerArray = new ArrayList<String>();

            for(int x = minBid; x <= 13; x++){

                spinnerArray.add(Integer.toString(x));

            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, spinnerArray);

            switch (teamNumToPopulate){

                case 1:
                    t1BidSpinner.setAdapter(adapter);
                    break;

                case 2:
                    t2BidSpinner.setAdapter(adapter);
                    break;

            }

        }
        catch (Exception ex){
            custFuncs.MsgBox(ex.getMessage());
        }

    }

    private int getBid(Spinner spinnerToGetBidFrom){

        try {
            return Integer.parseInt(spinnerToGetBidFrom.getSelectedItem().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return -13;
        }

    }

    public void acceptBids(View v){

        try{

            Intent i = new Intent();

            setResult(Activity.RESULT_OK, i);

            t1Bid = getBid(t1BidSpinner);
            t2Bid = getBid(t2BidSpinner);

            //Pass Bid Data Back to Scorepad Activity
            i.putExtra(ScorePad.INTENT_KEY_T1_BID, t1Bid);
            i.putExtra(ScorePad.INTENT_KEY_T1_BLIND, t1Blind);
            i.putExtra(ScorePad.INTENT_KEY_T1_NIL, t1Nil);

            i.putExtra(ScorePad.INTENT_KEY_T2_BID, t2Bid);
            i.putExtra(ScorePad.INTENT_KEY_T2_BLIND, t2Blind);
            i.putExtra(ScorePad.INTENT_KEY_T2_NIL, t2Nil);

            finish();
        }
        catch (Exception ex){

        }

    }

    public void cancelBids(View v){

        try{
            Intent i = new Intent();
            setResult(Activity.RESULT_CANCELED, i);
            finish();
        }
        catch (Exception ex){

        }

    }
}
