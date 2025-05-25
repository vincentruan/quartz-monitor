-- Quartz Configuration Table
CREATE TABLE IF NOT EXISTS quartz_config (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(50) NOT NULL UNIQUE,
    server_container VARCHAR(50),
    instance_name VARCHAR(100),
    host VARCHAR(100),
    port INT,
    jmx_username VARCHAR(50),
    jmx_password VARCHAR(100)
); 