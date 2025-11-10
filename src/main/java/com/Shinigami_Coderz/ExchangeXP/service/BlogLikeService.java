package com.Shinigami_Coderz.ExchangeXP.service;

import com.Shinigami_Coderz.ExchangeXP.entity.BlogLike;
import com.Shinigami_Coderz.ExchangeXP.repository.BlogLikeRepo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BlogLikeService {

    @Autowired
    private BlogLikeRepo  blogLikeRepo;

    // Toggle like/unlike
    public boolean toggleLike(ObjectId blogId, ObjectId userId) {
        long start = System.currentTimeMillis(); // ADDED
        log.info("BlogLikeService.toggleLike: Received request to toggle like for blogId={} by userId={}", blogId, userId); // ADDED

        if (blogId == null || userId == null) { // ADDED
            log.warn("BlogLikeService.toggleLike: blogId or userId is null (blogId={}, userId={})", blogId, userId);
            return false;
        }

        try {
            boolean liked = isLiked(blogId, userId);
            if (liked) {
                blogLikeRepo.deleteByBlogIdAndUserId(blogId, userId);
                log.info("BlogLikeService.toggleLike: User {} unliked blogId={} (elapsed={}ms)", userId, blogId, System.currentTimeMillis() - start); // ADDED
                return false; // user unliked
            } else {
                blogLikeRepo.save(new BlogLike(blogId, userId));
                log.info("BlogLikeService.toggleLike: User {} liked blogId={} (elapsed={}ms)", userId, blogId, System.currentTimeMillis() - start); // ADDED
                return true;  // user liked
            }
        } catch (Exception e) {
            log.error("BlogLikeService.toggleLike: Exception toggling like for blogId={} userId={}. error={}",
                    blogId, userId, e.getMessage(), e); // ADDED
            return false;
        }
    }

    // Count likes
    public long countLikes(ObjectId blogId) {
        long start = System.currentTimeMillis(); // ADDED
        log.debug("BlogLikeService.countLikes: Counting likes for blogId={}", blogId); // ADDED

        if (blogId == null) { // ADDED
            log.warn("BlogLikeService.countLikes: blogId is null."); // ADDED
            return 0L; // ADDED
        }

        try {
            long count = blogLikeRepo.countByBlogId(blogId);
            log.info("BlogLikeService.countLikes: blogId={} has {} likes (elapsed={}ms)", blogId, count, System.currentTimeMillis() - start); // ADDED
            return count;
        } catch (Exception e) {
            log.error("BlogLikeService.countLikes: Exception counting likes for blogId={}. error={}", blogId, e.getMessage(), e); // ADDED
            return 0L;
        }
    }

    // Get all liker IDs
    public List<ObjectId> getLikes(ObjectId blogId) {
        long start = System.currentTimeMillis(); // ADDED
        log.info("BlogLikeService.getLikes: Fetching likes for blogId={}", blogId); // ADDED

        if (blogId == null) { // ADDED
            log.warn("BlogLikeService.getLikes: blogId is null.");
            return Collections.emptyList(); // CHANGED from List.of() to Collections.emptyList()
        }

        try {
            List<ObjectId> likes = blogLikeRepo.findByBlogId(blogId)
                    .stream()
                    .map(BlogLike::getUserId)
                    .collect(Collectors.toList());

            log.info("BlogLikeService.getLikes: Found {} likes for blogId={} (elapsed={}ms)",
                    likes.size(), blogId, System.currentTimeMillis() - start); // ADDED
            return likes;
        } catch (Exception e) {
            log.error("BlogLikeService.getLikes: Exception fetching likes for blogId={}. error={}", blogId, e.getMessage(), e); // ADDED
            return Collections.emptyList(); // CHANGED from List.of() to Collections.emptyList()
        }
    }

    // Check if user liked this blog
    public boolean isLiked(ObjectId blogId, ObjectId userId) {
        log.debug("BlogLikeService.isLiked: Checking if userId={} liked blogId={}", userId, blogId); // ADDED

        if (blogId == null || userId == null) { // ADDED
            log.warn("BlogLikeService.isLiked: blogId or userId is null (blogId={}, userId={})", blogId, userId);
            return false;
        }

        try {
            boolean present = blogLikeRepo.findByBlogIdAndUserId(blogId, userId).isPresent();
            log.debug("BlogLikeService.isLiked: userId={} liked blogId={} => {}", userId, blogId, present); // ADDED
            return present;
        } catch (Exception e) {
            log.error("BlogLikeService.isLiked: Exception while checking like for blogId={} userId={}. error={}", blogId, userId, e.getMessage(), e); // ADDED
            return false;
        }
    }
}
