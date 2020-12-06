package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswerById(String uuid) {
        try{
            AnswerEntity answer = entityManager.createNamedQuery("getAnswerById", AnswerEntity.class).setParameter("uuid", uuid).getSingleResult();
            return answer;

        }catch (NoResultException noResultException) {
            return null;
        }
    }

    public void deleteAnswer(Integer id) {
        entityManager.flush();
        entityManager.clear();
        AnswerEntity answer = entityManager.find(AnswerEntity.class, id);
        entityManager.remove(answer);
    }

    public List<AnswerEntity> getAllAnswersToQuestion(QuestionEntity question) {
        try {
            return entityManager.createNamedQuery("answersByQuestionId", AnswerEntity.class).setParameter("question", question).getResultList();
        }
        catch (NoResultException noResultException) {
            return null;
        }
    }

    public void updateAnswer(AnswerEntity answer) {
        entityManager.merge(answer);
    }
}
