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
// LeapAppMuseum
//
// Archivo main que ejecuta la aplicación
//
////////////////////////////////////////////////////////////////////////////////

package leapappmuseum;

import com.leapmotion.leap.*;
import java.io.IOException;

public class LeapAppMuseum {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        //Canvas canvas = new Canvas();
        MapPanel map = new MapPanel();
        
        map.createAndShowGUI();

        // Crea listener y controller 
        LeapListener listener = new LeapListener(map);
        Controller controller = new Controller();
        
        // El listener recibe eventos del controller
        controller.addListener(
                listener
        );

        // Procedimiento de salida del programa 
        System.out.println("Press Enter to quit...");
        
        try {
            System.in.read();
        } catch (IOException e) {
        
        }
        
        // Elimina el listener cuando no es necesario 
        controller.removeListener(listener);

    }

}
