package at.htl.digitaleschulwahl.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository für alle Datenbankzugriffe rund um Klassen und VoteCounts.
 */
public class DiagrammRepository {
    private final Connection connection;

    public DiagrammRepository(Connection connection) {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    /**
     *  vote counts pro Kandidat in einer bestimmten Klasse.
     */
    public static class VoteCount {
        private final int candidateId;
        private final String candidateName;
        private final long count; // Anzahl der Stimmen

        public VoteCount(int candidateId, String candidateName, long count) {
            this.candidateId    = candidateId;
            this.candidateName  = candidateName;
            this.count          = count;
        }

        public int getCandidateId()       { return candidateId; }
        public String getCandidateName()  { return candidateName; }
        public long getCount()            { return count; }

        @Override
        public String toString() {
            return "VoteCount{" +
                    "candidateId=" + candidateId +
                    ", candidateName='" + candidateName + '\'' +
                    ", count=" + count +
                    '}';
        }
    }

    /**
     * Hilfsklasse halt... eine Klasse (nur ID und class_name) als Anzeige‐Objekt.
     */
    public static class ClassInfo {
        private final int id;
        private final String className; // nur der Name, z.B. "AHIF" oder "BHIF"

        public ClassInfo(int id, String className) {
            this.id = id;
            this.className = className;
        }

        public int getId()             { return id; }
        public String getClassName()   { return className; }

        @Override
        public String toString() {
            return className;
        }
    }


    public List<ClassInfo> getAllClasses() throws SQLException {
        List<ClassInfo> classes = new ArrayList<>();

        String sql = """
            SELECT id, extract(year from current_date)-start_year||class_name as class_name
              FROM class
             ORDER BY class_name
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id        = rs.getInt("id");
                String name   = rs.getString("class_name");

                System.out.println("Found class: " + id + " - " + name);

                classes.add(new ClassInfo(id, name));
            }
        }

        return classes;
    }


    public List<VoteCount> getVoteCountsByRoleAndClass(String role, int classId) throws SQLException {
        List<VoteCount> results = new ArrayList<>();

        String sql = """
            SELECT
              c.id            AS candidate_id,
              c.name          AS candidate_name,
              COALESCE(SUM(v.ranking), 0)        AS vote_count
            FROM votes v
           right JOIN candidate c
              ON v.candidate_id = c.id
            WHERE c.role = ?
              AND v.class_id_of_voter = ?
            GROUP BY c.id, c.name
            ORDER BY c.name
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, classId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int candidateId     = rs.getInt("candidate_id");
                    String candidateName = rs.getString("candidate_name");
                    long count          = rs.getLong("vote_count");
                    results.add(new VoteCount(candidateId, candidateName, count));
                }
            }
        }

        return results;
    }

    public List<VoteCount> getVoteCountsByRole(String role) throws SQLException {
        List<VoteCount> results = new ArrayList<>();

        String sql = """
            SELECT
              c.id            AS candidate_id,
              c.name          AS candidate_name,
              COALESCE(SUM(v.ranking), 0)        AS vote_count
            FROM votes v
            left JOIN candidate c
              ON v.candidate_id = c.id
            WHERE c.role = ?
            GROUP BY c.id, c.name
            ORDER BY c.name
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int candidateId     = rs.getInt("candidate_id");
                    String candidateName = rs.getString("candidate_name");
                    long count          = rs.getLong("vote_count");
                    results.add(new VoteCount(candidateId, candidateName, count));
                }
            }
        }

        return results;
    }
}
