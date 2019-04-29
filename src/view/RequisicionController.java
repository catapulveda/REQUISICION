package view;

import DAO.PedidoDAO;
import DAO.RequisicionDAO;
import com.jfoenix.controls.JFXTabPane;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import fx.NavegadorDeContenidos;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Conexion;
import model.Pedido;
import model.Requisicion;
import net.sf.jasperreports.engine.JRException;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import FormatCell.FormattedTableCellFactory;

public class RequisicionController implements Initializable {

    FormattedTableCellFactory formato = new FormattedTableCellFactory();

    ObservableList<Requisicion> listaRequisiciones = FXCollections.observableArrayList();

    @FXML AnchorPane anchorPane;

    @FXML TableView<Requisicion> tablaRequisiciones;

    @FXML TableColumn colItem;
    @FXML TableColumn<Requisicion, Integer> colNumeroRequisicion;
    @FXML TableColumn<Requisicion, String> colReferencia;
    @FXML TableColumn<Requisicion, LocalDateTime> colFecha;
    @FXML TableColumn<Requisicion, Integer> colTotalProductos;

    @FXML Button btnListar;
    @FXML Button btnImprimir;
    @FXML Button btnNuevo;
    @FXML Button btnEditar;

    @FXML TextField cjBuscar;

    @FXML JFXTabPane tabPane;
    
    Conexion con;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        tablaRequisiciones.setEditable(true);
        tablaRequisiciones.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tablaRequisiciones.getSelectionModel().setCellSelectionEnabled(true);
        tablaRequisiciones.setItems(listaRequisiciones);

        colNumeroRequisicion.setCellValueFactory(new PropertyValueFactory("numerorequisicion"));
        colReferencia.setCellValueFactory(new PropertyValueFactory("referencia"));

        colFecha.setCellValueFactory(new PropertyValueFactory("fechaderegistro"));
        colFecha.setCellFactory(column -> {
            return new TableCell<Requisicion, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((item == null || empty) ? null : DateTimeFormatter.ofPattern("dd MMM YYYY hh:mm a").format(item));
                }
            };
        });

        colTotalProductos.setCellValueFactory(new PropertyValueFactory("totalProductos"));
        formato.setAlignment(Pos.CENTER);
        formato.setFormat(NumberFormat.getIntegerInstance());
        colTotalProductos.setCellFactory(formato);

        colItem.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Requisicion, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Requisicion, Integer> param) {
                return new ReadOnlyObjectWrapper(param.getValue().getIdrequisicion());
            }
        });
        colItem.setCellFactory(new Callback<TableColumn<Integer, Integer>, TableCell<Integer, Integer>>() {
            @Override
            public TableCell<Integer, Integer> call(TableColumn<Integer, Integer> param) {
                TableCell cell = new TableCell<Integer, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (this.getTableRow() != null && item != null) {
                            setText("" + (this.getTableRow().getIndex() + 1));
                        } else {
                            setText("");
                        }
                    }
                };
                cell.setStyle("-fx-alignment: CENTER;");
                return cell;
            }
        });
        btnListar.fire();
    }

    @FXML
    public void cargarRequisiciones(ActionEvent evt) {
        Task<ObservableList<Requisicion>> tarea = new Task<ObservableList<Requisicion>>() {
            @Override
            protected ObservableList<Requisicion> call() throws Exception {
                RequisicionDAO rDAO = new RequisicionDAO();
                listaRequisiciones.setAll(rDAO.getRequisiciones());
                return listaRequisiciones;
            }
        };
        tarea.setOnFailed(e -> {
            tablaRequisiciones.setPlaceholder(null);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(tarea.getMessage() + "\n" + tarea.getException());
            alert.setTitle(tarea.getTitle());
            alert.showAndWait();
        });
        tarea.setOnSucceeded(e -> {
            for (int i = 0; i < tablaRequisiciones.getColumns().size(); i++) {
                util.Metodos.changeSizeOnColumn(tablaRequisiciones.getColumns().get(i), tablaRequisiciones);
            }
            tablaRequisiciones.setPlaceholder(null);
        });
        ProgressIndicator progressIndicator = new ProgressIndicator();
        tablaRequisiciones.setPlaceholder(progressIndicator);
        Thread loadDataThread = new Thread(tarea);
        loadDataThread.start();
    }

    FilteredList<Requisicion> filtro = new FilteredList(listaRequisiciones, p -> true);

    @FXML
    public void buscar(KeyEvent evt) {
        cjBuscar.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filtro.setPredicate((Predicate<? super Requisicion>) req -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return (String.valueOf(req.getNumerorequisicion()).contains(newValue) || 
                        req.getReferencia().toLowerCase().contains(newValue.toLowerCase()));
            });
        });
        SortedList<Requisicion> sorterData = new SortedList<>(filtro);
        sorterData.comparatorProperty().bind(tablaRequisiciones.comparatorProperty());
        tablaRequisiciones.setItems(sorterData);
    }

    @FXML
    void imprimirRequisicion(ActionEvent event) {
        if (tablaRequisiciones.getSelectionModel().getSelectedItem() == null) {
            util.Metodos.alert("Mensaje", "Seleccione una requisicion de la tabla", null, Alert.AlertType.INFORMATION, null, null);
            return;
        }
        HashMap<String, Object> p = new HashMap<>();
        p.put("IDREQUISICION", tablaRequisiciones.getSelectionModel().getSelectedItem().getIdrequisicion());
        try {
            util.Metodos.generarReporte(p, "REQUISICION");
        } catch (JRException | IOException ex) {
            Logger.getLogger(RequisicionController.class.getName()).log(Level.SEVERE, null, ex);
            util.Metodos.alert("Error", "Error al generar el reporte.", null, Alert.AlertType.ERROR, ex, null);
        }        
    }

    Tab tabNuevoRequisicion = null;

    @FXML
    void nuevaRequisicion(ActionEvent evt) {
        
        int num_req = util.Metodos.getConsecutivo("numerorequisicion", false);
        if(num_req==-1){
            return;
        }
        
        ContextMenu contex = new ContextMenu();
        tabNuevoRequisicion = new Tab("REQ # "+(num_req+1) );

        ObservableList<Pedido> listaPedido = FXCollections.observableArrayList();        
        
        TableView<Pedido> tablaPedido = new TableView();
        tablaPedido.setEditable(true);
        tablaPedido.getSelectionModel().cellSelectionEnabledProperty().set(true);
        tablaPedido.setItems(listaPedido);
        tablaPedido.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) tablaPedido.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });                
        
        tablaPedido.setRowFactory(tv ->{
            TableRow<Pedido> row = new TableRow<>();
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(contex));
            return row;
        });
        
        tablaPedido.setOnKeyPressed(event -> {
            if (event.getCode().isLetterKey() || event.getCode().isDigitKey() || event.getCode() == KeyCode.BACK_SPACE) {
                final TablePosition<Pedido, ?> focusedCell = tablaPedido.focusModelProperty().get().focusedCellProperty().get();
                tablaPedido.edit(focusedCell.getRow(), focusedCell.getTableColumn());
            } else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.TAB) {
                tablaPedido.getSelectionModel().selectNext();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {                
                if (tablaPedido.getSelectionModel().isCellSelectionEnabled()) {
                    TablePosition<Pedido, ?> pos = tablaPedido.getFocusModel().getFocusedCell();
                    if (pos.getColumn() - 1 >= 0) {
                        tablaPedido.getSelectionModel().select(pos.getRow(), tablaPedido.getVisibleLeafColumn((tablaPedido.getVisibleLeafIndex(pos.getTableColumn()) + -1)));
                    } else if (pos.getRow() < tablaPedido.getItems().size()) {
                        tablaPedido.getSelectionModel().select(pos.getRow() - 1, tablaPedido.getVisibleLeafColumn(tablaPedido.getVisibleLeafColumns().size() - 1));
                    }
                } else {
                    int focusIndex = tablaPedido.getFocusModel().getFocusedIndex();
                    if (focusIndex == -1) {
                        tablaPedido.getSelectionModel().select(tablaPedido.getItems().size() - 1);
                    } else if (focusIndex > 0) {
                        tablaPedido.getSelectionModel().select(focusIndex - 1);
                    }
                }
                event.consume();
            }
        });        
        
        Button btnAgregar = new Button("", new FontIcon(FontAwesome.PLUS));
        btnAgregar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                FXMLLoader loader = new FXMLLoader();
                StackPane root = null;
                TablaProductosController tpc = null;
                try {
                    root = loader.load(getClass().getResourceAsStream(NavegadorDeContenidos.TablaProductos));
                    tpc = loader.getController();
                } catch (IOException ex) {
                    Logger.getLogger(RequisicionController.class.getName()).log(Level.SEVERE, null, ex);
                }
                Stage stage = new Stage();
                stage.getIcons().add(new Image("/images/producto32.png"));
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(anchorPane.getScene().getWindow());
                stage.setX((Screen.getPrimary().getVisualBounds().getWidth() / 2) - (640 / 2));
                stage.setY((Screen.getPrimary().getVisualBounds().getHeight() / 2) - (480 / 2));
                stage.showAndWait();
                tpc.getProductosSeleccionados().forEach(p->{
                    Pedido ped = new Pedido();
                    ped.setIdpedido(0);
                    ped.setProducto(p);
                    ped.setCantidadsolicitada(1);
                    ped.setPrecioinicial(1);
                    ped.setObservaciones(null);
                    listaPedido.add(ped);
                });
                for (int i = 1; i < tablaPedido.getColumns().size(); i++) {
                    util.Metodos.changeSizeOnColumn(tablaPedido.getColumns().get(i), tablaPedido);
                }
            }
        });
        
        TableColumn colIten = new TableColumn();
        colIten.setMinWidth(40);
        colIten.setPrefWidth(40);
        colIten.setGraphic(btnAgregar);
        colIten.setCellValueFactory(new Callback<CellDataFeatures<Pedido, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(CellDataFeatures<Pedido, Integer> p) {
                return new ReadOnlyObjectWrapper(p.getValue().getIdpedido());
            }
        });

        colIten.setCellFactory(new Callback<TableColumn<Pedido, Integer>, TableCell<Pedido, Integer>>() {
            @Override
            public TableCell<Pedido, Integer> call(TableColumn<Pedido, Integer> param) {
                TableCell cell = new TableCell<Pedido, Integer>(){
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if(this.getTableRow() != null && item != null){
                            setText((this.getTableRow().getIndex()+1) + "");
                        }else{
                            setText("");
                        }
                    }
                };
                cell.setStyle("-fx-alignment: CENTER;");
                return cell;
            }
        });

        TableColumn colProducto = new TableColumn("PRODUCTO / SERVICIO");
        colProducto.setCellValueFactory(new PropertyValueFactory("producto"));

        TableColumn colCantidad = new TableColumn("CANTIDAD");
        colCantidad.setCellValueFactory(new PropertyValueFactory("cantidadsolicitada"));
        colCantidad.setCellFactory(tc -> new FormatCell.IntegerCell<>());        

        TableColumn colPrecio = new TableColumn("PRECIO INICIAL");
        colPrecio.setCellValueFactory(new PropertyValueFactory("precioinicial"));
//        colPrecio.setCellFactory(tc -> new util.CurrencyCell<>());

        tablaPedido.getColumns().addAll(colIten, colProducto, colCantidad, colPrecio);        
        
        MenuItem item = new MenuItem("Añadir nota", new FontIcon(FontAwesome.PLUS));
        item.setOnAction( action ->{
            Pedido ped = tablaPedido.getSelectionModel().getSelectedItem();
            
            Dialog dialog = new Dialog<>();
            dialog.setTitle("Añadir Observaciones");            
            dialog.setHeaderText(""+tablaPedido.getSelectionModel().getSelectedItem().getProducto());
            dialog.setGraphic(new ImageView(this.getClass().getResource("/images/register_producto.png").toString()));
            ButtonType loginButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            TextArea observaciones = new TextArea(ped.getObservaciones());
            grid.add(new Label("Observaciones:"), 0, 0);
            grid.add(observaciones, 0, 1);
            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);
            observaciones.textProperty().addListener((observable, oldValue, newValue) -> {
                loginButton.setDisable(newValue.trim().isEmpty());
            });
            dialog.getDialogPane().setContent(grid);
            Platform.runLater(() -> observaciones.requestFocus());
            Platform.runLater(() -> observaciones.end());
            Optional result = dialog.showAndWait();                        
                        
            if(result.get()==loginButtonType){                
                ped.setObservaciones(observaciones.getText());
            }
        });
                
        contex.getItems().add(item);

        HBox contenedorReferencia = new HBox(5.0);

        TextField cjreferencia = new TextField();
        cjreferencia.setTooltip(new Tooltip("Referencia"));
        cjreferencia.textProperty().addListener((observable, oldValue, newValue) -> {
            if(null!=newValue){
                cjreferencia.getTooltip().hide();
            }
        });
        cjreferencia.setPromptText("Ingrese una palabra clave que identifique la requisicion en general...");
        HBox.setHgrow(cjreferencia, Priority.ALWAYS);
        
        contenedorReferencia.getChildren().addAll(new Label("Referencia:"), cjreferencia, btnAgregar);
        contenedorReferencia.setAlignment(Pos.CENTER);                        
                
        Button btnGuardarRequisicion = new Button("Guardar", new FontIcon(FontAwesome.SAVE));
        btnGuardarRequisicion.setOnAction( action-> {
            if(cjreferencia.getText().isEmpty()){
                cjreferencia.getTooltip().setText("Ingrese una palabra clave que identifique la requisicion en general");
                cjreferencia.tooltipProperty().get().show(cjreferencia, util.Metodos.getX(cjreferencia), util.Metodos.getY(cjreferencia)+cjreferencia.getHeight());
                return;
            }
            if(tablaPedido.getItems().isEmpty()){
                util.Metodos.alert("Mensaje", "Debe registrar al menos un producto o servicio", null, Alert.AlertType.WARNING, null, null);
                return;
            }
            String sql = "INSERT INTO requisicion (referencia,idusuario) ";
            sql += "VALUES ('"+cjreferencia.getText()+"' , '"+model.Usuario.getInstanceUser(0, null, null, null).getIdusuario()+"');";            
            try {
                con = new Conexion();
                con.getCon().setAutoCommit(false);
                PreparedStatement pst = con.getCon().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                if(pst.executeUpdate()>0){
                    ResultSet rs = pst.getGeneratedKeys();
                    rs.next();                    
                    sql = "INSERT INTO pedidos (idrequisicion,idproducto,cantidadsolicitada,precioinicial,observaciones) VALUES";
                    for (Pedido ped : tablaPedido.getItems()) {
                        sql += "(";
                        sql += " "+rs.getInt(1)+" , "+ped.getProducto().getIdproducto()+" , "+ped.getCantidadsolicitada()+" , "+ped.getPrecioinicial()+" , '"+ped.getObservaciones()+"' ";
                        sql += "),";
                    }
                    sql = sql.substring(0, sql.length()-1)+";";
                    pst = con.getCon().prepareStatement(sql);
                    if(pst.executeUpdate()>0){
                        con.getCon().commit();
                        tabPane.getTabs().remove(tabNuevoRequisicion);
                        btnListar.fire();
                    }
                }
            } catch (SQLException ex) {
                util.Metodos.alert("Error", "No se pudo guardar la requisicion", null, Alert.AlertType.ERROR, ex, null);
                Logger.getLogger(RequisicionController.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                try {
                    con.getCon().rollback();
                    con.CERRAR();
                } catch (SQLException ex) {
                    Logger.getLogger(RequisicionController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        HBox contenedorGuardar = new HBox(btnGuardarRequisicion);
        
        VBox vBox = new VBox(5.0);
        vBox.getChildren().addAll(contenedorReferencia, tablaPedido, contenedorGuardar);
        VBox.setVgrow(vBox, Priority.ALWAYS);
        
        StackPane content = new StackPane();
        content.setPadding(new Insets(10.0, 10.0, 10.0, 10.0));        
        content.getChildren().add(vBox);

        tabNuevoRequisicion.contentProperty().set(content);

        tabPane.getTabs().add(tabNuevoRequisicion);
        tabPane.getSelectionModel().selectNext();
        cjreferencia.requestFocus();        
    }

    @FXML 
    void nueva(ActionEvent evt) throws IOException{
        con = new Conexion();
        try {
            ResultSet rs = con.CONSULTAR("SELECT * FROM requisicion ORDER BY fechaderegistro DESC LIMIT 1;");
            if(rs.next()){
                LocalDate fechareq = rs.getDate("fechaderegistro").toLocalDate();
                if(fechareq.isEqual(LocalDate.now())){
                    util.Metodos.alert("Mensaje", "Ya se ha creado la requisicion del dia, con el nombre de "+rs.getString("referencia")+".", null, Alert.AlertType.WARNING, null, null);
                    return;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RequisicionController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        int num_req = util.Metodos.getConsecutivo("numerorequisicion", false);
        if(num_req==-1){
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RequisicionNueva.fxml"));
        AnchorPane root = loader.load();
        RequisicionNuevaController rnc = (RequisicionNuevaController)loader.getController();
        rnc.setRc(this);
        tabPane.getTabs().add(new Tab("REQ # "+(num_req+1), root));
        tabPane.getSelectionModel().selectLast();
    }
    
    @FXML void editar(ActionEvent evt) throws IOException{
        Requisicion req = tablaRequisiciones.getSelectionModel().getSelectedItem();
        if(null != req){            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RequisicionNueva.fxml"));
            AnchorPane root = loader.load();
            RequisicionNuevaController rnc = (RequisicionNuevaController)loader.getController();
            rnc.setIdrequisicion(req);
            rnc.cjreferencia.setText(req.getReferencia());            
            rnc.setRc(this);            
            tabPane.getTabs().add(new Tab("REQ # "+req.getNumerorequisicion(), root));
            tabPane.getSelectionModel().selectLast();            
        }
    }
}
