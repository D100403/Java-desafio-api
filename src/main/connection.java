package ejercicio5api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Conexion {

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/software";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "admin";

    static {
        try {
            Class.forName(DRIVER);
            System.out.println("Conexión con MySQL exitosa");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error en el driver");
        }
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conectado a MySQL");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error de conexión");
        }
        return con;
    }

    public void close(Connection con) {
        try {
            if (con != null) {
                con.close();
                System.out.println("Se cerró la conexión exitosamente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al cerrar la conexión");
        }
    }

    public String getData(String inputData) {
        Connection con = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String jsonData = null;

        try {
            String query = "SELECT * FROM tu_tabla WHERE columna = ?";
            stmt = con.prepareStatement(query);
            stmt.setString(1, inputData);
            rs = stmt.executeQuery();
            
            jsonData = convertResultSetToJson(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra todas las conexiones y recursos
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                close(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return jsonData;
    }

    public static void main(String[] args) {
        Conexion db = new Conexion();
        String jsonData = db.getData("inputData"); // Puedes cambiar "inputData" con los datos que deseas obtener
        // Ahora, envía jsonData al frontend (por ejemplo, usando un servidor HTTP incorporado en Java)
    }
}