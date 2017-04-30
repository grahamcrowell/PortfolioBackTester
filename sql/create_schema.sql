-- assume database findw already created
\connect findw

-- lowercase schema names
-- everything is ignored/not accepted by postgres / aws

CREATE SCHEMA IF NOT EXISTS dim;

CREATE SCHEMA IF NOT EXISTS fact;

CREATE SCHEMA IF NOT EXISTS staging;

-- create tables using Slick