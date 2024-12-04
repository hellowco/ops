package kr.co.proten.llmops.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "User", description = "User-related operations")
@RequestMapping("/user")
public class UserController {
    @GetMapping("/hello")
    @Operation(summary = "Public API", description = "This API does not require authentication.")
    public String exampleEndpoint() {
        return "Hello! This is example of no authentication.";
    }

    @GetMapping("/secure")
    @Operation(summary = "Private API", description = "This API requires authentication.",
            security = @SecurityRequirement(name = "Authorization"))
    public String privateApi() {
        return "Hello, Secure World!";
    }
}
