package at.htl.digitaleschulwahl.database;

import at.htl.digitaleschulwahl.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiagrammController {
    private final Connection connection;

    public DiagrammController(Connection connection) {
        this.connection = DatabaseManager.getInstance().getConnection();


    }

    // SQL abfragen, die die daten groupiert zurückholt, also pro klasse die auswertungen. für jeden candidaten: Klasse + count
    // public Vote(int candidate_id, int ranking, int class_id_of_voter), so ist vote in der db aufgebaut
    // public Candidate(int id, String name, String className, String type) { so ist candidate aufgebaut

    public static class VoteCount {
        private final int candidateId;
        private final String candidateName;
        private final int voterClassId;
        private final long count;

        public VoteCount(int candidateId, String candidateName, int voterClassId, long count) {
            this.candidateId = candidateId;
            this.candidateName = candidateName;
            this.voterClassId = voterClassId;
            this.count = count;
        }

        public int getCandidateId() {
            return candidateId;
        }

        public String getCandidateName() {
            return candidateName;
        }

        public int getVoterClassId() {
            return voterClassId;
        }

        public long getCount() {
            return count;
        }

        @Override
        public String toString() {
            return "VoteCount{" +
                    "candidateId=" + candidateId +
                    ", candidateName='" + candidateName + '\'' +
                    ", voterClassId=" + voterClassId +
                    ", count=" + count +
                    '}';
        }
    }

    public List<VoteCount> getVoteCountsByCandidateAndClass() throws SQLException {
        List<VoteCount> results = new ArrayList<>();

        String sql =
                "SELECT " +
                        "  c.id AS candidate_id, " +
                        "  c.firstname || ' '|| c.lastname AS candidate_name, " +
                        "  v.class_id_of_voter AS voter_class_id, " +
                        "  COUNT(*) AS vote_count " +
                        "FROM vote v " +
                        "JOIN candidate c ON v.candidate_id = c.id " +
                        "GROUP BY c.id, c.name, v.class_id_of_voter";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int candidateId   = rs.getInt("candidate_id");
                String candidateName = rs.getString("candidate_name");
                int voterClassId  = rs.getInt("voter_class_id");
                long count        = rs.getLong("vote_count");

                results.add(new VoteCount(candidateId, candidateName, voterClassId, count));
            }
        }

        return results;
    }

}
