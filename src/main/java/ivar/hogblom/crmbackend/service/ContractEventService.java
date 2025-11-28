package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.entity.Contract;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface ContractEventService {

    void handleContractUpdate(Contract oldC, Contract newC);
    void handleContractActiveUpdate(Contract newC, boolean newActive, String details);
}
