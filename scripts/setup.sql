CREATE DATABASE IF NOT EXISTS sare_test_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON sare_test_db.* TO 'sare_user'@'localhost' IDENTIFIED BY 'sare_user_pwd';

CREATE DATABASE IF NOT EXISTS sare_dev_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON sare_dev_db.* TO 'sare_user'@'localhost';