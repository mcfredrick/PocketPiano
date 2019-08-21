package com.mcfredrick.pocketsynth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SynthActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("jni-bridge");
    }

    private native void startEngine();

    private native void stopEngine();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // start the audio engine
        startEngine();
    }

    @Override
    public void onDestroy(){

        //stop the audio engine
        stopEngine();
        super.onDestroy();
    }
}
