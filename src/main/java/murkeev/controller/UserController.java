package murkeev.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import murkeev.dto.UserDTO;
import murkeev.model.User;
import murkeev.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "Operations related to the authenticated user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @Operation(
            summary = "Get current authenticated user",
            description = "Returns details of the currently authenticated user."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
    )
    @GetMapping
    public ResponseEntity<UserDTO> getAuthenticatedUser() {
        User user = userService.getAuthenticatedUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ResponseEntity.ok(userDTO);
    }
}
