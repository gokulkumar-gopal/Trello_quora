package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class AnswerController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST, value = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable(name = "questionId") String quuid, @RequestHeader(name = "authorization")
            String authorization, AnswerRequest answerRequest) throws InvalidQuestionException, AuthorizationFailedException {

        UserAuthEntity userAuthEntity = commonBusinessService.authorizeUser(authorization);
        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }

        QuestionEntity question = questionBusinessService.getQuestionById(quuid);
        if(question == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setQuestion(question);
        answerEntity.setUser(userAuthEntity.getUser());
        answerEntity.setUuid(UUID.randomUUID().toString());

        AnswerEntity duplicateAnswerEntity = answerService.createAnswer(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.setId(duplicateAnswerEntity.getUuid());
        answerResponse.setStatus("ANSWER CREATED");

        return new ResponseEntity<>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable(name = "answerId") String auuid, @RequestHeader(name = "authorization")
            String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthEntity userAuth = commonBusinessService.authorizeUser(authorization);
        if(userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }

        AnswerEntity answer = answerService.getAnswerById(auuid);
        if(answer == null) {
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }

        if(userAuth.getUser() != answer.getUser() || userAuth.getUser().getRole().equals("non-admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
        answerService.deleteAnswer(answer.getId());

        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse();
        answerDeleteResponse.setId(auuid);
        answerDeleteResponse.setStatus("ANSWER DELETED");

        return new ResponseEntity<>(answerDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDetailsResponse> getAllAnswersToQuestion(@PathVariable(name = "questionId") String quuid, @RequestHeader(name = "authorization")
            String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuth = commonBusinessService.authorizeUser(authorization);
        if(userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }

        QuestionEntity question = questionBusinessService.getQuestionById(quuid);
        if(question == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        List<AnswerEntity> list = answerService.getAllAnswersToQuestion(question);
        AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
        answerDetailsResponse.setQuestionContent(question.getContent());

        String answers = "";
        String uuids = "";

        for (AnswerEntity a : list) {
            answers += a.getAns() + ", ";
            uuids += a.getUuid() + ", ";
        }

        answerDetailsResponse.setAnswerContent(answers);
        answerDetailsResponse.setId(uuids);

        return new ResponseEntity<>(answerDetailsResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value =  "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(@PathVariable(name = "answerId") String auuid, AnswerEditRequest answerEditRequest, @RequestHeader(name = "authorization")
            String authorization) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthEntity userAuth = commonBusinessService.authorizeUser(authorization);
        if(userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }

        AnswerEntity answer = answerService.getAnswerById(auuid);
        if(answer == null) {
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }

        if(userAuth.getUser() != answer.getUser()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answer.setAns(answerEditRequest.getContent());
        answerService.updateAnswer(answer);

        AnswerEditResponse answerEditResponse = new AnswerEditResponse();
        answerEditResponse.setId(auuid);
        answerEditResponse.setStatus("ANSWER EDITED");

        return new ResponseEntity<>(answerEditResponse, HttpStatus.OK);
    }
}
