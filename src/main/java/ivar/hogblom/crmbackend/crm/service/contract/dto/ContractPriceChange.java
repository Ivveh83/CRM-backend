package ivar.hogblom.crmbackend.crm.service.contract.dto;

import ivar.hogblom.crmbackend.crm.entity.contract.Contract;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContractPriceChange {
    private final Contract contract;
    private final Double oldTotalPrice;
    private final Double newTotalPrice;
}
