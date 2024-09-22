# Spring boot outbox cleaner starter
version 0.0.1-SNAPSHOT

#### Before using make sure that you have tables.
Add to you project liquidbase/flyway migration like this:
```sql
CREATE TABLE outbox (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    key VARCHAR(255) NOT NULL UNIQUE,
    message TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    month INTEGER GENERATED ALWAYS AS (EXTRACT(MONTH FROM created_at)) STORED NOT NULL
) PARTITION BY LIST (month);

CREATE TABLE outbox_m1 PARTITION OF outbox FOR VALUES IN (1);
CREATE TABLE outbox_m2 PARTITION OF outbox FOR VALUES IN (2);
CREATE TABLE outbox_m3 PARTITION OF outbox FOR VALUES IN (3);
CREATE TABLE outbox_m4 PARTITION OF outbox FOR VALUES IN (4);
CREATE TABLE outbox_m5 PARTITION OF outbox FOR VALUES IN (5);
CREATE TABLE outbox_m6 PARTITION OF outbox FOR VALUES IN (6);
CREATE TABLE outbox_m7 PARTITION OF outbox FOR VALUES IN (7);
CREATE TABLE outbox_m8 PARTITION OF outbox FOR VALUES IN (8);
CREATE TABLE outbox_m9 PARTITION OF outbox FOR VALUES IN (9);
CREATE TABLE outbox_m10 PARTITION OF outbox FOR VALUES IN (10);
CREATE TABLE outbox_m11 PARTITION OF outbox FOR VALUES IN (11);
CREATE TABLE outbox_m12 PARTITION OF outbox FOR VALUES IN (12);
```