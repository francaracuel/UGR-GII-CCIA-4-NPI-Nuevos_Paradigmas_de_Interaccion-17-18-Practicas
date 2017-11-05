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

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ConversationalAgentActivity extends AppCompatActivity implements TaskCompleted{

    // Objeto que se encarga de inicializar todo lo necesario para llamar al Agente
    ConversationalAgent ca;

    /**
     * Método que se ejecuta al iniciar el Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_conversational_agent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Se define el objeto que se encarga de hacer las peticiones con el Agente
        ca = new ConversationalAgent(this, 1);

        ////////////////////////////////////////////////////////////////////////////////////////////
        // BORRAR DE AQUI
        // Cuando se quiera enviar una petición al Agente se hace a través de la siguiente instrucción.
        // La respuesta se recibe en el método onTaskComplete(String)
        //ca.execQuery("Hi");
        ////////////////////////////////////////////////////////////////////////////////////////////

    }

    /**
     * Método que obtiene la respuesta del Agente
     * @param response - Tiene la respuesta que ha recibido la clase ConversationalAgent al hacer
     *                  la petición a Api.io
     */
    @Override
    public void onTaskComplete(String response) {

        int duration = Toast.LENGTH_SHORT;
        Toast toast;

        toast = Toast.makeText(getApplicationContext(), response, duration);
        toast.show();

    }

    public void query(View view){

        EditText text = (EditText)findViewById(R.id.query);
        String value = text.getText().toString();

        ca.execQuery(value);

    }

}
