
-- as root
CREATE USER 'm2m'@'localhost' IDENTIFIED BY 'm2m';
GRANT ALL PRIVILEGES ON * . * TO 'm2m'@'localhost';
FLUSH PRIVILEGES;

