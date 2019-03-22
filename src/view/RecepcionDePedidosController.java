package view;

import DAO.ProductoDAO;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Conexion;
import model.Cotizacion;
import model.Pedido;
import model.RecepcionDePedido;

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
    @FXML private Button btnBorrar;
    
    RecepcionDePedidoDAO rpDAO;
    Conexion con;
    
    private Pedido ped = new Pedido();
    ObservableList<Cotizacion> listaFacturas = FXCollections.observableArrayList();    
    ObjectProperty<RecepcionDePedido> item = new SimpleObjectProperty<>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        listaFacturas.addListener((ListChangeListener.Change<? extends Cotizacion> c) -> {
            btnAdjuntar.setText(""+listaFacturas.size());
        });
        
        colFecha.setCellValueFactory( new PropertyValueFactory("fechaderecibido"));
        colCantidad.setCellValueFactory( new PropertyValueFactory("cantidadrecibida"));
        colValorFinal.setCellValueFactory( new PropertyValueFactory("preciofinal"));
        colFactura.setCellValueFactory( new PropertyValueFactory("factura"));
        colRemision.setCellValueFactory( new PropertyValueFactory("remision"));
        colObservacion.setCellValueFactory( new PropertyValueFactory("observaciones"));
        colFechaRegistro.setCellValueFactory( new PropertyValueFactory("fechaderegistro"));                
        
        item.bind(tabla.getSelectionModel().selectedItemProperty());

        tabla.setRowFactory(tv->{
            TableRow<RecepcionDePedido> row = new TableRow<>();            

            row.setOnDragOver(event -> {
                int dropIndex = (row.isEmpty())?tabla.getItems().size():row.getIndex();
                tabla.getSelectionModel().select(dropIndex);                
            });

            row.setOnDragDropped(e -> {
                final Dragboard db = e.getDragboard(); 
                List<File> lista = e.getDragboard().getFiles();
                if(lista.size()>0){
                    con = new Conexion();
                    Platform.runLater(
                        () -> {
                            lista.forEach((File file) -> {
                                if(file.isFile()){
                                    try {
                                        String nombre = file.getName().substring(0, file.getName().lastIndexOf("."));
                                        String formato = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                                        byte[] bytes = Files.readAllBytes(file.toPath());
                                        Cotizacion cot = new Cotizacion();
                                        cot.setArchivo(bytes);
                                        cot.setFormato(formato);
                                        cot.setNombre(nombre);                                       
                                        String sql = "INSERT INTO facturas (idrecepciondepedido,archivo,formato,nombrearchivo) VALUES ("+tabla.getItems().get(row.getIndex()).getIdrecepciondepedido()+" , ?, '"+cot.getFormato()+"' , '"+cot.getNombre()+"')";
                                        try {
                                            PreparedStatement pst = con.getCon().prepareStatement(sql);
                                            pst.setBytes(1, cot.getArchivo());
                                            if(pst.executeUpdate()>0){
                                                listaFacturas.add(cot);
                                            }
                                        } catch (SQLException ex) {
                                            Logger.getLogger(RegistrarRecepcionDePedidoController.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    } catch (IOException ex) {
                                        Logger.getLogger(RecepcionDePedidosController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            });
                        }
                    );
//                    con.CERRAR();
                    e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    e.setDropCompleted(true);
                    e.consume();
                }else{
                    e.consume();
                }
            });

            return row ;
        });        
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
        lista.setItems(listaFacturas);
//        listaFacturas.forEach(e->{
//            lista.getItems().add(e);
//        });
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
                    Logger.getLogger(RequisicionNuevaController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        lista.setOnKeyPressed(evt->{
            if(evt.getCode()==KeyCode.DELETE){
                Cotizacion c = lista.getSelectionModel().getSelectedItem();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar factura", ButtonType.YES, ButtonType.NO);
                alert.setTitle("Confirmar ");
                alert.setHeaderText("Eliminar "+c.getNombre());
                alert.setContentText("Seguro que desea eliminar la factura seleccionada?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES){
                    con = new Conexion();
                    try {
                        if(con.GUARDAR("DELETE FROM facturas WHERE idfactura="+c.getId())>0){
                            listaFacturas.remove(lista.getSelectionModel().getSelectedIndex());
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(RecepcionDePedidosController.class.getName()).log(Level.SEVERE, null, ex);
                    }
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
    
    @FXML
    void borrarFactura(){
        if(item.get()==null){
            Alert a = new Alert(Alert.AlertType.WARNING, "Seleccione un item de la tabla", ButtonType.OK);
            a.showAndWait();
            return;
        }
        borrar(item.get());
    }
    
    public void borrar(RecepcionDePedido rp){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar factura", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar ");
        alert.setHeaderText("Eliminar factura Nº "+rp.getFactura());
        alert.setContentText("Seguro que desea eliminar la factura seleccionada?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES){
            con = new Conexion();
            rpDAO = new RecepcionDePedidoDAO(con);
            try {
                if(rpDAO.borrar(item.get())>0){
                    tabla.getItems().remove(rp);
                }
            } catch (SQLException ex) {               
                Logger.getLogger(RecepcionDePedidosController.class.getName()).log(Level.SEVERE, null, ex);
                Alert a = new Alert(Alert.AlertType.ERROR, "ERROR AL BORRAR LA FACTURA", ButtonType.OK);
                a.showAndWait();
            }                        
        }
    }
    
    public Pedido getPed() {
        return ped;
    }

    public void setPed(Pedido ped) {
        this.ped = ped;
        try {
            con = new Conexion();
            rpDAO = new RecepcionDePedidoDAO(con);
            tabla.getItems().addAll(rpDAO.getPedidos(this.ped));
            tabla.getSelectionModel().selectFirst();
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
                cot.setId(rs.getInt("idfactura"));
                cot.setArchivo(rs.getBytes("archivo"));
                cot.setFormato(rs.getString("formato"));
                cot.setNombre(rs.getString("nombrearchivo"));
                
                listaFacturas.add(cot);
            }                        
        }catch (SQLException ex) {
            Logger.getLogger(RecepcionDePedidosController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            con.CERRAR();
        }
    }

    @FXML
    private void borrarFacturaKeyPressed(KeyEvent event) {
        if(event.getCode()==KeyCode.DELETE){
            borrar(tabla.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void OnDragOver(DragEvent event) {
        if(event.getDragboard().hasFiles()){
            tabla.setStyle("-fx-border-color: #D14836;-fx-border-width: 2px;");
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();                        
        }
    }
    
    @FXML
    private void OnDragExited(DragEvent event) {
        tabla.setStyle("-fx-border-color: none;-fx-border-width: 0px;");
    }

}
