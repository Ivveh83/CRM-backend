package ivar.hogblom.crmbackend.ai;

public interface OllamaChatService {
    String processSimpleChatQuery(ChatRequest dto);
    String chatMemory(ChatRequest dto);
}
