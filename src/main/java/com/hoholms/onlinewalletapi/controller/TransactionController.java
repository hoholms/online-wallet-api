package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.Profile;
import com.hoholms.onlinewalletapi.entity.Transaction;
import com.hoholms.onlinewalletapi.entity.User;
import com.hoholms.onlinewalletapi.entity.dto.TransactionDto;
import com.hoholms.onlinewalletapi.service.ProfileService;
import com.hoholms.onlinewalletapi.service.TransactionService;
import com.hoholms.onlinewalletapi.service.TransactionsCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    private final TransactionsCategoryService categoryService;
    private final ProfileService profileService;

    @GetMapping("{transactionID}")
    public String transactionEditForm(
            @AuthenticationPrincipal User user,
            @PathVariable Long transactionID,
            Model model
    ) {
        logger.info("Call for transaction with id: {} edit page", transactionID);

        Profile currentProfile = profileService.findProfileByUser(user);

        Transaction transaction = transactionService.findTransactionByIdAndProfile(transactionID, currentProfile);

        model.addAttribute("transactionEdit", transaction);
        model.addAttribute("id", transaction.getId());
        model.addAttribute("categories",
                categoryService.findAllCategoriesByTransactionIdByIsIncome(transactionID));

        return "transactionEdit";
    }

    @GetMapping("/delete/{transactionID}")
    public String transactionDelete(@AuthenticationPrincipal User user, @PathVariable Long transactionID) {
        transactionService.deleteTransactionById(transactionID, user);
        logger.info("Deleted transaction with id: {}", transactionID);

        return "redirect:/dashboard";
    }

    @PostMapping()
    public String transactionSave(
            @AuthenticationPrincipal User user,
            @RequestParam Long id,
            @Valid TransactionDto transactionDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("transactionEdit", transactionDto);
            model.addAttribute("id", id);
            model.addAttribute("categories",
                    categoryService.findAllCategoriesByTransactionIdByIsIncome(id));
            return "transactionEdit";
        } else {
            transactionService.save(user, id, transactionDto);
            logger.info("Saved transaction by id {}", id);
        }

        return "redirect:/dashboard";
    }
}
