package view;

import DAO.BuscarTodoDAO;
import DAO.ProductoDAO;
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
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
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

    @FXML
    AnchorPane ap;
    @FXML
    VBox vbox;
    @FXML
    HBox hbox;

    @FXML
    TextField cjBuscar;

    ObservableList<BuscarTodo> lista = FXCollections.observableArrayList();

    @FXML
    TableView<BuscarTodo> tabla;
    @FXML
    TableColumn<BuscarTodo, String> colReq;
    @FXML
    TableColumn<BuscarTodo, String> colOC;
    @FXML
    TableColumn<BuscarTodo, String> colProveedor;
    @FXML
    TableColumn<BuscarTodo, String> colProducto;

    private Conexion con;

    FilteredList<BuscarTodo> filtro;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colReq.setCellValueFactory((param) -> {
            return new SimpleStringProperty(param.getValue().getReq().getNumerorequisicion() + "");
        });
        colReq.setStyle("-fx-alignment: CENTER;");

        colOC.setCellValueFactory((param) -> {
            return new SimpleStringProperty(param.getValue().getOc().getNumerodeorden() + "");
        });
        colOC.setStyle("-fx-alignment: CENTER;");

        colProveedor.setCellValueFactory((param) -> {
            return new SimpleStringProperty(param.getValue().getPv().getNombreprovee());
        });

        colProducto.setCellValueFactory((param) -> {
            return new SimpleStringProperty(param.getValue().getP().getNombreproducto());
        });

        tabla.setItems(lista);

        filtro = new FilteredList(lista, p -> true);

        Task<ObservableList<BuscarTodo>> loadDataTask = new Task<ObservableList<BuscarTodo>>() {
            @Override
            protected ObservableList<BuscarTodo> call() {
                con = new Conexion();
                BuscarTodoDAO bDAO = new BuscarTodoDAO(con);
                lista.setAll(bDAO.getTodo());
                return lista;
            }
        };
        loadDataTask.setOnFailed(e -> {
            tabla.setPlaceholder(null);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cargar los productos : " + loadDataTask.getException(), javafx.scene.control.ButtonType.CLOSE);
            alert.showAndWait();
        });

        loadDataTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                for (int i = 0; i < tabla.getColumns().size(); i++) {
                    util.Metodos.changeSizeOnColumn(tabla.getColumns().get(i), tabla);
                }
                tabla.setPlaceholder(null);                
            }
        });
        ProgressIndicator progressIndicator = new ProgressIndicator();
        tabla.setPlaceholder(progressIndicator);
        Thread loadDataThread = new Thread(loadDataTask);
        loadDataThread.start();

        Platform.runLater(() -> {

        });
    }

    @FXML
    void buscarProducto(KeyEvent evt) {
        cjBuscar.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filtro.setPredicate((Predicate<? super BuscarTodo>) param -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String upper = newValue.toUpperCase();
                if (param.getP().getNombreproducto().toUpperCase().contains(upper)) {
                    return true;
                } else if (param.getPv().getNombreprovee().toUpperCase().contains(upper)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<BuscarTodo> sorterData = new SortedList<>(filtro);
        sorterData.comparatorProperty().bind(tabla.comparatorProperty());
        tabla.setItems(sorterData);
    }

}
