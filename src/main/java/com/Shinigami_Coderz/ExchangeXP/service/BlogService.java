package com.Shinigami_Coderz.ExchangeXP.service;

import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.repository.BlogRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BlogService {

    @Autowired
    private BlogRepo blogRepo;

    @Autowired
    private UserService userService;

    @Transactional
    public boolean saveNewBlog(Blog blog, String username){                                                  //  Create a Blog
        try {
            User userByUsername = userService.findUserByUsername(username);
            blog.setBlogDate(LocalDateTime.now());
            blog.setUsername(username);
            Blog saveBlog = blogRepo.save(blog);
            userByUsername.getBlogs().add(saveBlog);
            userService.saveUser(userByUsername);
            return true;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Transactional
    public void saveBlog(Blog blog){                                                    //  Update a Blog
        try {
            blogRepo.save(blog);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public List<Blog> findAllBlog(){                                                   //  Find all Blogs
        return blogRepo.findAll();
    }

    public Blog findBlogById(ObjectId id, String username){                                             //  Find Blog by I'd
        User userByUsername = userService.findUserByUsername(username);
        List<Blog> collect = userByUsername.getBlogs().stream().filter(blog -> blog.getBlogId().equals(id)).collect(Collectors.toList());
        if (collect.isEmpty()) return null;
        Optional<Blog> byId = blogRepo.findById(id);
        return byId.orElse(null);
    }

    @Transactional
    public boolean deleteBlogById(ObjectId id, String username){                                          //  Delete Blog by Id
        try {
            User userByUsername = userService.findUserByUsername(username);
            boolean b = userByUsername.getBlogs().removeIf(blog -> blog.getBlogId().equals(id));
            if (b) {
                blogRepo.deleteById(id);
                return true;
            }
            else throw new RuntimeException("Journal Entry with id " + id + " not found");
        }  catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void deleteBlogById(ObjectId id){                                          //  Delete Blog by Id
        try {
            blogRepo.deleteById(id);
        }  catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Transactional
    public boolean deleteAllBlogFromUser(User user){
        try {
            List<Blog> blogs = user.getBlogs();
            for (Blog blog : blogs) {
                deleteBlogById(blog.getBlogId());
            }
            userService.deleteUserById(user.getUserId());
            return true;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
}
