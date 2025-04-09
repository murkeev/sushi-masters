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
import org.springframework.web.multipart.MultipartFile;

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
    private final FileUploadService fileUploadService;

    public List<Sushi> getAllSushi() {
        return sushiRepository.findAll();
    }

    public Optional<Sushi> getSushiById(Long id) {
        return sushiRepository.findById(id);
    }

    public List<Sushi> getSushiByCategory(SushiCategory category) {
        return sushiRepository.findByCategory(category);
    }

    public Sushi createSushi(SushiRequest request, MultipartFile image) {
        String validationError = validateSushiRequest(request);
        if (!validationError.isEmpty()) {
            throw new SushiServiceException("Validation failed: " + validationError);
        }

        try {
            Sushi sushi = modelMapper.map(request, Sushi.class);
            sushi.setWeight(request.getWeight() + "г");

            String imageUrl = fileUploadService.uploadFile(image);
            sushi.setImage(imageUrl);

            return sushiRepository.save(sushi);
        } catch (RuntimeException e) {
            throw new SushiServiceException("Failed to create sushi item");
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