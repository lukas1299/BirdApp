package com.birdapp.BirdApp.authenticationTest;

import com.birdapp.BirdApp.ContainerBase;
import com.birdapp.BirdApp.config.JwtRequest;
import com.birdapp.BirdApp.entity.RoleType;
import com.birdapp.BirdApp.entity.User;
import com.birdapp.BirdApp.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtAuthenticationControllerTestIT extends ContainerBase {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        System.setProperty("SECRET_KEY", "1c45b6a2fbbe2c688e3375d6fae83a8a45c9eabf92de0a02d55a8e837c10dc1e");
        userRepository.deleteAll();
    }

    @Test
    void checkSuccessAuthentication() throws Exception {

        User user = userBuilder("user@gmail.com", "user", "pass", RoleType.USER);

        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(MockMvcResultMatchers.status().isCreated());

        json = objectMapper.writeValueAsString(new JwtRequest("user", "pass"));

        assertThat(userRepository.findAll().size()).isEqualTo(1);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        jsonPath("$.access_token").isString(),
                        jsonPath("$.refresh_token").isString());
    }

    @Test
    void checkSuccessRegistration() throws Exception {

        User user = userBuilder("user@gmail.com", "user", "pass", RoleType.USER);

        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void checkIncorrectRegistration() throws Exception {
        //given
        User user = userBuilder("user@gmail.com", "user", "pass", RoleType.USER);

        userRepository.save(user);
        String json = objectMapper.writeValueAsString(user);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("User currently exist"));
    }

    private User userBuilder(String email, String username, String password, RoleType roleType) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .username(username)
                .password(password)
                .role(roleType)
                .build();
    }
}
