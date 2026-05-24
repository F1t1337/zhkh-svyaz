-- Migration 004: extend services table for UC-09

-- Drop legacy 'title' column and replace with service_type + description + apartment_number
-- (run after 003 is applied)

alter table public.services
    add column if not exists service_type    text not null default '',
    add column if not exists description     text not null default '',
    add column if not exists apartment_number text not null default '';

-- Migrate old title → service_type for existing rows
update public.services set service_type = title where service_type = '' and title is not null;
