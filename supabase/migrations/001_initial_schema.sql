-- ЖКХ Связь — начальная схема БД
-- Запускать в SQL-редакторе Supabase (localhost:8000 → Studio → SQL Editor)

-- =========================
-- 1. Удаляем таблицы если есть
-- =========================
drop table if exists public.work_log_entries cascade;
drop table if exists public.services cascade;
drop table if exists public.notifications cascade;
drop table if exists public.receipts cascade;
drop table if exists public.requests cascade;
drop table if exists public.admins cascade;
drop table if exists public.residents cascade;

-- =========================
-- 2. Создаём таблицы
-- =========================

create table public.residents (
    id               text primary key,
    phone            text unique not null,
    passport         text        not null,
    password         text,
    apartment_number text        not null,
    entrance         text        not null
);

create table public.admins (
    id          text primary key,
    adm_login   text unique not null,
    password    text        not null,
    permissions text[]      not null default '{}'
);

create table public.requests (
    id          text primary key,
    resident_id text not null references public.residents (id),
    category    text not null,
    description text not null,
    attachments text[] not null default '{}',
    status      text not null default 'NEW',
    created_at  text not null,
    deadline    text not null
);

create table public.receipts (
    id           text    primary key,
    resident_id  text    not null references public.residents (id),
    period       text    not null,
    cold_water   float8  not null default 0,
    hot_water    float8  not null default 0,
    electricity  float8  not null default 0,
    gas          float8  not null default 0,
    garbage      float8  not null default 0,
    maintenance  float8  not null default 0,
    total_amount float8  not null default 0,
    is_read      boolean not null default false,
    sent_at      text    not null
);

create table public.notifications (
    id                 text    primary key,
    title              text    not null,
    body               text    not null,
    type               text    not null,
    target_apartments  text[]  not null default '{}',
    sent_at            text    not null,
    is_read            boolean not null default false
);

create table public.services (
    id           text primary key,
    title        text not null,
    scheduled_at text not null,
    resident_id  text not null,
    status       text not null default 'SCHEDULED'
);

create table public.work_log_entries (
    id             text primary key,
    work_type      text not null,
    location       text not null,
    description    text not null,
    performed_at   text not null,
    report_pdf_url text not null default '',
    admin_id       text not null
);

-- =========================
-- 3. Доступ для anon-ключа
-- =========================
grant usage on schema public to anon;
grant all privileges on all tables in schema public to anon;
grant all privileges on all sequences in schema public to anon;

-- =========================
-- 4. Тестовые данные
-- =========================

insert into public.residents (id, phone, passport, password, apartment_number, entrance)
values ('1', '89603568729', '2411222333', null, '13', '1'),
       ('2', '89271672730', '5269526952', '52676942', '67', '3');

insert into public.admins (id, adm_login, password, permissions)
values ('1', 'admin', 'admin321', '{MANAGE_USERS,EDIT_SETTINGS,VIEW_ANALYTICS}');

insert into public.requests (id, resident_id, category, description, status, created_at, deadline)
values ('1', '1', 'ELECTRICITY', 'Мигает лампочка в подъезде на 3 этаже, уже третий день', 'NEW', '2026-05-20T10:00:00', '2026-05-27T10:00:00'),
       ('2', '1', 'PLUMBING', 'Течёт кран в ванной, капает постоянно', 'IN_PROGRESS', '2026-05-15T14:30:00', '2026-05-22T14:30:00'),
       ('3', '2', 'REPAIR', 'Трещина на стене в коридоре', 'DONE', '2026-05-01T09:00:00', '2026-05-10T09:00:00');

insert into public.receipts (id, resident_id, period, cold_water, hot_water, electricity, gas, garbage, maintenance, total_amount, is_read, sent_at)
values ('1', '1', 'Май 2026', 380.0, 540.0, 920.0, 450.0, 120.0, 680.0, 3090.0, false, '2026-05-01T09:00:00'),
       ('2', '1', 'Апрель 2026', 350.0, 520.0, 890.0, 430.0, 120.0, 650.0, 2960.0, true, '2026-04-01T09:00:00'),
       ('3', '2', 'Май 2026', 310.0, 480.0, 750.0, 390.0, 120.0, 650.0, 2700.0, false, '2026-05-01T09:00:00');

insert into public.notifications (id, title, body, type, target_apartments, sent_at, is_read)
values ('1', 'Плановое отключение света',
        '25 мая с 10:00 до 14:00 будет плановое отключение электроэнергии в связи с ремонтными работами',
        'GENERAL', '{}', '2026-05-23T09:00:00', false),
       ('2', 'Ваше обращение принято',
        'Обращение №1 «Мигает лампочка» принято в работу. Ожидаемый срок устранения: 27 мая',
        'REQUEST_UPDATE', '{}', '2026-05-20T11:00:00', false),
       ('3', 'Квитанция за май',
        'Выставлена квитанция за май 2026. Сумма к оплате: 3090 ₽',
        'RECEIPT', '{}', '2026-05-01T09:00:00', true);

insert into public.services (id, title, scheduled_at, resident_id, status)
values ('1', 'Замена трубы', '2026-05-15T10:00:00', '1', 'SCHEDULED');

insert into public.work_log_entries (id, work_type, location, description, performed_at, report_pdf_url, admin_id)
values ('1', 'Сантехника', 'Квартира 13, подъезд 1', 'Замена трубы холодного водоснабжения',
        '2026-05-15T12:00:00', '', '1');
