package view;

import DAO.ProductoDAO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Producto;

public class TablaProductosController implements Initializable {

    @FXML TableView<Producto> tablaProductos;
    
    @FXML TableColumn<Producto, Boolean> colSelected;
    @FXML TableColumn<Producto, String> colProducto;
    
    @FXML TextField cjbuscar;
    
    @FXML Button btn;
    
    ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    
    ProductoDAO pDAO = new ProductoDAO();
    
    FilteredList<Producto> filtro;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colSelected.setCellValueFactory(new PropertyValueFactory("selected"));
        colSelected.setCellFactory(CheckBoxTableCell.forTableColumn(colSelected));
               
        colProducto.setCellValueFactory(new PropertyValueFactory("nombreproducto"));
        
        listaProductos.setAll(pDAO.getProductos());
        tablaProductos.setItems(listaProductos);
        tablaProductos.getSelectionModel().setCellSelectionEnabled(true);
        
        filtro = new FilteredList(listaProductos, p -> true);
        
        for (int i = 1; i < tablaProductos.getColumns().size(); i++) {
            util.Metodos.changeSizeOnColumn(tablaProductos.getColumns().get(i), tablaProductos);
        }
    }
    
    @FXML
    void buscarProducto(KeyEvent evt){
        cjbuscar.textProperty().addListener((observableValue, oldValue, newValue)->{
            filtro.setPredicate( (Predicate<? super Producto>) proveedor->{
                if(newValue==null || newValue.isEmpty()){
                    return true;
                }
                return proveedor.getNombreproducto().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        SortedList<Producto> sorterData = new SortedList<>(filtro);
        sorterData.comparatorProperty().bind(tablaProductos.comparatorProperty());
        tablaProductos.setItems(sorterData);
    }
    
    @FXML
    void AgregarYCerrar(ActionEvent evt){
        ((Stage)tablaProductos.getParent().getScene().getWindow()).close();
    }
    
    public ObservableList<Producto> getProductosSeleccionados(){
        ObservableList<Producto> productos = FXCollections.observableArrayList();
        tablaProductos.getItems().forEach(p->{
            if(p.isSelected()){
                productos.add(p);
            }
        });
        return productos;
    }
    
}
