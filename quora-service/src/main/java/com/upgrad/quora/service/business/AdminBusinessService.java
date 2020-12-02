package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommonBusinessService commonBusinessService;

    public UserEntity userDelete(final String userUuid, final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        commonBusinessService.checkForAdminRole(authorization);
        UserEntity userToDelete = commonBusinessService.getUserDetails(userUuid);
        UserEntity deletedUser = userDao.deleteUser(userToDelete);
        return deletedUser;
    }
}
