//
// Created by mattc on 8/14/2019.
//

#ifndef POCKETPIANO_WAVEGENERATOR_H
#define POCKETPIANO_WAVEGENERATOR_H

#include <math.h>

class WaveGenerator {
public:

    WaveGenerator(); //sets a default frequency and initializes the phase and phase increment

    void setFrequency(double frequency);    //frequency set by voice class when key is pressed
    void setSampleRate(double sampleRate);  //global sample rate gets passed in from the audio engine
    double nextSample();                    //return the next sample from the wave
    void reset();                           //set the phase back to 0.0

private:

    const double mPI = M_PI;       //pi constant to be used in calculations
    const double twoPI = 2*M_PI;   //2*pi to be used in calculations
    double mFrequency;             //frequency to be set by the voice class when activated
    double mPhase;                 //current phase of the wave
    double mSampleRate = 44100;    //sample rate defaults to most common
    double mPhaseIncrement;        //discrete increment in phase per sample

    void updateIncrement();        //change the phase increment to reflect sample rate or frequency

};


#endif //POCKETPIANO_WAVEGENERATOR_H
