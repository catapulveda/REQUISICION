package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
 
public class Conexion {
    
    private Connection con;
    private Statement st;

    public Conexion(){
        try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://192.168.10.5:5432/REQUISICION", "postgres", "cdm");
//            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/REQUISICION", "postgres", "cdm");
//            con = DriverManager.getConnection("jdbc:postgresql://PRODUCCION:5432/REQUISICION", "postgres", "cdm");
            st = con.createStatement (ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        }catch(ClassNotFoundException | SQLException ex){
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex); 
            JOptionPane.showMessageDialog(null, "ERROR EN LA CONEXION A LA BASE DE DATOS+\n"+ex);
        }
    }
    
    public ResultSet CONSULTAR(String sql) throws SQLException{
        System.out.println(sql);
        return st.executeQuery(sql);
    }
    
    public int GUARDAR(String sql) throws SQLException{
        System.out.println("Guardado -> "+sql);
        return st.executeUpdate(sql);
    }
    
    public void CERRAR(){
        try {
            this.con.close();
            this.st.close();
            System.out.println("****CONEXON CERRADA****");
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }

    public Statement getSt() {
        return st;
    }

    public void setSt(Statement st) {
        this.st = st;
    }
        
}
