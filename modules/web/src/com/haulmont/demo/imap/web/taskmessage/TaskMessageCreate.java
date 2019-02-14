package com.haulmont.demo.imap.web.taskmessage;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.demo.imap.entity.TaskMessage;

@UiController("tasktrack_TaskMessage.create")
@UiDescriptor("task-message-create.xml")
@EditedEntityContainer("taskMessageDc")
@LoadDataBeforeShow
public class TaskMessageCreate extends StandardEditor<TaskMessage> {
}