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
        var createTableStatements = new String[]{
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
                FOREIGN KEY (class_id) REFERENCES class(id)
            )
            """,
                "  drop table if exists votes;",
                "  drop table if exists candidate;",
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
                id SERIAL PRIMARY KEY,
                candidate_id INTEGER NOT NULL,
                ranking INTEGER NOT NULL,
                class_id_of_voter not null,
                FOREIGN KEY (candidate_id) REFERENCES candidate(id)
                foreign key (class_id_of_voter) references student(class_id)
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

    public void insertDummyData() {
        try {
            try (var statement = connection.createStatement()) {
                var insertClassQuery = """
                            INSERT INTO class (start_year, class_name) VALUES
                            (2022, 'AHIF'),
                            (2022, 'BHIF'),
                            (2023, 'AHIF'),
                            (2023, 'BHIF'),
                            (2020, 'AHIF'),
                            (2020, 'BHIF')
                        """;
                var insertStudentQuery = """
                        INSERT INTO student (first_name, last_name, class_id) VALUES
                        ('Max', 'Mustermann', 1),
                        ('Anna', 'Berger', 1),
                        ('Felix', 'Hofer', 1),
                        ('Julia', 'Mayer', 2),
                        ('David', 'Gruber', 2),
                        ('Emma', 'Schmid', 3),
                        ('Jakob', 'Fischer', 3),
                        ('Sophie', 'Wagner', 4),
                        ('Lukas', 'Huber', 4),
                        ('Lena', 'Steiner', 5),
                        ('Paul', 'Bauer', 5),
                        ('Hannah', 'Müller', 6)
                        """;
                var insertCandidateQuery = """
                            INSERT INTO candidate (name,class, role) VALUES
                            ('Lukas Meier','5ahif', 'Schülersprecher'),
                            ('Anna Schmidt','4ahitm', 'Schülersprecher'),
                            ('Felix Bauer', '3bhif','Abteilungsvertreter'),
                            ('Julia Fischer','5bhitm', 'Abteilungsvertreter')
                        """;

             /*   var insertVotesQuery = """
                    INSERT INTO votes (student_id, candidate_id, ranking) VALUES
                    (1, 1, 5),
                    (2, 1, 4),
                    (3, 2, 6),
                    (4, 3, 2),
                    (5, 3, 1),
                    (6, 4, 2)
                """`*/
                ;

                statement.execute(insertClassQuery);
                statement.execute(insertStudentQuery);
                statement.execute(insertCandidateQuery);
                //  statement.execute(insertVotesQuery);
            }
        } catch (SQLException e) {
            System.err.println("Fehler beim Ausführen der SQL-Anweisung: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
