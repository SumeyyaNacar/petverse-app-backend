package com.petverse.controller.user;

import com.petverse.payload.ResponseMessage;
import com.petverse.payload.request.abstracts.AbstractUserRequest;
import com.petverse.payload.request.user.UserRequest;
import com.petverse.payload.request.user.UserRequestWithoutPassword;
import com.petverse.payload.response.user.UserResponse;
import com.petverse.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    // ---------- GET ----------
    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER')")
    public ResponseMessage<UserResponse> getUserProfile(Principal principal){
        return userService.getUserProfile(principal);
    }
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','OWNER')")
    public ResponseMessage<UserResponse> getUserId(@PathVariable UUID  userId){
        return userService.getUserById(userId);

    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getOwnersByPage(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type) {

        Page<UserResponse> usersPage = userService.getOwnersByPage(query, page, size, sort, type);
        return ResponseEntity.ok(usersPage);
    }

    // ---------- PUT ----------
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<UserResponse> updateUserFully(@RequestBody UserRequest userRequest,
                                                         @PathVariable UUID userId){
        return userService.updateUserProfile(userRequest, userId);
    }

    // ---------- POST ----------
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<String> createUser(@RequestBody @Valid UserRequest userRequest,
                                              String roleType) {
        return userService.createUser(userRequest, roleType);
    }

    // ---------- PATCH ----------
    // !!! Kullanicinin kendisini update etmesini saglayan method
    @PatchMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','OWNER')")
    public ResponseMessage<String> updateCurrentUserPartially(
            @RequestBody UserRequestWithoutPassword userRequestWithoutPassword) {
        return userService.updateUserForUsers(userRequestWithoutPassword);
    }

    // ---------- DELETE ----------
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<String> deleteUser(@PathVariable UUID userId) {
        return userService.deleteUser(userId);
    }




}
