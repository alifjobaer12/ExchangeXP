package com.Shinigami_Coderz.ExchangeXP.service;

import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.BlogComment;
import com.Shinigami_Coderz.ExchangeXP.repository.BlogCommentRepo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BlogCommentService {

    @Autowired
    private BlogCommentRepo blogCommentRepo;

    @Autowired
    private  BlogService blogService;

    public BlogComment findCommentById(ObjectId id){                                       //  Find a Comment
        log.debug("BlogCommentService.findCommentById: Searching for commentId={}", id);
        Optional<BlogComment> blogComment = blogCommentRepo.findById(id);
        BlogComment result = blogComment.orElse(null);
        if (result == null) {
            log.debug("BlogCommentService.findCommentById: Comment not found commentId={}", id);
        } else {
            log.debug("BlogCommentService.findCommentById: Found commentId={}", id);
        }
        return result;
    }

    @Transactional
    public boolean saveComment(BlogComment comment, ObjectId blogId) {                   // Save a Comment
        long start = System.currentTimeMillis();
        log.info("BlogCommentService.saveComment: Request to save comment for blogId={}", blogId);

        if (comment == null) {
            log.warn("BlogCommentService.saveComment: Comment is null for blogId={}", blogId);
            return false;
        }

        try {
            Blog blogById = blogService.findBlogById(blogId);
            if (blogById == null) {
                log.warn("BlogCommentService.saveComment: Blog not found blogId={}", blogId);
                return false;
            }

            // Null-safe and trim comment text
            String text = comment.getComment().trim();
            if (text.isEmpty()) {
                log.warn("BlogCommentService.saveComment: Comment text empty for blogId={}", blogId);
                return false;
            }
            comment.setComment(text);

            comment.setCommentAt(LocalDateTime.now());
            comment.setBlogId(blogId);
            BlogComment save = blogCommentRepo.save(comment);
            blogById.getBlogComments().add(save);
            blogService.saveBlog(blogById);

            log.info("BlogCommentService.saveComment: Comment saved commentId={} for blogId={} (elapsed={}ms)",
                    save.getBlogCommentId(), blogId, System.currentTimeMillis() - start);
            return true;
        } catch (Exception e){
            log.error("BlogCommentService.saveComment: Exception while saving comment for blogId={}. error={}", blogId, e.getMessage(), e); // CHANGED (was System.out)
            return false;
        }
    }

    public void updateComment(BlogComment comment) {                                    // Update a Comment
        if (comment == null) {
            log.warn("BlogCommentService.updateComment: Received null comment update request.");
            return;
        }
        try {
            // trim and validate comment text before save
            String text = comment.getComment().trim();
            if (text.isEmpty()) {
                log.warn("BlogCommentService.updateComment: Comment text empty for commentId={}", comment.getBlogCommentId());
                return;
            }
            comment.setComment(text);
            blogCommentRepo.save(comment);
            log.info("BlogCommentService.updateComment: Updated commentId={}", comment.getBlogCommentId());
        } catch (Exception e) {
            log.error("BlogCommentService.updateComment: Exception while updating commentId={}. error={}", comment.getBlogCommentId(), e.getMessage(), e);
        }
    }

    @Transactional
    public boolean deleteCommentById(ObjectId blogId, ObjectId commentId) {             // Delete a Comment
        long start = System.currentTimeMillis();
        log.info("BlogCommentService.deleteCommentById: Request to delete commentId={} from blogId={}", commentId, blogId);

        try {
            BlogComment commentById = findCommentById(commentId);
            Blog blogById = blogService.findBlogById(blogId);

            if (commentById == null) {
                log.warn("BlogCommentService.deleteCommentById: Comment not found commentId={}", commentId);
                return false;
            }
            if (blogById == null) {
                log.warn("BlogCommentService.deleteCommentById: Blog not found blogId={}", blogId);
                return false;
            }

            boolean removed = blogById.getBlogComments().removeIf(comment -> comment.getBlogCommentId().equals(commentId));
            if (!removed) {
                log.warn("BlogCommentService.deleteCommentById: Comment removal from blog failed commentId={}", commentId);
                return false;
            }

            blogService.saveBlog(blogById);
            blogCommentRepo.deleteById(commentId);

            log.info("BlogCommentService.deleteCommentById: Deleted commentId={} from blogId={} (elapsed={}ms)", commentId, blogId, System.currentTimeMillis() - start);
            return  true;
        } catch (Exception e){
            log.error("BlogCommentService.deleteCommentById: Exception while deleting commentId={} from blogId={}. error={}", commentId, blogId, e.getMessage(), e);
            return false;
        }
    }


}
