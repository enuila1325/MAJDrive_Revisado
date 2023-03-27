/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coordinationserverapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Conexion {
    
    public Connection con;
    
    public Connection getConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String ConexionURL = "jdbc:mysql://localhost:3306/Concu_db2";
            String user = "root";
            String pass = "";
            con = DriverManager.getConnection(ConexionURL, user, pass);
            //JOptionPane.showMessageDialog(null, "conexion Exitosa");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error");
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }

    public void Insertar(String user,String name, String fecha, long tam, String tipo ,String pathUser,String pathServer) {
        try {
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            String sql = "SELECT * FROM Concu_db2.registroArchivos WHERE nombre = ? AND userr= ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, user);
            rs = pstmt.executeQuery();
            
      if (rs.next()) {
        sql = "UPDATE Concu_db2.registroArchivos SET fechaIngreso = ?, tipo = ?, tamanio = ?, pathUser = ?, pathServer = ? WHERE userr = ? AND nombre = ?";
        pstmt = con.prepareStatement(sql);
        pstmt.setString(1, fecha);
        pstmt.setString(2, tipo);
        pstmt.setLong(3, tam);
        pstmt.setString(4, pathUser);
        pstmt.setString(5, pathServer);
        pstmt.setString(6, user);
        pstmt.setString(7, name);

        int filasActualizadas = pstmt.executeUpdate();
        System.out.println("Se han actualizado " + filasActualizadas + " filas en la tabla archivos.");
          
      } else {
       sql = "INSERT INTO Concu_db2.registroArchivos(userr,nombre,fechaIngreso,tipo,tamanio,pathUser,pathServer,idserver) values (?,?,?,?,?,?,?,?)";
            PreparedStatement sentencia = con.prepareStatement(sql);
            sentencia.setString(1, user);
            sentencia.setString(2, name);
            sentencia.setString(3, fecha);
            sentencia.setString(4, tipo);
            sentencia.setLong(5, tam);
            sentencia.setString(6, pathUser);
            sentencia.setString(7, pathServer);
            sentencia.setInt(8, 0 );
            sentencia.executeUpdate();
            con.close();
      }
            JOptionPane.showMessageDialog(null, "Ingreso Exitosa");
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void borrar(String name, String user){
        try {
        String query = "DELETE FROM Concu_db2.registroArchivos WHERE nombre = ? AND userr= ?";
        PreparedStatement sentencia = con.prepareStatement(query);
        sentencia.setString(1, name);
        sentencia.setString(2, user);
        sentencia.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
           
    }
    
    public String login(String user, String pass) {
        String resp="";
        try {
            // Check if user already exists
            String sqlCheck = "SELECT * FROM Concu_db2.usuarios WHERE usuario = ? AND contrasena = ?";
            PreparedStatement checkStmt = con.prepareStatement(sqlCheck);
            checkStmt.setString(1, user);
            checkStmt.setString(2, pass);
            ResultSet result = checkStmt.executeQuery();

            if (result.next()) {
                resp= "true";
            }else{
                resp="false";
            }
         } catch (SQLException ex) {
             Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
         }
        return resp;
     }
    
    public boolean crearUser(String user, String pass, String ubicacion, int idserver) {
        getConexion();
                
        boolean resp=false;
         try {
         // Check if user already exists
            String sqlCheck = "SELECT * FROM Concu_db2.usuarios WHERE usuario = ?";
            PreparedStatement checkStmt = con.prepareStatement(sqlCheck);
            checkStmt.setString(1, user);
            ResultSet result = checkStmt.executeQuery();
            
            if (result.next()) {
                System.out.println("El usuario ya existe");
                resp= false;
            }else{
                String sqlInsert = "INSERT INTO Concu_db2.usuarios(usuario, contrasena, idserver, ubicacion) VALUES (?, ?, ?, ?)";
                PreparedStatement stmtInsert = con.prepareStatement(sqlInsert);
                stmtInsert.setString(1, user);
                stmtInsert.setString(2, pass);
                stmtInsert.setInt(3, idserver);
                stmtInsert.setString(4, ubicacion);
                stmtInsert.executeUpdate();    
                System.out.println("Nuevo Usuario Creado");
                resp= true;
            }
        con.close();    
         } catch (SQLException ex) {
             Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
         }
           
        
        return resp;
     }
    
    public int ubicacion(String user) {
        int serverid=0;
        try {
            String consulta = "SELECT idserver FROM usuarios WHERE usuario = ?";
            
            PreparedStatement pstmt = con.prepareStatement(consulta);
            pstmt.setString(1, "nombre_del_usuario_a_buscar");
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int idserver = rs.getInt("idserver");
                serverid=idserver;
            }
            
            rs.close();
            pstmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return serverid;
    }


}
