-- Добавить поле ответа администратора в обращениях
alter table public.requests add column if not exists admin_response text;

-- Таблица настроек приложения (ключ-значение)
create table if not exists public.settings (
    key   text primary key,
    value text not null default ''
);

grant all privileges on public.settings to anon;

-- Ссылка на мессенджер (по умолчанию пусто)
insert into public.settings (key, value) values ('messenger_url', '') on conflict (key) do nothing;
