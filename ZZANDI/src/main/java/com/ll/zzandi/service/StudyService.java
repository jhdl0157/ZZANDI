package com.ll.zzandi.service;

import com.ll.zzandi.domain.*;
import com.ll.zzandi.dto.BookDto;
import com.ll.zzandi.dto.LectureDto;
import com.ll.zzandi.enumtype.StudyStatus;
import com.ll.zzandi.enumtype.StudyType;
import com.ll.zzandi.exception.StudyForm;
import com.ll.zzandi.repository.BoardRepository;
import com.ll.zzandi.repository.BookRepository;
import com.ll.zzandi.repository.CommentRepository;
import com.ll.zzandi.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final BookService bookService;
    private final LectureService lectureService;
    private final StudyRepository studyRepository;
    private final BookRepository bookRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    public void createStudyWithBook(@Valid StudyForm studyform, Book book , User user) {
        Study study = new Study(user , studyform.getStudyTitle(), book, null, StudyType.BOOK,
            studyform.getStudyStart(),
            studyform.getStudyEnd(), studyform.getStudyPeople(), studyform.getStudyTag(), 0,
            StudyStatus.RECRUIT);
        studyRepository.save(study);
    }

    public void createStudyWithLecture(@Valid StudyForm studyform, Lecture lecture , User user) {
        Study study = new Study(user , studyform.getStudyTitle(), null, lecture, StudyType.LECTURE,
            studyform.getStudyStart(),
            studyform.getStudyEnd(), studyform.getStudyPeople(), studyform.getStudyTag(), 0,
            StudyStatus.RECRUIT);
        studyRepository.save(study);
    }

    public List<Study> findAll() {
        return studyRepository.findAll();
    }

    public Optional<Study> findByStudyId(Long studyId) {
        return studyRepository.findById(studyId);
    }

    @Transactional
    public void deleteStudy(Study studies) {
        List<Board> boardList = boardRepository.findBoardListByStudyId(studies.getId());
        for (Board board : boardList) {
            commentRepository.deleteCommentByBoardId(board.getId());
        }
        boardRepository.deleteBoardByStudyId(studies.getId());
        studyRepository.delete(studies);
    }

    public void updateStudyWithBook(Long studyId, @Valid StudyForm studyform, BookDto bookDto , User user) {
        Study s1 = studyRepository.findById(studyId).orElseThrow(null);
        Book book;
        if (s1.getBook() != null) {
            book = bookService.modify(s1.getBook().getId(), bookDto);
        } else {
            book = bookService.save(bookDto);
        }

        Lecture lecture = s1.getLecture();
        s1.setUser(user);
        s1.setId(studyId);
        s1.setStudyTitle(studyform.getStudyTitle());
        s1.setBook(book);
        s1.setLecture(null);
        s1.setStudyType(StudyType.BOOK);
        s1.setStudyStart(studyform.getStudyStart());
        s1.setStudyEnd(studyform.getStudyEnd());
        s1.setStudyPeople(studyform.getStudyPeople());
        s1.setStudyTag(studyform.getStudyTag());
        studyRepository.save(s1);

        if (lecture != null) {
            lectureService.delete(lecture);
        }
    }

    public void updateStudyWithLecture(Long studyId, StudyForm studyform, LectureDto lectureDto, User user) {
        Study s1 = studyRepository.findById(studyId).orElseThrow(null);
        Lecture lecture;
        if (s1.getLecture() != null) {
            lecture = lectureService.modify(s1.getLecture().getId(), lectureDto);
        } else {
            lecture = lectureService.save(lectureDto);
        }

        Book book = s1.getBook();
        s1.setUser(user);
        s1.setId(studyId);
        s1.setStudyTitle(studyform.getStudyTitle());
        s1.setBook(null);
        s1.setLecture(lecture);
        s1.setStudyType(StudyType.LECTURE);
        s1.setStudyStart(studyform.getStudyStart());
        s1.setStudyEnd(studyform.getStudyEnd());
        s1.setStudyPeople(studyform.getStudyPeople());
        s1.setStudyTag(studyform.getStudyTag());
        studyRepository.save(s1);

        if (book != null) {
            bookRepository.delete(book);
        }
    }
}
