package view;

import DAO.RecepcionDePedidoDAO;
import FormatCell.CurrencyCell;
import com.jfoenix.controls.JFXListView;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Conexion;
import model.Factura;
import model.RecepcionDePedido;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;

public class RecepcionDePedidosController implements Initializable {

    @FXML
    AnchorPane root;
    @FXML
    VBox vbox;
    @FXML
    ToolBar toolBar;
    @FXML
    TableView<model.RecepcionDePedido> tabla;

    @FXML
    TableColumn colFecha;
    @FXML
    TableColumn colCantidad;
    @FXML
    TableColumn colValorFinal;
    @FXML
    TableColumn colFactura;
    @FXML
    TableColumn colRemision;
    @FXML
    TableColumn colObservacion;
    @FXML
    TableColumn colFechaRegistro;

    @FXML
    Button btnAgregar;
    @FXML
    Button btnAdjuntar;
    @FXML
    private Button btnBorrar;

    RecepcionDePedidoDAO rpDAO;
    Conexion con;

    private RecepcionDePedido ped = new RecepcionDePedido();
    private ObservableList<Factura> listaFacturas;
    ObjectProperty<RecepcionDePedido> item = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colFecha.setCellValueFactory(new PropertyValueFactory("fechaderecibido"));
        colCantidad.setCellValueFactory(new PropertyValueFactory("cantidadrecibida"));
        colValorFinal.setCellValueFactory(new PropertyValueFactory("preciofinal"));
        colValorFinal.setCellFactory(tc -> new CurrencyCell<>());
        colFactura.setCellValueFactory(new PropertyValueFactory("factura"));
        colRemision.setCellValueFactory(new PropertyValueFactory("remision"));
        colObservacion.setCellValueFactory(new PropertyValueFactory("observaciones"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory("fechaderegistro"));

        item.bind(tabla.getSelectionModel().selectedItemProperty());
    }

    @FXML
    void agregarFactura(ActionEvent evt) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fx.NavegadorDeContenidos.REGISTRAR_FACTURA));
        AnchorPane ap = loader.load();
        RegistrarRecepcionDePedidoController rpc = (RegistrarRecepcionDePedidoController) loader.getController();
        rpc.setPedido(getRecepcionDePedido());

        Stage stage = new Stage();
        Scene scene = new Scene(ap);
        stage.setScene(scene);
        stage.initOwner(root.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        tabla.getItems().clear();
        setPed(getRecepcionDePedido());
        tabla.setDisable(false);
    }

    @FXML
    void verFacturas() {
        if (listaFacturas.isEmpty()) {
            util.Metodos.alert("Mensaje", "No hay facturas adjuntas", null, Alert.AlertType.INFORMATION, null, null);
            return;
        }
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

    @FXML
    void borrarFactura() {
        if (item.get() == null) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Seleccione un item de la tabla", ButtonType.OK);
            a.showAndWait();
            return;
        }
        borrar(item.get());
    }

    public void borrar(RecepcionDePedido rp) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar factura", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar ");
        alert.setHeaderText("Eliminar factura Nº " + rp.getFactura());
        alert.setContentText("Seguro que desea eliminar la factura seleccionada?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES) {
            con = new Conexion();
            rpDAO = new RecepcionDePedidoDAO(con);
            try {
                if (rpDAO.borrar(item.get()) > 0) {
                    tabla.getItems().remove(rp);
                    getRecepcionDePedido().setCantidadrecibida(getRecepcionDePedido().getCantidadrecibida() - rp.getCantidadrecibida());
                    getRecepcionDePedido().setPendiente(getRecepcionDePedido().getPedido().getCantidadsolicitada()-getRecepcionDePedido().getCantidadrecibida());
                }
            } catch (SQLException ex) {
                Logger.getLogger(RecepcionDePedidosController.class.getName()).log(Level.SEVERE, null, ex);
                Alert a = new Alert(Alert.AlertType.ERROR, "ERROR AL BORRAR LA FACTURA", ButtonType.OK);
                a.showAndWait();
            }
        }
    }

    public RecepcionDePedido getRecepcionDePedido() {
        return ped;
    }

    public void setPed(RecepcionDePedido ped) {
        this.ped = ped;
        listaFacturas.addListener((ListChangeListener.Change<? extends Factura> c) -> {
            btnAdjuntar.setText("" + listaFacturas.size());
        });
        try {
            con = new Conexion();
            rpDAO = new RecepcionDePedidoDAO(con);
            tabla.getItems().addAll(rpDAO.getPedidos(getRecepcionDePedido().getPedido()));
            tabla.getSelectionModel().selectFirst();
            tabla.refresh();
            for (int i = 0; i < tabla.getColumns().size(); i++) {
                util.Metodos.changeSizeOnColumn(tabla.getColumns().get(i), tabla);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecepcionDePedidosController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            con.CERRAR();
        }
    }

    @FXML
    private void borrarFacturaKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            borrar(tabla.getSelectionModel().getSelectedItem());
        }
    }

    public ObservableList<Factura> getListaFacturas() {
        return listaFacturas;
    }

    public void setListaFacturas(ObservableList<Factura> listaFacturas) {
        this.listaFacturas = listaFacturas;
    }

}
