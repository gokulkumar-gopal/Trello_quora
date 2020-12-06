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
    private AuthenticationService authenticationService;

    @Autowired
    private UserDao userDao;

    public void authenticateUser(UserEntity userEntity , ZonedDateTime logout) throws AuthenticationFailedException{

        if (userEntity == null) {
            throw new AuthenticationFailedException("ATHR-001", "User has not signed in");
        }

        if (logout != null) {
            throw new AuthenticationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, String authorization) throws AuthenticationFailedException {

        UserAuthEntity userAuthToken =userDao.getUserAuthToken(authorization);
        authenticateUser(userAuthToken.getUser(),userAuthToken.getLogoutAt());

        questionEntity.setUuid(UUID.randomUUID().toString());
        // questionEntity.setId();
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUser(userAuthToken.getUser());
        return questionDao.createQuestion(questionEntity);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(String authorization) throws AuthenticationFailedException {
        UserAuthEntity userAuthToken =userDao.getUserAuthToken(authorization);
        authenticateUser(userAuthToken.getUser(),userAuthToken.getLogoutAt());
        List<QuestionEntity> questions  = new ArrayList<QuestionEntity>() ;
        questions = questionDao.getAllQuestions();
        return questions;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(String authorization, String questionId, String content) throws AuthenticationFailedException,
            AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthToken =userDao.getUserAuthToken(authorization);
        QuestionEntity questionEntity =questionDao.editQuestionContent(questionId);

        authenticateUser(userAuthToken.getUser(),userAuthToken.getLogoutAt());

        if(userAuthToken.getUser() != questionEntity.getUser()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        if(questionEntity.getId() == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        questionEntity.setContent(content);
        questionEntity.setDate(ZonedDateTime.now());

        return questionDao.updateQuestion(questionEntity);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsbyId(String authorization, String uuid)
            throws AuthenticationFailedException, UserNotFoundException {
        UserAuthEntity userAuthToken =userDao.getUserAuthToken(authorization);
        authenticateUser(userAuthToken.getUser(),userAuthToken.getLogoutAt());

        if(userDao.getUserByUuid(uuid)==null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        List<QuestionEntity> questions  = new ArrayList<QuestionEntity>() ;
        questions = questionDao.getAllQuestionsbyId(userDao.getUserByUuid(uuid));
        return questions;

    }


}
