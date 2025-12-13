package ivar.hogblom.crmbackend.ai;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class OllamaChatServiceImpl implements OllamaChatService {

    private OllamaChatModel ollamaChatModel;

    public OllamaChatServiceImpl(OllamaChatModel ollamaChatModel) {
        this.ollamaChatModel = ollamaChatModel;
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
}
