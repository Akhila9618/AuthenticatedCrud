DROP TABLE IF EXISTS role_menu;
DROP TABLE IF EXISTS sub_menu;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS menus;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
    id BIGINT PRIMARY KEY,
    role_name VARCHAR(50)
);

CREATE TABLE menus (
    id BIGINT PRIMARY KEY,
    menu_name VARCHAR(100),
    icon VARCHAR(100)
);

CREATE TABLE sub_menu (
    id BIGINT PRIMARY KEY,
    page_name VARCHAR(100),
    routing_url VARCHAR(100),
    menu_id BIGINT,
    FOREIGN KEY (menu_id) REFERENCES menus(id)
);

CREATE TABLE role_menu (
    role_id BIGINT,
    menu_id BIGINT,
    PRIMARY KEY (role_id, menu_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (menu_id) REFERENCES menus(id)
);