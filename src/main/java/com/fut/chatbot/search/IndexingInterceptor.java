/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fut.chatbot.search;

import com.fut.chatbot.model.Question;
import com.fut.chatbot.model.Question.QuestionStatus;
import org.hibernate.search.indexes.interceptor.EntityIndexingInterceptor;
import org.hibernate.search.indexes.interceptor.IndexingOverride;

/**
 *
 * @author ahmad
 */
public class IndexingInterceptor implements EntityIndexingInterceptor<Question> {
    @Override
    public IndexingOverride onAdd(Question entity) {
        if (entity.getStatus() == QuestionStatus.APPROVED) {
            return IndexingOverride.APPLY_DEFAULT;
        }
        return IndexingOverride.SKIP;
    }

    @Override
    public IndexingOverride onUpdate(Question entity) {
        if (entity.getStatus() == QuestionStatus.APPROVED) {
            return IndexingOverride.UPDATE;
        }
        return IndexingOverride.REMOVE;
    }

    @Override
    public IndexingOverride onDelete(Question entity) {
        return IndexingOverride.APPLY_DEFAULT;
    }

    @Override
    public IndexingOverride onCollectionUpdate(Question entity) {
        return onUpdate(entity);
    }
}
