package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userDetails(@PathVariable("userId") final String userUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        commonBusinessService.authorizeUser(authorization);
        final UserEntity userEntity = commonBusinessService.getUserDetails(userUuid);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse()
                .userName(userEntity.getUserName())
                .aboutMe(userEntity.getAboutMe())
                .contactNumber(userEntity.getContactNumber())
                .country(userEntity.getCountry())
                .emailAddress(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .dob(userEntity.getDateOfBirth());
        return new ResponseEntity<>(userDetailsResponse, HttpStatus.OK);
    }
}
