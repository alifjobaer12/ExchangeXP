package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.dto.BlogCommentReqDto;
import com.Shinigami_Coderz.ExchangeXP.dto.BlogCommentResDto;
import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.BlogComment;
import com.Shinigami_Coderz.ExchangeXP.service.BlogCommentService;
import com.Shinigami_Coderz.ExchangeXP.service.BlogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "Blog Comment APIs")
@RequestMapping("/comment")
public class BlogCommentController {

    @Autowired
    private BlogCommentService blogCommentService;

    @Autowired
    private BlogService blogService;

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return authentication.getName();
    }

    @PostMapping("/post/{blogID}")                                            //  Post a Comment on a Blog
    public ResponseEntity<?> addComment(@RequestBody BlogCommentReqDto request,
                                        @PathVariable String blogID){

        long start = System.currentTimeMillis();
        log.info("BlogCommentController.addComment: Received request to post a comment on blogId={}", blogID);

        ObjectId blogId = new ObjectId(blogID);

        String username = getAuthenticatedUsername();
        log.info("addComment: Request to addComment of this blogId={} for user={}", blogId, username);

        if (username == null) {
            log.warn("addComment: Unauthenticated request rejected.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            if (request.getComment().isEmpty()) {
                log.warn("BlogCommentController.addComment: Comment text is empty or null for blogId={}", blogId);

                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // DTO -> Entity
            BlogComment blogComment = new BlogComment();
            blogComment.setComment(request.getComment());

            BlogComment saved = blogCommentService.saveComment(blogComment, blogId);
            if (saved == null) {
                log.error("BlogCommentController.addComment: Failed to save comment on blogId={}", blogId);
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }

            // Entity -> DTO
            BlogCommentResDto response = new BlogCommentResDto(
                    saved.getBlogCommentId() != null ? saved.getBlogCommentId().toString() : null,
                    saved.getUser(),
                    saved.getComment(),
                    saved.getCommentAt(),
                    saved.getBlogId()
            );

            log.info("BlogCommentController.addComment: Comment added to blogId={} by user={} (elapsed={}ms)", blogId, blogComment.getUser(), System.currentTimeMillis() - start);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("BlogCommentController.addComment: Exception while saving comment for blogId={}. error={}", blogId, e.getMessage(), e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{commentID}")                             //  Update a Comment
    public ResponseEntity<?> updateComment(@RequestBody BlogCommentReqDto request,
                                           @PathVariable String commentID){

        long start = System.currentTimeMillis();
        log.info("BlogCommentController.updateComment: Received request to update commentId={}", commentID);

        ObjectId commentId = new ObjectId(commentID);

        String username = getAuthenticatedUsername();
        log.info("updateComment: Request to updateComment of this commentId={} for user={}", commentId, username);

        if (username == null) {
            log.warn("updateComment: Unauthenticated request rejected.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            BlogComment commentById = blogCommentService.findCommentById(commentId);
            if (commentById == null) {
                log.warn("BlogCommentController.updateComment: Comment with id {} not found.", commentId);

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (!username.equals(commentById.getUser())) {
                log.warn("BlogCommentController.updateComment: Unauthorized update attempt by {} on comment owned by {}", username, commentById.getUser());

                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else  {
                commentById.setComment(request.getComment().isEmpty() ? commentById.getComment() : request.getComment());
                BlogComment blogComment = blogCommentService.updateComment(commentById);

                // Entity -> DTO
                BlogCommentResDto response = new BlogCommentResDto(
                        blogComment.getBlogCommentId() != null ? blogComment.getBlogCommentId().toString() : null,
                        blogComment.getUser(),
                        blogComment.getComment(),
                        blogComment.getCommentAt(),
                        blogComment.getBlogId()
                );

                log.info("BlogCommentController.updateComment: Successfully updated commentId={} by user={} (elapsed={}ms)", commentId, username, System.currentTimeMillis() - start);

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("BlogCommentController.updateComment: Exception while updating commentId={}. error={}", commentId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/delete/{commentID}")                          //  Delete a Comment
    public ResponseEntity<?> deleteComment(@PathVariable String commentID){

        long start = System.currentTimeMillis();
        log.info("BlogCommentController.deleteComment: Received request to delete commentId={}", commentID);

        ObjectId commentId = new ObjectId(commentID);

        String username = getAuthenticatedUsername();
        log.info("deleteComment: Request to deleteComment of this commentId={} for user={}", commentId, username);

        if (username == null) {
            log.warn("deleteComment: Unauthenticated request rejected.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            BlogComment commentById = blogCommentService.findCommentById(commentId);
            if (commentById == null) {
                log.warn("BlogCommentController.deleteComment: Comment with id {} not found for deletion.", commentId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Blog blogById = blogService.findBlogById(commentById.getBlogId());
            if (blogById == null) {
                log.error("BlogCommentController.deleteComment: Related blog not found for commentId={}", commentId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (!username.equals(commentById.getUser()) && !blogById.getUsername().equals(username)) {
                log.warn("BlogCommentController.deleteComment: Unauthorized delete attempt by {}. Comment owner={} Blog owner={}",
                        username, commentById.getUser(), blogById.getUsername());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                boolean success = blogCommentService.deleteCommentById(blogById.getBlogId(), commentId);
                if (!success) {
                    log.error("BlogCommentController.deleteComment: Failed to delete commentId={}", commentId);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

                // Entity -> DTO
                BlogCommentResDto response = new BlogCommentResDto(
                        commentById.getBlogCommentId() != null ? commentById.getBlogCommentId().toString() : null,
                        commentById.getUser(),
                        commentById.getComment(),
                        commentById.getCommentAt(),
                        commentById.getBlogId()
                );

                log.info("BlogCommentController.deleteComment: Successfully deleted commentId={} by user={} (elapsed={}ms)",
                        commentId, username, System.currentTimeMillis() - start);
                return new ResponseEntity<>(commentById, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("BlogCommentController.deleteComment: Exception while deleting commentId={}. error={}", commentId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
