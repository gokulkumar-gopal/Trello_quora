package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {
        QuestionEntity createdQuestion = questionDao.createQuestion(questionEntity);
        return createdQuestion;
    }

    public QuestionEntity getQuestionById(String uuid) {
        QuestionEntity questionEntity = questionDao.getQuestionById(uuid);
        return questionEntity;
    }
}
