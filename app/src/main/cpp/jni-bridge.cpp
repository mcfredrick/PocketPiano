#include <jni.h>
#include <android/input.h>
#include "AudioEngine.h"

//instantiate and audio engine
static AudioEngine *audioEngine = new AudioEngine();

extern "C" {

    JNIEXPORT void JNICALL
    Java_com_mcfredrick_pocketpiano_PianoView_touchEvent(JNIEnv *env,
                                                         jobject instance,
                                                         jint keyId,
                                                         jint keyOn)
    {
        //send the key # and on/off message to the audio engine to send along to the voice manager
        audioEngine->setToneOn(keyId, keyOn);

    }

    JNIEXPORT void JNICALL
    Java_com_mcfredrick_pocketpiano_PianoActivity_startEngine(JNIEnv *env, jobject /* this */)
    {
        //called when the app opens to start an audio stream
        audioEngine->start();

    }

    JNIEXPORT void JNICALL
    Java_com_mcfredrick_pocketpiano_PianoActivity_stopEngine(JNIEnv *env, jobject /* this */){
        //called when the app stops the close the audio stream
        audioEngine->stop();

    }
}
