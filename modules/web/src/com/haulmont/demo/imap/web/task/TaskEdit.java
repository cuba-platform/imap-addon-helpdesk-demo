package com.haulmont.demo.imap.web.task;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.actions.list.CreateAction;
import com.haulmont.cuba.gui.actions.list.EditAction;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.demo.imap.entity.Task;
import com.haulmont.demo.imap.entity.TaskMessage;

import javax.inject.Inject;
import javax.inject.Named;

@UiController("tasktrack_Task.edit")
@UiDescriptor("task-edit.xml")
@EditedEntityContainer("taskDc")
@LoadDataBeforeShow
public class TaskEdit extends StandardEditor<Task> {

    @Inject
    private Table<TaskMessage> messagesTable;
    @Inject
    private ScreenBuilders screenBuilders;
    @Inject
    private Metadata metadata;

    @Subscribe("messagesTable.create")
    private void onTasksTableCreate(Action.ActionPerformedEvent event) {
        screenBuilders.editor(messagesTable)
                .newEntity()
                .withScreenId(metadata.getClass(TaskMessage.class).getName() + ".create")
                .withLaunchMode(OpenMode.DIALOG)
                .build()
                .show()
                .addAfterCloseListener(e ->
                        getEditedEntityLoader().load()
                );
    }


}