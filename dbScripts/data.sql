-- ==========================
-- ROLES
-- ==========================

INSERT INTO roles (id, role_name)
VALUES
(1, 'ADMIN'),
(2, 'USER');


-- ==========================
-- MENUS
-- ==========================

INSERT INTO menus (id, menu_name, icon)
VALUES
(1, 'Dashboard', 'dashboard'),
(2, 'Employees', 'group'),
(3, 'Reports', 'assessment');


-- ==========================
-- SUB MENUS
-- ==========================

INSERT INTO sub_menu (id, page_name, routing_url, menu_id)
VALUES
(1, 'Dashboard', 'dashboard', 1),
(2, 'Employee List', 'employees', 2),
(3, 'Health', 'health', 3),
(4, 'Attendance', 'attendance', 3);


-- ==========================
-- ROLE MENU MAPPING
-- ==========================

INSERT INTO role_menu (role_id, menu_id)
VALUES
(1, 1), -- ADMIN -> Dashboard
(1, 2), -- ADMIN -> Employees
(1, 3), -- ADMIN -> Reports

(2, 1), -- USER -> Dashboard
(2, 3); -- USER -> Reports


INSERT INTO employee
(
    emp_name,
    emp_salary,
    emp_address,
    password,
    emp_email,
    company_name,
    role
)
VALUES
(
    'defaultuser',
    1000,
    'hyd',
    'defaultpassword',
    'default@gmail.com',
    'psx',
    'admin'
);