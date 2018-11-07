package util;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import model.Conexion;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class Metodos {

    public static void changeSizeOnColumn(TableColumn tc, TableView table) {
        try {
            Text t = new Text( ((tc.getText()==null)?"":tc.getText()) );
            double dPixWidth = t.getLayoutBounds().getWidth() + 10;

            int iCount = table.getItems().size(); // # of rows

            for (int i = 0; i < iCount; i++) {                
                String sTest = (tc.getCellData(i)==null)?"":tc.getCellData(i).toString();   // Get string data entry
                t = new Text();
                t.setText(sTest);
                if (t.getLayoutBounds().getWidth() > dPixWidth) {
                    dPixWidth = t.getLayoutBounds().getWidth()+10; // Get pixel width
                }
            }
            tc.setPrefWidth(dPixWidth + 8); // <= It needs 8-10 more to work.  Not sure why...
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
    
    public static void efectoShadowInToolBar(ToolBar toolBar){
        toolBar.getItems().forEach( (a) ->{
            if(a instanceof Button){
                a.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        a.setEffect(new DropShadow());
                    }
                });
                a.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        a.setEffect(null);
                    }
                });
            }
        });
    }
    
    public static byte[] ImageToByte(Image image) {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        if(image!=null){
            BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
            try {                
                ImageIO.write(bImage, "jpg", s);                
            } catch (IOException ex) {
                Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }                
        byte[] res  = s.toByteArray();
        try {
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(Metodos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    public static void alert(String tittle, String header, String content, AlertType type, Exception ex, String graphic){
        Alert a = new Alert(type);
        a.setTitle(tittle);
        
        Stage stage = (Stage) a.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Metodos.class.getClassLoader().getResource("images/LOGO_CONSORCIO.png").toString()));
        
        if(header!=null){
            a.setHeaderText(header); 
        }
        if(content!=null){
            a.setContentText(content);
        }                
        if(graphic!=null){
            a.setGraphic(new ImageView(Metodos.class.getClass().getResource("/images/"+graphic).toString()));
        }                
        
        if(ex!=null){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            
            Label label = new Label("Detalles del error:");
            TextArea textArea = new TextArea(sw.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            a.getDialogPane().setExpandableContent(expContent);
        }        
        a.showAndWait();        
    }
    
    public static void generarReporte(HashMap<String, Object> p, String reportName) throws MalformedURLException, JRException, IOException{
        Task<Void> task = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                Conexion cc = new Conexion();
                JasperReport reporte = (JasperReport) JRLoader.loadObject(new URL(Metodos.class.getResource("/reportes/"+reportName+".jasper").toString()));
                JasperPrint jasperprint = JasperFillManager.fillReport(reporte, p, cc.getCon());
        //        JasperViewer.viewReport(jasperprint, false);
                cc.CERRAR();        
                OutputStream output = null;
                File temp = null;
                try {
                    temp = File.createTempFile(reportName, ".pdf");
                    output = new FileOutputStream(temp); 
                } catch (java.io.FileNotFoundException e) {
                    util.Metodos.alert("Error", "Error al generar el reporte.", null, Alert.AlertType.ERROR, e, null);
                }
                JasperExportManager.exportReportToPdfStream(jasperprint, output);
        //        JasperPrintManager.printReport(jasperprint, false);
                output.close();
                Desktop.getDesktop().open(temp);
                return null;
            }
        };
        Thread t = new Thread(task);
        t.start();
    }
    
    public static int getConsecutivo(String tabla, boolean accion){
        Conexion conex = new Conexion();
        try {
            String sql = (accion)?"SELECT nextval('"+tabla+"');":"SELECT last_value FROM "+tabla;
            ResultSet rs = conex.CONSULTAR(sql);
            rs.next();
            return rs.getInt(1);            
        } catch (Exception ex) {
            util.Metodos.alert("Mensaje de Error", null, ex.getMessage(), AlertType.ERROR, ex, null);            
        }finally{
            conex.CERRAR();
        }
        return -1;
    }
    
    public static int getX(Node node){
        Point2D p = node.localToScene(0.0, 0.0);
        return (int) (p.getX() + node.getScene().getX() + node.getScene().getWindow().getX());
    }
    
    public static int getY(Node node){
        Point2D p = node.localToScene(0.0, 0.0);
        return (int) (p.getY() + node.getScene().getY() + node.getScene().getWindow().getY());
    }

}
