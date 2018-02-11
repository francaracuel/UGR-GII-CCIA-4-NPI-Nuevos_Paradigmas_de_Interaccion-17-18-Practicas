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
// MainActivity
//
// Activity principal de la aplicación desde donde se puede iniciar el controlador del personaje,
// la conversación con el personaje y la transmisión por NFC.
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.npi_jf.npi_1718_jf;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    // Método que se ejecuta al pulsar el botón de Agente Conversacional. Inicia la conversación
    // con el personaje
    public void initCA(View view) {
        Intent intent = new Intent(this, ConversationalAgentActivity.class);
        startActivity(intent);
    }


    // Inicia el control de la selección de los personajes
    public void initGC(View view) {
        Intent intent = new Intent(this, RemoteGalleryControllerActivity.class);
        startActivity(intent);
    }

    // Se conecta con NFC para obtener el punto al que se quiere ir en el mapa
    public void initMapNFC(View view) {
        Intent intent = new Intent(this, ReadNFCActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

}
