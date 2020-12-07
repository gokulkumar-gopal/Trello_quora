package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity,authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<>(questionResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        final QuestionEntity questionEntity = new QuestionEntity();
        final List<QuestionDetailsResponse> questions = new ArrayList<QuestionDetailsResponse>();

        for(QuestionEntity qE : questionBusinessService.getAllQuestions(authorization)){
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(qE.getUuid()).content(qE.getContent());
            questions.add(questionResponse);
        }

        return new ResponseEntity<>(questions,  HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest questionEditRequest ,
                                                                    @PathVariable("questionId") final String questionId,
                                                                    @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuth = commonBusinessService.getAuthToken(authorization);
        authorizeUser(userAuth);
        QuestionEntity questionEntity = questionBusinessService.getQuestionById(questionId);

        if(questionEntity == null) {
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        if(userAuth.getUser() != questionEntity.getUser()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        final QuestionEntity editedQuestion = questionBusinessService.editQuestionContent(questionId,questionEditRequest.getContent());
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestion.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<>(questionEditResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable(name = "questionId") String questionId,
                                                             @RequestHeader(name = "authorization")
            String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuth = commonBusinessService.getAuthToken(authorization);
        authorizeUser(userAuth);
        QuestionEntity questionEntity = questionBusinessService.getQuestionById(questionId);

        if(questionEntity == null) {
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        if(userAuth.getUser() != questionEntity.getUser() || userAuth.getUser().getRole().equals("non-admin")) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        questionBusinessService.deleteQuestion(questionId);

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
        questionDeleteResponse.setId(questionId);
        questionDeleteResponse.setStatus("QUESTION DELETED");

        return new ResponseEntity<>(questionDeleteResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions( @PathVariable("userId") final String uuid,
                                                                          @RequestHeader("authorization") final String authorization
                                                                         ) throws AuthorizationFailedException, UserNotFoundException {
        final QuestionEntity questionEntity = new QuestionEntity();
        final List<QuestionDetailsResponse> questions = new ArrayList<QuestionDetailsResponse>();

        for(QuestionEntity qE : questionBusinessService.getAllQuestionsbyId(authorization,uuid)){
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(qE.getUuid()).content(qE.getContent());
            questions.add(questionResponse);
        }

        return new ResponseEntity<>(questions,  HttpStatus.OK);
    }

    public void authorizeUser(UserAuthEntity userAuthEntity) throws AuthorizationFailedException {

        if(userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if(userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }
    }


}
