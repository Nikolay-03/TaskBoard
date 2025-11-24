package com.example.kanban;

import com.example.kanban.config.AppConfig;
import com.example.kanban.db.ConnectionManager;
import com.example.kanban.http.Router;
import com.example.kanban.http.handlers.AuthHandler;
import com.example.kanban.http.handlers.BoardHandler;
import com.example.kanban.http.handlers.TaskHandler;
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

        // Сервисы
        AuthService authService = new AuthService(userRepo, sessionRepo);
        BoardService boardService = new BoardService(
                boardRepo, columnRepo,taskRepo, assigneeRepo, participantRepo, labelRepo
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

        server.createContext("/api/auth", new AuthHandler(authService));
        server.createContext("/api/boards", new BoardHandler(authService, boardService));

        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port " + config.getServerPort());
    }
}

