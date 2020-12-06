package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class QuestionController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Autowired
    private QuestionService questionService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<QuestionResponse> createQuestion(final String authorization, final QuestionRequest questionRequest) throws AuthorizationFailedException {
        final UserAuthEntity userAuthEntity = commonBusinessService.authorizeUser(authorization);
        UserEntity userEntity = userAuthEntity.getUser();
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setUser(userEntity);
        questionEntity.setDate(ZonedDateTime.now());
        final QuestionEntity createdQuestion = questionService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<>(questionResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = commonBusinessService.authorizeUser(authorization);
        List<QuestionEntity> questionsList = questionService.getQuestionsByEveryone();
        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<>();
        for(QuestionEntity question : questionsList) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.content(question.getContent());
            questionDetailsResponse.id(question.getUuid());
            questionDetailsResponses.add(questionDetailsResponse);
        }
        return new ResponseEntity<>(questionDetailsResponses, HttpStatus.OK);
    }
}
