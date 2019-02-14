package com.haulmont.demo.imap.web.taskmessage;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.demo.imap.entity.TaskMessage;

@UiController("tasktrack_TaskMessage.browse")
@UiDescriptor("task-message-browse.xml")
@LookupComponent("taskMessagesTable")
@LoadDataBeforeShow
public class TaskMessageBrowse extends StandardLookup<TaskMessage> {
}