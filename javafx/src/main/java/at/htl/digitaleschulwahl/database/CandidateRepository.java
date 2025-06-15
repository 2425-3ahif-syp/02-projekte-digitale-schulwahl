package at.htl.digitaleschulwahl.database;

import at.htl.digitaleschulwahl.model.Candidate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CandidateRepository {

    private final Connection connection;

    public CandidateRepository() {
        this.connection = DatabaseManager.getInstance().getConnection();

    }

    public List<Candidate> getCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String query = "Select id, name, class, role from candidate";
        try (PreparedStatement statement = connection.prepareStatement(query);
                var resultSet = statement.executeQuery();) {
            while (resultSet.next()) {
                candidates.add(new Candidate(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("class"),
                        resultSet.getString("role")));
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

    public boolean addCandidate(String name, String className, String role) {
        String query = "INSERT INTO candidate (name, class, role) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, className);
            statement.setString(3, role);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean candidateExists(String name, String role) {
        String query = "SELECT COUNT(*) FROM candidate WHERE name = ? AND role = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, role);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
