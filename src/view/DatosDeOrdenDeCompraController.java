package view;

import DAO.CentroDeCostosDAO;
import DAO.FacturaDAO;
import DAO.OrdenDeCompraDAO;
import DAO.ProveedorDAO;
import DAO.RecepcionDePedidoDAO;
import com.jfoenix.controls.JFXListView;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Conexion;
import model.Factura;
import model.OrdenDeCompra;
import model.Pedido;
import model.Producto;
import model.RecepcionDePedido;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class DatosDeOrdenDeCompraController implements Initializable {

    @FXML
    AnchorPane root;

    @FXML
    Button btnDevolver;

    @FXML
    TableView<RecepcionDePedido> tabla;
    @FXML
    TableColumn colItem;
    @FXML
    TableColumn<RecepcionDePedido, String> colCantSoli;
    @FXML
    TableColumn colCantRecib;
    @FXML
    TableColumn colCantPend;
    @FXML
    TableColumn<RecepcionDePedido, String> colProducto;
    @FXML
    private TableColumn<RecepcionDePedido, Boolean> colSeleccion;
    @FXML
    private Button btnAdjuntar;
    @FXML
    private CheckBox checkTodos;

    private OrdenDeCompra oc;

    ProveedorDAO pvDAO = new ProveedorDAO();
    CentroDeCostosDAO ccDAO = new CentroDeCostosDAO();

    OrdenDeCompraDAO ocDAO;
    Conexion con;

    private int idorden = -1;

    ObservableList<Factura> listaFacturas = FXCollections.observableArrayList();
    private ObservableList<Pedido> listaSeleccionados;
    @FXML
    private Button btnRecibirPedidos;

    private ObservableList<RecepcionDePedido> listaRecibidos = FXCollections.observableArrayList();
    @FXML
    private TableColumn colValorFinal;
    @FXML
    private Button btnVerDatosOrden;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        listaFacturas.addListener((ListChangeListener.Change<? extends Factura> c) -> {
            btnAdjuntar.setText("Facturas (" + listaFacturas.size() + ")");
        });

        tabla.getSelectionModel().setCellSelectionEnabled(true);
        tabla.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tabla.setOnKeyPressed(evt -> {
            if (evt.getCode().isDigitKey()) {
                final TablePosition focusedCell = tabla.focusModelProperty().get().focusedCellProperty().get();
                tabla.edit(focusedCell.getRow(), focusedCell.getTableColumn());
            }
        });

        colItem.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<model.RecepcionDePedido, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<model.RecepcionDePedido, Integer> p) {
                return new ReadOnlyObjectWrapper(0);
            }
        });
        colItem.setCellFactory(tc -> new FormatCell.NumberRowCell<>());
        colItem.setMinWidth(35);

        colSeleccion.setCellValueFactory(new Callback<CellDataFeatures<RecepcionDePedido, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(CellDataFeatures<RecepcionDePedido, Boolean> param) {
                RecepcionDePedido p = param.getValue();

                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(p.isSeleccionado());

                booleanProp.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    p.setSeleccionado(newValue);
                    if (p.getPedido().getCantidadsolicitada() > p.getCantidadrecibida()) {
                        if (!newValue) {
                            listaRecibidos.removeIf(rp -> rp.getPedido().getIdpedido() == p.getPedido().getIdpedido());
                        } else {
                            listaRecibidos.add(p);
                        }
                    }
                });
                return booleanProp;
            }
        });

        listaRecibidos.addListener((ListChangeListener.Change<? extends RecepcionDePedido> c) -> {
            btnRecibirPedidos.setText("Recibir (" + listaRecibidos.stream().count() + ")");
            btnRecibirPedidos.setDisable(listaRecibidos.size() <= 0);
        });

        colSeleccion.setCellFactory(new Callback<TableColumn<RecepcionDePedido, Boolean>, TableCell<RecepcionDePedido, Boolean>>() {
            @Override
            public TableCell<RecepcionDePedido, Boolean> call(TableColumn<RecepcionDePedido, Boolean> p) {
                CheckBoxTableCell<RecepcionDePedido, Boolean> cell = new CheckBoxTableCell<>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        colCantSoli.setCellValueFactory((param) -> {
            return new SimpleStringProperty(param.getValue().getPedido().getCantidadsolicitada() + "");
        });
        colCantSoli.setStyle("-fx-alignment: CENTER;");

        colCantRecib.setMinWidth(80);
        colCantRecib.setCellValueFactory(new PropertyValueFactory("cantidadrecibida"));
        colCantRecib.setStyle("-fx-alignment: CENTER;");
        colCantRecib.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {

                FontIcon icon = new FontIcon(FontAwesome.EYE);
                icon.setIconSize(16);
                Button btn = new Button("", icon);

                TableCell tableCell = new TableCell() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            btn.setText(item.toString());
                            btn.setMinWidth(70);
                            btn.setOnAction(evt -> {
                                try {
                                    seleccionandoTodos(false);
                                    RecepcionDePedido ped = ((RecepcionDePedido) getTableView().getItems().get(getIndex()));
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fx.NavegadorDeContenidos.RECEPCION_DE_PEDIDOS));
                                    AnchorPane ap = loader.load();

                                    RecepcionDePedidosController rpc = (RecepcionDePedidosController) loader.getController();
                                    rpc.setListaFacturas(listaFacturas);
                                    rpc.setPed(ped);

                                    Stage stage = new Stage();
                                    Scene scene = new Scene(ap);
                                    stage.setScene(scene);
                                    stage.initOwner(root.getScene().getWindow());
                                    stage.initModality(Modality.APPLICATION_MODAL);
                                    stage.showAndWait();                                    
                                    tabla.refresh();
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

        colCantPend.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<RecepcionDePedido, Double>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<RecepcionDePedido, Double> event) {
                ((RecepcionDePedido) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPendiente(event.getNewValue());
            }
        });
        colCantPend.setCellValueFactory(new PropertyValueFactory("pendiente"));
        colCantPend.setCellFactory(tc -> new FormatCell.ColorYRecibirPedido());

        colValorFinal.setCellValueFactory(new PropertyValueFactory("preciofinal"));
        colValorFinal.setCellFactory(tc -> new FormatCell.CurrencyCell<>());

        colProducto.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().getPedido().getProducto().getNombreproducto());
        });
    }

    @FXML
    void devolverProducto(ActionEvent evt) {
        RecepcionDePedido rp = tabla.getSelectionModel().getSelectedItem();
        if (rp == null) {
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
                if (con.GUARDAR("UPDATE pedidos SET idordendecompra=null WHERE idpedido=" + rp.getPedido().getIdpedido()) > 0) {
                    tabla.getItems().remove(tabla.getSelectionModel().getSelectedIndex());
                    tabla.refresh();
                }
            } catch (SQLException ex) {
                util.Metodos.alert("Error", "Ocurrio un error al intentar excluir el producto " + rp.getPedido().getProducto().getNombreproducto() + " de la orden de compra", null, Alert.AlertType.ERROR, ex, null);
            } finally {
                con.CERRAR();
            }
        }
    }

    @FXML
    void verFacturas() {
        if (listaFacturas.isEmpty()) {
            adjuntarFactura(buscarArchivo());
        } else {
            JFXListView<Factura> lista = new JFXListView();
            lista.setItems(listaFacturas);
            lista.setCellFactory((ListView<Factura> list) -> {
                ListCell<Factura> listCell = new ListCell<Factura>() {
                    @Override
                    protected void updateItem(Factura item, boolean empty) {
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
                    Factura c = lista.getSelectionModel().getSelectedItem();
                    try {
                        Desktop.getDesktop().open(c.getFile().toFile());
                    } catch (IOException ex) {
                        Logger.getLogger(RequisicionNuevaController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            lista.setOnKeyPressed(evt -> {
                if (evt.getCode() == KeyCode.DELETE) {
                    Factura c = lista.getSelectionModel().getSelectedItem();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar factura", ButtonType.YES, ButtonType.NO);
                    alert.setTitle("Confirmar ");
                    alert.setHeaderText("Eliminar " + c.getNombrearchivo());
                    alert.setContentText("Seguro que desea eliminar la factura seleccionada?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.YES) {
                        con = new Conexion();
                        try {
                            if (con.GUARDAR("DELETE FROM facturas WHERE idfactura=" + c.getIdfactura()) > 0) {
                                listaFacturas.remove(lista.getSelectionModel().getSelectedIndex());
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(RecepcionDePedidosController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });

            Button btnNuevo = new Button("Nuevo", new FontIcon(FontAwesome.PLUS));
            btnNuevo.setOnAction((ActionEvent e) -> {
                adjuntarFactura(buscarArchivo());
            });
            Button btnAbrir = new Button("Abrir", new FontIcon(FontAwesome.EYE));
            btnAbrir.setOnAction(evt -> {
                if (lista.getSelectionModel().getSelectedItem() != null) {
                    try {
                        Desktop.getDesktop().open(lista.getSelectionModel().getSelectedItem().getFile().toFile());
                    } catch (IOException ex) {
                        Logger.getLogger(DatosDeOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            HBox hBox = new HBox(5.0, btnNuevo, btnAbrir);

            VBox vBox = new VBox(5.0, hBox, lista);

            AnchorPane.setTopAnchor(vBox, 10.0);
            AnchorPane.setLeftAnchor(vBox, 10.0);
            AnchorPane.setRightAnchor(vBox, 10.0);
            AnchorPane.setBottomAnchor(vBox, 10.0);

            AnchorPane ap = new AnchorPane(vBox);
            Scene scene = new Scene(ap, 320, 240);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.showAndWait();
        }
    }

    private File buscarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Buscar Imagen");
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());
        // Mostar la imagen
        if (file != null) {
            return file.getAbsoluteFile();
        }
        return null;
    }

    private void adjuntarFactura(File file) {
        if (file == null) {
            return;
        }
        try {
            String nombre = file.getName().substring(0, file.getName().lastIndexOf("."));
            String formato = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            byte[] bytes = Files.readAllBytes(file.toPath());
            Factura fac = new Factura();
            fac.setIdordendecompra(getOc().getIdordendecompra());
            fac.setArchivo(bytes);
            fac.setFormato(formato.toLowerCase());
            fac.setNombrearchivo(nombre);

            con = new Conexion();
            FacturaDAO facDAO = new FacturaDAO(con);
            fac.setIdfactura(facDAO.guardar(fac));
            if (fac.getIdfactura() > 0) {
                listaFacturas.add(fac);
            }
        } catch (IOException ex) {
            Logger.getLogger(RegistrarRecepcionDePedidoController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public OrdenDeCompra getOc() {
        return oc;
    }

    public void setOc(OrdenDeCompra oc) {
        this.oc = oc;

        con = new Conexion();

        String sql = "SELECT p.idproducto, p.nombreproducto, ped.idpedido, ped.cantidadsolicitada, ped.precioinicial, \n"
                + "count(rp.cantidadrecibida) AS entregas, sum(rp.cantidadrecibida) as recibidos,\n"
                + "(ped.cantidadsolicitada-sum(rp.cantidadrecibida)) as pendiente\n"
                + "FROM ordendecompra oc\n"
                + "INNER JOIN pedidos ped ON ped.idordendecompra=oc.idordendecompra\n"
                + "LEFT JOIN recepciondepedidos rp ON rp.idpedido=ped.idpedido\n"
                + "INNER JOIN producto p ON p.idproducto=ped.idproducto\n"
                + "WHERE oc.idordendecompra=" + this.oc.getIdordendecompra() + "\n"
                + "GROUP BY oc.idordendecompra, p.idproducto, ped.idpedido;";
        try {
            ResultSet rs = con.CONSULTAR(sql);
            int row = 0;
            while (rs.next()) {
                RecepcionDePedido rp = new RecepcionDePedido();

                Producto p = new Producto();
                p.setIdproducto(rs.getInt("idproducto"));
                p.setNombreproducto(rs.getString("nombreproducto"));

                Pedido ped = new Pedido();
                ped.setIdpedido(rs.getInt("idpedido"));
                ped.setCantidadsolicitada(rs.getDouble("cantidadsolicitada"));
                ped.setPrecioinicial(rs.getDouble("precioinicial"));
                ped.setProducto(p);
                ped.setOc(oc);
                ped.setSelected(false);

                rp.setPedido(ped);
                rp.setCantidadrecibida(rs.getDouble("recibidos"));
                rp.setPreciofinal(rs.getDouble("precioinicial"));
                rp.setPendiente((ped.getCantidadsolicitada() - rp.getCantidadrecibida()));

                tabla.getItems().add(row, rp);
                row++;
            }
            for (int i = 0; i < tabla.getColumns().size(); i++) {
                util.Metodos.changeSizeOnColumn(tabla.getColumns().get(i), tabla);
            }

            sql = "SELECT * FROM facturas WHERE idordendecompra=" + this.oc.getIdordendecompra();
            rs = con.CONSULTAR(sql);
            while (rs.next()) {
                Factura cot = new Factura();
                cot.setIdfactura(rs.getInt("idfactura"));
                cot.setArchivo(rs.getBytes("archivo"));
                cot.setFormato(rs.getString("formato"));
                cot.setNombrearchivo(rs.getString("nombrearchivo"));

                listaFacturas.add(cot);
            }
            btnAdjuntar.setText("Facturas (" + listaFacturas.size() + ")");
        } catch (SQLException ex) {
            Logger.getLogger(DatosDeOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);
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

    @FXML
    private void seleccionarTodos(ActionEvent event) {
        seleccionandoTodos(checkTodos.isSelected());
    }

    int n = 1;

    @FXML
    private void recibirPedido(ActionEvent event) throws IOException {

        VBox rootvBox = new VBox();

        Label lbl0 = new Label("Fecha:");
        DatePicker cjFecha = new DatePicker(LocalDate.now());
        rootvBox.getChildren().addAll(lbl0, cjFecha);

        Label lbl1 = new Label("Nº Factura:");
        TextField cjFactura = new TextField();
        rootvBox.getChildren().addAll(lbl1, cjFactura);

        Label lbl2 = new Label("Nº Remision:");
        TextField cjRemision = new TextField();
        rootvBox.getChildren().addAll(lbl2, cjRemision);

        Button btnGuardar = new Button("Guardar");
        rootvBox.getChildren().addAll(btnGuardar);

        rootvBox.setSpacing(5);
        rootvBox.setPadding(new Insets(10, 10, 20, 10));

        btnGuardar.setOnAction(evt -> {
            LocalDate fecha = cjFecha.getValue();
            String factura = cjFactura.getText();
            String remision = cjRemision.getText();

            RecepcionDePedido rp = new RecepcionDePedido();
            rp.setFactura(factura);
            rp.setFechaderecibido(fecha);
            rp.setRemision(remision);

            con = new Conexion();
            RecepcionDePedidoDAO rpDAO = new RecepcionDePedidoDAO(con);

            listaRecibidos.stream().map((i) -> {
                rp.setCantidadrecibida(i.getPendiente());
                return i;
            }).map((i) -> {
                rp.setPreciofinal(i.getPreciofinal());
                return i;
            }).map((i) -> {
                rp.setObservaciones("");
                rp.setPedido(i.getPedido());
                return i;
            }).forEachOrdered((i) -> {
                try {
                    if (rpDAO.guardar(rp) > 0) {
                        i.setCantidadrecibida((i.getCantidadrecibida() + i.getPendiente()));
                        i.setPendiente(i.getPedido().getCantidadsolicitada() - i.getCantidadrecibida());
                        n++;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(DatosDeOrdenDeCompraController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            con.CERRAR();

            if ((n - 1) == listaRecibidos.size()) {
                ((Stage) rootvBox.getScene().getWindow()).close();
            }
        });

        Stage stage = new Stage();

        Scene scene = new Scene(rootvBox);

        stage.setTitle("Recibir productos");
        stage.setScene(scene);
        stage.initOwner(root.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();

        if ((n - 1) == listaRecibidos.size()) {
            tabla.refresh();
            seleccionandoTodos(false);
            listaRecibidos.clear();
            n = 1;
        }
    }

    public void seleccionandoTodos(boolean estado) {

        checkTodos.setSelected(estado);
        listaRecibidos.clear();
        tabla.getItems().forEach((rp) -> {
            if (estado) {
                if (rp.getPedido().getCantidadsolicitada() > rp.getCantidadrecibida()) {
                    rp.setSeleccionado(estado);
                    listaRecibidos.add(rp);
                }
            } else {
                rp.setSeleccionado(estado);
            }
        });
        tabla.refresh();
    }

    @FXML
    private void verOrden(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fx.NavegadorDeContenidos.GUARDAR_ORDENDECOMPRA));
        AnchorPane ap = loader.load();
        GuardarOrdenDeCompraController occ = (GuardarOrdenDeCompraController) loader.getController();
        
        con = new Conexion();
        ocDAO = new OrdenDeCompraDAO(con);
        
        occ.setOc(ocDAO.getOrdenes(oc.getIdordendecompra()).get(0));
        
        Stage stage = new Stage();

        Scene scene = new Scene(ap);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        
        Rectangle2D tam = Screen.getPrimary().getBounds();
        
        int mitad_ancho = (int) (tam.getWidth()/2);
        int mitad_alto = (int) (tam.getHeight()/2);
        stage.setX(mitad_ancho);
        
        stage.showAndWait();
    }

}
