package principal;

import algoritmo.AEstrella;
import algoritmo.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Core;
import pdi.HoughCirclesRun;
import servidor.Servicio;

import java.io.IOException;
import java.util.Scanner;


public class Main extends Application{

    private static AEstrella aEstrella;
    private static ManejadorComandos mc;

    private static FXMLLoader loader_gui;
    private static Parent gui_parent;
    private static Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        loader_gui = new FXMLLoader(getClass().getResource("Gui.fxml"));
        gui_parent = loader_gui.load();
        controller = loader_gui.getController();


        //Parent root = FXMLLoader.load(getClass().getResource("Gui.fxml"));

        primaryStage.setTitle("A*");
        primaryStage.setScene(new Scene(gui_parent, 1136, 654));

        primaryStage.show();
    }

    public static void main(String [] args) throws InterruptedException {
        //System.load("C:\\OpenCV\\opencv\\build\\java\\opencv-310");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        mc = new ManejadorComandos(new ManejadorComandos.CallBack() {
            @Override
            public void onAck(char comando, int ack) {
                System.out.println("Comando [" + comando + "]: "+ ack);

                switch (comando){
                    case 'H': aEstrella.onDistanciaAck(ack);
                        controller.updateLog("Distancia: " + ack);
                        break;
                    default:
                        aEstrella.onMovimientoEjecutado(ack);
                        controller.updateLog("Comando ejecutado [" + comando+ "]");
                        break;
                }
            }

            @Override
            public void onAck(char comando, int ack, byte[] img) throws IOException {
                controller.onImagenAck(img);
                controller.updateLog("Imagen recibida [" + img.length + "]");
                aEstrella.onImagenAck(img);
            }
        });
        aEstrella = AEstrella.getaEstrella(null, mc);

        Servicio servicio = new Servicio(1500, mc);
        servicio.inicia();
        mc.setServer(servicio);

        launch();


//        Scanner scanner = new Scanner(System.in);
//        String line = "";
//        while(!line.equals("quit")){
//            line = scanner.nextLine();
//            mc.enviaComando(line.charAt(0));
//        }

        servicio.close();
    }



    /**
     * Este metodo envia el comando que pasa como parametro
     * al celular.
     * @param comando comando que se desaa ejecutar
     */
    public static void  ejecutaComando(char comando){
        controller.updateLog("Comando enviado [" + comando + "]");
        mc.enviaComando(comando);
    }

    public static ManejadorComandos getMc() {
        return mc;
    }
}
