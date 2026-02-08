package com.cloud.authentication_service;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.cloud.auth_service.AuthenticationServiceApplication;

@SpringBootTest(
    classes = AuthenticationServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test") 
@AutoConfigureMockMvc   
public abstract class BaseIntegrationTest {
    
    @Autowired
    protected MockMvc mockMvc; 
}