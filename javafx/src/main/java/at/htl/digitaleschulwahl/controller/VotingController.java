package at.htl.digitaleschulwahl.controller;

import at.htl.digitaleschulwahl.model.Candidate;
import at.htl.digitaleschulwahl.model.Vote;
import at.htl.digitaleschulwahl.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class VotingController {

    private final Connection connection;

    public VotingController() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    public void castVote(Vote vote) {
        var query = "INSERT INTO votes (candidate, ranking) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, vote.getCandidate().getName());
            statement.setInt(2, vote.getRanking());
            statement.executeUpdate();
            System.out.println("Vote for " + vote.getCandidate().getName() + " with ranking " + vote.getRanking() + " recorded.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}
