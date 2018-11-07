package view;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.BuscarTodo;
import model.Conexion;
import model.OrdenDeCompra;
import model.Producto;
import model.Proveedor;
import model.Requisicion;

public class BuscarTodoController implements Initializable {

    @FXML AnchorPane ap;
    @FXML VBox vbox;
    @FXML HBox hbox;
    
    @FXML TextField cjBuscar;
    
    ObservableList<BuscarTodo> lista = FXCollections.observableArrayList();
    
    @FXML TableView<BuscarTodo> tabla;
    @FXML TableColumn<BuscarTodo, String> colReq;
    @FXML TableColumn<BuscarTodo, String> colOC;
    @FXML TableColumn<BuscarTodo, String> colProveedor;
    @FXML TableColumn<BuscarTodo, String> colProducto;
    
    private Conexion con;
    
    FilteredList<BuscarTodo> filtro;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colReq.setCellValueFactory((param)->{
            return new SimpleStringProperty(param.getValue().getReq().getNumerorequisicion()+"");
        });
        colReq.setStyle("-fx-alignment: CENTER;");
        
        colOC.setCellValueFactory((param)->{
            return new SimpleStringProperty(param.getValue().getOc().getNumerodeorden()+"");
        });
        colOC.setStyle("-fx-alignment: CENTER;");
        
        colProveedor.setCellValueFactory((param)->{
            return new SimpleStringProperty(param.getValue().getPv().getNombreprovee());
        });
        
        colProducto.setCellValueFactory((param)->{
            return new SimpleStringProperty(param.getValue().getP().getNombreproducto());
        });
        
        tabla.setItems(lista);
        
        filtro = new FilteredList(lista, p -> true);                
                
        Platform.runLater(() -> {
            String sql = "SELECT r.referencia, r.numerorequisicion, oc.numerodeorden, pv.nombreprovee, p.nombreproducto FROM pedidos ped\n" +
                    "INNER JOIN requisicion r ON r.idrequisicion=ped.idrequisicion\n" +
                    "LEFT JOIN ordendecompra oc ON oc.idordendecompra=ped.idordendecompra\n" +
                    "INNER JOIN producto p ON p.idproducto=ped.idproducto\n" +
                    "LEFT JOIN proveedor pv ON pv.idproveedor=oc.idproveedor "+
                    "ORDER BY numerorequisicion DESC, numerodeorden DESC;";
            con = new Conexion();
            try {
                ResultSet rs = con.CONSULTAR(sql);
                while(rs.next()){
                    
                    Requisicion req = new Requisicion();
                    req.setNumerorequisicion(rs.getInt("numerorequisicion"));
                    req.setReferencia(rs.getString("referencia"));
                    
                    OrdenDeCompra oc = new OrdenDeCompra();
                    oc.setNumerodeorden(rs.getInt("numerodeorden"));
                    
                    Producto p = new Producto();
                    p.setNombreproducto(rs.getString("nombreproducto"));
                    
                    Proveedor pv = new Proveedor();
                    pv.setNombreprovee(rs.getString("nombreprovee"));
                    
                    BuscarTodo bt = new BuscarTodo();
                    bt.setReq(req);
                    bt.setOc(oc);
                    bt.setP(p);
                    bt.setPv(pv);
                    
                    lista.add(bt);
                }
                for (int i = 0; i < tabla.getColumns().size(); i++) {
                    util.Metodos.changeSizeOnColumn(tabla.getColumns().get(i), tabla);
                }
            } catch (SQLException e) {System.err.println(0);}
        });                
    }
    
    @FXML
    void buscarProducto(KeyEvent evt){
        cjBuscar.textProperty().addListener((observableValue, oldValue, newValue)->{
            filtro.setPredicate( (Predicate<? super BuscarTodo>) param->{
                if(newValue==null || newValue.isEmpty() || param.getPv().getNombreprovee()==null || param.getP().getNombreproducto() == null){
                    return true;
                }
                return (param.getP().getNombreproducto().contains(newValue)||
                         param.getPv().getNombreprovee().contains(newValue));
            });
        });
        SortedList<BuscarTodo> sorterData = new SortedList<>(filtro);
        sorterData.comparatorProperty().bind(tabla.comparatorProperty());
        tabla.setItems(sorterData);
    }
    
}
