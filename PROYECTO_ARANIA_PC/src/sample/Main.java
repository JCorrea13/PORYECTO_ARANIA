package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        System.out.println("Inicio Programa");
        //launch(args);

        /*int a1 = 1;
        int a2 = 2;

        int a = (a1 << 8);
        System.out.println("Valor a: " + a);*/



        byte [] array1 = "asdf".getBytes();
        byte [] array2 = "ghjk".getBytes();


        byte [] array_union = unir(array1, array2);

        System.out.println("Union");
        System.out.println(new String(array_union));
    }

    private static byte [] unir(byte [] array1, byte [] array2){
        byte [] union = new byte [array1.length+array2.length];

        int i = 0;
        for(; i < array1.length; i ++)
            union[i] = array1[i];

        for(int j = 0; j < array2.length; j ++, i++)
            union[i] = array2[j];

        return union;
    }



}
