package se.gameofthirty;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class hold the state of the game.
 *
 * It creates new dices, throws dices and keeps track of the status of the game,
 * round and round score, dices that the user decides to keep.
 *
 * This class implements parcelable in order to restore the sate of the game
 * in case of screen rotation.
 */

public class Thirty implements Parcelable {

    private Dice[] dices;
    private int mNbrOfRolls;
    private int mRoundScore;
    private int mRoundNbr;
    private boolean mGameOn;
    private int[] roundScores;
    private List<String> scoringMethodList;

    public Thirty() {
        newGame();
        scoringMethodList = new ArrayList<String>(
                Arrays.asList("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        );
        roundScores = new int[10];
        mRoundScore = 0;
        mRoundNbr = 10;
    }

    protected Thirty(Parcel in) {
        dices = new Dice[6];
        for (int i = 0; i < dices.length; i++) {
            dices[i] = new Dice();
            dices[i].setValue(in.readInt());
        }
        for(int i= 0; i < dices.length; i++) {
            int diceSaveStatus = in.readInt();
            if (diceSaveStatus == 1) {
                dices[i].setSaved(true);
            }
        }
        scoringMethodList = in.createStringArrayList();
        roundScores = new int[10];
        in.readIntArray(roundScores);
        mNbrOfRolls = in.readInt();
        mRoundScore = in.readInt();
        mRoundNbr = in.readInt();
        mGameOn = in.readByte() != 0;
    }

    public static final Creator<Thirty> CREATOR = new Creator<Thirty>() {
        @Override
        public Thirty createFromParcel(Parcel in) {
            return new Thirty(in);
        }

        @Override
        public Thirty[] newArray(int size) {
            return new Thirty[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.dices[0].getValue());
        dest.writeInt(this.dices[1].getValue());
        dest.writeInt(this.dices[2].getValue());
        dest.writeInt(this.dices[3].getValue());
        dest.writeInt(this.dices[4].getValue());
        dest.writeInt(this.dices[5].getValue());
        dest.writeInt(this.dices[0].getSavedStatus() ? 1 : 0);
        dest.writeInt(this.dices[1].getSavedStatus() ? 1 : 0);
        dest.writeInt(this.dices[2].getSavedStatus() ? 1 : 0);
        dest.writeInt(this.dices[3].getSavedStatus() ? 1 : 0);
        dest.writeInt(this.dices[4].getSavedStatus() ? 1 : 0);
        dest.writeInt(this.dices[5].getSavedStatus() ? 1 : 0);
        dest.writeStringList(scoringMethodList);
        dest.writeIntArray(roundScores);
        dest.writeInt(mNbrOfRolls);
        dest.writeInt(mRoundScore);
        dest.writeInt(mRoundNbr);
        dest.writeByte((byte) (mGameOn ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Starts a new Game by creating 6 dices with value 1
     */
    public void newGame() {
        dices = new Dice[6];
        for (int i = 0; i < dices.length; i++) {
            dices[i] = new Dice();
        }
        mNbrOfRolls = 0;
    }

    /**
     * Iterates through all the dices and rolls them
     * @return returns the number of times the dices have been thrown
     */
    public int rollDices() {
        for (Dice dice : dices) {
            dice.roll();
        }
        mNbrOfRolls++;
        return mNbrOfRolls;
    }

    public void setSavedStatus(int dieNbr, boolean value) {

        dices[dieNbr].setSaved(value);
    }

    public int getDiceValue(int diceNbr) {
        return dices[diceNbr].getValue();
    }

    public boolean getDiceSavedStatus(int diceNbr) {

        return dices[diceNbr].getSavedStatus();
    }

    public Dice[] getDices() {
        return dices;
    }

    public int getRoundScore() {
        return mRoundScore;
    }

    public boolean isGameOn() {
        return mGameOn;
    }

    /**
     * Sets the status of the game
     */
    public void setGameStatus(boolean value) {

        this.mGameOn = value;
    }

    public int getRoundNbr() {

        return mRoundNbr;
    }

    public int[] getRoundScores() {

        return roundScores;
    }

    /**
     * Gets the list of available scoring methods
     */
    public List<String> getScoringMethodList() {
        return scoringMethodList;
    }

    /**
     * Calculates the round score depending on which scoring method was chosen
     * */
    public void calculateScore(String spinnerValue) {
        List<Integer> diceValueList = new ArrayList<Integer>();
        for (Dice d : dices) {
            diceValueList.add(d.getValue());
        }
        int scoringMethod;
        if (spinnerValue.equals("Low")) {
            scoringMethod = 3;
        } else {
            scoringMethod = Integer.parseInt(spinnerValue);
        }
        mRoundScore = 0;
        if (scoringMethod == 3) {
            for (int dice : diceValueList) {
                if (dice == 1 || dice == 2 || dice == 3) {
                    mRoundScore += dice;
                }
            }
        } else {

            ArrayList<Integer> tempDiceList = new ArrayList<>();
            for (int dice : diceValueList) {
                //Removes dices that are higher than the selected mode
                if (dice <= scoringMethod) {
                    tempDiceList.add(dice);
                }
            }
            Collections.sort(tempDiceList,(dice1, dice2) -> dice2 - dice1);
            sumDiceValues(tempDiceList, 0, scoringMethod);
        }
        mRoundNbr--;
        roundScores[scoringMethod-3] = mRoundScore;
        //Removes the used scoring method from the scoringMethodList
        for (int i = 0; i < scoringMethodList.size() - 1; i++) {
            if (scoringMethodList.get(i).equals("Low") || scoringMethodList.get(i).equals(String.valueOf(scoringMethod))) {
                scoringMethodList.remove(i);
            }
        }
    }

    /**
     * Chooses the dice values that sums up to the scoringMethodValue.
     * First selects the value closest to the scoringMethodValue. Then, finds
     * the new value that makes the sum closer to the scoringMethodValue recursively.
     * If the sum is equal to the scoringMethodValue, removes the dice from the list and
     * puts the sum to zero.
     */

    private void sumDiceValues(List<Integer> diceValues, int sum, int scoringMethodValue) {

        if (sum == scoringMethodValue) {
            mRoundScore += sum;
            return;
        }

        for (int i = 0; i < diceValues.size(); i++) {
            if (sum + diceValues.get(i) <= scoringMethodValue) {
                sum += diceValues.get(i);
                diceValues.remove(i);
                sumDiceValues(diceValues, sum, scoringMethodValue);
                i = -1;
                sum = 0;
            }
        }
    }
}
