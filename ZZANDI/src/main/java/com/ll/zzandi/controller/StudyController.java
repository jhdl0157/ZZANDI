package com.ll.zzandi.controller;

import com.ll.zzandi.domain.*;
import com.ll.zzandi.dto.BookDto;
import com.ll.zzandi.dto.BookInfoDto;
import com.ll.zzandi.dto.LectureDto;
import com.ll.zzandi.dto.StudyDto;

import com.ll.zzandi.dto.api.SearchDto;
import com.ll.zzandi.service.BoardService;
import com.ll.zzandi.service.BookService;
import com.ll.zzandi.service.LectureService;
import com.ll.zzandi.service.StudyService;

import com.ll.zzandi.service.TeamMateService;
import com.ll.zzandi.service.UserService;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StudyController {
    @Value("${aladin.key}")
    private  String TTB_KEY;

    @Value("${aladin.searchUrl}")
    private  String SEARCH_URL;

    @Value("${aladin.detailUrl}")
    private  String DETAIL_URL;


    private final StudyService studyService;
    private final BookService bookService;
    private final LectureService lectureService;
    private final TeamMateService teamMateService;
    private final UserService userService;

    @GetMapping("/study/create")
    public String createStudy(StudyDto studyDto) {
        return "study/studyForm";
    }

    @PostMapping("/study/create")
    public String createStudy(@Valid StudyDto studyDto, BindingResult bindingResult, BookDto bookDto, LectureDto lectureDto) {
        if (bindingResult.hasErrors()) {
            return "study/studyForm";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal(); // 현재 로그인 한 유저 정보
        Study study = null;
        if (studyDto.getStudyType().equals("BOOK")) {
            RestTemplate restTemplate = new RestTemplate();
            URI targetUrl = UriComponentsBuilder
                    .fromHttpUrl(DETAIL_URL)
                    .queryParam("ItemId", bookDto.getBookIsbn())
                    .queryParam("ttbkey", TTB_KEY)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            BookInfoDto bookInfoDto = restTemplate.getForEntity(targetUrl, BookInfoDto.class).getBody();
            Book book = bookService.save(bookInfoDto);
            study = studyService.createStudyWithBook(studyDto, book, user);
        } else if (studyDto.getStudyType().equals("LECTURE")) {
            Lecture lecture = lectureService.save(lectureDto);
            study = studyService.createStudyWithLecture(studyDto, lecture, user);
        }
        teamMateService.createTeamMate(user, study.getId());
        return "redirect:/";
    }

    @GetMapping("/study/list")
    public String studyList(Model model,@RequestParam(defaultValue = "ALL") String st,@RequestParam(defaultValue = "ALL") String ss, @RequestParam(defaultValue = "") String kw) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        List<Study> studyList = studyService.getList(st, ss, kw);



        model.addAttribute("studyList", studyList);
        model.addAttribute("user", user);
        return "study/studyList";
    }

    @GetMapping("/study/detail/{studyId}")
    public String detailStudy(@AuthenticationPrincipal User user, Model model, @PathVariable Long studyId) {
        Study studies = studyService.findByStudyId(studyId).orElseThrow(null);
        Book books = studies.getBook();
        Lecture lectures = studies.getLecture();
        if (books != null) {
            books = bookService.findByid(books.getId()).orElseThrow(null);
        } else if (lectures != null) {
            lectures = lectureService.findById(lectures.getId()).orElseThrow(null);
        }

        boolean participation = userService.participation(studies.getTeamMateList(), user);
        model.addAttribute("studies", studies);
        model.addAttribute("books", books);
        model.addAttribute("lectures", lectures);
        model.addAttribute("user", user);
        model.addAttribute("participation", participation);
        return "study/studyDetail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/study/delete/{studyId}")
    public String deleteStudy(@PathVariable Long studyId, Principal principal) {
        Study studies = studyService.findByStudyId(studyId).orElseThrow(null);

        if (!studies.getUser().getUserId().equals(principal.getName().split(",")[1].substring(8, principal.getName().split(",")[1].length()))) {
            return "study/studyError";
        }
        studyService.deleteStudy(studies);
        return "redirect:/";
    }

    @GetMapping("/study/update/{studyId}")
    public String updateStudyForm(@PathVariable Long studyId, Model model, StudyDto studyDto) {

        Study studies = studyService.findByStudyId(studyId).orElseThrow(null);
        Book books = studies.getBook();
        Lecture lectures = studies.getLecture();
        StudyDto newStudyDto = studyService.saveNewStudyDto(studyId, studyDto);
        model.addAttribute("studies", studies);
        model.addAttribute("lectures", lectures);
        model.addAttribute("books", books);
        model.addAttribute("studyDto", newStudyDto);
        return "study/studyUpdate";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/study/update/{studyId}")
    public String updateStudy(@Valid StudyDto studyDto, BindingResult bindingResult, @PathVariable Long studyId, BookDto bookDto, LectureDto lectureDto, Principal principal, Model model) {

        if (bindingResult.hasErrors()) {
            return "study/studyUpdate";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal(); // 현재 로그인 한 유저 정보
        Study studies = studyService.findByStudyId(studyId).orElseThrow(null);
        System.out.println("principal.getName() = " + principal.getName());
        if (!studies.getUser().getUserId().equals(principal.getName().split(",")[1].substring(8, principal.getName().split(",")[1].length()))) {
            return "study/studyError";
        }
        if (studyDto.getStudyType().equals("BOOK")) {
            RestTemplate restTemplate = new RestTemplate();
            URI targetUrl = UriComponentsBuilder
                    .fromHttpUrl(DETAIL_URL)
                    .queryParam("ItemId", bookDto.getBookIsbn())
                    .queryParam("ttbkey", TTB_KEY)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            BookInfoDto bookInfoDto = restTemplate.getForEntity(targetUrl, BookInfoDto.class).getBody();
            studyService.updateStudyWithBook(studyId, studyDto, bookInfoDto, user);
        } else if (studyDto.getStudyType().equals("LECTURE")) {
            studyService.updateStudyWithLecture(studyId, studyDto, lectureDto, user);
        }

        return "redirect:/";
    }

    @GetMapping("/study/search/book")
    @ResponseBody
    public SearchDto searchBook(@RequestParam("query")String bookKeyword){
        RestTemplate restTemplate = new RestTemplate();
        URI targetUrl = UriComponentsBuilder
                .fromHttpUrl(SEARCH_URL)
                .queryParam("Query", bookKeyword)
                .queryParam("ttbkey", TTB_KEY)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        try {
            SearchDto dtoResponseEntity = restTemplate.getForEntity(targetUrl, SearchDto.class).getBody();
            System.out.println(dtoResponseEntity.getItem().get(0).getTitle());
            return dtoResponseEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
