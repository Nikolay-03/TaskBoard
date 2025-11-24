package com.example.kanban.model;

public class Column {
    private long id;
    private long boardId;
    private String title;
    private int position;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getBoardId() { return boardId; }
    public void setBoardId(long boardId) { this.boardId = boardId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}
