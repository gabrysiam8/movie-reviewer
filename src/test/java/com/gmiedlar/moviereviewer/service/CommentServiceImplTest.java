package com.gmiedlar.moviereviewer.service;

import java.util.Optional;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.gmiedlar.moviereviewer.common.TestData.COMMENT;
import static com.gmiedlar.moviereviewer.common.TestData.COMMENT_ID;
import static com.gmiedlar.moviereviewer.common.TestData.ENABLED_USER;
import static com.gmiedlar.moviereviewer.common.TestData.UNIQUE_USERNAME;
import static com.gmiedlar.moviereviewer.common.TestData.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository repository;

    @Mock
    private UserFinderService userFinderService;

    private CommentService commentService;

    private Comment comment;

    @BeforeEach
    public void setUp() {
        commentService = new CommentServiceImpl(repository, userFinderService);

        comment = Comment.builder()
                         .rating(5)
                         .text("Awesome")
                         .build();
    }

    @Test
    public void shouldAddComment() {
        //given
        given(userFinderService.findUserByUsername(anyString())).willReturn(ENABLED_USER);
        given(repository.save(any(Comment.class))).willReturn(COMMENT);

        //when
        Comment result = commentService.addComment(UNIQUE_USERNAME, comment);

        //then
        verify(repository, times(1)).save(any(Comment.class));
        assertNotNull(result);
        assertEquals(COMMENT, result);
    }

    @Test
    public void shouldNotAddCommentWhenUsernameNotFoundException() {
        //given
        given(userFinderService.findUserByUsername(anyString())).willThrow(UsernameNotFoundException.class);

        assertThrows(
            UsernameNotFoundException.class,
            //when
            () -> commentService.addComment(UNIQUE_USERNAME, comment)
        );

        //then
        verify(repository, never()).save(any(Comment.class));
    }

    @Test
    public void shouldGetCommentById() {
        //given
        given(repository.findById(COMMENT_ID)).willReturn(Optional.ofNullable(COMMENT));

        //when
        Comment result = commentService.getCommentById(COMMENT_ID);

        //then
        verify(repository, times(1)).findById(anyString());
        assertNotNull(result);
        assertEquals(COMMENT, result);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenInvalidCommentId() {
        //given
        String invalidCommentId = "invalidId-1234";
        given(repository.findById(invalidCommentId)).willReturn(Optional.empty());

        //when
        Throwable exception = assertThrows(
            IllegalArgumentException.class,
            //when
            () -> commentService.getCommentById(invalidCommentId)
        );

        //then
        verify(repository, times(1)).findById(anyString());
        assertEquals("No comment with that id exists!", exception.getMessage());
    }

    @Test
    public void shouldUpdateComment() {
        //given
        comment.setText("Updated comment");
        Comment commentUpdate = Comment.builder()
                                       .id(COMMENT_ID)
                                       .rating(5)
                                       .text("Updated comment")
                                       .authorId(USER_ID)
                                       .build();

        given(repository.findById(COMMENT_ID)).willReturn(Optional.ofNullable(COMMENT));
        given(repository.save(any(Comment.class))).willReturn(commentUpdate);

        //when
        Comment result = commentService.updateComment(COMMENT_ID, comment);

        //then
        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any(Comment.class));
        assertNotNull(result);
        assertEquals(commentUpdate, result);
    }

    @Test
    public void shouldDeleteComment() {
        //given
        given(repository.existsById(COMMENT_ID)).willReturn(true);

        //when
        String result = commentService.deleteComment(COMMENT_ID);

        //then
        verify(repository, times(1)).existsById(anyString());
        verify(repository, times(1)).deleteById(anyString());
        assertNotNull(result);
        assertEquals(result, "Comment successfully deleted");
    }
}