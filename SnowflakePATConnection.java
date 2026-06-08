import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class SnowflakePATConnection {

    public static void main(String[] args) {

        try {

            // Charger le fichier de configuration
            Properties config = new Properties();
            try (FileInputStream fis = new FileInputStream("config.properties")) {
                config.load(fis);
            }

            // Lire les paramètres
            String url = config.getProperty("snowflake.url");
            String user = config.getProperty("snowflake.user");
            String token = config.getProperty("snowflake.token");
            
            // Paramètres de connexion
            Properties connectionProperties = new Properties();

            connectionProperties.put("user", user);
            connectionProperties.put("token", token);
            connectionProperties.put(
                    "authenticator",
                    "programmatic_access_token"
            );

            // On injecte les propriétés par défaut (avec le NOUVEAU warehouse)
            connectionProperties.put("warehouse", config.getProperty("snowflake.warehouse"));
            connectionProperties.put("database", config.getProperty("snowflake.database"));
            connectionProperties.put("schema", config.getProperty("snowflake.schema"));
            connectionProperties.put("role", config.getProperty("snowflake.role"));

            System.out.println("Token length = " + token.length());
            System.out.println("Token starts with = " + token.substring(0, Math.min(10, token.length())));

            // Connexion
            try (Connection conn = DriverManager.getConnection(url, connectionProperties);
                 Statement stmt = conn.createStatement()) {

                System.out.println("----------------------------------");
                System.out.println("1. Configuration du contexte...");
                
                // On force le contexte par sécurité
                stmt.execute("USE ROLE DFONDATION_SCOLAIRE");
                stmt.execute("USE DATABASE DFONDATION_771000");
                stmt.execute("USE SCHEMA SCOLAIRE");

                System.out.println("2. Contexte OK ! Exécution de la requête...");
                
                // La requête finale
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM DFONDATION_771000.SCOLAIRE.INDICES_IMSE LIMIT 10;")) {
                    System.out.println("----------------------------------");
                    System.out.println("✅ SUCCÈS ! Voici les données :");
                    
                    while (rs.next()) {
                        // Affiche la première colonne. Vous pouvez utiliser rs.getString("Nom_de_la_colonne") aussi.
                        System.out.println("- " + rs.getString(2)); 
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion Snowflake :");
            e.printStackTrace();
        }
    }
}