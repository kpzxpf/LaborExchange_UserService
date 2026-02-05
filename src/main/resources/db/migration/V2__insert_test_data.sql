INSERT INTO roles (id, role_name)
VALUES (1, 'JOB_SEEKER'),
       (2, 'EMPLOYER'),
       (3, 'ADMIN');

-- Кандидаты (ID 1-20)
INSERT INTO users (username, password, email, first_name, last_name, role_id)
VALUES ('ivan_i', 'hash', 'ivan@mail.ru', 'Иван', 'Иванов', 1),
       ('petr_p', 'hash', 'petr@mail.ru', 'Петр', 'Петров', 1),
       ('anna_s', 'hash', 'anna@mail.ru', 'Анна', 'Смирнова', 1),
       ('mariya_k', 'hash', 'mariya@mail.ru', 'Мария', 'Кузнецова', 1),
       ('sergey_v', 'hash', 'sergey@mail.ru', 'Сергей', 'Волков', 1),
       ('elena_m', 'hash', 'elena@mail.ru', 'Елена', 'Морозова', 1),
       ('dmitry_n', 'hash', 'dmitry@mail.ru', 'Дмитрий', 'Новиков', 1),
       ('olga_f', 'hash', 'olga@mail.ru', 'Ольга', 'Федорова', 1),
       ('andrey_s', 'hash', 'andrey@mail.ru', 'Андрей', 'Соколов', 1),
       ('natalia_p', 'hash', 'natalia@mail.ru', 'Наталья', 'Попова', 1),
       ('igor_l', 'hash', 'igor@mail.ru', 'Игорь', 'Лебедев', 1),
       ('julia_v', 'hash', 'julia@mail.ru', 'Юлия', 'Васильева', 1),
       ('artem_z', 'hash', 'artem@mail.ru', 'Артем', 'Зайцев', 1),
       ('svetlana_t', 'hash', 'svetlana@mail.ru', 'Светлана', 'Тихонова', 1),
       ('maxim_g', 'hash', 'maxim@mail.ru', 'Максим', 'Григорьев', 1),
       ('victoria_e', 'hash', 'victoria@mail.ru', 'Виктория', 'Егорова', 1),
       ('anton_k', 'hash', 'anton@mail.ru', 'Антон', 'Козлов', 1),
       ('darina_b', 'hash', 'darina@mail.ru', 'Дарина', 'Беляева', 1),
       ('roman_o', 'hash', 'roman@mail.ru', 'Роман', 'Орлов', 1),
       ('ksenia_a', 'hash', 'ksenia@mail.ru', 'Ксения', 'Абрамова', 1);

-- Работодатели (ID 21-40)
INSERT INTO users (username, password, email, first_name, last_name, role_id)
VALUES ( 'hr_yandex', 'hash', 'hr1@yandex.ru', 'Алина', 'Белова', 2),
       ( 'hr_sber', 'hash', 'hr2@sber.ru', 'Борис', 'Чернов', 2),
       ( 'hr_tinkoff', 'hash', 'hr3@tinkoff.ru', 'Вера', 'Павлова', 2),
       ( 'hr_ozon', 'hash', 'hr4@ozon.ru', 'Глеб', 'Щербаков', 2),
       ( 'hr_avito', 'hash', 'hr5@avito.ru', 'Денис', 'Соловьев', 2),
       ( 'hr_vk', 'hash', 'hr6@vk.com', 'Ева', 'Данилова', 2),
       ( 'hr_kasper', 'hash', 'hr7@kasper.ru', 'Жанна', 'Фролова', 2),
       ( 'hr_mts', 'hash', 'hr8@mts.ru', 'Захар', 'Громов', 2),
       ( 'hr_mega', 'hash', 'hr9@mega.ru', 'Илья', 'Поляков', 2),
       ( 'hr_2gis', 'hash', 'hr10@2gis.ru', 'Кира', 'Кириллова', 2),
       ( 'hr_skyeng', 'hash', 'hr11@skyeng.ru', 'Лев', 'Николаев', 2),
       ( 'hr_selectel', 'hash', 'hr12@selectel.ru', 'Майя', 'Тарасова', 2),
       ( 'hr_pos', 'hash', 'hr13@pos.ru', 'Нина', 'Никитина', 2),
       ( 'hr_lux', 'hash', 'hr14@lux.ru', 'Олег', 'Осипов', 2),
       ( 'hr_epam', 'hash', 'hr15@epam.ru', 'Павел', 'Петров', 2),
       ( 'hr_wb', 'hash', 'hr16@wb.ru', 'Роза', 'Рыбакова', 2),
       ( 'hr_lamoda', 'hash', 'hr17@lamoda.ru', 'Стас', 'Савельев', 2),
       ( 'hr_qiwi', 'hash', 'hr18@qiwi.ru', 'Тая', 'Трофимова', 2),
       ( 'hr_alfa', 'hash', 'hr19@alfa.ru', 'Ульяна', 'Уварова', 2),
       ( 'hr_vtb', 'hash', 'hr20@vtb.ru', 'Федор', 'Фомин', 2);