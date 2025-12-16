package ivar.hogblom.crmbackend.crm.service.reseller;

import ivar.hogblom.crmbackend.crm.security.RequireCrmDatabase;
import ivar.hogblom.crmbackend.dto.reseller.ResellerForContractComponentsDto;
import ivar.hogblom.crmbackend.dto.reseller.ResellerRequestDto;
import ivar.hogblom.crmbackend.dto.reseller.ResellerResponseDto;
import ivar.hogblom.crmbackend.util.OrganizationNumberValidator;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequireCrmDatabase
@Transactional(transactionManager = "crmTransactionManager")
@Service("validatingResellerService")
public class ResellerServiceValidatingImpl implements ResellerService {

    private final ResellerService delegate;
    private final OrganizationNumberValidator orgNoValidator;

    public ResellerServiceValidatingImpl(
            @Qualifier("mainResellerService") ResellerService delegate,
            OrganizationNumberValidator orgNoValidator) {
        this.delegate = delegate;
        this.orgNoValidator = orgNoValidator;
    }

    @Override
    public List<ResellerResponseDto> findAllResellers() {
        return delegate.findAllResellers();
    }

    @Override
    public List<ResellerForContractComponentsDto> findAllResellersForContractComponents() {
        return delegate.findAllResellersForContractComponents();
    }

    @Override
    public ResellerResponseDto findById(UUID id) {
        return delegate.findById(id);
    }

    @Override
    public void createReseller(ResellerRequestDto request) {
        if (!orgNoValidator.isValid(request.orgNo())) {
            throw new ValidationException("orgNo is not valid");
        }
        delegate.createReseller(request);
    }

    @Override
    public void updateReseller(UUID id, ResellerRequestDto request) {
        if (!orgNoValidator.isValid(request.orgNo())) {
            throw new ValidationException("orgNo is not valid");
        }
        delegate.updateReseller(id, request);

    }

    @Override
    public void deleteReseller(UUID id) {
        delegate.deleteReseller(id);
    }

    @Override
    public void updateResellerActive(UUID id, boolean active) {
        delegate.updateResellerActive(id, active);
    }
}
