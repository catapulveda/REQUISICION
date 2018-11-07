package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import model.Conexion;

public class UsuarioDAO {

    public UsuarioDAO() {
    }
    
    public boolean login(String user, String password){
        Conexion con = new Conexion();
        try {
            ResultSet rs = con.CONSULTAR("SELECT * FROM usuario WHERE usuario='"+user+"' AND pass='"+password+"' ");
            if(rs.next()){
                model.Usuario usuario = model.Usuario.getInstanceUser(
                        rs.getInt("idusuario"),
                        rs.getString("nombreusuario"),
                        rs.getString("usuario"),
                        rs.getString("pass"));
                return true;
            }                        
        } catch (SQLException ex) {
            Alert a = new Alert(Alert.AlertType.ERROR, "MENSAJE");
            a.showAndWait();
            Logger.getLogger(UsuarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
}
