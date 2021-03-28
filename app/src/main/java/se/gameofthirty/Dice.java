package se.gameofthirty;

/**
 * THis class represents a dice with the value mValue(1-6) and the status of the dice
 * whether it is saved or not
 * */

public class Dice {

    private int mValue;
    private boolean mSaved;

    public Dice() {
        this.mValue = 1;
        this.mSaved = false;
    }

    /**
     * Roll a dice and randomly selects a value for it between 1 and 6
     * */
    public void roll() {
        if (!mSaved)
            this.mValue = (int) (Math.random() * 6) + 1;
    }

    public int getValue() {
        return this.mValue;
    }

    public void setValue(int value) { this.mValue = value;}

    public void setSaved(boolean value) {
        this.mSaved = value;
    }

    public boolean getSavedStatus() {
        return this.mSaved;
    }


}
