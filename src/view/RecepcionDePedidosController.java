package view;

import DAO.RecepcionDePedidoDAO;
import com.jfoenix.controls.JFXListView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Conexion;
import model.Cotizacion;
import model.Pedido;

public class RecepcionDePedidosController implements Initializable {

    @FXML AnchorPane anchorPane;
    @FXML VBox vbox;
    @FXML ToolBar toolBar;
    @FXML TableView<model.RecepcionDePedido> tabla;
    
    @FXML TableColumn colFecha;
    @FXML TableColumn colCantidad;
    @FXML TableColumn colValorFinal;
    @FXML TableColumn colFactura;
    @FXML TableColumn colRemision;
    @FXML TableColumn colObservacion;
    @FXML TableColumn colFechaRegistro;
    
    @FXML Button btnAgregar;
    @FXML Button btnAdjuntar;
    
    RecepcionDePedidoDAO rpDAO = new RecepcionDePedidoDAO();
    
    Conexion con;
    
    private Pedido ped = new Pedido();
    ObservableList<Cotizacion> listaFacturas = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colFecha.setCellValueFactory( new PropertyValueFactory("fechaderecibido"));
        colCantidad.setCellValueFactory( new PropertyValueFactory("cantidadrecibida"));
        colValorFinal.setCellValueFactory( new PropertyValueFactory("preciofinal"));
        colFactura.setCellValueFactory( new PropertyValueFactory("factura"));
        colRemision.setCellValueFactory( new PropertyValueFactory("remision"));
        colObservacion.setCellValueFactory( new PropertyValueFactory("observaciones"));
        colFechaRegistro.setCellValueFactory( new PropertyValueFactory("fechaderegistro"));                
    }    

    @FXML
    void agregarFactura(ActionEvent evt) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fx.NavegadorDeContenidos.REGISTRAR_FACTURA));
        AnchorPane root = loader.load();
        RegistrarRecepcionDePedidoController rpc = (RegistrarRecepcionDePedidoController)loader.getController();
        rpc.setPedido(getPed());
        
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);  
        stage.showAndWait();
        tabla.getItems().clear();
        setPed(getPed());
        tabla.setDisable(false);
    }
    
    @FXML
    void verFacturas(){
        if(listaFacturas.isEmpty()){
            util.Metodos.alert("Mensaje", "No hay facturas adjuntas", null, Alert.AlertType.INFORMATION, null, null);
            return;
        }
        JFXListView<Cotizacion> lista = new JFXListView();
        listaFacturas.forEach(e->{
            lista.getItems().add(e);
        });
        lista.setCellFactory((ListView<Cotizacion> list) -> {
            ListCell<Cotizacion> listCell = new ListCell<Cotizacion>(){
                @Override
                protected void updateItem(Cotizacion item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty){
                        setGraphic(null);
                        setText(null);
                    } else {
                        switch(item.getFormato()){
                            case "pdf":setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_PDF_ALT));
                                break;
                            case "xls":setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_EXCEL_ALT));
                                break;
                            case "xlsx":setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_EXCEL_ALT));
                                break;
                            case "png":setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_IMAGE_ALT));
                                break;
                            case "jpg":setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_IMAGE_ALT));
                                break; 
                            default: setGraphic(new FontAwesomeIconView(FontAwesomeIcon.QUESTION_CIRCLE_ALT));
                        }                                                                           
                        setText(item.toString());
                    }
                }
            };
            return listCell;            
        });
        lista.setOnMouseClicked(evt->{
            if(evt.getClickCount()==2&&evt.getButton()==MouseButton.PRIMARY){
                Cotizacion c = lista.getSelectionModel().getSelectedItem();
                try {                
                    Desktop.getDesktop().open(c.getFile().toFile());
                } catch (IOException ex) {
                    Logger.getLogger(RequisicionNuevaController2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        AnchorPane.setTopAnchor(lista, 0.0);
        AnchorPane.setLeftAnchor(lista, 0.0);
        AnchorPane.setRightAnchor(lista, 0.0);
        AnchorPane.setBottomAnchor(lista, 0.0);
        
        AnchorPane ap = new AnchorPane(lista);        
        Scene scene = new Scene(ap, 320, 240);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    public Pedido getPed() {
        return ped;
    }

    public void setPed(Pedido ped) {
        this.ped = ped;
        try {
            con = new Conexion();
            tabla.getItems().addAll(rpDAO.getPedidos(this.ped, con));
            tabla.refresh();            
            for (int i = 0; i < tabla.getColumns().size(); i++) {
                util.Metodos.changeSizeOnColumn(tabla.getColumns().get(i), tabla);
            }
            String sql = "SELECT f.* FROM recepciondepedidos rp\n" +
            "INNER JOIN facturas f ON f.idrecepciondepedido=rp.idrecepciondepedido\n" +
            "WHERE idpedido="+ped.getIdpedido()+";";
            ResultSet rs = con.CONSULTAR(sql);
            while(rs.next()){
                Cotizacion cot = new Cotizacion();
                cot.setArchivo(rs.getBytes("archivo"));
                cot.setFormato(rs.getString("formato"));
                cot.setNombre(rs.getString("nombrearchivo"));
                
                listaFacturas.add(cot);
            }
            btnAdjuntar.setText("("+listaFacturas.size()+")");
            con.CERRAR();
        } catch (SQLException ex) {
            Logger.getLogger(RecepcionDePedidosController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
