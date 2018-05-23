package unit7.dev.androidonlinequizapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import unit7.dev.androidonlinequizapp.Common.Common;
import unit7.dev.androidonlinequizapp.Interface.ItemClickListener;
import unit7.dev.androidonlinequizapp.Interface.RankingCallBack;
import unit7.dev.androidonlinequizapp.Model.QuestionScore;
import unit7.dev.androidonlinequizapp.Model.Ranking;
import unit7.dev.androidonlinequizapp.ViewHolder.RankingViewHolder;


public class RankingFragment extends Fragment {

    View myFragment;

    RecyclerView rankingList;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<Ranking, RankingViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference questionScore, rankingTbl;

    int sum = 0;

    public static RankingFragment newInstance() {
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        questionScore = database.getReference("Question_Score");
        rankingTbl = database.getReference("Ranking");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.fragment_ranking, container, false);

        //Init View
        rankingList = (RecyclerView) myFragment.findViewById(R.id.rankingList);
        layoutManager = new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);

        //Porque o método OrderByChild do firebase ordenará a lista ascendendo
        //Então precisamos reverter nosso Recycler data
        //Para LayoutManager

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);


        //Implementando CallBack
        updateScore(Common.currentUser.getNome(), new RankingCallBack<Ranking>() {
            @Override
            public void callBack(Ranking ranking) {
                //Update para a tabela ranking
                rankingTbl.child(ranking.getUserName()).setValue(ranking);
                
                //showRanking(); //Depois do upload, precisamos organizar a tabela Ranking e mostrar resultados
            }
        });

        //Setando Adapter
        adapter = new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(
            Ranking.class,
            R.layout.layout_ranking,
            RankingViewHolder.class,
            rankingTbl.orderByChild("score")

        ) {

            @Override
            protected void populateViewHolder(RankingViewHolder viewHolder, Ranking model, int position) {

                viewHolder.txt_name.setText(model.getUserName());
                viewHolder.txt_score.setText(String.valueOf(model.getScore()));

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);

        return myFragment;
    }

    private void updateScore(final String nome, final RankingCallBack<Ranking> callBack) {
        questionScore.orderByChild("user").equalTo(nome).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren())
                {
                    QuestionScore ques = data.getValue(QuestionScore.class);
                    sum += Integer.parseInt(ques.getScore());
                }

                //Depois de somar todos os escores, precisamos processar a variável sum aqui
                //Porque firebase é assincrono db, então se processar fora, nosso valor de 'sum'
                //será resetado para zero.
                Ranking ranking = new Ranking(nome, sum);
                callBack.callBack(ranking);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
