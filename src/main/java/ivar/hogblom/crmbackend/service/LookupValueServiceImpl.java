package ivar.hogblom.crmbackend.service;

import ivar.hogblom.crmbackend.dto.LookupSortUpdateDto;
import ivar.hogblom.crmbackend.dto.LookupValueCreateDto;
import ivar.hogblom.crmbackend.dto.LookupValueResponseDto;
import ivar.hogblom.crmbackend.dto.LookupValueUpdateDto;
import ivar.hogblom.crmbackend.entity.LookupValue;
import ivar.hogblom.crmbackend.repository.LookupValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.slugify.Slugify;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LookupValueServiceImpl implements LookupValueService {

    private final LookupValueRepository repo;
    Slugify slugify = new Slugify();

    @Override
    public void create(LookupValueCreateDto dto) {

        String generatedValue = slugify.slugify(dto.label()).replace("-", "_");

        if (repo.existsByTypeAndValue(dto.type(), generatedValue)) {
            throw new RuntimeException("Värde finns redan för denna typ");
        }

        LookupValue saved = repo.save(
                LookupValue.builder()
                        .type(dto.type())
                        .value(generatedValue)
                        .label(dto.label())
                        .sortOrder(dto.sortOrder())
                        .active(true)
                        .build()
        );

        toResponseDto(saved);
    }

    @Override
    @Transactional
    public void update(String id, LookupValueUpdateDto dto) {

        LookupValue entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Lookup value not found: " + id));

        String newLabel = dto.label() != null ? dto.label().trim() : null;
        Integer newSort = dto.sortOrder();

        if (newLabel == null || newLabel.isEmpty()) {
            throw new RuntimeException("Label may not be empty");
        }

        // Check for duplicate label within same type (excluding current entry)
        boolean exists = repo.existsByTypeAndLabelAndIdNot(entity.getType(), newLabel, id);
        if (exists) {
            throw new RuntimeException(
                    "A label with this name already exists for this type."
            );
        }

        // Apply updates
        entity.setLabel(newLabel);
        entity.setSortOrder(newSort);

        repo.save(entity);
    }



    @Override
    public List<LookupValueResponseDto> getAllByTypeAndActive(String type, boolean onlyActive) {
        var list = onlyActive
                ? repo.findByTypeAndActiveTrueOrderBySortOrderAsc(type)
                : repo.findByTypeOrderBySortOrderAsc(type);

        return list.stream().map(this::toResponseDto).toList();
    }

    @Override
    public List<LookupValueResponseDto> getAllByType(String type) {
        List<LookupValue> list = repo.findByTypeOrderBySortOrderAsc(type);
        return list.stream().map(this::toResponseDto).toList();
    }

    @Override
    @Transactional
    public void updateActive(String id, boolean active) {
        LookupValue value = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Värde ej hittat"));

        value.setActive(active);
        toResponseDto(value);
    }

    @Override
    @Transactional
    public void updateSortOrder(String type, List<LookupSortUpdateDto> updates) {

        // Hämta alla värden av denna typ
        List<LookupValue> existingValues = repo.findByType(type);

        if (existingValues.isEmpty()) {
            throw new RuntimeException("No lookup values found for type: " + type);
        }

        // Lägg i map för snabb lookup
        Map<String, LookupValue> map = existingValues.stream()
                .collect(Collectors.toMap(LookupValue::getId, v -> v));

        // Validera inkommande ID:n
        for (LookupSortUpdateDto u : updates) {

            if (!map.containsKey(u.id())) {
                throw new RuntimeException("Invalid lookup ID in reorder request: " + u.id());
            }

            if (u.sortOrder() == null || u.sortOrder() < 1) {
                throw new RuntimeException("Sort order must be >= 1");
            }
        }

        // Uppdatera sortOrder
        updates.forEach(u -> {
            LookupValue val = map.get(u.id());
            val.setSortOrder(u.sortOrder());
        });

        // Spara ändringar
        repo.saveAll(existingValues);
    }


    private LookupValueResponseDto toResponseDto(LookupValue v) {
        return LookupValueResponseDto.builder()
                .id(v.getId())
                .type(v.getType())
                .value(v.getValue())
                .label(v.getLabel())
                .sortOrder(v.getSortOrder())
                .active(v.isActive())
                .build();
    }
}

