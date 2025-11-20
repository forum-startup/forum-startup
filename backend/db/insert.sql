-- Users

INSERT INTO `forum-startup`.users (is_blocked, created_at, updated_at, user_id, email, first_name, last_name, password, phone_number, profile_photo_url, username) VALUES (false, '2025-11-18 20:34:06.000000', null, 1, 'admin@example.com', 'Admin', 'User', '$2a$10$/WGgv2GGHNf/HGijmV98ouR9lLUEs3In4yZzabbyyNdu6EUhlFDnW', '+359888000000', null, 'admin');
INSERT INTO `forum-startup`.users (is_blocked, created_at, updated_at, user_id, email, first_name, last_name, password, phone_number, profile_photo_url, username) VALUES (false, '2025-11-18 22:48:35.494746', null, 3, 'user@example.com', 'user', 'user', '$2a$10$VciHuO4uwKhPU9IymDxwSuSAWiTtXL8LpVFG93IoV/wztMEnGblZq', null, null, 'user');
INSERT INTO `forum-startup`.users (is_blocked, created_at, updated_at, user_id, email, first_name, last_name, password, phone_number, profile_photo_url, username) VALUES (true, '2025-11-19 00:03:29.495288', '2025-11-19 00:05:43.299410', 5, 'block@example.com', 'block', 'user', '$2a$10$8EsnhtYJmXuHcNz8pIsj2OP5GilrIj.hlQCHTfdLi1CFVuw96Xkyu', null, null, 'blocked');
INSERT INTO `forum-startup`.users (is_blocked, created_at, updated_at, user_id, email, first_name, last_name, password, phone_number, profile_photo_url, username) VALUES (false, '2025-11-20 15:12:24.000000', null, 6, 'regularuser@example.com', 'Regular', 'User', '$2a$10$TargXmliDKlwW3HFwqzcBuu7UAebJTBUZ86eY7B.1Z34Mb7BUaLNy', '0888123456', null, 'regularuser');

-- Roles

INSERT INTO `forum-startup`.roles (role_id, name) VALUES (1, 'ROLE_USER');
INSERT INTO `forum-startup`.roles (role_id, name) VALUES (2, 'ROLE_ADMIN');

-- User-Roles

INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (1, 1);
INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (1, 5);
INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (1, 6);
INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (2, 1);

-- Posts

INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (0, '2025-11-20 15:15:39.000000', 1, null, 1, 'Welcome to the Forum', 'This is the first post by the admin.');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (0, '2025-11-20 15:15:39.000000', 2, null, 6, 'Random Length Text Generator', 'With this online tool, you can create text of a specific length and inner composition. In the "Text Length" option, you can enter the exact number of characters and the program will generate text of this length. The length value takes into account all characters in the text – including letters, numbers, spaces, and any other symbols. In the "Text Composition" option, you can specify the type of characters that should be included in the generated text. You can choose from several popular options, such as "Lowercase Letters (a-z)", "Uppercase Letters (A-Z)", "Mixed Letters (a-zA-Z)", "Numbers (0-9)", "Lowercase Letters and Numbers", "Uppercase Letters and Numbers", "All Letters and Numbers", and "Custom Symbols".');

-- Tags

INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (3, 'no_limit');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (1, 'start_up');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (2, 'tech');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (4, 'web2');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (5, 'web3');

-- Posts-Tags

INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (1, 1);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (2, 1);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (2, 2);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (1, 4);

-- Comments

INSERT INTO `forum-startup`.comments (likes_count, comment_id, created_at, parent_comment_id, post_id, updated_at, user_id, content) VALUES (0, 1, '2025-11-20 15:17:00.000000', null, 1, null, 6, 'Great to be here!');
INSERT INTO `forum-startup`.comments (likes_count, comment_id, created_at, parent_comment_id, post_id, updated_at, user_id, content) VALUES (0, 2, '2025-11-20 15:17:00.000000', null, 2, null, 1, 'That is an awesome tool! How do you work with it? Where can i access it?');
