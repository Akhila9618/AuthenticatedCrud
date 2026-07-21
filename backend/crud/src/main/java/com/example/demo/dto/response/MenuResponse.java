package com.example.demo.dto.response;

import java.util.List;

import com.example.demo.dto.Menu;

import lombok.Data;

@Data
public class MenuResponse {
    private String role;
    private List<Menu> menus;
}
