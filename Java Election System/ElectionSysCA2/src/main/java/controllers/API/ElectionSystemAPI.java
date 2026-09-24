package controllers.API;

import models.*;
import utils.CustomList.HashList;

import java.io.*;
import java.time.LocalDate;

public class ElectionSystemAPI {
    private final HashList<Politician> politicians;
    private final HashList<Election> elections;
    private final HashList<Candidate> candidates;

    public ElectionSystemAPI() {
        this.politicians = new HashList<>(128);
        this.elections = new HashList<>(64);
        this.candidates = new HashList<>(1024);
    }

    /**
     * HASH FUNCTIONS
     */
    public int hashPolitician(String name) {
        if (name == null || name.isEmpty()) return 0;
        int hash = 7;
        String lowerName = name.toLowerCase();
        for (int i = 0; i < lowerName.length(); i++) {
            hash = hash * 31 + lowerName.charAt(i);
        }
        return Math.abs(hash);
    }

    public int hashElection(Election election) {
        if (election == null) return 0;
        String key = election.getType().toString() + ":" +
                election.getLocation().toLowerCase() + ":" +
                election.getYear();
        return hashString(key);
    }

    public int hashElectionDetails(ElectionType type, String location, int year) {
        String key = type.toString() + ":" + location.toLowerCase() + ":" + year;
        return hashString(key);
    }

    private int hashString(String str) {
        if (str == null || str.isEmpty()) return 0;
        int hash = 7;
        for (int i = 0; i < str.length(); i++) {
            hash = hash * 31 + str.charAt(i);
        }
        return Math.abs(hash);
    }

    public int hashCandidate(Politician p, Election e) {
        if (p == null || e == null) return 0;
        return Math.abs(hashPolitician(p.getName()) ^ hashElection(e));
    }

    /**
     * CRUD OPERATIONS
     */

    public boolean addPolitician(Politician politician) {
        if (politician == null || politician.getName() == null) return false;

        String name = politician.getName().trim();
        if (name.isEmpty()) return false;

        int hash = hashPolitician(name);

        // check if politician already exists
        Politician existing = politicians.get(hash, p ->
                p != null && p.getName().equalsIgnoreCase(name));
        if (existing != null) return false;

        politicians.add(hash, politician);
        return true;
    }

    public boolean addElection(Election election) {
        if (election == null || election.getLocation() == null) return false;

        Election existing = findElectionByDetails(
                election.getType(),
                election.getLocation(),
                election.getYear()
        );

        if (existing != null) return false;

        int hash = hashElection(election);
        elections.add(hash, election);
        return true;
    }

    public boolean addCandidate(Candidate candidate) {
        if (candidate == null || candidate.getPolitician() == null || candidate.getElection() == null) {
            return false;
        }

        Politician pol = candidate.getPolitician();
        Election elec = candidate.getElection();

        Politician existingPol = findPoliticianByName(pol.getName());
        if (existingPol == null) {
            return false;
        }

        Election existingElec = findElectionById(elec.getId());
        if (existingElec == null) {
            return false;
        }

        int hash = hashCandidate(existingPol, existingElec);

        Candidate existing = candidates.get(hash, c ->
                c != null && c.getPolitician().getId() == existingPol.getId() &&
                        c.getElection().getId() == existingElec.getId());

        if (existing != null) {
            existing.setPartyAffiliation(candidate.getPartyAffiliation());
            existing.setVotesReceived(candidate.getVotesReceived());
            existing.setWinner(candidate.isWinner());
            return true;
        }

        candidate.setPolitician(existingPol);
        candidate.setElection(existingElec);
        //use hash in future
        candidates.add(hash, candidate);

        if (!existingPol.getElectionsParticipated().contains(candidate)) {
            existingPol.getElectionsParticipated().addAtLast(candidate);
        }

        if (!existingElec.getCandidates().contains(candidate)) {
            existingElec.getCandidates().addAtLast(candidate);
            existingElec.setCandidateCount(existingElec.getCandidates().size());
        }

        return true;
    }

    public boolean removePolitician(Politician p) {
        if (p == null) return false;

        String name = p.getName();
        if (name == null) return false;

        int hash = hashPolitician(name);

        Politician toRemove = politicians.get(hash, pol ->
                pol != null && pol.getName().equalsIgnoreCase(name));
        if (toRemove == null) return false;

        HashList<Candidate> candidatesToRemove = new HashList<>();

        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.getByIndex(i);
            if (c != null && c.getPolitician() != null &&
                    c.getPolitician().getId() == toRemove.getId()) {
                candidatesToRemove.add(hashCandidate(c.getPolitician(), c.getElection()), c);
            }
        }

        for (int i = 0; i < candidatesToRemove.size(); i++) {
            Candidate c = candidatesToRemove.getByIndex(i);
            if (c != null) {
                removeCandidate(c);
            }
        }


        boolean removed = politicians.removeByKey(hash);

        return removed;
    }

    public boolean removeElection(Election e) {
        if (e == null) return false;

        int hash = hashElection(e);

        Election toRemove = elections.get(hash, elec ->
                elec != null && elec.getId() == e.getId());
        if (toRemove == null) return false;

        HashList<Candidate> candidatesToRemove = new HashList<>();

        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.getByIndex(i);
            if (c != null && c.getElection() != null &&
                    c.getElection().getId() == toRemove.getId()) {
                candidatesToRemove.add(hashCandidate(c.getPolitician(), c.getElection()), c);
            }
        }

        for (int i = 0; i < candidatesToRemove.size(); i++) {
            Candidate c = candidatesToRemove.getByIndex(i);
            if (c != null) {
                removeCandidate(c);
            }
        }

        boolean removed = elections.removeByKey(hash);

        return removed;
    }

    public boolean removeCandidate(Candidate c) {
        if (c == null) return false;

        Politician pol = c.getPolitician();
        Election elec = c.getElection();

        if (pol == null || elec == null) return false;

        int hash = hashCandidate(pol, elec);

        Candidate toRemove = candidates.get(hash, cand ->
                cand != null && cand.getPolitician().getId() == pol.getId() &&
                        cand.getElection().getId() == elec.getId());

        if (toRemove == null) return false;


        if (pol.getElectionsParticipated().contains(toRemove)) {
            pol.getElectionsParticipated().removeFirstHit(toRemove);
        }


        if (elec.getCandidates().contains(toRemove)) {
            elec.getCandidates().removeFirstHit(toRemove);
            elec.setCandidateCount(elec.getCandidates().size());
        }


        boolean removed = candidates.removeByKey(hash);

        return removed;
    }

    /**
     * SEARCH METHODS
     */

    public Politician findPoliticianByName(String name) {
        if (name == null || name.trim().isEmpty()) return null;
        int hash = hashPolitician(name);
        return politicians.get(hash, p ->
                p != null && p.getName().equalsIgnoreCase(name.trim()));
    }

    public Election findElectionById(int id) {
        return elections.find(e -> e != null && e.getId() == id);
    }

    public Election findElectionByDetails(ElectionType type, String location, int year) {
        int hash = hashElectionDetails(type, location, year);
        return elections.get(hash, e ->
                e != null && e.getType() == type &&
                        e.getLocation().equalsIgnoreCase(location.trim()) &&
                        e.getYear() == year);
    }

    public Candidate findCandidate(Politician p, Election e) {
        if (p == null || e == null) return null;
        int hash = hashCandidate(p, e);
        return candidates.get(hash, c ->
                c != null && c.getPolitician().getId() == p.getId() &&
                        c.getElection().getId() == e.getId());
    }

    /**
     * SEARCH COLLECTIONS METHODS
     */

    public HashList<Politician> searchPoliticiansByName(String partialName) {
        if (partialName == null || partialName.trim().isEmpty())
            return new HashList<>();

        String search = partialName.toLowerCase();
        return politicians.findAll(p ->
                p != null && p.getName().toLowerCase().contains(search));
    }

    public HashList<Politician> searchPoliticiansByParty(String party) {
        if (party == null || party.trim().isEmpty())
            return new HashList<>();

        String search = party.toLowerCase();
        return politicians.findAll(p ->
                p != null && p.getCurrentParty().toLowerCase().contains(search));
    }

    public HashList<Politician> searchPoliticiansByCounty(String county) {
        if (county == null || county.trim().isEmpty())
            return new HashList<>();

        String search = county.toLowerCase();
        return politicians.findAll(p ->
                p != null && p.getHomeCounty().toLowerCase().contains(search));
    }

    public HashList<Election> searchElectionsByType(ElectionType type) {
        if (type == null) return new HashList<>();
        return elections.findAll(e -> e != null && e.getType() == type);
    }

    public HashList<Election> searchElectionsByYear(int year) {
        return elections.findAll(e -> e != null && e.getYear() == year);
    }

    public HashList<Election> searchElectionsByLocation(String location) {
        if (location == null || location.trim().isEmpty())
            return new HashList<>();

        String search = location.toLowerCase();
        return elections.findAll(e ->
                e != null && e.getLocation().toLowerCase().contains(search));
    }

    public HashList<Candidate> searchCandidatesByParty(String party) {
        if (party == null || party.trim().isEmpty()) return new HashList<>();
        String search = party.toLowerCase();
        return candidates.findAll(c ->
                c != null && c.getPartyAffiliation().toLowerCase().contains(search));
    }

    public HashList<Candidate> searchCandidatesByElection(Election election) {
        if (election == null) return new HashList<>();
        return candidates.findAll(c ->
                c != null && c.getElection() != null &&
                        c.getElection().getId() == election.getId());
    }

    public HashList<Candidate> searchWinningCandidates() {
        return candidates.findAll(c -> c != null && c.isWinner());
    }

    /**
     * GET ALL METHODS
     */

    public HashList<Politician> getAllPoliticians() {
        return politicians;
    }

    public HashList<Election> getAllElections() {
        return elections;
    }

    public HashList<Candidate> getAllCandidates() {
        return candidates;
    }

    /**
     * COUNT METHODS
     */

    public int getPoliticianCount() {
        return politicians.size();
    }

    public int getElectionCount() {
        return elections.size();
    }

    public int getCandidateCount() {
        return candidates.size();
    }

    public boolean isSystemEmpty() {
        return politicians.isEmpty() && elections.isEmpty() && candidates.isEmpty();
    }

    /**
     * SORTING METHODS
     */

    public HashList<Politician> getSortedPoliticiansByName(boolean ascending) {
        return politicians.getSortedValues((p1, p2) -> {
            if (p1 == null && p2 == null) return 0;
            if (p1 == null) return -1;
            if (p2 == null) return 1;
            return p1.getName().compareToIgnoreCase(p2.getName());
        }, ascending);
    }

    public HashList<Politician> getSortedPoliticiansByParty(boolean ascending) {
        return politicians.getSortedValues((p1, p2) -> {
            if (p1 == null && p2 == null) return 0;
            if (p1 == null) return -1;
            if (p2 == null) return 1;
            int compare = p1.getCurrentParty().compareToIgnoreCase(p2.getCurrentParty());
            if (compare == 0) compare = p1.getName().compareToIgnoreCase(p2.getName());
            return compare;
        }, ascending);
    }

    public HashList<Election> getSortedElectionsByDate(boolean ascending) {
        return elections.getSortedValues((e1, e2) -> {
            if (e1 == null && e2 == null) return 0;
            if (e1 == null) return -1;
            if (e2 == null) return 1;
            return e1.getDate().compareTo(e2.getDate());
        }, ascending);
    }

    public HashList<Candidate> getSortedCandidatesByVotes(boolean descending) {
        return candidates.getSortedValues((c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return -1;
            if (c2 == null) return 1;
            int compare = Integer.compare(c1.getVotesReceived(), c2.getVotesReceived());
            return descending ? -compare : compare;
        }, true);
    }


    /**
     * PERSISTENCE METHODS
     */

    public boolean saveData(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("# Election System Data - Simple Format");

            writer.println("# POLITICIANS: id,name,dob,party,county,image");
            HashList<Politician> politicians = getAllPoliticians();
            for (int i = 0; i < politicians.size(); i++) {
                Politician p = politicians.getByIndex(i);
                if (p != null) {
                    writer.println(String.format("P,%d,%s,%s,%s,%s,%s",
                            p.getId(),
                            escapeCSV(p.getName()),
                            p.getDateOfBirth(),
                            escapeCSV(p.getCurrentParty()),
                            escapeCSV(p.getHomeCounty()),
                            escapeCSV(p.getImageUrl() != null ? p.getImageUrl() : "")));
                }
            }

            writer.println("# ELECTIONS: id,type,location,date,seats");
            HashList<Election> elections = getAllElections();
            for (int i = 0; i < elections.size(); i++) {
                Election e = elections.getByIndex(i);
                if (e != null) {
                    writer.println(String.format("E,%d,%s,%s,%s,%d",
                            e.getId(),
                            e.getType(),
                            escapeCSV(e.getLocation()),
                            e.getDate(),
                            e.getSeatsAvailable()));
                }
            }

            writer.println("# CANDIDATES: politicianId,electionId,party,votes,winner");
            HashList<Candidate> candidates = getAllCandidates();
            for (int i = 0; i < candidates.size(); i++) {
                Candidate c = candidates.getByIndex(i);
                if (c != null && c.getPolitician() != null && c.getElection() != null) {
                    writer.println(String.format("C,%d,%d,%s,%d,%s",
                            c.getPolitician().getId(),
                            c.getElection().getId(),
                            escapeCSV(c.getPartyAffiliation()),
                            c.getVotesReceived(),
                            c.isWinner() ? "1" : "0"));
                }
            }
            System.out.println("Saved " + politicians.size() + " politicians, " +
                    elections.size() + " elections, " +
                    candidates.size() + " candidates");
            return true;

        } catch (IOException e) {
            System.err.println("ERROR saving data: " + e.getMessage());
            return false;
        }
    }

    private String escapeCSV(String text) {
        if (text == null) return "";
        text = text.replace("\"", "\"\"");
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text + "\"";
        }
        return text;
    }

    public boolean loadData(String filename) {
        System.out.println("[ElectionSystemAPI] Loading data from: " + filename);

        try {
            clearAllData();

            File file = new File(filename);
            if (!file.exists()) {
                System.out.println("File not found: " + filename);
                return false;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            HashList<CandidateData> candidateDataList = new HashList<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 2) continue;

                String type = parts[0].trim();

                if (type.equals("P")) {
                    loadPolitician(parts);
                } else if (type.equals("E")) {
                    loadElection(parts);
                } else if (type.equals("C")) {
                    storeCandidateData(parts, candidateDataList);
                }
            }
            reader.close();

            System.out.println("Creating " + candidateDataList.size() + " candidates...");
            int successCount = 0;

            for (int i = 0; i < candidateDataList.size(); i++) {
                CandidateData data = candidateDataList.getByIndex(i);
                if (data != null) {
                    if (createCandidateFromData(data)) {
                        successCount++;
                    }
                }
            }

            System.out.println("Load complete: " + getPoliticianCount() + " politicians, " +
                    getElectionCount() + " elections, " +
                    getCandidateCount() + " candidates created");

            return true;

        } catch (Exception e) {
            System.err.println("ERROR loading data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static class CandidateData {
        int politicianId;
        int electionId;
        String party;
        int votes;
        boolean winner;

        CandidateData(int politicianId, int electionId, String party, int votes, boolean winner) {
            this.politicianId = politicianId;
            this.electionId = electionId;
            this.party = party;
            this.votes = votes;
            this.winner = winner;
        }
    }

    private void loadPolitician(String[] parts) {
        try {
            if (parts.length >= 6) {
                int id = Integer.parseInt(parts[1].trim());
                String name = unescapeCSV(parts[2].trim());
                LocalDate dob = LocalDate.parse(parts[3].trim());
                String party = unescapeCSV(parts[4].trim());
                String county = unescapeCSV(parts[5].trim());


                String image = "";
                if (parts.length > 6) {
                    image = unescapeCSV(parts[6].trim());

                    if (image != null && !image.isEmpty()) {
                        if (image.startsWith("file:")) {
                            String filePath = image.substring(5);
                            File imageFile = new File(filePath);
                            if (!imageFile.exists()) {
                                System.err.println("WARNING: Image file not found: " + filePath);
                                image = "";
                            }
                        }
                    }
                }

                Politician politician = new Politician(name, dob, party, county, image);

                try {
                    java.lang.reflect.Field idField = Politician.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(politician, id);


                    if (id >= Politician.idIncrement) {
                        java.lang.reflect.Field incrementField = Politician.class.getDeclaredField("idIncrement");
                        incrementField.setAccessible(true);
                        incrementField.set(null, id + 1);
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Could not set politician ID: " + e.getMessage());
                }


                boolean added = addPolitician(politician);
                if (!added) {
                    System.err.println("Failed to add politician: " + name);
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR loading politician: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void loadElection(String[] parts) {
        try {
            if (parts.length >= 6) {
                int id = Integer.parseInt(parts[1].trim());
                ElectionType electionType = ElectionType.valueOf(parts[2].trim().toUpperCase());
                String location = unescapeCSV(parts[3].trim());
                LocalDate date = LocalDate.parse(parts[4].trim());
                int seats = Integer.parseInt(parts[5].trim());

                Election election = new Election(electionType, location, date, seats);

                try {
                    java.lang.reflect.Field idField = Election.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(election, id);
                } catch (Exception e) {

                }

                addElection(election);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void storeCandidateData(String[] parts, HashList<CandidateData> candidateDataList) {
        try {
            if (parts.length >= 6) {
                int politicianId = Integer.parseInt(parts[1].trim());
                int electionId = Integer.parseInt(parts[2].trim());
                String party = unescapeCSV(parts[3].trim());
                int votes = Integer.parseInt(parts[4].trim());
                boolean winner = parts[5].trim().equals("1");

                candidateDataList.addAtLast(
                        new CandidateData(politicianId, electionId, party, votes, winner)
                );
            }
        } catch (Exception e) {
            System.err.println("ERROR storing candidate data: " + e.getMessage());
        }
    }

    private boolean createCandidateFromData(CandidateData data) {
        try {
            Politician politician = null;
            HashList<Politician> politicians = getAllPoliticians();
            for (int i = 0; i < politicians.size(); i++) {
                Politician p = politicians.getByIndex(i);
                if (p != null && p.getId() == data.politicianId) {
                    politician = p;
                    break;
                }
            }

            Election election = null;
            HashList<Election> elections = getAllElections();
            for (int i = 0; i < elections.size(); i++) {
                Election e = elections.getByIndex(i);
                if (e != null && e.getId() == data.electionId) {
                    election = e;
                    break;
                }
            }

            if (politician == null) {
                System.err.println("ERROR: Politician with ID " + data.politicianId + " not found");
                return false;
            }

            if (election == null) {
                System.err.println("ERROR: Election with ID " + data.electionId + " not found");
                return false;
            }

            Candidate existing = findCandidate(politician, election);
            if (existing != null) {
                System.out.println("Candidate already exists, updating...");
                existing.setPartyAffiliation(data.party);
                existing.setVotesReceived(data.votes);
                existing.setWinner(data.winner);
                return true;
            }

            Candidate candidate = new Candidate(politician, election, data.party, data.votes);
            candidate.setWinner(data.winner);

            return addCandidate(candidate);

        } catch (Exception e) {
            System.err.println("ERROR creating candidate: " + e.getMessage());
            return false;
        }
    }

    private String unescapeCSV(String text) {
        if (text == null || text.isEmpty()) return "";

        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
            text = text.replace("\"\"", "\"");
        }

        return text;
    }

    /**
     * SYSTEM MANAGEMENT
     */

    public void clearAllData() {
        politicians.clear();
        elections.clear();
        candidates.clear();
    }

    public String getSystemStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== System Statistics ===\n\n")
                .append("Politicians: ").append(getPoliticianCount()).append("\n")
                .append("Elections: ").append(getElectionCount()).append("\n")
                .append("Candidates: ").append(getCandidateCount()).append("\n\n");
        return stats.toString();
    }

    /**
     * WINNER DETERMINATION
     */

    public void determineElectionWinners(Election election) {
        if (election == null ) return;

        HashList<Candidate> electionCandidates = election.getCandidates();
        if (electionCandidates == null || electionCandidates.size() == 0) {
            return;
        }


        for (int i = 0; i < electionCandidates.size(); i++) {
            Candidate c = electionCandidates.getByIndex(i);
            if (c != null) {
                c.setWinner(false);
            }
        }

        HashList<Candidate> sorted = electionCandidates.getSortedValues((c1, c2) -> {
            if (c1 == null && c2 == null) return 0;
            if (c1 == null) return 1;
            if (c2 == null) return -1;
            return Integer.compare(c2.getVotesReceived(), c1.getVotesReceived());
        }, false);

        int seats = election.getSeatsAvailable();
        int winnersToSelect = Math.min(seats, sorted.size());

        if (winnersToSelect > 0) {
            Candidate lastWinner = sorted.getByIndex(winnersToSelect - 1);
            int minWinningVotes = (lastWinner != null) ? lastWinner.getVotesReceived() : 0;

            for (int i = 0; i < sorted.size(); i++) {
                Candidate c = sorted.getByIndex(i);
                if (c != null) {
                    if (i < winnersToSelect || c.getVotesReceived() == minWinningVotes) {
                        c.setWinner(true);
                    }
                }
            }
        }
    }

    public void determineAllWinners() {
        for (int i = 0; i < elections.size(); i++) {
            Election e = elections.getByIndex(i);
            if (e != null) {
                determineElectionWinners(e);
            }
        }
    }

    /**
     * HASH TABLE STATISTICS (for debugging)
     */

    public String getHashStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Hash Table Statistics ===\n\n");

        stats.append("POLITICIANS:\n");
        stats.append(politicians.getHashStatistics()).append("\n");

        stats.append("ELECTIONS:\n");
        stats.append(elections.getHashStatistics()).append("\n");

        stats.append("CANDIDATES:\n");
        stats.append(candidates.getHashStatistics()).append("\n");

        return stats.toString();
    }
}