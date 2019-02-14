package com.haulmont.demo.imap.web.task;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.actions.list.CreateAction;
import com.haulmont.cuba.gui.actions.list.EditAction;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.data.DataSupplier;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.DataComponents;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.demo.imap.entity.Task;
import com.haulmont.demo.imap.entity.TaskState;

import javax.inject.Inject;
import javax.inject.Named;

@UiController("tasktrack_Task.browse")
@UiDescriptor("task-browse.xml")
@LookupComponent("tasksTable")
@LoadDataBeforeShow
public class TaskBrowse extends StandardLookup<Task> {

    @Inject
    private UserSession userSession;

    @Inject
    private GroupTable<Task> tasksTable;

    @Inject
    private CollectionLoader<Task> tasksDl;

    @Inject
    private DataManager dataManager;


    @Subscribe("assign")
    private void onAssignClick(Button.ClickEvent event) {
        Task task = tasksTable.getSingleSelected();
        if (task != null) {
            User user = userSession.getUser();
            task.setAssignee(user);
            task.setState(TaskState.ASSIGNED);
            dataManager.commit(task);
            tasksDl.load();
        }
    }

    @Subscribe("close")
    private void onCloseClick(Button.ClickEvent event) {
        Task task = tasksTable.getSingleSelected();
        if (task != null) {
            task.setState(TaskState.CLOSED);
            dataManager.commit(task);
            tasksDl.load();
        }
    }

    @Subscribe("inProgress")
    private void onInProgressClick(Button.ClickEvent event) {
        Task task = tasksTable.getSingleSelected();
        if (task != null) {
            task.setState(TaskState.IN_PROGRESS);
            dataManager.commit(task);
            tasksDl.load();
        }
    }


}