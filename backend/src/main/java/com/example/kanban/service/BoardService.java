package com.example.kanban.service;

import com.example.kanban.model.*;
import com.example.kanban.repository.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class BoardService {

    private final BoardRepository boardRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;
    private final TaskParticipantRepository taskParticipantRepository;
    private final TaskLabelRepository taskLabelRepository;

    public BoardService(BoardRepository boardRepository,
                        TaskAssigneeRepository taskAssigneeRepository,
                        TaskParticipantRepository taskParticipantRepository,
                        TaskLabelRepository taskLabelRepository) {
        this.boardRepository = boardRepository;
        this.taskAssigneeRepository = taskAssigneeRepository;
        this.taskParticipantRepository = taskParticipantRepository;
        this.taskLabelRepository = taskLabelRepository;
    }

    public BoardView getBoardView(long boardId, long userId) throws SQLException, IllegalAccessException {
        Board board = boardRepository.findBoardById(boardId);
        if (board == null) {
            throw new NoSuchElementException("Board not found");
        }

        // пока без сложной проверки прав — просто владелец
        if (board.getOwnerId() != userId) {
            throw new IllegalAccessException("Access denied");
        }

        List<Column> columns = boardRepository.findColumnsByBoard(boardId);
        List<Task> tasks = boardRepository.findTasksByBoard(boardId);

        List<Long> taskIds = tasks.stream()
                .map(Task::getId)
                .collect(Collectors.toList());

        Map<Long, List<User>> assigneesByTask = taskAssigneeRepository.findUsersByTaskIds(taskIds);
        Map<Long, List<User>> participantsByTask = taskParticipantRepository.findUsersByTaskIds(taskIds);
        Map<Long, List<Label>> labelsByTask = taskLabelRepository.findLabelsByTaskIds(taskIds);

        // группируем задачи по колонкам
        Map<Long, List<Task>> tasksByColumnId = tasks.stream()
                .collect(Collectors.groupingBy(Task::getColumnId));

        List<BoardColumnView> columnViews = new ArrayList<>();

        for (Column c : columns) {
            List<Task> colTasks = tasksByColumnId.getOrDefault(c.getId(), List.of());
            List<TaskView> taskViews = new ArrayList<>();

            for (Task t : colTasks) {
                TaskView tv = new TaskView();
                tv.setId(t.getId());
                tv.setBoardId(t.getBoardId());
                tv.setColumnId(t.getColumnId());
                tv.setTitle(t.getTitle());
                tv.setDescription(t.getDescription());
                tv.setPosition(t.getPosition());
                tv.setDueDate(t.getDueDate());
                tv.setCreatedAt(t.getCreatedAt());
                tv.setUpdatedAt(t.getUpdatedAt());

                tv.setAssignees(assigneesByTask.getOrDefault(t.getId(), List.of()));
                tv.setParticipants(participantsByTask.getOrDefault(t.getId(), List.of()));
                tv.setLabels(labelsByTask.getOrDefault(t.getId(), List.of()));

                taskViews.add(tv);
            }

            BoardColumnView cv = new BoardColumnView();
            cv.setId(c.getId());
            cv.setBoardId(c.getBoardId());
            cv.setTitle(c.getTitle());
            cv.setPosition(c.getPosition());
            cv.setTasks(taskViews);

            columnViews.add(cv);
        }

        BoardView view = new BoardView();
        view.setId(board.getId());
        view.setOwnerId(board.getOwnerId());
        view.setTitle(board.getTitle());
        view.setDescription(board.getDescription());
        view.setCreatedAt(board.getCreatedAt());
        view.setUpdatedAt(board.getUpdatedAt());
        view.setColumns(columnViews);

        return view;
    }
}
