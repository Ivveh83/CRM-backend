package ivar.hogblom.crmbackend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ivar.hogblom.crmbackend.dto.UserEntityDto;
import ivar.hogblom.crmbackend.dto.UserEntityRegistrationDto;
import ivar.hogblom.crmbackend.service.UserEntityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Validated
@Tag(name = "User API", description = "API endpoints for managing User")
public class UserEntityController {

UserEntityService userEntityService;
@Autowired
UserEntityController(UserEntityService userEntityService) {
    this.userEntityService = userEntityService;
}

@PostMapping("/register")
@ResponseStatus(HttpStatus.CREATED) // 201 Created
    public UserEntityDto register(
            @Valid
            @RequestBody
            @NotNull(message = "Person cannot be null")
            UserEntityRegistrationDto userEntityRegistrationDto) {
    System.out.println("Registering UserEntity: " + userEntityRegistrationDto);
    return userEntityService.create(userEntityRegistrationDto);
}
}
