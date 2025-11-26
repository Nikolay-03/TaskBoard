package com.example.kanban.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class Task {
    private long id;
    private long boardId;
    private long columnId;
    private String title;
    private String description;
    private int position;
    private LocalDate dueDate;
    private Instant createdAt;
    private Instant updatedAt;
    private List<Label> labels;
    private List<TaskParticipant> participants;
    private List<TaskAssignee> assignees;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBoardId() {
        return boardId;
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<TaskParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<TaskParticipant> participants) {
        this.participants = participants;
    }

    public List<TaskAssignee> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<TaskAssignee> assignees) {
        this.assignees = assignees;
    }
}
