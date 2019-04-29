package view;

import DAO.ProveedorDAO;
import com.jfoenix.controls.JFXTabPane;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import model.Proveedor;

public class ProveedorController implements Initializable {

    @FXML AnchorPane container;
    
    @FXML JFXTabPane tabPane;
    
    @FXML Tab tabProveedores;
    @FXML Tab tabRegistrar;
    
    @FXML TableView<Proveedor> tablaProveedores;
    
    @FXML TableColumn<Proveedor, Integer> colItem;
    @FXML TableColumn colAcciones;
    @FXML TableColumn<Proveedor, String> colProveedor;
    
    @FXML TextField cjbuscar;
    @FXML Button btnListar;
    @FXML Button btnNuevo;
    @FXML Button btnGuardar;
    @FXML Button btnRegresar;        
    
    @FXML TextField cjnombre;
    @FXML TextField cjnit;
    @FXML TextField cjdireccion;
    @FXML TextField cjtelefonofijo;
    @FXML TextField cjcelular;
    @FXML TextField cjcorreo;
    @FXML TextField cjpaginaweb;
    @FXML TextField cjciudad;
    
    private boolean ACTUALIZAR = false;    
    
    ObservableList<Proveedor> listaProveedores = FXCollections.observableArrayList();
    
    private Proveedor pv;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        tablaProveedores.setEditable(true);
        tablaProveedores.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tablaProveedores.getSelectionModel().setCellSelectionEnabled(true);
        tablaProveedores.setItems(listaProveedores);
        
        FilteredList<Proveedor> filtro = new FilteredList(listaProveedores, p -> true);
        cjbuscar.setOnKeyReleased(e ->{
            cjbuscar.textProperty().addListener( (observableValue, oldValue, newValue)->{
                filtro.setPredicate( (Predicate<? super Proveedor>) proveedor->{
                    if(newValue==null || newValue.isEmpty()){
                        return true;
                    }
                    return proveedor.getNombreprovee().toLowerCase().contains(newValue.toLowerCase());
                });
            });
            SortedList<Proveedor> sorterData = new SortedList<>(filtro);
            sorterData.comparatorProperty().bind(tablaProveedores.comparatorProperty());
            tablaProveedores.setItems(sorterData);
        });        
        
        colItem.setCellValueFactory(new Callback<CellDataFeatures<Proveedor, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(CellDataFeatures<Proveedor, Integer> param) {
                return new ReadOnlyObjectWrapper(param.getValue().getIdproveedor());
            }
        });
        colItem.setCellFactory(new Callback<TableColumn<Proveedor,Integer>, TableCell<Proveedor,Integer>>(){
            @Override
            public TableCell<Proveedor, Integer> call(TableColumn<Proveedor, Integer> param) {                                
                
                TableCell cell = new TableCell<Proveedor, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (this.getTableRow() != null && item != null) {
                          setText(""+(this.getTableRow().getIndex()+1));                            
                        } else {
                          setText("");
                        }
                    }
                };      
                cell.setStyle("-fx-alignment: CENTER;");
                return cell;
            }
        });
        
        colAcciones.setCellValueFactory(new PropertyValueFactory("Acciones"));        
        colAcciones.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {                
                
                ImageView icon = new ImageView(getClass().getResource("/images/basura.png").toString());
                icon.setStyle("-fx-fill: #FFF;-fx-text-fill: #FFF;");
                final Button delete = new Button("", icon);
                delete.setTooltip(new Tooltip("Eliminar producto"));
                delete.setStyle("-fx-border-radius: 4px;-fx-background-color: #d9534f;-fx-border-color: #d43f3a;-fx-text-fill: #fff;-fx-fill: #fff;");
                
                TableCell tableCell = new TableCell(){
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            delete.setOnAction(event -> {
                                Proveedor p = (Proveedor) getTableView().getItems().get(getIndex());
                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Confirmar ");
                                alert.setHeaderText(p.getNombreprovee());
                                alert.setContentText("Seguro que desea eliminar el proveedor?");

                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == ButtonType.OK){
                                    ProveedorDAO pDAO = new ProveedorDAO();
                                    if(pDAO.delete(p)>0){
                                        listaProveedores.remove(p);                                        
                                    }                                    
                                }
                            });
                            setGraphic(delete);
                            setText(null);
                        }
                    }
                    
                };
                return tableCell;
            }
        });
        
        colProveedor.setCellValueFactory(new PropertyValueFactory("nombreprovee"));        
    }
    
    @FXML
    public void cargarProveedores(ActionEvent evt){
        Task<ObservableList<Proveedor>> loadDataTask = new Task<ObservableList<Proveedor>>() {
            @Override
            protected ObservableList<Proveedor> call() {
                ProveedorDAO pDAO = new ProveedorDAO();
                listaProveedores.setAll(pDAO.getProveedores());                    
                return listaProveedores;
            }
        };
        loadDataTask.setOnFailed(e -> {
            tablaProveedores.setPlaceholder(null);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cargar los proveedores : " + loadDataTask.getException(), javafx.scene.control.ButtonType.CLOSE);
            alert.showAndWait();
        });

        loadDataTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                for(int i =0; i<tablaProveedores.getColumns().size(); i++){
                    util.Metodos.changeSizeOnColumn(tablaProveedores.getColumns().get(i), tablaProveedores);
                }
                tablaProveedores.setPlaceholder(null);
            }
        });
        ProgressIndicator progressIndicator = new ProgressIndicator();
        tablaProveedores.setPlaceholder(progressIndicator);
        Thread loadDataThread = new Thread(loadDataTask);
        loadDataThread.start();
    }
    
    
    @FXML public void abrirProveedor(MouseEvent evt){
        if(evt.getClickCount()==2&&evt.getButton()==MouseButton.PRIMARY){
            ACTUALIZAR = true;
            pv = tablaProveedores.getSelectionModel().getSelectedItem();
            cjnombre.setText(pv.getNombreprovee());
            cjnit.setText(pv.getNitprovee());
            cjdireccion.setText(pv.getDireccionprovee());
            cjtelefonofijo.setText(pv.getTelefonofijoprovee());
            cjcelular.setText(pv.getTelefonofijoprovee());
            cjcorreo.setText(pv.getCorreoprovee());
            cjpaginaweb.setText(pv.getPaginawebprovee());
            cjciudad.setText(pv.getCiudad());
            tabPane.getSelectionModel().select(tabRegistrar);
            btnGuardar.requestFocus();
        }
    }
    
    @FXML
    public void regresar(ActionEvent evt){
        limpiar();
        tabPane.getSelectionModel().select(tabProveedores);
    }
    
    @FXML
    public void nuevoProveedor(ActionEvent evt){
        tabPane.getSelectionModel().select(tabRegistrar);
        cjnombre.requestFocus();
    }
    
    @FXML
    public void guardar(ActionEvent evt){        
        if(cjnombre.getText()== null || cjnombre.getText().trim().isEmpty()){
            util.Metodos.alert("Complete los campos obligatorios", "Ingrese el nombre del proveedor", null, AlertType.WARNING, null, null);
            return;
        }
        String nombre = cjnombre.getText().trim();
        if(pv == null){
            pv = new Proveedor();
        }
        
        pv.setNombreprovee(cjnombre.getText());
        pv.setNitprovee(cjnit.getText());
        pv.setDireccionprovee(cjdireccion.getText());
        pv.setTelefonofijoprovee(cjtelefonofijo.getText());
        pv.setCelularprovee(cjcelular.getText());
        pv.setCorreoprovee(cjcorreo.getText());
        pv.setPaginawebprovee(cjpaginaweb.getText());
        pv.setCiudad(cjciudad.getText());
        pv.setFechaderegistro(LocalDateTime.now());
        
        ProveedorDAO pvDAO = new ProveedorDAO();
        int n = pvDAO.guardar(pv);
        if(n>0){            
            limpiar();
            util.Metodos.alert("Mensje de confirmacion", "Proveedor registrado", null, Alert.AlertType.INFORMATION, null, "bien.png");
        }
    }
    
    /*METODOS SIN INTEREACCION DIRECTA ACCIONADOS POR INTERFAZ GRAFICA*/
    public void limpiar(){
        cjnombre.setText(null);
        cjnit.setText(null);
        cjdireccion.setText(null);
        cjtelefonofijo.setText(null);
        cjcelular.setText(null);
        cjcorreo.setText(null);
        cjpaginaweb.setText(null);
        cjciudad.setText(null);
        ACTUALIZAR = false;
        pv = null;
    }
}
