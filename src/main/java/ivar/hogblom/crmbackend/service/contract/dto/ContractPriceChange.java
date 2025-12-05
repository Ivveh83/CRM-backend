package ivar.hogblom.crmbackend.service.contract.dto;

import ivar.hogblom.crmbackend.entity.contract.Contract;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContractPriceChange {
    private final Contract contract;
    private final Double oldTotalPrice;
    private final Double newTotalPrice;
}
