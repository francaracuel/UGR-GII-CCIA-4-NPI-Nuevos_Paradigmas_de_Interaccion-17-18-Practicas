package com.npi_jf.npi_1718_jf;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by juanmfajardo on 6/11/17.
 */

public class Voice implements RecognitionListener {

    private static final String LOGTAGSR = "SpeechRecognition";
    private static final String LOGTAGTTS = "TextToSpeech";

    private long startListeningTime = 0; // To skip errors (see onError method)


    private ConversationalAgent ca;
    private Context context;
    private SpeechRecognizer sr;
    private TextToSpeech tts;

    private Locale localeListen;
    private Locale localeSpeech;


    public Voice(Context c) {
        ca = new ConversationalAgent(c,1);
        context = c;

        localeListen = new Locale("es", "ES");
        localeSpeech = new Locale("es", "ES");

        initSR();
        initTTS();
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int errorCode) {
        //Possible bug in Android SpeechRecognizer: NO_MATCH errors even before the the ASR
        // has even tried to recognized. We have adopted the solution proposed in:
        // http://stackoverflow.com/questions/31071650/speechrecognizer-throws-onerror-on-the-first-listening
        long duration = System.currentTimeMillis() - startListeningTime;
        if (duration < 500 && errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
            Log.e(LOGTAGSR, "Doesn't seem like the system tried to listen at all. duration = " + duration + "ms. Going to ignore the error");
            stopListening();
        }
        else {
            String errorMsg = "";
            switch (errorCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    errorMsg = "Audio recording error";
                case SpeechRecognizer.ERROR_CLIENT:
                    errorMsg = "Unknown client side error";
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMsg = "Insufficient permissions";
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMsg = "Network related error";
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMsg = "Network operation timed out";
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMsg = "No recognition result matched";
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMsg = "RecognitionService busy";
                case SpeechRecognizer.ERROR_SERVER:
                    errorMsg = "Server sends error status";
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMsg = "No speech input";
                default:
                    errorMsg = "";
            }
            if (errorCode == 5 && errorMsg == "") {
                Log.e(LOGTAGSR, "Going to ignore the error");
                //Another frequent error that is not really due to the ASR
            } else {
                Log.e(LOGTAGSR, "Error -> " + errorMsg);
                stopListening();
            }
        }
    }

    @Override
    public void onResults(Bundle results) {
        if(results!=null) {

            Log.i(LOGTAGSR, "SR results received ok");

            //Retrieves the N-best list and the confidences from the ASR result
            ArrayList<String> nBestList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            Log.i("Pregunta", nBestList.get(0));
            ca.execQuery(nBestList.get(0));
            //ca.execQuery("hola");

            // Para poder devolver la respuesta al Activity que lo llama es necesario indicar el
            // método que lo devuelve
            DataSend ds = (DataSend)context;

            ds.onDataSended(0, nBestList.get(0));

        }
        else{
            Log.e(LOGTAGSR, "SR results null");

        }

        stopListening();
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    /**
     * Creates the speech recognizer instance if it is available
     * */
    public void initSR() {

        // find out whether speech recognition is supported
        List<ResolveInfo> intActivities = context.getPackageManager().queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        //Speech recognition does not currently work on simulated devices

        if (intActivities.size() != 0) {
            sr = SpeechRecognizer.createSpeechRecognizer(context);
            sr.setRecognitionListener(this);
        }


        Log.i(LOGTAGSR, "SR initialized");
    }

    public void initTTS(){
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status){
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(localeSpeech);
                }
            }
        });

        Log.i(LOGTAGTTS, "TTS initialized");

    }

    public void listen(){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify the application
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        //Caution: be careful not to use: getClass().getPackage().getName());

        // Specify language model
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Specify how many results to receive. Results listed in order of confidence
        //intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        // Specify recognition language
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeListen);

        Log.i(LOGTAGSR, "Going to start listening...");
        sr.startListening(intent);


    }

    public void speak(String text){

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "msg");

    }

    public void stopListening(){
        sr.stopListening();
        Log.i(LOGTAGSR, "Stopped listening");
    }

    public boolean isSpeaking(){
        return tts.isSpeaking();
    }

    public void stopSpeaking(){
        tts.stop();
    }

    public void shutDown() {
        tts.shutdown();
    }




}