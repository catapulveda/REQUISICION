package view;

import DAO.CentroDeCostosDAO;
import DAO.OrdenDeCompraDAO;
import DAO.ProveedorDAO;
import SearchComboBox.SearchComboBox;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Conexion;
import model.OrdenDeCompra;
import model.Pedido;
import model.Proveedor;
import net.sf.jasperreports.engine.JRException;

public class GuardarOrdenDeCompraController implements Initializable {

    @FXML AnchorPane anchorPane;
    @FXML GridPane gridPane;
    
    @FXML JFXTextField cjnumerodeorden;
    @FXML JFXDatePicker cjfecha;
    SearchComboBox<Proveedor> comboProveedor = new SearchComboBox<>();
    //@FXML JFXComboBox<Proveedor> comboProveedor;
    @FXML JFXTextField cjcentrodecostos;
    @FXML JFXTextField cjcontacto;
    @FXML JFXTextField cjcargoflete;
    @FXML JFXTextField cjtransportadora;
    @FXML JFXTextField cjnumerodeguia;
    @FXML JFXTextField cjiva;
    @FXML JFXCheckBox checkExentoIva;
    @FXML JFXComboBox comboFormaPago;
    @FXML JFXTextArea cjobservaciones;
    @FXML JFXButton btnGuardar;
    
    private OrdenDeCompra oc;
    
    ProveedorDAO pvDAO = new ProveedorDAO();
    CentroDeCostosDAO ccDAO = new CentroDeCostosDAO();
    OrdenDeCompraDAO ocDAO = new OrdenDeCompraDAO();
    
    Conexion con;
    
    private int idorden = -1;
    
    private ObservableList<Pedido> listaSeleccionados;
    @FXML
    private JFXTextField cjvaloriva;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            
        con = new Conexion();
        
        comboFormaPago.getItems().addAll("CREDITO","CONTADO","DEBITO");
        
        comboProveedor.setItems(pvDAO.getProveedores());
        comboProveedor.setFilter((item, text) -> item.getNombreprovee().contains(text));
        gridPane.add(comboProveedor, 1, 2);          
        
        con.CERRAR();
    }
    
    @FXML
    void guardarOrden(ActionEvent evt){
//        Platform.runLater(() -> {
//           btnGuardar.setDisable(true); 
//        });
        if(cjfecha.getValue()==null){
            util.Metodos.alert("Mensaje", "Seleccione una fecha", null, Alert.AlertType.WARNING, null, null);
            return;
        }
        if(comboProveedor.getValue()==null){
            util.Metodos.alert("Mensaje", "Seleccione un proveedor", null, Alert.AlertType.WARNING, null, null);
            return;
        }
        if(comboFormaPago.getValue()==null){
            util.Metodos.alert("Mensaje", "Seleccione una forma de pago", null, Alert.AlertType.WARNING, null, null);
            return;
        }
        if(cjiva.getText().isEmpty()){
            util.Metodos.alert("Mensaje", "Ingrese el valor del iva", null, Alert.AlertType.WARNING, null, null);
            return;
        }
                
        OrdenDeCompra occ = new OrdenDeCompra();
        if(getOc()==null){
            occ.setIdordendecompra(0);
        }else{
            occ.setIdordendecompra(this.getOc().getIdordendecompra());
        }
        occ.setNumerodeorden(Integer.parseInt(cjnumerodeorden.getText()));
        occ.setFechadeorden(cjfecha.getValue());
        occ.setProveedor(comboProveedor.getValue());
        occ.setCentrodecostos(cjcentrodecostos.getText());
        occ.setContacto(cjcontacto.getText());
        occ.setCargoflete(cjcargoflete.getText());
        occ.setTransportador(cjtransportadora.getText());
        occ.setNumerodeguia(cjnumerodeguia.getText());
        if(cjiva.getText().isEmpty()){
            cjiva.setText("0");
        }
        occ.setIva(Integer.parseInt(cjiva.getText()));
        occ.setExentodeiva(checkExentoIva.isSelected());
        occ.setFormadepago(comboFormaPago.getValue().toString());
        occ.setObservaciones(cjobservaciones.getText());        
        
        oc = occ;
        
        con = new  Conexion();
        
        try {
            con.getCon().setAutoCommit(false);
            idorden = ocDAO.guardar(oc, con);
            listaSeleccionados.forEach(ped->{
                try {
                    if(ped.isSelected()){
                        con.GUARDAR("UPDATE pedidos SET idordendecompra="+idorden+" WHERE idpedido="+ped.getIdpedido()+";");
                    }                    
                } catch (SQLException ex) {
                    Logger.getLogger(GuardarOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            con.getCon().commit();
            HashMap<String, Object> p = new HashMap<>();p.put("IDORDEN", idorden);try {util.Metodos.generarReporte(p, "ORDEN_DE_COMPRA");} catch (JRException | IOException ex) {Logger.getLogger(GuardarOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);}
            ((Stage)anchorPane.getScene().getWindow()).close();
        } catch (SQLException ex) {
            try {con.getCon().rollback();} catch (SQLException ex1) {Logger.getLogger(GuardarOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex1);}
            Logger.getLogger(GuardarOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);
            util.Metodos.alert("Mensaje", "Ocurrio un error al intentar guardar la orden de compra", null, Alert.AlertType.ERROR, ex, null);
        }finally{
            con.CERRAR();
//            Platform.runLater(() -> {
//               btnGuardar.setDisable(false); 
//            });
        }
    }

    public OrdenDeCompra getOc() {
        return oc;
    }

    public void setOc(OrdenDeCompra oc) {
        this.oc = oc;
        if(oc==null){
            cjnumerodeorden.setText(""+(util.Metodos.getConsecutivo("numeroordendecompra", false)+1));
        }else{
            cjnumerodeorden.setText(oc.getNumerodeorden()+"");
            cjfecha.setValue(oc.getFechadeorden());
            comboProveedor.getItems().stream().filter(pv -> (oc.getProveedor().getNombreprovee().equals(pv.getNombreprovee()))).findFirst().ifPresent(p->{comboProveedor.getSelectionModel().select(p);});
//            try {
//                comboCentroDeCostos.getItems()
//                        .stream()
//                        .filter(cc -> (oc.getCentrodecostos().getCentrodecostos().equals(cc.getCentrodecostos())))
//                        .findFirst()
//                        .ifPresent(p->{comboCentroDeCostos.getSelectionModel().select(p);});
//            } catch (java.lang.NullPointerException e) {}
            cjcentrodecostos.setText(oc.getCentrodecostos());
            cjcontacto.setText(oc.getContacto());
            cjcargoflete.setText(oc.getCargoflete());
            cjtransportadora.setText(oc.getTransportador());
            cjnumerodeguia.setText(oc.getNumerodeguia());
            cjiva.setText(""+oc.getIva());
            checkExentoIva.setSelected(oc.isExentodeiva());
            comboFormaPago.setValue(oc.getFormadepago());
            cjobservaciones.setText(oc.getObservaciones());
        }
    }

    public ObservableList<Pedido> getListaSeleccionados() {
        return listaSeleccionados;
    }

    public void setListaSeleccionados(ObservableList<Pedido> listaSeleccionados) {
        this.listaSeleccionados = listaSeleccionados;
    }

    public int getIdorden() {
        return idorden;
    }

    public void setIdorden(int idorden) {
        this.idorden = idorden;
    }
    
}
