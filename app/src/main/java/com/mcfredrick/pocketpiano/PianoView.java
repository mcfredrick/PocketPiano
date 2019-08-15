package com.mcfredrick.pocketpiano;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Paint;
import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.WHITE;

public class PianoView extends View {

    static {
        System.loadLibrary("jni-bridge");
    }

    private native void touchEvent(int note, int keyOn);

    public static final int numOfDivs = 14; //Used to divide the screen into a set number of keys
    private Paint black, white, onColor;
    private ArrayList<Key> ivory = new ArrayList<>();
    private ArrayList<Key> ebony = new ArrayList<>();
    private int keyWidth, height;
    private int[] C = {12,24,36,48,60,72,84,96,108,120};

    public PianoView(Context context, AttributeSet attributes) {

        super(context, attributes);

        black = new Paint();
        black.setColor(BLACK);

        white = new Paint();
        white.setColor(WHITE);
        white.setStyle(Paint.Style.FILL);

        onColor = new Paint();
        onColor.setColor(BLUE);
        onColor.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){

        super.onSizeChanged(w, h, oldw, oldh);
        keyWidth = w / numOfDivs;
        height = h;
        int count = 15;

        //change whiteNums to a MIDI number corresponding to a C in a particular octave
        //An array of the possible C values is defined above such that C[4] for example,
        //corresponds to C4.
        //Thus, if refNote = C[4], the first (leftmost) key is C4.
        int refNote=C[4];
        int whiteNums=refNote;
        int blackNums=whiteNums+1;

        for (int i = 0; i < numOfDivs; i++){
            int left = i * keyWidth;
            int right = left + keyWidth;

            if (i == numOfDivs - 1){
                right = w;
            }

            RectF rect = new RectF(left, 0, right, h);

            //don't use the numbers that correspond to the black keys
            while (whiteNums == refNote+1 || whiteNums == refNote+3 || whiteNums == refNote+6
                    || whiteNums == refNote+8 || whiteNums == refNote+10 || whiteNums == refNote+13
                    || whiteNums == refNote+15 || whiteNums == refNote+18 || whiteNums == refNote+20
                    || whiteNums == refNote+22)
            {
                whiteNums++;
            }
                ivory.add(new Key(rect, whiteNums));
                whiteNums++;


            if (i != 0 && i != 3 && i != 7 && i != 10){
                rect = new RectF((float)(i-1)* keyWidth + 0.5f * keyWidth + 0.25f * keyWidth, 0,
                        (float) i * keyWidth + 0.25f * keyWidth, 0.67f * height);

                //skip the numbers assigned to the white keys
                while (blackNums != refNote+1 && blackNums != refNote+3 && blackNums != refNote+6
                        && blackNums != refNote+8 && blackNums != refNote+10 && blackNums != refNote+13
                        && blackNums != refNote+15 && blackNums != refNote+18 && blackNums != refNote+20
                        && blackNums != refNote+22)
                {
                    blackNums++;
                }

                ebony.add(new Key(rect, blackNums));
                blackNums ++;
            }

        }

    }

    @Override
    protected void onDraw(Canvas canvas){

        for (Key k : ivory){
            canvas.drawRect(k.rect, k.keyOn ? onColor : white);
        }

        for (int i = 1; i < numOfDivs; i++){
            canvas.drawLine(i * keyWidth, 0, i * keyWidth, height, black);
        }

        for (Key k : ebony){
            canvas.drawRect(k.rect, k.keyOn ? onColor : black);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        boolean isDownAction = action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE;

        for (int touchIndex = 0; touchIndex < event.getPointerCount(); touchIndex++){
            float x = event.getX(touchIndex);
            float y = event.getY(touchIndex);

            Key k = keyAtCoords(x,y); //select the key at the current coordinates

            if (k != null) {
                k.keyOn = isDownAction; //the key is on if the action is DOWN or MOVE as above
                int keyOn = k.keyOn ? 1 : 0; //cast the boolean to int to play nicely with C++
                touchEvent(k.keyID, keyOn); //send the action and the Key ID to JNI bridge
                super.onTouchEvent(event); //send the event to the parent class
                invalidate(); //redraw the piano view
            }

        }

/*
        ArrayList<Key> tmp = new ArrayList<>(ivory);
        tmp.addAll(ebony);

        for (Key k : tmp){
                    int keyOn = k.keyOn ? 1 : 0; //cast the boolean to int to play nicely with C++
                    touchEvent(k.keyID, keyOn); //send the action and the Key ID to JNI bridge
                    super.onTouchEvent(event); //send the event to the parent class
                    invalidate(); //redraw the PianoView
        }
*/
        return true;

    }


    //determine which key is at the given coordinates and return the Key, k
    private Key keyAtCoords(float x, float y){

        for (Key k : ebony){

            if (k.rect.contains(x,y)){

                return k;

            }
        }

        for (Key k : ivory){

            if (k.rect.contains(x,y)){

                return k;

            }
        }

        return null;
    }

}
