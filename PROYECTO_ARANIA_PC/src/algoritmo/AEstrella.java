package algoritmo;

//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import pdi.HoughCirclesRun;
import principal.ManejadorComandos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class AEstrella {

    private ArrayList<Nodo> frontera;
    private ArrayList<Nodo> interior;
    private ArrayList<Nodo> bloqueos;
    private Nodo nodos [][];
    private final double VALOR_DIAGONAL = 1.5; //Costo que se le dara a caminar en diagonal
    private ManejadorComandos mc;
    private HoughCirclesRun hcr;
    private static AEstrella aEstrella;
    private final int ANGULO_VISION = 46;
    private final int METRO_CUADRO = 60;

    private int distancia_actual = 0;
    private byte [] imagen_actual = null;


    private volatile boolean solicitudComando = false;


    /**
     * Hacemos la clase un Singleton
     */
    public static AEstrella getaEstrella(Nodo nodos [][], ManejadorComandos mc){
        return  aEstrella == null ? new AEstrella(nodos, mc): aEstrella;
    }

    private AEstrella(Nodo nodos [][], ManejadorComandos mc){
        this.nodos = nodos;
        this.mc = mc;
        frontera = new ArrayList<>();
        interior = new ArrayList<>();
        bloqueos = new ArrayList<>();
        hcr = new HoughCirclesRun();
    }


    Nodo actual; //instancia para recorrer la matriz
    /**
     * Este metood ejecuta el algoritmo estrella con la tabla de nodos
     * que pasa como parametro en el constructor
     * @param origen Nodo origen
     * @param destino Nodo destino
     * @return 0 cuando termina con exito, -1 en caso de no encontrar ruta
     */
    public int ejecuta(Nodo origen, Nodo destino) throws IOException {
        frontera = new ArrayList<>();
        interior = new ArrayList<>();

        //agregamos el nodo origen a la lista de frontera
        origen.setH(0);
        origen.setG(Math.abs(origen.getPos_x()-destino.getPos_x()) + Math.abs(origen.getPos_y()-destino.getPos_y()));
        frontera.add(origen);


        while(true) {
            if(frontera.isEmpty()) return -1; //si la lista de frontera esta vacia regrezamos -1

            //Analizamos la imagen para ver si hay un camino marcado
            ejecutaComando('H'); //solicitamos la distancia detectada
            if(distancia_actual > METRO_CUADRO) {
                ejecutaComando('A'); //solicitamos la imagen
                if (analizaImagen()) continue; //Si se encontro un circulo se incia nuevamente
            }

            //tomamos el primer nodo de la lista de frontera
            //lo quitamos de la lista de fronterea y lo agregamos a la de interior
            actual = frontera.get(0);
            interior.add(frontera.get(0));
            frontera.remove(actual);

            //si encontramos el destino regresamos 0
            if(actual.equals(destino)) return 0;


            //agregamos todos los nodos adyacentes al nodo actual  a la lista de frontera
            //calculando las G y H para cada uno
            agregaVesinosaFrontera(actual, destino);

            //ordenamos la lista de frontera
            frontera.sort(new Comparator<Nodo>() {
                @Override
                public int compare(Nodo o1, Nodo o2) {
                    if (o1.getF() > o2.getF()) return 1;
                    else if(o1.getF() < o2.getF())return  -1;

                    return 0;
                }
            });
        }
    }

    /**
     * Este metodo calcula los parametros H y G para cada nodo
     * vecino del nodo que es pasa como parametro
     * @param nodo nodo del que se desean calcular los vecinos
     * @param destino destino al que se quiere llegar
     */
    private void agregaVesinosaFrontera(Nodo nodo, Nodo destino){


        //recorremos todos los nodos adyacentes al 'nodo' que pasa como parametro
        for(int i = nodo.getPos_x()-1; i < nodo.getPos_x()+2; i++)
            for(int j = nodo.getPos_y()-1; j < nodo.getPos_y()+2; j++) {

            //validamos los limites de la matriz
            if(i < 0 || i > nodos.length-1 || j < 0 || j > nodos[0].length-1) continue;

            if(nodos[i][j].equals(nodo)) continue; //validamos el mismo nodo

                //validamos si el nodo[i][j] ya esta en la lista de frontera o interior
            if(frontera.contains(nodos[i][j]) || interior.contains(nodos[i][j])){
                //validamos si es una mejor ruta o no, y si es el caso corregimos la ruta
                if((nodo.getH() + ((nodo.getPos_x() == i || nodo.getPos_y() == j)?1:VALOR_DIAGONAL)) < nodos[i][j].getH()) {
                    nodos[i][j].setH(nodo.getH() + ((nodo.getPos_x() == i || nodo.getPos_y() == j)?1:VALOR_DIAGONAL));
                    nodos[i][j].setPadre(nodo);
                }

            }else{ //cuando el nodo[i][j] no esta en ninguna lista aun
                if(bloqueos.contains(nodos[i][j])) continue; //validamos si el nodo[i][j] esta bloqueado
                //calculamos H y G y lo agregamos a la lista de frontera
                nodos[i][j].setH(nodo.getH() + ((nodo.getPos_x() == i || nodo.getPos_y() == j)?1:VALOR_DIAGONAL));
                nodos[i][j].setG(Math.abs(nodos[i][j].getPos_x() - destino.getPos_x())
                        + Math.abs(nodos[i][j].getPos_y() - destino.getPos_y()));
                nodos[i][j].setPadre(nodo);
                frontera.add(nodos[i][j]);
            }
        }
    }


    /**
     * Este metodo se llama cunado hay un ACK
     * del comando de distancia 'H'
     */
    public void onDistanciaAck(int distacia){
        this.distancia_actual = distacia;
        solicitudComando = false;
    }

    /**
     * Este metodo se llama cuando hay un ACK
     * del comando de Imagen 'A'
     */
    public void onImagenAck(byte [] imagen) {
        this.imagen_actual = imagen;
        solicitudComando = false;
    }

    public void onMovimientoEjecutado(int i){
        solicitudComando = false;
    }

    /**
     * Este metodo ejecuta un comando y espera hasta el ACK
     * @param comando comando que se quiere ejecutar
     */
    private void ejecutaComando(char comando){
        mc.enviaComando(comando); //solicitamos la distancia detectada
        solicitudComando = true;
        while(solicitudComando); //Esperamos a que haya una respuesta de los comandos enviados
    }

    /**
     * Este metodo analiza la imagen que captura el celular
     * para buscar circulos, de encontrarlos camina hacia el
     */
    private boolean analizaImagen() throws IOException {

        Mat img = new Mat(640,408, CvType.CV_8UC1);
        img.put(0,0,imagen_actual);
        int cuadrante_circulo = hcr.run(img);
        int cuadrosXcaminar = 0;

        if(cuadrante_circulo != -1){
            int rango_visibilidad = getRangoVisibilidad(ANGULO_VISION, distancia_actual);
            int metrosXcuadrante = rango_visibilidad/5;


            switch (cuadrante_circulo){
                case 1:
                    cuadrosXcaminar = (metrosXcuadrante/METRO_CUADRO)*2;
                    ejecutaComando('B');
                    for (int i = 0; i < cuadrosXcaminar; i++) ejecutaComando('D');
                    ejecutaComando('F');
                    break;

                case 2:
                    cuadrosXcaminar = (metrosXcuadrante/METRO_CUADRO);
                    ejecutaComando('B');
                    for (int i = 0; i < cuadrosXcaminar; i++) ejecutaComando('D');
                    ejecutaComando('F');
                    break;

                case 3:
                    if(distancia_actual > METRO_CUADRO) {
                        ejecutaComando('D');
                        return false;
                    }
                    break;

                case 4:
                    cuadrosXcaminar = (metrosXcuadrante/METRO_CUADRO);
                    ejecutaComando('F');
                    for (int i = 0; i < cuadrosXcaminar; i++) ejecutaComando('D');
                    ejecutaComando('B');
                    break;

                case 5:
                    cuadrosXcaminar = (metrosXcuadrante/METRO_CUADRO)*2;
                    ejecutaComando('F');
                    for (int i = 0; i < cuadrosXcaminar; i++) ejecutaComando('D');
                    ejecutaComando('B');
                    break;
            }

            return true;
        }

        return false;
    }

    /**
     * Este metodo regresa el rango de vision de la araña
     * @param vision_angle angulo de vision de la camara
     * @param distance distancia detectada
     * @return rango de vision en metros
     */
    public int getRangoVisibilidad(int vision_angle, int distance)
    {
        double alpha=180-(90+(vision_angle/2));
        double sideb=(distance*Math.sin(Math.toRadians(vision_angle/2)))/Math.sin(Math.toRadians(alpha));

        return (int) Math.round(sideb*2);
    }

    public ArrayList<Nodo> getFrontera() {
        return frontera;
    }

    public ArrayList<Nodo> getInterior() {
        return interior;
    }

    public void setNodos(Nodo[][] nodos) {
        this.nodos = nodos;
    }

    public void setBloqueos(ArrayList<Nodo> bloqueos) {
        this.bloqueos = bloqueos;
    }
}
