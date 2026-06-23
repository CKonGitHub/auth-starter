package com.example.auth_starter.infrastructure.adapter.in.web;

import static org.hamcrest.Matchers.hasItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.auth_starter.infrastructure.adapter.out.persistence.UserJpaRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserJpaRepository userJpaRepository;

  @BeforeEach
  void setUp() {
    userJpaRepository.deleteAll();
  }

  @Test
  void should_register_user_successfully() throws Exception {
    String body = """
        {
          "email": "test@example.com",
          "password": "password12345"
        }
        """;

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.roles", hasItem("USER")))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  void should_return_bad_request_when_email_is_already_used() throws Exception {
    String body = """
        {
          "email": "test@example.com",
          "password": "password12345"
        }
        """;

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isOk());

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_USED"))
        .andExpect(jsonPath("$.message").value("Email déjà utilisé"));
  }

  @Test
  void should_login_user_successfully() throws Exception {
    String registerBody = """
        {
          "email": "test@example.com",
          "password": "password12345"
        }
        """;

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(registerBody))
        .andExpect(status().isOk());

    String loginBody = """
        {
          "email": "test@example.com",
          "password": "password12345"
        }
        """;

    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isNotEmpty())
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.user.id").exists())
        .andExpect(jsonPath("$.user.email").value("test@example.com"))
        .andExpect(jsonPath("$.user.roles", hasItem("USER")))
        .andExpect(jsonPath("$.user.password").doesNotExist())
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  void should_return_unauthorized_when_password_is_invalid() throws Exception {
    String registerBody = """
        {
          "email": "test@example.com",
          "password": "password12345"
        }
        """;

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(registerBody))
        .andExpect(status().isOk());

    String loginBody = """
        {
          "email": "test@example.com",
          "password": "wrong-password"
        }
        """;

    mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginBody))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
        .andExpect(jsonPath("$.message").value("Identifiants invalides"));
  }

  @Test
  void should_return_bad_request_when_register_password_is_too_short() throws Exception {
    String body = """
        {
          "email": "test@example.com",
          "password": "short"
        }
        """;

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_return_bad_request_when_register_email_is_invalid() throws Exception {
    String body = """
        {
          "email": "not-an-email",
          "password": "password12345"
        }
        """;

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isBadRequest());
  }

  @Test
  void should_return_unauthorized_when_accessing_me_without_token() throws Exception {
    mockMvc.perform(get("/api/auth/me"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void should_return_current_user_when_accessing_me_with_valid_token() throws Exception {
    String registerBody = """
        {
          "email": "test@example.com",
          "password": "password12345"
        }
        """;

    mockMvc.perform(post("/api/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(registerBody))
        .andExpect(status().isOk());

    String loginBody = """
        {
          "email": "test@example.com",
          "password": "password12345"
        }
        """;

    var loginResult = mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(loginBody))
        .andExpect(status().isOk())
        .andReturn();

    var loginJson = loginResult.getResponse().getContentAsString();

    var accessToken = loginJson
        .split("\"accessToken\":\"")[1]
        .split("\"")[0];

    mockMvc.perform(get("/api/auth/me")
        .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.email").value("test@example.com"))
        .andExpect(jsonPath("$.roles", hasItem("USER")))
        .andExpect(jsonPath("$.password").doesNotExist());
  }
}