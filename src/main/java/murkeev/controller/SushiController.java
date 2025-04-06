package murkeev.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import murkeev.enums.SushiCategory;
import murkeev.model.Sushi;
import murkeev.service.SushiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/sushi")
@RequiredArgsConstructor
public class SushiController {
    private final SushiService sushiService;

    @GetMapping
    public ResponseEntity<List<Sushi>> getAllSushi() {
        return ResponseEntity.ok(sushiService.getAllSushi());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Sushi> getSushiById(@PathVariable Long id) {
        log.info("Getting sushi by id: {}", id);
        return sushiService.getSushiById(id)
                .map(sushi -> {
                    log.info("Retrieved sushi with id {}: {}", id, sushi.getName());
                    return ResponseEntity.ok(sushi);
                })
                .orElseGet(() -> {
                    log.warn("Sushi with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<Sushi>> getSushiByCategory(@RequestParam SushiCategory category) {
        List<Sushi> sushiList = sushiService.getSushiByCategory(category);
        return ResponseEntity.ok(sushiList);
    }

    @PostMapping
    public ResponseEntity<List<Sushi>> createSushi(@RequestBody List<Sushi> sushiList) {
        log.info("Creating {} new sushi items", sushiList.size());
        List<Sushi> savedSushiList = sushiService.createSushi(sushiList);
        log.info("Successfully created {} sushi items", savedSushiList.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSushiList);
    }
}