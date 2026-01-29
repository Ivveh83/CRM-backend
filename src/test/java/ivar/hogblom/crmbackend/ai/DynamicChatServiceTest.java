package ivar.hogblom.crmbackend.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DynamicChatServiceTest {

    @Mock
    private ChatModel openAiModel;

    @Mock
    private ChatModel googleModel;

    @Mock
    private ChatModel deepSeekModel;

    @Mock
    private ChatModel anthropicModel;

    @Mock
    private ChatModel huggingfaceModel;

    @Mock
    private ChatModel ollamaModel;

    @Mock
    private ChatMemory chatMemory;

    @Mock
    private SystemPromptService systemPromptService;

    @Mock
    private ReadToolCalling readToolCalling;

    @Mock
    private CRUToolCalling cruToolCalling;

    @Mock
    private ChatResponse chatResponse;

    @Mock
    private org.springframework.ai.chat.model.Generation generation;

    @Mock
    private org.springframework.ai.chat.messages.AssistantMessage assistantMessage;

    private DynamicChatService service;

    @BeforeEach
    void setUp() {
        service = new DynamicChatService(
                chatMemory,
                systemPromptService,
                readToolCalling,
                cruToolCalling
        );

        // Inject mocked models using reflection
        injectField(service, "openAiModel", openAiModel);
        injectField(service, "googleModel", googleModel);
        injectField(service, "deepSeekModel", deepSeekModel);
        injectField(service, "anthropicModel", anthropicModel);
        injectField(service, "huggingfaceModel", huggingfaceModel);
        injectField(service, "ollamaModel", ollamaModel);
    }

    // Helper method to inject private fields
    private void injectField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Helper methods for private methods
    private ChatOptions invokePrivateBuildOptions(ChatRequest request) throws Exception {
        var method = service.getClass().getDeclaredMethod("buildOptions", ChatRequest.class);
        method.setAccessible(true);
        return (ChatOptions) method.invoke(service, request);
    }

    private ChatClient invokePrivateSelectClient(String provider) throws Exception {
        var method = service.getClass().getDeclaredMethod("selectClient", String.class);
        method.setAccessible(true);
        return (ChatClient) method.invoke(service, provider);
    }


// ==================== VALIDATION TESTS ====================

    @Test
    void chatMemory_shouldThrowException_whenConversationIdIsNull() {
        ChatRequest request = new ChatRequest(
                "openai",
                "gpt-4",
                "test prompt",
                "GENERAL",
                null,  // conversationId
                null, null, null, null
        );

        assertThrows(IllegalArgumentException.class, () -> service.chatMemory(request));
    }

    @Test
    void chatMemory_shouldThrowException_whenConversationIdIsEmpty() {
        ChatRequest request = new ChatRequest(
                "openai",
                "gpt-4",
                "test prompt",
                "GENERAL",
                "",  // conversationId
                null, null, null, null
        );

        assertThrows(IllegalArgumentException.class, () -> service.chatMemory(request));
    }

    @Test
    void chatMemory_shouldThrowException_whenPromptIsNull() {
        ChatRequest request = new ChatRequest(
                "openai",
                "gpt-4",
                null,  // prompt
                "GENERAL",
                "conv-123",
                null, null, null, null
        );

        assertThrows(IllegalArgumentException.class, () -> service.chatMemory(request));
    }

    @Test
    void chatMemory_shouldThrowException_whenPromptIsEmpty() {
        ChatRequest request = new ChatRequest(
                "openai",
                "gpt-4",
                "",  // prompt
                "GENERAL",
                "conv-123",
                null, null, null, null
        );

        assertThrows(IllegalArgumentException.class, () -> service.chatMemory(request));
    }

    @Test
    void chatMemory_shouldThrowException_whenSystemPromptProfileIsNull() {
        ChatRequest request = new ChatRequest(
                "openai",
                "gpt-4",
                "test prompt",
                null,  // systemPromptProfile
                "conv-123",
                null, null, null, null
        );

        assertThrows(IllegalArgumentException.class, () -> service.chatMemory(request));
    }

    @Test
    void chatMemory_shouldThrowException_whenSystemPromptProfileIsEmpty() {
        ChatRequest request = new ChatRequest(
                "openai",
                "gpt-4",
                "test prompt",
                "",  // systemPromptProfile
                "conv-123",
                null, null, null, null
        );

        assertThrows(IllegalArgumentException.class, () -> service.chatMemory(request));
    }

// ==================== PROVIDER SELECTION TESTS ====================

    @Test
    void selectClient_shouldThrowException_whenProviderIsUnknown() {
        ChatRequest request = new ChatRequest(
                "unknown-provider",
                "model",
                "test prompt",
                "GENERAL",
                "conv-123",
                null, null, null, null
        );

        assertThrows(IllegalArgumentException.class, () -> service.chatMemory(request));
    }

// ==================== OPTIONS BUILDING TESTS ====================

    @Test
    void buildOptions_shouldUseDefaultTopP_whenNotProvided() throws Exception {
        ChatRequest request = new ChatRequest(
                "openai",  // OpenAI stödjer topP
                "gpt-4",
                "test prompt",
                "GENERAL",
                "conv-123",
                0.5,
                1000,
                null, // topP = null
                null
        );

        // Använd reflection för att anropa buildOptions
        ChatOptions options = invokePrivateBuildOptions(request);

        // Cast till rätt typ för att komma åt topP
        OpenAiChatOptions openAiOptions = (OpenAiChatOptions) options;

        // Verifiera att default topP är 1.0
        assertEquals(1.0, openAiOptions.getTopP());
    }

    @Test
    void buildOptions_shouldUseDefaultTemperature_whenNotProvided() throws Exception {
        ChatRequest request = new ChatRequest(
                "anthropic",
                "claude-3-sonnet",
                "test prompt",
                "GENERAL",
                "conv-123",
                null, // temperature = null
                1000,
                1.0,
                null
        );

        ChatOptions options = invokePrivateBuildOptions(request);

        AnthropicChatOptions anthropicOptions = (AnthropicChatOptions) options;

        // Verifiera att default temperature är 0.3
        assertEquals(0.3, anthropicOptions.getTemperature());
    }

    @Test
    void buildOptions_shouldUseDefaultMaxTokens_whenNotProvided() throws Exception {
        ChatRequest request = new ChatRequest(
                "google",
                "gemini-pro",
                "test prompt",
                "GENERAL",
                "conv-123",
                0.5,
                null, // maxTokens = null
                1.0,
                null
        );

        ChatOptions options = invokePrivateBuildOptions(request);

        GoogleGenAiChatOptions googleOptions = (GoogleGenAiChatOptions) options;

        // Verifiera att default maxOutputTokens är 800
        assertEquals(800, googleOptions.getMaxOutputTokens());
    }

    @Test
    void buildOptions_shouldUseProvidedValues_whenNotNull() throws Exception {
        ChatRequest request = new ChatRequest(
                "openai",
                "gpt-4",
                "test prompt",
                "GENERAL",
                "conv-123",
                0.7,    // custom temperature
                1500,   // custom maxTokens
                0.95,   // custom topP
                null
        );

        ChatOptions options = invokePrivateBuildOptions(request);

        OpenAiChatOptions openAiOptions = (OpenAiChatOptions) options;

        // Verifiera att custom-värden används
        assertEquals(0.7, openAiOptions.getTemperature());
        assertEquals(1500, openAiOptions.getMaxTokens());
        assertEquals(0.95, openAiOptions.getTopP());
    }

// ==================== FALLBACK TESTS ====================

    @ParameterizedTest
    @ValueSource(strings = {
            "openAi", "google", "deepSeek", "anthropic", "huggingface", "ollama"
    })
    void selectClient_shouldFailFast_whenProviderModelIsNull(String provider) {
        injectField(service, provider + "Model", null);

        ChatRequest request = new ChatRequest(
                provider,
                "model",
                "test",
                "GENERAL",
                "conv-123",
                null, null, null, null
        );

        assertThrows(IllegalStateException.class, () -> service.chatMemory(request));
    }


// ==================== CASE INSENSITIVITY TESTS ====================

    @ParameterizedTest
    @ValueSource(strings = {
            "openai", "OPENAI", "OpenAI",
            "google", "GOOGLE", "Google",
            "deepseek", "DEEPSEEK", "DeepSeek",
            "anthropic", "ANTHROPIC", "Anthropic",
            "huggingface", "HUGGINGFACE", "HuggingFace",
            "ollama", "OLLAMA", "Ollama"
    })
    void selectClient_shouldBeCaseInsensitive_forAllProviders(String provider) throws Exception {
        ChatClient client = invokePrivateSelectClient(provider);
        assertNotNull(client);
    }
}