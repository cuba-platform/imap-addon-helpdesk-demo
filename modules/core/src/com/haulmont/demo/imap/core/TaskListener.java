package com.haulmont.demo.imap.core;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.app.UniqueNumbersAPI;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.demo.imap.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("tasktrack_TaskListener")
public class TaskListener implements BeforeInsertEntityListener<Task> {

    @Inject
    private UniqueNumbersAPI uniqueNumbersAPI;

    private static final Logger log = LoggerFactory.getLogger(TaskListener.class);

    @Override
    public void onBeforeInsert(Task entity, EntityManager entityManager) {
        if (entity.getNumber() == null) {
            entity.setNumber(uniqueNumbersAPI.getNextNumber(Task.class.getSimpleName()));
            entityManager.persist(entity);
        }
    }
}
