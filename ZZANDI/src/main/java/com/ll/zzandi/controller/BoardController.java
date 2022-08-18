package com.ll.zzandi.controller;

import com.ll.zzandi.domain.Board;
import com.ll.zzandi.domain.User;
import com.ll.zzandi.dto.BoardDetailDto;
import com.ll.zzandi.dto.BoardListDto;
import com.ll.zzandi.dto.BoardUpdateFormDto;
import com.ll.zzandi.dto.BoardWriteDto;
import com.ll.zzandi.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public String boardListPaging() {
        return "board/boardList";
    }

    @GetMapping("/list-json")
    @ResponseBody
    public Page<BoardListDto> boardListPagingToJson(Pageable pageable, @RequestParam(defaultValue = "0") int page) {
        return boardService.boardListPaging(pageable, page);
    }

    @GetMapping("/detail/{id}")
    public String boardDetail(Model model, @PathVariable Long id) {
        boardService.updateView(id);
        BoardDetailDto boardDetail = boardService.boardDetail(id);
        model.addAttribute("boardDetail", boardDetail);
        return "board/boardDetail";
    }

    @GetMapping("/write")
    public String boardWriteForm(Model model) {
        model.addAttribute("boardWriteDto", new BoardWriteDto());
        return "board/boardWriteForm";
    }

    @PostMapping("/write")
    public String boardWrite(@Valid BoardWriteDto boardWriteDto, BindingResult result) {
        System.out.println(boardWriteDto.getCategory());
        // @NotBlank 값이 없는 경우 BindingResult로 처리!
        if (result.hasErrors()) {
            return "/board/boardWriteForm";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal(); // 현재 로그인 한 유저 정보

        Board board = new Board(user, boardWriteDto.getCategory(), boardWriteDto.getTitle(), boardWriteDto.getContent(), 0, 0);
        Long saveId = boardService.save(board);

        return "redirect:/board/detail/" + saveId;
    }

    @GetMapping("/update/{id}")
    public String boardUpdateForm(@PathVariable Long id, Model model) {
        BoardUpdateFormDto updateFormDto = boardService.boardUpdateForm(id);
        model.addAttribute("updateDto", updateFormDto);
        return "board/boardUpdateForm";
    }

    @PostMapping("/update/{id}")
    public String boardUpdate(@PathVariable Long id, BoardUpdateFormDto updateFormDto) {
        boardService.boardUpdate(id, updateFormDto);
        return "redirect:/board/detail/" + id;
    }

    @PostMapping("/delete/{id}")
    public String boardDelete(@PathVariable Long id) {
        boardService.boardDelete(id);
        return "redirect:/board/list";
    }

}
