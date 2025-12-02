package ivar.hogblom.crmbackend.service.dto;

import ivar.hogblom.crmbackend.entity.Contract;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContractPriceChange {
    private final Contract contract;
    private final Double oldTotalPrice;
    private final Double newTotalPrice;
}
