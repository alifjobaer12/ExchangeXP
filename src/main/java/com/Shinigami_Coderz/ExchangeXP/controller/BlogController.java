package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.BlogService;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @PostMapping("/post/{username}")                                               //  Create a Blog
    public ResponseEntity<?> postBlog(@RequestBody Blog blog, @PathVariable String username) {
        if (blog.getBlogTitle().isEmpty() || blog.getBlogContent().isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return blogService.saveNewBlog(blog, username) ? new ResponseEntity<>(blog, HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/findAll/{username}")                                               //  Find all Blogs
    public ResponseEntity<?> findAllBlog(@PathVariable String username){
        User userByUsername = userService.findUserByUsername(username);
        List<Blog> allBlog = userByUsername.getBlogs();
        return allBlog.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(allBlog, HttpStatus.OK);
    }

    @GetMapping("/find/{username}/{id}")                                               //  Find Blog by Id
    public ResponseEntity<?> findBlogById(@PathVariable ObjectId id, @PathVariable String username){
        Blog blogById = blogService.findBlogById(id, username);
        return blogById == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(blogById, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{username}/{id}")                                          //  Delete Blog by Id
    public ResponseEntity<?> deleteBlogById(@PathVariable ObjectId id, @PathVariable String username){
        Blog blogById = blogService.findBlogById(id, username);
        if (blogById == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        boolean b = blogService.deleteBlogById(id, username);
        if (b) {
            return new ResponseEntity<>(blogById, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/update/{username}/{id}")                                            //  Update Blog by Id
    public ResponseEntity<?> updateBlog(@PathVariable ObjectId id, @RequestBody Blog blog, @PathVariable String username){
        Blog blogById = blogService.findBlogById(id, username);
        if (blogById == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        blogById.setBlogTitle(blog.getBlogTitle().isEmpty() ? blogById.getBlogTitle() : blog.getBlogTitle());
        blogById.setBlogContent(blog.getBlogContent().isEmpty() ? blogById.getBlogContent() : blog.getBlogContent());
        blogService.saveBlog(blogById);
        return new ResponseEntity<>(blogById, HttpStatus.OK);
    }


}
