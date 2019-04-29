package view;

import fx.NavegadorDeContenidos;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author AUXPLANTA
 */
public class MainController implements Initializable {

    @FXML ToolBar toolBar;
    @FXML ImageView logo;
    @FXML BorderPane mainBorderPane;

    @FXML Button btnProductos;
    @FXML Button btnProveedores;
    @FXML Button btnRequisiciones;
    @FXML Button btnCompras;
    @FXML Button btnBuscar;
    @FXML MenuItem menuSalir;
    
    @FXML MenuItem menuCentroDeCostos;
    @FXML MenuItem menuClientes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        util.Metodos.efectoShadowInToolBar(toolBar);

        btnCompras.setTooltip(new Tooltip("Compras"));
        btnProveedores.setTooltip(new Tooltip("Proveedores"));
        btnRequisiciones.setTooltip(new Tooltip("Requisiciones"));
        btnProductos.setTooltip(new Tooltip("Productos"));

        btnProductos.setOnAction(action -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                AnchorPane root = loader.load(getClass().getResourceAsStream(NavegadorDeContenidos.PRODUCTOS));
                mainBorderPane.setCenter(root);
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        btnProveedores.setOnAction(action -> {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource(NavegadorDeContenidos.PROVEEDORES));
                mainBorderPane.setCenter(root);
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }                        
        });
        
        btnRequisiciones.setOnAction(action -> {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource(NavegadorDeContenidos.REQUISICIONES));
                mainBorderPane.setCenter(root);
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }                        
        });
        
        btnCompras.setOnAction(action -> {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource(NavegadorDeContenidos.ORDENES));
                mainBorderPane.setCenter(root);
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }                        
        });
        
        btnBuscar.setOnAction(action -> {
            Parent root;
            try {
                root = FXMLLoader.load(getClass().getResource(NavegadorDeContenidos.BUSCAR));
                mainBorderPane.setCenter(root);
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }                        
        });

    }

    public void setContent(Node node) {
        ((Stage)mainBorderPane.getScene().getWindow()).setTitle(node.toString());
//        mainBorderPane.getChildren().setAll(node);
        mainBorderPane.setCenter(node);
    }
    
    @FXML
    void salir(ActionEvent e){
        System.exit(0);
    }
    
    @FXML 
    void editarCentroDeCostos(ActionEvent e) throws IOException{
        AnchorPane ap = FXMLLoader.load(getClass().getResource("/view/RegistrarCentroDeCostos.fxml"));
        Scene scene = new Scene(ap);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Stage)mainBorderPane.getScene().getWindow()));
        stage.setTitle("Registrar Centro de Costos");
        scene.getStylesheets().add("bootstrapfx.css");
        stage.showAndWait();
    }
    
    @FXML
    void editarClientes(ActionEvent e) throws IOException{
        AnchorPane ap = FXMLLoader.load(getClass().getResource("/view/RegistrarCliente.fxml"));
        Scene scene = new Scene(ap);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((Stage)mainBorderPane.getScene().getWindow()));
        stage.setTitle("Registrar Clientes");
        scene.getStylesheets().add("bootstrapfx.css");
        stage.showAndWait();
    }
}
