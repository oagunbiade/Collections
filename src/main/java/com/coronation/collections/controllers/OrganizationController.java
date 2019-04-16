package com.coronation.collections.controllers;

import com.coronation.collections.domain.Organization;
import com.coronation.collections.domain.OrganizationUser;
import com.coronation.collections.domain.User;
import com.coronation.collections.repositories.predicate.CustomPredicateBuilder;
import com.coronation.collections.repositories.predicate.Operation;
import com.coronation.collections.services.OrganizationService;
import com.coronation.collections.services.UserService;
import com.coronation.collections.util.PageUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * Created by Toyin on 4/12/19.
 */
@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {
    private OrganizationService organizationService;
    private UserService userService;

    public OrganizationController(OrganizationService organizationService, UserService userService) {
        this.organizationService = organizationService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('CREATE_ORGANIZATION')")
    @PostMapping
    public ResponseEntity<Organization> create(@RequestBody @Valid Organization organization,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else if (organizationService.findByName(organization.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.ok(organizationService.save(organization));
        }
    }

    @PreAuthorize("hasRole('EDIT_ORGANIZATION')")
    @PutMapping("/{id}")
    public ResponseEntity<Organization> edit(@PathVariable("id") Long id,
            @RequestBody @Valid Organization organization, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Organization previous = organizationService.findById(id);
            if (previous == null) {
                return ResponseEntity.notFound().build();
            } else {
                try {
                   return ResponseEntity.ok(organizationService.update(previous, organization));
                } catch (DataIntegrityViolationException dve) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
        }
    }

    @PreAuthorize("hasRole('DELETE_ORGANIZATION')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Organization> delete(@PathVariable("id") Long id) {
        Organization organization = organizationService.findById(id);
        if (organization == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(organizationService.delete(organization));
        }
    }

    @PreAuthorize("hasRole('ACTIVATE_ORGANIZATION')")
    @PostMapping("/{id}/status")
    public ResponseEntity<Organization> activateOrDeactivate(@PathVariable("id") Long id) {
        Organization organization = organizationService.findById(id);
        if (organization == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(organizationService.deactivateOrActivate(organization));
        }
    }

    @PreAuthorize("hasRole('VIEW_ORGANIZATIONS')")
    @GetMapping
    public ResponseEntity<Page<Organization>> listOrganizations(@RequestParam(value="page",
            required = false, defaultValue = "0") int page,
                                                @RequestParam(value="pageSize", defaultValue = "10") int pageSize,
                                                @RequestParam(value="name", required = false) String name) {
        BooleanExpression filter = new CustomPredicateBuilder<>("organization", Organization.class)
                .with("name", Operation.LIKE, name).build();
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize,
                        Sort.by(Sort.Order.asc("name")));
        return ResponseEntity.ok(organizationService.listAll(filter, pageRequest));
    }

    @PreAuthorize("hasRole('ADD_ORGANIZATION_USER')")
    @PostMapping("/{id}/users/{userId}")
    public ResponseEntity<OrganizationUser> addUser(@PathVariable("id") Long id,
                                                    @PathVariable("userId") Long userId) {
        Organization organization = organizationService.findById(id);
        User user = userService.findById(userId);
        if (organization == null || user == null) {
            return ResponseEntity.notFound().build();
        } else {
            OrganizationUser organizationUser = organizationService.findByUserId(userId);
            if (organizationUser == null) {
                return ResponseEntity.ok(organizationService.addUser(organization, user));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<OrganizationUser>> getUsers(@PathVariable("id") Long id) {
        Organization organization = organizationService.findById(id);
        if (organization == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(organizationService.organizationUsers(id));
        }
    }
}
