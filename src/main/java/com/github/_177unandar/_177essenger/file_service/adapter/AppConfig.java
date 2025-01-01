package com.github._177unandar._177essenger.file_service.adapter;

import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;

public class AppConfig {

    @ApplicationScoped
    public Gson getGson() {
        return new Gson();
    }
}
