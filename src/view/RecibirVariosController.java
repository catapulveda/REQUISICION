package view;

import DAO.RecepcionDePedidoDAO;
import FormatCell.CurrencyCell;
import FormatCell.DoubleCell;
import FormatCell.ColorYRecibirPedido;
import com.jfoenix.controls.JFXDatePicker;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Conexion;
import model.RecepcionDePedido;

public class RecibirVariosController implements Initializable {

    @FXML
    private VBox root;
    @FXML
    private TextField cjFactura;
    @FXML
    private TextField cjRemision;
    @FXML
    private JFXDatePicker cjFecha;
    @FXML
    private Button btnGuardar;
    @FXML
    private TableView<RecepcionDePedido> tabla;    
    @FXML
    private TableColumn<RecepcionDePedido, String> colProducto;
    @FXML
    private TableColumn colPendientes;
    @FXML
    private TableColumn colValorFinal;
    
    private ObservableList<RecepcionDePedido> lista = FXCollections.observableArrayList();
    private Conexion con;
    private boolean GUARDADO = false;
    @FXML
    private TableColumn colRecibidas;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colPendientes.setStyle("-fx-alignment: CENTER;");
        colPendientes.setCellValueFactory(new PropertyValueFactory<>("pendiente"));
        
        colRecibidas.setStyle("-fx-alignment: CENTER;");
        colRecibidas.setCellValueFactory(new PropertyValueFactory<>("pendiente"));        
//        colPendientes.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<RecepcionDePedido, Double>>() {
//            @Override
//            public void handle(TableColumn.CellEditEvent<RecepcionDePedido, Double> event) {
//                ((RecepcionDePedido) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPendiente(event.getNewValue());
//            }
//        });
        
        colValorFinal.setCellValueFactory(new PropertyValueFactory<>("preciofinal"));
        colValorFinal.setCellFactory(tc -> new CurrencyCell<>());        
        
        colProducto.setCellValueFactory((param) -> {
            return new SimpleStringProperty(param.getValue().getPedido().getProducto().toString());
        });                                
        
        tabla.getSelectionModel().setCellSelectionEnabled(true);
        tabla.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tabla.setOnKeyPressed(evt->{
            if(evt.getCode().isDigitKey()){
                final TablePosition focusedCell = tabla.focusModelProperty().get().focusedCellProperty().get();
                tabla.edit(focusedCell.getRow(), focusedCell.getTableColumn());
            }
        });
        tabla.setItems(lista);
    }

    public void setListaRecepcionDePedidos(ObservableList<RecepcionDePedido> l) {
        lista.clear();
        lista.addAll(l);
        for (int i = 0; i < tabla.getColumns().size(); i++) {
            util.Metodos.changeSizeOnColumn(tabla.getColumns().get(i), tabla);
        }
    }

    @FXML
    private void guardar(ActionEvent event) throws SQLException {
        
        if(cjFecha.getValue()==null){
            util.Metodos.alert("Mensaje", "Seleccione la fecha de recepcion", null, Alert.AlertType.WARNING, null, null);
            return;
        }
        LocalDate fecha = cjFecha.getValue();
        String factura = cjFactura.getText();
        String remision = cjRemision.getText();
        
        RecepcionDePedido rp = new RecepcionDePedido();        
        rp.setFactura(factura);
        rp.setFechaderecibido(fecha);        
        rp.setRemision(remision);        
        
        con = new Conexion();
        RecepcionDePedidoDAO rpDAO = new RecepcionDePedidoDAO(con);
           
        int n = 0;
        for (RecepcionDePedido i : lista) {
            rp.setCantidadrecibida(i.getPendiente());
            rp.setPreciofinal(i.getPreciofinal());
            rp.setObservaciones("");
            rp.setPedido(i.getPedido());
            if(rpDAO.guardar(rp)>0){
                i.setCantidadrecibida(i.getCantidadrecibida()+i.getPendiente());
                tabla.getSelectionModel().select(n);
                n++;
            }
        }
        con.CERRAR();
        System.out.println("");
        if( GUARDADO=(n==lista.size()) ){
            System.out.println("CERRARRR");
            ((Stage)root.getScene().getWindow()).close();
        }
    }

    public boolean isGUARDADO() {
        return GUARDADO;
    }

    public void setGUARDADO(boolean GUARDADO) {
        this.GUARDADO = GUARDADO;
    }
    
}
