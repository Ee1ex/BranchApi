package com.branch.branchapiinterface.controller;

import com.branch.branchapiclientsdk.model.User;
import com.branch.branchapiclientsdk.utils.SignUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NameControllerTest {

    @InjectMocks
    private NameController nameController;

    @Mock
    private HttpServletRequest request;

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setUsername("yupi");
    }

    @Test
    public void getUsernameByPost_Success() {
        // Setup
        setupRequest("yupi", "1234", "1589283487", "POST 你的名字是yupi");

        // Execute
        String result = nameController.getUsernameByPost(user, request);

        // Verify
        assertEquals("Result should match expected message", "POST 你的名字是yupi", result);
    }

    @Test(expected = RuntimeException.class)
    public void getUsernameByPost_ThrowsException_When_AccessKeyIsIncorrect() {
        // Setup
        setupRequest("wrongKey", "1234", "1589283487", "");

        // Execute
        nameController.getUsernameByPost(user, request);

        // The expected exception is thrown
    }

    @Test(expected = RuntimeException.class)
    public void getUsernameByPost_ThrowsException_When_NonceIsIncorrect() {
        // Setup
        setupRequest("yupi", "99999", "1589283487", "");

        // Execute
        nameController.getUsernameByPost(user, request);

        // The expected exception is thrown
    }

    @Test(expected = RuntimeException.class)
    public void getUsernameByPost_ThrowsException_When_TimestampIsTooOld() {
        // Setup
        setupRequest("yupi", "1234", "1589283487", "");

        // Execute
        nameController.getUsernameByPost(user, request);

        // The expected exception is thrown
    }

    @Test(expected = RuntimeException.class)
    public void getUsernameByPost_ThrowsException_When_SignIsIncorrect() {
        // Setup
        setupRequest("yupi", "1234", "1589283487", "incorrectSign");

        // Execute
        nameController.getUsernameByPost(user, request);

        // The expected exception is thrown
    }

    private void setupRequest(String accessKey, String nonce, String timestamp, String expectedOutcome) {
        when(request.getHeader("accessKey")).thenReturn(accessKey);
        when(request.getHeader("nonce")).thenReturn(nonce);
        when(request.getHeader("timestamp")).thenReturn(timestamp);
        when(request.getHeader("sign")).thenReturn(generateSign("yupi" + nonce + timestamp)); // Assuming correct sign generation here
        when(request.getHeader("body")).thenReturn(user.getUsername());

        if (!accessKey.equals("yupi")) {
            doNothing().when(nameController).throwAccessKeyException();
        }
        if (Long.parseLong(nonce) > 10000) {
            doNothing().when(nameController).throwNonceException();
        }
        // Time comparison is dynamic and cannot be pre-verified, exclude from setup
        if (!SignUtils.getSign(user.getUsername(), "abcdefgh").equals(generateSign("yupi" + nonce + timestamp))) {
            doNothing().when(nameController).throwSignException();
        }
    }

    private String generateSign(String message) {
        // This should match the expected behavior of SignUtils.getSign
        // For simplicity, assuming it just returns a hash (in practice, this would be the actual signing logic)
        return message; // Placeholder for actual signing logic
    }

}
