//
// Created by mcfre on 7/23/2019.
//

#include "AudioEngine.h"
#include <android/log.h>
#include <thread>
#include <mutex>

//Set the buffer size - using 2 for compromise between latency and glitches
constexpr int kBufferSizeInBursts = 2;

//use the data callback function from the aaudio library
aaudio_data_callback_result_t dataCallback(AAudioStream *stream, //a reference provided by AAudioStreamBuilder_openStream()
                                            void *userData,      //the same address that was passed to AAudioStreamBuilder_setCallback()
                                            void *audioData,     //a pointer to the audio data
                                            int numFrames)       //the number of frames to be processed, which can vary
{
    //recast the userData to a VoiceManager pointer
    //pass its render function audioData as a pointer to a float and ask for a number of frames of data
    //the render function loops through the audioData and sets it to the current sample
    ((VoiceManager*) (userData))->render(static_cast<float *>(audioData), numFrames);
    return AAUDIO_CALLBACK_RESULT_CONTINUE;
}

//use the error callback function from the aaudio library
void errorCallback(AAudioStream *stream, //reference provided by AAudioStreamBuilder_openStream()
                    void *userData,      //the same address that was passed to AAudioStreamBuilder_setErrorCallback()
                    aaudio_result_t error) //an AAUDIO_ERROR_* value.
{
    //if there's a disconnected error, try to reconnect and restart
    if (error == AAUDIO_ERROR_DISCONNECTED){
        std::function<void(void)> restartFunction = std::bind(&AudioEngine::restart,
                static_cast<AudioEngine *>(userData));
        new std::thread(restartFunction);
    }
}

bool AudioEngine::start() {
    //call to StreamBuilder functions from aaudio library to start the engine
    AAudioStreamBuilder *streamBuilder = nullptr;
    AAudio_createStreamBuilder(&streamBuilder);
    AAudioStreamBuilder_setFormat(streamBuilder, AAUDIO_FORMAT_PCM_FLOAT);
    AAudioStreamBuilder_setChannelCount(streamBuilder, 1);
    AAudioStreamBuilder_setPerformanceMode(streamBuilder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    AAudioStreamBuilder_setDataCallback(streamBuilder, ::dataCallback, &mVoiceManager);
    AAudioStreamBuilder_setErrorCallback(streamBuilder, ::errorCallback, this);

    //open the stream
    aaudio_result_t result = AAudioStreamBuilder_openStream(streamBuilder, &mStream);
    if (result != AAUDIO_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Error opening stream %s", AAudio_convertResultToText(result));
        return false;
    }

    //retrieve the sample rate and pass it to the voice manager to pass down to the voices then
    // the wave generators
    int sampleRate = AAudioStream_getSampleRate(mStream);
    mVoiceManager.setSampleRate(sampleRate);

    //set the buffer size
    AAudioStream_setBufferSizeInFrames(mStream,
        AAudioStream_getFramesPerBurst(mStream) * kBufferSizeInBursts);

    //start the stream
    result = AAudioStream_requestStart(mStream);
    if (result != AAUDIO_OK){
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Error starting stream %s", AAudio_convertResultToText(result));
        return false;
    }

    //delete the resources associated with the stream builder now that we have what we need
    AAudioStreamBuilder_delete(streamBuilder);
    return true;
}

//stop the engine
void AudioEngine::stop(){
    //if there is a stream, stop it and close it
    if (mStream != nullptr){
        AAudioStream_requestStop(mStream);
        AAudioStream_close(mStream);
    }
}


void AudioEngine::restart(){

    static std::mutex restartingLock;
    //if the stream can be locked, stop the engine, start the engine then release the lock
    if (restartingLock.try_lock()){
        stop();
        start();
        restartingLock.unlock();
    }
}

//called by the jni bridge from the UI to turn the keys on and off
void AudioEngine::setToneOn(int keyId, bool isToneOn){
    //send the keyId to the proper function depending on the message from the UI
    if (isToneOn){
        mVoiceManager.onNoteOn(keyId);
    } else {
        mVoiceManager.onNoteOff(keyId);
    }
}
