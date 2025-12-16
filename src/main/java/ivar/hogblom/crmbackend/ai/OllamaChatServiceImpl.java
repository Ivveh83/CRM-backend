package ivar.hogblom.crmbackend.ai;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OllamaChatServiceImpl implements OllamaChatService {

    private OllamaChatModel ollamaChatModel;

    private ChatMemory chatMemory;

    @Autowired
    public OllamaChatServiceImpl(
            OllamaChatModel ollamaChatModel,
            ChatMemory chatMemory) {
        this.ollamaChatModel = ollamaChatModel;
        this.chatMemory = chatMemory;
    }

    @Override
    public String processSimpleChatQuery(ChatRequest dto) {
        if (dto == null || dto.prompt().isEmpty()) {
            throw new IllegalArgumentException("message or dto cannot be null or empty");
        }
        System.out.println("Before ollamaChatModel.call(dto.prompt(): " + dto.prompt());
        String answer = ollamaChatModel.call(dto.prompt());
        System.out.println("answer : " + answer);
        return answer;
    }

    @Override
    public String chatMemory(ChatRequest dto) {
        if (dto.prompt() == null || dto.prompt().isEmpty()) {
            throw new IllegalArgumentException("message cannot be null or empty");
        }
        if (dto.conversationId() == null || dto.conversationId().isEmpty()) {
            throw new IllegalArgumentException("conversationId cannot be null or empty");
        }
        UserMessage userMessage = UserMessage.builder()
                .text(dto.prompt())
                .build();
        chatMemory.add(dto.conversationId(), userMessage);

        Prompt prompt = Prompt.builder()
                .messages(chatMemory.get(dto.conversationId())) //Här skiljer vi på CUD och enbart Read genom olika conversationId.
                .chatOptions(OllamaChatOptions.builder()
                        .model(dto.model())
                        .temperature(dto.temperature())
                        .topP(dto.topP())
                        .topK(dto.topK())
                        .build())
                .build();
        System.out.println("Before ollamaChatModel.call(dto.prompt(): " + dto.prompt());
        ChatResponse chatResponse = ollamaChatModel.call(prompt);
        chatMemory.add(dto.conversationId(), chatResponse.getResult().getOutput()); //Sparar assistentens svar
        System.out.println("Current memory size: " + chatMemory.get(dto.conversationId()).size());
        System.out.println("Messages in memory");
        chatMemory.get(dto.conversationId()).forEach(msg -> System.out.println(msg.getText()));

        return chatResponse.getResult().getOutput().getText();
    }

    public void resetChat(String conversationId) {
        if (conversationId == null || conversationId.isEmpty()) {
            throw new IllegalArgumentException("conversationId cannot be null or empty when clearing chat memory");
        }

        chatMemory.clear(conversationId);
    }
}
