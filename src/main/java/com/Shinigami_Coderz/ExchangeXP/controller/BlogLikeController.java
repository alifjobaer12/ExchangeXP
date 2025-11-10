package com.Shinigami_Coderz.ExchangeXP.controller;

import com.Shinigami_Coderz.ExchangeXP.entity.User;
import com.Shinigami_Coderz.ExchangeXP.service.BlogLikeService;
import com.Shinigami_Coderz.ExchangeXP.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
@Slf4j
public class BlogLikeController {

    @Autowired
    private BlogLikeService blogLikeService;

    @Autowired
    private UserService userService;

    @PostMapping("/{blogId}")
    public ResponseEntity<?> toggleLike(@PathVariable ObjectId blogId) {
        long start = System.currentTimeMillis();
        log.info("BlogLikeController.toggleLike: Received toggle-like request for blogId={}", blogId);

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("BlogLikeController.toggleLike: Unauthenticated request to toggle like for blogId={}", blogId);
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            String username = authentication.getName();
            User userByUsername = userService.findUserByUsername(username);

            if (userByUsername == null) {
                log.warn("BlogLikeController.toggleLike: No user found with username={} for blogId={}", username, blogId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            boolean liked = blogLikeService.toggleLike(blogId, userByUsername.getUserId());
            long totalLikes = blogLikeService.countLikes(blogId);

            if (liked) {
                log.info("BlogLikeController.toggleLike: User '{}' liked blogId={} (totalLikes={}) (elapsed={}ms)", username, blogId, totalLikes, System.currentTimeMillis() - start);
            } else {
                log.info("BlogLikeController.toggleLike: User '{}' unliked blogId={} (totalLikes={}) (elapsed={}ms)", username, blogId, totalLikes, System.currentTimeMillis() - start);
            }
            return new ResponseEntity<>(totalLikes, HttpStatus.OK);
        } catch (Exception e) {
            log.error("BlogLikeController.toggleLike: Exception while toggling like for blogId={}. error={}", blogId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-liker/{blogId}")
    public ResponseEntity<?> getLikes(@PathVariable ObjectId blogId) {
        long start = System.currentTimeMillis();
        log.info("BlogLikeController.getLikes: Received request to get likers for blogId={}", blogId);

        try {
            List<ObjectId> likers = blogLikeService.getLikes(blogId);
            if (likers == null || likers.isEmpty()) {
                log.warn("BlogLikeController.getLikes: No likers found for blogId={}", blogId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            log.info("BlogLikeController.getLikes: Found {} likers for blogId={} (elapsed={}ms)", likers.size(), blogId, System.currentTimeMillis() - start);
            return new ResponseEntity<>(likers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("BlogLikeController.getLikes: Exception while fetching likers for blogId={}. error={}", blogId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
