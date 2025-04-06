package murkeev.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import murkeev.dto.SushiRequest;
import murkeev.enums.SushiCategory;
import murkeev.exception.SushiServiceException;
import murkeev.model.Sushi;
import murkeev.repository.SushiRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SushiService {
    private final SushiRepository sushiRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;

    public List<Sushi> getAllSushi() {
        return sushiRepository.findAll();
    }

    public Optional<Sushi> getSushiById(Long id) {
        return sushiRepository.findById(id);
    }

    public List<Sushi> getSushiByCategory(SushiCategory category) {
        return sushiRepository.findByCategory(category);
    }

    public List<Sushi> createSushi(List<SushiRequest> sushiRequests) {
        List<String> validationErrors = sushiRequests.stream()
                .map(this::validateSushiRequest)
                .filter(error -> !error.isEmpty())
                .collect(Collectors.toList());

        if (!validationErrors.isEmpty()) {
            throw new SushiServiceException("Validation failed: " + String.join(", ", validationErrors));
        }

        try {
            List<Sushi> sushiList = sushiRequests.stream()
                    .map(request -> {
                        Sushi sushi = modelMapper.map(request, Sushi.class);
                        sushi.setWeight(request.getWeight() + "г");
                        return sushi;
                    })
                    .collect(Collectors.toList());

            return sushiRepository.saveAll(sushiList);
        } catch (RuntimeException e) {
            throw new SushiServiceException("Failed to create sushi items");
        }
    }

    private String validateSushiRequest(SushiRequest sushiRequest) {
        Set<ConstraintViolation<SushiRequest>> violations = validator.validate(sushiRequest);

        if (!violations.isEmpty()) {
            return violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
        }

        return "";
    }
}