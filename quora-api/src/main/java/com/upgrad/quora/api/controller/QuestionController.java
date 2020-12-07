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
            throws AuthenticationFailedException {
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        final QuestionEntity createdQuestionEntity = questionBusinessService.createQuestion(questionEntity,authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<>(questionResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
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
            throws AuthenticationFailedException, AuthorizationFailedException, InvalidQuestionException {


        final QuestionEntity questionEntity = questionBusinessService.editQuestionContent(authorization ,questionId,questionEditRequest.getContent());
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(questionEntity.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<>(questionEditResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable(name = "questionId") String questionId,
                                                             @RequestHeader(name = "authorization")
            String authorization) throws AuthorizationFailedException, InvalidQuestionException, AuthenticationFailedException {


        questionBusinessService.deleteQuestion(authorization, questionId);

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
        questionDeleteResponse.setId(questionId);
        questionDeleteResponse.setStatus("QUESTION DELETED");

        return new ResponseEntity<>(questionDeleteResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions( @PathVariable("userId") final String uuid,
                                                                          @RequestHeader("authorization") final String authorization
                                                                         ) throws AuthenticationFailedException, UserNotFoundException {
        final QuestionEntity questionEntity = new QuestionEntity();
        final List<QuestionDetailsResponse> questions = new ArrayList<QuestionDetailsResponse>();

        for(QuestionEntity qE : questionBusinessService.getAllQuestionsbyId(authorization,uuid)){
            QuestionDetailsResponse questionResponse = new QuestionDetailsResponse().id(qE.getUuid()).content(qE.getContent());
            questions.add(questionResponse);
        }

        return new ResponseEntity<>(questions,  HttpStatus.OK);
    }




}
