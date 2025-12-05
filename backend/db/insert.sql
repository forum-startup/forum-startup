-- Users

INSERT INTO `forum-startup`.users (is_blocked, created_at, updated_at, user_id, email, first_name, last_name, password, phone_number, profile_photo_url, username) VALUES (false, '2025-12-05 09:40:57.000000', '2025-12-05 09:40:57.000000', 1, 'admin@example.com', 'Admin', 'User', '$2a$10$b45lkfynrmyaKsORXQDj/eJY1R4Lytf5R2v5gmHhVc6BKau1oiIUq', null, null, 'admin');
INSERT INTO `forum-startup`.users (is_blocked, created_at, updated_at, user_id, email, first_name, last_name, password, phone_number, profile_photo_url, username) VALUES (false, '2025-12-05 09:40:57.000000', '2025-12-05 09:40:57.000000', 2, 'john@example.com', 'John', 'Doe', '$2a$10$lB2eAYiRxOPxOphw2q/F/OnYXf7VsX7fW07xtWHot5zlp2ZpUpOH6', null, null, 'john');
INSERT INTO `forum-startup`.users (is_blocked, created_at, updated_at, user_id, email, first_name, last_name, password, phone_number, profile_photo_url, username) VALUES (false, '2025-12-05 11:55:12.774330', null, 3, 'ivelinyanev00@gmail.com', 'Ivelin', 'Yanev', '$2a$10$v7r0.1Rioj1Y4ooay.3wkujzkM2YqTo2utIyWfIUXxHX3xYD7gZsO', null, null, 'ivelinyanev');
INSERT INTO `forum-startup`.users (is_blocked, created_at, updated_at, user_id, email, first_name, last_name, password, phone_number, profile_photo_url, username) VALUES (false, '2025-12-05 11:58:00.181393', null, 4, 'martindimitrov@gmail.com', 'Martin', 'Dimitrov', '$2a$10$A3kwzd0x4FMKG/96LUDUcOmDn3IJUtLitFU4pm4noFG5pgdWtBhce', null, null, 'martindimitrov');


-- Roles

INSERT INTO `forum-startup`.roles (role_id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO `forum-startup`.roles (role_id, name) VALUES (2, 'ROLE_USER');


-- User-Roles

INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (1, 1);
INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (2, 1);
INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (2, 2);
INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (2, 3);
INSERT INTO `forum-startup`.user_roles (role_id, user_id) VALUES (2, 4);


-- Posts

INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (1, '2025-12-05 09:44:50.000000', 1, '2025-12-05 11:52:16.737824', 1, 'AI-Powered Personal Finance Assistant', 'Idea: A startup offering an AI-based personal budgeting assistant that learns user habits and automatically creates optimized monthly financial plans. Targeting young professionals who struggle with money management.');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (1, '2025-12-05 09:44:50.000000', 2, '2025-12-05 11:52:40.075204', 2, 'Decentralized Talent Marketplace', 'Concept: A Web3 platform where freelancers verify skills through on-chain credentials. Your reputation is your wallet. Businesses can hire based on provable work history.');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (1, '2025-12-05 09:44:50.000000', 3, '2025-12-05 12:01:54.185894', 1, 'EdTech App for Micro-Learning', 'A mobile app that delivers 2–3 minute micro-lessons tailored to user learning goals. Gamified progression and AI-based difficulty adjustment.');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (2, '2025-12-05 09:44:50.000000', 4, '2025-12-05 12:01:58.843610', 2, 'Hardware Startup: Smart Desk Accessory', 'Idea for a modular IoT desk device that measures productivity using sensors and provides feedback. Great for remote workers and startup teams.');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (0, '2025-12-05 09:44:50.000000', 5, null, 1, 'SaaS Tool for MVP Validation', 'A tool for startups to quickly validate early ideas: landing pages, traffic heatmaps, waitlists, and simple analytics — all without coding.');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (2, '2025-12-05 09:44:50.000000', 6, '2025-12-05 12:01:58.002171', 2, 'Pitch Deck AI Evaluator', 'An AI that analyzes your startup pitch deck and gives a VC-style score with suggestions for improvement.');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (1, '2025-12-05 09:56:25.000000', 7, '2025-12-05 12:01:53.017108', 3, 'AI-Powered Market Research Assistant', 'I’m working on a tool that uses AI to validate startup ideas by analyzing trends, search volume, competitor activity, and social sentiment. Would love feedback on features to include.');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (0, '2025-12-05 09:56:25.000000', 8, '2025-12-05 09:56:25.000000', 3, 'Blockchain-Based Invoice Financing', 'Idea: A platform where freelancers can tokenize their invoices and receive instant liquidity from investors. Looking for someone familiar with smart contracts for an MVP.');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (1, '2025-12-05 09:56:25.000000', 9, '2025-12-05 12:01:55.230292', 3, 'Micro-SaaS for Local Businesses', 'Thinking about creating small, ultra-focused SaaS tools for local shops: queue management, reservation widgets, and automated customer reminder systems. Thoughts on niche selection?');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (0, '2025-12-05 09:59:03.000000', 10, null, 4, 'Micro-SaaS for Freelancers', 'A lightweight tool that helps freelancers track clients, invoices, and deadlines with AI suggestions.');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (0, '2025-12-05 09:59:03.000000', 11, null, 4, 'API for Real-Time Productivity Tracking', 'A backend service that tracks focus time, break patterns, and integrates with Notion and Google Calendar.');
INSERT INTO `forum-startup`.posts (likes_count, created_at, post_id, updated_at, user_id, title, content) VALUES (0, '2025-12-05 09:59:03.000000', 12, null, 4, 'Business Name Generator with Branding Kit', 'Enter keywords and instantly generate brand names, logos, domains, and color palettes.');



-- Tags

INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (1, 'AI');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (7, 'Bootstrapping');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (5, 'EdTech');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (3, 'FinTech');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (11, 'Funding');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (14, 'Hardware');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (4, 'HealthTech');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (10, 'Marketing');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (15, 'MobileApps');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (9, 'MVP');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (13, 'Pitching');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (6, 'Productivity');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (8, 'SaaS');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (12, 'Startups');
INSERT INTO `forum-startup`.tags (tag_id, name) VALUES (2, 'Web3');


-- Posts-Tags

INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (1, 1);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (6, 1);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (2, 2);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (1, 3);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (3, 5);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (1, 6);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (4, 6);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (5, 7);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (5, 8);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (3, 9);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (5, 9);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (2, 11);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (6, 11);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (2, 12);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (4, 12);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (6, 13);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (4, 14);
INSERT INTO `forum-startup`.posts_tags (post_id, tag_id) VALUES (3, 15);


-- Comments

INSERT INTO `forum-startup`.comments (is_deleted, likes_count, comment_id, created_at, deleted_at, deleted_by, parent_comment_id, post_id, updated_at, user_id, content) VALUES (false, 0, 1, '2025-12-05 09:45:10.000000', null, null, null, 1, null, 2, 'This is actually a solid niche. Are you planning to integrate Open Banking APIs?');
INSERT INTO `forum-startup`.comments (is_deleted, likes_count, comment_id, created_at, deleted_at, deleted_by, parent_comment_id, post_id, updated_at, user_id, content) VALUES (false, 0, 2, '2025-12-05 09:45:10.000000', null, null, 1, 1, null, 1, 'Yes, the idea is to use PSD2 APIs so users can link accounts securely.');
INSERT INTO `forum-startup`.comments (is_deleted, likes_count, comment_id, created_at, deleted_at, deleted_by, parent_comment_id, post_id, updated_at, user_id, content) VALUES (false, 0, 3, '2025-12-05 09:45:10.000000', null, null, null, 2, null, 1, 'Love the concept. On-chain credentials fix so many issues in HR.');
INSERT INTO `forum-startup`.comments (is_deleted, likes_count, comment_id, created_at, deleted_at, deleted_by, parent_comment_id, post_id, updated_at, user_id, content) VALUES (false, 0, 4, '2025-12-05 09:45:10.000000', null, null, null, 3, null, 2, 'Micro-learning hits hard in 2025. The market is growing fast.');
INSERT INTO `forum-startup`.comments (is_deleted, likes_count, comment_id, created_at, deleted_at, deleted_by, parent_comment_id, post_id, updated_at, user_id, content) VALUES (false, 0, 5, '2025-12-05 09:45:10.000000', null, null, null, 4, null, 1, 'This could pair nicely with a productivity AI coach.');
INSERT INTO `forum-startup`.comments (is_deleted, likes_count, comment_id, created_at, deleted_at, deleted_by, parent_comment_id, post_id, updated_at, user_id, content) VALUES (false, 0, 6, '2025-12-05 09:45:10.000000', null, null, null, 5, null, 2, 'As a founder, I would absolutely pay for this.');
INSERT INTO `forum-startup`.comments (is_deleted, likes_count, comment_id, created_at, deleted_at, deleted_by, parent_comment_id, post_id, updated_at, user_id, content) VALUES (false, 0, 7, '2025-12-05 09:45:10.000000', null, null, null, 6, null, 1, 'Brilliant idea — founders suck at pitch deck clarity.');

-- Post Likes

INSERT INTO `forum-startup`.post_likes (post_id, user_id) VALUES (1, 1);
INSERT INTO `forum-startup`.post_likes (post_id, user_id) VALUES (2, 1);
INSERT INTO `forum-startup`.post_likes (post_id, user_id) VALUES (4, 1);
INSERT INTO `forum-startup`.post_likes (post_id, user_id) VALUES (6, 1);
INSERT INTO `forum-startup`.post_likes (post_id, user_id) VALUES (3, 4);
INSERT INTO `forum-startup`.post_likes (post_id, user_id) VALUES (4, 4);
INSERT INTO `forum-startup`.post_likes (post_id, user_id) VALUES (6, 4);
INSERT INTO `forum-startup`.post_likes (post_id, user_id) VALUES (7, 4);
INSERT INTO `forum-startup`.post_likes (post_id, user_id) VALUES (9, 4);

