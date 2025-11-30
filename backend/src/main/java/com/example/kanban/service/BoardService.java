package com.example.kanban.service;

import com.example.kanban.model.*;
import com.example.kanban.repository.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class BoardService {

    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;
    private final TaskRepository taskRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;
    private final TaskParticipantRepository taskParticipantRepository;
    private final TaskLabelRepository taskLabelRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final BoardFavoriteRepository boardFavoriteRepository;

    public BoardService(BoardRepository boardRepository,
                        ColumnRepository columnRepository,
                        TaskRepository taskRepository,
                        TaskAssigneeRepository taskAssigneeRepository,
                        TaskParticipantRepository taskParticipantRepository,
                        TaskLabelRepository taskLabelRepository,
                        BoardMemberRepository boardMemberRepository,
                        BoardFavoriteRepository boardFavoriteRepository) {
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.taskRepository = taskRepository;
        this.taskAssigneeRepository = taskAssigneeRepository;
        this.taskParticipantRepository = taskParticipantRepository;
        this.taskLabelRepository = taskLabelRepository;
        this.boardMemberRepository = boardMemberRepository;
        this.boardFavoriteRepository = boardFavoriteRepository;
    }

    public List<Board> getBoardsByOwner(long ownerId) throws SQLException {
        return boardRepository.findBoardsByOwner(ownerId);
    }

    public BoardView getBoardView(long boardId, long userId) throws SQLException, IllegalAccessException {
        Board board = boardRepository.findBoardById(boardId);
        if (board == null) throw new NoSuchElementException("Board not found");
        if (board.getOwnerId() != userId && !boardMemberRepository.isMember(boardId, userId)) {
            throw new IllegalAccessException("Forbidden");
        }

        List<Column> columns = columnRepository.findByBoardId(boardId);
        List<Task> tasks = taskRepository.findByBoardId(boardId);

        Map<Long, List<Task>> tasksByColumn = tasks.stream()
                .collect(Collectors.groupingBy(Task::getColumnId));

        List<BoardColumnView> columnViews = new ArrayList<>();
        for (Column c : columns) {
            List<Task> colTasks = tasksByColumn.getOrDefault(c.getId(), List.of());
            List<TaskView> tvList = new ArrayList<>();
            for (Task t : colTasks) {
                TaskView tv = new TaskView();
                long tId = t.getId();
                List<User> assigneesByTask = taskAssigneeRepository.getAssigneesByTaskId(tId);
                List<User> participantsByTask = taskParticipantRepository.getParticipantsByTaskId(tId);
                List<Label> labelsByTask = taskLabelRepository.getLabelsByTaskId(tId);
                tv.setId(t.getId());
                tv.setBoardId(t.getBoardId());
                tv.setColumnId(t.getColumnId());
                tv.setTitle(t.getTitle());
                tv.setDescription(t.getDescription());
                tv.setPosition(t.getPosition());
                tv.setDueDate(t.getDueDate());
                tv.setCreatedAt(t.getCreatedAt());
                tv.setUpdatedAt(t.getUpdatedAt());

                tv.setAssignees(assigneesByTask);
                tv.setParticipants(participantsByTask);
                tv.setLabels(labelsByTask);
                tvList.add(tv);
            }
            BoardColumnView cv = new BoardColumnView();
            cv.setId(c.getId());
            cv.setBoardId(c.getBoardId());
            cv.setTitle(c.getTitle());
            cv.setPosition(c.getPosition());
            cv.setTasks(tvList);
            columnViews.add(cv);
        }

        BoardView view = new BoardView();
        view.setId(board.getId());
        view.setOwnerId(board.getOwnerId());
        view.setTitle(board.getTitle());
        view.setDescription(board.getDescription());
        view.setCreatedAt(board.getCreatedAt());
        view.setUpdatedAt(board.getUpdatedAt());
        view.setIsFavorite(boardFavoriteRepository.isFavorite(view.getId(), userId));
        view.setColumns(columnViews);
        return view;
    }

    public Board createBoard(long ownerId, String title, String description) throws SQLException {
        return boardRepository.createBoard(ownerId, title, description);
    }

    public Board updateBoard(long boardId, long userId, String title, String description)
            throws SQLException, IllegalAccessException {
        Board existing = boardRepository.findBoardById(boardId);
        if (existing == null) throw new NoSuchElementException("Board not found");
        if (existing.getOwnerId() != userId) throw new IllegalAccessException("Forbidden");
        return boardRepository.updateBoard(boardId, title, description);
    }

    public void deleteBoard(long boardId, long userId) throws SQLException, IllegalAccessException {
        Board existing = boardRepository.findBoardById(boardId);
        if (existing == null) return;
        if (existing.getOwnerId() != userId) throw new IllegalAccessException("Forbidden");
        boardRepository.deleteBoard(boardId);
    }

    public Column createColumn(long boardId, long userId, String title, Integer position)
            throws SQLException, IllegalAccessException {
        Board board = boardRepository.findBoardById(boardId);
        if (board == null) throw new NoSuchElementException("Board not found");
        if (board.getOwnerId() != userId) throw new IllegalAccessException("Forbidden");
        return columnRepository.createColumn(boardId, title, position);
    }

    public Column updateColumn(long columnId, long userId, String title, Integer position)
            throws SQLException, IllegalAccessException {
        Column c = columnRepository.findById(columnId);
        if (c == null) throw new NoSuchElementException("Column not found");
        Board board = boardRepository.findBoardById(c.getBoardId());
        if (board == null || board.getOwnerId() != userId) throw new IllegalAccessException("Forbidden");
        return columnRepository.updateColumn(columnId, title, position);
    }

    public void deleteColumn(long columnId, long userId)
            throws SQLException, IllegalAccessException {
        Column c = columnRepository.findById(columnId);
        if (c == null) return;
        Board board = boardRepository.findBoardById(c.getBoardId());
        if (board == null || board.getOwnerId() != userId) throw new IllegalAccessException("Forbidden");
        columnRepository.deleteColumn(columnId);
    }

    public List<User> getMembers(long boardId) throws SQLException {
        return boardMemberRepository.getMembersByBoardId(boardId);
    }

    public List<Board> getBoardsByMember(long userId) throws SQLException {
        List<Board> boards = boardRepository.findBoardsByMember(userId);
        for (Board board : boards) {
            board.setIsFavorite(boardFavoriteRepository.isFavorite(board.getId(), userId));
        }
        return boards;
    }

    public void addFavorite(long boardId, long userId) throws SQLException, IllegalAccessException {
        Board board = boardRepository.findBoardById(boardId);
        if (board == null) throw new NoSuchElementException("Board not found");
        if (board.getOwnerId() != userId && !boardMemberRepository.isMember(boardId, userId)) {
            throw new IllegalAccessException("Forbidden");
        }
        boardFavoriteRepository.addFavorite(boardId, userId);
    }

    public void removeFavorite(long boardId, long userId) throws SQLException, IllegalAccessException {
        Board board = boardRepository.findBoardById(boardId);
        if (board == null) throw new NoSuchElementException("Board not found");
        if (board.getOwnerId() != userId && !boardMemberRepository.isMember(boardId, userId)) {
            throw new IllegalAccessException("Forbidden");
        }
        boardFavoriteRepository.removeFavorite(boardId, userId);
    }
}
