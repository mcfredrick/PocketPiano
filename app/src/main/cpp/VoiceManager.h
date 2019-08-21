//
// Created by mattc on 8/14/2019.
//

#ifndef POCKETSYNTH_VOICEMANAGER_H
#define POCKETSYNTH_VOICEMANAGER_H

#include "Voice.h"

class VoiceManager {
public:

    void setSampleRate(int sampleRate); //store the global sample rate and pass it to each voice
    void onNoteOn(int noteNumber);      //find a free voice and turn it on
    void onNoteOff(int noteNumber);     //find the correct voice and release it
    double nextSample();                //sum the voices and return a sample
    void render (float *audioData, int numFrames); //fill the buffer with the requested # of samples

private:

    int mSampleRate;
    static const int NumberOfVoices = 16;   //sets the total number of voices allowed by the synth
    Voice voices[NumberOfVoices];           //create all of the voices

    Voice* findFreeVoice();                 //find a voice that's not currently in use

};


#endif //POCKETSYNTH_VOICEMANAGER_H
