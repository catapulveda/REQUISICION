package fx;

import view.MainController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;

/**
 *
 * @author AUXPLANTA
 */
public class NavegadorDeContenidos {
    
    //fxml files
    public static String LOGIN = "/view/Login.fxml";
    public static String PRODUCTOS = "/view/Producto.fxml";
    public static String PROVEEDORES = "/view/Proveedor.fxml";
    public static String REQUISICIONES = "/view/Requisicion.fxml";
    public static String ORDENES = "/view/OrdenesDeCompra.fxml";
    public static String RequisicionNueva = "/view/RequisicionNueva2.fxml";
    public static String TablaProductos = "/view/TablaProductos.fxml";
    public static String MAIN = "/view/Main.fxml";
    public static String GUARDAR_ORDENDECOMPRA = "/view/GuardarOrdenDeCompra.fxml";
    public static String RECEPCION_DE_PEDIDOS = "/view/RecepcionDePedidos.fxml";
    public static String REGISTRAR_FACTURA = "/view/RegistrarRecepcionDePedido.fxml";
    public static String BUSCAR = "/view/BuscarTodo.fxml";
    public static String RECIBIR_VARIOS = "/view/RecibirVarios.fxml";

    private static MainController mainController;

    public static void setController(MainController controller) {
        NavegadorDeContenidos.mainController = controller;
    }

    public static void loadContent(String fxmlFile) {
        try {
            System.out.println("mainController -> "+mainController);
            mainController.setContent(FXMLLoader.load(NavegadorDeContenidos.class.getResource(fxmlFile)));
        } catch (IOException ex) {
            Logger.getLogger(NavegadorDeContenidos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}