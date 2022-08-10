package com.ll.zzandi.controller;

import com.ll.zzandi.dto.BoardListDto;
import com.ll.zzandi.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public String boardListPaging(Model model, Pageable pageable, @RequestParam(defaultValue = "0") int page) {
        Page<BoardListDto> boardList = boardService.boardListPaging(pageable, page);
        model.addAttribute("boardList", boardList); // 게시물 데이터
        model.addAttribute("totalPage", boardList.getTotalPages()); // 총 페이지 수
        model.addAttribute("currentPage", boardList.getNumber()); // 현재 페이지 번호
        return "board/boardList";
    }
}
