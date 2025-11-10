package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.BlogComment;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.BlogService;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        return authentication.getName();
    }

    @PostMapping("/post")                                               //  Create a Blog
    public ResponseEntity<?> postBlog(@RequestBody Blog blog) {
        long start = System.currentTimeMillis();

        String username = getAuthenticatedUsername();
        if (username == null) {
            log.warn("postBlog: Unauthenticated request rejected.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String title = blog.getBlogTitle().trim();
        String content = blog.getBlogContent().trim();
        if (title.isEmpty() || content.isEmpty()) {
            log.warn("postBlog: Validation failed for user={} - title or content empty.", username);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            boolean saved = blogService.saveNewBlog(blog, username);
            if (!saved) {
                log.error("postBlog: Saving blog failed for user={}", username);

                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            log.info("postBlog: Blog created successfully for user={} (title='{}'). elapsed={}ms", username, title, System.currentTimeMillis() - start);

            return new ResponseEntity<>(blog, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("postBlog: Exception while creating blog for user={}. error={}", username, e.getMessage(), e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/findAll")                                               //  Find all Blogs
    public ResponseEntity<?> findAllBlog(){
        long start = System.currentTimeMillis();

        String username = getAuthenticatedUsername();

        log.info("findAllBlog: Request to fetch all blogs for user={}", username);

        if (username == null) {
            log.warn("findAllBlog: Unauthenticated request rejected.");

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            User userByUsername = userService.findUserByUsername(username);
            if (userByUsername == null) {
                log.warn("findAllBlog: No user found with username={}", username);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            List<Blog> allBlog = userByUsername.getBlogs();
            log.info("findAllBlog: Returning {} blogs for user={} (elapsed={}ms)", allBlog == null ? 0 : allBlog.size(), username, System.currentTimeMillis() - start);
            return allBlog.isEmpty()
                    ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                    : new ResponseEntity<>(allBlog, HttpStatus.OK);
        } catch (Exception e) {
            log.error("findAllBlog: Exception fetching blogs for user={}. error={}", username, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/find/{blogId}")                                               //  Find Blog by Id
    public ResponseEntity<?> findBlogById(@PathVariable ObjectId blogId){
        long start = System.currentTimeMillis();

        String username = getAuthenticatedUsername();
        log.info("findBlogById: Request to fetch blogId={} by user={}", blogId, username);

        if (username == null) {
            log.warn("findBlogById: Unauthenticated request rejected.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            Blog blogById = blogService.findUserBlogById(blogId, username);
            if (blogById == null) {
                log.warn("findBlogById: Blog not found or does not belong to user={} (blogId={})", username, blogId);

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            log.info("findBlogById: Found blogId={} for user={} (elapsed={}ms)", blogId, username, System.currentTimeMillis() - start);

            return new ResponseEntity<>(blogById, HttpStatus.OK);
        } catch (Exception e) {
            log.error("findBlogById: Exception fetching blogId={} for user={}. error={}", blogId, username, e.getMessage(), e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{blogId}")                                          //  Delete Blog by Id
    public ResponseEntity<?> deleteBlogById(@PathVariable ObjectId blogId){
        long start = System.currentTimeMillis();

        String username = getAuthenticatedUsername();
        log.info("deleteBlogById: Request to delete blogId={} by user={}", blogId, username);

        if (username == null) {
            log.warn("deleteBlogById: Unauthenticated request rejected.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            Blog blogById = blogService.findUserBlogById(blogId, username);
            if (blogById == null) {
                log.warn("deleteBlogById: Blog not found or doesn't belong to user={} (blogId={})", username, blogId);

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            boolean deleted = blogService.deleteBlogById(blogId, username);
            if (!deleted) {
                log.error("deleteBlogById: Failed to delete blogId={} for user={}", blogId, username);

                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            log.info("deleteBlogById: Successfully deleted blogId={} for user={} (elapsed={}ms)", blogId, username, System.currentTimeMillis() - start);

            return new ResponseEntity<>(blogById, HttpStatus.OK);
        } catch (Exception e) {
            log.error("deleteBlogById: Exception deleting blogId={} for user={}. error={}", blogId, username, e.getMessage(), e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{blogId}")                                            //  Update Blog by Id
    public ResponseEntity<?> updateBlog(@PathVariable ObjectId blogId,
                                        @RequestBody Blog blog){
        long start = System.currentTimeMillis();

        String username = getAuthenticatedUsername();
        log.info("updateBlog: Request to update blogId={} by user={}", blogId, username);

        if (username == null) {
            log.warn("updateBlog: Unauthenticated request rejected.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            Blog blogById = blogService.findUserBlogById(blogId, username);
            if (blogById == null) {
                log.warn("updateBlog: Blog not found or not owned by user={} (blogId={})", username, blogId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            String newTitle = blog.getBlogTitle().trim();
            String newContent = blog.getBlogContent().trim();

            if (!newTitle.isEmpty()) {
                blogById.setBlogTitle(newTitle);
            }
            if (!newContent.isEmpty()) {
                blogById.setBlogContent(newContent);
            }

            blogService.saveBlog(blogById);
            log.info("updateBlog: Successfully updated blogId={} by user={} (elapsed={}ms)", blogId, username, System.currentTimeMillis() - start);

            return new ResponseEntity<>(blogById, HttpStatus.OK);
        } catch (Exception e) {
            log.error("updateBlog: Exception updating blogId={} for user={}. error={}", blogId, username, e.getMessage(), e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all-comments/{blogId}")                                            //  Find All Comments of a Blog by blogId
    public ResponseEntity<?> getAllCommentOfBlog(@PathVariable ObjectId blogId){
        long start = System.currentTimeMillis();

        log.info("getAllCommentOfBlog: Request to fetch comments for blogId={}", blogId);

        try {
            Blog blog = blogService.findBlogById(blogId);
            if (blog == null) {
                log.warn("getAllCommentOfBlog: Blog not found blogId={}", blogId);

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<BlogComment> allCommentByBlogId = blogService.findAllCommentByBlogId(blogId);
            log.info("getAllCommentOfBlog: Returning {} comments for blogId={} (elapsed={}ms)", allCommentByBlogId == null ? 0 : allCommentByBlogId.size(), blogId, System.currentTimeMillis() - start);

            return new ResponseEntity<>(allCommentByBlogId, HttpStatus.OK);
        } catch (Exception e) {
            log.error("getAllCommentOfBlog: Exception fetching comments for blogId={}. error={}", blogId, e.getMessage(), e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
