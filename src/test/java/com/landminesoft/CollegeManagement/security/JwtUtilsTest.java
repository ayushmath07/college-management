package com.landminesoft.CollegeManagement.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtils = new JwtUtils();

        // Inject values via reflection since @Value won't work in plain unit tests
        Field secretField = JwtUtils.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtUtils, "MySecretKeyForCollegeManagementJWTTokenGeneration2024VeryLongKeyForSecurity");

        Field expirationField = JwtUtils.class.getDeclaredField("jwtExpiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtils, 86400000L); // 24 hours
    }

    @Test
    void generateToken_andExtractEmail() {
        String token = jwtUtils.generateToken(1L, "rahul@college.edu", "STUDENT");

        String email = jwtUtils.getEmailFromToken(token);

        assertEquals("rahul@college.edu", email);
    }

    @Test
    void generateToken_andExtractRole() {
        String token = jwtUtils.generateToken(1L, "rahul@college.edu", "STUDENT");

        String role = jwtUtils.getRoleFromToken(token);

        assertEquals("STUDENT", role);
    }

    @Test
    void generateToken_andExtractUserId() {
        String token = jwtUtils.generateToken(1L, "rahul@college.edu", "STUDENT");

        Long userId = jwtUtils.getUserIdFromToken(token);

        assertEquals(1L, userId);
    }

    @Test
    void generateToken_withoutUserId() {
        String token = jwtUtils.generateToken("faculty@college.edu", "FACULTY");

        assertEquals("faculty@college.edu", jwtUtils.getEmailFromToken(token));
        assertEquals("FACULTY", jwtUtils.getRoleFromToken(token));
    }

    @Test
    void validateToken_valid() {
        String token = jwtUtils.generateToken(1L, "admin@college.edu", "ADMIN");

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void validateToken_invalid() {
        assertFalse(jwtUtils.validateToken("this.is.not.a.valid.token"));
    }

    @Test
    void validateToken_expired() throws Exception {
        // Set expiration to 0 ms (immediate expiry)
        Field expirationField = JwtUtils.class.getDeclaredField("jwtExpiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtils, 0L);

        String token = jwtUtils.generateToken(1L, "test@college.edu", "STUDENT");

        // Token should be expired immediately
        assertFalse(jwtUtils.validateToken(token));
    }

    @Test
    void validateToken_null() {
        assertFalse(jwtUtils.validateToken(null));
    }

    @Test
    void differentRoles_produceDifferentTokens() {
        String studentToken = jwtUtils.generateToken(1L, "test@college.edu", "STUDENT");
        String facultyToken = jwtUtils.generateToken(2L, "test@college.edu", "FACULTY");

        assertNotEquals(studentToken, facultyToken);
        assertEquals("STUDENT", jwtUtils.getRoleFromToken(studentToken));
        assertEquals("FACULTY", jwtUtils.getRoleFromToken(facultyToken));
    }
}
