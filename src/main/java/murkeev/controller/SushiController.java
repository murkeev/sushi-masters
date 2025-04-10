package murkeev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import murkeev.dto.SushiRequest;
import murkeev.enums.SushiCategory;
import murkeev.exception.handles.ErrorResponse;
import murkeev.model.Sushi;
import murkeev.service.FileUploadService;
import murkeev.service.SushiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/sushi")
@Tag(name = "Sushi", description = "Operations related to sushi catalog and creation")
@RequiredArgsConstructor
public class SushiController {
    private final SushiService sushiService;

    @Operation(
            summary = "Get all sushi",
            description = "Returns a list of all available sushi items"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved sushi list",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Sushi.class))
            )
    )
    @GetMapping
    public ResponseEntity<List<Sushi>> getAllSushi() {
        return ResponseEntity.ok(sushiService.getAllSushi());
    }

    @Operation(
            summary = "Get sushi by ID",
            description = "Returns a single sushi item by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sushi found",
                    content = @Content(schema = @Schema(implementation = Sushi.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Sushi not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/id/{id}")
    public ResponseEntity<Sushi> getSushiById(
            @Parameter(description = "ID of sushi to retrieve", required = true)
            @PathVariable Long id) {
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

    @Operation(
            summary = "Get sushi by category",
            description = "Returns a list of sushi items filtered by category"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved filtered sushi list",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Sushi.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-category")
    public ResponseEntity<List<Sushi>> getSushiByCategory(
            @Parameter(description = "Category to filter by", required = true)
            @RequestParam SushiCategory category) {
        List<Sushi> sushiList = sushiService.getSushiByCategory(category);
        return ResponseEntity.ok(sushiList);
    }


    @Operation(
            summary = "Create new sushi item (admins only)",
            description = "This endpoint allows you to create a sushi item. " +
                    "The request body should contain sushi details like name, price, ingredients, and category."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created sushi item"),
            @ApiResponse(responseCode = "403", description = "Forbidden: You do not have permission to access this resource"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or validation errors",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Sushi> createSushi(
            @RequestPart("sushi") SushiRequest sushiRequest,
            @RequestPart("image") MultipartFile image
    ) {
        log.info("Creating new sushi item: {}", sushiRequest.getName());
        Sushi savedSushi = sushiService.createSushi(sushiRequest, image);
        log.info("Successfully created sushi: {}", savedSushi.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSushi);
    }

    @Operation(
            summary = "Get all sushi categories",
            description = "Returns a list of all available sushi categories"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved categories list",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = SushiCategory.class))
            )
    )
    @GetMapping("/categories")
    public ResponseEntity<List<SushiCategory>> getAllCategories() {
        List<SushiCategory> categories = Arrays.asList(SushiCategory.values());
        return ResponseEntity.ok(categories);
    }
}