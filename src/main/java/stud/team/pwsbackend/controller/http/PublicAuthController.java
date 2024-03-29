package stud.team.pwsbackend.controller.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stud.team.pwsbackend.dto.LoginRequestDto;
import stud.team.pwsbackend.dto.NewUserDto;
import stud.team.pwsbackend.exception.user.UserNotFoundException;
import stud.team.pwsbackend.security.JwtTokenProvider;
import stud.team.pwsbackend.service.AuthService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(path = "api/v1/public/auth")
@Slf4j
public class PublicAuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PublicAuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //TODO логику вынести в из контроллера?
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse res) throws Exception {
        var userId = authService.login(loginRequestDto);
        var token = jwtTokenProvider.createToken(userId);


        ObjectNode json = objectMapper.createObjectNode();
        json.put("userId", userId);
        json.put("token", token);

        return ResponseEntity.ok(json);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody NewUserDto newUserDto, HttpServletResponse res) throws UserNotFoundException {
        var userIdOptional = authService.register(newUserDto);
        if (userIdOptional.isEmpty()) {
            return createBadRequest();
        }

        var userId = userIdOptional.get();
        var token = jwtTokenProvider.createToken(userId);

        ObjectNode json = objectMapper.createObjectNode();
        json.put("userId", userId);
        json.put("token", token);

        return ResponseEntity.ok(json);
    }

    private ResponseEntity<?> createBadRequest() {
        return ResponseEntity.badRequest()
                .build();
    }
}
