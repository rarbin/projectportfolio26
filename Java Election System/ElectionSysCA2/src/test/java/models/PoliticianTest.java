package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.CustomList.HashList;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PoliticianTest {

    private Politician politician;
    private Candidate candidate1;
    private Candidate candidate2;
    private Election election1;
    private Election election2;

    @BeforeEach
    void setUp() {
        politician = new Politician("Leo Varadkar",
                LocalDate.of(1979, 1, 18),
                "Fine Gael",
                "Dublin",
                "https://example.com/varadkar.jpg");

        election1 = new Election(ElectionType.GENERAL, "Ireland",
                LocalDate.of(2020, 2, 8), 160);

        election2 = new Election(ElectionType.LOCAL, "Dublin",
                LocalDate.of(2019, 5, 24), 63);

        candidate1 = new Candidate(politician, election1, "Fine Gael", 8759);
        candidate2 = new Candidate(politician, election2, "Fine Gael", 4500);

        politician.addElectionParticipation(candidate1);
        politician.addElectionParticipation(candidate2);
    }

    @AfterEach
    void tearDown() {
        politician = null;
        candidate1 = null;
        candidate2 = null;
        election1 = null;
        election2 = null;
    }

    @Test
    void constructor_ValidParameters() {
        assertEquals("Leo Varadkar", politician.getName());
        assertEquals(LocalDate.of(1979, 1, 18), politician.getDateOfBirth());
        assertEquals("Fine Gael", politician.getCurrentParty());
        assertEquals("Dublin", politician.getHomeCounty());
        assertEquals("https://example.com/varadkar.jpg", politician.getImageUrl());
        assertNotNull(politician.getElectionsParticipated());
        assertEquals(2, politician.getElectionsParticipated().getSize());
    }

    @Test
    void constructor_NullParty() {
        Politician independentPolitician = new Politician("Independent Candidate",
                LocalDate.of(1980, 1, 1),
                null,
                "Cork",
                null);

        assertEquals("Independent", independentPolitician.getCurrentParty());
    }

    @Test
    void constructor_EmptyParty() {
        Politician emptyPartyPolitician = new Politician("Empty Party",
                LocalDate.of(1980, 1, 1),
                "",
                "Cork",
                null);

        assertEquals("Independent", emptyPartyPolitician.getCurrentParty());
    }

    @Test
    void constructor_NullDateOfBirth() {
        Politician nullDobPolitician = new Politician("No DOB",
                null,
                "Fine Gael",
                "Dublin",
                null);

        assertNull(nullDobPolitician.getDateOfBirth());
    }

    @Test
    void constructor_FutureDateOfBirth() {
        Politician futureDobPolitician = new Politician("Future Born",
                LocalDate.now().plusDays(1),
                "Fine Gael",
                "Dublin",
                null);

        assertNull(futureDobPolitician.getDateOfBirth());
    }

    @Test
    void getId() {
        int id = politician.getId();
        assertTrue(id >= 1000);

        Politician anotherPolitician = new Politician("Micheál Martin",
                LocalDate.of(1960, 8, 1),
                "Fianna Fáil",
                "Cork",
                "https://example.com/martin.jpg");

        assertEquals(id + 1, anotherPolitician.getId());
    }

    @Test
    void setName() {
        politician.setName("Leo Varadkar Updated");
        assertEquals("Leo Varadkar Updated", politician.getName());
    }

    @Test
    void setName_TooLong() {
        String longName = "A".repeat(101);
        String originalName = politician.getName();

        politician.setName(longName);
        assertEquals(originalName, politician.getName());
    }

    @Test
    void setName_Null() {
        String originalName = politician.getName();
        politician.setName(null);
        assertEquals(originalName, politician.getName());
    }

    @Test
    void setDateOfBirth() {
        LocalDate newDob = LocalDate.of(1980, 6, 15);
        politician.setDateOfBirth(newDob);
        assertEquals(newDob, politician.getDateOfBirth());
    }

    @Test
    void setDateOfBirth_FutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        LocalDate originalDob = politician.getDateOfBirth();

        politician.setDateOfBirth(futureDate);
        assertEquals(originalDob, politician.getDateOfBirth());
    }

    @Test
    void setCurrentParty() {
        politician.setCurrentParty("Independent");
        assertEquals("Independent", politician.getCurrentParty());

        politician.setCurrentParty(null);
        assertEquals("Independent", politician.getCurrentParty());
    }

    @Test
    void setHomeCounty() {
        politician.setHomeCounty("Meath");
        assertEquals("Meath", politician.getHomeCounty());
    }

    @Test
    void setHomeCounty_TooLong() {
        String longCounty = "A".repeat(51); // 51 characters
        String originalCounty = politician.getHomeCounty();

        politician.setHomeCounty(longCounty);
        assertEquals(originalCounty, politician.getHomeCounty());
    }

    @Test
    void setImageUrl() {
        politician.setImageUrl("https://example.com/newphoto.jpg");
        assertEquals("https://example.com/newphoto.jpg", politician.getImageUrl());

        politician.setImageUrl(null);
        assertNull(politician.getImageUrl());
    }

    @Test
    void addElectionParticipation() {
        Election newElection = new Election(ElectionType.EUROPEAN, "Ireland",
                LocalDate.of(2024, 6, 7), 13);

        Candidate newCandidate = new Candidate(politician, newElection, "Fine Gael", 6500);

        politician.addElectionParticipation(newCandidate);
        assertEquals(3, politician.getElectionsParticipated().getSize());
        assertTrue(politician.getElectionsParticipated().contains(newCandidate));
    }

    @Test
    void addElectionParticipation_Null() {
        int initialSize = politician.getElectionsParticipated().getSize();
        politician.addElectionParticipation(null);
        assertEquals(initialSize, politician.getElectionsParticipated().getSize());
    }

    @Test
    void addElectionParticipation_Duplicate() {
        int initialSize = politician.getElectionsParticipated().getSize();
        politician.addElectionParticipation(candidate1);
        assertEquals(initialSize, politician.getElectionsParticipated().getSize());
    }

    @Test
    void removeElectionParticipation() {
        assertTrue(politician.getElectionsParticipated().contains(candidate1));
        politician.removeElectionParticipation(candidate1);
        assertFalse(politician.getElectionsParticipated().contains(candidate1));
        assertEquals(1, politician.getElectionsParticipated().getSize());
    }

    @Test
    void removeElectionParticipation_Null() {
        int initialSize = politician.getElectionsParticipated().getSize();
        politician.removeElectionParticipation(null);
        assertEquals(initialSize, politician.getElectionsParticipated().getSize());
    }

    @Test
    void removeElectionParticipation_NotInList() {
        Election newElection = new Election(ElectionType.EUROPEAN, "Ireland",
                LocalDate.of(2024, 6, 7), 13);

        Candidate nonExistentCandidate = new Candidate(politician, newElection, "Fine Gael", 6500);

        int initialSize = politician.getElectionsParticipated().getSize();
        politician.removeElectionParticipation(nonExistentCandidate);
        assertEquals(initialSize, politician.getElectionsParticipated().getSize());
    }

    @Test
    void getElectionsParticipated() {
        HashList<Candidate> participations = politician.getElectionsParticipated();
        assertNotNull(participations);
        assertEquals(2, participations.getSize());
        assertTrue(participations.contains(candidate1));
        assertTrue(participations.contains(candidate2));
    }

    @Test
    void testToString() {
        String result = politician.toString();
        assertTrue(result.contains("Leo Varadkar"));
        assertTrue(result.contains("Fine Gael"));
        assertTrue(result.contains("Dublin"));
    }

    @Test
    void testToString_Independent() {
        Politician independentPolitician = new Politician("Independent Candidate",
                LocalDate.of(1980, 1, 1),
                null,
                "Cork",
                null);

        String result = independentPolitician.toString();
        assertTrue(result.contains("Independent"));
        assertTrue(result.contains("Cork"));
    }

    @Test
    void testPoliticianWithNoElections() {
        Politician newPolitician = new Politician("New Politician",
                LocalDate.of(1990, 1, 1),
                "Green Party",
                "Galway",
                null);

        assertTrue(newPolitician.getElectionsParticipated().isEmpty());
    }

}