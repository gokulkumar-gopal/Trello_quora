package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;


    @Autowired
    private UserDao userDao;

    public void authenticateUser(UserEntity userEntity , ZonedDateTime logout) throws AuthorizationFailedException{

        if (userEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (logout != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, UserAuthEntity userAuthEntity) throws AuthorizationFailedException {
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUser(userAuthEntity.getUser());
        return questionDao.createQuestion(questionEntity);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(String authorization) throws AuthorizationFailedException {
        List<QuestionEntity> questions  = new ArrayList<QuestionEntity>();
        questions = questionDao.getAllQuestions();
        return questions;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(String questionId, String content) {
        QuestionEntity questionEntity =questionDao.editQuestionContent(questionId);
        questionEntity.setContent(content);
        questionEntity.setDate(ZonedDateTime.now());
        return questionDao.updateQuestion(questionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity getQuestionById(String questionId) {
        return questionDao.getQuestionById(questionId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(String questionId) {
        QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
        questionDao.deleteQuestion(questionEntity.getId());
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsbyId(String uuid)
            throws UserNotFoundException {

        if(userDao.getUserByUuid(uuid) == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        List<QuestionEntity> questions  = new ArrayList<QuestionEntity>() ;
        questions = questionDao.getAllQuestionsbyId(userDao.getUserByUuid(uuid));
        return questions;
    }


}
