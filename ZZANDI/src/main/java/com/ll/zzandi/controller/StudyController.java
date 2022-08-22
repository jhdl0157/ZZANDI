package com.ll.zzandi.controller;

import com.ll.zzandi.domain.Book;
import com.ll.zzandi.domain.Lecture;
import com.ll.zzandi.domain.Study;
import com.ll.zzandi.dto.BookDto;
import com.ll.zzandi.dto.LectureDto;
import com.ll.zzandi.dto.StudyDto;
import com.ll.zzandi.exception.StudyForm;
import com.ll.zzandi.service.BookService;
import com.ll.zzandi.service.LectureService;
import com.ll.zzandi.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final BookService bookService;
    private final LectureService lectureService;

    @GetMapping("/study/create")
    public String studyCreate(StudyForm studyForm) {
        return "study/studyForm";
    }

    @PostMapping("/study/create")
    public String studyCreate(@Valid StudyForm studyForm, BindingResult bindingResult, BookDto bookDto, LectureDto lectureDto) {
        if (bindingResult.hasErrors()) {
            return "study/studyForm";
        }
        studyService.save(studyForm);
        bookService.save(bookDto);
        lectureService.save(lectureDto);
        return "redirect:/";
    }

    @GetMapping("/study/list")
    public String list(Model model) {
        List<Study> studyList = studyService.findall();
        model.addAttribute("studyList", studyList);
        return "study/studyList";
    }

    @GetMapping("/study/detail/{studyId}")
    public String detail(Model model, @PathVariable Long studyId) {
        Study studies = studyService.findByid(studyId).get();
        Book books = bookService.findByid(studyId).get();
        Lecture lectures = lectureService.findById(studyId).get();
        model.addAttribute("studies", studies);
        model.addAttribute("books", books);
        model.addAttribute("lectures", lectures);
        return "study/studyDetail";
    }

    @GetMapping("/study/delete/{studyId}")
    public String delete(@PathVariable Long studyId) {
        Study studies = studyService.findByid(studyId).get();
        Book books = bookService.findByid(studyId).get();
        Lecture lectures = lectureService.findById(studyId).get();
        studyService.delete(studies);
        bookService.delete(books);
        lectureService.delete(lectures);
        return "redirect:/";
    }

    @GetMapping("/study/modify/{studyId}")
    public String modify(StudyForm studyForm) {
        return "study/studyModify";
    }

    @PostMapping("/study/modify/{studyId}")
    public String modify(@Valid StudyForm studyForm, BindingResult bindingResult, @PathVariable Long studyId, BookDto bookDto, LectureDto lectureDto) {
        if (bindingResult.hasErrors()) {
            return "study/studyModify";
        }
        studyService.modify(studyId, studyForm);
        bookService.modify(studyId, bookDto);
        lectureService.modify(studyId, lectureDto);
        return "redirect:/";
    }

}
