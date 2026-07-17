package org.carloslopez.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private Connection conexion;
    private static Conexion instancia;

    // Credenciales leidas desde variables de entorno, NUNCA escritas aqui.
    // Antes de correr el proyecto, configura en tu sistema (o en la
    // configuracion de ejecucion de tu IDE):
    //   DB_URL  -> ej. jdbc:mysql://localhost:3306/DBVeterinaria2024242?useSSL=false
    //   DB_USER -> ej. root
    //   DB_PASS -> tu contraseña de MySQL
    // Si alguna no esta definida, se usan valores de respaldo pensados solo
    // para desarrollo local (URL/usuario por defecto, sin contraseña).
    private static final String DB_URL = System.getenv().getOrDefault(
            "DB_URL", "jdbc:mysql://localhost:3306/DBVeterinaria2024242?useSSL=false");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASS = System.getenv().getOrDefault("DB_PASS", "");

    private Conexion(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conexion = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
        }catch(IllegalAccessException e){
            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        } 
    }
    
    public static Conexion getInstance(){
        if(instancia == null)
            instancia = new Conexion();
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }
    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }
}
