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

    public ChatController(OllamaChatService ollamaChatService) {
        this.ollamaChatService = ollamaChatService;
    }
    @PostMapping("/ask")
    public String ask(@RequestBody ChatRequest dto) {

        String answer = ollamaChatService.processSimpleChatQuery(dto);

        System.out.println("answer in repository before return: " + answer);

        return answer;
    }
}
