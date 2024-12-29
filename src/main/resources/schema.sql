DROP TABLE IF EXISTS
    employees,
    positions,
    accounts,
    cars,
    roles,
    requests,
    clients
    CASCADE;



-- Создание таблицы Clients
CREATE TABLE clients (
                         id BIGSERIAL PRIMARY KEY,               -- ID клиента, автоинкремент
                         surname VARCHAR(50) NOT NULL,           -- Фамилия, обязательное поле
                         name VARCHAR(50) NOT NULL,              -- Имя, обязательное поле
                         patronymic VARCHAR(50),                 -- Отчество, может быть пустым
                         phone_number VARCHAR(11) NOT NULL,      -- Номер телефона, длина 11 символов
                         email VARCHAR(50),                      -- Почта, может быть пустым
                         registration_date DATE DEFAULT CURRENT_DATE,  -- Дата регистрации, по умолчанию текущая

    -- Ограничение на только цифры в номере телефона и точную длину 11 символов
                         CONSTRAINT phone_number_digits_clients CHECK (phone_number ~ '^[0-9]{11}$')
);



-- Создание таблицы Cars
CREATE TABLE cars (
                      id BIGSERIAL PRIMARY KEY,                   -- ID автомобиля
                      state_number VARCHAR(20),                   -- Госномер, длина до 20 символов, чтобы учесть международные номера
                      vin VARCHAR(17) NOT NULL,                   -- VIN номер, длина 17 символов
                      brand VARCHAR(50) NOT NULL,                 -- Марка автомобиля, обязательное поле
                      model VARCHAR(50) NOT NULL,                 -- Модель автомобиля, обязательное поле
                      year_of_manufacture INTEGER NOT NULL,       -- Год выпуска, теперь целое число

    -- Год выпуска, от 1900 до текущего года
                      CONSTRAINT year_of_manufacture_check
                          CHECK (year_of_manufacture >= 1900 AND year_of_manufacture <= EXTRACT(YEAR FROM CURRENT_DATE)),
    -- Ограничение на формат госномера (если госномер не пустой)
                      CONSTRAINT state_number_check
                          CHECK (state_number IS NULL OR state_number ~ '^[A-Za-zА-Яа-я0-9]{1,20}$'),
    -- Ограничение на формат и точную длину VIN номера (17 символов)
                      CONSTRAINT vin_format_check CHECK (vin ~ '^[A-HJ-NPR-Z0-9]{17}$')
);




-- Создание таблицы accounts (Учётные записи)
CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,               -- ID учётной записи
                          login VARCHAR(30) NOT NULL UNIQUE,      -- Логин, уникальное поле
                          password VARCHAR(100) NOT NULL,         -- Пароль, обязательное поле
                          role VARCHAR(20) NOT NULL               -- Ссылка на роль, внешний ключ
);




-- Создание таблицы positions (Должности)
CREATE TABLE positions (
                           id BIGSERIAL PRIMARY KEY,                 -- ID должности
                           position_name VARCHAR(25) NOT NULL UNIQUE -- Наименование должности, обязательное и уникальное поле
);


-- Создание таблицы employees (Сотрудники)
CREATE TABLE employees (
                           id BIGSERIAL PRIMARY KEY,                           -- ID сотрудника
                           account_id BIGINT NOT NULL UNIQUE,                  -- ID учётной записи, обязательное поле
                           position_id BIGINT NOT NULL,                        -- ID должности, обязательное поле
                           employment_status VARCHAR(20) NOT NULL,             -- Статус трудоустройства (enum), обязательное поле
                           surname VARCHAR(50) NOT NULL,                       -- Фамилия сотрудника, обязательное поле
                           name VARCHAR(50) NOT NULL,                          -- Имя сотрудника, обязательное поле
                           patronymic VARCHAR(50),                             -- Отчество сотрудника, может быть пустым
                           phone_number VARCHAR(11) NOT NULL,                  -- Номер телефона, обязательное поле, длина 11
                           salary DECIMAL(10, 2) NOT NULL,                     -- Заработная плата, обязательное поле
                           hire_date DATE NOT NULL,                            -- Дата трудоустройства, обязательное поле
                           dismissal_date DATE,                                -- Дата увольнения, может быть пустым
                           birth_date DATE NOT NULL,                           -- Дата рождения, обязательное поле

    -- Внешний ключ на таблицу accounts
                           CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id),
    -- Внешний ключ на таблицу positions
                           CONSTRAINT fk_position FOREIGN KEY (position_id) REFERENCES positions(id),
    -- Ограничение на значения employment_status
                           CONSTRAINT chk_employment_status CHECK (employment_status IN ('ACTIVE', 'DISMISSED', 'INACTIVE')),
    -- Ограничение на формат номера телефона
                           CONSTRAINT phone_number_digits_employees CHECK (phone_number ~ '^[0-9]{11}$')
);







-- Создание таблицы requests (Заявки)
CREATE TABLE requests (
                          id BIGSERIAL PRIMARY KEY,                             -- ID заявки
                          client_id BIGINT NOT NULL,                            -- ID клиента
                          car_id BIGINT NOT NULL,                               -- ID автомобиля
                          operator_id BIGINT NOT NULL,                          -- ID оператора
                          request_status VARCHAR(20) NOT NULL,                  -- Статус заявки (enum), обязательное поле
                          submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Дата подачи заявки, по умолчанию текущая
                          complaints TEXT,                                      -- Жалобы, может быть пустым

    -- Внешний ключ на таблицу clients
                          CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES clients(id),
    -- Внешний ключ на таблицу cars
                          CONSTRAINT fk_car FOREIGN KEY (car_id) REFERENCES cars(id),
    -- Внешний ключ на таблицу employees
                          CONSTRAINT fk_operator FOREIGN KEY (operator_id) REFERENCES employees(id),
    -- Ограничение на допустимые значения статуса заявки
                          CONSTRAINT chk_status CHECK (request_status IN ('OPEN', 'IN_PROGRESS', 'COMPLETED', 'REJECTED'))
);

