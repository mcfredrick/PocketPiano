//
// Created by mattc on 8/14/2019.
//

#include "Voice.h"

//constructor sets the note number inaudibly low just in case
//voice is initialized as off
Voice::Voice(){
    mNoteNumber = -1;
    mIsActive = false;
}

void Voice::setNoteNumber(int noteNumber) {
    mNoteNumber = noteNumber;
    //set the frequency relative to A 440 Hz.
    //this could change for something other than concert tuning
    double frequency = 440.0 * pow(2.0, (mNoteNumber - 69.0) / 12.0);
    mWaveGenerator.setFrequency(frequency);
}

double Voice::nextSample(){
    //if the voice is off, we don't bother going to the wave generator
    //just return a 0
    if (!mIsActive) return 0.0;
    //otherwise, call to the wave generator for the next sample
    return mWaveGenerator.nextSample();
}

void Voice::setFree(){
    //called by the manager to turn the voice off
    mIsActive = false;
}

void Voice::setSampleRate(int sampleRate){
    //take the global sample rate and pass it on
    //sample rate originates from the audio engine
    mSampleRate = sampleRate;
    mWaveGenerator.setSampleRate(mSampleRate);
}

void Voice::reset(){
    //set the note number back to default
    mNoteNumber = -1;
    //reset the wave generator also
    mWaveGenerator.reset();
}