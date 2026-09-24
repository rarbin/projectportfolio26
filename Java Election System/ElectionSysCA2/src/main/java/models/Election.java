package models;

import utils.CustomList.HashList;
import java.io.Serializable;
import java.time.LocalDate;

public class Election implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int idIncrement = 2000;
    private final int id;
    private ElectionType type;
    private String location;
    private LocalDate date;
    private int seatsAvailable;
    private HashList<Candidate> candidates;
    private int candidateCount;

    public Election(ElectionType type, String location, LocalDate date, int seatsAvailable) {
        this.id = idIncrement++;
        setType(type);
        setLocation(location);
        setDate(date);
        setSeatsAvailable(seatsAvailable);
        this.candidates = new HashList<>();
        this.candidateCount = 0;
    }
    public int getId() { return id; }
    public ElectionType getType() { return type; }
    public String getLocation() { return location; }
    public LocalDate getDate() { return date; }
    public int getYear() { return date != null ? date.getYear() : 0; }
    public int getSeatsAvailable() { return seatsAvailable; }
    public HashList<Candidate> getCandidates() {
        if (candidates == null) {
            candidates = new HashList<>();
        }
        return candidates;
    }
    public void setCandidateCount(int count) {
        // This method ensures the candidates list has the specified number of elements
        // It doesn't create actual Candidate objects, just adjusts the list size

        if (candidates == null) {
            candidates = new HashList<>();
        }

        while (candidates.size() < count) {
            candidates.addAtLast(null);
        }

        while (candidates.size() > count) {
            candidates.removeLast();
        }
    }


    public boolean addCandidate(Candidate candidate, boolean setElectionOnCandidate) {
        if (candidate == null) return false;
        if (candidates.contains(candidate)) {
            return false;
        }

        if (candidate.getPolitician() != null) {
            for (int i = 0; i < candidates.size(); i++) {
                Candidate c = candidates.getByIndex(i);
                if (c != null && c.getPolitician() != null && candidate.getPolitician() != null &&
                        c.getPolitician().getId() == candidate.getPolitician().getId()) {
                    return false;
                }
            }
        }
        candidates.addAtLast(candidate);
        candidateCount = candidates.size();
        if (setElectionOnCandidate && candidate.getElection() != this) {
            candidate.setElection(this, false);
        }

        return true;
    }

    public void setCandidates(HashList<Candidate> candidates) {
        this.candidates = candidates != null ? candidates : new HashList<>();
    }

    public void setType(ElectionType type) {
        this.type = type != null ? type : ElectionType.GENERAL;
    }

    public void setLocation(String location) {
        this.location = location != null ? location.trim() : "Unknown";
    }

    public void setDate(LocalDate date) {
        this.date = date != null ? date : LocalDate.now();
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = Math.max(1, seatsAvailable);
    }

    public boolean addCandidate(Candidate candidate) {
        if (candidate == null) return false;
        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.getByIndex(i);
            if (c != null && c.getPolitician() != null && candidate.getPolitician() != null &&
                    c.getPolitician().getId() == candidate.getPolitician().getId()) {
                return false;
            }
        }
        candidate.setElection(this);
        candidates.addAtLast(candidate);
        return true;
    }

    public boolean removeCandidate(Candidate candidate) {
        if (candidate == null) return false;
        return candidates.removeFirstHit(candidate);
    }

    public int getCandidateCount() {
        return candidates.size();
    }

    @Override
    public String toString() {
        return type + " Election in " + location + " (" + date.getYear() + ")";
    }

    @Override
    public int hashCode() {
        return id;
    }
}