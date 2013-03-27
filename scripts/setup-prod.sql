CREATE DATABASE IF NOT EXISTS sare_test_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON sare_test_db.* TO 'java_user'@'localhost' IDENTIFIED BY 'java_user_pwd';

CREATE DATABASE IF NOT EXISTS sare_main_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON sare_main_db.* TO 'java_user'@'localhost';

CREATE DATABASE IF NOT EXISTS sare_webapp_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON sare_webapp_db.* TO 'java_user'@'localhost';