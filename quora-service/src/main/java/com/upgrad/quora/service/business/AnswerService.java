package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    private AnswerDao answerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity) {
        AnswerEntity createdAnswer = answerDao.createAnswer(answerEntity);
        return createdAnswer;
    }

    public AnswerEntity getAnswerById(String auuid) {
        return answerDao.getAnswerById(auuid);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(Integer id) {
        answerDao.deleteAnswer(id);
    }

    public List<AnswerEntity> getAllAnswersToQuestion(QuestionEntity question) {
        return answerDao.getAllAnswersToQuestion(question);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAnswer(AnswerEntity answer) {
        answerDao.updateAnswer(answer);
    }
}
