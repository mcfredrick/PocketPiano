//
// Created by mcfre on 7/23/2019.
//

#ifndef POCKETPIANO_AUDIOENGINE_H
#define POCKETPIANO_AUDIOENGINE_H

#include "aaudio/AAudio.h"
#include "VoiceManager.h"

//A general purpose audio engine built from the aaudio library
//Modified with calls to the voice manager for this synth
class AudioEngine {
public:
    bool start();   //starts the audio engine
    void stop();    //stops the audio engine
    void restart(); //restarts the audio engine
    void setToneOn(int keyId, bool isToneOn); //called by the jni bridge to turn voices on/off

private:
    VoiceManager mVoiceManager;         //create and instance of the voice manager class
    AAudioStream *mStream = nullptr;    //initialize the audio stream without and address
};


#endif //POCKETPIANO_AUDIOENGINE_H
