package models;

import java.io.Serializable;

public class Candidate implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int idIncrement = 3000;
    private final int id;
    private Politician politician;
    private Election election;
    private String partyAffiliation;
    private int votesReceived;
    private boolean winner;

    public Candidate(Politician politician, Election election, String partyAffiliation, int votesReceived) {
        this.id = idIncrement++;
        setPolitician(politician);
        setElection(election, false);
        setPartyAffiliation(partyAffiliation);
        setVotesReceived(votesReceived);
        this.winner = false;
    }

    public int getId() { return id; }
    public Politician getPolitician() { return politician; }
    public Election getElection() { return election; }
    public String getPartyAffiliation() { return partyAffiliation; }
    public int getVotesReceived() { return votesReceived; }
    public boolean isWinner() { return winner; }

    public void setPolitician(Politician politician) {
        this.politician = politician;
    }

    public void setElection(Election election) {
        setElection(election, true);
    }

    public void setElection(Election election, boolean addToElectionList) {
        Election oldElection = this.election;
        this.election = election;

        if (oldElection != null && oldElection != election) {
            oldElection.removeCandidate(this);
        }

        if (addToElectionList && election != null && !election.getCandidates().contains(this)) {
            election.addCandidate(this, false);
        }
    }

    public void setPartyAffiliation(String partyAffiliation) {
        this.partyAffiliation = partyAffiliation != null ? partyAffiliation :
                (politician != null ? politician.getCurrentParty() : "Independent");
    }

    public void setVotesReceived(int votesReceived) {
        this.votesReceived = Math.max(0, votesReceived);
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    @Override
    public String toString() {
        String name = politician != null ? politician.getName() : "Unknown";
        return name + " (" + partyAffiliation + ") - " + votesReceived + " votes" +
                (winner ? " [WINNER]" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Candidate candidate = (Candidate) obj;
        return id == candidate.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}