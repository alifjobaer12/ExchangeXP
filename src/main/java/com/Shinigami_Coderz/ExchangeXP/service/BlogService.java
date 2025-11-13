package com.Shinigami_Coderz.ExchangeXP.service;

import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.BlogComment;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.repository.BlogRepo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogService {

    @Autowired
    private BlogRepo blogRepo;

    @Autowired
    private UserService userService;

    @Transactional
    public Blog saveNewBlog(Blog blog, String username){                             //  Create a Blog
        long start = System.currentTimeMillis(); // ADDED
        log.info("BlogService.saveNewBlog: Request to save new blog for username={}", username); // ADDED

        if (blog == null) {
            log.warn("BlogService.saveNewBlog: Blog payload is null for username={}", username); // ADDED
            return null; // ADDED
        }
        if (username == null || username.trim().isEmpty()) {
            log.warn("BlogService.saveNewBlog: username is null/empty."); // ADDED
            return null; // ADDED
        }

        try {
            User userByUsername = userService.findUserByUsername(username);
            if (userByUsername == null) {
                log.warn("BlogService.saveNewBlog: User not found username={}", username); // ADDED
                return null; // ADDED
            }

            blog.setBlogDate(LocalDateTime.now());
            blog.setUsername(username);
            Blog saveBlog = blogRepo.save(blog);

            if (userByUsername.getBlogs() == null) {
                userByUsername.setBlogs(Collections.singletonList(saveBlog)); // ADDED: ensure list
            } else {
                userByUsername.getBlogs().add(saveBlog);
            }

            userService.saveUser(userByUsername);

            log.info("BlogService.saveNewBlog: Blog saved blogId={} for username={} (elapsed={}ms)", saveBlog.getBlogId(), username, System.currentTimeMillis() - start); // ADDED
            return saveBlog;
        } catch (Exception e){
            log.error("BlogService.saveNewBlog: Exception while saving blog for username={}. error={}", username, e.getMessage(), e); // CHANGED (was System.out)
            return null;
        }
    }

    @Transactional
    public Blog saveBlog(Blog blog){                                                    //  Update a Blog
        long start = System.currentTimeMillis(); // ADDED
        log.info("BlogService.saveBlog: Request to save/update blogId={}", blog == null ? null : blog.getBlogId()); // ADDED

        if (blog == null) {
            log.warn("BlogService.saveBlog: Blog is null - nothing to save."); // ADDED
            return null; // ADDED
        }

        try {
            Blog save = blogRepo.save(blog);
            log.info("BlogService.saveBlog: Blog saved blogId={} (elapsed={}ms)", blog.getBlogId(), System.currentTimeMillis() - start); // ADDED
            return save;
        } catch (Exception e){
            log.error("BlogService.saveBlog: Exception while saving blogId={}. error={}", blog.getBlogId(), e.getMessage(), e); // CHANGED (was System.out)
            return null;
        }
    }

    public List<Blog> findAllBlog(){                                                   //  Find all Blogs
        log.debug("BlogService.findAllBlog: Fetching all blogs"); // ADDED
        try {
            List<Blog> result = blogRepo.findAll();
            log.info("BlogService.findAllBlog: Found {} blogs", result.size()); // ADDED
            return result; // ADDED
        } catch (Exception e) {
            log.error("BlogService.findAllBlog: Exception while fetching all blogs. error={}", e.getMessage(), e); // ADDED
            return Collections.emptyList(); // ADDED
        }
    }

    public Blog findUserBlogById(ObjectId id, String username){                            //  Find Blog by I'd
        log.debug("BlogService.findUserBlogById: Finding blogId={} for username={}", id, username); // ADDED
        if (id == null || username == null) { // ADDED
            log.warn("BlogService.findUserBlogById: blogId or username is null (blogId={}, username={})", id, username); // ADDED
            return null; // ADDED
        }

        try {
            User userByUsername = userService.findUserByUsername(username);
            if (userByUsername == null || userByUsername.getBlogs() == null) {
                log.warn("BlogService.findUserBlogById: No user or no blogs for username={}", username); // ADDED
                return null; // ADDED
            }

            boolean owned = userByUsername.getBlogs().stream().anyMatch(blog -> blog.getBlogId().equals(id));
            if (!owned) {
                log.warn("BlogService.findUserBlogById: BlogId={} does not belong to username={}", id, username); // ADDED
                return null; // ADDED
            }

            Optional<Blog> byId = blogRepo.findById(id);
            Blog blog = byId.orElse(null);
            if (blog == null) {
                log.warn("BlogService.findUserBlogById: Blog not found in repo blogId={}", id); // ADDED
            } else {
                log.info("BlogService.findUserBlogById: Found blogId={} for username={}", id, username); // ADDED
            }
            return blog;
        } catch (Exception e) {
            log.error("BlogService.findUserBlogById: Exception finding blogId={} for username={}. error={}", id, username, e.getMessage(), e); // ADDED
            return null; // ADDED
        }
    }

    public Blog findBlogById(ObjectId id){
        log.debug("BlogService.findBlogById: Finding blogId={}", id); // ADDED
        try {
            Optional<Blog> byId = blogRepo.findById(id);
            Blog blog = byId.orElse(null);
            if (blog == null) {
                log.warn("BlogService.findBlogById: Blog not found blogId={}", id); // ADDED
            } else {
                log.info("BlogService.findBlogById: Found blogId={}", id); // ADDED
            }
            return blog;
        } catch (Exception e) {
            log.error("BlogService.findBlogById: Exception while finding blogId={}. error={}", id, e.getMessage(), e); // ADDED
            return null; // ADDED
        }
    }

    @Transactional
    public boolean deleteBlogById(ObjectId id, String username){                      //  Delete Blog by Id
        long start = System.currentTimeMillis(); // ADDED
        log.info("BlogService.deleteBlogById: Request to delete blogId={} for username={}", id, username); // ADDED

        if (id == null || username == null) { // ADDED
            log.warn("BlogService.deleteBlogById: blogId or username is null (blogId={}, username={})", id, username); // ADDED
            return false; // ADDED
        }

        try {
            User userByUsername = userService.findUserByUsername(username);
            if (userByUsername == null || userByUsername.getBlogs() == null) {
                log.warn("BlogService.deleteBlogById: User not found or has no blogs username={}", username); // ADDED
                return false; // ADDED
            }

            boolean removed = userByUsername.getBlogs().removeIf(blog -> blog.getBlogId().equals(id));
            if (removed) {
                userService.saveUser(userByUsername);
                blogRepo.deleteById(id);
                log.info("BlogService.deleteBlogById: Deleted blogId={} for username={} (elapsed={}ms)", id, username, System.currentTimeMillis() - start); // ADDED
                return true;
            } else {
                log.warn("BlogService.deleteBlogById: Blog with id {} not found for user {}", id, username); // CHANGED (was throw)
                return false; // CHANGED
            }
        }  catch (Exception e){
            log.error("BlogService.deleteBlogById: Exception while deleting blogId={} for username={}. error={}", id, username, e.getMessage(), e); // CHANGED (was System.out)
            return false;
        }
    }

    public void deleteBlogById(ObjectId id){                                          //  Delete Blog by Id
        log.info("BlogService.deleteBlogById: Request to delete blogId={}", id); // ADDED
        try {
            blogRepo.deleteById(id);
            log.info("BlogService.deleteBlogById: Deleted blogId={}", id); // ADDED
        }  catch (Exception e){
            log.error("BlogService.deleteBlogById: Exception while deleting blogId={}. error={}", id, e.getMessage(), e); // CHANGED (was System.out)
        }
    }

    @Transactional
    public boolean deleteAllBlogAndUser(User user){
        long start = System.currentTimeMillis(); // ADDED
        log.info("BlogService.deleteAllBlogAndUser: Request to delete all blogs and userId={}", user == null ? null : user.getUserId()); // ADDED

        if (user == null) { // ADDED
            log.warn("BlogService.deleteAllBlogAndUser: User is null"); // ADDED
            return false; // ADDED
        }

        try {
            List<Blog> blogs = user.getBlogs();
            if (blogs != null) {
                for (Blog blog : blogs) {
                    deleteBlogById(blog.getBlogId());
                }
            }
            userService.deleteUserById(user.getUserId());
            log.info("BlogService.deleteAllBlogAndUser: Deleted all blogs and userId={} (elapsed={}ms)", user.getUserId(), System.currentTimeMillis() - start); // ADDED
            return true;
        } catch (Exception e){
            log.error("BlogService.deleteAllBlogAndUser: Exception while deleting all blogs for userId={}. error={}", user.getUserId(), e.getMessage(), e); // CHANGED (was System.out)
            return false;
        }
    }

    public List<BlogComment> findAllCommentByBlogId(ObjectId blogId) {
        log.debug("BlogService.findAllCommentByBlogId: Fetching comments for blogId={}", blogId); // ADDED
        Blog blogById = findBlogById(blogId);
        if (blogById == null) {
            log.warn("BlogService.findAllCommentByBlogId: Blog not found blogId={}", blogId); // ADDED
            return Collections.emptyList(); // ADDED
        }
        List<BlogComment> comments = blogById.getBlogComments();
        return comments == null ? Collections.emptyList() : comments; // ADDED
    }

}
