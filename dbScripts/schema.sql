DROP TABLE IF EXISTS role_menu;
DROP TABLE IF EXISTS sub_menu;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS menus;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS employee;
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

CREATE TABLE employee (
    empl_id INT AUTO_INCREMENT PRIMARY KEY,
    emp_name VARCHAR(255),
    emp_salary INT,
    emp_address VARCHAR(255),
    password VARCHAR(255),
    emp_email VARCHAR(255),
    company_name VARCHAR(255),
    role VARCHAR(100)
);
