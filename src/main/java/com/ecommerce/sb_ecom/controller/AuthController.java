package com.ecommerce.sb_ecom.controller;

import com.ecommerce.sb_ecom.model.AppRole;
import com.ecommerce.sb_ecom.model.Role;
import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.repositories.RoleRepository;
import com.ecommerce.sb_ecom.repositories.UserRepository;
import com.ecommerce.sb_ecom.security.jwt.JwtUtils;
import com.ecommerce.sb_ecom.security.request.LoginRequest;
import com.ecommerce.sb_ecom.security.request.SignupRequest;
import com.ecommerce.sb_ecom.security.response.MessageResponse;
import com.ecommerce.sb_ecom.security.response.UserInfoResponse;
import com.ecommerce.sb_ecom.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.beans.Encoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/Api/Auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(), roles, jwtCookie.toString());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,jwtCookie.toString()).body(response);
    }

    @PostMapping("/signup")

    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){

        if (userRepository.ExistsByUserName(signupRequest.getUsername())){

            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if(userRepository.ExistsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null || strRoles.isEmpty()){
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(()-> new RuntimeException("Error: Role is not found"));

            roles.add(userRole);
        } else{

            strRoles.forEach(Role -> {
                switch (Role){
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(adminRole);

                        break;



                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(sellerRole);

                        break;


                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found"));

                        roles.add(userRole);

                }


            });

        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered succesfully!"));

    }

    @GetMapping("/username")

    public String currentUserName(Authentication authentication){

        if(authentication != null)
            return authentication.getName();
        else
            return "";


    }

    @GetMapping("/user")

    public ResponseEntity<?> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> Roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toList();

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), Roles);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/signout")

    public ResponseEntity<?> signoutUser(){

        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(new MessageResponse("You've been signed out!"));
    }




}
