package view;

import DAO.CotizacionDAO;
import DAO.OrdenDeCompraDAO;
import DAO.PedidoDAO;
import com.jfoenix.controls.JFXListView;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import fx.NavegadorDeContenidos;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.Conexion;
import model.Cotizacion;
import model.OrdenDeCompra;
import model.Pedido;
import model.Producto;
import model.Requisicion;
import net.sf.jasperreports.engine.JRException;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import FormatCell.BooleanCell;

public class RequisicionNuevaController implements Initializable {

    @FXML
    AnchorPane anchorPane;

    @FXML
    TextField cjreferencia;
    @FXML
    TextField cjbuscar;

    FilteredList<Pedido> filtro;

    @FXML
    TableView<Pedido> tablaPedido;

    @FXML
    TableColumn colItem;
    @FXML
    TableColumn colProducto;
    @FXML
    TableColumn<Pedido, String> colUnidad;
    @FXML
    TableColumn colCantidad;
    @FXML
    TableColumn colValor;
    @FXML
    TableColumn colObservaciones;

    @FXML
    TableColumn<Pedido, OrdenDeCompra> colOc;
    @FXML
    TableColumn colSelected;

    @FXML
    Button btnAgregarProducto;
    @FXML
    Button btnGuardar;
    @FXML
    Button btnAnadirNota;
    @FXML
    Button btnEliminarProducto;

    @FXML
    CheckBox checkTodos;

    @FXML
    ContextMenu menuTabla = new ContextMenu();
    @FXML
    MenuItem menuItemNota = new MenuItem("Añadir nota", new FontIcon(FontAwesome.PLUS));
    @FXML
    MenuItem menuItemQuitar = new MenuItem("Eliminar", new FontIcon(FontAwesome.RECYCLE));

    //ObservableList<Pedido> listaPedido = FXCollections.observableArrayList(); 
    ObservableList<Pedido> listaPedidos = FXCollections.observableArrayList();

    ObservableList<Pedido> listaSeleccionados = FXCollections.observableArrayList();

    @FXML
    ComboBox<OrdenDeCompra> comboOrdenes;
    ObservableList<OrdenDeCompra> listaOrdenes = FXCollections.observableArrayList();

    @FXML
    Button btnAsignarOrden;
    @FXML
    Button btnActualizarOrdenes;
    @FXML
    Button btnAdjuntar;

    Tooltip toolTip = new Tooltip();

    private RequisicionController rc;
    private Requisicion requisicion = null;

    Conexion con;

    int indexRow = -1;

    ObservableList<model.Cotizacion> listaCotizaciones = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cjreferencia.requestFocus();

        comboOrdenes.setItems(listaOrdenes);
        comboOrdenes.setConverter(new StringConverter<OrdenDeCompra>() {
            @Override
            public String toString(OrdenDeCompra object) {
                return object.getNumerodeorden() + " " + object.getProveedor().getNombreprovee();
            }

            @Override
            public OrdenDeCompra fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        menuTabla.getItems().addAll(menuItemNota, menuItemQuitar);

        tablaPedido.setEditable(true);
//        tablaPedido.getSelectionModel().cellSelectionEnabledProperty().set(true);
        tablaPedido.getSelectionModel().setCellSelectionEnabled(true);
        tablaPedido.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        tablaPedido.getSelectionModel().selectionModeProperty().setValue(SelectionMode.MULTIPLE);

        tablaPedido.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) tablaPedido.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });

        tablaPedido.setRowFactory(tv -> {
            TableRow<Pedido> row = new TableRow<>();
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(menuTabla));

//            row.setOnMouseMoved(evt->{
//                if(row.getIndex()!=indexRow){
//                    indexRow = row.getIndex();
//                    if (!row.isEmpty()) {
//                        toolTip.setText(tablaPedido.getItems().get(row.getIndex()).getObservaciones());                        
//                        toolTip.show(row, MouseInfo.getPointerInfo().getLocation().x, row.localToScene(0, 0).getY());
//                    }
//                }
//            });
//            row.setOnMouseExited(evt->{
//                toolTip.hide();
//                indexRow = -1;
//            });
            return row;
        });

        tablaPedido.setOnKeyPressed(event -> {
            if (/*event.getCode().isLetterKey() ||*/event.getCode().isDigitKey() || event.getCode() == KeyCode.BACK_SPACE) {
                final TablePosition<Pedido, ?> focusedCell = tablaPedido.focusModelProperty().get().focusedCellProperty().get();
                tablaPedido.edit(focusedCell.getRow(), focusedCell.getTableColumn());
            } else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
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

        colItem.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pedido, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Pedido, Integer> p) {
                return new ReadOnlyObjectWrapper(p.getValue().getIdpedido());
            }
        });

        colItem.setCellFactory(new Callback<TableColumn<Pedido, Integer>, TableCell<Pedido, Integer>>() {
            @Override
            public TableCell<Pedido, Integer> call(TableColumn<Pedido, Integer> param) {
                TableCell cell = new TableCell<Pedido, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (this.getTableRow() != null && item != null) {
                            setText((this.getTableRow().getIndex() + 1) + "");
                        } else {
                            setText("");
                        }
                    }
                };
                cell.setStyle("-fx-alignment: CENTER;");
                return cell;
            }
        });

        colProducto.setCellValueFactory(new PropertyValueFactory("producto"));

        colCantidad.setEditable(true);
        colCantidad.setCellValueFactory(new PropertyValueFactory("cantidadsolicitada"));
        colCantidad.setCellFactory(tc -> new FormatCell.DoubleCell<>());
        colCantidad.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Pedido, Double>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Pedido, Double> event) {
                ((Pedido) event.getTableView().getItems().get(event.getTablePosition().getRow())).setCantidadsolicitada(event.getNewValue());
                if (event.getRowValue().getIdpedido() > 0 && event.getRowValue().getOc().getIdordendecompra() == 0) {
                    actualizarCampo("cantidadsolicitada", event.getNewValue(), event.getRowValue().getIdpedido());
                }
            }
        });

        colObservaciones.setCellValueFactory(new PropertyValueFactory("observaciones"));

        colValor.setEditable(true);
        colValor.setCellValueFactory(new PropertyValueFactory("precioinicial"));
        //colValor.setCellFactory(tc -> new util.CurrencyCell<>());
        colValor.setCellFactory(tc -> new FormatCell.FormatoMoneda<>());
        colValor.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Pedido, Double>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Pedido, Double> event) {
                ((Pedido) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPrecioinicial(event.getNewValue());
                if (event.getRowValue().getIdpedido() > 0 && event.getRowValue().getOc().getIdordendecompra() == 0) {
                    actualizarCampo("precioinicial", event.getNewValue(), event.getRowValue().getIdpedido());
                }
                util.Metodos.changeSizeOnColumn(colValor, tablaPedido);
            }
        });

        colOc.setCellValueFactory(new PropertyValueFactory("oc"));

        colUnidad.setCellValueFactory((param) -> {
            return new SimpleStringProperty(param.getValue().getProducto().getMedida());
        });

        colSelected.setCellValueFactory(new PropertyValueFactory("selected"));
        colSelected.setCellFactory(CheckBoxTableCell.forTableColumn((Integer param) -> {
            Pedido p = tablaPedido.getItems().get(param);
            if (p.getOc().getIdordendecompra() == 0 && p.selectedProperty().get()) {
                listaSeleccionados.add(p);
            } else {
                listaSeleccionados.removeIf(ped -> ped.getProducto().getIdproducto().equals(p.getProducto().getIdproducto()));
            }
            btnAsignarOrden.setText("(" + listaSeleccionados.stream().count() + ") Asignar Orden");
            return p.selectedProperty();
        }));
        listaSeleccionados.addListener((ListChangeListener.Change<? extends Pedido> c) -> {
            btnAsignarOrden.setDisable(listaSeleccionados.size() <= 0);
        });

        cjreferencia.setTooltip(new Tooltip("Referencia"));
        cjreferencia.textProperty().addListener((observable, oldValue, newValue) -> {
            if (null != newValue) {
                cjreferencia.getTooltip().hide();
            }
        });

        btnAdjuntar.setTooltip(new Tooltip("Adjuntar cotizaciones"));

        listaPedidos.addListener((ListChangeListener.Change<? extends Pedido> c) -> {
            btnGuardar.setDisable(listaPedidos.size() <= 0);
        });

        menuItemNota.setOnAction(actionAnadirNota);
        menuItemQuitar.setOnAction(actionQuitarProducto);
        btnAnadirNota.setOnAction(actionAnadirNota);
        btnEliminarProducto.setOnAction(actionQuitarProducto);
        btnAnadirNota.setTooltip(new Tooltip("Añadir nota"));
        btnEliminarProducto.setTooltip(new Tooltip("Eliminar producto"));
    }

    EventHandler actionAnadirNota = (EventHandler<ActionEvent>) (ActionEvent event) -> {
        Pedido ped = tablaPedido.getSelectionModel().getSelectedItem();
        if (ped == null) {
            return;
        }
        Dialog dialog = new Dialog<>();
        dialog.setTitle("Añadir Observaciones");
        dialog.setHeaderText("" + tablaPedido.getSelectionModel().getSelectedItem().getProducto());
        dialog.setGraphic(new ImageView(this.getClass().getResource("/images/register_producto.png").toString()));
        ButtonType loginButtonType = new ButtonType("Guardar", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextArea observaciones = new TextArea(ped.getObservaciones());
        observaciones.setWrapText(true);
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

        if (result.get() == loginButtonType) {
            ped.setObservaciones(observaciones.getText());
            tablaPedido.refresh();
            if (ped.getIdpedido() > 0) {
                actualizarCampo("observaciones", observaciones.getText(), ped.getIdpedido());
                tablaPedido.getItems().get(tablaPedido.getSelectionModel().getSelectedIndex()).setObservaciones(observaciones.getText());
            }
        }
    };

    EventHandler actionQuitarProducto = (EventHandler<ActionEvent>) (ActionEvent event) -> {
        Pedido ped = tablaPedido.getSelectionModel().getSelectedItem();
        if (ped == null) {
            return;
        }
        if (ped.getIdpedido() > 0) {
            con = new Conexion();
            try {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmacion");
                alert.setHeaderText("¿Continuar eliminando?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    if (con.GUARDAR("DELETE FROM pedidos WHERE idpedido=" + ped.getIdpedido()) > 0) {
                    }
                    listaPedidos.removeIf(t -> Objects.equals(t.getProducto().getIdproducto(), ped.getProducto().getIdproducto()));
                    tablaPedido.getItems().remove(tablaPedido.getSelectionModel().getSelectedItem());
                }
            } catch (Exception ex) {
                Logger.getLogger(RequisicionNuevaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    @FXML
    void OnDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    @FXML
    void OnDragDropped(DragEvent e) {
        final Dragboard db = e.getDragboard();
        List<File> lista = e.getDragboard().getFiles();
        if (lista.size() > 0) {
            if (getRequisicion().getIdrequisicion() > 0) {
                Platform.runLater(() -> {
                    int count = 0;
                    for (File file : lista) {
                        try {
                            if (file.isFile()) {
                                String nombre = file.getName().substring(0, file.getName().lastIndexOf("."));
                                String formato = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                                byte[] bytes = Files.readAllBytes(file.toPath());
                                con = new Conexion();
                                PreparedStatement pst = con.getCon().prepareStatement("INSERT INTO cotizaciones (idrequisicion,archivo, formato, nombrearchivo, fechaderegistro) VALUES (" + getRequisicion().getIdrequisicion() + ", ?, '" + formato + "' , '" + nombre + "', '" + LocalDateTime.now() + "')");
                                pst.setBytes(1, bytes);
                                if (pst.executeUpdate() > 0) {
                                    count += 1;
                                    con.CERRAR();
                                }
                            }
                        } catch (IOException | SQLException ex) {
                            Logger.getLogger(RequisicionNuevaController.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                        }
                    }
                    cargarCotizaciones();
                }
                );
            } else {
                util.Metodos.alert("Mensaje", "Primero debe guardar la requisicion para adjuntar cotizaciones", null, AlertType.WARNING, null, null);
            }
            e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            e.setDropCompleted(true);
            e.consume();
        } else {
            e.consume();
        }
    }

    ;

    @FXML
    void agregarProducto(ActionEvent evt) {
        if (getRequisicion() != null && LocalDate.now().isAfter(getRequisicion().getFechaderegistro().toLocalDate())) {
            util.Metodos.alert("Advertencia", "No puede registrar productos a una requisicion con una fecha diferente a la fecha actual.", null, Alert.AlertType.WARNING, null, null);
            return;
        }
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
        tpc.getProductosSeleccionados().forEach(p -> {
            Pedido ped = new Pedido();
            ped.setIdpedido(0);
            ped.setProducto(p);
            ped.setCantidadsolicitada(1);
            ped.setPrecioinicial(1);
            ped.setObservaciones(" ");
            ped.setSelected(false);
            OrdenDeCompra oc = new OrdenDeCompra();
            oc.setIdordendecompra(0);
            oc.setNumerodeorden(0);
            ped.setOc(oc);
            tablaPedido.getItems().add(ped);
            listaPedidos.add(ped);
//            btnGuardar.fire();
        });
        organizarColumnas();
    }

    @FXML
    void guardarRequisicion(ActionEvent evt) {
        if (cjreferencia.getText().isEmpty()) {
            cjreferencia.getTooltip().setText("Ingrese una palabra clave que identifique la requisicion en general");
            cjreferencia.tooltipProperty().get().show(cjreferencia, util.Metodos.getX(cjreferencia), util.Metodos.getY(cjreferencia) + cjreferencia.getHeight());
            return;
        }

        Conexion con = new Conexion();
        String sql = null;
        PreparedStatement pst = null;
        boolean registrado = false;
        try {
            if (getRequisicion() == null) {
                con.getCon().setAutoCommit(false);
                sql = "INSERT INTO requisicion (referencia,idusuario) ";
                sql += "VALUES ('" + cjreferencia.getText() + "' , '" + model.Usuario.getInstanceUser(0, null, null, null).getIdusuario() + "');";
                pst = con.getCon().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                if (pst.executeUpdate() > 0) {
                    ResultSet rs = pst.getGeneratedKeys();
                    registrado = rs.next();
                    Requisicion r = new Requisicion();
                    r.setIdrequisicion(rs.getInt(1));
                    setIdrequisicion(r);
                }
            }

            if (listaPedidos.size() > 0) {
                sql = "INSERT INTO pedidos (idrequisicion,idproducto,cantidadsolicitada,precioinicial,observaciones) VALUES \n";
                for (Pedido ped : listaPedidos) {
                    sql += "(";
                    sql += " " + getRequisicion().getIdrequisicion() + " , " + ped.getProducto().getIdproducto() + " , " + ped.getCantidadsolicitada() + " , " + ped.getPrecioinicial() + " , '" + ped.getObservaciones().trim() + "' ";
                    sql += "),\n";
                }
                sql = sql.substring(0, sql.length() - 2) + ";";
                pst = con.getCon().prepareStatement(sql);
                if (pst.executeUpdate() > 0) {
                    if (registrado) {
                        con.getCon().commit();
                    }
                    listaPedidos.clear();
                    rc.btnListar.fire();
                    rc.tabPane.getSelectionModel().selectFirst();
                    rc.tabPane.getTabs().remove(1);
                }
            }
        } catch (SQLException ex) {
            try {
                con.getCon().rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(RequisicionNuevaController.class.getName()).log(Level.SEVERE, null, ex1);
            }
            util.Metodos.alert("Error", "No se pudo registrar el PRODUCTO / SERVICIO a la requisicion", null, Alert.AlertType.ERROR, ex, null);
            Logger.getLogger(RequisicionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void asignarOrden(ActionEvent evt) {
        if (listaSeleccionados.isEmpty()) {
            util.Metodos.alert("", "Seleccione los productos o items que desea asociar a la orden de compra", null, Alert.AlertType.INFORMATION, null, null);
            return;
        }
        OrdenDeCompra oc = comboOrdenes.getSelectionModel().getSelectedItem();
        con = new Conexion();
        if (oc == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fx.NavegadorDeContenidos.GUARDAR_ORDENDECOMPRA));
                AnchorPane root = loader.load();
                GuardarOrdenDeCompraController occ = (GuardarOrdenDeCompraController) loader.getController();
                occ.setOc(oc);
                occ.setListaSeleccionados(tablaPedido.getItems());

                Stage stage = new Stage();

                Scene scene = new Scene(root);
                stage.setScene(scene);

                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(anchorPane.getScene().getWindow());
                stage.showAndWait();

                if (occ.getIdorden() > 0) {
                    actualizarTabla();
                }
            } catch (IOException | SQLException ex) {
                //Logger.getLogger(RequisicionNuevaController.class.getName()).log(Level.SEVERE, null, ex);
                util.Metodos.alert("Error", "No se pudo actualizar la tabla", null, Alert.AlertType.ERROR, ex, null);
            } finally {
                con.CERRAR();
            }
        } else {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmacion");
            alert.setHeaderText("¿Continuar con el registro?");
            //alert.setContentText("Continuar con el registro?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                listaSeleccionados.forEach(ped -> {
                    try {
                        con.GUARDAR("UPDATE pedidos SET idordendecompra=" + oc.getIdordendecompra() + " WHERE idpedido=" + ped.getIdpedido() + ";");
                    } catch (SQLException ex) {
                        //Logger.getLogger(OrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);
                        util.Metodos.alert("Error", "No se pudo asociar el item " + ped.getProducto().getNombreproducto() + " a la orden de compra ", null, Alert.AlertType.ERROR, ex, null);
                    }
                });
                HashMap<String, Object> p = new HashMap<>();
                p.put("IDORDEN", oc.getIdordendecompra());
                try {
                    util.Metodos.generarReporte(p, "ORDEN_DE_COMPRA");
                    actualizarTabla();
                } catch (JRException | IOException ex) {
                    //Logger.getLogger(OrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);
                    util.Metodos.alert("Error", "No se pudo generar el reporte", null, Alert.AlertType.ERROR, ex, null);
                } catch (SQLException ex) {
                    //Logger.getLogger(RequisicionNuevaController.class.getName()).log(Level.SEVERE, null, ex);
                    util.Metodos.alert("Error", "No se pudo actualizar la tabla", null, Alert.AlertType.ERROR, ex, null);
                }
            }
        }
    }

    @FXML
    void actualizarOrdenes() {
        setIdrequisicion(getRequisicion());
    }

    @FXML
    void verCotizaciones() {
        if (listaCotizaciones.isEmpty()) {
            util.Metodos.alert("Mensaje", "No hay cotizaciones adjuntas", null, AlertType.INFORMATION, null, null);
            return;
        }
        JFXListView<Cotizacion> lista = new JFXListView();
        lista.setItems(listaCotizaciones);
//        listaCotizaciones.forEach(e->{
//            lista.getItems().add(e);
//        });
        lista.setCellFactory((ListView<Cotizacion> list) -> {
            ListCell<Cotizacion> listCell = new ListCell<Cotizacion>() {
                @Override
                protected void updateItem(Cotizacion item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        switch (item.getFormato()) {
                            case "pdf":
                                setGraphic(new FontIcon(FontAwesome.FILE_PDF_O));
                                break;
                            case "xls":
                                setGraphic(new FontIcon(FontAwesome.FILE_EXCEL_O));
                                break;
                            case "xlsx":
                                setGraphic(new FontIcon(FontAwesome.FILE_EXCEL_O));
                                break;
                            case "png":
                                setGraphic(new FontIcon(FontAwesome.FILE_IMAGE_O));
                                break;
                            case "jpg":
                                setGraphic(new FontIcon(FontAwesome.FILE_IMAGE_O));
                                break;
                            default:
                                setGraphic(new FontIcon(FontAwesome.QUESTION_CIRCLE_O));
                        }
                        setText(item.toString());
                    }
                }
            };
            return listCell;
        });
        lista.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2 && evt.getButton() == MouseButton.PRIMARY) {
                Cotizacion c = lista.getSelectionModel().getSelectedItem();
                try {
                    Desktop.getDesktop().open(c.getFile().toFile());
                } catch (IOException ex) {
                    Logger.getLogger(RequisicionNuevaController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        lista.setOnKeyPressed(evt -> {
            if (evt.getCode() == KeyCode.DELETE) {
                Cotizacion c = lista.getSelectionModel().getSelectedItem();
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmacion");
                alert.setHeaderText("¿Continuar con el registro?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    con = new Conexion();
                    CotizacionDAO cDAO = new CotizacionDAO();
                    try {
                        if (cDAO.delete(c, con) > 0) {
                            lista.getItems().remove(lista.getSelectionModel().getSelectedIndex());
                            btnAdjuntar.setText(" (" + listaCotizaciones.size() + ")");
                        }
                    } catch (SQLException ex) {
                        util.Metodos.alert("Mensaje", "No se pudo eliminar la cotizacion", null, AlertType.ERROR, ex, null);
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

    void actualizarTabla() throws SQLException {
        PedidoDAO pDAO = new PedidoDAO();
        listaSeleccionados.clear();
        tablaPedido.getItems().clear();
        tablaPedido.getItems().setAll(pDAO.getPedidos(getRequisicion().getIdrequisicion(), con));
        tablaPedido.refresh();
    }

    public RequisicionController getRc() {
        return rc;
    }

    public void setRc(RequisicionController rc) {
        this.rc = rc;
    }

    void organizarColumnas() {
        for (int i = 1; i < tablaPedido.getColumns().size(); i++) {
            util.Metodos.changeSizeOnColumn(tablaPedido.getColumns().get(i), tablaPedido);
        }
    }

    void actualizarCampo(String columna, Object valor, Object id) {
        con = new Conexion();
        try {
            if (con.GUARDAR("UPDATE pedidos SET " + columna + "='" + valor + "' WHERE idpedido=" + id) > 0) {
                con.CERRAR();
            }
        } catch (SQLException ex) {
            util.Metodos.alert("Error", "No se pudo actualizar el valor de la celda (" + valor + ")", null, Alert.AlertType.ERROR, ex, null);
        }
    }

    public Requisicion getRequisicion() {
        return requisicion;
    }

    public void setIdrequisicion(Requisicion req) {
        this.requisicion = req;
        try {
            con = new Conexion();
            OrdenDeCompraDAO ocDAO = new OrdenDeCompraDAO(con);
            PedidoDAO pDAO = new PedidoDAO();
            tablaPedido.getItems().setAll(pDAO.getPedidos(getRequisicion().getIdrequisicion(), con));
            filtro = new FilteredList(tablaPedido.getItems(), p -> true);
            organizarColumnas();

            cargarCotizaciones();

            
            listaOrdenes.removeAll(listaOrdenes);
            listaOrdenes.setAll(ocDAO.getOrdenes(0));
        } catch (SQLException ex) {
            util.Metodos.alert("ERROR", null, "NO SE PUDO CARGAR LOS DATOS DE LA REQUISICION", Alert.AlertType.ERROR, ex, null);
            Logger.getLogger(RequisicionNuevaController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.CERRAR();
        }
    }

    void cargarCotizaciones() {
        try {
            listaCotizaciones.clear();
            CotizacionDAO cotDAO = new CotizacionDAO();
            Conexion conex = new Conexion();
            listaCotizaciones.setAll(cotDAO.getCotizaciones(getRequisicion().getIdrequisicion(), conex));
            btnAdjuntar.setText(" (" + listaCotizaciones.size() + ")");
            conex.CERRAR();
        } catch (SQLException ex) {
            util.Metodos.alert("ERROR", null, "NO SE PUDO CARGAR LAS COTIZACIONES", Alert.AlertType.ERROR, ex, null);
        }
    }

    @FXML
    void seleccionarTodos(ActionEvent evt) {
        tablaPedido.getItems().forEach(a -> {
            if (a.getOc().getNumerodeorden() == 0) {
                a.setSelected(true);
            }
        });
        tablaPedido.refresh();
    }

    @FXML
    void buscarProducto(KeyEvent evt) {
        cjbuscar.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filtro.setPredicate((Predicate<? super Pedido>) ped -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return ped.getProducto().getNombreproducto().toLowerCase().contains(newValue.toLowerCase());
            });
        });
        SortedList<Pedido> sorterData = new SortedList<>(filtro);
        sorterData.comparatorProperty().bind(tablaPedido.comparatorProperty());
        tablaPedido.setItems(sorterData);
    }
}
