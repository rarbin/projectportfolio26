
package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.CustomList.HashList;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ElectionTest {

    private Election election;
    private Candidate candidate1;
    private Candidate candidate2;
    private Politician politician1;
    private Politician politician2;

    @BeforeEach
    void setUp() {
        politician1 = new Politician("John Doe",
                LocalDate.of(1975, 5, 15),
                "Fine Gael",
                "Dublin",
                "https://example.com/johndoe.jpg");

        politician2 = new Politician("Jane Smith",
                LocalDate.of(1980, 8, 22),
                "Fianna Fáil",
                "Cork",
                "https://example.com/janesmith.jpg");

        election = new Election(ElectionType.GENERAL, "Ireland",
                LocalDate.of(2020, 2, 8), 160);

        candidate1 = new Candidate(politician1, election, "Fine Gael", 8759);
        candidate2 = new Candidate(politician2, election, "Fianna Fáil", 9312);

        election.addCandidate(candidate1);
        election.addCandidate(candidate2);
    }

    @AfterEach
    void tearDown() {
        election = null;
        candidate1 = null;
        candidate2 = null;
        politician1 = null;
        politician2 = null;
    }

    @Test
    void constructor_ValidParameters() {
        assertEquals(ElectionType.GENERAL, election.getType());
        assertEquals("Ireland", election.getLocation());
        assertEquals(LocalDate.of(2020, 2, 8), election.getDate());
        assertEquals(2020, election.getYear());
        assertEquals(160, election.getSeatsAvailable());
        assertNotNull(election.getCandidates());
    }


    @Test
    void constructor_InvalidSeats() {
        Election electionWithZeroSeats = new Election(ElectionType.GENERAL, "Ireland", LocalDate.now(), 0);
        assertEquals(1, electionWithZeroSeats.getSeatsAvailable());

        Election electionWithNegativeSeats = new Election(ElectionType.GENERAL, "Ireland", LocalDate.now(), -5);
        assertEquals(1, electionWithNegativeSeats.getSeatsAvailable());
    }

    @Test
    void getId() {
        int id = election.getId();
        assertTrue(id >= 1000);

        Election anotherElection = new Election(ElectionType.LOCAL, "Dublin",
                LocalDate.of(2019, 5, 24), 63);
        assertEquals(id + 1, anotherElection.getId());
    }

    @Test
    void setType() {
        election.setType(ElectionType.LOCAL);
        assertEquals(ElectionType.LOCAL, election.getType());

    }

    @Test
    void setLocation() {
        election.setLocation("Dublin");
        assertEquals("Dublin", election.getLocation());

    }

    @Test
    void setDate() {
        LocalDate newDate = LocalDate.of(2024, 5, 1);
        election.setDate(newDate);
        assertEquals(newDate, election.getDate());
        assertEquals(2024, election.getYear());
    }

    @Test
    void setSeatsAvailable() {
        election.setSeatsAvailable(200);
        assertEquals(200, election.getSeatsAvailable());
    }

    @Test
    void getYear() {
        assertEquals(2020, election.getYear());

        election.setDate(LocalDate.of(2024, 5, 1));
        assertEquals(2024, election.getYear());
    }

    @Test
    void addCandidate() {
        Politician newPolitician = new Politician("Mary Johnson",
                LocalDate.of(1972, 3, 10),
                "Sinn Féin",
                "Belfast",
                "https://example.com/maryjohnson.jpg");

        Candidate newCandidate = new Candidate(newPolitician, election, "Sinn Féin", 10103);

        election.addCandidate(newCandidate);
        assertTrue(election.getCandidates().contains(newCandidate));
    }

    @Test
    void addCandidate_Null() {
        int initialSize = election.getCandidates().getSize();
        election.addCandidate(null);
        assertEquals(initialSize, election.getCandidates().getSize());
    }

    @Test
    void removeCandidate() {
        assertTrue(election.getCandidates().contains(candidate1));
        election.removeCandidate(candidate1);
    }

    @Test
    void removeCandidate_Null() {
        int initialSize = election.getCandidates().getSize();
        election.removeCandidate(null);
        assertEquals(initialSize, election.getCandidates().getSize());
    }

    @Test
    void removeCandidate_NotInList() {
        Politician newPolitician = new Politician("Mary Johnson",
                LocalDate.of(1972, 3, 10),
                "Sinn Féin",
                "Belfast",
                "https://example.com/maryjohnson.jpg");

        Candidate nonExistentCandidate = new Candidate(newPolitician, election, "Sinn Féin", 10103);

        int initialSize = election.getCandidates().getSize();
        election.removeCandidate(nonExistentCandidate);
        assertEquals(initialSize, election.getCandidates().getSize());
    }

    @Test
    void getCandidates() {
      HashList<Candidate> candidates = election.getCandidates();
        assertNotNull(candidates);
        assertTrue(candidates.contains(candidate1));
        assertTrue(candidates.contains(candidate2));
    }



    @Test
    void testToString_NoCandidates() {
        Election emptyElection = new Election(ElectionType.LOCAL, "Dublin",
                LocalDate.of(2019, 5, 24), 63);

        String result = emptyElection.toString();
        assertTrue(result.contains("Dublin"));
        assertTrue(result.contains("2019"));
    }

    @Test
    void testMultipleElectionTypes() {
        Election general = new Election(ElectionType.GENERAL, "Ireland", LocalDate.now(), 160);
        assertEquals(ElectionType.GENERAL, general.getType());

        Election local = new Election(ElectionType.LOCAL, "Dublin", LocalDate.now(), 63);
        assertEquals(ElectionType.LOCAL, local.getType());

        Election european = new Election(ElectionType.EUROPEAN, "Ireland", LocalDate.now(), 13);
        assertEquals(ElectionType.EUROPEAN, european.getType());

        Election presidential = new Election(ElectionType.PRESIDENTIAL, "Ireland", LocalDate.now(), 1);
        assertEquals(ElectionType.PRESIDENTIAL, presidential.getType());
    }

    @Test
    void testElectionWithManyCandidates() {
        Election election = new Election(ElectionType.GENERAL, "Test", LocalDate.now(), 5);

        for (int i = 0; i < 10; i++) {
            Politician politician = new Politician("Politician " + i,
                    LocalDate.of(1970 + i, 1, 1),
                    "Party " + i,
                    "County " + i,
                    "https://example.com/politician" + i + ".jpg");

            Candidate candidate = new Candidate(politician, election, "Party " + i, 1000 + i * 100);
            election.addCandidate(candidate);
        }
    }

}