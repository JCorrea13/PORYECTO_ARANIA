package principal;

import algoritmo.AEstrella;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import servidor.Servicio;

import java.util.Scanner;


public class Main extends Application implements ManejadorComandos.CallBack{

    private static AEstrella aEstrella;
    private static ManejadorComandos mc;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Gui.fxml"));
        primaryStage.setTitle("A*");
        primaryStage.setScene(new Scene(root, 1136, 654));

        primaryStage.show();
    }

    public static void main(String [] args) throws InterruptedException {
        mc = new ManejadorComandos(null);
        aEstrella = AEstrella.getaEstrella(null, mc);

        Servicio servicio = new Servicio(1500, mc);
        servicio.inicia();
        mc.setServer(servicio);

        Scanner scanner = new Scanner(System.in);
        String line = "";
        while(!line.equals("quit")){
            line = scanner.nextLine();
            mc.enviaComando(line.charAt(0));
        }
        servicio.close();
        //launch();
    }

    @Override
    public void onAck(char comando, int ack) {
        System.out.println("Comando [" + comando + "]: "+ ack);

        switch (comando){
            case 'H': aEstrella.onDistanciaAck(ack);
                break;
            default:
                aEstrella.onMovimientoEjecutado(ack);
                break;
        }

    }

    @Override
    public void onAck(char comando, int ack, byte[] img) {
        aEstrella.onImagenAck(img);
    }

    /**
     * Este metodo envia el comando que pasa como parametro
     * al celular.
     * @param comando comando que se desaa ejecutar
     */
    public static void  ejecutaComando(char comando){
        mc.enviaComando(comando);
    }

    public static ManejadorComandos getMc() {
        return mc;
    }
}
