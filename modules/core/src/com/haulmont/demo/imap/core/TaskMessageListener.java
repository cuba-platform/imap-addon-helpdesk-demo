package com.haulmont.demo.imap.core;

import com.haulmont.addon.emailtemplates.core.EmailTemplatesAPI;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.app.EmailerAPI;
import com.haulmont.cuba.core.app.EmailerConfig;
import com.haulmont.cuba.core.global.EmailException;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.demo.imap.entity.MessageDirection;
import com.haulmont.demo.imap.entity.Task;
import com.haulmont.demo.imap.entity.TaskMessage;
import com.haulmont.demo.imap.entity.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;

@Component("tasktrack_TaskMessageListener")
public class TaskMessageListener implements AfterInsertEntityListener<TaskMessage>, BeforeInsertEntityListener<TaskMessage> {

    @Inject
    private EmailTemplatesAPI emailTemplatesAPI;

    @Inject
    private EmailerAPI emailerAPI;

    private static final Logger log = LoggerFactory.getLogger(TaskMessageListener.class);

    @Override
    public void onAfterInsert(TaskMessage message, Connection connection) {

    }

    @Override
    public void onBeforeInsert(TaskMessage message, EntityManager entityManager) {
        if (MessageDirection.OUTBOX == message.getDirection()) {
            Task task = entityManager.find(Task.class, message.getTask().getId(), "task-message-view");
            task.setState(TaskState.REPLIED);
            entityManager.persist(task);
            try {
                EmailInfo emailInfo = emailTemplatesAPI.buildFromTemplate("TASK_REPLIED")
                        .setTo(task.getReporterEmail())
                        .setBodyParameter("task", task)
                        .setBodyParameter("message", message)
                        .generateEmail();
                message.setContent(emailInfo.getBody());
                emailerAPI.sendEmail(emailInfo);

            } catch (TemplateNotFoundException e) {
                log.error("Unable to find email template code: TASK_REPLIED", e);
            } catch (ReportParameterTypeChangedException e) {
                log.error(String.format("Incorrect parameter type for email template code: TASK_REPLIED. Description: %s", e.getMessage()), e);
            } catch (EmailException e) {
                log.error("Unable to send email",e);
            }
        }
    }
}
