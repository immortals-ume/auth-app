package com.immortals.authapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SmokeTest {

//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Test
//    void healthEndpointShouldReturnUp() {
//        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/actuator/health", String.class);
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).contains("\"status\":\"UP\"");
//    }
}
