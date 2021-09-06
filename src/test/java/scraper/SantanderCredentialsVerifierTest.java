package scraper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SantanderCredentialsVerifierTest {
    private SantanderCredentialsVerifier verifier;

    @BeforeEach
    void setUp() {
        this.verifier = new SantanderCredentialsVerifier();
    }

    @Test
    void verifyToken() {
        String valid = "123-123";
        String invalid1 = "12-123";
        String invalid2 = "";
        String invalid3 = "123123";
        assertTrue(verifier.verifyToken(valid));
        assertFalse(verifier.verifyToken(invalid1));
        assertFalse(verifier.verifyToken(invalid2));
        assertFalse(verifier.verifyToken(invalid3));
    }
}