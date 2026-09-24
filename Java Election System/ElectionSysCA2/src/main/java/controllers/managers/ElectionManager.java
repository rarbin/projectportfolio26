package controllers.managers;

import controllers.API.ElectionSystemAPI;
import models.Candidate;
import models.Election;
import models.ElectionType;
import models.Politician;
import utils.CustomList.HashList;
import utils.Validators.InputVal;

import java.time.LocalDate;

public class ElectionManager {
    private final ElectionSystemAPI system;

    public ElectionManager(ElectionSystemAPI system) {
        this.system = system;
    }

    /**
     * ADD
     */
    public boolean addElection(ElectionType type, String location, LocalDate date, int seats) {
        System.out.println("\n[DEBUG] addElection called:");
        System.out.println("  - Type: " + type);
        System.out.println("  - Location: " + location);
        System.out.println("  - Date: " + date);
        System.out.println("  - Seats: " + seats);

        if (type == null) {
            System.out.println("ERROR: Election type cannot be null.");
            return false;
        }
        if (!InputVal.validStringlength(location, 100)) {
            System.out.println("ERROR: Invalid location.");
            return false;
        }
        if (date == null) {
            System.out.println("ERROR: Election date cannot be null.");
            return false;
        }
        if (!InputVal.isValidSeats(seats)) {
            System.out.println("ERROR: Invalid number of seats.");
            return false;
        }
        if (!InputVal.isValidYear(date.getYear())) {
            System.out.println("ERROR: Invalid election year.");
            return false;
        }

        Election election = new Election(type, location, date, seats);
        boolean added = system.addElection(election);

        if (added) {
            System.out.println("SUCCESS: Added election: " + election);
        } else {
            System.out.println("ERROR: Failed to add election");
        }

        return added;
    }

    public boolean addCandidateToElection(String politicianName, Election election,
                                          String partyAffiliation, int votes) {
        System.out.println("\n[DEBUG] addCandidateToElection called:");
        System.out.println("  - Politician: " + politicianName);
        System.out.println("  - Election: " + (election != null ?
                election.getType() + " - " + election.getLocation() : "NULL"));

        if (politicianName == null || politicianName.trim().isEmpty()) {
            System.out.println("ERROR: Politician name cannot be empty.");
            return false;
        }
        if (election == null) {
            System.out.println("ERROR: Election cannot be null.");
            return false;
        }
        if (!InputVal.validStringlength(partyAffiliation, 50)) {
            System.out.println("ERROR: Invalid party affiliation.");
            return false;
        }
        if (!InputVal.isValidVoteCount(votes)) {
            System.out.println("ERROR: Invalid vote count.");
            return false;
        }

        Politician politician = system.findPoliticianByName(politicianName);
        if (politician == null) {
            System.out.println("ERROR: Politician '" + politicianName + "' not found.");
            return false;
        }

        Election verifiedElection = system.findElectionById(election.getId());
        if (verifiedElection == null) {
            System.out.println("ERROR: Election not found in system.");
            return false;
        }

        Candidate candidate = new Candidate(politician, verifiedElection, partyAffiliation, votes);
        boolean added = system.addCandidate(candidate);

        if (added) {
            if (verifiedElection.getCandidates() == null) {
                verifiedElection.setCandidates(new HashList<>());
            }

            HashList<Candidate> electionCandidates = verifiedElection.getCandidates();
            boolean alreadyExists = false;
            for (int i = 0; i < electionCandidates.size(); i++) {
                Candidate c = electionCandidates.getByIndex(i);
                if (c != null && c.getPolitician() != null &&
                        c.getPolitician().getId() == politician.getId()) {
                    alreadyExists = true;
                    break;
                }
            }

            if (!alreadyExists) {
                electionCandidates.add(candidate.hashCode(), candidate);
                verifiedElection.setCandidateCount(electionCandidates.size());
                System.out.println("  - Added to election's candidate list");
            }

            determineWinners(verifiedElection);
            System.out.println("SUCCESS: Added candidate to election");
        } else {
            System.out.println("ERROR: Failed to add candidate");
        }

        return added;
    }

    /**
     * REMOVE
     */
    public boolean removeElection(Election election) {
        System.out.println("\n[DEBUG] removeElection called:");
        System.out.println("  - Election: " + (election != null ?
                election.getType() + " - " + election.getLocation() : "NULL"));

        if (election == null) {
            System.out.println("ERROR: Election cannot be null.");
            return false;
        }

        Election foundElection = system.findElectionById(election.getId());
        if (foundElection == null) {
            System.out.println("ERROR: Election not found.");
            return false;
        }

        HashList<Candidate> candidates = getCandidatesForElection(foundElection);
        System.out.println("  - Removing " + candidates.size() + " candidates from election");

        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.getByIndex(i);
            if (c != null && c.getPolitician() != null) {
                removeCandidateFromElection(c.getPolitician(), foundElection);
            }
        }
        boolean removed = system.removeElection(foundElection);

        if (removed) {
            System.out.println("SUCCESS: Removed election");
        } else {
            System.out.println("ERROR: Failed to remove election");
        }

        return removed;
    }

    public boolean removeCandidateFromElection(Politician politician, Election election) {
        System.out.println("\n[DEBUG] removeCandidateFromElection called:");
        System.out.println("  - Politician: " + (politician != null ? politician.getName() : "NULL"));
        System.out.println("  - Election: " + (election != null ?
                election.getType() + " - " + election.getLocation() : "NULL"));

        if (politician == null || election == null) {
            System.out.println("ERROR: Politician and election cannot be null.");
            return false;
        }

        Candidate candidate = system.findCandidate(politician, election);
        if (candidate == null) {
            System.out.println("ERROR: Candidate not found.");
            return false;
        }

        if (election.getCandidates() != null) {
            election.getCandidates().removeFirstHit(candidate);
            election.setCandidateCount(election.getCandidates().size());
            System.out.println("  - Removed from election's candidate list");
        }
        boolean removed = system.removeCandidate(candidate);

        if (removed) {
            determineWinners(election);
            System.out.println("SUCCESS: Removed candidate from election");
        } else {
            System.out.println("ERROR: Failed to remove candidate");
        }

        return removed;
    }

    /**
     * UPDATE
     */
    public boolean updateElectionType(Election election, ElectionType newType) {
        if (election == null) {
            return false;
        }
        if (newType == null) {
            return false;
        }

        Election foundElection = system.findElectionById(election.getId());
        if (foundElection == null) {
            return false;
        }

        foundElection.setType(newType);
        System.out.println("Updated election type to " + newType);
        return true;
    }

    public boolean updateElectionLocation(Election election, String newLocation) {
        if (!InputVal.validStringlength(newLocation, 100)) {
            return false;
        }

        Election foundElection = system.findElectionById(election.getId());
        if (foundElection == null) return false;

        foundElection.setLocation(newLocation);
        System.out.println("Updated election location to " + newLocation);
        return true;
    }

    public boolean updateElectionDate(Election election, LocalDate newDate) {
        if (newDate == null) {
            return false;
        }
        if (!InputVal.isValidYear(newDate.getYear())) {
            return false;
        }

        Election foundElection = system.findElectionById(election.getId());
        if (foundElection == null) return false;

        foundElection.setDate(newDate);
        System.out.println("Updated election date to " + newDate);
        return true;
    }

    public boolean updateElectionSeats(Election election, int newSeats) {
        if (!InputVal.isValidSeats(newSeats)) {
            return false;
        }

        Election foundElection = system.findElectionById(election.getId());
        if (foundElection == null) return false;

        foundElection.setSeatsAvailable(newSeats);
        determineWinners(foundElection);
        System.out.println("Updated election seats to " + newSeats);
        return true;
    }

    public boolean updateCandidateVotes(Politician politician, Election election, int newVotes) {
        if (politician == null || election == null) {
            return false;
        }
        if (!InputVal.isValidVoteCount(newVotes)) {
            return false;
        }

        Candidate candidate = system.findCandidate(politician, election);
        if (candidate == null) {
            return false;
        }

        candidate.setVotesReceived(newVotes);
        determineWinners(election);
        System.out.println("Updated votes for " + politician.getName() + " to " + newVotes);
        return true;
    }

    public boolean updateCandidateParty(Politician politician, Election election, String newParty) {
        if (!InputVal.validStringlength(newParty, 50)) {
            return false;
        }

        Candidate candidate = system.findCandidate(politician, election);
        if (candidate == null) {
            return false;
        }

        candidate.setPartyAffiliation(newParty);
        System.out.println("Updated party for " + politician.getName() + " to " + newParty);
        return true;
    }

    /**
     * SEARCH
     */
    public Election findElection(ElectionType type, String location, int year) {
        if (type == null) {
            return null;
        }
        if (location == null || location.trim().isEmpty()) {
            return null;
        }
        if (!InputVal.isValidYear(year)) {
            return null;
        }

        return system.findElectionByDetails(type, location, year);
    }

    public Election findElectionById(int id) {
        return system.findElectionById(id);
    }

    public HashList<Election> searchElections(ElectionType type, Integer year, String location) {
        HashList<Election> results = new HashList<>();

        if (type != null && year == null && (location == null || location.isEmpty())) {
            results = system.searchElectionsByType(type);
        } else if (type == null && year != null && (location == null || location.isEmpty())) {
            results = system.searchElectionsByYear(year);
        } else {
            HashList<Election> allElections = system.getAllElections();

            for (int i = 0; i < allElections.size(); i++) {
                Election e = allElections.getByIndex(i);
                if (e != null) {
                    boolean matches = true;

                    if (type != null && e.getType() != type) {
                        matches = false;
                    }
                    if (year != null && e.getYear() != year) {
                        matches = false;
                    }
                    if (location != null && !location.isEmpty() &&
                            !e.getLocation().toLowerCase().contains(location.toLowerCase())) {
                        matches = false;
                    }

                    if (matches) {
                        results.add(e.hashCode(), e);
                    }
                }
            }
        }

        return results;
    }

    /**
     * LISTING
     */
    public String listAllElections() {
        return listElections(system.getAllElections(), "All Elections:\n", false, "date", false);
    }


    private String listElections(HashList<Election> elections, String header,
                                 boolean detailed, String sortBy, boolean ascending) {
        if (elections.isEmpty()) {
            return header + "No elections found.";
        }

        StringBuilder result = new StringBuilder(header);

        switch (sortBy.toLowerCase()) {
            case "type" -> sortElectionsByType(elections, ascending);
            case "year" -> sortElectionsByYear(elections, ascending);
            case "location" -> sortElectionsByLocation(elections, ascending);
            default -> sortElectionsByDate(elections, ascending);
        }

        for (int i = 0; i < elections.size(); i++) {
            Election e = elections.getByIndex(i);
            if (e != null) {
                if (detailed) {
                    result.append(String.format("%3d) %-12s | %-20s | %-4d | %d candidates | %d seats\n",
                            i + 1, e.getType(), e.getLocation(), e.getYear(),
                            e.getCandidateCount(), e.getSeatsAvailable()));
                } else {
                    result.append(String.format("%3d) %s\n", i + 1, e));
                }
            }
        }

        return result.toString();
    }

    /**
     * SORT
     */
    private void sortElectionsByDate(HashList<Election> elections, boolean ascending) {
        if (elections == null || elections.size() <= 1) return;

        elections.selectionSort((e1, e2) -> {
            if (e1 == null && e2 == null) return 0;
            if (e1 == null) return -1;
            if (e2 == null) return 1;
            int compare = e1.getDate().compareTo(e2.getDate());
            return ascending ? compare : -compare;
        });
    }

    private void sortElectionsByType(HashList<Election> elections, boolean ascending) {
        if (elections == null || elections.size() <= 1) return;

        elections.selectionSort((e1, e2) -> {
            if (e1 == null && e2 == null) return 0;
            if (e1 == null) return -1;
            if (e2 == null) return 1;

            int typeCompare = e1.getType().toString().compareTo(e2.getType().toString());
            if (typeCompare == 0) {
                int dateCompare = e1.getDate().compareTo(e2.getDate());
                return ascending ? dateCompare : -dateCompare;
            }
            return ascending ? typeCompare : -typeCompare;
        });
    }

    private void sortElectionsByYear(HashList<Election> elections, boolean ascending) {
        if (elections == null || elections.size() <= 1) return;

        elections.selectionSort((e1, e2) -> {
            if (e1 == null && e2 == null) return 0;
            if (e1 == null) return -1;
            if (e2 == null) return 1;

            int compare = Integer.compare(e1.getYear(), e2.getYear());
            if (compare == 0) {
                int dateCompare = e1.getDate().compareTo(e2.getDate());
                return ascending ? dateCompare : -dateCompare;
            }
            return ascending ? compare : -compare;
        });
    }

    private void sortElectionsByLocation(HashList<Election> elections, boolean ascending) {
        if (elections == null || elections.size() <= 1) return;

        elections.selectionSort((e1, e2) -> {
            if (e1 == null && e2 == null) return 0;
            if (e1 == null) return -1;
            if (e2 == null) return 1;

            int compare = e1.getLocation().compareToIgnoreCase(e2.getLocation());
            return ascending ? compare : -compare;
        });
    }

    /**
     * STATS
     */
    public String getElectionResults(Election election) {
        if (election == null) {
            return "Election cannot be null.";
        }

        if (election.getCandidates() == null || election.getCandidates().isEmpty()) {
            return "No candidates available for this election.";
        }

        StringBuilder result = new StringBuilder();
        result.append("Election Results for ").append(election.getType())
                .append(" in ").append(election.getLocation())
                .append(" (").append(election.getYear()).append(")\n");
        result.append("=".repeat(60)).append("\n");
        result.append("Candidates (sorted by votes):\n\n");

        HashList<Candidate> candidates = getCandidatesForElection(election);
        sortCandidatesByVotes(candidates, true);

        int seatCount = 0;
        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.getByIndex(i);
            if (c != null && c.getPolitician() != null) {
                result.append(String.format("%2d. %-30s (%s): %,9d votes",
                        i + 1, c.getPolitician().getName(), c.getPartyAffiliation(),
                        c.getVotesReceived()));

                if (c.isWinner()) {
                    seatCount++;
                    result.append("  [WINNER - Seat ").append(seatCount).append("]");
                }
                result.append("\n");
            }
        }

        result.append("\nTotal seats available: ").append(election.getSeatsAvailable());
        result.append("\nTotal candidates: ").append(election.getCandidateCount());
        return result.toString();
    }

    public HashList<Candidate> getCandidatesForElection(Election election) {
        if (election == null) return new HashList<>();

        HashList<Candidate> candidates = election.getCandidates();
        if (candidates == null) {
            candidates = new HashList<>();
            election.setCandidates(candidates);
        }

        HashList<Candidate> allValues = candidates.getAllValues();
        sortCandidatesByVotes(allValues, true);
        return allValues;
    }

    public HashList<Election> getElectionsForPolitician(Politician politician) {
        if (politician == null) return new HashList<>();

        HashList<Election> politicianElections = new HashList<>();
        HashList<Candidate> participations = politician.getElectionsParticipated();

        if (participations != null) {
            for (int i = 0; i < participations.size(); i++) {
                Candidate c = participations.getByIndex(i);
                if (c != null && c.getElection() != null) {
                    Election e = c.getElection();
                    if (!politicianElections.contains(e)) {
                        politicianElections.add(e.hashCode(), e);
                    }
                }
            }
        }

        sortElectionsByDate(politicianElections, false);
        return politicianElections;
    }

    private void sortCandidatesByVotes(HashList<Candidate> list, boolean descending) {
        if (list == null || list.size() <= 1) return;

        list.insertionSort((c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return -1;
            if (c2 == null) return 1;
            int compare = Integer.compare(c1.getVotesReceived(), c2.getVotesReceived());
            return descending ? -compare : compare;
        });
    }

    public void determineWinners(Election election) {
        System.out.println("\n[DEBUG] ElectionManager.determineWinners called:");
        System.out.println("  - Election: " + (election != null ?
                election.getType() + " - " + election.getLocation() + " (" + election.getYear() + ")" : "NULL"));

        if (election == null) {
            System.out.println("  - ERROR: Election is null");
            return;
        }
        if (election.getCandidates() == null) {
            election.setCandidates(new HashList<>());
        }

        HashList<Candidate> candidates = election.getCandidates();
        System.out.println("  - Total candidates: " + candidates.size());

        if (candidates.size() == 0) {
            System.out.println("  - No candidates, no winners to determine");
            return;
        }

        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.getByIndex(i);
            if (c != null) {
                c.setWinner(false);
            }
        }
        HashList<Candidate> sortedCandidates = candidates.getAllValues();
        sortedCandidates.insertionSort((c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return 1;
            if (c2 == null) return -1;
            return Integer.compare(c2.getVotesReceived(), c1.getVotesReceived());
        });

        int seats = election.getSeatsAvailable();
        System.out.println("  - Available seats: " + seats);

        int winnersCount = Math.min(seats, sortedCandidates.size());
        System.out.println("  - Will select " + winnersCount + " winner(s)");

        if (winnersCount > 0) {
            for (int i = 0; i < winnersCount; i++) {
                Candidate candidate = sortedCandidates.getByIndex(i);
                if (candidate != null) {
                    candidate.setWinner(true);
                    System.out.println("  - Winner #" + (i+1) + ": " +
                            (candidate.getPolitician() != null ? candidate.getPolitician().getName() : "Unknown") +
                            " with " + candidate.getVotesReceived() + " votes");
                }
            }

            if (winnersCount > 0 && winnersCount < sortedCandidates.size()) {
                Candidate lastWinner = sortedCandidates.getByIndex(winnersCount - 1);
                if (lastWinner != null) {
                    int lastWinnerVotes = lastWinner.getVotesReceived();

                    for (int i = winnersCount; i < sortedCandidates.size(); i++) {
                        Candidate candidate = sortedCandidates.getByIndex(i);
                        if (candidate != null && candidate.getVotesReceived() == lastWinnerVotes) {
                            candidate.setWinner(true);
                            winnersCount++;
                            System.out.println("  - Additional winner (tie): " +
                                    (candidate.getPolitician() != null ? candidate.getPolitician().getName() : "Unknown"));
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        System.out.println("  - Total winners determined: " + winnersCount);
    }

    /**
     * STATS
     */
    public String getElectionStatistics() {
        String systemStats = system.getSystemStatistics();

        StringBuilder enhancedStats = new StringBuilder();
        enhancedStats.append("=== Enhanced Election Statistics ===\n\n");
        enhancedStats.append(systemStats);

        HashList<Election> allElections = system.getAllElections();
        if (!allElections.isEmpty()) {
            enhancedStats.append("\n=== Election Details ===\n");

            double totalSeats = 0;
            int electionsWithCandidates = 0;

            for (int i = 0; i < allElections.size(); i++) {
                Election e = allElections.getByIndex(i);
                if (e != null) {
                    totalSeats += e.getSeatsAvailable();
                    if (e.getCandidates() != null && !e.getCandidates().isEmpty()) {
                        electionsWithCandidates++;
                    }
                }
            }

            double avgSeats = allElections.size() > 0 ? totalSeats / allElections.size() : 0;

            enhancedStats.append(String.format("Average seats per election: %.2f\n", avgSeats));
            enhancedStats.append("Elections with candidates: ").append(electionsWithCandidates)
                    .append(" out of ").append(allElections.size()).append("\n");

            enhancedStats.append("\nElections by Type:\n");
            for (ElectionType type : ElectionType.values()) {
                HashList<Election> typeElections = allElections.findAll(e ->
                        e != null && e.getType() == type);
                int count = typeElections.size();
                enhancedStats.append(String.format("  %-15s: %d\n", type, count));
            }
        }
        enhancedStats.append("\n");

        return enhancedStats.toString();
    }
}