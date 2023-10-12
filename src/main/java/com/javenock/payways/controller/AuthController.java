package com.javenock.payways.controller;


import com.javenock.payways.model.ERole;
import com.javenock.payways.model.Role;
import com.javenock.payways.model.User;
import com.javenock.payways.repository.RoleRepository;
import com.javenock.payways.repository.UserRepository;
import com.javenock.payways.request.LoginRequest;
import com.javenock.payways.request.SignupRequest;
import com.javenock.payways.response.JwtResponse;
import com.javenock.payways.response.MessageResponse;
import com.javenock.payways.security.JwtUtils;
import com.javenock.payways.service.UserDetailsImpl;
import com.javenock.payways.service.UserDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    AuthenticationManager authenticationManager;


    UserRepository userRepository;


    RoleRepository roleRepository;


    PasswordEncoder encoder;


    JwtUtils jwtUtils;

    UserDetailsServiceImpl userDetailsServiceImpl;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils,
                          UserDetailsServiceImpl userDetailsServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(userDetails.getId(),userDetails.getUsername(),jwt,roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .build();
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userCustomerRole = roleRepository.findByName(ERole.ROLE_JUDGEONE)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userCustomerRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "judgeone":
                        Role judgeoneRole = roleRepository.findByName(ERole.ROLE_JUDGEONE)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(judgeoneRole);

                        break;

                    case "judgetwo":
                        Role judgetwoRole = roleRepository.findByName(ERole.ROLE_JUDGETWO)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(judgetwoRole);

                        break;

                    case "judgethree":
                        Role judgethreeRole = roleRepository.findByName(ERole.ROLE_JUDGETHREE)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(judgethreeRole);

                        break;


                    default:
                        Role joneRole = roleRepository.findByName(ERole.ROLE_JUDGEONE)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found." + strRoles));
                        roles.add(joneRole);

                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("registered successfully!"));
    }

    @GetMapping("/validate")
    public boolean detValidate(@RequestParam String token){
        return jwtUtils.validateJwtToken(token);
    }

}
