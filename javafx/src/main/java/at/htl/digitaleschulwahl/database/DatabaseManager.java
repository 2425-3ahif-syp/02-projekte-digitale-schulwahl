package at.htl.digitaleschulwahl.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/studentdb";
    private static final String USERNAME = "app";
    private static final String PASSWORD = "app";

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        synchronized (DatabaseManager.class) {
            if (instance == null) {
                instance = new DatabaseManager();
            }
        }

        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void initializeDatabase() throws SQLException {
        boolean tablesExist = checkIfTablesExist();

        if (!tablesExist) {
            var createTableStatements = new String[] {
                    """
                            CREATE TABLE IF NOT EXISTS class (
                                id SERIAL PRIMARY KEY,
                                start_year INTEGER NOT NULL,
                                class_name VARCHAR(4) NOT NULL
                            )
                            """,
                    """
                            CREATE TABLE IF NOT EXISTS student (
                                id SERIAL PRIMARY KEY,
                                first_name VARCHAR(100) NOT NULL,
                                last_name VARCHAR(100) NOT NULL,
                                class_id INTEGER NOT NULL,
                                login_code VARCHAR(20),
                                FOREIGN KEY (class_id) REFERENCES class(id) ON DELETE CASCADE
                            )
                            """,
                    """
                            CREATE TABLE IF NOT EXISTS candidate (
                                id SERIAL PRIMARY KEY,
                                name VARCHAR(100) NOT NULL,
                                class  VARCHAR(100) NOT NULL,
                                role VARCHAR(50) NOT NULL CHECK (role IN ('Schülersprecher', 'Abteilungsvertreter'))
                            )
                            """,
                    """
                            CREATE TABLE IF NOT EXISTS votes (
                                id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                candidate_id INTEGER NOT NULL,
                                ranking INTEGER NOT NULL,
                                class_id_of_voter INTEGER not null,
                                FOREIGN KEY (candidate_id) REFERENCES candidate(id) ON DELETE CASCADE,
                                foreign key (class_id_of_voter) references class(id) ON DELETE CASCADE
                            )
                            """
            };

            try (var statement = connection.createStatement()) {
                for (var createTableStatement : createTableStatements) {
                    statement.execute(createTableStatement);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                insertDummyData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkIfTablesExist() throws SQLException {
        String query = """
                SELECT COUNT(*) as table_count FROM information_schema.tables
                WHERE table_schema = 'public'
                AND table_name IN ('class', 'student', 'candidate', 'votes')
                """;

        try (var statement = connection.prepareStatement(query);
                var resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("table_count") == 4;
            }
        }
        return false;
    }

    public void insertDummyData() {
        try {
            var checkQuery = "SELECT COUNT(*) FROM class";
            boolean dataExists = false;

            try (var statement = connection.createStatement();
                    var resultSet = statement.executeQuery(checkQuery)) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    dataExists = true;
                }
            }

            if (!dataExists) {
                try (var statement = connection.createStatement()) {

                    // 1) Beispiel-Klassen
                    String insertClassQuery = """
                            INSERT INTO class (start_year, class_name) VALUES
                              (2022, 'AHIF'),
                              (2022, 'BHIF'),
                              (2023, 'AHIF'),
                              (2023, 'BHIF'),
                              (2020, 'AHIF'),
                              (2020, 'BHIF');
                            """;
                    statement.execute(insertClassQuery);

                    // 2) Beispiel-Schüler (je Klasse eine Handvoll)
                    String insertStudentQuery = """
                            INSERT INTO student (first_name, last_name, class_id) VALUES
                              ('Max', 'Mustermann', 1),
                              ('Anna', 'Berger',      1),
                              ('Felix', 'Hofer',      1),
                              ('Julia', 'Mayer',      2),
                              ('David', 'Gruber',     2),
                              ('Emma', 'Schmid',      3),
                              ('Jakob', 'Fischer',    3),
                              ('Sophie', 'Wagner',    4),
                              ('Lukas', 'Huber',      4),
                              ('Lena', 'Steiner',     5),
                              ('Paul', 'Bauer',       5),
                              ('Hannah', 'Müller',    6);
                            """;
                    statement.execute(insertStudentQuery);

                    // 3) Beispiel-Kandidaten (zwei Schülersprecher, zwei Abteilungsvertreter)
                    // Wir erwarten, dass diese vier Kandidaten IDs 1, 2, 3 und 4 bekommen.
                    String insertCandidateQuery = """
                            INSERT INTO candidate (name, class, role) VALUES
                              ('Lukas Meier',   '5AHIF',  'Schülersprecher'),
                              ('Anna Schmidt',  '4AHITM', 'Schülersprecher'),
                              ('Felix Bauer',   '3BHIF',  'Abteilungsvertreter'),
                              ('Julia Fischer', '5BHITM', 'Abteilungsvertreter');
                            """;
                    statement.execute(insertCandidateQuery);

                    // 4) Beispiel-Votes
                    // Wir referenzieren candidate_id 1–4 und class_id_of_voter 1–6.
                    // Der Wert „ranking“ setzen wir hier jeweils zu 1,
                    // da er in unserer Auswertung nicht verwendet wird.
                    //
                    // Schülersprecher (candidate_id = 1 und 2):
                    String insertVotesQuery = """
                            INSERT INTO votes VALUES
                              -- Schülersprecher, Klasse 1 (ID=1, AHIF 2022):
                              (default,1, 1, 1),
                              (default,1, 1, 1),
                              (default,2, 1, 1),

                              -- Schülersprecher, Klasse 2 (ID=2, BHIF 2022):
                              (default,1, 1, 2),
                              (default,2, 1, 2),
                              (default,2, 1, 2),

                              -- Schülersprecher, Klasse 3 (ID=3, AHIF 2023):
                              (default,2, 1, 3),
                              (default,2, 1, 3),
                              (default,2, 1, 3),

                              -- Schülersprecher, Klasse 5 (ID=5, AHIF 2020):
                              (default,1, 1, 5),

                              -- Abteilungsvertreter (candidate_id = 3 und 4):
                              -- Klasse 2 (BHIF 2022, ID=2):
                              (default,3, 1, 2),
                              (default,3, 1, 2),
                              (default,4, 1, 2),

                              -- Klasse 3 (AHIF 2023, ID=3):
                              (default,3, 1, 3),
                              (default,4, 1, 3),
                              (default,4, 1, 3),

                              -- Klasse 4 (BHIF 2023, ID=4):
                              (default,4, 1, 4),

                              -- Klasse 6 (BHIF 2020, ID=6):
                              (default,3, 1, 6),
                              (default,3, 1, 6);
                            """;
                    statement.execute(insertVotesQuery);
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Ausführen der SQL-Anweisung: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method to completely reset the database (useful for testing)
     * This will drop all tables and recreate them with fresh dummy data
     */
    public void resetDatabase() throws SQLException {
        var dropTableStatements = new String[] {
                "DROP TABLE IF EXISTS votes CASCADE;",
                "DROP TABLE IF EXISTS candidate CASCADE;",
                "DROP TABLE IF EXISTS student CASCADE;",
                "DROP TABLE IF EXISTS class CASCADE;"
        };

        try (var statement = connection.createStatement()) {
            for (var dropStatement : dropTableStatements) {
                statement.execute(dropStatement);
            }
        }

        // Recreate tables and insert dummy data
        initializeDatabase();
    }

    /**
     * Check if the database connection is still valid
     */
    public boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

}
