CREATE DATABASE IF NOT EXISTS sare_test_db CHARACTER SET utf8mb4 COLLATE utf8_unicode_ci;
GRANT ALL PRIVILEGES ON sare_test_db.* TO 'java_user'@'localhost' IDENTIFIED BY 'java_user_pwd';

CREATE DATABASE IF NOT EXISTS sare_dev_db CHARACTER SET utf8mb4 COLLATE utf8_unicode_ci;
GRANT ALL PRIVILEGES ON sare_dev_db.* TO 'java_user'@'localhost';
