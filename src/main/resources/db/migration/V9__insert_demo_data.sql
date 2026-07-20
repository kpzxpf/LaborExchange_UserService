-- Demo users and user-side state for a full product walkthrough.
-- Password for all demo accounts below: DemoPass123!

UPDATE users
SET password = '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.',
    email_verified = TRUE,
    active = TRUE,
    phone_number = COALESCE(phone_number, '+7900' || LPAD(id::text, 7, '0')),
    updated_at = NOW()
WHERE id BETWEEN 1 AND 40;

INSERT INTO users (id, username, password, email, first_name, last_name, phone_number, role_id, email_verified, active, created_at, updated_at)
VALUES
    (100, 'admin_demo', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'admin@laborexchange.demo', 'Алексей', 'Администратор', '+79001000100', 3, TRUE, TRUE, NOW() - INTERVAL '90 days', NOW()),
    (101, 'alex_backend', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'alex.backend@laborexchange.demo', 'Александр', 'Петров', '+79001000101', 1, TRUE, TRUE, NOW() - INTERVAL '65 days', NOW()),
    (102, 'maria_frontend', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'maria.frontend@laborexchange.demo', 'Мария', 'Соколова', '+79001000102', 1, TRUE, TRUE, NOW() - INTERVAL '60 days', NOW()),
    (103, 'ivan_data', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'ivan.data@laborexchange.demo', 'Иван', 'Кузнецов', '+79001000103', 1, TRUE, TRUE, NOW() - INTERVAL '54 days', NOW()),
    (104, 'olga_qa', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'olga.qa@laborexchange.demo', 'Ольга', 'Морозова', '+79001000104', 1, TRUE, TRUE, NOW() - INTERVAL '48 days', NOW()),
    (105, 'nikita_devops', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'nikita.devops@laborexchange.demo', 'Никита', 'Волков', '+79001000105', 1, TRUE, TRUE, NOW() - INTERVAL '44 days', NOW()),
    (106, 'pending_email', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'pending.email@laborexchange.demo', 'Дарья', 'Лебедева', '+79001000106', 1, FALSE, TRUE, NOW() - INTERVAL '3 days', NOW()),
    (107, 'inactive_candidate', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'inactive.candidate@laborexchange.demo', 'Павел', 'Сергеев', '+79001000107', 1, TRUE, FALSE, NOW() - INTERVAL '120 days', NOW()),
    (121, 'hr_nebula', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'hr@nebulasoft.demo', 'Екатерина', 'Орлова', '+79001000121', 2, TRUE, TRUE, NOW() - INTERVAL '80 days', NOW()),
    (122, 'hr_finpulse', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'talent@finpulse.demo', 'Дмитрий', 'Громов', '+79001000122', 2, TRUE, TRUE, NOW() - INTERVAL '73 days', NOW()),
    (123, 'hr_medcloud', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'people@medcloud.demo', 'Светлана', 'Никитина', '+79001000123', 2, TRUE, TRUE, NOW() - INTERVAL '61 days', NOW()),
    (124, 'hr_gameforge', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'jobs@gameforge.demo', 'Роман', 'Миронов', '+79001000124', 2, TRUE, TRUE, NOW() - INTERVAL '57 days', NOW()),
    (125, 'hr_retailcore', '$2b$10$5NGd2xAJpm3jC1PqCd0BcuohO29s65FVWTQGkLWPTaCqUgwIwRpG.', 'career@retailcore.demo', 'Анна', 'Павлова', '+79001000125', 2, TRUE, TRUE, NOW() - INTERVAL '50 days', NOW())
ON CONFLICT (id) DO UPDATE SET
    username = EXCLUDED.username,
    password = EXCLUDED.password,
    email = EXCLUDED.email,
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    phone_number = EXCLUDED.phone_number,
    role_id = EXCLUDED.role_id,
    email_verified = EXCLUDED.email_verified,
    active = EXCLUDED.active,
    updated_at = NOW();

INSERT INTO favorites (user_id, item_id, item_type, created_at)
VALUES
    (101, 100, 'VACANCY', NOW() - INTERVAL '9 days'),
    (101, 101, 'VACANCY', NOW() - INTERVAL '8 days'),
    (102, 101, 'VACANCY', NOW() - INTERVAL '7 days'),
    (103, 104, 'VACANCY', NOW() - INTERVAL '6 days'),
    (104, 105, 'VACANCY', NOW() - INTERVAL '5 days'),
    (105, 102, 'VACANCY', NOW() - INTERVAL '4 days'),
    (121, 100, 'RESUME', NOW() - INTERVAL '6 days'),
    (121, 102, 'RESUME', NOW() - INTERVAL '5 days'),
    (122, 103, 'RESUME', NOW() - INTERVAL '4 days'),
    (123, 104, 'RESUME', NOW() - INTERVAL '3 days'),
    (124, 108, 'RESUME', NOW() - INTERVAL '2 days')
ON CONFLICT (user_id, item_id, item_type) DO NOTHING;

INSERT INTO email_verification_tokens (user_id, token, created_at, expires_at, used)
VALUES
    (106, 'demo-verify-token-user-106', NOW() - INTERVAL '1 day', NOW() + INTERVAL '6 days', FALSE),
    (101, 'demo-used-verify-token-user-101', NOW() - INTERVAL '60 days', NOW() - INTERVAL '59 days', TRUE)
ON CONFLICT (token) DO UPDATE SET
    user_id = EXCLUDED.user_id,
    expires_at = EXCLUDED.expires_at,
    used = EXCLUDED.used;

INSERT INTO password_reset_tokens (user_id, token, created_at, expires_at, used)
VALUES
    (102, 'demo-reset-token-user-102', NOW() - INTERVAL '10 minutes', NOW() + INTERVAL '50 minutes', FALSE),
    (105, 'demo-expired-reset-token-user-105', NOW() - INTERVAL '3 hours', NOW() - INTERVAL '2 hours', FALSE)
ON CONFLICT (token) DO UPDATE SET
    user_id = EXCLUDED.user_id,
    expires_at = EXCLUDED.expires_at,
    used = EXCLUDED.used;

SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE((SELECT MAX(id) FROM users), 1), TRUE);
SELECT setval(pg_get_serial_sequence('favorites', 'id'), COALESCE((SELECT MAX(id) FROM favorites), 1), TRUE);
SELECT setval(pg_get_serial_sequence('email_verification_tokens', 'id'), COALESCE((SELECT MAX(id) FROM email_verification_tokens), 1), TRUE);
SELECT setval(pg_get_serial_sequence('password_reset_tokens', 'id'), COALESCE((SELECT MAX(id) FROM password_reset_tokens), 1), TRUE);
