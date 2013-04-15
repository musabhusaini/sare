CREATE DATABASE IF NOT EXISTS sare_test_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON sare_test_db.* TO 'sare_user'@'localhost' IDENTIFIED BY '[insert password here]';

CREATE DATABASE IF NOT EXISTS sare_main_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON sare_main_db.* TO 'sare_user'@'localhost';

CREATE DATABASE IF NOT EXISTS sare_webapp_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON sare_webapp_db.* TO 'sare_user'@'localhost';