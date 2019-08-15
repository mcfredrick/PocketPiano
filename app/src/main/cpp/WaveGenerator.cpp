//
// Created by mattc on 8/14/2019.
//

#include "WaveGenerator.h"
#include <math.h>

WaveGenerator::WaveGenerator() {
    //on construction, the frequency defaults to 440 Hz
    mFrequency = 440.0;
    mPhase = 0.0;
    updateIncrement();
}

void WaveGenerator::setFrequency(double frequency) {
    mFrequency = frequency;
    //update the discrete phase increment to reflect the new frequency
    updateIncrement();
}

void WaveGenerator::setSampleRate(double sampleRate) {
    mSampleRate = sampleRate;
    //update the discrete phase increment to reflect the new sample rate
    updateIncrement();
}

double WaveGenerator::nextSample() {
    //calculate the amplitude of the wave at the current phase
    double value = sin(mPhase);
    //update the phase and keep it between 0 and 2*pi
    mPhase += mPhaseIncrement;
    while (mPhase >= twoPI) {
        mPhase -= twoPI;
    }
    return value;
}

void WaveGenerator::reset() {
    //set the phase back to 0. Called when the voice is released
    mPhase = 0.0;
}

void WaveGenerator::updateIncrement() {
    //set the discrete increment between samples
    mPhaseIncrement = mFrequency * 2 * mPI / mSampleRate;
}
