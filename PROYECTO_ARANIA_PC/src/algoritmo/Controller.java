package algoritmo;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import pdi.HoughCirclesRun;
import principal.Main;
import principal.ManejadorComandos;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

    @FXML private Canvas canvas;
    @FXML private RadioButton rb_normal;
    @FXML private RadioButton rb_bloqueo;
    @FXML private RadioButton rb_origen;
    @FXML private RadioButton rb_destino;
    @FXML private TextField txt_x;
    @FXML private TextField txt_y;
    @FXML private Button aplicar;
    @FXML private Button btn_ejecuta;

    //Controles
    @FXML private ImageView img_view;
    @FXML private Button btn_adelante;
    @FXML private Button btn_atras;
    @FXML private Button btn_gira_izquierda;
    @FXML private Button btn_gira_derecha;
    @FXML private Button btn_imagen;
    @FXML private Button btn_distancia;

    @FXML private TextArea txt_log;



    private GraphicsContext gc;
    private ArrayList<Selector> selectores = new ArrayList();
    private Selector selector_actual;
    private int ancho_cuadros;
    private int alto_cuadros;
    private Nodo [][] nodos;

    //Referencias a los nodos de interes
    private Nodo origen;
    private Nodo destino;
    private ArrayList<Nodo> bloqueos = new ArrayList<>();
    private AEstrella aEstrella;

    @FXML
    public void initialize(){
        //evento para click del botno principal de mouse
        canvas.setOnMouseClicked(e->onClickCanvas(e.getX(), e.getY()));
        //evento para drag del mouse
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.isPrimaryButtonDown()){
                    double x = event.getX();
                    double y = event.getY();

                    if(x > canvas.getWidth()-1)
                        x = canvas.getWidth()-1;
                    else if( x < 0)
                        x = 0;

                    if(y > canvas.getHeight()-1)
                        y = canvas.getHeight()-1;
                    else if(y < 0)
                        y = 0;


                    onDragCanvas(x, y);
                }
            }
        });

        inicializaSelectores();
        inicializa();
        aEstrella = AEstrella.getaEstrella(nodos, Main.getMc());
        aEstrella.setNodos(nodos);
        inicializaControles();
        img_view.setRotate(90);
    }

    /**
     * Este metodo inicializa la interfaz grafica
     */
    public void inicializa(){
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.CYAN);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());


        origen = null;
        destino = null;
        bloqueos = new ArrayList<>();
        camino = new ArrayList<>();
        inicializaCuadros();
        inicializaNodos();
    }

    /**
     * Este metodo pinta calcula el tamaño de los cuadros
     * de la gui y los pinta
     */
    public void inicializaCuadros(){
        int x = Integer.valueOf(txt_x.getText());
        int y = Integer.valueOf(txt_y.getText());

        ancho_cuadros =(int)(canvas.getWidth()/x);
        alto_cuadros =(int)(canvas.getHeight()/y);

        gc.setFill(Color.WHITE);
        for(int i = 0; i < (int)(canvas.getWidth()/ancho_cuadros); i++){
            for(int j = 0; j < (int)(canvas.getHeight()/alto_cuadros); j++){
                    gc.fillRect((i*ancho_cuadros)+1, (j*alto_cuadros)+1 , ancho_cuadros-2, alto_cuadros-2);
            }
        }
    }

    /**
     * Este metodo se ejecuta cuando se hace click en el boton
     * Aplicar de la interfaz grafica
     */
    @FXML private void onAplicar(){
        inicializa();
    }

    /**
     * Este metod inicializa los selectores (RadioButtons) de la
     * gui y se les asigna un color
     */
    private void inicializaSelectores(){
        //agregamos los selectores a la lista
        selectores.add(new Selector(rb_normal, Color.WHITE));
        selectores.add(new Selector(rb_bloqueo, Color.GREEN));
        selectores.add(new Selector(rb_origen, Color.RED));
        selectores.add(new Selector(rb_destino, Color.BLUE));
        rb_normal.setSelected(true);
        selector_actual = selectores.get(0);

        //agregamos los listeners a los selectores
        for(Selector s: selectores)
            s.getRadioButton().setOnAction(e->onClickRadioButtons(s.getRadioButton()));
    }

    /**
     * Este metodo se ejcuta cuando se hace click sobre un RadioButton en la
     * gui.
     * Selecciona el RadioButton que pasa como parametro y desselecciona elr resto
     * de los RadioButtons que esten en la lista de selectores
     * @param rb
     */
    private void onClickRadioButtons(RadioButton rb){
        for(Selector selector: selectores)
            if(selector.getRadioButton() != rb)
                selector.getRadioButton().setSelected(false);
            else
                selector_actual = selector;
    }

    /**
     * este metodo se ejecuta cuando se hace click sobre el canvas de la
     * gui.
     * Pinta el cuandron en el que se hizo click del color del selector acutal.
     * @param pos_x posicion en x en la que se hizo el click
     * @param pos_y posicion en y en la que se hizo el click
     */
    private void onClickCanvas(double pos_x, double pos_y){
        int cuadro_x = (int) pos_x/ancho_cuadros;
        int cuadro_y = (int) pos_y/alto_cuadros;

        seteaVariablesAlgoritmo(cuadro_x, cuadro_y);

        gc.setFill(selector_actual.getColor());
        gc.fillRect((cuadro_x*ancho_cuadros)+1, (cuadro_y*alto_cuadros)+1 , ancho_cuadros-2, alto_cuadros-2);
    }

    /**
     * Este metodo se ejecuta cuando se hace drag en el canvas
     * con el boton primario del mouse aretado.
     * Pinta los cuadros por los que se desliza del color del
     * selector acual.
     *
     * @param pos_x posicion en x del drag.
     * @param pos_y posicion en y del drag.
     */
    private void onDragCanvas(double pos_x, double pos_y){
        onClickCanvas(pos_x, pos_y);
    }


    /**
     * Este metodo incializa la matriz o mapa de nodos
     * se debe ejecuatar cada que se preciene el boton aplicar de la gui
     * ya que en ese momento cambia el tamaño de la matriz
     */
    private void inicializaNodos(){
        nodos = new Nodo[Integer.valueOf(txt_x.getText())][Integer.valueOf(txt_y.getText())];

        for(int i = 0; i < nodos.length; i ++)
            for(int j = 0; j < nodos[0].length; j++)
                nodos[i][j] = new Nodo(i,j);
    }


    /**
     * Este metodo setea las variables del algoritmo.
     *
     * Se agrega el nodo[x][y] que pasa como parametro a las listas o variables del
     * algoritmo segun el selector que este seleccionado.
     *
     * Ej. Si el selector seleccionado es 'bloqueo'
     *     el nodo[x][y] de la matriz de nodos se mete a la lista de bloqueos
     *
     *     Si el selector seleccionado es 'origen'
     *     el nodo de 'origen' se setea igual a nodo[x][y] de la matriz de nodos
     *
     * @param x posicion en x, del nodo que se agregara
     * @param y posicion en y, del nodo que se agregara
     */
    private void seteaVariablesAlgoritmo(int x, int y){
        if(selector_actual  == selectores.get(2)) { //si esta seleccionado el selector de origen
            if (origen != null) {
                gc.setFill(selectores.get(0).getColor());
                gc.fillRect((origen.getPos_x() * ancho_cuadros) + 1, (origen.getPos_y() * alto_cuadros) + 1, ancho_cuadros - 2, alto_cuadros - 2);
            }
            origen = nodos[x][y];
        }else if(selector_actual  == selectores.get(3)) { //si esta seleccionado el selector de destino
            if (destino != null) {
                gc.setFill(selectores.get(0).getColor());
                gc.fillRect((destino.getPos_x() * ancho_cuadros) + 1, (destino.getPos_y() * alto_cuadros) + 1, ancho_cuadros - 2, alto_cuadros - 2);
            }
            destino = nodos[x][y];
        }else if(selector_actual == selectores.get(0)){ //si esta seleccionado el selector de normal

            if(bloqueos.contains(nodos[x][y]))
                bloqueos.remove(nodos[x][y]);
            else if(nodos[x][y].equals(origen)){
                origen = null;
            }else if(nodos[x][y].equals(destino)){
                destino = null;
            }
        }else if(selector_actual == selectores.get(1)){ //si esta seleccionado el selector de bloqueo
            if(!bloqueos.contains(nodos[x][y]))
                bloqueos.add(nodos[x][y]);

            if(nodos[x][y].equals(origen)){
                origen = null;
            }else if(nodos[x][y].equals(destino)){
                destino = null;
            }
        }
    }

    private ArrayList<Nodo> camino = new ArrayList<>();
    private ArrayList<Nodo> frontera = new ArrayList<>();
    private ArrayList<Nodo> interior = new ArrayList<>();

    /**
     * Este metodo se ejcuta cuando se hace click
     * en le boton ejecutar de la gui.
     *
     * Hace las validacoines de los datos del algoritmo y
     * si se cumplen los requisitos se ejecuta el algoritmo A*
     * y manda pintar los resultados en gui.
     */
    @FXML private void onClickEjecuta() {
        //validamos que los campos esten correctos
        if(!validaCampos()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Selecciona origen y destino", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        //seteamos la matriz de nodos al algoritmo
        aEstrella.setNodos(nodos);
        //seteamos lo lista de bloqueos al algoritmo
        aEstrella.setBloqueos(bloqueos);

        //limpiamos la gui
        limpia();
        //ejecuatamos el A*
        try {
            if(aEstrella.ejecuta(origen, destino) == 0) {
                //si se logro encontrar una ruta se recuperan las listas
                //de frontera, camino e interior y se pintan en gui todos
                //los resultados
                camino = getCamino(origen, destino);
                frontera = aEstrella.getFrontera();
                interior = aEstrella.getInterior();

                pinta(frontera, Color.ORANGE);
                pinta(interior, Color.VIOLET);
                pinta(camino, Color.YELLOW);

            } else
                System.out.printf("ERROR!! No se encontro una ruta");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Este metodo recupera el camino desde el nodo destino
     * hasta el nodo de origen.
     * @param origen nodo origen
     * @param destino nodo destino
     * @return lista del camino ArrayList<Nodo>
     */
    private ArrayList<Nodo> getCamino(Nodo origen, Nodo destino){
        ArrayList<Nodo> camino = new ArrayList<>();
        Nodo actual = destino.getPadre();
        if(actual == null) return null;

        while(!actual.equals(origen)){
            camino.add(actual);
            actual = actual.getPadre();
        }
        return camino;
    }

    /**
     * Este metodo pinta en pantalla los nodos que pasan
     * como parametro del color que pasa como parametro
     * @param nodos nodos a pintar
     * @param color color que se pintara
     */
    private void pinta(ArrayList<Nodo> nodos, Color color){
        gc.setFill(color);
        for(Nodo n: nodos)
            if(!bloqueos.contains(n) && !n.equals(origen) && !n.equals(destino))
                gc.fillRect((n.getPos_x() * ancho_cuadros) + 1, (n.getPos_y() * alto_cuadros) + 1, ancho_cuadros - 2, alto_cuadros - 2);
    }

    /**
     * Este metodo valida los parametros del algoritmo
     * @return True en caso de satisfacerlos, False en caso contrario
     */
    private boolean validaCampos(){
        return !(origen == null || destino == null);
    }

    /**
     * Este meotodo limpia el gui de los caminos del
     * algoritmo
     */
    private void limpia(){
        pinta(camino, Color.WHITE);
        pinta(frontera, Color.WHITE);
        pinta(interior, Color.WHITE);
    }

    /**
     * Este metodo inicializa los controles
     * desde la gui
     */
    private void inicializaControles(){

        btn_adelante.setOnAction((x) -> Main.ejecutaComando('D'));
        btn_atras.setOnAction((x) -> Main.ejecutaComando('G'));
        btn_gira_izquierda.setOnAction((x) -> Main.ejecutaComando('B'));
        btn_gira_derecha.setOnAction((x) -> Main.ejecutaComando('F'));
        btn_imagen.setOnAction((x) -> Main.ejecutaComando('A'));
        btn_distancia.setOnAction((x) -> Main.ejecutaComando('H'));
    }

    public  void onImagenAck(byte [] img){
        Image i = new Image(new ByteArrayInputStream(img));

        BufferedImage bi = new BufferedImage(480,640,BufferedImage.TYPE_3BYTE_BGR);
        bi.getRaster().setDataElements(0,0, 480, 640, img);

        Mat m = img2Mat(bi);
        Mat rotada = new Mat();

        Core.flip(m, rotada, 1);

        updateLog("Cuadrante de circulo = " + new HoughCirclesRun().run(m));

        img_view.setImage(i);
    }

    public static Mat img2Mat(BufferedImage in)
    {
        Mat out;
        byte[] data;
        int r, g, b;

        if(in.getType() == BufferedImage.TYPE_INT_RGB)
        {
            out = new Mat(240, 320, CvType.CV_8UC3);
            data = new byte[320 * 240 * (int)out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, 320, 240, null, 0, 320);
            for(int i = 0; i < dataBuff.length; i++)
            {
                data[i*3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
                data[i*3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
                data[i*3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
            }
        }
        else
        {
            out = new Mat(640, 480, 16);
            data = new byte[640 * 480 * (int)out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, 640, 480, null, 0, 320);
            for(int i = 0; i < dataBuff.length; i++)
            {
                r = (byte) ((dataBuff[i] >> 16) & 0xFF);
                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
                b = (byte) ((dataBuff[i] >> 0) & 0xFF);
                data[i] = (byte)((0.21 * r) + (0.71 * g) + (0.07 * b)); //luminosity
            }
        }
        out.put(0, 0, data);
        return out;
    }

    public  void updateLog(String nuevo_texto){

        Platform.runLater(new Runnable(){
            public void run() {
                txt_log.setText(txt_log.getText()+ "\n" + nuevo_texto);
            }
        });
    }

}
