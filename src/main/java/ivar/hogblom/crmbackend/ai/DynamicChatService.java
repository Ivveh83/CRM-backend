package ivar.hogblom.crmbackend.ai;

import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DynamicChatService {

    // -----------------------------------------------------
    // Optional ChatModels (skapas bara om env-vars finns)
    // -----------------------------------------------------

    @Autowired(required = false)
    @Qualifier("openAiChatModel")
    private ChatModel openAiModel;

    @Autowired(required = false)
    @Qualifier("googleGenAiChatModel")
    private ChatModel googleModel;

    @Autowired(required = false)
    @Qualifier("deepSeekChatModel")
    private ChatModel deepSeekModel;

    @Autowired(required = false)
    @Qualifier("anthropicChatModel")
    private ChatModel anthropicModel;

    @Autowired(required = false)
    @Qualifier("huggingfaceChatModel")
    private ChatModel huggingfaceModel;

    @Autowired(required = false)
    @Qualifier("ollamaChatModel")
    private ChatModel ollamaModel;

    // -----------------------------------------------------
    // Övriga beroenden
    // -----------------------------------------------------

    private ChatMemory chatMemory;
    private SystemPromptService systemPromptService;
    private ReadToolCalling readToolCalling;
    private CRUToolCalling CRUToolCalling;

    @Autowired
    public DynamicChatService(
            ChatMemory chatMemory,
            SystemPromptService systemPromptService,
            ReadToolCalling readToolCalling,
            CRUToolCalling CRUToolCalling) {
        this.chatMemory = chatMemory;
        this.systemPromptService = systemPromptService;
        this.readToolCalling = readToolCalling;
        this.CRUToolCalling = CRUToolCalling;
    }

    // -----------------------------------------------------
    // MAIN CHAT ENTRY POINT
    // -----------------------------------------------------


    public String chatMemory(ChatRequest dto) {
        if (dto.conversationId() == null || dto.conversationId().isEmpty() || dto.prompt() == null || dto.prompt().isEmpty())
        {
            throw new IllegalArgumentException("conversationId and prompt cannot be null or empty");
        }
        if (dto.systemPromptProfile() == null || dto.systemPromptProfile().isEmpty()) {
            throw new IllegalArgumentException("systemPromptProfile cannot be null or empty");
        }
        ChatClient providerClient = selectClient(dto.provider());

        boolean isAction = dto.systemPromptProfile().equals("ACTION");

        try{
            ChatResponse chatResponse = providerClient.prompt()
                    .system(systemPromptService.getPrompt(dto.systemPromptProfile()))
                    .user(dto.prompt())
                    .tools(isAction ? CRUToolCalling : readToolCalling)
                    .options(buildOptions(dto))
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, dto.conversationId()))
                    .call()
                    .chatResponse();
            assert chatResponse != null;
            return chatResponse.getResult().getOutput().getText();
    } catch (Exception ex) {
        // om fallback(dto) idag returnerar Flux<String>, gör en separat "blocking fallback"
            System.out.println("Error in chat memory: " + ex.getMessage());
            System.out.println("Switching to fallback");
            return fallback(dto);
    }
    }


// -----------------------------------------------------
// Provider router (dynamisk & säker)
// -----------------------------------------------------

private ChatClient selectClient(String provider) {
    return switch (provider.toLowerCase()) {
        case "openai" -> buildClient(openAiModel, "OpenAI");
        case "google" -> buildClient(googleModel, "Google GenAI");
        case "deepseek" -> buildClient(deepSeekModel, "DeepSeek");
        case "anthropic" -> buildClient(anthropicModel, "Anthropic");
        case "huggingface" -> buildClient(huggingfaceModel, "HuggingFace");
        case "ollama" -> buildClient(ollamaModel, "Ollama");
        default -> throw new IllegalArgumentException("Unknown provider: " + provider);
    };
}

private ChatClient buildClient(ChatModel model, String name) {
    if (model == null) {
        throw new IllegalStateException(
                name + " är inte konfigurerad. Kontrollera miljövariabler."
        );
    }
    return ChatClient.builder(model)
            .defaultAdvisors(
                    MessageChatMemoryAdvisor.builder(chatMemory).build())
            .build();
}
    // -----------------------------------------------------
    // Fallback (ex: Ollama)
    // -----------------------------------------------------

    private String fallback(ChatRequest dto) {

        if (ollamaModel == null) {
            return "Ingen fallback-provider är konfigurerad";
        }

        return ChatClient.builder(ollamaModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .build()
                )
                .build()
                .prompt(dto.prompt())
                .system(systemPromptService.getPrompt(dto.systemPromptProfile()))
                .user(dto.prompt())
                .options(OllamaChatOptions.builder()
                        .model("phi4-mini:latest")
                        .temperature(dto.temperature() != null ? dto.temperature() : 0.2)
                        .build())
                .call()
                .chatResponse().getResult().getOutput().getText();
    }

    // -----------------------------------------------------
    // Dynamic ChatOptions per provider
    // -----------------------------------------------------

    private ChatOptions buildOptions(ChatRequest dto) {

        String provider = dto.provider().toLowerCase();
        double temp = dto.temperature() != null ? dto.temperature() : 0.3;
        int max = dto.maxTokens() != null ? dto.maxTokens() : 800;
        Double topP = dto.topP() != null ? dto.topP() : 1.0;

        return switch (provider) {

            case "openai" -> OpenAiChatOptions.builder()
                    .model(dto.model())
                    .temperature(temp)
                    .maxTokens(max)
                    .topP(topP)
                    .build();

            case "google" -> GoogleGenAiChatOptions.builder()
                    .model(dto.model())
                    .temperature(temp)
                    .maxOutputTokens(max)
                    .topP(topP)
                    .build();

            case "deepseek" -> DeepSeekChatOptions.builder()
                    .model(dto.model())
                    .temperature(temp)
                    .maxTokens(max)
                    .build();

            case "anthropic" -> AnthropicChatOptions.builder()
                    .model(dto.model())
                    .temperature(temp)
                    .maxTokens(max)
                    .build();

            case "huggingface" -> ChatOptions.builder()
                    .model(dto.model())
                    .temperature(temp)
                    .build();

            case "ollama" -> OllamaChatOptions.builder()
                    .model(dto.model())
                    .temperature(temp)
                    .numPredict(max)
                    .build();

            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }
}