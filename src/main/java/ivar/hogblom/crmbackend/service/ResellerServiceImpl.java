package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.ResellerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.ResellerRequestDto;
import ivar.hogblom.crmbackend.dto.ResellerResponseDto;
import ivar.hogblom.crmbackend.entity.Contract;
import ivar.hogblom.crmbackend.entity.Reseller;
import ivar.hogblom.crmbackend.repository.ContractRepository;
import ivar.hogblom.crmbackend.repository.ResellerRepository;
import ivar.hogblom.crmbackend.util.ResellerCloneUtil;
import ivar.hogblom.crmbackend.util.ResellerDiffUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ResellerServiceImpl implements ResellerService {

    final private ResellerRepository resellerRepository;
    final private ContractEventService contractEventService;
    final private ContractService contractService;
    final private ResellerCloneUtil resellerCloneUtil;
    final private ResellerDiffUtil resellerDiffUtil;


    @Override
    public List<ResellerResponseDto> findAllResellers() {
        return resellerRepository.findAll()
                .stream()
                .map(this::toResellerResponseDto)
                .toList();
    }

    @Override
    public List<ResellerForContractComponentsDto> findAllResellersForContractComponents() {
        return resellerRepository.findAll().stream()
                .map(this::toResellerForContractComponentsDto)
                .collect(Collectors.toList());
    }

    public ResellerResponseDto findById(UUID id) {
        Reseller reseller = resellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseller not found with id: " + id));

        return toResellerResponseDto(reseller);
    }

    @Override
    public void createReseller(ResellerRequestDto request) {
        boolean exists = resellerRepository.existsByOrgNo(request.orgNo());
        if (exists) {
            throw new IllegalArgumentException(
                    "A reseller with orgNo " + request.orgNo() + " already exists."
            );
        }
        Reseller reseller = toEntity(request);
        resellerRepository.save(reseller);
    }

    @Override
    @Transactional
    public void updateReseller(UUID id, ResellerRequestDto request) {

        // 1. Hämta befintlig återförsäljare
        Reseller existing = resellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseller not found: " + id));

        // 2. Klona BEFORE
        Reseller oldCopy = resellerCloneUtil.clone(existing);

        // 3. Uppdatera entity från DTO, ej active
        existing.setName(request.name());
        existing.setOrgNo(request.orgNo());
        existing.setAddress(request.address());
        existing.setContactEmail(request.contactEmail());
        existing.setContactTelephone(request.contactTelephone());
        existing.setInvoiceReference(request.invoiceReference());
        existing.setCreatedAt(request.createdAt());

        // 4. Spara AFTER
        Reseller updated = resellerRepository.save(existing);

        // 5. Diffa återförsäljaren
        List<String> diffs = resellerDiffUtil.diff(oldCopy, updated);

        // 6. Låt ContractService hantera kontrakten + eventlogg
        contractService.handleResellerUpdated(oldCopy, updated, diffs);
    }

    @Override
    @Transactional
    public void deleteReseller(UUID id) {

        Reseller existing = resellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseller not found: " + id));

        // 1. Clone BEFORE deletion (for event logging)
        Reseller oldCopy = resellerCloneUtil.clone(existing);

        // 2. Let ContractService handle ALL logic regarding affected contracts
        contractService.handleResellerDeleted(oldCopy, existing);

        // 3. Finally delete the reseller itself
        resellerRepository.delete(existing);
    }

// ---------------------------------------------------------
// UPDATE ACTIVE
// ---------------------------------------------------------
    @Override
    @Transactional
    public void updateResellerActive(UUID id, boolean active) {

        Reseller reseller = resellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseller not found with id: " + id));

        reseller.setActive(active);

        Reseller updated = resellerRepository.save(reseller);

        // Låt ContractEventService hantera loggen
        contractEventService.logResellerActiveUpdate(updated);
    }


    private ResellerResponseDto toResellerResponseDto(Reseller r) {

        if (r == null) {
            return null;
        }
        return ResellerResponseDto.builder()
                .id(r.getId())
                .name(r.getName())
                .orgNo(r.getOrgNo())
                .active(r.isActive())
                .address(r.getAddress())
                .contactEmail(r.getContactEmail())
                .contactTelephone(r.getContactTelephone())
                .invoiceReference(r.getInvoiceReference())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private ResellerForContractComponentsDto toResellerForContractComponentsDto(Reseller r) {

        if (r == null) {
            return null;
        }

        return ResellerForContractComponentsDto.builder()
                .id(r.getId())
                .name(r.getName())
                .orgNo(r.getOrgNo())
                .active(r.isActive())
                .build();
    }

    private Reseller toEntity(ResellerRequestDto r) {
        if (r == null) {
            return null;
        }
        return Reseller.builder()
            .name(r.name())
            .orgNo(r.orgNo())
            .active(r.active())
            .address(r.address())
            .contactEmail(r.contactEmail())
            .contactTelephone(r.contactTelephone())
            .invoiceReference(r.invoiceReference())
            .createdAt(r.createdAt())
            .build();
    }
}
