package activityClasses;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import customClasses.CUSTOM_FUNCTIONS;
import customClasses.Game;
import com.slistersoft.slistersoftspadesscorepad.R;

import java.util.ArrayList;

import adapters.GameListAdapter;

public class GamePickerActivity extends AppCompatActivity {

    ArrayList<Game> games;
    RecyclerView rv;
    CUSTOM_FUNCTIONS custFuncs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_picker);

        try {
            custFuncs = new CUSTOM_FUNCTIONS(this);
            rv = (RecyclerView)findViewById(R.id.gamePickerRecyclerView);
        } catch (Exception e) {
            custFuncs.MsgBox(e.getMessage());
            e.printStackTrace();
        }

       populateRecyclerView();

    }

    private void populateRecyclerView(){

        try {
            games = new Game(this).getInCompleteGames();

            GameListAdapter gameAdapter = new GameListAdapter(this, games);

            rv.setAdapter(gameAdapter);
            rv.setLayoutManager(new LinearLayoutManager(this));

        } catch (Exception e) {
            custFuncs.MsgBox(e.getMessage());
            e.printStackTrace();
        }

    }
}
