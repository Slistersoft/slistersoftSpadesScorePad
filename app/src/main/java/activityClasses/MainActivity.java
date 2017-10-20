package activityClasses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import customClasses.CUSTOM_FUNCTIONS;
import customClasses.Game;
import com.slistersoft.slistersoftspadesscorepad.R;

import databaseStuff.GameSaveDatabase;

@SuppressWarnings("UnusedParameters")
public class MainActivity extends AppCompatActivity {

    GameSaveDatabase dbHandler;

    ActionBar actionBar = null;

    //Reference to the Custom Functions class that has commonly used methods like toast and msgbox
    CUSTOM_FUNCTIONS custFuncs = new CUSTOM_FUNCTIONS(this);

    public final static String TEAM_1 = "com.slistersoft.slistersoftspadesscorepad.TEAM_1";
    public final static String TEAM_2 = "com.slistersoft.slistersoftspadesscorepad.TEAM_2";

    private boolean hateApp = false;

    private EditText t1NameBox=null , t2NameBox = null;

    private Button btnLoadGames = null;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.main, menu);

        // Return true to display menu
        return true;

    }

    public void launchGamePicker(View v){

        new Game(this).launchGamePicker();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menu_item_preferences:

                try {
                    Intent prefs = new Intent(this, PreferencesActivity.class);
                    startActivity(prefs);
                }
                catch (Exception ex){
                    custFuncs.MsgBox(getApplicationContext(), "Error launching preferences :(",true);
                }

                break;

        }

        return false;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            setupActionBar();

            t1NameBox = (EditText)findViewById(R.id.editTxtTeam1Name);
            t2NameBox = (EditText)findViewById(R.id.editTxtTeam2Name);
            btnLoadGames = (Button)findViewById(R.id.btnLoadGame);

            //Load team names from preferences
            String t1Name = custFuncs.getPreference(this.getApplicationContext(), this.getResources().getString(R.string.pref_key_team1DefaultName), this.getResources().getString(R.string.pref_default_team1DefaultName));
            String t2Name = custFuncs.getPreference(this.getApplicationContext(), this.getResources().getString(R.string.pref_key_team2DefaultName), this.getResources().getString(R.string.pref_default_team2DefaultName));

            t1NameBox.setText(t1Name);
            t2NameBox.setText(t2Name);

            //Initialize Database
            dbHandler = new GameSaveDatabase(this);


        }
        catch(Exception e){
            custFuncs.MsgBox(this, e.getMessage(),true);
        }

    }

    private void checkForSavedGames() {
        if(new Game(this).checkForIncompleteGames()){
            btnLoadGames.setVisibility(View.VISIBLE);
        }
        else{
            btnLoadGames.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkForSavedGames();

    }

    private void setupActionBar() {

        actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);

    }


    private boolean validateTeamNames(String t1Name, String t2Name){

        if(t1Name.equals("")){
            custFuncs.vibrateDevice(this);
            custFuncs.MsgBox(this, getString(R.string.team1NameMissingMsg), true);
            return false;
        }
        else if(t2Name.equals("")){
            custFuncs.vibrateDevice(this);
            custFuncs.MsgBox(this, getString(R.string.team2NameMissingMsg), true);
            return false;
        }
        else
            return true;

    }

    public void startNewGame(View v){

        try{

            String t1Name = t1NameBox.getText().toString(), t2Name = t2NameBox.getText().toString();

            if(!validateTeamNames(t1Name, t2Name)){
                return;
            }
            else{

                /*Intent intent = new Intent(this, ScorePad.class);
                intent.putExtra(TEAM_1, t1Name);
                intent.putExtra(TEAM_2, t2Name);*/

                //Create New Game
                Game newGame = new Game(this, t1Name, t2Name);
                newGame.insertNewGameToDB();
                newGame.launchGame();


            }
        }
        catch(Exception e){
            custFuncs.MsgBox(this, e.getMessage(),true);
        }

    }

    public void openPlayStorePage(View v){

        try{
            //Create on the fly dialog

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.prePlayStoreRatingMessage))
                    .setCancelable(false)
                    .setNegativeButton(R.string.rateAppCancelBtnText, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            hateApp = true;
                        }
                    })
                    .setPositiveButton(R.string.rateAppOkBtnText, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            try{
                                //Get link from strings file
                                String pageURL = getString(R.string.playStoreURL);

                                //Set up intent
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(pageURL));

                                startActivity(intent);

                            }
                            catch(Exception e){
                                custFuncs.MsgBox(getApplicationContext(), getString(R.string.playStoreLoadErrorMsg), true);
                            }

                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();



        }
        catch(Exception e){}

        if(hateApp)
            custFuncs.MsgBox(this, getString(R.string.hateAppMsg), true);



    }

    public void sendFeedback(View v){

        String myEmail = getResources().getString(R.string.contactEmailAddress);
        String subject = getResources().getString(R.string.emailDefaultSubject);
        String title = getResources().getString(R.string.emailPickerDialogText);

        try{
            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.setType(getString(R.string.sendEmailIntentType));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{myEmail});
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);

            startActivity(Intent.createChooser(intent, title));

        }
        catch(Exception e){
            custFuncs.MsgBox(this, "Sorry for some reason your E-Mail app could not be launched. Please E-Mail me at " + myEmail + " so that I can fix this issue.", true);
        }

    }

}
