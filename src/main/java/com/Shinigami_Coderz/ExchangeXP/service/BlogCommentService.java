package com.Shinigami_Coderz.ExchangeXP.service;

import com.Shinigami_Coderz.ExchangeXP.entity.BlogComment;
import com.Shinigami_Coderz.ExchangeXP.repository.BlogCommentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogCommentService {

    @Autowired
    private BlogCommentRepo blogCommentRepo;

    public void saveComment(BlogComment comment) {
        blogCommentRepo.save(comment);
    }
}
