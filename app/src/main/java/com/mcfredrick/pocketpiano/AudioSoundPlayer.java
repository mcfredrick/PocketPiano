package com.mcfredrick.pocketpiano;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.SparseArray;

import java.io.InputStream;

public class AudioSoundPlayer {

    private SparseArray<PlayThread> threadMap = null;
    private Context context;
    private static final SparseArray<String> SOUND_MAP = new SparseArray<>();
    public static final int MAX_VOLUME = 100, CURRENT_VOLUME = 90;

    static{

        //white key sounds
        SOUND_MAP.put(1, "C4");
        SOUND_MAP.put(2, "D4");
        SOUND_MAP.put(3, "E4");
        SOUND_MAP.put(4, "F4");
        SOUND_MAP.put(5, "G4");
        SOUND_MAP.put(6, "A4");
        SOUND_MAP.put(7, "B4");
        SOUND_MAP.put(8, "C5");
        SOUND_MAP.put(9, "D5");
        SOUND_MAP.put(10, "E5");
        SOUND_MAP.put(11, "F5");
        SOUND_MAP.put(12, "G5");
        SOUND_MAP.put(13, "A5");
        SOUND_MAP.put(14, "B5");

        //black key sounds
        SOUND_MAP.put(15, "C#4");
        SOUND_MAP.put(16, "D#4");
        SOUND_MAP.put(17, "F#4");
        SOUND_MAP.put(18, "G#4");
        SOUND_MAP.put(19, "A#4");
        SOUND_MAP.put(20, "C#5");
        SOUND_MAP.put(21, "D#5");
        SOUND_MAP.put(22, "F#5");
        SOUND_MAP.put(23, "G#5");
        SOUND_MAP.put(24, "A#5");

        }

        public AudioSoundPlayer(Context context) {

            this.context = context;
            threadMap = new SparseArray<>();

        }

        public void playNote(int note) {

            if (!isNotePlaying(note)) {
                PlayThread thread = new PlayThread(note);
                thread.start();
                threadMap.put(note, thread);
            }

        }

        public void stopNote(int note) {

            PlayThread thread = threadMap.get(note);

            if (thread != null) {
                threadMap.remove(note);
            }

        }

        public boolean isNotePlaying(int note) {

            return threadMap.get(note) != null;

        }

        private class PlayThread extends Thread {

            int note;
            AudioTrack audioTrack;

            public PlayThread(int note) {

                this.note = note;

            }

            @Override
            public void run() {

                try {
                    String path = SOUND_MAP.get(note) + ".wav";
                    AssetManager assetManager = context.getAssets();
                    AssetFileDescriptor ad = assetManager.openFd(path);
                    long fileSize = ad.getLength();
                    int bufferSize = 4096;
                    byte[] buffer = new byte[bufferSize];

                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

                    float logVolume = (float) (1 - (Math.log(MAX_VOLUME - CURRENT_VOLUME) / Math.log(MAX_VOLUME)));
                    audioTrack.setStereoVolume(logVolume, logVolume);

                    audioTrack.play();
                    InputStream audioStream = null;
                    int headerOffset = 0x2C;
                    long bytesWritten = 0;
                    int bytesRead = 0;

                    audioStream = assetManager.open(path);
                    audioStream.read(buffer, 0, headerOffset);

                    while (bytesWritten < fileSize - headerOffset) {

                        bytesRead = audioStream.read(buffer, 0, bufferSize);
                        bytesWritten += audioTrack.write(buffer, 0, bytesRead);

                    }

                    audioTrack.stop();
                    audioTrack.release();

                    } catch (Exception e) {
                    }    finally {
                        if (audioTrack != null) {
                            audioTrack.release();
                        }
                    }
                }

            }



}
