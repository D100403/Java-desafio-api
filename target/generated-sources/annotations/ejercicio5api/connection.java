package ejercicio5api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class connection {

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/apis?useSSL=false";
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

    public static Connection getConnection() {
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

    public static void close(Connection con) {
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
    public static void insertPokemon(pokemon pokemon) {
        Connection con = getConnection();
        PreparedStatement stmt = null;

        try {
            // Crear la declaración SQL para el INSERT
            String sql = "INSERT INTO covid (id, nombre, datos_json) VALUES (?, ?, ?)";
            stmt = con.prepareStatement(sql);

            // Establecer los valores para la declaración preparada
            stmt.setInt(1, pokemon.getId());
            stmt.setString(2, pokemon.getNombre());
            stmt.setString(3, pokemon.getJson());

            // Ejecutar la declaración
            stmt.executeUpdate();

            System.out.println("¡Pokémon insertado con éxito!");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos (siempre en un bloque finally para asegurarse de cerrarlos)
            close(con);
        }
    }
    public String getData(String inputData) {
        Connection con = getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String jsonData = null;

        try {
            String query = "SELECT * FROM covid WHERE covid.id = 1";
            stmt = con.prepareStatement(query);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("No hay resultados en la consulta.");
                String apiUrl = "https://pokeapi.co/api/v2/pokemon/1";
                callApi(apiUrl, 1);
                
            } else {
                do {
                    String columna1 = rs.getString("id");
                    String columna2 = rs.getString("datos_json");

                    System.out.println("Columna1: " + columna1 + ", Columna2: " + columna2);
                } while (rs.next());
            }
   
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    private static void callApi(String apiUrl, int id) {
        try {
            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // System.out.println("Respuesta de la API:\n" + response.toString());
                 // Crear un ObjectMapper de Jackson
                    ObjectMapper objectMapper = new ObjectMapper();

                    // Deserializar el JSON a la clase Pokemon
                    pokemon pokemon = objectMapper.readValue(response.toString(), pokemon.class);

                    // Configurar el id y nombre según tus requisitos
                    pokemon.setId(id);
                    pokemon.setNombre(pokemon.getForms().get(0).getName());
                    pokemon.setJson(response.toString());

                    // Imprimir el resultado
                    System.out.println("Resultado: " + objectMapper.writeValueAsString(pokemon));
                    
                    insertPokemon(pokemon);
                }
            } else {
                System.out.println("Error en la solicitud a la API. Código de respuesta: " + responseCode);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        connection db = new connection();
        String jsonData = db.getData("inputData"); // Puedes cambiar "inputData" con los datos que deseas obtener
        // Ahora, envía jsonData al frontend (por ejemplo, usando un servidor HTTP incorporado en Java)
    }
}