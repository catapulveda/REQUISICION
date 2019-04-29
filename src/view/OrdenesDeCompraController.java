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
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import model.Conexion;
import model.OrdenDeCompra;
import model.RecepcionDePedido;

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
    @FXML
    private TableColumn colSolicitados;
    @FXML
    private TableColumn colRecibidos;
    
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
        colItem.setCellFactory(tc -> new FormatCell.NumberRowCell<>()); 
        
        colNumeroOrden.setCellValueFactory(new PropertyValueFactory("numerodeorden"));
        colNumeroOrden.setStyle("-fx-alignment: CENTER;");
        
        colProveedor.setCellValueFactory(new PropertyValueFactory("proveedor"));
        
        colCentroDeCostos.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getCentrodecostos());
        });
        colCentroDeCostos.setStyle("-fx-alignment: CENTER;");
        
        colOpciones.setCellFactory(tc -> new FormatCell.ButtonCellOrdenes<>(this)); 
        
        for (int i = 1; i < tablaOrdenes.getColumns().size(); i++) {
            util.Metodos.changeSizeOnColumn(tablaOrdenes.getColumns().get(i), tablaOrdenes);
        }
        colOpciones.setMinWidth(110);
        
        colSolicitados.setCellValueFactory(new PropertyValueFactory("solicitados"));
        colSolicitados.setStyle("-fx-alignment: CENTER;");
        
        colRecibidos.setCellValueFactory(new PropertyValueFactory("recibidos"));
        colRecibidos.setStyle("-fx-alignment: CENTER;");
        colRecibidos.setCellFactory(tc -> new TableCell<OrdenDeCompra, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    OrdenDeCompra occ = getTableView().getItems().get(getIndex());
                    Double solicitados = occ.getSolicitados();
                    double recibidos = occ.getRecibidos();                    
                    setText(String.valueOf(recibidos));
                    setAlignment(Pos.CENTER);
                    setTextFill(Color.ANTIQUEWHITE);
                    if (recibidos < solicitados) {
                        setStyle("-fx-background-insets: 0 0 1 0 ;-fx-background-color: -fx-background;"
                                + "-fx-background: #d14836;-fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-insets: 0 0 1 0 ;-fx-background-color: -fx-background;"
                                + "-fx-background: #3cba54;-fx-font-weight: bold;");
                    }
                }
            }
        });
    }
    
}
