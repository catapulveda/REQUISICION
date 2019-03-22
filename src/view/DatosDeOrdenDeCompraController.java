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
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JOptionPane;
import model.CentroDeCostos;
import model.Conexion;
import model.OrdenDeCompra;
import model.Pedido;
import model.Producto;
import model.Proveedor;
import model.RecepcionDePedido;
import net.sf.jasperreports.engine.JRException;

public class DatosDeOrdenDeCompraController implements Initializable {

    @FXML AnchorPane anchorPane;
    
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
    @FXML Button btnDevolver;
    
   @FXML GridPane gridPane;
    
    @FXML TableView<RecepcionDePedido> tabla;
    @FXML TableColumn colItem;    
    @FXML TableColumn<RecepcionDePedido, String> colCantSoli;
    @FXML TableColumn colCantRecib;
    @FXML TableColumn colCantPend;
    @FXML TableColumn<RecepcionDePedido, String> colProducto;
    
    private OrdenDeCompra oc;
    
    ProveedorDAO pvDAO = new ProveedorDAO();
    CentroDeCostosDAO ccDAO = new CentroDeCostosDAO();
    OrdenDeCompraDAO ocDAO = new OrdenDeCompraDAO();
    
    Conexion con;
    
    private int idorden = -1;
    
    private ObservableList<Pedido> listaSeleccionados;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        con = new Conexion();
        
        comboFormaPago.getItems().addAll("CREDITO","CONTADO","DEBITO");
        
        comboProveedor.setItems(pvDAO.getProveedores());
        
        
        comboProveedor.setItems(pvDAO.getProveedores());
        comboProveedor.setFilter((item, text) -> item.getNombreprovee().contains(text));
        gridPane.add(comboProveedor, 1, 2);
        
        con.CERRAR();
                
        tabla.getSelectionModel().setCellSelectionEnabled(true);
        tabla.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        colItem.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<model.RecepcionDePedido, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<model.RecepcionDePedido, Integer> p) {
                return new ReadOnlyObjectWrapper(0);
            }
        });
        colItem.setCellFactory(tc -> new util.NumberRowCell<>()); 
        
        colCantSoli.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(""+cellData.getValue().getPedido().getCantidadsolicitada());
        });
        colCantSoli.setStyle("-fx-alignment: CENTER;");
        
        colCantRecib.setCellValueFactory( new PropertyValueFactory("cantidadrecibida"));
        colCantRecib.setStyle("-fx-alignment: CENTER;");
        colCantRecib.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                
                FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EYE, "16");
                Button btn = new Button("", icon);                                
                
                TableCell tableCell = new TableCell(){
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty){
                            btn.setText(item.toString());
                            btn.setOnAction(evt->{
                                try {
                                    Pedido ped = ((RecepcionDePedido) getTableView().getItems().get(getIndex())).getPedido();
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fx.NavegadorDeContenidos.RECEPCION_DE_PEDIDOS));
                                    AnchorPane root = loader.load();
                                    RecepcionDePedidosController rpc = (RecepcionDePedidosController)loader.getController();
                                    rpc.setPed(ped);                                    
                                    Stage stage = new Stage();
                                    Scene scene = new Scene(root);
                                    stage.setScene(scene);  
                                    stage.showAndWait();
                                } catch (IOException ex) {
                                    Logger.getLogger(DatosDeOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            });
                            setGraphic(btn);
                        }
                    }                    
                };                
                return tableCell;
            }            
        });
        
        colCantPend.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<model.RecepcionDePedido, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<model.RecepcionDePedido, Integer> p) {
                return new ReadOnlyObjectWrapper(0);
            }
        });
        
        colCantPend.setCellFactory(tc -> new TableCell<RecepcionDePedido, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if(item!=null){
                    Double solicitada = getTableView().getItems().get(getIndex()).getPedido().getCantidadsolicitada();
                    double recibida = getTableView().getItems().get(getIndex()).getCantidadrecibida();
                    double pendiente = solicitada-recibida;
                    setText(String.valueOf(pendiente));
                    setAlignment(Pos.CENTER);
                    setTextFill(Color.ANTIQUEWHITE);
                    if(pendiente>0){
                        setStyle("-fx-background-insets: 0 0 1 0 ;-fx-background-color: -fx-background;"
                                + "-fx-background: #d14836;-fx-font-weight: bold;");
                    }else{
                        setStyle("-fx-background-insets: 0 0 1 0 ;-fx-background-color: -fx-background;"
                                + "-fx-background: #3cba54;-fx-font-weight: bold;");
                    }                    
                }                
            }
        });
        
        colProducto.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getPedido().getProducto().getNombreproducto());
        });                
    }
    
    @FXML
    void devolverProducto(ActionEvent evt){
        RecepcionDePedido rp = tabla.getSelectionModel().getSelectedItem();
        System.out.println(rp);
        if(rp==null){
            util.Metodos.alert("Mensaje", "Seleccione un item de la tabla", null, Alert.AlertType.INFORMATION, null, null);
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmacion");
        alert.setHeaderText("¿Continuar con la exlusión?");
        //alert.setContentText("Continuar con el registro?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            con = new Conexion();
            try {
                if(con.GUARDAR("UPDATE pedidos SET idordendecompra=null WHERE idpedido="+rp.getPedido().getIdpedido())>0){
                    tabla.getItems().remove(tabla.getSelectionModel().getSelectedIndex());
                    tabla.refresh();
                }
            } catch (SQLException ex) {
                util.Metodos.alert("Error", "Ocurrio un error al intentar excluir el producto "+rp.getPedido().getProducto().getNombreproducto()+" de la orden de compra", null, Alert.AlertType.ERROR, ex, null);
            }finally{
                con.CERRAR();
            }
        }        
    }
    
    @FXML
    void guardarOrden(ActionEvent evt){
        Platform.runLater(() -> {
           btnGuardar.setDisable(true); 
        });
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
            idorden = ocDAO.guardar(oc, con);
            System.out.println("IDORDEN = "+idorden);
            HashMap<String, Object> p = new HashMap<>();
            p.put("IDORDEN", idorden);
            try {
                util.Metodos.generarReporte(p, "ORDEN_DE_COMPRA");
            } catch (JRException | IOException ex) {
                Logger.getLogger(GuardarOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);
            }
            //((Stage)anchorPane.getScene().getWindow()).close();            
        } catch (SQLException ex) {
            try {
                con.getCon().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(GuardarOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex1);
            }
            Logger.getLogger(GuardarOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);
            util.Metodos.alert("Mensaje", "Ocurrio un error al intentar guardar la orden de compra", null, Alert.AlertType.ERROR, ex, null);
        }finally{
            con.CERRAR();
            Platform.runLater(() -> {
               btnGuardar.setDisable(false); 
            });
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
//                comboCentroDeCostos.getItems().stream().filter(cc -> (oc.getCentrodecostos().getCentrodecostos().equals(cc.getCentrodecostos()))).findFirst().ifPresent(p->{comboCentroDeCostos.getSelectionModel().select(p);});
//            }catch(java.lang.NullPointerException e){}
            cjcentrodecostos.setText(oc.getCentrodecostos());
            cjcontacto.setText(oc.getContacto());
            cjcargoflete.setText(oc.getCargoflete());
            cjtransportadora.setText(oc.getTransportador());
            cjnumerodeguia.setText(oc.getNumerodeguia());
            cjiva.setText(""+oc.getIva());
            checkExentoIva.setSelected(oc.isExentodeiva());
            comboFormaPago.setValue(oc.getFormadepago());
            cjobservaciones.setText(oc.getObservaciones());
            con = new Conexion();
            
            String sql = "SELECT p.idproducto, p.nombreproducto, ped.idpedido, ped.cantidadsolicitada,\n" +
            "count(rp.cantidadrecibida) AS entregas, sum(rp.cantidadrecibida) as recibidos,\n" +
            "(ped.cantidadsolicitada-sum(rp.cantidadrecibida)) as pendiente\n" +
            "FROM ordendecompra oc\n" +
            "INNER JOIN pedidos ped ON ped.idordendecompra=oc.idordendecompra\n" +
            "LEFT JOIN recepciondepedidos rp ON rp.idpedido=ped.idpedido\n" +
            "INNER JOIN producto p ON p.idproducto=ped.idproducto\n" +
            "WHERE oc.idordendecompra="+this.oc.getIdordendecompra()+"\n" +
            "GROUP BY oc.idordendecompra, p.idproducto, ped.idpedido;";
            try {
                ResultSet rs = con.CONSULTAR(sql);
                int row = 0;
                while(rs.next()){
                    RecepcionDePedido rp = new RecepcionDePedido();
                    
                    Producto p = new Producto();
                    p.setIdproducto(rs.getInt("idproducto"));
                    p.setNombreproducto(rs.getString("nombreproducto"));
                    
                    Pedido ped = new Pedido();
                    ped.setIdpedido(rs.getInt("idpedido"));
                    ped.setCantidadsolicitada(rs.getInt("cantidadsolicitada"));
                    ped.setProducto(p);
                    ped.setOc(oc);
                    ped.setSelected(false);                    
                    
                    rp.setPedido(ped);
                    rp.setCantidadrecibida(rs.getInt("recibidos"));
                    
                    tabla.getItems().add(row, rp);
                    row++;                    
                }
                for (int i = 0; i < tabla.getColumns().size(); i++) {
                    util.Metodos.changeSizeOnColumn(tabla.getColumns().get(i), tabla);
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatosDeOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);
            }
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
