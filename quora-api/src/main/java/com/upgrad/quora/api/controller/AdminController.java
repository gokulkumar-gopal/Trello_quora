package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AdminController {

    @Autowired
    private AdminBusinessService adminBusinessService;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}")
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        commonBusinessService.authorizeUser(authorization);
        final UserEntity deletedUser = adminBusinessService.userDelete(userUuid, authorization);
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(deletedUser.getUuid()).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<>(userDeleteResponse, HttpStatus.OK);
    }
}
