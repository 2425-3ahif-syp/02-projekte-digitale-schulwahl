package at.htl.digitaleschulwahl.controller;

import at.htl.digitaleschulwahl.model.Vote;
import at.htl.digitaleschulwahl.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Hier kommt die Logik für das Abstimmen
/* die votes der Kandidaten speichern und zählen jo */
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

}
