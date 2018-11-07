/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import DAO.ClienteDAO;
import SearchComboBox.SearchComboBox;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Cliente;
import model.Conexion;

/**
 * FXML Controller class
 *
 * @author PROGRAMADOR
 */
public class RegistrarCentroDeCostosController implements Initializable {

    @FXML GridPane gridPane;
    @FXML TextField cjCentro;
    @FXML Button btn;
    @FXML AnchorPane ap;    
    
    SearchComboBox<Cliente> comboCliente = new SearchComboBox<>();
    
    ClienteDAO cDAO = new ClienteDAO();
    Conexion con;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            con = new Conexion();
            comboCliente.setItems(cDAO.getClientes(con));
            comboCliente.setFilter((item, text) -> item.getNombrecliente().contains(text));
        } catch (SQLException ex) {
            util.Metodos.alert("Mensaje", "No se pudo cargar la lista de clientes", null, Alert.AlertType.ERROR, null, null);
        }finally{con.CERRAR();}
        gridPane.add(comboCliente, 1, 1);
    }
    
    @FXML
    void guardar(){
        Cliente c = comboCliente.getValue();
        if(c==null){
            util.Metodos.alert("Mensaje", "Seleccione un cliente", null, Alert.AlertType.WARNING, null, null);
            return;
        }
        String centro = cjCentro.getText();
        if(centro==null||centro.isEmpty()){
            util.Metodos.alert("Mensaje", "Ingrese el centro de costos", null, Alert.AlertType.WARNING, null, null);
            return;
        }
        con = new Conexion();
        try {
            if(con.GUARDAR("INSERT INTO centrodecostos (centrodecostos,idusuario,idcliente) "
                    + "VALUES('"+centro+"' , "+model.Usuario.getInstanceUser(0, null, null, null).getIdusuario()+" , "+c.getIdcliente()+")")>0){
                ((Stage)ap.getScene().getWindow()).close();
            }
        } catch (SQLException ex) {
            util.Metodos.alert("Mensaje", "Error al registrar el centro de costos", null, Alert.AlertType.ERROR, ex, null);
        }finally{
            con.CERRAR();
        }
    }
    
}
