package adapters;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import customClasses.CUSTOM_FUNCTIONS;
import customClasses.Game;
import com.slistersoft.slistersoftspadesscorepad.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by slist on 10/1/2017.
 */

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHolder> {

    private List<Game> mGames;
    private Context callingContext;
    private CUSTOM_FUNCTIONS custFuncs;

    public class GameViewHolder extends RecyclerView.ViewHolder{

        public TextView tvT1Name, tvT2Name, tvT1Score, tvT2Score, tvDateStarted;
        public CardView rvGameCard;

        public GameViewHolder(View itemView){

            super(itemView);

            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.

            //region Initialize Game Card Controls

            rvGameCard = (CardView)itemView.findViewById(R.id.rvGameCard);
            tvT1Name = (TextView)itemView.findViewById(R.id.rv_tvTeam1Name);
            tvT2Name = (TextView)itemView.findViewById(R.id.rv_tvTeam2Name);
            tvT1Score = (TextView)itemView.findViewById(R.id.rv_tvT1ScoreDisplay);
            tvT2Score = (TextView)itemView.findViewById(R.id.rv_tvT2ScoreDisplay);
            tvDateStarted = (TextView)itemView.findViewById(R.id.rv_tvDateStartedLabel);

            //endregion

            rvGameCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Gets currently tapped game object and launch it
                    mGames.get(getAdapterPosition()).launchGame();

                }
            });

        }
    }

    public GameListAdapter(Context callingContext, ArrayList<Game> gamesList) {
        this.mGames = gamesList;
        this.callingContext = callingContext;
        custFuncs = new CUSTOM_FUNCTIONS(callingContext);
    }

    @Override
    public GameListAdapter.GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //Inflate Custom Game Card Layout
        View gameView = inflater.inflate(R.layout.recyclerview_game_row_layout, parent, false);

        //Return a new holder instance

        GameViewHolder vh = new GameViewHolder(gameView);

        return vh;

    }

    @Override
    public void onBindViewHolder(GameListAdapter.GameViewHolder viewHolder, int position) {

        try {
            //Get the data model based on position
            Game game = mGames.get(position);

            //Set layout controls based on game object

            //Set text on controls
            viewHolder.tvT1Name.setText(game.getTeam1Name());
            viewHolder.tvT2Name.setText(game.getTeam2Name());
            viewHolder.tvT1Score.setText(Integer.toString(game.getTeam1Score()));
            viewHolder.tvT2Score.setText(Integer.toString(game.getTeam2Score()));
            viewHolder.tvDateStarted.setText(game.getHumanDateStartedString());

        } catch (Exception e) {
            custFuncs.MsgBox(e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return mGames.size();
    }

    public Context getContext() {
        return callingContext;
    }

}
