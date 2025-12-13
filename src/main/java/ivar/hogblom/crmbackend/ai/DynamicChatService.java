package ivar.hogblom.crmbackend.ai;

import ivar.hogblom.crmbackend.dto.ai.DynamicAiRequest;
import reactor.core.publisher.Flux;

public interface DynamicChatService {

    Flux<String> chat(DynamicAiRequest req);
}
