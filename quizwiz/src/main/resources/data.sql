USE quizwiz_db;

INSERT INTO students (name, email)
VALUES ('John Doe', 'john@example.com')
    ON DUPLICATE KEY UPDATE name=name;