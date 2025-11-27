package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.*;
import ivar.hogblom.crmbackend.entity.Contract;
import ivar.hogblom.crmbackend.entity.Subscription;
import ivar.hogblom.crmbackend.repository.ContractRepository;
import ivar.hogblom.crmbackend.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ContractRepository contractRepository;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, ContractRepository contractRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.contractRepository = contractRepository;
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
    @Transactional
    public void createSubscription(SubscriptionRequestDto request) {

        Subscription subscription = toEntity(request);

        subscriptionRepository.save(subscription);
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------
    @Override
    @Transactional
    public void updateSubscription(UUID id, SubscriptionRequestDto request) {

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        updateEntity(subscription, request);

        subscriptionRepository.save(subscription);
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------
    @Override
    @Transactional
    public void deleteSubscription(UUID id) {

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription with id " + id + " not found!"));

        // Hitta alla kontrakt som använder detta abonnemang
        List<Contract> contracts = contractRepository.findAllBySubscriptions_Id(id);

        // Ta bort abonnemanget från varje kontrakt
        for (Contract contract : contracts) {
            contract.getSubscriptions().remove(subscription);
        }

        // Spara ändrade kontrakt
        contractRepository.saveAll(contracts);

        // Ta bort själva abonnemanget
        subscriptionRepository.delete(subscription);
    }


    // ---------------------------------------------------------
    // UPDATE ACTIVE
    // ---------------------------------------------------------
    @Override
    @Transactional
    public void updateSubscriptionActive(UUID id, boolean active) {

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        subscription.setActive(active);

        subscriptionRepository.save(subscription);
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

    private void updateEntity(Subscription subscription, SubscriptionRequestDto dto) {
        subscription.setName(dto.name());
        subscription.setCategory(dto.category());
        subscription.setDescription(dto.description());
        subscription.setServiceLevel(dto.serviceLevel());
        subscription.setPricePerMonth(dto.pricePerMonth());
        subscription.setContractLength(dto.contractLength());
        subscription.setRenewalPeriod(dto.renewalPeriod());
        subscription.setActive(dto.active());
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
