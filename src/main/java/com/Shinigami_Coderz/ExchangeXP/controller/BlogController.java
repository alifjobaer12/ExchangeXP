package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.dto.BlogReqDto;
import com.Shinigami_Coderz.ExchangeXP.dto.BlogResDto;
import com.Shinigami_Coderz.ExchangeXP.dto.UserResDto;
import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.BlogComment;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.BlogService;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Blog APIs")
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
    public ResponseEntity<?> postBlog(@RequestBody BlogReqDto request) {
        long start = System.currentTimeMillis();

        String username = getAuthenticatedUsername();
        if (username == null) {
            log.warn("postBlog: Unauthenticated request rejected.");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String title = request.getBlogTitle().trim();
        String content = request.getBlogContent().trim();
        if (title.isEmpty() || content.isEmpty()) {
            log.warn("postBlog: Validation failed for user={} - title or content empty.", username);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // DTO -> Entity
        Blog blog = new Blog();
        blog.setBlogTitle(title);
        blog.setBlogContent(content);

        try {
            Blog saved = blogService.saveNewBlog(blog, username);
            if (saved == null) {
                log.error("postBlog: Saving blog failed for user={}", username);

                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Entity -> DTO
            BlogResDto response = new BlogResDto(
                    saved.getBlogId() != null ? saved.getBlogId().toString() : null,
                    saved.getBlogTitle(),
                    saved.getBlogContent(),
                    saved.getBlogDate(),
                    saved.getUsername()
            );

            log.info("postBlog: Blog created successfully for user={} (title='{}'). elapsed={}ms", username, title, System.currentTimeMillis() - start);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
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

    @GetMapping("/find/{blogID}")                                               //  Find Blog by Id
    public ResponseEntity<?> findBlogById(@PathVariable String blogID){
        long start = System.currentTimeMillis();

        String username = getAuthenticatedUsername();
        log.info("findBlogById: Request to fetch blogId={} by user={}", blogID, username);

        ObjectId blogId = new ObjectId(blogID);

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

    @DeleteMapping("/delete/{blogID}")                                          //  Delete Blog by Id
    public ResponseEntity<?> deleteBlogById(@PathVariable String blogID){
        long start = System.currentTimeMillis();

        String username = getAuthenticatedUsername();
        log.info("deleteBlogById: Request to delete blogID={} by user={}", blogID, username);

        ObjectId blogId = new ObjectId(blogID);

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

            // Entity -> DTO
            BlogResDto response = new BlogResDto(
                    blogById.getBlogId() != null ? blogById.getBlogId().toString() : null,
                    blogById.getBlogTitle(),
                    blogById.getBlogContent(),
                    blogById.getBlogDate(),
                    blogById.getUsername()
            );

            log.info("deleteBlogById: Successfully deleted blogId={} for user={} (elapsed={}ms)", blogId, username, System.currentTimeMillis() - start);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("deleteBlogById: Exception deleting blogId={} for user={}. error={}", blogId, username, e.getMessage(), e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{blogID}")                                            //  Update Blog by Id
    public ResponseEntity<?> updateBlog(@PathVariable String blogID,
                                        @RequestBody BlogReqDto request){
        long start = System.currentTimeMillis();

        String username = getAuthenticatedUsername();
        log.info("updateBlog: Request to update blogID={} by user={}", blogID, username);

        ObjectId blogId = new ObjectId(blogID);

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

            String newTitle = request.getBlogTitle().trim();
            String newContent = request.getBlogContent().trim();

            if (!newTitle.isEmpty()) {
                blogById.setBlogTitle(newTitle);
            }
            if (!newContent.isEmpty()) {
                blogById.setBlogContent(newContent);
            }

            Blog saved = blogService.saveBlog(blogById);

            // Entity -> DTO
            BlogResDto response = new BlogResDto(
                    saved.getBlogId() != null ? saved.getBlogId().toString() : null,
                    saved.getBlogTitle(),
                    saved.getBlogContent(),
                    saved.getBlogDate(),
                    saved.getUsername()
            );

            log.info("updateBlog: Successfully updated blogId={} by user={} (elapsed={}ms)", blogId, username, System.currentTimeMillis() - start);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("updateBlog: Exception updating blogId={} for user={}. error={}", blogId, username, e.getMessage(), e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all-comments/{blogID}")                                            //  Find All Comments of a Blog by blogID
    public ResponseEntity<?> getAllCommentOfBlog(@PathVariable String blogID){
        long start = System.currentTimeMillis();

        log.info("getAllCommentOfBlog: Request to fetch comments for blogID={}", blogID);

        ObjectId blogId = new ObjectId(blogID);

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
