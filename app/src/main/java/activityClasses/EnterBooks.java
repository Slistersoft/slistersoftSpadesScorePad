package activityClasses;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import customClasses.CUSTOM_FUNCTIONS;
import com.slistersoft.slistersoftspadesscorepad.R;

public class EnterBooks extends AppCompatActivity {

    CUSTOM_FUNCTIONS custFuncs = new CUSTOM_FUNCTIONS(this);

    String t1Name, t2Name;

    int t1Books, t2Books, spinnerSelectedCount;

    TextView tvT1Name, tvT2Name;

    Spinner t1BookSpinner, t2BookSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_enter_books);

            getDataFromScorePad();

            //Initialize Controls
            tvT1Name = (TextView)findViewById(R.id.tvT1Name);
            tvT2Name = (TextView)findViewById(R.id.tvT2Name);

            t1BookSpinner = (Spinner)findViewById(R.id.t1BookPicker);
            t2BookSpinner = (Spinner)findViewById(R.id.t2BookPicker);

            //Display Team Names
            tvT1Name.setText(t1Name);
            tvT2Name.setText(t2Name);


            //Setup Spinner Event Handlers

            t1BookSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override

                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    setBooks(adapterView);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });

            t2BookSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    setBooks(adapterView);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    return;
                }
            });


        } catch (Exception e) {
            custFuncs.MsgBox(e.getMessage());
            e.printStackTrace();
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

    private void setBooks(View v){

        try {

            //This is a hack since this method is called once for each spinner at creation.
            if (spinnerSelectedCount < 2)  {
                spinnerSelectedCount++;
            }
            else{
            Spinner sp = (Spinner) v;

            int selectedBooks = Integer.parseInt(sp.getSelectedItem().toString());

            switch (v.getId()) {

                case R.id.t1BookPicker:
                    t1Books = selectedBooks;
                    t2BookSpinner.setSelection(getOpposingTeamsBooks(selectedBooks));
                    break;

                case R.id.t2BookPicker:
                    t2Books = selectedBooks;
                    t1BookSpinner.setSelection(getOpposingTeamsBooks(selectedBooks));
                    break;

            }
            }

        } catch(Exception e){
                custFuncs.MsgBox(e.getMessage());
                e.printStackTrace();
            }
    }

    private int getOpposingTeamsBooks(int knownTeamsBooks){

        int opposingTeamBooks = 13 - knownTeamsBooks;

        if(opposingTeamBooks < 0)
            opposingTeamBooks *= -1;

        return opposingTeamBooks;

    }

    private void getDataFromScorePad(){

        try{

            Intent i = getIntent();

            t1Name = i.getStringExtra(ScorePad.INTENT_KEY_TEAM1_NAME);
            t2Name = i.getStringExtra(ScorePad.INTENT_KEY_TEAM2_NAME);

        }
        catch (Exception ex){
            custFuncs.MsgBox("Error getting data from parent activity. Message: " + ex.getMessage());
        }

    }

    private boolean areEnteredBooksLegalAmount(int t1BooksEntered, int t2BooksEntered){
        int bookSum = t1BooksEntered + t2BooksEntered;

        if(bookSum == 13)
            return  true;
        else
            return false;

    }

    public void acceptBids(View v){

        try{

            if (areEnteredBooksLegalAmount(t1Books, t2Books)) {

                Intent i = new Intent();

                setResult(Activity.RESULT_OK, i);

                //Pass Bid Data Back to Scorepad Activity
                i.putExtra(ScorePad.INTENT_KEY_T1_BOOKS, t1Books);
                i.putExtra(ScorePad.INTENT_KEY_T2_BOOKS, t2Books);

                finish();

            } else {
                custFuncs.MsgBox(getString(R.string.illegalBookEnteredAmountMsg));
            }

        }
        catch (Exception ex){
            custFuncs.MsgBox(getString(R.string.acceptBidsCatchMsg) + ex.getMessage());
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
