
CREATE DATABASE IF NOT EXISTS temperature_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE temperature_db;

CREATE TABLE IF NOT EXISTS conversions (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           celsius DOUBLE NOT NULL,
                                           fahrenheit DOUBLE NOT NULL,
                                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS units (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     unit_key VARCHAR(30) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS unit_translations (
                                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                                 unit_id INT NOT NULL,
                                                 lang VARCHAR(5) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    UNIQUE (unit_id, lang),
    FOREIGN KEY (unit_id) REFERENCES units(id)
    );

INSERT INTO units (unit_key)
VALUES
    ('celsius'),
    ('fahrenheit'),
    ('kelvin')
    ON DUPLICATE KEY UPDATE
                         unit_key = VALUES(unit_key);

INSERT INTO unit_translations
(unit_id, lang, name, description)
SELECT
    u.id,
    t.lang,
    t.name,
    t.description
FROM units u
         JOIN (
    SELECT 'celsius' AS unit_key, 'en' AS lang,
           'Celsius' AS name,
           'Temperature in Celsius' AS description
    UNION ALL SELECT 'celsius', 'fi',
                     'Celsius', 'Lämpötila Celsius-asteina'
    UNION ALL SELECT 'celsius', 'sv',
                     'Celsius', 'Temperatur i grader Celsius'
    UNION ALL SELECT 'celsius', 'ja',
                     '摂氏', '摂氏で表した温度'
    UNION ALL SELECT 'celsius', 'ar',
                     'مئوية', 'درجة الحرارة بالمئوية'

    UNION ALL SELECT 'fahrenheit', 'en',
                     'Fahrenheit', 'Temperature in Fahrenheit'
    UNION ALL SELECT 'fahrenheit', 'fi',
                     'Fahrenheit', 'Lämpötila Fahrenheit-asteina'
    UNION ALL SELECT 'fahrenheit', 'sv',
                     'Fahrenheit', 'Temperatur i grader Fahrenheit'
    UNION ALL SELECT 'fahrenheit', 'ja',
                     '華氏', '華氏で表した温度'
    UNION ALL SELECT 'fahrenheit', 'ar',
                     'فهرنهايت', 'درجة الحرارة بالفهرنهايت'

    UNION ALL SELECT 'kelvin', 'en',
                     'Kelvin', 'Temperature in Kelvin'
    UNION ALL SELECT 'kelvin', 'fi',
                     'Kelvin', 'Lämpötila kelvineinä'
    UNION ALL SELECT 'kelvin', 'sv',
                     'Kelvin', 'Temperatur i kelvin'
    UNION ALL SELECT 'kelvin', 'ja',
                     'ケルビン', 'ケルビンで表した温度'
    UNION ALL SELECT 'kelvin', 'ar',
                     'كلفن', 'درجة الحرارة بالكلفن'
) t ON u.unit_key = t.unit_key
    ON DUPLICATE KEY UPDATE
                         name = VALUES(name),
                         description = VALUES(description);

SELECT
    u.unit_key,
    ut.lang,
    ut.name,
    ut.description
FROM unit_translations ut
         JOIN units u ON ut.unit_id = u.id
ORDER BY u.id, ut.lang;

