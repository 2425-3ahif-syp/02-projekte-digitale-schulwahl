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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
