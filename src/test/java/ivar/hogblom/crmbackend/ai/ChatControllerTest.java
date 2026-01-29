package ivar.hogblom.crmbackend.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import ivar.hogblom.crmbackend.security.JwtTokenUtil;
import ivar.hogblom.crmbackend.security.TokenBlacklistStorage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@Import(ChatControllerTest.TestConfig.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DynamicChatService dynamicChatService;

    @TestConfiguration
    static class TestConfig {

        @Bean
        DynamicChatService dynamicChatService() {
            return Mockito.mock(DynamicChatService.class);
        }

        @Bean
        JwtTokenUtil jwtTokenUtil() {
            return Mockito.mock(JwtTokenUtil.class);
        }

        @Bean
        TokenBlacklistStorage tokenBlacklistStorage() {
            return Mockito.mock(TokenBlacklistStorage.class);
        }
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void askDynamicChatService_shouldReturnAnswer_whenUserIsAdmin() throws Exception {
        ChatRequest request = ChatRequest.builder()
                .provider("openai")
                .prompt("Hello dynamic chat")
                .systemPromptProfile("ANALYSIS")
                .build();

        String expectedAnswer = "Dynamic response";

        when(dynamicChatService.chatMemory(request))
                .thenReturn(expectedAnswer);

        mockMvc.perform(post("/api/chat/ask/dynamic-chat-service")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedAnswer));
    }

    @Test
    void askDynamicChatService_shouldReturn403_whenUserIsNotAuthenticated() throws Exception {
        ChatRequest request = ChatRequest.builder()
                .provider("openai")
                .prompt("Hello dynamic chat")
                .systemPromptProfile("ANALYSIS")
                .build();

        mockMvc.perform(post("/api/chat/ask/dynamic-chat-service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
