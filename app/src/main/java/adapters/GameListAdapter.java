package adapters;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.slistersoft.slistersoftspadesscorepad.Game;
import com.slistersoft.slistersoftspadesscorepad.R;
import java.util.ArrayList;

/**
 * Created by slist on 10/1/2017.
 */

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.GameViewHolder> {

    private ArrayList<Game> mGames = new ArrayList<>();
    private Context callingContext;

    public GameListAdapter(Context callingContext, ArrayList<Game> gamesList) {
        this.mGames = gamesList;
        this.callingContext = callingContext;
    }

    @Override
    public GameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //Inflate Custom Game Card Layout
        View gameView = inflater.inflate(R.layout.recyclerview_game_row_layout, parent, false);

        //Return a new holder instance
        GameViewHolder gameViewHolder = new GameViewHolder(gameView);
        return gameViewHolder;

    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {

        //Get the data model based on position
        Game game = mGames.get(position);

        //Set layout controls based on game object
        TextView tvT1Name, tvT2Name, tvT1Score, tvT2Score, tvDateStarted;

        //Get controls from GameViewHolder
        tvT1Name = holder.tvT1Name;
        tvT2Name = holder.tvT2Name;
        tvT1Score = holder.tvT1Score;
        tvT2Score = holder.tvT2Score;
        tvDateStarted = holder.tvDateStarted;

        //Set text on controls
        tvT1Name.setText(game.getTeam1Name());
        tvT2Name.setText(game.getTeam2Name());
        tvT1Score.setText(game.getTeam1Score());
        tvT2Score.setText(game.getTeam2Score());
        tvDateStarted.setText(game.getHumanDateStartedString());

    }

    @Override
    public int getItemCount() {
        return mGames.size();
    }

    public Context getCallingContext() {
        return callingContext;
    }

    public class GameViewHolder extends RecyclerView.ViewHolder{

        public TextView tvT1Name, tvT2Name, tvT1Score, tvT2Score, tvDateStarted;

        public GameViewHolder(View itemView){

            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            //region Initialize Game Card Controls

            tvT1Name = (TextView)itemView.findViewById(R.id.rv_tvTeam1Name);
            tvT2Name = (TextView)itemView.findViewById(R.id.rv_tvTeam2Name);
            tvT1Score = (TextView)itemView.findViewById(R.id.rv_tvT1ScoreDisplay);
            tvT2Score = (TextView)itemView.findViewById(R.id.rv_tvT2ScoreDisplay);
            tvDateStarted = (TextView)itemView.findViewById(R.id.rv_tvDateStartedLabel);

            //endregion

        }




    }
}
