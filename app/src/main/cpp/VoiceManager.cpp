//
// Created by mattc on 8/14/2019.
//

#include "VoiceManager.h"


void VoiceManager::setSampleRate(int sampleRate){
    mSampleRate = sampleRate;
    //pass the global sample rate to each voice
    for (int i = 0; i < NumberOfVoices; i++) {
        voices[i].setSampleRate(mSampleRate);
    }
}

void VoiceManager::onNoteOn(int noteNumber) {
    //find a free voice
    Voice* voice = findFreeVoice();
    //if all voices are in use, run away
    //pressing a new key when all voices are active will do nothing
    if (!voice) {
        return;
    }
    //reset, set frequency, and activate the voice if available
    voice->reset();
    voice->setNoteNumber(noteNumber);
    voice->mIsActive = true;
}

void VoiceManager::onNoteOff(int noteNumber) {
    //find the voice(s) with the given noteNumber:
    for (int i = 0; i < NumberOfVoices; i++) {
        Voice& voice = voices[i];
        //when the key is released, this should release the voice
        if (voice.mIsActive && voice.mNoteNumber == noteNumber) {
            voice.setFree();
        }
    }
}

double VoiceManager::nextSample() {
    //set output to zero to start fresh on a new sample
    double output = 0.0;
    //sum all of the current voices and add it to the output value
    for (int i = 0; i < NumberOfVoices; i++) {
        Voice& voice = voices[i];
        output += voice.nextSample();
    }
    return output;
}

void VoiceManager::render (float *audioData, int numFrames){
    //fill the buffer
    for (int i = 0; i < numFrames; i++) {
        audioData[i]=nextSample();
    }
}

Voice* VoiceManager::findFreeVoice() {
    //select the first voice that is inactive and return it
    Voice* freeVoice = NULL;
    for (int i = 0; i < NumberOfVoices; i++) {
        if (!voices[i].mIsActive) {
            freeVoice = &(voices[i]);
            break;
        }
    }
    return freeVoice;
}
