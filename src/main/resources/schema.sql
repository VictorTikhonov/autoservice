DROP TABLE IF EXISTS
    employees,
    positions,
    accounts,
    cars,
    requests,
    clients
    CASCADE;


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


















DROP INDEX IF EXISTS idx_auto_goods_category_id;
DROP INDEX IF EXISTS idx_services_category_id;

DROP TABLE IF EXISTS services CASCADE;
DROP TABLE IF EXISTS categories_services CASCADE;
DROP TABLE IF EXISTS auto_goods CASCADE;
DROP TABLE IF EXISTS categories_auto_goods CASCADE;






-- �������� ������� ��������� �����������
CREATE TABLE categories_auto_goods
(
    id   BIGSERIAL PRIMARY KEY, -- ID ���������, �������������
    name VARCHAR(35) NOT NULL   -- ������������ ���������
);


-- �������� ������� ���������
CREATE TABLE auto_goods
(
    id             BIGSERIAL PRIMARY KEY,   -- ID ����������, �������������
    category_id    BIGINT         NOT NULL, -- ID ���������
    name           VARCHAR(35)    NOT NULL, -- ������������ ����������
    quantity       INT            NOT NULL, -- ����������
    price_one_unit DECIMAL(10, 2) NOT NULL, -- ���� �� 1 �����
    manufacturer   VARCHAR(35)    NOT NULL, -- �������������
    description    TEXT,                    -- ��������
    relevance      BOOLEAN DEFAULT TRUE,    -- ������������, �� ��������� TRUE

    -- ������� ���� �� ������� categories_auto_goods
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories_auto_goods (id),

    -- �����������: ���� �� ����� ���� ������ ��� ����� 0
    CONSTRAINT chk_price_positive CHECK (price_one_unit > 0),

    -- �����������: ���������� �� ����� ���� ������ 0
    CONSTRAINT chk_quantity_non_negative CHECK (quantity >= 0)
);
CREATE INDEX idx_auto_goods_category_id ON auto_goods (category_id);



-- �������� ������� ��������� �����
CREATE TABLE categories_services (
                                     id BIGSERIAL PRIMARY KEY,               -- ID ��������� ������, �������������
                                     name VARCHAR(35) NOT NULL               -- ������������ ��������� ������
);



-- �������� ������� ������
CREATE TABLE services (
                          id BIGSERIAL PRIMARY KEY,        -- ID ������, �������������
                          name VARCHAR(35) NOT NULL,       -- ������������ ������
                          description TEXT,                -- �������� ������
                          category_id BIGINT NOT NULL,     -- ID ��������� ������

    -- ������� ���� �� ������� categories_of_services
                          CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories_services(id),

    -- ������������ ����: ��������� + ������
                          CONSTRAINT unique_service_name_per_category UNIQUE (name, category_id)
);
-- ���������� ������� �� category_id
CREATE INDEX idx_services_category_id ON services(category_id);