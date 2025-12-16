package ivar.hogblom.crmbackend.ai;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/chat")
@Validated
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class ChatController {

    private final OllamaChatService ollamaChatService;
    private final DynamicChatService dynamicChatService;

    public ChatController(OllamaChatService ollamaChatService,
                          DynamicChatService dynamicChatService) {
        this.ollamaChatService = ollamaChatService;
        this.dynamicChatService = dynamicChatService;
    }
    @PostMapping("/ask")
    public String ask(@RequestBody ChatRequest dto) {

        String answer = ollamaChatService.processSimpleChatQuery(dto);

        System.out.println("answer in repository before return: " + answer);

        return answer;
    }

    @PostMapping("/ask/memory")
    public String askMemory(@RequestBody ChatRequest dto) {

        System.out.println("question in controller before ollamaChatService.chatMemory(dto) ");
        String answer = ollamaChatService.chatMemory(dto);

        System.out.println("answer in controller before return: " + answer);

        return answer;
    }

    @PostMapping("/ask/dynamic-chat-service")
    public String askDynamicChatService(@RequestBody ChatRequest dto) {

        System.out.println("question in controller before dynamicChatService.chatMemory(dto): " + dto.prompt());
        String answer = dynamicChatService.chatMemory(dto);

        System.out.println("answer in controller before return: " + answer);

        return answer;
    }

}
