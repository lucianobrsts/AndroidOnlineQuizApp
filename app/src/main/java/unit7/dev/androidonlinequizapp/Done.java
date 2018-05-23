package unit7.dev.androidonlinequizapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import unit7.dev.androidonlinequizapp.Common.Common;
import unit7.dev.androidonlinequizapp.Model.QuestionScore;

public class Done extends AppCompatActivity {

    Button btnTryAgain;
    TextView txtResultScore, getTxtResultQuestion;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference question_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        database = FirebaseDatabase.getInstance();
        question_score = database.getReference("Question_Score");

        txtResultScore = (TextView) findViewById(R.id.textTotalScore);
        getTxtResultQuestion = (TextView) findViewById(R.id.textTotalQuestion);
        progressBar = (ProgressBar) findViewById(R.id.doneProgressBar);
        btnTryAgain = (Button) findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Done.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
        //Pega o dado vindo do Bundle e seta na view
        Bundle extra = getIntent().getExtras();
        if(extra != null)
        {
            int score = extra.getInt("SCORE");
            int totalQuestion = extra.getInt("TOTAL");
            int correctAnswer = extra.getInt("CORRETAS");

            txtResultScore.setText(String.format("SCORE : %d", score));
            getTxtResultQuestion.setText(String.format("ANTERIOR : %d / %d", correctAnswer, totalQuestion));

            progressBar.setMax(totalQuestion);
            progressBar.setProgress(correctAnswer);

            //upload de pontos para DB
            question_score.child(String.format("%s_%s", Common.currentUser.getNome(),
                                                        Common.categoryId))
                    .setValue(new QuestionScore(String.format("%s_%s", Common.currentUser.getNome(),
                            Common.categoryId),
                            Common.currentUser.getNome(),
                            String.valueOf(score),
                            Common.categoryId, Common.getCategoryName));
        }
    }
}