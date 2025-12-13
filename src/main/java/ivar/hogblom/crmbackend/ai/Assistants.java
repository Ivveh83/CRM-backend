package ivar.hogblom.crmbackend.ai;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum Assistants {
    ANALYSIS,
    DECISION_SUPPORT,
    EMAIL_CREATION,
    ACTION
    ;
    private String systemPrompt;
    public String getSystemPrompt() {
        return systemPrompt;
    }
}
