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
// TaskCompleted
//
// Interfaz utilizada para devolver el resultado de la petición al Agente
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.npi_jf.npi_1718_jf;

public interface DataSend {

    /**
     * Se define el resultado que se quiere devolver a la clase que llama al Agente
     * @param response - Respuesta que devuelve el Agente
     */
    public void onDataSended(int code, String response);

}
