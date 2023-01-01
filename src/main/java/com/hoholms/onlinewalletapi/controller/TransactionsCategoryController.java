package com.hoholms.onlinewalletapi.controller;

import com.hoholms.onlinewalletapi.entity.dto.TransactionsCategoryDto;
import com.hoholms.onlinewalletapi.service.TransactionsCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class TransactionsCategoryController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsCategoryController.class);
    private final TransactionsCategoryService transactionsCategoryService;

    @GetMapping
    public String getCategories(Model model) {
        model.addAttribute("categories", transactionsCategoryService.findAllCategoriesOrderByIsIncome());

        return "categoryList";
    }

    @PostMapping
    public String addCategory(
            @Valid TransactionsCategoryDto categoryDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("categoryDto", categoryDto);
            logger.error("Category add error!");
        } else {
            transactionsCategoryService.addCategory(categoryDto);
            logger.info(String.format("Category \"%s\" added.", categoryDto.getCategory()));
        }

        model.addAttribute("categories", transactionsCategoryService.findAllCategoriesOrderByIsIncome());
        return "categoryList";
    }

    @GetMapping("{categoryID}")
    public String categoryEditForm(@PathVariable Long categoryID, Model model) {
        model.addAttribute("category", transactionsCategoryService.findById(categoryID));

        return "categoryEdit";
    }

    @PostMapping("/edit")
    public String updateCategory(
            @RequestParam Long id,
            @Valid TransactionsCategoryDto categoryDto,
            BindingResult bindingResult,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("category", categoryDto);
            model.addAttribute("category.id", id);
            logger.error("Category update error!");
            return "categoryEdit";
        } else {
            transactionsCategoryService.updateCategory(categoryDto);
            logger.info(String.format("Category \"%s\" added.", categoryDto.getCategory()));
        }

        return "redirect:/categories";
    }

    @GetMapping("delete/{categoryID}")
    public String deleteCategory(@PathVariable Long categoryID) {
        transactionsCategoryService.deleteCategoryById(categoryID);

        return "redirect:/categories";
    }
}
