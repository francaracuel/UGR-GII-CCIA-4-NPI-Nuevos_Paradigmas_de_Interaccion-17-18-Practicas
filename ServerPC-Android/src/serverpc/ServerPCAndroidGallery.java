////////////////////////////////////////////////////////////////////////////////
//
// Juan Manuel Fajardo Sarmiento
// Francisco Javier Caracuel Beltrán
//
// Nuevos Paradigmas de Interacción - Ciencias de la Computación e Inteligencia
// Artificial
//
// UGR - GII
//
// Curso 2017-2018
//
// ServerPCAndroidGallery
//
// Clase principal que actúa como servidor para recibir las peticiones del
// control en la selección de personajes
//
////////////////////////////////////////////////////////////////////////////////

package serverpc;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerPCAndroidGallery {
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        // Puerto que se utiliza para recibir las peticiones
        int port = 1818;
        
        // Contador para saber que personaje se quiere mostrar
        int character = 0;
        
        // Se obtienen las rutas de las imágenes
        ArrayList<String> pathImages = getPathImages();
        
        // Se obtienen los nombres de los personajes de las imágenes
        ArrayList<String> namesImages = getNameImages();
        
        // Se crea el JFrame que contiene la ventana donde se van a mostrar las
        // imágenes de los personajes
        Gallery gallery = new Gallery();
        
        // Se activa la ventana
        gallery.setVisible(true);
                
        // Se crea un bucle infinito para escuchar las peticiones
        while(true){
            
            // Se crea el objeto que abre el puerto para leer las peticiones
            ServerSocket socket = new ServerSocket(port); 

            // Se inicia la escucha del puerto
            Socket clientSocket = socket.accept();

            DataInputStream DIS;
                           
            // Se engloba en try/catch por si hay algún error al establecer la
            // conexión
            try {

                // Se crea el flujo de datos para leer las peticiones del puerto
                DIS = new DataInputStream(clientSocket.getInputStream());

                // Petición recibida
                String request = DIS.readUTF();

                // En la petición se recibe:
                // -1: mostrar el personaje anterior
                // 0: seleccionar el personaje
                // 1: mostrar el personaje siguiente
                int requestAux = Integer.parseInt(request);
                
                switch(requestAux){

                    // Selecciona el personaje
                    case 0:

                        gallery.selectImage();

                        break;

                    // Retrocede o avanza al mostrar los personajes
                    case -1:
                    case 1:

                        // Se modifica el personaje que se quiere mostrar
                        character = (character+requestAux)%pathImages.size();
                        
                        if(character < 0)
                            character = pathImages.size()-1;

                        //System.out.println("Personaje: "+character);

                        // Se envía la petición a la galería
                        gallery.setImage(namesImages.get(character), 
                                                    pathImages.get(character));

                        break;

                }

                // Cuando se termina de leer la petición se cierra el puerto 
                // para volver a leer de nuevo y esperar a la siguiente
                // petición
                DIS.close();
                clientSocket.close();
                socket.close();

            } catch(IOException e){

                System.out.println("Error: "+e);
                                
            }
        
        }
                   
    }
    
    // Devuelve un vector con la ruta donde se encuentran las imágenes
    private static ArrayList<String> getPathImages(){
        
        // Extensión que tendrán todas las imágenes
        String ext = ".jpg";
        
        // Número de imágenes que se van a mostrar
        int numberImages = 11;
        
        // Vector donde se guarda la ruta
        ArrayList<String> path = new ArrayList<>();
        
        // Se crean los nombres de los ficheros
        for(int i=0; i<numberImages; i++){
            
            path.add("/images/"+String.format("%03d", i)+ext);
            //System.out.println(path.get(i));
            
        }
        
        // Se devuelven las rutas de las imágenes
        return path;
        
    }
    
    // Devuelve un vector con los nombres de los personajes en el mismo orden 
    // que se encuentran enumerados
    private static ArrayList<String> getNameImages(){
                
        // Se crea un vector de String para guardar los nombres
        ArrayList<String> names = new ArrayList<>();
        
        // Se añaden los nombres
        names.add("Ibn al-Jatib");
        names.add("Albert Einstein");
        names.add("Leonardo Da Vinci");
        names.add("Mahatma Gandhi");
        names.add("Colón");
        names.add("William Shakespeare");
        names.add("Napoleón");
        names.add("Cervantes");
        names.add("Julio César");
        names.add("Ramsés II");
        names.add("Reyes Católicos");
        
        // Se devuelven los nombres
        return names;
        
    }
    
}
