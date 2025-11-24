package com.example.kanban;

import com.example.kanban.config.AppConfig;
import com.example.kanban.db.ConnectionManager;

import java.sql.Connection;

public class DbTest {
    public static void main(String[] args) {
        try {
            AppConfig config = new AppConfig();
            ConnectionManager cm = new ConnectionManager(config);

            try (Connection con = cm.getConnection()) {
                System.out.println("Connected to PostgreSQL OK!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}