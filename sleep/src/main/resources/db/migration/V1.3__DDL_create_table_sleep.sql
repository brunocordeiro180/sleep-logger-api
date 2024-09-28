CREATE TABLE IF NOT EXISTS sleep_log (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    date DATE NOT NULL DEFAULT CURRENT_DATE,
    time_in_bed_start TIMESTAMP NOT NULL,
    time_in_bed_end TIMESTAMP NOT NULL,
    morning_feeling VARCHAR(4) CHECK (morning_feeling IN ('BAD', 'OK', 'GOOD')),
    FOREIGN KEY (user_id) REFERENCES "user"(id)
--     created_at TIMESTAMP DEFAULT now(),
--     updated_at TIMESTAMP DEFAULT now() ON UPDATE now()
);