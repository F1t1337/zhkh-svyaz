-- Migration 003: resident name, apartment_number in requests, telegram/vk URLs

-- 1. Add name field to residents (default empty, residents can fill in later)
alter table public.residents add column if not exists name text not null default '';

-- 2. Add apartment_number to requests so admins can see which flat sent the request
alter table public.requests add column if not exists apartment_number text not null default '';

-- 3. Add separate Telegram and VK messenger URL settings
insert into public.settings (key, value) values ('telegram_url', '') on conflict (key) do nothing;
insert into public.settings (key, value) values ('vk_url', '')      on conflict (key) do nothing;
