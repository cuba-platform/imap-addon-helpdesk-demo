package com.haulmont.demo.imap.web.taskmessage;

import com.haulmont.cuba.gui.screen.*;
import com.haulmont.demo.imap.entity.TaskMessage;

@UiController("tasktrack_TaskMessage.edit")
@UiDescriptor("task-message-edit.xml")
@EditedEntityContainer("taskMessageDc")
@LoadDataBeforeShow
public class TaskMessageEdit extends StandardEditor<TaskMessage> {

    @Subscribe
    private void onInitEntity(InitEntityEvent<TaskMessage> event) {

    }


}