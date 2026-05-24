-- Migration 005: give services.title a default value so new inserts (without title) don't fail
-- The title column is legacy — replaced by service_type in migration 004.

alter table public.services
    alter column title set default '';
