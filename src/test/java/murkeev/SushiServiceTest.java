package murkeev;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import murkeev.dto.SushiRequest;
import murkeev.exception.SushiServiceException;
import murkeev.model.Sushi;
import murkeev.repository.SushiRepository;
import murkeev.service.FileUploadService;
import murkeev.service.SushiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SushiServiceTest {
    @Mock
    SushiRepository sushiRepository;
    @Mock
    ModelMapper modelMapper;
    @Mock
    Validator validator;
    @Mock
    FileUploadService fileUploadService;

    @InjectMocks
    SushiService sushiService;

    @Test
    void getAllSushi_ShouldReturnList() {
        List<Sushi> sushiList = List.of(new Sushi(), new Sushi());
        when(sushiRepository.findAll()).thenReturn(sushiList);
        List<Sushi> result = sushiService.getAllSushi();
        assertEquals(2, result.size());
    }

    @Test
    void createSushi_ValidRequest_ShouldReturnSavedSushi() {
        SushiRequest request = new SushiRequest();
        request.setName("TestSushi");
        request.setWeight("150");
        MultipartFile image = mock(MultipartFile.class);

        Sushi sushi = new Sushi();
        sushi.setWeight("150г");

        when(validator.validate(request)).thenReturn(Collections.emptySet());
        when(modelMapper.map(request, Sushi.class)).thenReturn(sushi);
        when(fileUploadService.uploadFile(image)).thenReturn("image.jpg");
        when(sushiRepository.save(any(Sushi.class))).thenReturn(sushi);

        Sushi result = sushiService.createSushi(request, image);
        assertEquals("150г", result.getWeight());
        verify(sushiRepository).save(sushi);
    }

    @Test
    void createSushi_InvalidRequest_ShouldThrowException() {
        SushiRequest request = new SushiRequest();
        MultipartFile image = mock(MultipartFile.class);
        @SuppressWarnings("unchecked")
        ConstraintViolation<SushiRequest> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(path.toString()).thenReturn("name");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");

        Set<ConstraintViolation<SushiRequest>> violations = Set.of(violation);
        when(validator.validate(request)).thenReturn(violations);

        SushiServiceException exception = assertThrows(SushiServiceException.class,
                () -> sushiService.createSushi(request, image));

        assertTrue(exception.getMessage().contains("Validation failed"));
    }
}