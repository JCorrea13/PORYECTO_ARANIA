package algoritmo;

import javafx.scene.control.RadioButton;
import javafx.scene.paint.Color;


/**
 * Esta clase modela los selectores de
 * la interfaz grafica
 */
public class Selector {

    private RadioButton radioButton;
    private Color color;

    public Selector(RadioButton radioButton, Color color) {
        this.radioButton = radioButton;
        this.color = color;
    }

    public RadioButton getRadioButton() {
        return radioButton;
    }

    public Color getColor() {
        return color;
    }
}
