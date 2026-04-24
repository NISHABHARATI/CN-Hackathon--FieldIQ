package ai.creditnirvana.fieldiq.controller;

import ai.creditnirvana.fieldiq.dto.PrioritisedAccountDTO;
import ai.creditnirvana.fieldiq.entity.Account;
import ai.creditnirvana.fieldiq.repository.AccountRepository;
import ai.creditnirvana.fieldiq.service.AccountPrioritisationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountPrioritisationService prioritisationService;
    private final AccountRepository accountRepository;

    public AccountController(AccountPrioritisationService prioritisationService, AccountRepository accountRepository) {
        this.prioritisationService = prioritisationService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/prioritised")
    public ResponseEntity<List<PrioritisedAccountDTO>> getPrioritised(
            @RequestParam(required = false) String city) {
        return ResponseEntity.ok(prioritisationService.getPrioritisedAccounts(city));
    }

    @GetMapping("/{id}/priority-score")
    public ResponseEntity<PrioritisedAccountDTO> getScore(@PathVariable Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        return ResponseEntity.ok(prioritisationService.score(account));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        return accountRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts(
            @RequestParam(required = false) String city) {
        List<Account> accounts = city != null ? accountRepository.findByCity(city) : accountRepository.findAll();
        return ResponseEntity.ok(accounts);
    }
}