INSERT INTO `forum-startup`.roles (role_id, name) VALUES (1, 'ROLE_USER');
INSERT INTO `forum-startup`.roles (role_id, name) VALUES (2, 'ROLE_ADMIN');


INSERT INTO `forum-startup`.users (is_blocked, created_at, updated_at, user_id, email, first_name, last_name, password, phone_number, profile_photo_url, username) VALUES (false, '2025-11-18 20:34:06.000000', null, 1, 'admin@example.com', 'Admin', 'User', '$2a$10$/WGgv2GGHNf/HGijmV98ouR9lLUEs3In4yZzabbyyNdu6EUhlFDnW', '+359888000000', null, 'admin');
INSERT INTO `forum-startup`.users (is_blocked, created_at, updated_at, user_id, email, first_name, last_name, password, phone_number, profile_photo_url, username) VALUES (false, '2025-11-18 22:48:35.494746', null, 3, 'user@example.com', 'user', 'user', '$2a$10$VciHuO4uwKhPU9IymDxwSuSAWiTtXL8LpVFG93IoV/wztMEnGblZq', null, null, 'user');
INSERT INTO `forum-startup`.users (is_blocked, created_at, updated_at, user_id, email, first_name, last_name, password, phone_number, profile_photo_url, username) VALUES (true, '2025-11-19 00:03:29.495288', '2025-11-19 00:05:43.299410', 5, 'block@example.com', 'block', 'user', '$2a$10$8EsnhtYJmXuHcNz8pIsj2OP5GilrIj.hlQCHTfdLi1CFVuw96Xkyu', null, null, 'blocked');


INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (1, 1);
INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (1, 5);
INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (2, 1);
