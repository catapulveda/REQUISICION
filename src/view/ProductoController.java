package view;

import DAO.ProductoDAO;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.Duration;
import model.Producto;

/**
 *
 * @author AUXPLANTA
 */
public class ProductoController implements Initializable{

    @FXML private AnchorPane mainPane;
    
    @FXML Accordion acordion;
    @FXML TitledPane paneProductos;
    @FXML TitledPane paneRegistrar;    
    
    @FXML private TableView<Producto> tablaProducto;    
    @FXML private TableColumn<Producto, String> colNombreproducto;
    @FXML private TableColumn<Producto, Integer> colNumproducto;
    @FXML private TableColumn colAcciones;
    
    @FXML private TextField cjBuscarProducto;
    
    @FXML private TextArea cjNombre;
    @FXML private TextField cjCodigo;
    @FXML private TextField cjUnidad;
    
    @FXML private Button btnCargar;        
    @FXML private Button btnGuardarProdcuto;
    @FXML private Button btnCerrarVentana;
    @FXML private Button btnVerProducto;
    @FXML private Button btnCancelar;
    @FXML private Button btnNuevoProducto;    
        
    @FXML private ImageView imageView;
    private Image imagen = null;
    
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    
    private boolean ACTUALIZAR = false;
    Producto p = null;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        mainPane.setOpacity(0.0);
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(800));
        fade.setNode(mainPane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();  
        
        acordion.setExpandedPane(paneProductos);
        
        btnCargar.setTooltip(new Tooltip("Listar productos"));
        
        tablaProducto.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Producto>() {
            @Override
            public void changed(ObservableValue<? extends Producto> observable, Producto oldValue, Producto newValue) {
                System.out.println("OLD -> "+oldValue+" NEW -> "+newValue);
            }
        });
        
        tablaProducto.setOnMouseClicked( e ->{
            if(e.getClickCount()==2 && e.getButton()==MouseButton.PRIMARY){
                abrirProducto();
            }
        });
        
        tablaProducto.setEditable(true);
        tablaProducto.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); 
        tablaProducto.getSelectionModel().setCellSelectionEnabled(true);
        
        colNombreproducto.setCellValueFactory(new PropertyValueFactory("nombreproducto"));
        
        colNumproducto.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<model.Producto, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<model.Producto, Integer> p) {
                return new ReadOnlyObjectWrapper(0);
            }
        });
        colNumproducto.setCellFactory(tc -> new FormatCell.NumberRowCell<>());         
        
        colAcciones.setCellValueFactory(new PropertyValueFactory<>("Acciones"));
        colAcciones.setCellFactory(new Callback<TableColumn<Producto, String>, TableCell<Producto, String>>(){
            @Override
            public TableCell<Producto, String> call(TableColumn<Producto, String> param) {
                
                ImageView icon = new ImageView(getClass().getResource("/images/basura.png").toString());
                icon.setStyle("-fx-fill: #FFF;-fx-text-fill: #FFF;");
                final Button delete = new Button("", icon);
                delete.setTooltip(new Tooltip("Eliminar producto"));
                delete.setStyle("-fx-border-radius: 4px;-fx-background-color: #d9534f;-fx-border-color: #d43f3a;-fx-text-fill: #fff;-fx-fill: #fff;");                
                
                final TableCell<Producto, String> cell = new TableCell<Producto, String>() {

                    @Override
                    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
                        return super.buildEventDispatchChain(tail); //To change body of generated methods, choose Tools | Templates.
                    }
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);                        
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            delete.setOnAction(event -> {
                                Producto p = getTableView().getItems().get(getIndex());
                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Confirmar ");
                                alert.setHeaderText(p.getNombreproducto());
                                alert.setContentText("Seguro que desea eliminar el producto?");

                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == ButtonType.OK){
                                    ProductoDAO pDAO = new ProductoDAO();
                                    if(pDAO.delete(p)>0){
                                        listaProductos.remove(p);
                                    }                                    
                                }
                            });
                            setGraphic(delete);
                            setText(null);
                        }
                    }                    
                };
                return cell;
            }        
        });
        tablaProducto.setItems(listaProductos);                
        
        FilteredList<Producto> filteredData = new FilteredList<>(listaProductos, p -> true);
        
        // 2. Set the filter Predicate whenever the filter changes.
        cjBuscarProducto.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(producto -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toUpperCase();
                // Does not match.

                return producto.getNombreproducto().toUpperCase().contains(lowerCaseFilter); 
            });
        });
        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<Producto> sortedData = new SortedList<>(filteredData);
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(tablaProducto.comparatorProperty());
        // 5. Add sorted (and filtered) data to the table.
        tablaProducto.setItems(sortedData);
        
        imageView.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent e) {
                try {
                    imagen = new Image(new FileInputStream(e.getDragboard().getFiles().get(0)));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ProductoController.class.getName()).log(Level.SEVERE, null, ex);
                }
                insertarImagen(e);
            }
        });
        imageView.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if(event.getDragboard().hasFiles()){
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            }
        });
        imageView.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                imageView.setImage(null);event.consume();
            }
        });
        imageView.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.setDropCompleted(insertarImagen(event));
                event.consume();
            }
        });
        
        btnCargar.setOnAction( (ActionEvent event)->{
            Task<ObservableList<Producto>> loadDataTask = new Task<ObservableList<Producto>>() {
                @Override
                protected ObservableList<Producto> call() {
                    ProductoDAO pDAO = new ProductoDAO();
                    listaProductos.setAll(pDAO.getProductos());                    
                    return listaProductos;
                }
            };
            loadDataTask.setOnFailed(e -> {
                tablaProducto.setPlaceholder(null);
                Alert alert = new Alert(AlertType.ERROR, "Error al cargar los productos : " + loadDataTask.getException(), javafx.scene.control.ButtonType.CLOSE);
                alert.showAndWait();
            });

            loadDataTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    for(int i =0; i<tablaProducto.getColumns().size(); i++){
                        util.Metodos.changeSizeOnColumn(tablaProducto.getColumns().get(i), tablaProducto);
                    }
                    tablaProducto.setPlaceholder(null);
                }
            });
            ProgressIndicator progressIndicator = new ProgressIndicator();
            tablaProducto.setPlaceholder(progressIndicator);            
            Thread loadDataThread = new Thread(loadDataTask);
            loadDataThread.start();
            
        });
        
        btnCerrarVentana.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FadeTransition fade = new FadeTransition();
                fade.setDuration(Duration.millis(300));
                fade.setNode(mainPane);
                fade.setFromValue(1);
                fade.setToValue(0);
                fade.play();
            }
        });
        
        btnGuardarProdcuto.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(cjNombre.getText().isEmpty()){
                    util.Metodos.alert("Campos vacios", "Ingrese el nombre del producto", "El nombre del producto no puede estar vacio", Alert.AlertType.WARNING, null, null);
                }
                
                String nombre = cjNombre.getText().trim();
                String codigo = cjCodigo.getText().trim();
                String medida = cjUnidad.getText().trim();
                byte[] imagen = util.Metodos.ImageToByte(imageView.getImage());
                
                if(p==null){
                    p = new Producto();
                }
                p.setNombreproducto(nombre);
                p.setCodigoproducto(codigo);
                p.setMedida(medida);
                p.setImagen(imagen);
                
                ProductoDAO pDAO = new ProductoDAO();
                int n = pDAO.guardar(p, ACTUALIZAR);
                if(n>0){
                    limpiar(); 
                    util.Metodos.alert("Registrado", "Producto registrado", null, Alert.AlertType.INFORMATION, null, "bien.png");
                }
           }
        });
        
        btnCancelar.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {                
                limpiar();
                ACTUALIZAR = false;
                acordion.setExpandedPane(paneProductos);                
            }
        });
        
        btnNuevoProducto.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                limpiar();
                ACTUALIZAR = false;
                acordion.setExpandedPane(paneRegistrar);                
            }
        });
        
        btnVerProducto.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                abrirProducto();
            }
        });
        
    }
    
    public void abrirProducto(){
        if(tablaProducto.getSelectionModel().getSelectedIndex()>=0){
            ProductoDAO pDAO = new ProductoDAO();
            p = tablaProducto.getSelectionModel().getSelectedItem();
            cjNombre.setText(p.getNombreproducto());
            cjCodigo.setText(p.getCodigoproducto());
            cjUnidad.setText(p.getMedida());
            imageView.setImage(pDAO.getImagen(p.getIdproducto()));
            acordion.setExpandedPane(paneRegistrar);
            ACTUALIZAR = true;
        }else{
            util.Metodos.alert("Mensaje", "Seleccione un producto de la tabla", null, AlertType.WARNING, null, null);
        }
    }
    
    public void limpiar(){
        p = null;
        ACTUALIZAR = false;
        cjNombre.setText("");
        cjCodigo.setText("");
        cjUnidad.setText("");
        imageView.setImage(new Image(ProductoController.class.getResourceAsStream("/images/register_producto.png")));
    }
    
    public boolean insertarImagen(DragEvent e){
        final Dragboard db = e.getDragboard(); 
        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".png")|| db.getFiles().get(0).getName().toLowerCase().endsWith(".jpeg")|| db.getFiles().get(0).getName().toLowerCase().endsWith(".jpg");
        if (db.hasFiles()) {
            if (isAccepted) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImage(imagen);
                    }
                });
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        } else {
            e.consume();
        }
        return isAccepted;
    }

}
