DROP TABLE IF EXISTS
    employees,
    positions,
    accounts,
    cars,
    requests,
    clients,
    services,
    categories_services,
    auto_goods,
    categories_auto_goods
    CASCADE;
DROP INDEX IF EXISTS idx_auto_goods_category_id;
DROP INDEX IF EXISTS idx_services_category_id;


-- Вставка данных в таблицы
INSERT INTO positions (position_name)
VALUES ('оператор');

INSERT INTO accounts (login, password, role)
VALUES ('1', '1', 'OPERATOR');

INSERT INTO employees (account_id, position_id, employment_status, surname, name,
                       patronymic, phone_number, salary, hire_date, birth_date, dtype)
VALUES ((SELECT id FROM accounts WHERE login = '1'), -- Используем ID учётной записи
        (SELECT id FROM positions WHERE position_name = 'оператор'), -- Используем ID должности
        'Активен', -- Статус трудоустройства
        'Тихонов', -- Фамилия
        'Виктор', -- Имя
        'Владимирович', -- Отчество
        '88888888888', -- Номер телефона
        50000, -- Зарплата
        '2022-01-01', -- Дата трудоустройства
        '1990-05-10', -- Дата рождения
        'OPERATOR' -- Роль сотрудника
       );

-- Вставка записей в таблицу positions
INSERT INTO positions (position_name)
VALUES ('Старший оператор'),         -- 1
       ('Помощник оператора'),       -- 2
       ('Стажер-оператор'),          -- 3
       ('Главный механик'),          -- 4
       ('Механик первой категории'), -- 5
       ('Стажёр-механик');
-- 6


-- Создание таблицы Клиенты
CREATE TABLE clients
(
    id                BIGSERIAL PRIMARY KEY,       -- ID клиента, автоинкремент
    surname           VARCHAR(50) NOT NULL,        -- Фамилия, обязательное поле
    name              VARCHAR(50) NOT NULL,        -- Имя, обязательное поле
    patronymic        VARCHAR(50),                 -- Отчество, может быть пустым
    phone_number      VARCHAR(11) NOT NULL UNIQUE, -- Номер телефона, длина 11 символов
    email             VARCHAR(50),                 -- Почта, может быть пустым
    registration_date DATE DEFAULT CURRENT_DATE,   -- Дата регистрации, по умолчанию текущая

    -- Ограничение на только цифры в номере телефона и точную длину 11 символов
    CONSTRAINT phone_number_digits_clients CHECK (phone_number ~ '^[0-9]{11}$')
);


-- Создание таблицы Автомобили
CREATE TABLE cars
(
    id                  BIGSERIAL PRIMARY KEY, -- ID автомобиля
    state_number        VARCHAR(20),           -- Госномер, длина до 20 символов, чтобы учесть международные номера
    vin                 VARCHAR(17) NOT NULL,  -- VIN номер, длина 17 символов
    brand               VARCHAR(50) NOT NULL,  -- Марка автомобиля, обязательное поле
    model               VARCHAR(50) NOT NULL,  -- Модель автомобиля, обязательное поле
    year_of_manufacture INTEGER     NOT NULL,  -- Год выпуска, теперь целое число

    -- Год выпуска, от 1900 до текущего года
    CONSTRAINT year_of_manufacture_check
        CHECK (year_of_manufacture >= 1900 AND year_of_manufacture <= EXTRACT(YEAR FROM CURRENT_DATE)),
    -- Ограничение на формат госномера (если госномер не пустой)
    CONSTRAINT state_number_check
        CHECK (state_number IS NULL OR state_number ~ '^[A-Za-zА-Яа-я0-9]{1,20}$'),
    -- Ограничение на формат и точную длину VIN номера (17 символов)
    CONSTRAINT vin_format_check CHECK (vin ~ '^[A-HJ-NPR-Z0-9]{17}$')
);


-- Создание таблицы Учётные записи
CREATE TABLE accounts
(
    id       BIGSERIAL PRIMARY KEY,        -- ID учётной записи
    login    VARCHAR(30)  NOT NULL UNIQUE, -- Логин, уникальное поле
    password VARCHAR(100) NOT NULL,        -- Пароль, обязательное поле
    role     VARCHAR(20)  NOT NULL         -- Ссылка на роль, внешний ключ
);


-- Создание таблицы positions (Должности)
CREATE TABLE positions
(
    id            BIGSERIAL PRIMARY KEY,      -- ID должности
    position_name VARCHAR(25) NOT NULL UNIQUE -- Наименование должности, обязательное и уникальное поле
);


-- Создание таблицы Сотрудники
CREATE TABLE employees
(
    id                BIGSERIAL PRIMARY KEY,          -- ID сотрудника
    account_id        BIGINT         NOT NULL UNIQUE, -- ID учётной записи, обязательное поле
    position_id       BIGINT         NOT NULL,        -- ID должности, обязательное поле
    employment_status VARCHAR(20)    NOT NULL,        -- Статус трудоустройства (enum), обязательное поле
    surname           VARCHAR(50)    NOT NULL,        -- Фамилия сотрудника, обязательное поле
    name              VARCHAR(50)    NOT NULL,        -- Имя сотрудника, обязательное поле
    patronymic        VARCHAR(50),                    -- Отчество сотрудника, может быть пустым
    phone_number      VARCHAR(11)    NOT NULL UNIQUE, -- Номер телефона, обязательное поле, длина 11
    salary            DECIMAL(10, 2) NOT NULL,        -- Заработная плата, обязательное поле
    hire_date         DATE           NOT NULL,        -- Дата трудоустройства, обязательное поле
    dismissal_date    DATE,                           -- Дата увольнения, может быть пустым
    birth_date        DATE           NOT NULL,        -- Дата рождения, обязательное поле
    dtype             VARCHAR(20)    NOT NULL,        -- Тип сотрудника (роль)

    -- Внешний ключ на таблицу accounts
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    -- Внешний ключ на таблицу positions
    CONSTRAINT fk_position FOREIGN KEY (position_id) REFERENCES positions (id),
    -- Ограничение на значения employment_status
    CONSTRAINT chk_employment_status CHECK (employment_status IN ('Активен', 'Уволен', 'Не активен')),
    -- Ограничение на формат номера телефона
    CONSTRAINT phone_number_digits_employees CHECK (phone_number ~ '^[0-9]{11}$'),
    -- Ограничение на допустимые значения dtype (роль сотрудника)
    CONSTRAINT chk_dtype CHECK (dtype IN ('ADMIN', 'MECHANIC', 'OPERATOR'))
);


-- Создание таблицы Заявки
CREATE TABLE requests
(
    id              BIGSERIAL PRIMARY KEY,               -- ID заявки
    client_id       BIGINT      NOT NULL,                -- ID клиента
    car_id          BIGINT      NOT NULL,                -- ID автомобиля
    operator_id     BIGINT      NOT NULL,                -- ID оператора
    request_status  VARCHAR(20) NOT NULL,                -- Статус заявки (enum), обязательное поле
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата подачи заявки, по умолчанию текущая
    complaints      TEXT,                                -- Жалобы, может быть пустым

    -- Внешний ключ на таблицу clients
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES clients (id),
    -- Внешний ключ на таблицу cars
    CONSTRAINT fk_car FOREIGN KEY (car_id) REFERENCES cars (id),
    -- Внешний ключ на таблицу employees
    CONSTRAINT fk_operator FOREIGN KEY (operator_id) REFERENCES employees (id),
    -- Ограничение на допустимые значения статуса заявки
    CONSTRAINT chk_status CHECK (request_status IN ('В ожидании', 'Исполняется', 'Завершена', 'Отклонена'))
);


-- Создание таблицы Категории автотоваров
CREATE TABLE categories_auto_goods
(
    id   BIGSERIAL PRIMARY KEY, -- ID категории, автоинкремент
    name VARCHAR(35) NOT NULL   -- Наименование категории
);


-- Создание таблицы Авотовары
CREATE TABLE auto_goods
(
    id              BIGSERIAL PRIMARY KEY,   -- ID автотовара, автоинкремент
    category_id     BIGINT         NOT NULL, -- ID категории
    name            VARCHAR(35)    NOT NULL, -- Наименование автотовара
    quantity        INT            NOT NULL, -- Количество
    price_one_unit  DECIMAL(10, 2) NOT NULL, -- Цена за 1 штуку
    expiration_date DATE,                    -- Срок годности
    relevance       BOOLEAN DEFAULT TRUE,    -- Релевантность (логическое удаление)

    -- Внешний ключ на таблицу categories_auto_goods
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories_auto_goods (id),

    -- Ограничение: цена не может быть меньше или равна 0
    CONSTRAINT chk_price_positive CHECK (price_one_unit > 0),

    -- Ограничение: количество не может быть меньше 0
    CONSTRAINT chk_quantity_non_negative CHECK (quantity >= 0)
);
CREATE INDEX idx_auto_goods_category_id ON auto_goods (category_id);


-- Создание таблицы Категории услуг
CREATE TABLE categories_services
(
    id   BIGSERIAL PRIMARY KEY, -- ID категории услуги, автоинкремент
    name VARCHAR(35) NOT NULL   -- Наименование категории услуги
);


-- Создание таблицы Услуги
CREATE TABLE services
(
    id          BIGSERIAL PRIMARY KEY, -- ID услуги, автоинкремент
    name        VARCHAR(35) NOT NULL,  -- Наименование услуги
    description TEXT,                  -- Описание услуги
    category_id BIGINT      NOT NULL,  -- ID категории услуги
    relevance   BOOLEAN DEFAULT TRUE,  -- Релевантность (логическое удаление)

    -- Внешний ключ на таблицу categories_of_services
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories_services (id),

    -- Уникальность пары: категория + услуга
    CONSTRAINT unique_service_name_per_category UNIQUE (name, category_id)
);
-- Добавление индекса на category_id
CREATE INDEX idx_services_category_id ON services (category_id);


-- Удаление индексов
DROP INDEX IF EXISTS idx_work_order_services_service_id;
DROP INDEX IF EXISTS idx_work_order_services_work_order_id;
DROP INDEX IF EXISTS idx_work_order_auto_goods_auto_good_id;
DROP INDEX IF EXISTS idx_work_order_auto_goods_work_order_id;

-- Удаление внешних ключей
ALTER TABLE work_order_services
    DROP CONSTRAINT IF EXISTS fk_service;
ALTER TABLE work_order_services
    DROP CONSTRAINT IF EXISTS fk_work_order_services;
ALTER TABLE work_order_auto_goods
    DROP CONSTRAINT IF EXISTS fk_auto_good;
ALTER TABLE work_order_auto_goods
    DROP CONSTRAINT IF EXISTS fk_work_order_auto_goods;
ALTER TABLE work_orders
    DROP CONSTRAINT IF EXISTS fk_mechanic;
ALTER TABLE work_orders
    DROP CONSTRAINT IF EXISTS fk_request;

-- Удаление таблиц
DROP TABLE IF EXISTS work_order_services CASCADE;
DROP TABLE IF EXISTS work_order_auto_goods CASCADE;
DROP TABLE IF EXISTS work_orders CASCADE;


-- Создание таблицы work_orders (Заказ-наряды)
CREATE TABLE work_orders
(
    id                BIGSERIAL PRIMARY KEY,               -- ID заказ-наряда, автоинкремент
    request_id        BIGINT      NOT NULL,                -- ID заявки, обязательное поле
    mechanic_id       BIGINT      NOT NULL,                -- ID механика, обязательное поле
    work_order_status VARCHAR(20) NOT NULL,                -- Статус заказ-наряда, обязательное поле
--                              price DECIMAL(10, 2) NOT NULL DEFAULT 0,          -- Цена работ, обязательное поле с начальным значением 0
    start_date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Дата начала работ, может быть пустым
    end_date          TIMESTAMP,                           -- Дата окончания работ, может быть пустым

    -- Внешние ключи
    CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES requests (id),
    CONSTRAINT fk_mechanic FOREIGN KEY (mechanic_id) REFERENCES employees (id),

    -- Ограничение на статус заказ-наряда
    CONSTRAINT chk_status CHECK (work_order_status IN ('В процессе', 'Завершен', 'Отменен'))
);


-- Создание таблицы work_order_services (Заказ_наряд_Автотовары)
CREATE TABLE work_order_auto_goods
(
    work_order_id  BIGINT         NOT NULL,                            -- ID заказ-наряда, обязательное поле
    auto_good_id   BIGINT         NOT NULL,                            -- ID автотовара, обязательное поле
    quantity       INT            NOT NULL CHECK (quantity > 0),       -- Количество автотовара, обязательное поле
    price_one_unit DECIMAL(10, 2) NOT NULL CHECK (price_one_unit > 0), -- Цена за 1 штуку, обязательное поле

    -- Внешний ключ на таблицу work_orders
    CONSTRAINT fk_work_order_auto_goods FOREIGN KEY (work_order_id) REFERENCES work_orders (id),
    -- Внешний ключ на таблицу auto_goods
    CONSTRAINT fk_auto_good FOREIGN KEY (auto_good_id) REFERENCES auto_goods (id),

    -- Составной первичный ключ
    CONSTRAINT pk_work_order_auto_goods PRIMARY KEY (work_order_id, auto_good_id)
);
-- Индексы для работы с полями work_order_id и auto_good_id
CREATE INDEX idx_work_order_auto_goods_work_order_id ON work_order_auto_goods (work_order_id);
CREATE INDEX idx_work_order_auto_goods_auto_good_id ON work_order_auto_goods (auto_good_id);


-- Создание таблицы work_order_services (Заказ_наряд_Услуги)
CREATE TABLE work_order_services
(
    work_order_id BIGINT         NOT NULL,                    -- ID заказ-наряда, обязательное поле
    service_id    BIGINT         NOT NULL,                    -- ID услуги, обязательное поле
    price         DECIMAL(10, 2) NOT NULL CHECK (price >= 0), -- Цена услуги, обязательное поле

    -- Внешний ключ на таблицу work_orders
    CONSTRAINT fk_work_order_services FOREIGN KEY (work_order_id) REFERENCES work_orders (id),
    -- Внешний ключ на таблицу services
    CONSTRAINT fk_service FOREIGN KEY (service_id) REFERENCES services (id),

    -- Составной первичный ключ
    CONSTRAINT pk_work_order_services PRIMARY KEY (work_order_id, service_id)
);
-- Индексы для работы с полями work_order_id и service_id
CREATE INDEX idx_work_order_services_work_order_id ON work_order_services (work_order_id);
CREATE INDEX idx_work_order_services_service_id ON work_order_services (service_id);
