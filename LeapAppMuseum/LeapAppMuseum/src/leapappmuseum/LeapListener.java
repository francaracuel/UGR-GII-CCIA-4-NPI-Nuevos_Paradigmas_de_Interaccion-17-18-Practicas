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
// LeapListener
//
// Clase que recibe los eventos que se producen con Leap
//
////////////////////////////////////////////////////////////////////////////////
package leapappmuseum;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;
import com.leapmotion.leap.Vector;
import java.awt.AWTException;
import static java.lang.Math.abs;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LeapListener extends Listener {

    private final MapPanel map;

    public LeapListener(MapPanel map) {

        this.map = map;

    }

    // Se registra e inicializa el controlador
    @Override
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    // Se elimina el listener de un controlador
    @Override
    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    // Leap conectado y listo para enviar Frames
    @Override
    public void onConnect(Controller controller) {
        System.out.println("Connected");

        // Habilita la detección de gestos 
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);

        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);

        //controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        //controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }

    // Desconectamos Leap software o hardware
    @Override
    public void onDisconnect(Controller controller) {
        System.out.println("Disconnected");
    }

    // Cuando un nuevo Frame está disponible
    @Override
    public void onFrame(Controller controller) {

        // Frame más reciente
        Frame frame = controller.frame();

        // Número de dedos
        int nFingers = frame.fingers().extended().count();

        // ID, timestamp y número de elementos 
        /*System.out.println("Frame id: " + frame.id() + ", " + "timestamp: "
                + frame.timestamp() + ", hands: " + frame.hands().count()
                + ", fingers: " + frame.fingers().extended().count() + ", tools: "
                + frame.tools().count() + ", gestures" + frame.gestures().count()
        );
         */
        // Vamos a examinar la primera mano
        if (!frame.hands().isEmpty()) {

            // Obtiene el objeto mano
            Hand hand = frame.hands().get(0);

            // Comprobamos la lista de dedos 
            FingerList fingers = hand.fingers();

            if (!fingers.isEmpty()) {

                // Calcula la posición media de los dedos 
                Vector avgPos = Vector.zero();

                for (Finger finger : fingers) {
                    avgPos = avgPos.plus(finger.tipPosition());
                }

                //avgPos = avgPos.divide(fingers.count());

                /*System.out.println("Hand has " + fingers.count()
                        + "fingers, average finger tip position: " + avgPos);*/
            }

            // Seguimos examinando la primera mano
            if (!frame.hands().isEmpty()) {

                // Obtiene datos de la mano 
                /*System.out.println("Hand sphere radius: " + hand.sphereRadius()
                        + " mm, palm position: " + hand.palmPosition());*/
                // Obtiene la normal y la dirección
                Vector normal = hand.palmNormal();
                Vector direction = hand.direction();

                // Calcula los dedos de la mano, evuelve radianes y se pasan a 
                // grados 
                /*System.out.println("Hand pitch: " + Math.toDegrees(direction.pitch())
                        + ", " + "roll: " + Math.toDegrees(+normal.roll()) + ", "
                        + "+yaw: " + Math.toDegrees(direction.yaw()));*/
            }

            // Recorremos la lista de gestos 
            GestureList gestures = frame.gestures();

            for (int i = 0; i < gestures.count(); i++) {

                Gesture gesture = gestures.get(i);

                switch (gesture.type()) {

                    // La lista contiene objetos de tipo Gesture, debemos convertir 
                    // ese objeto a la instancia de la  subclase. 
                    // No se puede hacer Type Casting, usamos los constructores 
                    // específicos , ejemplo 
                    //CircleGesture circle = new CircleGesture(gesture);
                    case TYPE_CIRCLE:

                        CircleGesture circle = new CircleGesture(gesture);

                        // Calcula la dirección usando el ángulo entre la normal
                        // y el pointable
                        String clockwiseness;

                        if (circle.pointable().direction().angleTo(circle.normal())
                                <= Math.PI / 4) {

                            // Si es menor que 90 grados
                            clockwiseness = "clockwise";

                        } else {
                            clockwiseness = "counterclockwise";
                        }

                        // Calcula en ángulo desplazado desde el último Frame
                        double sweptAngle = 0;

                        if (circle.state() != State.STATE_START) {
                            CircleGesture previousUpdate = new CircleGesture(
                                    controller.frame(1).gesture(circle.id()));

                            sweptAngle = (circle.progress()
                                    - previousUpdate.progress()) * 2 * Math.PI;
                        }

                        // Solo si se realiza el gesto con un dedo aumentará
                        // o disminuirá el zoom
                        if (nFingers == 1) {

                            float value = (float) 0.01;

                            // Se comprueba si es en sentido horario. En caso de
                            // serlo se aumenta uno el zoom
                            if (clockwiseness == "counterclockwise") {

                                try {

                                    // Se disminuye el zoom
                                    map.zoom(1);

                                } catch (AWTException ex) {
                                    Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                // Si es en sentido antihorario se disminuye el zoom
                            } else {

                                try {

                                    // Se aumenta el zoom
                                    map.zoom(-1);

                                } catch (AWTException ex) {
                                    Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }

                        }

                        System.out.println("Circle id: " + circle.id() + ", "
                                + circle.state() + ", progress: " + circle.progress()
                                + ", radius: " + circle.radius() + ", angle: "
                                + Math.toDegrees(sweptAngle) + ", " + clockwiseness);

                        break;

                    // Evento que se produce cuando se mueve la mano
                    case TYPE_SWIPE:

                        int value = 5;

                        SwipeGesture swipe = new SwipeGesture(gesture);

                        // Solo si el número de dedos es 5 realizará las 
                        // acciones correspondientes
                        if (nFingers == value) {

                            try {

                                // Antes de hacer ningún gesto, se indica que
                                // se debe simular que se pulsa el botón del 
                                // ratón
                                map.press();

                            } catch (AWTException ex) {
                                Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            // Si lo que se mueve la mano hacia arriba o hacia
                            // abajo es menor de 0.5, el movimiento que se 
                            // realiza es para desplazarse por el mapa
                            if (abs(swipe.direction().getY()) < 0.5) {

                                // Si lo que se desplaza la mano es más de 0.5
                                // hacia la derecha, se indica ese movimiento
                                if (swipe.direction().getX() > 0.5) {

                                    try {

                                        // Se desplaza el ratón 100 puntos
                                        // hacia la derecha
                                        map.drag(100, 0);

                                    } catch (AWTException ex) {
                                        Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    // Si lo que se desplaza la mano es más de 0.5 
                                    // hacia la izquierda, se indica ese movimiento
                                } else if (swipe.direction().getX() < -0.5) {

                                    try {

                                        // Se desplaza el ratón 100 puntos hacia
                                        // la izquierda
                                        map.drag(-100, 0);

                                    } catch (AWTException ex) {
                                        Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }

                                // Si lo que se desplaza la mano es más de 0.5
                                // hacia adelante, se indica ese movimiento
                                if (swipe.direction().getZ() > 0.5) {

                                    try {

                                        // Se desplaza el ratón 100 puntos hacia
                                        // abajo
                                        map.drag(0, -100);

                                    } catch (AWTException ex) {
                                        Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    // Si lo que se desplaza la mano es más de 0.5
                                    // hacia atrás, se indica ese movimiento
                                } else if (swipe.direction().getZ() < -0.5) {

                                    try {

                                        // Se desplaza el ratón 100 puntos hacia
                                        // arriba
                                        map.drag(0, 100);

                                    } catch (AWTException ex) {
                                        Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }

                            }

                            // Si el movimiento que se realiza en horizontal y
                            // hacia adelante y hacia atrás es menor de 0.5, se
                            // entiende que el movimiento es hacia arriba y 
                            // abajo para hacer zoom
                            if (abs(swipe.direction().getX()) < 0.5
                                    && abs(swipe.direction().getZ()) < 0.5) {

                                // La mano se mueve hacia arriba, se disminuye 
                                // el zoom
                                if (swipe.direction().getY() > 0) {

                                    try {

                                        // Se disminuye el zoom
                                        map.zoom(1);

                                    } catch (AWTException ex) {
                                        Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    // La mano se mueve hacia abajo, se aumenta el
                                    // zoom
                                } else {

                                    try {

                                        // Se aumenta el zoom
                                        map.zoom(-1);

                                    } catch (AWTException ex) {
                                        Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }
                            }

                            try {

                                // Cuando se termina todo se indica que el
                                // botón del ratón se deje de pulsa
                                map.release();

                            } catch (AWTException ex) {
                                Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                        if (nFingers == 2) {

                            // Si el movimiento que se realiza en horizontal y
                            // hacia adelante y hacia atrás es menor de 0.5, se
                            // entiende que el movimiento es hacia arriba y 
                            // abajo para hacer zoom
                            if (abs(swipe.direction().getX()) < 0.5
                                    && abs(swipe.direction().getZ()) < 0.5) {

                                // La mano se mueve hacia arriba, se disminuye 
                                // el zoom
                                if (swipe.direction().getY() > 0) {

                                    // La mano se mueve hacia abajo, se aumenta el
                                    // zoom
                                } else {

                                    try {

                                        // Se aumenta el zoom
                                        map.show_nfc("Acerca del dispositivo",
                                                "Ya puedes conectar tu móvil con NFC");

                                    } catch (AWTException ex) {
                                        Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                }
                            }

                            try {

                                // Cuando se termina todo se indica que el
                                // botón del ratón se deje de pulsa
                                map.release();

                            } catch (AWTException ex) {
                                Logger.getLogger(LeapListener.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                }

                /*System.out.println("Swipe id: " + swipe.id()
                                + ", " + swipe.state() + ", position: "
                                + swipe.position() + ", direction: "
                                + swipe.direction() + ", speed: "
                                + swipe.speed() + ", number: " + nFingers);*/
                break;

                /*case TYPE_SCREEN_TAP:
                        
                        value = 1;
                        
                        ScreenTapGesture screenTap = new ScreenTapGesture(gesture);
                        
                        if(nFingers == value){
                            
                            
                            
                        }
                                                
                        System.out.println("Screen Tap id: " + screenTap.id()
                                + ", " + screenTap.state() + ", position: "
                                + screenTap.position() + ", direction: "
                                + screenTap.direction());
                                                
                        break;

                    case TYPE_KEY_TAP:

                        KeyTapGesture keyTap = new KeyTapGesture(gesture);

                        System.out.println("Key Tap id: " + keyTap.id()
                                + ", " + keyTap.state() + ", position: "
                                + keyTap.position() + ", direction: "
                                + keyTap.direction());
                        
                        break;*/
            }

        }

    }

}
