package se.gameofthirty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private TextView mTotalScore;
    private TextView[] mTextViews;
    private Button mStartNewGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mTextViews = new TextView[]{
                (TextView) findViewById(R.id.c1),
                (TextView) findViewById(R.id.c2),
                (TextView) findViewById(R.id.c3),
                (TextView) findViewById(R.id.c4),
                (TextView) findViewById(R.id.c5),
                (TextView) findViewById(R.id.c6),
                (TextView) findViewById(R.id.c7),
                (TextView) findViewById(R.id.c8),
                (TextView) findViewById(R.id.c9),
                (TextView) findViewById(R.id.c10)
        };
        mTotalScore = (TextView) findViewById(R.id.total_score);

        // Writes the points for every round and calculates the total score
        Intent intent = getIntent();
        int totalScore = 0;
        for (int i = 3; i < 13; i++) {
            String key = String.valueOf(i);
            int value = intent.getIntExtra(key, -1);
            if (value >= 0) {
                if (i == 3) {
                    mTextViews[i-3].setText("The scoring Method " + "Low" + ": " + String.valueOf(value));
                } else {
                    mTextViews[i-3].setText("The scoring Method " + key + ": " + String.valueOf(value));
                }
                totalScore += value;
            } else {
                mTextViews[i-3].setText("");
            }
        }
        mTotalScore.setText("Total Score: " + String.valueOf(totalScore));

        //This button is used to start a new game
        mStartNewGameButton = (Button) findViewById(R.id.restart_button);
        mStartNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
}