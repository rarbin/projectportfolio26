package controllers.managers;

import controllers.API.ElectionSystemAPI;
import models.Candidate;
import models.Election;
import models.ElectionType;
import models.Politician;
import utils.CustomList.HashList;
import utils.Validators.InputVal;

public class CandidateManager {
    private final ElectionSystemAPI system;

    public CandidateManager(ElectionSystemAPI system) {
        this.system = system;
    }

    /**
     * ADD CANDIDATE
     */
    public boolean addCandidate(String politicianName, Election election,
                                String partyAffiliation, int votes, boolean isWinner) {
        if (politicianName == null || politicianName.trim().isEmpty()) {
            return false;
        }
        if (election == null) {
            return false;
        }
        if (!InputVal.validStringlength(partyAffiliation, 50)) {
            return false;
        }
        if (!InputVal.isValidVoteCount(votes)) {
            return false;
        }

        Politician politician = system.findPoliticianByName(politicianName);
        if (politician == null) {
            return false;
        }

        // Check if candidate already exists for this politician in this election
        Candidate existingCandidate = system.findCandidate(politician, election);
        if (existingCandidate != null) {
            // Update existing candidate instead of creating new one
            return updateCandidate(politicianName, election, partyAffiliation, votes, isWinner);
        }

        Candidate candidate = new Candidate(politician, election, partyAffiliation, votes);
        candidate.setWinner(isWinner);

        // Add to the system
        boolean addedToSystem = system.addCandidate(candidate);

        if (addedToSystem) {
            // Ensure bidirectional relationships
            politician.addElectionParticipation(candidate);

            // Check if election already has this candidate
            if (!election.getCandidates().contains(candidate)) {
                // Use the version that doesn't cause recursion
                election.getCandidates().addAtLast(candidate);
                election.setCandidateCount(election.getCandidates().size());
            }

            // Recalculate winners for this election
            determineWinners(election);

            return true;
        }

        return false;
    }

    /**
     * COMPLETE UPDATE METHOD
     */
    public boolean updateCandidate(String politicianName, Election election,
                                   String partyAffiliation, int votes, boolean isWinner) {
        if (politicianName == null || election == null) {
            return false;
        }

        Politician politician = system.findPoliticianByName(politicianName);
        if (politician == null) {
            return false;
        }

        Candidate candidate = system.findCandidate(politician, election);
        if (candidate == null) {
            return false;
        }

        // Update all fields
        if (partyAffiliation != null && !partyAffiliation.trim().isEmpty()
                && InputVal.validStringlength(partyAffiliation, 50)) {
            candidate.setPartyAffiliation(partyAffiliation);
        }

        if (InputVal.isValidVoteCount(votes)) {
            candidate.setVotesReceived(votes);
        }

        candidate.setWinner(isWinner);

        // Recalculate winners for this election
        determineWinners(election);

        return true;
    }

    /**
     * INDIVIDUAL UPDATE METHODS
     */
    public boolean updateCandidateVotes(String politicianName, Election election, int newVotes) {
        return updateCandidate(politicianName, election, null, newVotes, false);
    }

    public boolean updateCandidateWinnerStatus(String politicianName, Election election, boolean isWinner) {
        return updateCandidate(politicianName, election, null, -1, isWinner);
    }

    public boolean updateCandidateParty(String politicianName, Election election, String newParty) {
        return updateCandidate(politicianName, election, newParty, -1, false);
    }

    // ==================== SEARCH METHODS ====================

    public HashList<Candidate> searchCandidatesByElectionType(String electionTypeStr) {
        if (electionTypeStr == null || electionTypeStr.trim().isEmpty()) {
            return new HashList<>();
        }

        HashList<Candidate> results = new HashList<>();
        HashList<Candidate> allCandidates = system.getAllCandidates();

        try {
            ElectionType type = ElectionType.valueOf(electionTypeStr.toUpperCase());

            for (int i = 0; i < allCandidates.size(); i++) {
                Candidate c = allCandidates.getByIndex(i);
                if (c != null && c.getElection() != null && c.getElection().getType() == type) {
                    results.add(c.hashCode(), c);
                }
            }
        } catch (IllegalArgumentException e) {
            return results;
        }

        return results;
    }

    public HashList<Candidate> searchCandidatesByYear(int year) {
        HashList<Candidate> results = new HashList<>();
        HashList<Candidate> allCandidates = system.getAllCandidates();

        for (int i = 0; i < allCandidates.size(); i++) {
            Candidate c = allCandidates.getByIndex(i);
            if (c != null && c.getElection() != null && c.getElection().getYear() == year) {
                results.add(c.hashCode(), c);
            }
        }

        return results;
    }

    // ==================== LIST METHODS ====================

    public String listAllCandidates() {
        HashList<Candidate> candidates = system.getAllCandidates();

        if (candidates.isEmpty()) {
            return "No candidates in the system.";
        }

        StringBuilder result = new StringBuilder("All Candidates (" + candidates.size() + "):\n\n");

        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.getByIndex(i);
            if (c != null) {
                String politicianName = c.getPolitician() != null ? c.getPolitician().getName() : "Unknown";
                String electionInfo = c.getElection() != null ?
                        c.getElection().getType() + " - " + c.getElection().getLocation() + " (" + c.getElection().getYear() + ")" :
                        "Unknown Election";

                result.append(String.format("%3d) %-25s | %-40s | %-15s | %6d votes | %s\n",
                        i + 1, politicianName, electionInfo, c.getPartyAffiliation(),
                        c.getVotesReceived(), c.isWinner() ? "WINNER" : ""));
            }
        }

        return result.toString();
    }

    // ==================== WINNER DETERMINATION ====================

    public void determineWinners(Election election) {
        if (election == null) {
            return;
        }

        HashList<Candidate> candidates = election.getCandidates();

        if (candidates == null || candidates.size() == 0) {
            return;
        }

        // Reset all winners
        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.getByIndex(i);
            if (c != null) {
                c.setWinner(false);
            }
        }

        // Sort candidates by votes (descending)
        HashList<Candidate> sortedCandidates = candidates.getAllValues();
        sortedCandidates.insertionSort((c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return 1;
            if (c2 == null) return -1;
            return Integer.compare(c2.getVotesReceived(), c1.getVotesReceived());
        });

        // Determine winners based on available seats
        int seats = election.getSeatsAvailable();
        int winnersCount = Math.min(seats, sortedCandidates.size());

        if (winnersCount > 0) {
            // Set first 'winnersCount' candidates as winners
            for (int i = 0; i < winnersCount; i++) {
                Candidate candidate = sortedCandidates.getByIndex(i);
                if (candidate != null) {
                    candidate.setWinner(true);
                }
            }

            // Handle ties for the last seat
            if (winnersCount > 0 && winnersCount < sortedCandidates.size()) {
                Candidate lastWinner = sortedCandidates.getByIndex(winnersCount - 1);
                if (lastWinner != null) {
                    int lastWinnerVotes = lastWinner.getVotesReceived();

                    // Check for candidates with the same votes as the last winner
                    for (int i = winnersCount; i < sortedCandidates.size(); i++) {
                        Candidate candidate = sortedCandidates.getByIndex(i);
                        if (candidate != null && candidate.getVotesReceived() == lastWinnerVotes) {
                            candidate.setWinner(true);
                            winnersCount++;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    // ==================== STATISTICS ====================

    public String getCandidateStatistics() {
        StringBuilder stats = new StringBuilder("=== CANDIDATE STATISTICS ===\n\n");

        HashList<Candidate> allCandidates = system.getAllCandidates();
        stats.append("Total candidates: ").append(allCandidates.size()).append("\n\n");

        if (!allCandidates.isEmpty()) {
            int totalVotes = 0;
            int winnerCount = 0;
            double highestVotes = 0;
            String highestVotesCandidate = "None";

            for (int i = 0; i < allCandidates.size(); i++) {
                Candidate c = allCandidates.getByIndex(i);
                if (c != null) {
                    totalVotes += c.getVotesReceived();
                    if (c.isWinner()) winnerCount++;

                    if (c.getVotesReceived() > highestVotes) {
                        highestVotes = c.getVotesReceived();
                        highestVotesCandidate = c.getPolitician() != null ?
                                c.getPolitician().getName() : "Unknown";
                    }
                }
            }

            double avgVotes = allCandidates.size() > 0 ?
                    (double) totalVotes / allCandidates.size() : 0;

            stats.append(String.format("Average votes per candidate: %.2f\n", avgVotes));
            stats.append("Total winners: ").append(winnerCount).append("\n");
            stats.append("Highest votes: ").append((int) highestVotes)
                    .append(" (").append(highestVotesCandidate).append(")\n");
        }

        return stats.toString();
    }

    public String getTopPerformersByVotes(int limit) {
        HashList<Candidate> allCandidates = system.getAllCandidates();

        if (allCandidates.isEmpty()) {
            return "No candidates in the system.";
        }

        HashList<Candidate> sorted = allCandidates.getAllValues();
        sorted.insertionSort((c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return 1;
            if (c2 == null) return -1;
            return Integer.compare(c2.getVotesReceived(), c1.getVotesReceived());
        });

        StringBuilder result = new StringBuilder();
        result.append("=== TOP ").append(Math.min(limit, sorted.size()))
                .append(" PERFORMERS BY VOTES ===\n\n");

        for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
            Candidate c = sorted.getByIndex(i);
            if (c != null) {
                String politicianName = c.getPolitician() != null ?
                        c.getPolitician().getName() : "Unknown";
                String electionInfo = c.getElection() != null ?
                        c.getElection().getType() + " " + c.getElection().getLocation() + " (" + c.getElection().getYear() + ")" :
                        "Unknown Election";

                result.append(String.format("%2d. %-25s | %-40s | %,9d votes | %s\n",
                        i + 1, politicianName, electionInfo, c.getVotesReceived(),
                        c.isWinner() ? "WINNER" : ""));
            }
        }

        return result.toString();
    }

    public String getTopPerformersByWins(int limit) {
        HashList<Candidate> allCandidates = system.getAllCandidates();

        if (allCandidates.isEmpty()) {
            return "No candidates in the system.";
        }

        HashList<Politician> allPoliticians = system.getAllPoliticians();

        int[] winCounts = new int[allPoliticians.size()];
        Politician[] politicianArray = new Politician[allPoliticians.size()];

        for (int i = 0; i < allPoliticians.size(); i++) {
            politicianArray[i] = allPoliticians.getByIndex(i);
            winCounts[i] = 0;
        }

        for (int i = 0; i < allCandidates.size(); i++) {
            Candidate c = allCandidates.getByIndex(i);
            if (c != null && c.isWinner() && c.getPolitician() != null) {
                Politician winningPolitician = c.getPolitician();
                for (int j = 0; j < politicianArray.length; j++) {
                    if (politicianArray[j] != null && politicianArray[j].equals(winningPolitician)) {
                        winCounts[j]++;
                        break;
                    }
                }
            }
        }

        int maxWins = 0;
        Politician topPerformer = null;
        String bestElectionInfo = "";
        int bestVotes = 0;

        for (int i = 0; i < politicianArray.length; i++) {
            if (winCounts[i] > maxWins) {
                maxWins = winCounts[i];
                topPerformer = politicianArray[i];

                for (int j = 0; j < allCandidates.size(); j++) {
                    Candidate c = allCandidates.getByIndex(j);
                    if (c != null && c.getPolitician() != null &&
                            c.getPolitician().equals(topPerformer) && c.isWinner()) {
                        if (c.getVotesReceived() > bestVotes) {
                            bestVotes = c.getVotesReceived();
                            if (c.getElection() != null) {
                                bestElectionInfo = c.getElection().getType() + " " +
                                        c.getElection().getLocation() +
                                        " (" + c.getElection().getYear() + ")";
                            }
                        }
                    }
                }
            }
        }

        StringBuilder result = new StringBuilder();
        result.append("=== TOP PERFORMER BY WINS ===\n\n");

        if (topPerformer != null && maxWins > 0) {
            result.append(String.format("1. %-25s | %-40s | %d wins | %,9d votes | WINNER\n",
                    topPerformer.getName(),
                    bestElectionInfo,
                    maxWins,
                    bestVotes));
        } else {
            result.append("No winning politicians in the system.\n");
        }

        return result.toString();
    }
}