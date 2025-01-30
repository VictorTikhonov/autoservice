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


-- ������� ������ � �������
INSERT INTO positions (position_name)
VALUES ('��������');

INSERT INTO accounts (login, password, role)
VALUES ('1', '1', 'OPERATOR');

INSERT INTO employees (account_id, position_id, employment_status, surname, name,
                       patronymic, phone_number, salary, hire_date, birth_date, dtype)
VALUES ((SELECT id FROM accounts WHERE login = '1'), -- ���������� ID ������� ������
        (SELECT id FROM positions WHERE position_name = '��������'), -- ���������� ID ���������
        '�������', -- ������ ���������������
        '�������', -- �������
        '������', -- ���
        '������������', -- ��������
        '88888888888', -- ����� ��������
        50000, -- ��������
        '2022-01-01', -- ���� ���������������
        '1990-05-10', -- ���� ��������
        'OPERATOR' -- ���� ����������
       );

-- ������� ������� � ������� positions
INSERT INTO positions (position_name)
VALUES ('������� ��������'),         -- 1
       ('�������� ���������'),       -- 2
       ('������-��������'),          -- 3
       ('������� �������'),          -- 4
       ('������� ������ ���������'), -- 5
       ('�����-�������');
-- 6


-- �������� ������� �������
CREATE TABLE clients
(
    id                BIGSERIAL PRIMARY KEY,       -- ID �������, �������������
    surname           VARCHAR(50) NOT NULL,        -- �������, ������������ ����
    name              VARCHAR(50) NOT NULL,        -- ���, ������������ ����
    patronymic        VARCHAR(50),                 -- ��������, ����� ���� ������
    phone_number      VARCHAR(11) NOT NULL UNIQUE, -- ����� ��������, ����� 11 ��������
    email             VARCHAR(50),                 -- �����, ����� ���� ������
    registration_date DATE DEFAULT CURRENT_DATE,   -- ���� �����������, �� ��������� �������

    -- ����������� �� ������ ����� � ������ �������� � ������ ����� 11 ��������
    CONSTRAINT phone_number_digits_clients CHECK (phone_number ~ '^[0-9]{11}$')
);


-- �������� ������� ����������
CREATE TABLE cars
(
    id                  BIGSERIAL PRIMARY KEY, -- ID ����������
    state_number        VARCHAR(20),           -- ��������, ����� �� 20 ��������, ����� ������ ������������� ������
    vin                 VARCHAR(17) NOT NULL,  -- VIN �����, ����� 17 ��������
    brand               VARCHAR(50) NOT NULL,  -- ����� ����������, ������������ ����
    model               VARCHAR(50) NOT NULL,  -- ������ ����������, ������������ ����
    year_of_manufacture INTEGER     NOT NULL,  -- ��� �������, ������ ����� �����

    -- ��� �������, �� 1900 �� �������� ����
    CONSTRAINT year_of_manufacture_check
        CHECK (year_of_manufacture >= 1900 AND year_of_manufacture <= EXTRACT(YEAR FROM CURRENT_DATE)),
    -- ����������� �� ������ ��������� (���� �������� �� ������)
    CONSTRAINT state_number_check
        CHECK (state_number IS NULL OR state_number ~ '^[A-Za-z�-��-�0-9]{1,20}$'),
    -- ����������� �� ������ � ������ ����� VIN ������ (17 ��������)
    CONSTRAINT vin_format_check CHECK (vin ~ '^[A-HJ-NPR-Z0-9]{17}$')
);


-- �������� ������� ������� ������
CREATE TABLE accounts
(
    id       BIGSERIAL PRIMARY KEY,        -- ID ������� ������
    login    VARCHAR(30)  NOT NULL UNIQUE, -- �����, ���������� ����
    password VARCHAR(100) NOT NULL,        -- ������, ������������ ����
    role     VARCHAR(20)  NOT NULL         -- ������ �� ����, ������� ����
);


-- �������� ������� positions (���������)
CREATE TABLE positions
(
    id            BIGSERIAL PRIMARY KEY,      -- ID ���������
    position_name VARCHAR(25) NOT NULL UNIQUE -- ������������ ���������, ������������ � ���������� ����
);


-- �������� ������� ����������
CREATE TABLE employees
(
    id                BIGSERIAL PRIMARY KEY,          -- ID ����������
    account_id        BIGINT         NOT NULL UNIQUE, -- ID ������� ������, ������������ ����
    position_id       BIGINT         NOT NULL,        -- ID ���������, ������������ ����
    employment_status VARCHAR(20)    NOT NULL,        -- ������ ��������������� (enum), ������������ ����
    surname           VARCHAR(50)    NOT NULL,        -- ������� ����������, ������������ ����
    name              VARCHAR(50)    NOT NULL,        -- ��� ����������, ������������ ����
    patronymic        VARCHAR(50),                    -- �������� ����������, ����� ���� ������
    phone_number      VARCHAR(11)    NOT NULL UNIQUE, -- ����� ��������, ������������ ����, ����� 11
    salary            DECIMAL(10, 2) NOT NULL,        -- ���������� �����, ������������ ����
    hire_date         DATE           NOT NULL,        -- ���� ���������������, ������������ ����
    dismissal_date    DATE,                           -- ���� ����������, ����� ���� ������
    birth_date        DATE           NOT NULL,        -- ���� ��������, ������������ ����
    dtype             VARCHAR(20)    NOT NULL,        -- ��� ���������� (����)

    -- ������� ���� �� ������� accounts
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    -- ������� ���� �� ������� positions
    CONSTRAINT fk_position FOREIGN KEY (position_id) REFERENCES positions (id),
    -- ����������� �� �������� employment_status
    CONSTRAINT chk_employment_status CHECK (employment_status IN ('�������', '������', '�� �������')),
    -- ����������� �� ������ ������ ��������
    CONSTRAINT phone_number_digits_employees CHECK (phone_number ~ '^[0-9]{11}$'),
    -- ����������� �� ���������� �������� dtype (���� ����������)
    CONSTRAINT chk_dtype CHECK (dtype IN ('ADMIN', 'MECHANIC', 'OPERATOR'))
);


-- �������� ������� ������
CREATE TABLE requests
(
    id              BIGSERIAL PRIMARY KEY,               -- ID ������
    client_id       BIGINT      NOT NULL,                -- ID �������
    car_id          BIGINT      NOT NULL,                -- ID ����������
    operator_id     BIGINT      NOT NULL,                -- ID ���������
    request_status  VARCHAR(20) NOT NULL,                -- ������ ������ (enum), ������������ ����
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- ���� ������ ������, �� ��������� �������
    complaints      TEXT,                                -- ������, ����� ���� ������

    -- ������� ���� �� ������� clients
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES clients (id),
    -- ������� ���� �� ������� cars
    CONSTRAINT fk_car FOREIGN KEY (car_id) REFERENCES cars (id),
    -- ������� ���� �� ������� employees
    CONSTRAINT fk_operator FOREIGN KEY (operator_id) REFERENCES employees (id),
    -- ����������� �� ���������� �������� ������� ������
    CONSTRAINT chk_status CHECK (request_status IN ('� ��������', '�����������', '���������', '���������'))
);


-- �������� ������� ��������� �����������
CREATE TABLE categories_auto_goods
(
    id   BIGSERIAL PRIMARY KEY, -- ID ���������, �������������
    name VARCHAR(35) NOT NULL   -- ������������ ���������
);


-- �������� ������� ���������
CREATE TABLE auto_goods
(
    id              BIGSERIAL PRIMARY KEY,   -- ID ����������, �������������
    category_id     BIGINT         NOT NULL, -- ID ���������
    name            VARCHAR(35)    NOT NULL, -- ������������ ����������
    quantity        INT            NOT NULL, -- ����������
    price_one_unit  DECIMAL(10, 2) NOT NULL, -- ���� �� 1 �����
    expiration_date DATE,                    -- ���� ��������
    relevance       BOOLEAN DEFAULT TRUE,    -- ������������� (���������� ��������)

    -- ������� ���� �� ������� categories_auto_goods
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories_auto_goods (id),

    -- �����������: ���� �� ����� ���� ������ ��� ����� 0
    CONSTRAINT chk_price_positive CHECK (price_one_unit > 0),

    -- �����������: ���������� �� ����� ���� ������ 0
    CONSTRAINT chk_quantity_non_negative CHECK (quantity >= 0)
);
CREATE INDEX idx_auto_goods_category_id ON auto_goods (category_id);


-- �������� ������� ��������� �����
CREATE TABLE categories_services
(
    id   BIGSERIAL PRIMARY KEY, -- ID ��������� ������, �������������
    name VARCHAR(35) NOT NULL   -- ������������ ��������� ������
);


-- �������� ������� ������
CREATE TABLE services
(
    id          BIGSERIAL PRIMARY KEY, -- ID ������, �������������
    name        VARCHAR(35) NOT NULL,  -- ������������ ������
    description TEXT,                  -- �������� ������
    category_id BIGINT      NOT NULL,  -- ID ��������� ������
    relevance   BOOLEAN DEFAULT TRUE,  -- ������������� (���������� ��������)

    -- ������� ���� �� ������� categories_of_services
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories_services (id),

    -- ������������ ����: ��������� + ������
    CONSTRAINT unique_service_name_per_category UNIQUE (name, category_id)
);
-- ���������� ������� �� category_id
CREATE INDEX idx_services_category_id ON services (category_id);


-- �������� ��������
DROP INDEX IF EXISTS idx_work_order_services_service_id;
DROP INDEX IF EXISTS idx_work_order_services_work_order_id;
DROP INDEX IF EXISTS idx_work_order_auto_goods_auto_good_id;
DROP INDEX IF EXISTS idx_work_order_auto_goods_work_order_id;

-- �������� ������� ������
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

-- �������� ������
DROP TABLE IF EXISTS work_order_services CASCADE;
DROP TABLE IF EXISTS work_order_auto_goods CASCADE;
DROP TABLE IF EXISTS work_orders CASCADE;


-- �������� ������� work_orders (�����-������)
CREATE TABLE work_orders
(
    id                BIGSERIAL PRIMARY KEY,               -- ID �����-������, �������������
    request_id        BIGINT      NOT NULL,                -- ID ������, ������������ ����
    mechanic_id       BIGINT      NOT NULL,                -- ID ��������, ������������ ����
    work_order_status VARCHAR(20) NOT NULL,                -- ������ �����-������, ������������ ����
--                              price DECIMAL(10, 2) NOT NULL DEFAULT 0,          -- ���� �����, ������������ ���� � ��������� ��������� 0
    start_date        TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- ���� ������ �����, ����� ���� ������
    end_date          TIMESTAMP,                           -- ���� ��������� �����, ����� ���� ������

    -- ������� �����
    CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES requests (id),
    CONSTRAINT fk_mechanic FOREIGN KEY (mechanic_id) REFERENCES employees (id),

    -- ����������� �� ������ �����-������
    CONSTRAINT chk_status CHECK (work_order_status IN ('� ��������', '��������', '�������'))
);


-- �������� ������� work_order_services (�����_�����_����������)
CREATE TABLE work_order_auto_goods
(
    work_order_id  BIGINT         NOT NULL,                            -- ID �����-������, ������������ ����
    auto_good_id   BIGINT         NOT NULL,                            -- ID ����������, ������������ ����
    quantity       INT            NOT NULL CHECK (quantity > 0),       -- ���������� ����������, ������������ ����
    price_one_unit DECIMAL(10, 2) NOT NULL CHECK (price_one_unit > 0), -- ���� �� 1 �����, ������������ ����

    -- ������� ���� �� ������� work_orders
    CONSTRAINT fk_work_order_auto_goods FOREIGN KEY (work_order_id) REFERENCES work_orders (id),
    -- ������� ���� �� ������� auto_goods
    CONSTRAINT fk_auto_good FOREIGN KEY (auto_good_id) REFERENCES auto_goods (id),

    -- ��������� ��������� ����
    CONSTRAINT pk_work_order_auto_goods PRIMARY KEY (work_order_id, auto_good_id)
);
-- ������� ��� ������ � ������ work_order_id � auto_good_id
CREATE INDEX idx_work_order_auto_goods_work_order_id ON work_order_auto_goods (work_order_id);
CREATE INDEX idx_work_order_auto_goods_auto_good_id ON work_order_auto_goods (auto_good_id);


-- �������� ������� work_order_services (�����_�����_������)
CREATE TABLE work_order_services
(
    work_order_id BIGINT         NOT NULL,                    -- ID �����-������, ������������ ����
    service_id    BIGINT         NOT NULL,                    -- ID ������, ������������ ����
    price         DECIMAL(10, 2) NOT NULL CHECK (price >= 0), -- ���� ������, ������������ ����

    -- ������� ���� �� ������� work_orders
    CONSTRAINT fk_work_order_services FOREIGN KEY (work_order_id) REFERENCES work_orders (id),
    -- ������� ���� �� ������� services
    CONSTRAINT fk_service FOREIGN KEY (service_id) REFERENCES services (id),

    -- ��������� ��������� ����
    CONSTRAINT pk_work_order_services PRIMARY KEY (work_order_id, service_id)
);
-- ������� ��� ������ � ������ work_order_id � service_id
CREATE INDEX idx_work_order_services_work_order_id ON work_order_services (work_order_id);
CREATE INDEX idx_work_order_services_service_id ON work_order_services (service_id);
