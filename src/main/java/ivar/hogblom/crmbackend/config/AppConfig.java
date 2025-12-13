package ivar.hogblom.crmbackend.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("ivar.hogblom.crmbackend.*")
public class AppConfig {

    @Bean
    public ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(16) //Tar med de senaste 16 messages, resten kickas ut.
                .chatMemoryRepository(chatMemoryRepository)
                .build();
    }
}