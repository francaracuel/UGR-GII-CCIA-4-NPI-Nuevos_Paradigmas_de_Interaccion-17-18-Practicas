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
// MapPanel
//
// JFrame que contiene la imagen y el controlador de los movimientos sobre ella
//
////////////////////////////////////////////////////////////////////////////////

package leapappmuseum;

/**
 * https://github.com/aterai/java-swing-tips/tree/master/ZoomAndPanPanel/src/java/example
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public final class MapPanel extends JPanel {
    
    // Clase que contiene la imagen y sus movimientos
    private ZoomAndPanePanel zAP;
    
    // JFrame donde se añade la imagen
    private final JFrame frame;

    public MapPanel() {
                        
        super(new BorderLayout());
        try {
            
            // Se guarda la imagen que se quiere mostrar
            Image img = ImageIO.read(getClass().getResource("plano.jpg"));
            
            // Se inicializa el JPanel con la imagen
            zAP = new ZoomAndPanePanel(img);
            
            // Se añade el escuchador con el scroll para el zoom
            add(new JScrollPane(zAP));
            
        } catch (IOException ex) {
            
        }
        
        // Se inicializa el JFrame
        frame = new JFrame("ZoomAndPanPanel");
        
    }
    
    // Aumenta o disminuye el zoom dependiendo de la opción que se quiere enviar
    public void zoom(int dir) throws AWTException{
               
        // La clase Robot simula el movimiento del ratón
        Robot r = new Robot();
        
        // Se indica hacia donde debe girar la rueda simulada del ratón (1 
        // abajo, -1 arriba)
        r.mouseWheel(dir);
        
        // Para no saturar la interfaz y que se mueva correctamente con la mano
        // se paraliza 50 ms la aplicación
        try{ 
            Thread.sleep(50); 
        }catch(InterruptedException e){
        }
                        
    }
    
    // Presiona el botón principal del ratón simulado
    public void press() throws AWTException{
                       
        Robot r = new Robot();

        // Se presiona el botón principal
        r.mousePress(InputEvent.BUTTON1_MASK);
                
    }
    
    // Libera el botón principal del ratón simulado
    public void release() throws AWTException{
                       
        Robot r = new Robot();

        // Libera el botón principal
        r.mouseRelease(InputEvent.BUTTON1_MASK);
                
    }
    
    // Mueve el ratón simulado las coordenadas x, y que recibe por parámetro
    public void drag(int x, int y) throws AWTException{
        
        // Se obtienen las coordenadas actuales del ratón
        Point coord = MouseInfo.getPointerInfo().getLocation();
        
        //System.out.println("RATÓN: X: "+coord.x+", Y: "+coord.y);
        
        // Dependiendo de la resolución del monitor, los píxeles pueden variar.
        // Solo se actualizan los puntos hacia donde debe moverse el ratón si 
        // están dentro de un intervalo de la pantalla
        if(coord.x + x<1350 && coord.x + x>100){
            x = coord.x + x;
        }else{
            x = coord.x;
        }
        
        if(coord.y + y<750 && coord.y + y>50){
            y = coord.y + y;
        }else{
            y = coord.y;
        }
        
        //System.out.println("X final: "+x+", Y final: "+y);
               
        Robot r = new Robot();
        
        // Se mueve el ratón a la posición calculada
        r.mouseMove(x, y);
        
        // Se paraliza la aplicación para no saturarla y que se mueva muy rápido
        try{ 
            Thread.sleep(50); 
        }catch(InterruptedException e){
        }
        
        this.release();
        
        r.mouseMove((int)1024/2, (int)768/2);
                
        //this.press();
        
    }
    
    // Libera el botón principal del ratón simulado
    public void show_nfc(String title, String message) throws AWTException{
                       
        JOptionPane pane = new JOptionPane(message,
                JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(null, title);
        dialog.setModal(false);
        dialog.setVisible(true);

        new Timer(5000, (ActionEvent e) -> {
            dialog.setVisible(false);
        }).start();
                
    }
    
    // Cierra la aplicación
    public void close(){
        
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        
    }
        
    // Crea el JFrame con todos los elementos
    public void createAndShowGUI() {
        
        try {
            
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        
        }
          
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // Se añade el JPanel con la imagen
        frame.getContentPane().add(new MapPanel());
        
        // Se inicia el JFrame maximizado
        frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        
        // No se puede cambiar el tamaño de la ventana ni contiene ningún tipo
        // de marco en ella
        frame.setResizable(false);
        frame.setUndecorated(true);
        
        frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
                
        frame.pack();
        
        // Se coloca la ventana en el centro de la pantalla
        frame.setLocationRelativeTo(null);
        frame.setLocation(0,0);
        
        // Se activa el JFrame
        frame.setVisible(true);
        
    }
    
}

// Clase que contiene el JPanel con la imagen
class ZoomAndPanePanel extends JPanel {
        
    protected final AffineTransform zoomTransform = new AffineTransform();
    protected final transient Image img;
    protected final Rectangle imgrect;
    protected transient ZoomHandler handler;
    protected transient DragScrollListener listener;

    protected ZoomAndPanePanel(Image img) {
        
        super();
        
        this.img = img;
        this.imgrect = new Rectangle(img.getWidth(this), img.getHeight(this));
                
    }
    
    // Método que pinta la imagen con el tamaño correspondiente
    @Override protected void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new Color(0x55FF0000, true));
        
        g2.drawImage(img, zoomTransform, this);
        
        g2.dispose();
        
    }
    
    // Devuelve el tamaño de la imagen
    @Override public Dimension getPreferredSize() {
        
        Rectangle r = zoomTransform.createTransformedShape(imgrect).getBounds();
        return new Dimension(r.width, r.height);
        
    }
    
    // Método que se ejecuta cada vez que se actualiza la vista de la ventana
    @Override public void updateUI() {
        
        // Se eliminan los escuchadores de eventos
        removeMouseListener(listener);
        removeMouseMotionListener(listener);
        removeMouseWheelListener(handler);
        
        // Se actualiza la interfaz
        super.updateUI();
        
        // Se añade el escuchador de los movimientos del ratón y los botones
        listener = new DragScrollListener();
        addMouseListener(listener);
        addMouseMotionListener(listener);
        
        // Se añade el escuchador de la rueda del ratón
        handler = new ZoomHandler();
        addMouseWheelListener(handler);
        
    }

    // Clase que contiene el escuchador de la rueda del ratón
    protected class ZoomHandler extends MouseAdapter {
        
        private static final double ZOOM_MULTIPLICATION_FACTOR = 1.2;
        private static final int MIN_ZOOM = -10;
        private static final int MAX_ZOOM = 10;
        private static final int EXTENT = 1;
        private final BoundedRangeModel zoomRange = 
            new DefaultBoundedRangeModel(0, EXTENT, MIN_ZOOM, MAX_ZOOM + EXTENT);
        
        @Override public void mouseWheelMoved(MouseWheelEvent e) {
            
            // Se obtiene la dirección del ratón (hacia arriba o hacia abajo)
            int dir = e.getWheelRotation();
            
            // Se calcula cuánto zoom debe hacerse
            int z = zoomRange.getValue();
            zoomRange.setValue(z + EXTENT * (dir > 0 ? -1 : 1));
            
            if (z != zoomRange.getValue()) {
                
                Component c = e.getComponent();
                Container p = SwingUtilities.getAncestorOfClass(JViewport.class, c);
                
                if (p instanceof JViewport) {
                    
                    JViewport vport = (JViewport) p;
                    
                    Rectangle ovr = vport.getViewRect();
                    
                    double s = dir > 0 ? 1d / ZOOM_MULTIPLICATION_FACTOR : ZOOM_MULTIPLICATION_FACTOR;
                    zoomTransform.scale(s, s);
                                    
                    Rectangle nvr = AffineTransform.getScaleInstance(s, s).createTransformedShape(ovr).getBounds();
                    
                    Point vp = nvr.getLocation();
                    
                    // Cuando se hace zoom puede cambiar la posición central
                    // del punto, de este modo se mantiene siempre en el centro
                    vp.translate((nvr.width - ovr.width) / 2, (nvr.height - ovr.height) / 2);
                    vport.setViewPosition(vp);
                    
                    c.revalidate();
                    c.repaint();
                    
                }
                
            }
            
        }
        
    }
    
}

// Clase que contiene el escuchador del movimiento y botones del ratón
class DragScrollListener extends MouseAdapter {

    private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final Point pp = new Point();
    
    Point cp = pp;
    Point vp;
    JViewport vport;
    
    // Método que se ejecuta cuando se mueve el ratón
    @Override
    public void mouseDragged(MouseEvent e) {
        
        Component c = e.getComponent();
        Container p = SwingUtilities.getUnwrappedParent(c);
        
        if (p instanceof JViewport) {
            
            vport = (JViewport) p;
            
            cp = SwingUtilities.convertPoint(c, e.getPoint(), vport);
            
            vp = vport.getViewPosition();
            
            // Se mueve la imagen la posición correspondiente a lo que se mueve
            // el ratón
            vp.translate(pp.x - cp.x, pp.y - cp.y);
                      
            ((JComponent) c).scrollRectToVisible(new Rectangle(vp, vport.getSize()));
            
            pp.setLocation(cp);
                                    
        }
        
    }

    // Se ejecuta cuando se presiona un botón
    @Override
    public void mousePressed(MouseEvent e) {
        
        Component c = e.getComponent();
        c.setCursor(hndCursor);
        Container p = SwingUtilities.getUnwrappedParent(c);
        
        if (p instanceof JViewport) {
            
            JViewport vport = (JViewport) p;
            Point cp = SwingUtilities.convertPoint(c, e.getPoint(), vport);
            pp.setLocation(cp);
            
        }
        
    }

    // Se ejecuta cuando se libera un botón
    @Override
    public void mouseReleased(MouseEvent e) {
        
        e.getComponent().setCursor(defCursor);
        
    }
    
    public Point getPoint(){
        return vp;
    }
    
    public JViewport getViewPort(){
        return vport;
    }
    
    /*public void drag(MouseEvent e) {
        
        Component c = e.getComponent();
        Container p = SwingUtilities.getUnwrappedParent(c);
        
        if (p instanceof JViewport) {
            
            vport = (JViewport) p;
            
            cp = SwingUtilities.convertPoint(c, e.getPoint(), vport);
            
            vp = vport.getViewPosition();
            
            vp.translate(pp.x - cp.x, pp.y - cp.y);
                      
            ((JComponent) c).scrollRectToVisible(new Rectangle(vp, vport.getSize()));
            
            pp.setLocation(cp);
                                    
        }
        
    }

    public void handAppear(MouseEvent e) {
        
        Component c = e.getComponent();
        c.setCursor(hndCursor);
        Container p = SwingUtilities.getUnwrappedParent(c);
        
        if (p instanceof JViewport) {
            
            JViewport vport = (JViewport) p;
            Point cp = SwingUtilities.convertPoint(c, e.getPoint(), vport);
            pp.setLocation(cp);
            
        }
        
    }

    public void handRelease(MouseEvent e) {
        
        e.getComponent().setCursor(defCursor);
        
    }*/
        
}
