package murkeev.service;

import lombok.AllArgsConstructor;
import murkeev.repository.SushiRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SushiService {
    private final SushiRepository sushiRepository;
}
