package com.upgrad.quora.service.dao;

//import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions( ) {
        try {
            return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity editQuestionContent(String questionId){
        try {
            return entityManager.createNamedQuery("questionbyId",
                    QuestionEntity.class).setParameter("uuid",questionId).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public QuestionEntity updateQuestion(final QuestionEntity questionEntity){
        entityManager.merge(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestionsbyId( UserEntity user) {
        try {
            return entityManager.createNamedQuery("getAllQuestionsbyId",
                    QuestionEntity.class).setParameter("user",user).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

/*

    public UserAuthEntity getAllQuestions(final String accessToken){
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken",
                    UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }

    }


    public UserEntity deleteQuestion(final QuestionEntity deleteQuestion){
        entityManager.remove(deleteQuestion);
        return deleteQuestion;
    }
*/
}
