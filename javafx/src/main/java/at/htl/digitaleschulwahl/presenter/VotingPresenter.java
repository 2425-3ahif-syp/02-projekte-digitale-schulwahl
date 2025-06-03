package at.htl.digitaleschulwahl.presenter;

import at.htl.digitaleschulwahl.database.VoteRepository;
import at.htl.digitaleschulwahl.model.Candidate;
import at.htl.digitaleschulwahl.model.Vote;
import at.htl.digitaleschulwahl.database.DatabaseManager;
import at.htl.digitaleschulwahl.view.VotingView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class VotingPresenter {

    private final VoteRepository voteRepository;

    public VotingPresenter() {
        this.voteRepository = new VoteRepository();
    }




}
