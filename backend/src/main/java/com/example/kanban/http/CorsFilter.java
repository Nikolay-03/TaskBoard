package com.example.kanban.http;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class CorsFilter extends Filter {

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        Headers reqHeaders = exchange.getRequestHeaders();
        Headers resHeaders = exchange.getResponseHeaders();

        // origin фронта – сюда подставь свой (React/Vite/…)
        String origin = reqHeaders.getFirst("Origin");
        if (origin != null) {
            resHeaders.set("Access-Control-Allow-Origin", origin);
            resHeaders.set("Vary", "Origin");
        }
        resHeaders.set("Access-Control-Allow-Credentials", "true");


        // если используешь куки/сессии
        resHeaders.set("Access-Control-Allow-Credentials", "true");

        resHeaders.set("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        resHeaders.set("Access-Control-Allow-Headers", "Content-Type, X-Session-Id");
        // чтобы фронт мог читать заголовок X-Session-Id
        resHeaders.set("Access-Control-Expose-Headers", "X-Session-Id");

        // preflight-запрос
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        chain.doFilter(exchange);
    }

    @Override
    public String description() {
        return "CORS filter";
    }
}
