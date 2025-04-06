package murkeev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import murkeev.dto.SushiRequest;
import murkeev.enums.SushiCategory;
import murkeev.exception.handles.ErrorResponse;
import murkeev.model.Sushi;
import murkeev.service.SushiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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


    @Operation(
            summary = "Create new sushi items",
            description = "This endpoint allows you to create a list of sushi items. " +
                    "The request body should contain sushi details like name, price, ingredients, and category."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created sushi items"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or validation errors",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<List<Sushi>> createSushi(
            @Parameter(description = "List of sushi details to be created")
            @RequestBody List<SushiRequest> sushiRequests) {

        log.info("Creating {} new sushi items", sushiRequests.size());
        List<Sushi> savedSushiList = sushiService.createSushi(sushiRequests);
        log.info("Successfully created {} sushi items", savedSushiList.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSushiList);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<SushiCategory>> getAllCategories() {
        List<SushiCategory> categories = Arrays.asList(SushiCategory.values());
        return ResponseEntity.ok(categories);
    }
}