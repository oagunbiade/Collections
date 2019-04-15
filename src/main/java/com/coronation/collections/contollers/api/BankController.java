package com.coronation.collections.contollers.api;

import com.coronation.collections.domain.Bank;
import com.coronation.collections.repositories.predicate.CustomPredicateBuilder;
import com.coronation.collections.repositories.predicate.Operation;
import com.coronation.collections.services.BankService;
import com.coronation.collections.util.PageUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by Toyin on 4/11/19.
 */
@RestController
@RequestMapping("/api/v1/banks")
public class BankController {
    private BankService bankService;

    @Autowired
    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PreAuthorize("hasRole('CREATE_BANK')")
    @PostMapping
    public ResponseEntity<Bank> create(@RequestBody @Valid Bank bank, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else if (bankService.findByName(bank.getName()) != null ||
                bankService.findByCode(bank.getBankCode()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.ok(bankService.create(bank));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bank> edit(@PathVariable("id") Long id,
                                     @RequestBody @Valid Bank bank, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Bank previous = bankService.findById(id);
            if (previous == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(bankService.edit(previous, bank));
            }
        }
    }

    @GetMapping
    public ResponseEntity<Page<Bank>> listBanks(@RequestParam(value="page",
            required = false, defaultValue = "0") int page,
                 @RequestParam(value="pageSize", defaultValue = "10") int pageSize,
                 @RequestParam(value="name", required = false) String name,
                 @RequestParam(value="code", required = false) String code) {
        BooleanExpression filter = new CustomPredicateBuilder<>("bank", Bank.class)
                .with("name", Operation.LIKE, name)
                .with("code", Operation.LIKE, code).build();
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize,
                        Sort.by(Sort.Order.asc("name")));
        return ResponseEntity.ok(bankService.findAll(filter, pageRequest));
    }
}
