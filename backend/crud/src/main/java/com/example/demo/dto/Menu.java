package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class Menu {
	private String menuName;
	private String icon;
	private List<SubMenu> subPages;	
}
