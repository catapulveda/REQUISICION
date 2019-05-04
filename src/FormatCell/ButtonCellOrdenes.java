package FormatCell;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.OrdenDeCompra;
import net.sf.jasperreports.engine.JRException;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import view.DatosDeOrdenDeCompraController;
import view.OrdenesDeCompraController;
import view.RequisicionController;

public class ButtonCellOrdenes<T> extends TableCell<T, String> {
    
    final Button btnImprimr = new Button("", new FontIcon(FontAwesome.PRINT));
    final Button btnAbrir = new Button("", new FontIcon(FontAwesome.LIST_OL));    
    HBox hBox = new HBox(5.0, btnImprimr, btnAbrir);
    
    private OrdenesDeCompraController occ;
    
    public ButtonCellOrdenes(OrdenesDeCompraController occ) {
        hBox.setAlignment(Pos.CENTER);
        btnImprimr.setTooltip(new Tooltip("Imprimir"));
        btnAbrir.setTooltip(new Tooltip("Abrir orden"));
        this.occ = occ;
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            btnAbrir.setOnAction(event -> {
                try {
                    OrdenDeCompra oc = (OrdenDeCompra) getTableView().getItems().get(getIndex());
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/DatosDeOrdenDeCompra.fxml"));
                    AnchorPane root = loader.load();
                    DatosDeOrdenDeCompraController docc = (DatosDeOrdenDeCompraController)loader.getController();
                    docc.setOc(oc);
                    occ.tabPane.getTabs().add(new Tab("O.C # "+oc.getNumerodeorden(), root));
                    occ.tabPane.getSelectionModel().selectLast();                                   
                } catch (IOException ex) {
                    Logger.getLogger(ButtonCellOrdenes.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            btnImprimr.setOnAction(evt->{
                Platform.runLater(() -> {                    
                    HashMap<String, Object> p = new HashMap<>();
                    p.put("IDORDEN", ((OrdenDeCompra) getTableView().getItems().get(getIndex())).getIdordendecompra());
                    try{
                        util.Metodos.generarReporte(p, "ORDEN_DE_COMPRA");                        
                    }catch (JRException | IOException ex){
                        Logger.getLogger(RequisicionController.class.getName()).log(Level.SEVERE, null, ex);
                        util.Metodos.alert("Error", "Error al generar el reporte.", null, Alert.AlertType.ERROR, ex, null);
                    }                    
                });
            });
            setGraphic(hBox);
            setText(null);
            setAlignment(Pos.CENTER);
        }
    }
    
    
    
}
