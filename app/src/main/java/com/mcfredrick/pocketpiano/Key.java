package com.mcfredrick.pocketpiano;

import android.graphics.RectF;

public class Key {

    public int keyID; //Which sound associated with key?
    public RectF rect; //Define the rectangle for each key graphic
    public boolean keyOn; //Is the key being pressed? (down)

    public Key(RectF rect, int keyID) {
        this.keyID = keyID;
        this.rect = rect;
    }

}
