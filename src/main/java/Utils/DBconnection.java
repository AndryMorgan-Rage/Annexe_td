package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection { private static Connection connection;

    private final String url = "jdbc:postgresql://localhost:5432/annexe_db";
    private final String user = "postgres";
    private final String password = "morgan 47";

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL non trouvé !");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données !");
            e.printStackTrace();
        }
        return connection;
    }
}
