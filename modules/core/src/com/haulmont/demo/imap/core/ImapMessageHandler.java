package com.haulmont.demo.imap.core;

import com.haulmont.addon.emailtemplates.core.EmailTemplatesAPI;
import com.haulmont.addon.emailtemplates.exceptions.ReportParameterTypeChangedException;
import com.haulmont.addon.emailtemplates.exceptions.TemplateNotFoundException;
import com.haulmont.addon.imap.api.ImapAPI;
import com.haulmont.addon.imap.dto.ImapMessageDto;
import com.haulmont.addon.imap.entity.ImapMessage;
import com.haulmont.addon.imap.events.NewEmailImapEvent;
import com.haulmont.cuba.core.app.UniqueNumbersAPI;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.demo.imap.entity.MessageDirection;
import com.haulmont.demo.imap.entity.Task;
import com.haulmont.demo.imap.entity.TaskMessage;
import com.haulmont.demo.imap.entity.TaskState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("tasktrack_ImapMessageHandler")
public class ImapMessageHandler {

    protected final static Pattern TASK_NUMBER_PATTERN = Pattern.compile("#(\\d+)");

    @Inject
    private ImapAPI imapAPI;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Inject
    private UniqueNumbersAPI uniqueNumbersAPI;

    @Inject
    private EmailTemplatesAPI emailTemplatesAPI;

    private static final Logger log = LoggerFactory.getLogger(ImapMessageHandler.class);

    @EventListener
    @Transactional
    public void handleMessage(NewEmailImapEvent imapEvent) {
        ImapMessage imapMessage = imapEvent.getMessage();
        ImapMessageDto imapMessageDto = imapAPI.fetchMessage(imapMessage);
        String subject = imapMessageDto.getSubject();

        Task task = null;
        TaskMessage message = metadata.create(TaskMessage.class);
        message.setContent(imapMessageDto.getBody());
        message.setSubject(imapMessageDto.getSubject());
        message.setReporter(imapMessageDto.getFrom());
        message.setImapMessage(imapMessage);
        message.setDirection(MessageDirection.INBOX);

        boolean isNewTask = false;

        Matcher matcher = TASK_NUMBER_PATTERN.matcher(subject);
        if (matcher.find()) {
            String taskNumber = matcher.group(1);
            task = findTaskByNumber(Integer.valueOf(taskNumber));
            if (task != null && TaskState.REPLIED == task.getState()) {
                task.setState(TaskState.ASSIGNED);
            }
        }
        if (task == null) {
            task = metadata.create(Task.class);
            task.setState(TaskState.OPEN);
            task.setContent(imapMessageDto.getBody());
            task.setSubject(imapMessageDto.getSubject());
            task.setReporterEmail(imapMessageDto.getFrom());
            task.setNumber(uniqueNumbersAPI.getNextNumber(Task.class.getSimpleName()));
            isNewTask = true;
        }

        message.setTask(task);

        dataManager.commit(task, message);

        String templateCode = isNewTask ? "NEW_TASK" : "EXIST_TASK";
        try {
            String cc = imapMessageDto.getCc();
            if ("[]".equals(cc)) {
                cc = null;
            }
            emailTemplatesAPI.buildFromTemplate(templateCode)
                    .setCc(cc)
                    .setTo(imapMessageDto.getFrom())
                    .setBodyParameter("task", task)
                    .setBodyParameter("message", message)
                    .sendEmailAsync();
        } catch (TemplateNotFoundException e) {
            log.error(String.format("Unable to find email template code: %s", templateCode), e);
        } catch (ReportParameterTypeChangedException e) {
            log.error(String.format("Incorrect parameter type for email template code: %s. Description: %s", templateCode, e.getMessage()), e);
        }
    }

    private Task findTaskByNumber(Integer taskNumber) {
        return dataManager.load(Task.class)
                .view("task-view")
                .query("select t from tasktrack_Task t where t.number = :number")
                .parameter("number", taskNumber)
                .optional()
                .orElse(null);
    }
}
