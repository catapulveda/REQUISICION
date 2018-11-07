package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CentroDeCostos;
import model.Cliente;
import model.Conexion;

public class CentroDeCostosDAO {

    public CentroDeCostosDAO() {
    }
    
    public ObservableList<CentroDeCostos> getCentros(Conexion con){
        
        ObservableList<CentroDeCostos> centros = FXCollections.observableArrayList();
        try {
            ResultSet rs = con.CONSULTAR("SELECT * FROM centrodecostos INNER JOIN cliente USING(idcliente)");
            while(rs.next()){
                Cliente cli = new Cliente();
                cli.setIdcliente(rs.getInt("idcliente"));
                cli.setNombrecliente(rs.getString("nombrecliente"));
                
                CentroDeCostos cc = new CentroDeCostos();
                cc.setIdcentro(rs.getInt("idcentrodecostos"));
                cc.setCentrodecostos(rs.getString("centrodecostos"));
                cc.setCliente(cli);
                
                centros.add(cc);
            }
        } catch (SQLException ex) {
            Logger.getLogger(CentroDeCostosDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return centros;
    }
}
