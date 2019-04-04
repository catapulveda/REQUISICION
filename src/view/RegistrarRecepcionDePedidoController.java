package view;

import DAO.RecepcionDePedidoDAO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Conexion;
import model.Cotizacion;
import model.Pedido;
import model.RecepcionDePedido;

public class RegistrarRecepcionDePedidoController implements Initializable {

    @FXML AnchorPane ap;
    
    @FXML DatePicker cjFecha;
    @FXML TextField cjFactura;
    @FXML TextField cjRemision;
    @FXML TextField cjCantidad;
    @FXML TextField cjValor;
    @FXML TextArea cjObservaciones;
    
    @FXML Button btnGuardar;
    
    @FXML HBox hBoxAdjuntos;
    
    private RecepcionDePedido pedido;
    private ObservableList<Cotizacion> listaFacturas = FXCollections.observableArrayList();
    private Conexion con;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {                
        
    }
    
    @FXML
    void guardar(){
        if(cjFecha.getValue()==null){
            util.Metodos.alert("Mensaje", "Seleccione la fecha de recepcion", null, Alert.AlertType.WARNING, null, null);
            return;
        }
        LocalDate fecha = cjFecha.getValue();
        String factura = cjFactura.getText();
        String remision = cjRemision.getText();
        double cantidad = Double.parseDouble(cjCantidad.getText());
        double valor = Double.parseDouble(cjValor.getText());
        String observaciones = cjObservaciones.getText();
        
        RecepcionDePedido rp = new RecepcionDePedido();
        rp.setCantidadrecibida(cantidad);
        rp.setFactura(factura);
        rp.setFechaderecibido(fecha);
        rp.setObservaciones(observaciones);
        rp.setPedido(getRecepcionDePedido().getPedido());
        rp.setPreciofinal(valor);
        rp.setRemision(remision);
        
        con = new Conexion();
        RecepcionDePedidoDAO rpDAO = new RecepcionDePedidoDAO(con);
        try {
            int n = 0;            
            if(listaFacturas.size()>0){
                con.getCon().setAutoCommit(false);
            }
            if((n=rpDAO.guardar(rp))>0){
                getRecepcionDePedido().setCantidadrecibida( (getRecepcionDePedido().getCantidadrecibida()+cantidad) );
                listaFacturas.forEach((e) -> {
                    String sql = "INSERT INTO facturas (idordendecompra,archivo,formato,nombrearchivo) VALUES ("+getRecepcionDePedido().getPedido().getOc().getIdordendecompra()+" , ?, '"+e.getFormato()+"' , '"+e.getNombre()+"')";
                    try {
                        PreparedStatement pst = con.getCon().prepareStatement(sql);
                        pst.setBytes(1, e.getArchivo());
                        if(pst.executeUpdate()>0){
                            con.getCon().commit();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(RegistrarRecepcionDePedidoController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                ((Stage)ap.getScene().getWindow()).close();
            }
        }catch(SQLException ex){
            try{
                con.getCon().rollback();
            }catch(SQLException ex1){
                Logger.getLogger(RegistrarRecepcionDePedidoController.class.getName()).log(Level.SEVERE, null, ex1);
            }
            util.Metodos.alert("Error", "No se pudo guardar", null, Alert.AlertType.ERROR, ex, null);
        }finally{
            con.CERRAR();
        }
    }

    public RecepcionDePedido getRecepcionDePedido() {
        return pedido;
    }

    public void setPedido(RecepcionDePedido pedido) {
        this.pedido = pedido;
    }

    @FXML
    void OnDragDropped( DragEvent e){
        final Dragboard db = e.getDragboard(); 
        List<File> lista = e.getDragboard().getFiles();        
        if(lista.size()>0){
            Platform.runLater(
                () -> {
                    int count = 0;
                    lista.forEach((File file) -> {
                        if(file.isFile()){
                            adjuntarFactura(file);
                        }
                    });
                }
            );
            e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            e.setDropCompleted(true);
            e.consume();
        }else{
            e.consume();
        }
    }
    
    @FXML
    void OnDragOver( DragEvent event){
        if(event.getDragboard().hasFiles()){
            hBoxAdjuntos.setStyle("-fx-border-color: black;-fx-border-width: 1px;");
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        }        
    }
    
    @FXML
    void onDragExited(DragEvent event){
        hBoxAdjuntos.getStyleClass().clear();        
    }

    @FXML
    private void abrirArchivo(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Buscar Imagen");       
        File file = fileChooser.showOpenDialog(ap.getScene().getWindow());
        // Mostar la imagen
        if (file != null) {            
            adjuntarFactura(file.getAbsoluteFile());
        }
    }
    
    public void adjuntarFactura(File file){
        try {
            String nombre = file.getName().substring(0, file.getName().lastIndexOf("."));
            String formato = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            byte[] bytes = Files.readAllBytes(file.toPath());
            Cotizacion cot = new Cotizacion();
            cot.setArchivo(bytes);
            cot.setFormato(formato.toLowerCase());
            cot.setNombre(nombre);
            
            listaFacturas.add(cot);
            hBoxAdjuntos.getChildren().add(new Label(nombre));
        } catch (IOException ex) {
            Logger.getLogger(RegistrarRecepcionDePedidoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}