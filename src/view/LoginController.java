package view;

import DAO.UsuarioDAO;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import fx.Main;
import fx.NavegadorDeContenidos;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Conexion;

public class LoginController implements Initializable {

    @FXML
    JFXTextField cjusuario;
    @FXML
    JFXPasswordField cjpassword;
    @FXML
    JFXButton btnIngresar;
    @FXML
    AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnIngresar.setOnAction((ActionEvent event) -> {
            String user = cjusuario.getText();
            String password = cjpassword.getText();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    UsuarioDAO userDAO = new UsuarioDAO();
                    if (userDAO.login(user, password)) {
                        FXMLLoader loader = new FXMLLoader();
                        URL location = LoginController.class.getResource(NavegadorDeContenidos.MAIN);
                        loader.setLocation(location);
                        try {
                            BorderPane main = loader.load();
                            Stage stage = new Stage();
                            stage.setTitle("Bienvenido " + model.Usuario.getInstanceUser(0, null, null, null).getNombreusuario());
                            Scene scena = new Scene(main);
                            scena.getStylesheets().add("bootstrapfx.css");
                            stage.setScene(scena);
                            stage.initOwner(anchorPane.getScene().getWindow());
                            stage.setMaximized(true);
                            stage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/LOGO_CONSORCIO.png").toString()));
                            ((Stage) anchorPane.getScene().getWindow()).close();
                            stage.show();
                        } catch (IOException ex) {
                            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        cjusuario.setStyle("-jfx-unfocus-color: red");
                        cjpassword.setStyle("-jfx-unfocus-color: red");
                    }
                }
            });
        });

    }

    @FXML
    void keyPressedUser(KeyEvent evt) {
        if (evt.getCode() == KeyCode.ENTER || evt.getCode() == KeyCode.TAB) {
            cjpassword.requestFocus();
        }
    }

    @FXML
    void keyPressedPassword(KeyEvent evt) {
        if (evt.getCode() == KeyCode.ENTER) {
            btnIngresar.fire();
        }
    }

}
