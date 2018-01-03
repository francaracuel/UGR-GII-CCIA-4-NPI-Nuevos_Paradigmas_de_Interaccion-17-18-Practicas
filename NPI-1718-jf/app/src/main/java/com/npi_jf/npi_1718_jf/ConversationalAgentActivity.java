////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Juan Manuel Fajardo Sarmiento
// Francisco Javier Caracuel Beltrán
//
// Nuevos Paradigmas de Interacción - Ciencias de la Computación e Inteligencia Artificial
//
// UGR - GII
//
// Curso 2017-2018
//
// ConversationalAgentActivity
//
// Clase que contiene el Activity que se encarga de la interacción con el agente conversacional.
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.npi_jf.npi_1718_jf;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.content.ActivityNotFoundException;
import java.util.Locale;
import java.util.ArrayList;
import android.speech.tts.TextToSpeech;



public class ConversationalAgentActivity extends AppCompatActivity implements DataSend{

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 22;
    private static final String LOGTAGSR = "SpeechRecognition";
    private static final String LOGTAGTTS = "TextToSpeech";

    // Objeto que se encarga de inicializar todo lo necesario para llamar al Agente
    ConversationalAgent ca;
    Voice voice;
    TextToSpeech tts;

    TextView tvQuestion;
    TextView tvAnswer;

    SensorManager sensorManager;


    /**
     * Método que se ejecuta al iniciar el Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_conversational_agent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        FloatingActionButton speak = (FloatingActionButton) findViewById(R.id.speak);

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (voice.isSpeaking() == false) {

                    tvQuestion.setText(null);
                    tvAnswer.setText(null);

                    checkSRPermission();
                    voice.listen();

                }
                else
                    voice.stopSpeaking();

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Se define el objeto que se encarga de hacer las peticiones con el Agente
        ca = new ConversationalAgent(this, 1);

        voice = new Voice(this);


        tvQuestion = (TextView)findViewById(R.id.textViewQuestion);

        tvAnswer = (TextView)findViewById(R.id.textViewAnswer);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        final Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        SensorEventListener proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if(event.values[0] < proximitySensor.getMaximumRange()){
                    voice.stopSpeaking();
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(proximitySensorListener, proximitySensor, 1000*1000*2);


        Sensor gyrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);



        SensorEventListener gyrSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                float value = 0.5f;

                if(event.values[2] > value){
                    try {
                        finish();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(gyrSensorListener, gyrSensor, 1000*1000*2);


    }

    /**
     * Método que obtiene la respuesta del Agente
     * @param data - Tiene la respuesta que ha recibido la clase ConversationalAgent al hacer
     *                  la petición a Api.io
     */
    @Override
    public void onDataSended(int code, String data) {

        TextView tv;

        switch (code){

            case 0:

                tvQuestion.setText(data);

                break;

            case 1:

                voice.speak(data);

                tvAnswer.setText(data);

                break;

        }

        /*int duration = Toast.LENGTH_SHORT;
        Toast toast;

        toast = Toast.makeText(getApplicationContext(), data, duration);
        toast.show();*/

    }

    /**
     * Processes the result of the record audio permission request. If it is not granted, the
     * abstract method "onRecordAudioPermissionDenied" method is invoked. Such method must be implemented
     * by the subclasses of VoiceActivity.
     * More info: http://developer.android.com/intl/es/training/permissions/requesting.html
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Log.i(LOGTAGSR, "Record audio permission granted");
            else {
                Log.i(LOGTAGSR, "Record audio permission denied");
                Toast.makeText(getApplicationContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkSRPermission() {
        if (android.support.v4.content.ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // If  an explanation is required, show it
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
                Toast.makeText(getApplicationContext(), getString(R.string.permission_explanation), Toast.LENGTH_SHORT).show();

            // Request the permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO); //Callback in "onRequestPermissionResult"
        }
    }








}