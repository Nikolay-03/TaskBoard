package com.example.kanban.model;

public class TaskLabel {
    private long taskId;
    private long labelId;

    public TaskLabel() {
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getLabelId() {
        return labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }
}
