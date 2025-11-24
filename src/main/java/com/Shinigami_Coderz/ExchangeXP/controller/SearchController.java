package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.entity.Blog;
import com.Shinigami_Coderz.ExchangeXP.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@RestController
@Tag(name = "Search APIs")
@RequestMapping("/search")
public class SearchController {

    private final MongoTemplate mongoTemplate;

    public SearchController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


//    * Example: GET /search/users?q=alif
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam(value = "q", required = false, defaultValue = "") String q) {

        long start = System.currentTimeMillis();
        log.info("SearchController.searchUsers: Received request to search users. q='{}'", q);

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || authentication instanceof AnonymousAuthenticationToken) {
                log.warn("SearchController.searchUsers: Unauthenticated request.");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            // sanitize input
            if (q == null) q = "";
            q = q.trim().replaceAll("[,]+$", "").replaceAll("\\s+", " ");

            Query query = new Query();
            if (!q.isEmpty()) {
                String regexWrapped = ".*" + Pattern.quote(q) + ".*";
                Pattern pattern = Pattern.compile(regexWrapped, Pattern.CASE_INSENSITIVE);

                Criteria c1 = Criteria.where("username").regex(pattern);
                Criteria c2 = Criteria.where("email").regex(pattern);

                query.addCriteria(new Criteria().orOperator(c1, c2));

                log.debug("SearchUsers: cleaned q='{}', regex='{}', query={}", q, regexWrapped, query);
            }

            List<User> found = mongoTemplate.find(query, User.class);
            found.forEach(u -> u.setPassword("null"));

            if (found.isEmpty()) {
                log.warn("SearchController.searchUsers: No users found for query='{}'. (elapsed={}ms)",
                        q, System.currentTimeMillis() - start);
            } else {
                log.info("SearchController.searchUsers: Found {} users. (elapsed={}ms)",
                        found.size(), System.currentTimeMillis() - start);
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("query", q);
            resp.put("total", found.size());
            resp.put("results", found);

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            log.error("SearchController.searchUsers: Exception occurred. error={} (elapsed={}ms)",
                    e.getMessage(), System.currentTimeMillis() - start, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    * Example: GET /search/blogs?q=java
    @GetMapping("/blogs")
    public ResponseEntity<Map<String, Object>> searchBlogs(
            @RequestParam(value = "q", required = false, defaultValue = "") String q) {

        long start = System.currentTimeMillis();
        log.info("SearchController.searchBlogs: Received request to search blogs. q='{}'", q);

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || authentication instanceof AnonymousAuthenticationToken) {
                log.warn("SearchController.searchBlogs: Unauthenticated request.");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (q == null) q = "";
            q = q.trim().replaceAll("[,]+$", "").replaceAll("\\s+", " ");

            Query query = new Query();
            if (!q.isEmpty()) {
                String regexWrapped = ".*" + Pattern.quote(q) + ".*";
                Pattern pattern = Pattern.compile(regexWrapped, Pattern.CASE_INSENSITIVE);

                Criteria c1 = Criteria.where("blogTitle").regex(pattern);
                Criteria c2 = Criteria.where("blogContent").regex(pattern);

                query.addCriteria(new Criteria().orOperator(c1, c2));

                log.debug("SearchBlogs: cleaned q='{}', regex='{}', query={}", q, regexWrapped, query);
            }

            List<Blog> found = mongoTemplate.find(query, Blog.class);

            if (found.isEmpty()) {
                log.warn("SearchController.searchBlogs: No blogs found for query='{}'. (elapsed={}ms)",
                        q, System.currentTimeMillis() - start);
            } else {
                log.info("SearchController.searchBlogs: Found {} blogs. (elapsed={}ms)",
                        found.size(), System.currentTimeMillis() - start);
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("query", q);
            resp.put("total", found.size());
            resp.put("results", found);

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            log.error("SearchController.searchBlogs: Exception occurred. error={} (elapsed={}ms)",
                    e.getMessage(), System.currentTimeMillis() - start, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

