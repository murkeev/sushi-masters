package murkeev.controller;

import lombok.RequiredArgsConstructor;
import murkeev.enums.SushiCategory;
import murkeev.model.Sushi;
import murkeev.service.SushiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return sushiService.getSushiById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<Sushi>> getSushiByCategory(@RequestParam SushiCategory category) {
        List<Sushi> sushiList = sushiService.getSushiByCategory(category);
        return ResponseEntity.ok(sushiList);
    }

    @PostMapping
    public ResponseEntity<List<Sushi>> createSushi(@RequestBody List<Sushi> sushiList) {
        List<Sushi> savedSushiList = sushiService.createSushi(sushiList);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSushiList);
    }
}