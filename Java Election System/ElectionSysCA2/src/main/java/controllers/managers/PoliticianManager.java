package controllers.managers;

import controllers.API.ElectionSystemAPI;
import models.Candidate;
import models.Election;
import models.Politician;
import utils.CustomList.HashList;
import utils.Validators.InputVal;

import java.time.LocalDate;

public class PoliticianManager {
    private final ElectionSystemAPI system;
    private final ElectionManager electionManager;

    public PoliticianManager(ElectionSystemAPI system, ElectionManager electionManager) {
        this.system = system;
        this.electionManager = electionManager;
    }

    public boolean addPolitician(String name, LocalDate dob, String party, String county, String imageUrl) {
        if (!InputVal.validStringlength(name, 100)) {
            System.out.println("Invalid name. Must be non-empty and <= 100 characters.");
            return false;
        }
        if (dob == null) {
            System.out.println("Date of birth cannot be null.");
            return false;
        }
        if (!InputVal.validStringlength(party, 50)) {
            System.out.println("Invalid party. Must be non-empty and <= 50 characters.");
            return false;
        }
        if (!InputVal.validStringlength(county, 50)) {
            System.out.println("Invalid county. Must be non-empty and <= 50 characters.");
            return false;
        }

        if (system.findPoliticianByName(name) != null) {
            System.out.println("Politician with name '" + name + "' already exists.");
            return false;
        }

        Politician politician = new Politician(name, dob, party, county, imageUrl);
        boolean added = system.addPolitician(politician);

        if (added) {
            System.out.println("Added politician: " + name);
        } else {
            System.out.println("Failed to add politician: " + name);
        }

        return added;
    }

    public boolean updatePoliticianName(String currentName, String newName) {
        if (!InputVal.validStringlength(currentName, 100) || !InputVal.validStringlength(newName, 100)) {
            System.out.println("Invalid name(s). Must be non-empty and <= 100 characters.");
            return false;
        }

        Politician politician = findPolitician(currentName);
        if (politician == null) {
            System.out.println("Politician not found: " + currentName);
            return false;
        }

        if (!currentName.equalsIgnoreCase(newName) && system.findPoliticianByName(newName) != null) {
            System.out.println("Another politician with name '" + newName + "' already exists.");
            return false;
        }

        String oldName = politician.getName();
        politician.setName(newName);

        System.out.println("Updated politician name from '" + oldName + "' to '" + newName + "'");
        return true;
    }

    public boolean updatePoliticianParty(String name, String newParty) {
        if (!InputVal.validStringlength(name, 100) || !InputVal.validStringlength(newParty, 50)) {
            System.out.println("Invalid input. Name <= 100 chars, party <= 50 chars.");
            return false;
        }

        Politician politician = findPolitician(name);
        if (politician == null) {
            System.out.println("Politician not found: " + name);
            return false;
        }

        politician.setCurrentParty(newParty);
        System.out.println("Updated party for " + name + " to " + newParty);
        return true;
    }

    public boolean updatePoliticianCounty(String name, String newCounty) {
        if (!InputVal.validStringlength(name, 100) || !InputVal.validStringlength(newCounty, 50)) {
            System.out.println("Invalid input. Name <= 100 chars, county <= 50 chars.");
            return false;
        }

        Politician politician = findPolitician(name);
        if (politician == null) {
            System.out.println("Politician not found: " + name);
            return false;
        }

        politician.setHomeCounty(newCounty);
        System.out.println("Updated county for " + name + " to " + newCounty);
        return true;
    }

    public boolean updatePoliticianDOB(String name, LocalDate newDob) {
        if (!InputVal.validStringlength(name, 100) || newDob == null) {
            System.out.println("Invalid input. Name required, date cannot be null.");
            return false;
        }

        Politician politician = findPolitician(name);
        if (politician == null) {
            System.out.println("Politician not found: " + name);
            return false;
        }

        politician.setDateOfBirth(newDob);
        System.out.println("Updated date of birth for " + name + " to " + newDob);
        return true;
    }

    public boolean updatePoliticianURL(String name, String newImageUrl) {
        if (!InputVal.validStringlength(name, 100)) {
            System.out.println("Invalid name. Must be non-empty and <= 100 characters.");
            return false;
        }

        Politician politician = findPolitician(name);
        if (politician == null) {
            System.out.println("Politician not found: " + name);
            return false;
        }

        politician.setImageUrl(newImageUrl);
        System.out.println("Updated image URL for " + name);
        return true;
    }

    public boolean removePolitician(String name) {
        if (!InputVal.validStringlength(name, 100)) {
            System.out.println("Invalid name. Must be non-empty and <= 100 characters.");
            return false;
        }

        Politician politician = findPolitician(name);
        if (politician == null) {
            System.out.println("Politician not found: " + name);
            return false;
        }

        removeAllCandidateAssociations(politician);

        boolean removed = system.removePolitician(politician);

        if (removed) {
            System.out.println("Removed politician: " + name);
        } else {
            System.out.println("Failed to remove politician: " + name);
        }

        return removed;
    }

    private void removeAllCandidateAssociations(Politician politician) {
        if (politician == null) return;

        HashList<Candidate> participations = politician.getElectionsParticipated();

        for (int i = 0; i < participations.size(); i++) {
            Candidate candidate = participations.getByIndex(i);
            if (candidate != null && candidate.getElection() != null) {
                electionManager.removeCandidateFromElection(politician, candidate.getElection());
            }
        }
    }

    public Politician findPolitician(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return null;
        }
        return system.findPoliticianByName(name);
    }



    public HashList<Politician> searchPoliticians(String name, String party, String county) {
        HashList<Politician> results = new HashList<>();
        if ((name == null || name.trim().isEmpty()) &&
                (party == null || party.trim().isEmpty()) &&
                (county == null || county.trim().isEmpty())) {
            return results;
        }

        HashList<Politician> allPoliticians = system.getAllPoliticians();

        for (int i = 0; i < allPoliticians.size(); i++) {
            Politician p = allPoliticians.getByIndex(i);
            if (p != null) {
                boolean matches = true;

                if (name != null && !name.trim().isEmpty() &&
                        !p.getName().toLowerCase().contains(name.toLowerCase())) {
                    matches = false;
                }

                if (matches && party != null && !party.trim().isEmpty() &&
                        !p.getCurrentParty().toLowerCase().contains(party.toLowerCase())) {
                    matches = false;
                }

                if (matches && county != null && !county.trim().isEmpty() &&
                        !p.getHomeCounty().toLowerCase().contains(county.toLowerCase())) {
                    matches = false;
                }

                if (matches) {
                    results.add(p.hashCode(), p);
                }
            }
        }

        return results;
    }

    public String listAllPoliticians() {
        return formatPoliticianList(system.getAllPoliticians(),
                "All Politicians:", "name", true);
    }


    private String formatPoliticianList(HashList<Politician> politicians, String header,
                                        String sortBy, boolean ascending) {
        if (politicians.isEmpty()) {
            return header + "\nNo politicians found.\n";
        }

        switch (sortBy.toLowerCase()) {
            case "party":
                sortPoliticiansByParty(politicians, ascending);
                break;
            case "county":
                sortPoliticiansByCounty(politicians, ascending);
                break;
            default:
                sortPoliticiansByName(politicians, ascending);
        }

        StringBuilder result = new StringBuilder();
        result.append(header).append("\n");
        result.append("=".repeat(80)).append("\n");

        for (int i = 0; i < politicians.size(); i++) {
            Politician p = politicians.getByIndex(i);
            if (p != null) {
                result.append(String.format("%3d. %-30s | %-20s | %-15s | %d elections\n",
                        i + 1, p.getName(), p.getCurrentParty(),
                        p.getHomeCounty(), p.getElectionsParticipated().size()));
            }
        }

        result.append("\nTotal: ").append(politicians.size()).append(" politicians\n");

        return result.toString();
    }

    private void sortPoliticiansByName(HashList<Politician> politicians, boolean ascending) {
        if (politicians == null || politicians.size() <= 1) return;

        politicians.selectionSort((p1, p2) -> {
            if (p1 == null && p2 == null) return 0;
            if (p1 == null) return ascending ? -1 : 1;
            if (p2 == null) return ascending ? 1 : -1;

            int compare = p1.getName().compareToIgnoreCase(p2.getName());
            return ascending ? compare : -compare;
        });
    }

    private void sortPoliticiansByParty(HashList<Politician> politicians, boolean ascending) {
        if (politicians == null || politicians.size() <= 1) return;

        politicians.selectionSort((p1, p2) -> {
            if (p1 == null && p2 == null) return 0;
            if (p1 == null) return ascending ? -1 : 1;
            if (p2 == null) return ascending ? 1 : -1;

            int compare = p1.getCurrentParty().compareToIgnoreCase(p2.getCurrentParty());
            if (compare == 0) {
                compare = p1.getName().compareToIgnoreCase(p2.getName());
            }
            return ascending ? compare : -compare;
        });
    }

    private void sortPoliticiansByCounty(HashList<Politician> politicians, boolean ascending) {
        if (politicians == null || politicians.size() <= 1) return;

        politicians.selectionSort((p1, p2) -> {
            if (p1 == null && p2 == null) return 0;
            if (p1 == null) return ascending ? -1 : 1;
            if (p2 == null) return ascending ? 1 : -1;

            int compare = p1.getHomeCounty().compareToIgnoreCase(p2.getHomeCounty());
            if (compare == 0) {
                compare = p1.getName().compareToIgnoreCase(p2.getName());
            }
            return ascending ? compare : -compare;
        });
    }

    private HashList<Candidate> sortParticipationsByYear(HashList<Candidate> participations, boolean ascending) {
        if (participations == null || participations.size() <= 1) return participations;

        participations.insertionSort((c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return ascending ? -1 : 1;
            if (c2 == null) return ascending ? 1 : -1;

            int year1 = c1.getElection() != null ? c1.getElection().getYear() : 0;
            int year2 = c2.getElection() != null ? c2.getElection().getYear() : 0;
            int compare = Integer.compare(year1, year2);
            if (compare == 0) {
                LocalDate date1 = c1.getElection() != null ? c1.getElection().getDate() : LocalDate.MIN;
                LocalDate date2 = c2.getElection() != null ? c2.getElection().getDate() : LocalDate.MIN;
                compare = date1.compareTo(date2);
            }
            return ascending ? compare : -compare;
        });

        return participations;
    }

    public String getPoliticianElectionHistory(String name) {
        if (!InputVal.validStringlength(name, 100)) {
            return "Invalid politician name.\n";
        }

        Politician politician = findPolitician(name);
        if (politician == null) {
            return "Politician not found: " + name + "\n";
        }

        HashList<Candidate> participations = politician.getElectionsParticipated();
        if (participations.isEmpty()) {
            return name + " has no election history.\n";
        }

        HashList<Candidate> sorted = sortParticipationsByYear(participations, false);

        StringBuilder result = new StringBuilder();
        result.append("Election History for ").append(name).append("\n");
        result.append("=".repeat(80)).append("\n");

        int totalVotes = 0;
        int wins = 0;
        int totalElections = participations.size();

        for (int i = 0; i < sorted.size(); i++) {
            Candidate candidate = sorted.getByIndex(i);
            if (candidate != null && candidate.getElection() != null) {
                Election election = candidate.getElection();
                String resultStr = candidate.isWinner() ? "WINNER" : "Lost";
                result.append(String.format("%2d. %-4d: %-12s in %-20s - %s (%,d votes) [%s]\n",
                        i + 1, election.getYear(), election.getType(), election.getLocation(),
                        candidate.getPartyAffiliation(), candidate.getVotesReceived(), resultStr));

                totalVotes += candidate.getVotesReceived();
                if (candidate.isWinner()) wins++;
            }
        }

        result.append("\n").append("=".repeat(80)).append("\n");
        result.append("Summary:\n");
        result.append(String.format("  Total elections contested: %d\n", totalElections));
        result.append(String.format("  Elections won: %d\n", wins));
        result.append(String.format("  Win rate: %.1f%%\n",
                totalElections > 0 ? (wins * 100.0 / totalElections) : 0));
        result.append(String.format("  Total votes received: %,d\n", totalVotes));
        if (totalElections > 0) {
            result.append(String.format("  Average votes per election: %.1f\n",
                    totalVotes / (double) totalElections));
        }

        return result.toString();
    }

    public String getPoliticianStatistics() {
        HashList<Politician> allPoliticians = system.getAllPoliticians();
        int total = allPoliticians.size();

        if (total == 0) {
            return "No politicians in the system.\n";
        }

        int withElections = 0;
        int totalParticipations = 0;
        HashList<String> parties = new HashList<>();
        HashList<String> counties = new HashList<>();

        for (int i = 0; i < total; i++) {
            Politician p = allPoliticians.getByIndex(i);
            if (p != null) {
                int participations = p.getElectionsParticipated().size();
                if (participations > 0) {
                    withElections++;
                    totalParticipations += participations;
                }

                String party = p.getCurrentParty();
                if (!parties.contains(party)) parties.addAtLast(party);

                String county = p.getHomeCounty();
                if (!counties.contains(county)) counties.addAtLast(county);
            }
        }

        sortPoliticiansByParty(allPoliticians, true);

        StringBuilder result = new StringBuilder();
        result.append("=== Politician Statistics ===\n");
        result.append("=".repeat(40)).append("\n");
        result.append("Total Politicians: ").append(total).append("\n");
        result.append("Politicians who ran in elections: ").append(withElections)
                .append(" (").append(String.format("%.1f%%", total > 0 ? (withElections * 100.0 / total) : 0)).append(")\n");
        result.append("Total election participations: ").append(totalParticipations).append("\n");
        if (withElections > 0) {
            result.append("Average participations per politician: ").append(String.format("%.1f\n",
                    totalParticipations / (double) withElections));
        }
        result.append("Number of unique parties: ").append(parties.size()).append("\n");
        result.append("Number of unique counties: ").append(counties.size()).append("\n\n");

        result.append("Party Distribution:\n");
        String currentParty = "";
        int countInParty = 0;

        for (int i = 0; i < allPoliticians.size(); i++) {
            Politician p = allPoliticians.getByIndex(i);
            if (p != null) {
                if (!p.getCurrentParty().equals(currentParty)) {
                    if (!currentParty.isEmpty()) {
                        result.append(String.format("  %-20s: %d politicians (%.1f%%)\n",
                                currentParty, countInParty, total > 0 ? (countInParty * 100.0 / total) : 0));
                    }
                    currentParty = p.getCurrentParty();
                    countInParty = 1;
                } else {
                    countInParty++;
                }
            }
        }

        if (!currentParty.isEmpty()) {
            result.append(String.format("  %-20s: %d politicians (%.1f%%)\n",
                    currentParty, countInParty, total > 0 ? (countInParty * 100.0 / total) : 0));
        }

        return result.toString();
    }
}