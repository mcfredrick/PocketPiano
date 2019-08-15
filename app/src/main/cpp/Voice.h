//
// Created by mattc on 8/14/2019.
//

#ifndef POCKETPIANO_VOICE_H
#define POCKETPIANO_VOICE_H

#include "WaveGenerator.h"
#include <math.h>

class Voice {
public:

    //allowing Voice class to access VoiceManager class private assets.
    //this reduces the required number of setter functions to take values
    //from the manager
    friend class VoiceManager;

    Voice(); //constructor sets the initial note number and and makes sure the voice is off

    void setNoteNumber(int noteNumber); //convert note # to frequency and pass to wave generator
    double nextSample();                //returns the next sample from the wave generator
    void setFree();                     //turn the voice off - now available as free voice
    void setSampleRate(int sampleRate); //set from global sample rate and pass to wave generator
    void reset();                       //set the note number back to -1 and reset the wave generator

    int mSampleRate = 44100;            //initialize to most common just in case

private:

    WaveGenerator mWaveGenerator;       //member wave generator instance for this voice

    int mNoteNumber;                    //stores the current note number
    bool mIsActive;                     //true - active / false - not active

};


#endif //POCKETPIANO_VOICE_H
