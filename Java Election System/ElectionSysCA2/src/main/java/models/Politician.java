package models;

import utils.CustomList.HashList;
import java.io.Serializable;
import java.time.LocalDate;
import utils.Validators.InputVal;

public class Politician implements Serializable {
    private static final long serialVersionUID = 1L;
    public static int idIncrement = 1000;
    private final int id;
    private String name;
    private LocalDate dateOfBirth;
    private String currentParty;
    private String homeCounty;
    private String imageUrl;
    private HashList<Candidate> electionsParticipated;

    public Politician(String name, LocalDate dateOfBirth, String currentParty, String homeCounty, String imageUrl) {
        this.id = idIncrement++;
        setName(name);
        setDateOfBirth(dateOfBirth);
        setCurrentParty(currentParty);
        setHomeCounty(homeCounty);
        setImageUrl(imageUrl);
        this.electionsParticipated = new HashList<>();
    }

    /**
     * GETTERS
     */
    public int getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getCurrentParty() { return currentParty; }
    public String getHomeCounty() { return homeCounty; }
    public String getImageUrl() { return imageUrl; }
    public HashList<Candidate> getElectionsParticipated() { return electionsParticipated; }

    /**
     * SETTERS
     */
    public void setName(String name) {
        if (InputVal.validStringlength(name, 100)) {
            this.name = name;
        }
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth != null && dateOfBirth.isBefore(LocalDate.now())) {
            this.dateOfBirth = dateOfBirth;
        }
    }

    public void setCurrentParty(String currentParty) {
        if (currentParty == null || currentParty.trim().isEmpty()) {
            this.currentParty = "Independent";
        } else {
            this.currentParty = currentParty;
        }
    }

    public void setHomeCounty(String homeCounty) {
        if (InputVal.validStringlength(homeCounty, 50)) {
            this.homeCounty = homeCounty;
        }
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setElectionsParticipated(HashList<Candidate> electionsParticipated) {
        this.electionsParticipated = electionsParticipated != null ? electionsParticipated : new HashList<>();
    }

    public void addElectionParticipation(Candidate candidate) {
        if (candidate != null && !electionsParticipated.contains(candidate)) {
            electionsParticipated.addAtLast(candidate);
        }
    }

    public void removeElectionParticipation(Candidate candidate) {
        if (candidate != null) {
            electionsParticipated.removeFirstHit(candidate);
        }
    }

    @Override
    public String toString() {
        return "Politician: " + name + " (" + currentParty + ") from " + homeCounty;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Politician that = (Politician) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}