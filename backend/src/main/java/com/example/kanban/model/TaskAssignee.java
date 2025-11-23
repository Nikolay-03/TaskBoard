package com.example.kanban.model;

public class TaskAssignee {
    private long taskId;
    private long userId;

    public TaskAssignee() {
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
