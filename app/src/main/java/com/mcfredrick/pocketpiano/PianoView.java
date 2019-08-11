package com.mcfredrick.pocketpiano;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Paint;
import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.WHITE;

public class PianoView extends View {

    public static final int numOfDivs = 14; //Used to divide the screen into a set number of keys
    private Paint black, white, onColor;
    private ArrayList<Key> ivory = new ArrayList<>();
    private ArrayList<Key> ebony = new ArrayList<>();
    private int keyWidth, height;
    private AudioSoundPlayer soundPlayer;

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

        soundPlayer = new AudioSoundPlayer(context);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){

        super.onSizeChanged(w, h, oldw, oldh);
        keyWidth = w / numOfDivs;
        height = h;
        int count = 15;

        for (int i = 0; i < numOfDivs; i++){
            int left = i * keyWidth;
            int right = left + keyWidth;

            if (i == numOfDivs - 1){
                right = w;
            }

            RectF rect = new RectF(left, 0, right, h);
            ivory.add(new Key(rect, i + 1));

            if (i != 0 && i != 3 && i != 7 && i != 10){
                rect = new RectF((float)(i-1)* keyWidth + 0.5f * keyWidth + 0.25f * keyWidth, 0,
                        (float) i * keyWidth + 0.25f * keyWidth, 0.67f * height);
                ebony.add(new Key(rect, count));
                count ++;
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

            Key k = keyAtCoords(x,y);

            if (k != null) {
                k.keyOn = isDownAction;
            }

        }


        ArrayList<Key> tmp = new ArrayList<>(ivory);
        tmp.addAll(ebony);

        for (Key k : tmp){
            if (k.keyOn){
                if (!soundPlayer.isNotePlaying(k.sound)){
                    soundPlayer.playNote(k.sound);
                    invalidate(); //redraw the PianoView
                } else {
                    releaseKey(k);
                }
            } else {
                soundPlayer.stopNote(k.sound);
                releaseKey(k);
            }
        }

        return true;

    }

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

    private void releaseKey(final Key k){
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {
                k.keyOn = false;
                handler.sendEmptyMessage(0);

            }
        }, 100);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg){
            invalidate();

        }
    };


}
