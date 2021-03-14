package sample;
import javafx.application.Application;

public class Controller {
    public static void main(String[] args) {
    new Thread() {
        @Override
        public void run() {
            javafx.application.Application.launch(Main.class);
        }
    }.start();
    }
}
