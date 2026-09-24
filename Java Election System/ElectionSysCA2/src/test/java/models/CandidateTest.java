package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CandidateTest {

    private Candidate candidate;
    private Politician politician;
    private Election election;

    @BeforeEach
    void setUp() {
        politician = new Politician("John Doe",
                LocalDate.of(1975, 5, 15),
                "Fine Gael",
                "Dublin",
                "https://example.com/johndoe.jpg");

        election = new Election(ElectionType.GENERAL, "Dublin",
                LocalDate.of(2020, 2, 8), 160);

        candidate = new Candidate(politician, election, "Fine Gael", 8759);
    }

    @AfterEach
    void tearDown() {
        candidate = null;
        politician = null;
        election = null;
    }

    @Test
    void constructor_ValidParameters() {
        assertNotNull(candidate);
        assertEquals(politician, candidate.getPolitician());
        assertEquals(election, candidate.getElection());
        assertEquals("Fine Gael", candidate.getPartyAffiliation());
        assertEquals(8759, candidate.getVotesReceived());
        assertFalse(candidate.isWinner());
    }

    @Test
    void constructor_NegativeVotes() {
        Candidate candidateWithNegativeVotes = new Candidate(politician, election, "Fine Gael", -100);
        assertEquals(0, candidateWithNegativeVotes.getVotesReceived());
    }


    @Test
    void setPartyAffiliation_ValidParty() {
        candidate.setPartyAffiliation("Fianna Fáil");
        assertEquals("Fianna Fáil", candidate.getPartyAffiliation());
    }

    @Test
    void setPartyAffiliation_DifferentFromPoliticianParty() {
        candidate.setPartyAffiliation("Independent");
        assertEquals("Independent", candidate.getPartyAffiliation());
        assertEquals("Fine Gael", politician.getCurrentParty());
    }

    @Test
    void setVotesReceived_Positive() {
        candidate.setVotesReceived(10000);
        assertEquals(10000, candidate.getVotesReceived());
    }

    @Test
    void setVotesReceived_Zero() {
        candidate.setVotesReceived(0);
        assertEquals(0, candidate.getVotesReceived());
    }



    @Test
    void setWinner_True() {
        candidate.setWinner(true);
        assertTrue(candidate.isWinner());
    }

    @Test
    void setWinner_False() {
        candidate.setWinner(true);
        candidate.setWinner(false);
        assertFalse(candidate.isWinner());
    }

    @Test
    void getPolitician() {
        assertSame(politician, candidate.getPolitician());
        assertEquals("John Doe", candidate.getPolitician().getName());
        assertEquals("Fine Gael", candidate.getPolitician().getCurrentParty());
    }

    @Test
    void getElection() {
        assertSame(election, candidate.getElection());
        assertEquals(ElectionType.GENERAL, candidate.getElection().getType());
        assertEquals("Dublin", candidate.getElection().getLocation());
        assertEquals(2020, candidate.getElection().getYear());
    }

    @Test
    void toString_NonWinner() {
        String result = candidate.toString();
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("Fine Gael"));
        assertTrue(result.contains("8759 votes"));
        assertFalse(result.contains("[WINNER]"));
    }

    @Test
    void toString_Winner() {
        candidate.setWinner(true);
        String result = candidate.toString();
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("Fine Gael"));
        assertTrue(result.contains("8759 votes"));
        assertTrue(result.contains("[WINNER]"));
    }

    @Test
    void toString_IndependentCandidate() {
        Candidate independentCandidate = new Candidate(politician, election, "Independent", 5000);
        String result = independentCandidate.toString();
        assertTrue(result.contains("Independent"));
    }

    @Test
    void testCandidateEquality() {
        Candidate candidate1 = new Candidate(politician, election, "Fine Gael", 8759);
        Candidate candidate2 = new Candidate(politician, election, "Fine Gael", 8759);

        assertEquals(candidate1.getPolitician(), candidate2.getPolitician());
        assertEquals(candidate1.getElection(), candidate2.getElection());

        assertNotSame(candidate1, candidate2);
    }

    @Test
    void testCandidateWithDifferentParty() {
        Candidate candidateWithDifferentParty = new Candidate(
                politician,
                election,
                "Sinn Féin",
                6500
        );

        assertEquals("Sinn Féin", candidateWithDifferentParty.getPartyAffiliation());
        assertEquals("Fine Gael", politician.getCurrentParty());
    }

    @Test
    void testCandidateInMultipleElections() {
        Election localElection = new Election(ElectionType.LOCAL, "Dublin City",
                LocalDate.of(2019, 5, 24), 63);
        Candidate candidateInLocalElection = new Candidate(politician, localElection, "Fine Gael", 4500);

        assertSame(politician, candidateInLocalElection.getPolitician());
        assertEquals(localElection, candidateInLocalElection.getElection());
        assertEquals(4500, candidateInLocalElection.getVotesReceived());
    }

    @Test
    void testCandidateWinnerStatus() {
        assertFalse(candidate.isWinner());

        candidate.setWinner(true);
        assertTrue(candidate.isWinner());

        String result = candidate.toString();
        assertTrue(result.contains("[WINNER]"));
    }

}