package ivar.hogblom.crmbackend.ai;

import ivar.hogblom.crmbackend.dto.ai.DynamicAiRequest;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DynamicChatServiceImpl implements DynamicChatService {

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

    private final AppToolCallingRead readTools;
    private final ChatMemory chatMemory;
    private final SystemPromptService promptService;

    @Autowired
    public DynamicChatServiceImpl(
            AppToolCallingRead readTools,
            ChatMemory chatMemory,
            SystemPromptService promptService
    ) {
        this.readTools = readTools;
        this.chatMemory = chatMemory;
        this.promptService = promptService;
    }

    // -----------------------------------------------------
    // MAIN CHAT ENTRY POINT
    // -----------------------------------------------------

    @Override
    public Flux<String> chat(DynamicAiRequest dto) {

        //1. Ställer in chat-klienten (ollama, openAI, google etc.)
        ChatClient client = selectClient(dto.provider());

        //2. Ställer in vilken roll som ska användas
        String systemPrompt = promptService.getPrompt(dto.systemPromptProfile());

        //3. Ger endast analys-rollen tillgång till tool-calling
        boolean allowTools = dto.systemPromptProfile().equals("ANALYSIS");

        return client.prompt()
                .system(systemPrompt)
                .user(dto.prompt())
                .tools(allowTools ? readTools : null)
                .options(buildOptions(dto))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, dto.conversationId()))
                .stream()
                .chatResponse()
                .mapNotNull(r -> r.getResult().getOutput().getText())
                .onErrorResume(ex -> fallback(dto));
    }

    // -----------------------------------------------------
    // Provider router (dynamisk & säker)
    // -----------------------------------------------------

    private ChatClient selectClient(String provider) {
        return switch (provider.toLowerCase()) {
            case "openai"      -> buildClient(openAiModel, "OpenAI");
            case "google"      -> buildClient(googleModel, "Google GenAI");
            case "deepseek"    -> buildClient(deepSeekModel, "DeepSeek");
            case "anthropic"   -> buildClient(anthropicModel, "Anthropic");
            case "huggingface" -> buildClient(huggingfaceModel, "HuggingFace");
            case "ollama"      -> buildClient(ollamaModel, "Ollama");
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }

    private ChatClient buildClient(ChatModel model, String name) {
        if (model == null) {
            throw new IllegalStateException(
                    name + " är inte konfigurerad. Kontrollera miljövariabler."
            );
        }
        return ChatClient.builder(model).build();
    }

    // -----------------------------------------------------
    // Fallback (ex: Ollama)
    // -----------------------------------------------------

    private Flux<String> fallback(DynamicAiRequest dto) {

        if (ollamaModel == null) {
            return Flux.error(
                    new IllegalStateException("Ingen fallback-provider är konfigurerad")
            );
        }

        return ChatClient.builder(ollamaModel)
                .build()
                .prompt()
                .system(promptService.getPrompt(dto.systemPromptProfile()))
                .user(dto.prompt())
                .options(OllamaChatOptions.builder()
                        .model("gemma3:4b")
                        .temperature(dto.temperature() != null ? dto.temperature() : 0.2)
                        .build())
                .stream()
                .chatResponse()
                .map(r -> r.getResult().getOutput().getText());
    }

    // -----------------------------------------------------
    // Dynamic ChatOptions per provider
    // -----------------------------------------------------

    private ChatOptions buildOptions(DynamicAiRequest dto) {

        String provider = dto.provider().toLowerCase();
        double temp = dto.temperature() != null ? dto.temperature() : 0.3;
        int max = dto.maxTokens() != null ? dto.maxTokens() : 800;
        Double topP = dto.topP();

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
