package fx;

import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.OrdenDeCompra;

/**
 *
 * @author AUXPLANTA
 */
public class Main extends Application {
    
    private double xOffset = 0;
    private double yOffset = 0;
    
    @Override
    public void start(Stage primaryStage) throws IOException {
               
        FXMLLoader loader = new FXMLLoader();
        Parent mainPane = loader.load(getClass().getResourceAsStream(NavegadorDeContenidos.LOGIN));
        mainPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
                
        mainPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }
        });

        primaryStage.setTitle("Iniciar sesión");
        
        Scene scene = new Scene(mainPane);
        scene.getStylesheets().add("bootstrapfx.css");
        
        primaryStage.setScene(scene);        
        
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/LOGO_CONSORCIO.png").toString()));
        primaryStage.setResizable(false);
        
        primaryStage.show();        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
