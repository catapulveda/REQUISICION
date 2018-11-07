package DAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.Conexion;
import model.Proveedor;
import model.Usuario;

public class ProveedorDAO {

    public ProveedorDAO() {
        
    }
    
    public ObservableList<Proveedor> getProveedores(){
        ObservableList<Proveedor> proveedores = FXCollections.observableArrayList();
        String sql = "SELECT * FROM proveedor INNER JOIN usuario u USING(idusuario) ORDER BY nombreprovee ASC";
        Conexion con = new Conexion();
        ResultSet rs;
        try {
            rs = con.CONSULTAR(sql);
            Proveedor p;
            while(rs.next()){
                p = new Proveedor();
                p.setIdproveedor(rs.getInt("idproveedor"));
                p.setNombreprovee(rs.getString("nombreprovee"));
                p.setNitprovee(rs.getString("nitprovee"));
                p.setDireccionprovee(rs.getString("direccionprovee"));
                p.setTelefonofijoprovee(rs.getString("telefonofijoprovee"));
                p.setCelularprovee(rs.getString("celularprovee"));
                p.setCorreoprovee(rs.getString("correoprovee"));
                p.setPaginawebprovee(rs.getString("paginawebprovee"));
                p.setFechaderegistro(rs.getTimestamp("fechaderegistro").toLocalDateTime());
                if(rs.getTimestamp("fechaactualizado")!=null){
                    p.setFechaactualizado(rs.getTimestamp("fechaactualizado").toLocalDateTime());
                }
                p.setCiudad(rs.getString("ciudad"));
                
                Usuario u = new Usuario(rs.getString("nombreusuario"));                
                p.setUsuario(u);
                
                proveedores.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProveedorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            con.CERRAR();
        }        
        return proveedores;
    }

    public int delete(Proveedor p) {
        Conexion con = new Conexion();        
        try {
            return con.GUARDAR("DELETE FROM proveedor WHERE idproveedor="+p.getIdproveedor());
        } catch (SQLException ex) {
            util.Metodos.alert("Error", "Error al intentar eliminar el proveedor", null, Alert.AlertType.WARNING, ex, null);
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            con.CERRAR();
        }
        return 0;
    }

    public int guardar(Proveedor pv) {
        String sql = null;
        if(pv.idproveedorProperty()==null){
            sql = "INSERT INTO proveedor (nombreprovee,nitprovee,direccionprovee,telefonofijoprovee,"
                    + "celularprovee,correoprovee,paginawebprovee,fechaderegistro,idusuario,ciudad) ";
            sql += "VALUES (";
            sql += " '"+pv.getNombreprovee()+"' , '"+pv.getNitprovee()+"' , '"+pv.getDireccionprovee()+"' , '"+pv.getTelefonofijoprovee()+"' , ";
            sql += " '"+pv.getCelularprovee()+"' , '"+pv.getCorreoprovee()+"' , '"+pv.getPaginawebprovee()+"' , '"+pv.getFechaderegistro()+"' , "+model.Usuario.getInstanceUser(0, null, null, null).getIdusuario()+" , '"+pv.getCiudad()+"' ";
            sql += ");";
        }else if(pv.getIdproveedor()>0){
            sql = "UPDATE proveedor SET nombreprovee='"+pv.getNombreprovee()+"' , nitprovee='"+pv.getNitprovee()+"' , "
                    + "direccionprovee='"+pv.getDireccionprovee()+"' , telefonofijoprovee='"+pv.getTelefonofijoprovee()+"' , "
                    + "celularprovee='"+pv.getCelularprovee()+"' , correoprovee='"+pv.getCorreoprovee()+"' , "
                    + "paginawebprovee='"+pv.getPaginawebprovee()+"' , fechaderegistro='"+pv.getFechaderegistro()+"' , "
                    + "idusuario="+model.Usuario.getInstanceUser(0, null, null, null).getIdusuario()+" , ciudad='"+pv.getCiudad()+"' ";
            sql += " WHERE idproveedor="+pv.getIdproveedor();            
        }
        Conexion con = new Conexion();
        int n = 0;
        try {
            n = con.GUARDAR(sql);
        } catch (SQLException ex) {
            util.Metodos.alert("Error", "Error al guardar el proveedor.", null, Alert.AlertType.WARNING, ex, null);
            Logger.getLogger(ProveedorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            con.CERRAR();
        }        
        return n;
    }
}
