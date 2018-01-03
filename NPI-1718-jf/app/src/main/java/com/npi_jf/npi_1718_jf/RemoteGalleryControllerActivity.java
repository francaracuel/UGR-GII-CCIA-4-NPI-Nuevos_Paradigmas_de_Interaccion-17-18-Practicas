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
// RemoteGalleryControllerActivity
//
// Activity utilizado para controlar la selección de personajes
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.npi_jf.npi_1718_jf;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import static java.lang.Math.abs;

public class RemoteGalleryControllerActivity extends AppCompatActivity implements SensorEventListener {

    // Datos utilizados para la conexión con el servidor
    String ip;
    int port;

    // Etiquetas utilizadas para cambiar la Ip y el puerto de la conexión con el servidor
    EditText etIp;
    EditText etPort;

    // Sensores del teléfono
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private Sensor senMagnetic;

    private long lastUpdate, lastUpdateMagn;
    private float accLastX, accLastY, accLastZ;

    // Velocidad del movimiento
    float speed;

    // Tiempo de espera cada vez que detecta un movimiento y envía al servidor
    private int ml;

    // Umbral de la velocidad del movimiento
    private int shakeThreshold;

    // Umbral para detectar el movimiento de retroceso o avance
    private int movementThreshold;

    // Umbral que se detecta cuando se inclina hacia arriba el teléfono
    private int upThreshold;

    // Gravity rotational data
    private float gravity[];

    // Magnetic rotational data
    private float magnetic[];

    // For magnetic rotational data
    private float accels[] = new float[3];
    private float mags[] = new float[3];
    private float[] values = new float[3];

    // Azimuth, pitch and roll
    private float azimuth;
    private float pitch;
    private float roll;

    // Diferencia de tiempo para comprobar los sensores
    private long diffMagn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_gallery_controller);

        // Se añade la flecha para volver a la ventana principal
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Se inicializan las variables
        init();

    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Inicializa las variables necesarias para establecer la conexión con el servidor
    private void init(){

        ip = "192.168.1.128";
        port = 1818;

        // Se asigna la Ip y puerto por defecto que tendrá el servidor
        etIp = (EditText)findViewById(R.id.editTextGalleryIp);
        etPort = (EditText)findViewById(R.id.editTextGalleryPort);

        etIp.setText(ip);
        etPort.setText(String.valueOf(port));

        // Inicialización de los sensores
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Registro del giroscopio
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        // Registro del magnenómetro
        senMagnetic = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        senSensorManager.registerListener(this, senMagnetic , SensorManager.SENSOR_DELAY_NORMAL);

        lastUpdate = 0;
        lastUpdateMagn = 0;

        // Velocidad del movimiento
        speed = 0;

        // Tiempo de espera cada vez que detecta un movimiento y envía al servidor
        ml = 500;

        // Umbral de la velocidad del movimiento
        shakeThreshold = 200;

        // Umbral para detectar el movimiento de retroceso o avance
        movementThreshold = 65;

        // Umbral que se detecta cuando se inclina hacia arriba el teléfono
        upThreshold = 50;

        // Diferencia de tiempo para comprobar los sensores
        diffMagn = 500;

    }

    // Envía la orden de mostrar el personaje anterior
    public void previous(View view){

        sendPrevious();

    }

    private void sendPrevious(){

        new DataSendHttp(etIp.getText().toString(),
                Integer.parseInt(etPort.getText().toString())).execute("-1");

    }

    // Envía la orden de mostrar el personaje siguiente
    public void next(View view){

        sendNext();

    }

    private void sendNext(){

        new DataSendHttp(etIp.getText().toString(),
                Integer.parseInt(etPort.getText().toString())).execute("1");

    }

    // Envía la orden de seleccionar el personaje actual
    public void select(View view){

        sendSelect();

    }

    private void sendSelect(){

        new DataSendHttp(etIp.getText().toString(),
                Integer.parseInt(etPort.getText().toString())).execute("0");

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor mySensor = sensorEvent.sensor;

        long curTime = System.currentTimeMillis();

        long diffTime = (curTime - lastUpdate);

        //Log.v("DIFF", diffTime+"");

        // Si es el acelerómetro de calcula la velocidad del movimiento
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            //curTime = System.currentTimeMillis();

            if (diffTime > 100) {

                speed = abs(x + y + z - accLastX - accLastY - accLastZ)/ diffTime * 10000;

                accLastX = x;
                accLastY = y;
                accLastZ = z;

            }
        }

        // Para comprobar la orientación es necesario el acelerómetro y el magnenómetro
        switch (sensorEvent.sensor.getType()) {

            case Sensor.TYPE_MAGNETIC_FIELD:
                mags = sensorEvent.values.clone();
                break;

            case Sensor.TYPE_ACCELEROMETER:
                accels = sensorEvent.values.clone();
                break;

        }

        // Si los sensores leídos son el acelerómetro y el magnenómetro se leen los datos
        if (mags != null && accels != null) {

            // Datos de la rotación
            gravity = new float[9];
            magnetic = new float[9];
            float[] outGravity = new float[9];

            // Se guardan los datos de la rotación
            SensorManager.getRotationMatrix(gravity, magnetic, accels, mags);

            SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X,
                                                                SensorManager.AXIS_Z, outGravity);

            // Se obtienen los datos de la orientación del teléfono
            SensorManager.getOrientation(outGravity, values);

            // Se asignan los valores de la orientación obtenidos anteriormente
            azimuth = values[0] * 57.2957795f;
            pitch =values[1] * 57.2957795f;
            roll = values[2] * 57.2957795f;

            //Log.v("MAGNETIC", "azimut: "+azimuth+", pitch: "+pitch+", roll: "+roll+", speed: "+speed);

            diffTime = (curTime - lastUpdateMagn);

            if (diffTime > diffMagn) {

                // Si el movimiento sobre el eje X es menor que un umbral y la velocidad indica que ha
                // habido un movimiento fuerte, se inspecciona para comprobar qué movimiento ha sido
                if (pitch < upThreshold && speed > shakeThreshold) {

                    // Si el movimiento sobre el eje Y es mayor o menor que un umbral se detecta un
                    // movimiento para retroceder o avanzar de personaje
                    if (roll > movementThreshold) {
                        sendNext();
                    } else if (roll < -movementThreshold) {
                        sendPrevious();
                    } else {
                        sendSelect();
                    }

                    // Se actualiza el tiempo
                    lastUpdateMagn = curTime;

                    // Cuando se haya enviado la orden al servidor, se duerme durante "ml" milisegundos
                    // para evitar otro envío al servidor
                    /*try {
                        Thread.sleep(ml);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/

                }

            }

        }

        // Se actualiza el tiempo
        lastUpdate = curTime;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
