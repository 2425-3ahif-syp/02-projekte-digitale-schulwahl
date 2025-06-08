package at.htl.digitaleschulwahl.database;

import at.htl.digitaleschulwahl.model.Candidate;
import at.htl.digitaleschulwahl.model.Vote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VoteRepository {
    private final Connection connection;

    public VoteRepository() {
        this.connection = DatabaseManager.getInstance().getConnection();

    }

    public void castVote(Vote vote) {
        var query = "INSERT INTO votes (candidate_id, ranking,class_id_of_voter) VALUES (?, ?,?)";
        //    var whichCandidate= "select name from candidate where id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, vote.getCandidate());
            statement.setInt(2, vote.getRanking());
            statement.setInt(3, vote.getClassIdFromVoter());
            statement.executeUpdate();
            System.out.println("Vote for Candidate  " + vote.getCandidate() + " with ranking " + vote.getRanking() + " recorded.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    /*    try (var stmt= connection.prepareStatement(whichCandidate)){
            stmt.setInt(1,vote.getCandidate());
            System.out.println(stmt.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/
    }

    public List<Candidate> getCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String query = "Select id, name, class, role from candidate";
        try (PreparedStatement statement = connection.prepareStatement(query);
             var resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                candidates.add(new Candidate(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("class"),
                        resultSet.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return candidates;
    }

    public Integer getCandidateIdByName(String candidateName) {
        String query = "SELECT id FROM candidate WHERE name = ?";
        Integer id = null;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, candidateName); // 🟢 Setze Parameter
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    id = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }
}
