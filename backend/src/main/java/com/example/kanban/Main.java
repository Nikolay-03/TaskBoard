package com.example.kanban;

import com.example.kanban.config.AppConfig;
import com.example.kanban.db.ConnectionManager;
import com.example.kanban.http.CorsFilter;
import com.example.kanban.http.Router;
import com.example.kanban.http.handlers.*;
import com.example.kanban.repository.*;
import com.example.kanban.service.AuthService;
import com.example.kanban.service.BoardService;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws Exception {
        AppConfig config = new AppConfig();
        ConnectionManager cm = new ConnectionManager(config);

        // Репозитории
        UserRepository userRepo = new UserRepository(cm);
        SessionRepository sessionRepo = new SessionRepository(cm);
        BoardRepository boardRepo = new BoardRepository(cm);
        TaskAssigneeRepository assigneeRepo = new TaskAssigneeRepository(cm);
        TaskParticipantRepository participantRepo = new TaskParticipantRepository(cm);
        TaskLabelRepository labelRepo = new TaskLabelRepository(cm);
        ColumnRepository columnRepo = new ColumnRepository(cm);
        TaskRepository taskRepo = new TaskRepository(cm);
        BoardMemberRepository boardMemberRepo = new BoardMemberRepository(cm);

        // Сервисы
        AuthService authService = new AuthService(userRepo, sessionRepo);
        BoardService boardService = new BoardService(
                boardRepo, columnRepo, taskRepo, assigneeRepo, participantRepo, labelRepo, boardMemberRepo
        );
        TaskHandler taskHandler = new TaskHandler(
                authService,
                taskRepo,
                columnRepo,
                assigneeRepo,
                participantRepo,
                labelRepo
        );

        // HTTP-сервер
        HttpServer server = HttpServer.create(new InetSocketAddress(config.getServerPort()), 0);
        server.createContext("/openapi.yaml", new OpenApiHandler());
        server.createContext("/swagger", new SwaggerUiHandler());
        var labelsContext = server.createContext("/api/labels", new LabelHandler(authService, labelRepo));
        var authContext = server.createContext("/api/auth", new AuthHandler(authService));
        var boardsContext = server.createContext("/api/boards", new BoardHandler(authService, boardService));
        var tasksContext = server.createContext("/api/tasks", taskHandler);
        var columnsContext = server.createContext("/api/columns", taskHandler);
        CorsFilter corsFilter = new CorsFilter();
        authContext.getFilters().add(corsFilter);
        labelsContext.getFilters().add(corsFilter);
        boardsContext.getFilters().add(corsFilter);
        tasksContext.getFilters().add(corsFilter);
        columnsContext.getFilters().add(corsFilter);
        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port " + config.getServerPort());
    }
}

