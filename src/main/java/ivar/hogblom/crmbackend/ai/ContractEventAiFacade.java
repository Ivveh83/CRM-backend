package ivar.hogblom.crmbackend.ai;

import ivar.hogblom.crmbackend.crm.repository.contract.ContractEventRepository;
import ivar.hogblom.crmbackend.crm.service.contract.ContractEventService;
import ivar.hogblom.crmbackend.dto.contract.ContractEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractEventAiFacade {

    private final ContractEventRepository contractEventRepository;

    @Transactional(readOnly = true)
    public List<ContractEventDto> getEventsForAi(UUID contractId) {

        return contractEventRepository
                .findByContractIdOrderByEventTsDesc(contractId)
                .stream()
                .map(event -> ContractEventDto.builder()
                        .id(event.getId())
                        .eventType(event.getEventType().name())
                        .detail(event.getDetail())
                        .eventTs(event.getEventTs())
                        .actor(event.getActor())
                        .build()
                )
                .toList();
    }
}
