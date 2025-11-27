package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.ResellerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.ResellerRequestDto;
import ivar.hogblom.crmbackend.dto.ResellerResponseDto;
import ivar.hogblom.crmbackend.entity.Contract;
import ivar.hogblom.crmbackend.entity.Reseller;
import ivar.hogblom.crmbackend.repository.ContractRepository;
import ivar.hogblom.crmbackend.repository.ResellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResellerServiceImpl implements ResellerService {

    final private ResellerRepository resellerRepository;
    final private ContractRepository contractRepository;
    @Autowired
    public ResellerServiceImpl(ResellerRepository resellerRepository, ContractRepository contractRepository) {
        this.resellerRepository = resellerRepository;
        this.contractRepository = contractRepository;
    }

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
    public void updateReseller(UUID id, ResellerRequestDto request) {
        Reseller existing = resellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseller with id " + id + " not found!"));

        existing.setName(request.name());
        existing.setOrgNo(request.orgNo());
        existing.setAddress(request.address());
        existing.setContactEmail(request.contactEmail());
        existing.setContactTelephone(request.contactTelephone());
        existing.setInvoiceReference(request.invoiceReference());
        existing.setCreatedAt(request.createdAt());

        resellerRepository.save(existing);
    }


    @Override
    @Transactional
    public void deleteReseller(UUID id) {

        Reseller reseller = resellerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseller with id " + id + " not found!"));

        List<Contract> contracts = contractRepository.findAllByResellers_Id(id);

        for (Contract contract : contracts) {
            contract.getResellers().remove(reseller);
        }

        contractRepository.saveAll(contracts);

        resellerRepository.delete(reseller);
    }

    @Transactional
    public void updateResellerActive(UUID id, boolean active) {

        Reseller reseller = resellerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Reseller not found with id: " + id)
                );

        reseller.setActive(active);
        resellerRepository.save(reseller);
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
