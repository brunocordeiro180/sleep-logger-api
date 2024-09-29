CREATE TABLE IF NOT EXISTS sleep_log (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    sleep_date DATE NOT NULL DEFAULT CURRENT_DATE,
    time_in_bed_start TIME NOT NULL,
    time_in_bed_end TIME NOT NULL,
    total_time_in_bed BIGINT NOT NULL,
    morning_feeling VARCHAR(4) CHECK (morning_feeling IN ('BAD', 'OK', 'GOOD')),
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);