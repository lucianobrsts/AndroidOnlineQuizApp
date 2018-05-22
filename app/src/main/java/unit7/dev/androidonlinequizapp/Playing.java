package unit7.dev.androidonlinequizapp;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import unit7.dev.androidonlinequizapp.Common.Common;

public class Playing extends AppCompatActivity implements View.OnClickListener{

    final static long INTERVAL = 1000; // 1 segundo
    final static long TIMEOUT = 7000; // 7 segundos
    int progressValue = 0;

    CountDownTimer mCountDown;

    int index=0, score=0, thisQuestion=0, totalQuestion, correctAnswer;

    ProgressBar progressBar;
    ImageView question_image;
    Button btnA, btnB, btnC, btnD;

    TextView txtScore, txtQuestionNum, question_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        //Views
        txtScore = (TextView) findViewById(R.id.txtScore);
        txtQuestionNum = (TextView) findViewById(R.id.txtTotalQuestion);
        question_txt = (TextView) findViewById(R.id.question_text);
        question_image = (ImageView) findViewById(R.id.question_image);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnA = (Button) findViewById(R.id.btnAswerA);
        btnB = (Button) findViewById(R.id.btnAswerB);
        btnC = (Button) findViewById(R.id.btnAswerC);
        btnD = (Button) findViewById(R.id.btnAswerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        mCountDown.cancel();
        if(index < totalQuestion) // Se ainda há questões na lista
        {
            Button clickedButton = (Button) view;
            if(clickedButton.getText().equals(Common.questionList.get(index).getCorrectAnswer()))
            {
                //Escolher pergunta correta
                score += 10;
                correctAnswer++;
                showQuestion(++index); //próxima questão
            }
            else
            {
                //Escolher pergunta errada
                Intent intent = new Intent(this, Done.class);
                Bundle dataSend = new Bundle();
                dataSend.putInt("SCORE", score);
                dataSend.putInt("TOTAL", totalQuestion);
                dataSend.putInt("CORRECT", correctAnswer);
                intent.putExtras(dataSend);
                startActivity(intent);
                finish();
            }
            txtScore.setText(String.format("%d", score));
        }
    }

    private void showQuestion(int index) {
        if(index < totalQuestion)
        {
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d / %d", thisQuestion, totalQuestion));
            progressBar.setProgress(0);
            progressValue = 0;

            if(Common.questionList.get(index).getIsImageQuestion().equals("true"))
            {
                //Se é imagem
                Picasso.with(getBaseContext())
                        .load(Common.questionList.get(index).getQuestion())
                        .into(question_image);
                question_image.setVisibility(View.VISIBLE);
                question_txt.setVisibility(View.INVISIBLE);
            }
            else
            {
                question_txt.setText(Common.questionList.get(index).getQuestion());

                //Se a questão é texto, setamos a imagem invisível
                question_image.setVisibility(View.INVISIBLE);
                question_txt.setVisibility(View.VISIBLE);
            }
            btnA.setText(Common.questionList.get(index).getAnswerA());
            btnB.setText(Common.questionList.get(index).getAnswerB());
            btnC.setText(Common.questionList.get(index).getAnswerC());
            btnD.setText(Common.questionList.get(index).getAnswerD());

            mCountDown.start(); //Começa a contar tempo.
        }
        else
        {
            //Se é a ultima questão
            Intent intent = new Intent(this, Done.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE", score);
            dataSend.putInt("TOTAL", totalQuestion);
            dataSend.putInt("CORRECT", correctAnswer);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        totalQuestion = Common.questionList.size();

        mCountDown = new CountDownTimer(TIMEOUT, INTERVAL) {

            @Override
            public void onTick(long minisec) {
                progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                mCountDown.cancel();
                showQuestion(++index);
            }
        };
        showQuestion(index);
    }
}
