package br.com.mrzoom.restwithspringbootandjava.controllers;

import br.com.mrzoom.restwithspringbootandjava.data.vo.v1.security.AccountCredentialsVO;
import br.com.mrzoom.restwithspringbootandjava.services.AuthServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoint", description = "Endpoint for managing authentication.")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthServices authServices;

    @SuppressWarnings("rawtypes")
    @Operation(summary = "Authenticates a user and returns a token")
    @PostMapping(value = "/signin")
    public ResponseEntity signin(@RequestBody AccountCredentialsVO data) {
        if (authServices.checkIfParamsIsNotNull(data)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        var token = authServices.signin(data);
        if (token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        return token;
    }

    @SuppressWarnings("rawtypes")
    @Operation(summary = "Refresh token for authenticated user and returns a token")
    @PutMapping(value = "/refresh/{username}")
    public ResponseEntity refreshToken(@PathVariable("username") String username,
                                       @RequestHeader("Authorization") String refreshToken) {
        if (authServices.checkIfParamsIsNotNull(username, refreshToken)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        var token = authServices.refreshToken(username, refreshToken);
        if (token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request");
        return token;
    }
}
