package murkeev.service;

import lombok.AllArgsConstructor;
import murkeev.enums.SushiCategory;
import murkeev.model.Sushi;
import murkeev.repository.SushiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SushiService {
    private final SushiRepository sushiRepository;

    public List<Sushi> getAllSushi() {
        return sushiRepository.findAll();
    }

    public Optional<Sushi> getSushiById(Long id) {
        return sushiRepository.findById(id);
    }

    public List<Sushi> getSushiByCategory(SushiCategory category) {
        return sushiRepository.findByCategory(category);
    }

    public List<Sushi> createSushi(List<Sushi> sushiList) {
        return sushiRepository.saveAll(sushiList);
    }
}