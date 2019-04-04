package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Conexion;
import model.Factura;

public class FacturaDAO {

    private Conexion con;

    public FacturaDAO(Conexion con) {
        this.con = con;
    }

    public int guardar(Factura fac) {
        String sql = "INSERT INTO facturas (idordendecompra,archivo,formato,nombrearchivo) VALUES (" + fac.getIdordendecompra() + " , ?, '" + fac.getFormato() + "' , '" + fac.getNombrearchivo() + "')";
        try {
            PreparedStatement pst = con.getCon().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setBytes(1, fac.getArchivo());
            if (pst.executeUpdate() > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FacturaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            con.CERRAR();
        }
        return 0;
    }
}
