package ru.itgirl.checklistproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itgirl.checklistproject.model.dto.*;
import ru.itgirl.checklistproject.model.service.FormService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    @PostMapping("form/create")
    FormDto createForm(@RequestBody FormCreateDto formCreateDto) {
        return formService.createForm(formCreateDto);
    }

    @PutMapping("form/update")
    FormDto createForm(@RequestBody FormUpdateDto formUpdateDto) {
        return formService.updateForm(formUpdateDto);
    }

    @PutMapping("form/updateAnswId")
    FormDto createForm(@RequestBody FormUpdateDtoAnswId formUpdateDto) {
        return formService.updateFormAnswId(formUpdateDto);
    }

    @GetMapping("/forms")
    List<FormDto> getFormsView() {
        return formService.getAllForms();
    }

    @GetMapping("/form/{id}")
    FormDto getFormsView(@PathVariable("id") Long id) {
        return formService.getFormById(id);
    }

    @GetMapping("/form/token")
    FormDto getFormsByTokenAdmin(@RequestParam("token") String token) {
        return formService.getFormByToken(token);
    }

    @GetMapping("/form/tokenUser")
    FormDtoUser getFormsByTokenUser(@RequestParam("token") String token) {
        return formService.getFormByTokenUser(token);
    }

    @DeleteMapping("/form/delete/{id}")
    void deleteForm(@PathVariable("id") Long id) {
        formService.deleteForm(id);
    }
}
