/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.search;

import com.fut.chatbot.model.Question;
import com.fut.chatbot.util.Constants;
import java.util.ArrayList;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import javax.persistence.PersistenceContext;
import org.hibernate.search.engine.ProjectionConstants;
import org.hibernate.search.jpa.FullTextQuery;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class HibernateSearchService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<SearchItem> search(String searchTerm) {

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Question.class)
                .get();
        Query luceneQuery = qb
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(1)
                .withPrefixLength(0)
                .onField("body")
                .matching(searchTerm)
                .createQuery();

        FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Question.class);
        fullTextQuery.setProjection(ProjectionConstants.SCORE, ProjectionConstants.EXPLANATION, ProjectionConstants.THIS);

        List<SearchItem> resultList = new ArrayList<>();
        try {
            fullTextQuery.getResultList().forEach((data)->{
                Object[] row = (Object[]) data;
                float score = (float) row[0];
                Question question = (Question) row[2];
                SearchItem searchItem = new SearchItem(score, question);
                System.out.println(Constants.GSON_EXPOSE.toJson(searchItem));
                resultList.add(searchItem);
            });
            System.out.println("");
        } catch (NoResultException ignore) {}

        return resultList;

    }
}
