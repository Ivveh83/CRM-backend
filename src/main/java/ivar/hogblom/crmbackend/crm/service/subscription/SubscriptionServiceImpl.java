package ivar.hogblom.crmbackend.crm.service.subscription;

import ivar.hogblom.crmbackend.crm.security.RequireCrmDatabase;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionRequestDto;
import ivar.hogblom.crmbackend.dto.subscription.SubscriptionResponseDto;
import ivar.hogblom.crmbackend.crm.entity.subscription.Subscription;
import ivar.hogblom.crmbackend.crm.repository.subscription.SubscriptionRepository;
import ivar.hogblom.crmbackend.crm.service.contract.ContractEventService;
import ivar.hogblom.crmbackend.crm.service.contract.ContractService;
import ivar.hogblom.crmbackend.util.SubscriptionCloneUtil;
import ivar.hogblom.crmbackend.util.SubscriptionDiffUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RequireCrmDatabase
@Service

@Transactional(transactionManager = "crmTransactionManager")
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionCloneUtil subscriptionCloneUtil;
    private final ContractService contractService;
    private final ContractEventService contractEventService;
    private final SubscriptionEventService subscriptionEventService;
    private final SubscriptionDiffUtil subscriptionDiffUtil;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, SubscriptionCloneUtil subscriptionCloneUtil, @Qualifier("mainContractService")ContractService contractService, ContractEventService contractEventService, SubscriptionEventService subscriptionEventService, SubscriptionDiffUtil subscriptionDiffUtil) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionCloneUtil = subscriptionCloneUtil;
        this.contractService = contractService;
        this.contractEventService = contractEventService;
        this.subscriptionEventService = subscriptionEventService;
        this.subscriptionDiffUtil = subscriptionDiffUtil;
    }

    // ---------------------------------------------------------
    // FIND ALL
    // ---------------------------------------------------------
    @Override
    public List<SubscriptionResponseDto> findAll() {
        return subscriptionRepository.findAll()
                .stream()
                .map(this::toSubscriptionResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionForContractComponentsDto> findAllSubscriptionsForContractComponents() {
        return subscriptionRepository.findAll().stream()
                .map(this::toSubscriptionForContractComponentsDto)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // FIND BY ID
    // ---------------------------------------------------------
    @Override
    public SubscriptionResponseDto findById(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        return toSubscriptionResponseDto(subscription);
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------
    @Override
    
    public void createSubscription(SubscriptionRequestDto request) {

        boolean exists = subscriptionRepository.existsByName(request.name());

        if (exists) {
            throw new IllegalArgumentException("A subscription with this name already exists: " + request.name());
        }

        Subscription subscription = toEntity(request);

        Subscription created = subscriptionRepository.save(subscription);

        subscriptionEventService.logSubscriptionCreated(created);
    }


    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @Override
    
    public void updateSubscription(UUID id, SubscriptionRequestDto request) {

        Subscription existing = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + id));

        // 1. Klona BEFORE
        Subscription oldCopy = subscriptionCloneUtil.clone(existing);

        // 2. Uppdatera entity, but not field active
        updateEntity(existing, request);

        // 3. Spara AFTER
        Subscription updated = subscriptionRepository.save(existing);

        // 4. Diffa abonnemanget
        List<String> subscriptionDiffs = subscriptionDiffUtil.diff(oldCopy, updated);

        // 5. Låt ContractService sköta ALL logik
        contractService.handleSubscriptionUpdated(oldCopy, updated, subscriptionDiffs);

        // 6. Låt SubscriptionEventService logga Subscription
        subscriptionEventService.logSubscriptionUpdated(updated, subscriptionDiffs);
    }

    // ---------------------------------------------------------
    // UPDATE ACTIVE
    // ---------------------------------------------------------
    @Override
    
    public void updateSubscriptionActive(UUID id, boolean active) {

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        subscription.setActive(active);

        Subscription updatedSub = subscriptionRepository.save(subscription);
        contractEventService.logSubscriptionActiveUpdate(updatedSub);

        if (updatedSub.getActive()) {
            subscriptionEventService.logSubscriptionReactivated(updatedSub);
        }else {
            subscriptionEventService.logSubscriptionPaused(updatedSub);
        }
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @Override
    
    public void deleteSubscription(UUID id) {

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + id));

        // Clone before delete
        Subscription oldCopy = subscriptionCloneUtil.clone(subscription);

        // Låt ContractService ta ALL logik, tranactional
        contractService.handleSubscriptionDeleted(oldCopy, subscription);

        // Delete subscription, tranactional
        subscriptionRepository.delete(subscription);

        // Log SubscriptionEvent
        subscriptionEventService.logSubscriptionDeleted(oldCopy);
    }



    // ---------------------------------------------------------
    // DTO ↔ ENTITY MAPPERS
    // ---------------------------------------------------------

    private Subscription toEntity(SubscriptionRequestDto dto) {
        return Subscription.builder()
                .name(dto.name())
                .category(dto.category())
                .description(dto.description())
                .serviceLevel(dto.serviceLevel())
                .pricePerMonth(dto.pricePerMonth())
                .contractLength(dto.contractLength())
                .renewalPeriod(dto.renewalPeriod())
                .active(dto.active())
                .supportContact(dto.supportContact())
                .createdAt(dto.createdAt())
                .notes(dto.notes())
                .build();
    }

    // This method do not update active
    private void updateEntity(Subscription subscription, SubscriptionRequestDto dto) {
        subscription.setName(dto.name());
        subscription.setCategory(dto.category());
        subscription.setDescription(dto.description());
        subscription.setServiceLevel(dto.serviceLevel());
        subscription.setPricePerMonth(dto.pricePerMonth());
        subscription.setContractLength(dto.contractLength());
        subscription.setRenewalPeriod(dto.renewalPeriod());
        subscription.setSupportContact(dto.supportContact());
        subscription.setCreatedAt(dto.createdAt());
        subscription.setNotes(dto.notes());
    }

    private SubscriptionResponseDto toSubscriptionResponseDto(Subscription s) {
        return SubscriptionResponseDto.builder()
                .id(s.getId())
                .name(s.getName())
                .category(s.getCategory())
                .description(s.getDescription())
                .serviceLevel(s.getServiceLevel())
                .pricePerMonth(s.getPricePerMonth())
                .contractLength(s.getContractLength())
                .renewalPeriod(s.getRenewalPeriod())
                .active(s.getActive())
                .supportContact(s.getSupportContact())
                .createdAt(s.getCreatedAt())
                .notes(s.getNotes())
                .build();
    }

    private SubscriptionForContractComponentsDto toSubscriptionForContractComponentsDto(Subscription s) {
        return SubscriptionForContractComponentsDto.builder()
                .id(s.getId())
                .name(s.getName())
                .contractLength(s.getContractLength())
                .renewalPeriod(s.getRenewalPeriod())
                .active(s.getActive())
                .pricePerMonth(s.getPricePerMonth())
                .build();
    }
}
