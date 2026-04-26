CREATE TABLE bid (
    bid_id BIGINT PRIMARY KEY,
    rfq_id BIGINT,
    supplier_id BIGINT,
    price DOUBLE,
    created_at DATETIME
);

CREATE TABLE activity_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50),
    description TEXT,
    timestamp DATETIME
);

CREATE TABLE rfq (
    rfq_id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    bid_start_time DATETIME,
    bid_close_time DATETIME,
    forced_close_time DATETIME,
    trigger_window INT,
    extension_duration INT,
    extension_type VARCHAR(50),
    status VARCHAR(50)
);