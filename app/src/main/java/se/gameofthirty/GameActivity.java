package se.gameofthirty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private Thirty mGame;
    private Button mRollButton;
    private Button mCollectPointsButton;
    private Spinner mSpinner;
    private int[] diceIds;
    private int[] whiteColorId;
    private int[] greyColorId;
    ArrayAdapter<String> mAdapter;
    private TextView mRoundScore;
    private TextView mRoundsLeft;
    private static final String GAME_STATE = "GameState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mGame = new Thirty();
        initIds();
        assignDiceIds();
        diceClickListeners();
        if (savedInstanceState != null) {
            mGame = savedInstanceState.getParcelable(GAME_STATE);
        }
        mRoundsLeft.setText("Rounds Left: " + mGame.getRoundNbr());
        updateSpinner();
        updateDices();
        throwDices();
        collectRoundPoints();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(GAME_STATE, this.mGame);
        super.onSaveInstanceState(outState);
    }

    /**
     * assign the ids of some of the view components to the variables
     */
    public void initIds() {
        mRoundsLeft = (TextView) findViewById(R.id.numberOfRoundsLeft);
        mRoundScore = (TextView) findViewById(R.id.roundPoints);
        mCollectPointsButton = (Button) findViewById(R.id.collectButton);
        mRollButton = (Button) findViewById(R.id.rollButton);
        mSpinner = (Spinner) findViewById(R.id.spinner);
    }

    /**
     * When the button Collect Points is clicked the points of the chosen dices
     * are calculated
     */
    private void collectRoundPoints() {
        mCollectPointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGame.isGameOn()) return;
                String scoringMethod = mSpinner.getSelectedItem().toString();
                mGame.calculateScore(scoringMethod);
                mRoundScore.setText("Round points:" + Integer.toString(mGame.getRoundScore()));
                startNewRound();
            }
        });
    }

    /**
     * Throws the dices when the button Roll is pressed
     */
    private void throwDices() {
        mRollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGame.setGameStatus(true);
                int nbrOfRolls = mGame.rollDices();
                updateDices();
                if (nbrOfRolls >= 3) {
                    mRollButton.setEnabled(false);
                }
                mRoundScore.setText("Round points:");
                mCollectPointsButton.setEnabled(true);
            }
        });
    }

    /**
     * Updates the spinner
     */
    private void updateSpinner() {
        List<String> scoringList = new ArrayList<>(mGame.getScoringMethodList());
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, scoringList);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);
    }

    /**
     * Puts the ids of different colors and dices in arrays
     */
    private void assignDiceIds() {
        diceIds = new int[]{R.id.dice1, R.id.dice2, R.id.dice3, R.id.dice4, R.id.dice5, R.id.dice6};
        greyColorId = new int[]{R.drawable.grey1, R.drawable.grey2, R.drawable.grey3,
                R.drawable.grey4, R.drawable.grey5, R.drawable.grey6};
        whiteColorId = new int[]{R.drawable.white1, R.drawable.white2, R.drawable.white3,
                R.drawable.white4, R.drawable.white5, R.drawable.white6};
    }

    /**
     * Create ClickListeners for the dices
     */
    private void diceClickListeners() {
        for (int i = 0; i < 6; i++) {
            ImageView dieImg = (ImageView) findViewById(diceIds[i]);
            final int INDEX = i;
            dieImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mGame.isGameOn())
                        return;
                    boolean diceStatus = mGame.getDiceSavedStatus(INDEX);
                    if (diceStatus) {
                        mGame.setSavedStatus(INDEX, false);
                    } else {
                        mGame.setSavedStatus(INDEX, true);
                    }
                    updateDices();
                }
            });
        }
    }

    /**
     * Updates the dices when they are thrown and changes the color
     * of the dices depending if they were saved or not saved
     */
    private void updateDices() {
        int count = 0;
        for (Dice d : mGame.getDices()) {
            int diceValue = d.getValue();
            boolean diceStatus = d.getSavedStatus();
            ImageView dice = (ImageView) findViewById(diceIds[count]);
            if (diceStatus) {
                dice.setImageResource(greyColorId[diceValue - 1]);
            } else {
                dice.setImageResource(whiteColorId[diceValue - 1]);
            }
            count++;
        }
    }

    /**
     * Starts a new Round of game with 3 rolls possibility
     */
    private void startNewRound() {
        mGame.newGame();
        mGame.setGameStatus(false);
        updateSpinner();
        updateDices();
        mRoundsLeft.setText("Rounds Left: " + mGame.getRoundNbr());
        mRollButton.setEnabled(true);
        mCollectPointsButton.setEnabled(false);
        if (mGame.getRoundNbr() <= 0) {
            mRollButton.setEnabled(false);
            showResults();
        }
    }

    /**
     * Creates new Intent and moves to ResultActivity when the Game is over
     */
    private void showResults() {
        int[] gameResults = mGame.getRoundScores();
        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        for (int i = 3; i < 13; i++) {
            String key = String.valueOf(i);
            intent.putExtra(key, gameResults[i-3]);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.finish();
        startActivity(intent);

    }
}