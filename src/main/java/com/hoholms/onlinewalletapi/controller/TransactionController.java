package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.Transaction;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.TransactionDto;
import com.hoholms.onlinewalletapi.entity.dto.TransactionDtoConverter;
import com.hoholms.onlinewalletapi.entity.dto.TransactionWithIdDto;
import com.hoholms.onlinewalletapi.entity.dto.TransactionWithIdDtoConverter;
import com.hoholms.onlinewalletapi.service.ProfileService;
import com.hoholms.onlinewalletapi.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    private final ProfileService profileService;
    private final TransactionDtoConverter transactionDtoConverter;
    private final TransactionWithIdDtoConverter transactionWithIdDtoConverter;

    @GetMapping
    private ResponseEntity<List<TransactionWithIdDto>> getTransactions(@AuthenticationPrincipal User user) {
        logger.info("Dashboard transactions info call by user id {}", user.getId());
        Profile currentProfile = profileService.findProfileByUser(user);
        return new ResponseEntity<>(currentProfile.getTransactions()
                .stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate)
                        .thenComparing(Transaction::getId).reversed())
                .map(transactionWithIdDtoConverter::toDto)
                .toList(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> addTransaction(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid TransactionDto transactionDto,
            BindingResult bindingResult
    ) {
        Profile currentProfile = profileService.findProfileByUser(user);

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errorsMap, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = transactionDtoConverter.fromDto(transactionDto, currentProfile);
        transactionService.add(transaction, currentProfile);
        currentProfile.setBalance(profileService.getCalcBalance(currentProfile));
        profileService.calcBalance(user);

        logger.info("User by id {} added new transaction", user.getId());
        return new ResponseEntity<>(transactionWithIdDtoConverter.toDto(transaction), HttpStatus.OK);
    }

    @GetMapping("{transactionID}")
    public ResponseEntity<TransactionWithIdDto> transactionEditForm(
            @AuthenticationPrincipal User user,
            @PathVariable Long transactionID,
            Model model
    ) {
        logger.info("Call for transaction with id: {} info page", transactionID);

        Profile currentProfile = profileService.findProfileByUser(user);

        Transaction transaction = transactionService.findTransactionByIdAndProfile(transactionID, currentProfile);

        return new ResponseEntity<>(transactionWithIdDtoConverter.toDto(transaction), HttpStatus.OK);
    }

    @DeleteMapping("{transactionID}")
    public ResponseEntity<HttpStatus> transactionDelete(@AuthenticationPrincipal User user, @PathVariable Long transactionID) {
        transactionService.deleteTransactionById(transactionID, user);
        logger.info("Deleted transaction with id {} by user by id {}", transactionID, user.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("{transactionID}")
    public ResponseEntity<Object> transactionSave(
            @AuthenticationPrincipal User user,
            @PathVariable Long transactionID,
            @RequestBody @Valid TransactionDto transactionDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errorsMap, HttpStatus.BAD_REQUEST);
        } else {
            transactionService.save(user, transactionID, transactionDto);
            logger.info("Saved transaction by id {}", transactionID);
        }

        return new ResponseEntity<>(transactionWithIdDtoConverter.toDto(transactionService.findTransactionById(transactionID)), HttpStatus.OK);
    }
}
