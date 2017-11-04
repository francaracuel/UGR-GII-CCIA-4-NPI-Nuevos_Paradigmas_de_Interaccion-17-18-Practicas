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
// ConversationalAgent
//
// Clase que se encarga de comunicarse con Api.io para enviar y recibir las preguntas y respuestas
// del agente conversacional.
//
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.npi_jf.npi_1718_jf;

// Imports del SDK de Android
import android.content.Context;
import android.os.AsyncTask;

// Imports de Api.io
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

// Clase ConversationalAgent
public class ConversationalAgent{

    // Constante que contiene el token necesario para hacer la petición con la pregunta a Api.io
    private static final String TOKEN = "629f352f417f40e794a8bbbe3d9673df";

    // Contexto del Activity donde se va a hacer uso del Agente
    private Context context;

    // Objeto que se encarga de inicializar la comunicación con el Agente
    private AIDataService aiDataService;

    // Objeto que se encarga de enviar las peticiones al Agente
    private AIRequest aiRequest;

    // Respuesta que ofrece el Agente conversacional
    private String response;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    //

    /**
     * Constructor único de la clase.
     * @param context - Contexto del activity desde donde se llama (se puede obtener desde
     * {@link android.content.Context#} con getApplicationContext()).
     * @param idLanguage - Lenguaje que se quiere utilizar como entrada. 0: Inglés. 1: Español.
     */
    ConversationalAgent(Context context, int idLanguage){

        // Se guarda el contexto del Activity
        this.context = context;

        // Variable donde se guarda el lenguaje que se quiere utilizar
        AIConfiguration.SupportedLanguages language;

        // Se obtiene el lenguage que se quiere utilizar
        switch(idLanguage){

            case 0:
                language = AIConfiguration.SupportedLanguages.English;
                break;

            case 1:
                language = AIConfiguration.SupportedLanguages.Spanish;
                break;

            default:
                language = AIConfiguration.SupportedLanguages.Spanish;
                break;

        }

        // Se crea la configuración del Agente
        final AIConfiguration config = new AIConfiguration(TOKEN,
                language,
                AIConfiguration.RecognitionEngine.System);

        // Se crea el servicio que se utiliza para conectar con el Agente
        aiDataService = new AIDataService(context, config);

        // Se crea el objeto que se encarga de hacer las peticiones al Agente
        aiRequest = new AIRequest();

    }

    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Utils
    //

    /**
     * Método que se encarga de hacer las peticiones al Agente y devuelve una respuesta.
     * @param query - Pregunta que se le quiere hacer al Agente.
     * @return - Respuesta que ofrece el Agente.
     */
    public void execQuery(String query){

        // Se hace la petición al Agente con la pregunta
        aiRequest.setQuery(query);

        // Se crea un hilo en segundo plano que se encarga de recibir la respuesta del Agente
        new AsyncTask<AIRequest, Void, AIResponse>(){

            // Para poder devolver la respuesta al Activity que lo llama es necesario indicar el
            // método que lo devuelve
            TaskCompleted tc = (TaskCompleted)context;

            // Hace toda la operación en segundo plano que se requiere
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {

                final AIRequest request = requests[0];

                try {

                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;

                } catch (AIServiceException e) {

                }

                return null;

            }

            // Obtiene el resultado de hacer la operación en segundo plano
            @Override
            protected void onPostExecute(AIResponse aiResponse) {

                // Si ha habido alguna respuesta se devuelve el resultado
                if (aiResponse != null) {

                    // Variable donde se guarda el resultado de la petición
                    final Result result = aiResponse.getResult();

                    //int duration = Toast.LENGTH_SHORT;
                    //Toast toast;

                    /*final Metadata metadata = result.getMetadata();

                    if (metadata != null) {
                        toast = Toast.makeText(context, "Intent id: " + metadata.getIntentId(), duration);
                        toast.show();
                        toast = Toast.makeText(context, "Intent name: " + metadata.getIntentName(), duration);
                        toast.show();
                    }

                    final ai.api.model.Status status = aiResponse.getStatus();

                    toast = Toast.makeText(context, "Status code: " + status.getCode(), duration);
                    toast.show();

                    toast = Toast.makeText(context, "Status type: " + status.getErrorType(), duration);
                    toast.show();

                    toast = Toast.makeText(context, "Resolved query: " + result.getResolvedQuery(),
                        duration);
                    toast.show();

                    toast = Toast.makeText(context, "Action: " + result.getAction(), duration);
                    toast.show();*/

                    //toast = Toast.makeText(context, "Speech: " + result.getFulfillment().getSpeech(),
                    //  duration);
                    //toast.show();

                    // Se le pasa el resultado a través del método que implementa el Activity
                    tc.onTaskComplete(result.getFulfillment().getSpeech());

                }

            }

        }.execute(aiRequest);

    }

}
