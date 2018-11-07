package DAO;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import model.Categoria;
import model.Conexion;
import model.Producto;
import model.Usuario;

public class ProductoDAO {

    public ProductoDAO() {        
    }
    
    public int guardar(Producto p, boolean accion){
        String sql = null;
        if(!accion){
            sql = "INSERT INTO producto (codigoproducto,nombreproducto,medida,imagen,fechaderegistro,idusuario,idcategoria) VALUES";
            sql += "(";
            sql += " '"+p.getCodigoproducto()+"' , '"+p.getNombreproducto()+"' , '"+p.getMedida()+"' , ? , '"+LocalDate.now()+"' , "+model.Usuario.getInstanceUser(0, null, null, null).getIdusuario()+" , 5 ";
            sql += ")";
        }else{
            sql = "UPDATE producto SET codigoproducto='"+p.getCodigoproducto()+"', "
                    + "nombreproducto='"+p.getNombreproducto()+"', "
                    + "medida='"+p.getMedida()+"', "
                    + "imagen=? WHERE idproducto="+p.getIdproducto();
        }
        Conexion con = new Conexion();
        try {
            PreparedStatement pst = con.getCon().prepareStatement(sql);
            pst.setBytes(1, p.getImagen());
            int n = pst.executeUpdate();                
            return n;
        } catch (SQLException ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
            util.Metodos.alert("Error", "Error al guardar el producto", null, Alert.AlertType.WARNING, ex, null);
        }finally{
            con.CERRAR();
        }
        return 0;
    }
    
    public int delete(Producto p){
        Conexion con = new Conexion();        
        try {
            return con.GUARDAR("DELETE FROM producto WHERE idproducto="+p.getIdproducto());
        } catch (SQLException ex) {
            util.Metodos.alert("Error", "Error al intentar eliminar el producto", null, Alert.AlertType.WARNING, ex, null);
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            con.CERRAR();
        }
        return 0;
    }
    
    public ObservableList<Producto> getProductos(){
        ObservableList<Producto> productos = FXCollections.observableArrayList();
        try {            
            String sql = "SELECT u.nombreusuario, idproducto, codigoproducto, nombreproducto, medida, fechaderegistro, idcategoria\n" +
                    " FROM producto p INNER JOIN usuario u ON u.idusuario=p.idusuario ORDER BY nombreproducto;";
            Conexion con = new Conexion();        
            ResultSet rs = con.CONSULTAR(sql);
            while(rs.next()){
                Producto p = new Producto();
                p.setIdproducto(rs.getInt("idproducto"));
                p.setCodigoproducto(rs.getString("codigoproducto"));
                p.setNombreproducto(rs.getString("nombreproducto"));
                p.setMedida(rs.getString("medida"));
                p.setFechaderegistro(rs.getDate("fechaderegistro").toLocalDate());
                p.setUsuario(new Usuario(rs.getString("nombreusuario")));
                p.setSelected(false);
                productos.add(p);
            }
            con.CERRAR();            
        } catch (SQLException ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return productos;
    }    
    
    public Image getImagen(int idproducto){
        Image imagen = null;
        Conexion con = new Conexion();
        ResultSet rs;
        try {
            rs = con.CONSULTAR("SELECT imagen FROM producto WHERE idproducto="+idproducto);
            if(rs.next()){
                imagen = new Image(new ByteArrayInputStream(rs.getBytes("imagen")));//ImageIO.read(new ByteArrayInputStream(rs.getBytes("imagen")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            con.CERRAR();
        }
        return imagen;
    }
}
