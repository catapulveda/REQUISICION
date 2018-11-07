package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Cliente;
import model.Conexion;

public class ClienteDAO {

    public ClienteDAO() {
    }
    
    public ObservableList<Cliente> getClientes(Conexion con) throws SQLException{
        ObservableList<Cliente> lista = FXCollections.observableArrayList();
        ResultSet rs = con.CONSULTAR("SELECT * FROM cliente");
        while(rs.next()){
            Cliente c = new Cliente();
            c.setIdcliente(rs.getInt("idcliente"));
            c.setNombrecliente(rs.getString("nombrecliente"));
            
            lista.add(c);
        }
        return lista;
    }
    
    public int guardar(Cliente c){
        Conexion con = new Conexion();
        try {
            return con.GUARDAR("INSERT INTO cliente(nombrecliente) VALUES('"+c.getNombrecliente()+"')");
        } catch (SQLException ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            con.CERRAR();
        }
        return -1;
    }
}
