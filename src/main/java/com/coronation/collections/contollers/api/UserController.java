package com.coronation.collections.contollers.api;

import com.coronation.collections.domain.MerchantUser;
import com.coronation.collections.domain.Role;
import com.coronation.collections.domain.User;
import com.coronation.collections.dto.PasswordDto;
import com.coronation.collections.repositories.predicate.CustomPredicateBuilder;
import com.coronation.collections.repositories.predicate.Operation;
import com.coronation.collections.security.ProfileDetails;
import com.coronation.collections.services.MerchantService;
import com.coronation.collections.services.RoleService;
import com.coronation.collections.services.UserService;
import com.coronation.collections.util.GenericUtil;
import com.coronation.collections.util.PageUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by Toyin on 4/11/19.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private UserService userService;
    private RoleService roleService;
    private MerchantService merchantService;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, RoleService roleService,
                          BCryptPasswordEncoder passwordEncoder, MerchantService merchantService) {
        this.userService = userService;
        this.roleService = roleService;
        this.merchantService = merchantService;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('CREATE_USER')")
    @PostMapping("/roles/{roleId}")
    public ResponseEntity<User> register(@PathVariable("roleId") Long roleId,
             @RequestBody @Valid User user, BindingResult bindingResult,
                 @AuthenticationPrincipal ProfileDetails profileDetails) {
        User admin = profileDetails.toUser();
        Role role = roleService.findById(roleId);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else if (role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else if (userService.findByEmail(user.getEmail()) != null ||
                userService.findByPhone(user.getPhone()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else if (GenericUtil.isMerchantUser(admin.getRole()) &&
                !GenericUtil.isMerchantUser(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            String password = null;
            if (GenericUtil.isStaffEmail(user.getEmail())) {
                user.setPassword(user.getEmail());
            } else {
                password = GenericUtil.generateRandomString(8);
                user.setPassword(password);
            }
            user.setRole(role);
            user = userService.save(user);
            return ResponseEntity.ok(user);
        }
    }

    @PreAuthorize("hasRole('EDIT_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<User> edit(@PathVariable("id") Long id,
                                     @RequestBody @Valid User newData, BindingResult bindingResult,
                                     @AuthenticationPrincipal ProfileDetails profileDetails) {
        User admin = profileDetails.toUser();
        User user = userService.findById(id);
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else if (user == null) {
            return ResponseEntity.notFound().build();
        } else if (GenericUtil.isMerchantUser(admin.getRole()) && !isMerchantUserAdmin(admin, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            try {
                user = userService.update(user, newData);
                return ResponseEntity.ok(user);
            } catch (DataIntegrityViolationException dve) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
    }

    @PreAuthorize("hasRole('DELETE_USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<User> delete(@PathVariable("id") Long id,
                                     @AuthenticationPrincipal ProfileDetails profileDetails) {
        User admin = profileDetails.toUser();
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else if (GenericUtil.isMerchantUser(admin.getRole()) && !isMerchantUserAdmin(admin, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            return ResponseEntity.ok(userService.delete(user));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ASSIGN_ROLE')")
    @PutMapping("/{id}/roles/{roleId}")
    public ResponseEntity<User> assignRole(@PathVariable("id") Long id, @PathVariable("roleId") Long roleId,
                                           @AuthenticationPrincipal ProfileDetails profileDetails) {
        User admin = profileDetails.toUser();
        User user = userService.findById(id);
        Role role = roleService.findById(roleId);
        if (user == null || role == null) {
            return ResponseEntity.notFound().build();
        } else if ((GenericUtil.isMerchantUser(admin.getRole()) && (!isMerchantUserAdmin(admin, user)
                || !GenericUtil.isMerchantUser(role)))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            return ResponseEntity.ok(userService.assignRole(user, role));
        }
    }

    @PostMapping("/email/{email}")
    public ResponseEntity<?> resetPassword(@PathVariable("email") String email) {
        if (GenericUtil.isStaffEmail(email)) {
            return ResponseEntity.unprocessableEntity().build();
        }
        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            String password = userService.resetPassword(user);
            return ResponseEntity.ok().build();
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<User> changePassword(@PathVariable("id") Long id,
                           BindingResult bindingResult, @RequestBody @Valid PasswordDto passwordDto) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            } else {
                if (passwordEncoder.matches(passwordDto.getCurrentPassword(), user.getPassword())) {
                    user = userService.changePassword(user, passwordDto);
                    return ResponseEntity.ok(user);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
    }

    @PreAuthorize("hasRole('VIEW_USERS')")
    @GetMapping
    public ResponseEntity<Page<User>> listUser(@RequestParam(value="page", required = false, defaultValue = "0") int page,
                                               @RequestParam(value="pageSize", defaultValue = "10") int pageSize, @RequestParam(value="firstname", required = false) String firstname,
                                               @RequestParam(value="lastname", required = false) String lastname, @RequestParam(value="email", required = false) String email,
                                               @RequestParam(value="phoneNumber", required = false) String phoneNumber,
                                               @RequestParam(value="role", required = false) Role role) {
        BooleanExpression filter = new CustomPredicateBuilder<>("user", User.class)
                .with("firstName", Operation.LIKE, firstname)
                .with("lastName", Operation.LIKE, lastname)
                .with("email", Operation.LIKE, email)
                .with("phone", Operation.LIKE, phoneNumber)
                .with("deleted", Operation.BOOLEAN, false)
                .with("role.name", Operation.STRING_EQUALS, role).build();
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize,
                        Sort.by(Sort.Order.asc("firstname"), Sort.Order.asc("lastname")));
        return ResponseEntity.ok(userService.listAll(filter, pageRequest));
    }

    private boolean isMerchantUserAdmin(User admin, User user) {
        MerchantUser merchantUserAdmin = merchantService.findByMerchantUserId(admin.getId());
        MerchantUser merchantUser = merchantService.findByMerchantUserId(user.getId());
        return (merchantUserAdmin != null && merchantUser != null && GenericUtil.isMerchantUser(user.getRole()) &&
                merchantUserAdmin.getMerchant().equals(merchantUser.getMerchant()));
    }
}
