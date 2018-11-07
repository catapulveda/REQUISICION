package view;

import DAO.OrdenDeCompraDAO;
import com.jfoenix.controls.JFXTabPane;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import model.Conexion;
import model.OrdenDeCompra;

public class OrdenesDeCompraController implements Initializable {

    @FXML AnchorPane anchorPane;
    
    public @FXML JFXTabPane tabPane;
    
    @FXML Tab tabOrden;
    
    @FXML TableView<OrdenDeCompra> tablaOrdenes;
    ObservableList<OrdenDeCompra> listaOrdenes = FXCollections.observableArrayList();
    
    @FXML TableColumn colItem;
    @FXML TableColumn colOpciones;
    @FXML TableColumn colNumeroOrden;
    @FXML TableColumn colProveedor;
    @FXML TableColumn<OrdenDeCompra, String> colCentroDeCostos;
    
    OrdenDeCompraDAO ocDAO = new OrdenDeCompraDAO();
    
    Conexion con;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
                
        con = new Conexion();
        try {
            listaOrdenes.setAll(ocDAO.getOrdenes(con));
        } catch (Exception ex) {
            util.Metodos.alert("Error", "No se cargaron las ordendes de compra.", null, Alert.AlertType.ERROR, ex, null);
        }finally{
            con.CERRAR();
        }
        tablaOrdenes.setItems(listaOrdenes);
        
        colItem.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OrdenDeCompra, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<OrdenDeCompra, Integer> p) {
                return new ReadOnlyObjectWrapper(p.getValue().getIdordendecompra());
            }
        });
        colItem.setCellFactory(tc -> new util.NumberRowCell<>()); 
        
        colNumeroOrden.setCellValueFactory(new PropertyValueFactory("numerodeorden"));
        colNumeroOrden.setStyle("-fx-alignment: CENTER;");
        
        colProveedor.setCellValueFactory(new PropertyValueFactory("proveedor"));
        
        colCentroDeCostos.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getCentrodecostos());
        });
        colCentroDeCostos.setStyle("-fx-alignment: CENTER;");
        
        colOpciones.setCellFactory(tc -> new util.ButtonCellOrdenes<>(this)); 
        
        for (int i = 1; i < tablaOrdenes.getColumns().size(); i++) {
            util.Metodos.changeSizeOnColumn(tablaOrdenes.getColumns().get(i), tablaOrdenes);
        }
        colOpciones.setMinWidth(110);
    }
    
}
