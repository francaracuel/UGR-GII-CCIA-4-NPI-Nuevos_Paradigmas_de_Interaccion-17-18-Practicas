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
// DataSendHttp
//
// Clase que se encarga de comunicarse con el servidor para elegir el personaje con el que se quiere
// mantener una conversación
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.npi_jf.npi_1718_jf;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DataSendHttp extends AsyncTask<String, Void, String> {

    // Se declara la IP y el puerto que se va a utilizar para las comunicaciones
    private String ip;
    private int port;

    // El constructor recibe la ip y el puerto
    public DataSendHttp(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected String doInBackground(String... strings) {

        // Se establece una conexión con el servidor, enviando la orden que se quiera
        try {

            Socket socket = new Socket(ip, port);
            socket.setSoTimeout(1);
            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
            DOS.writeUTF(strings[0]);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";

    }

}
