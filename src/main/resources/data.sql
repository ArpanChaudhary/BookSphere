-- Create roles
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'Administrator role with full access'),
('USER', 'Regular user role with basic access'),
('AUTHOR', 'Author role with book management access');

-- Create admin user
INSERT INTO users (username, password, email, first_name, last_name, active, enabled, user_role, created_at, updated_at)
VALUES ('admin', '$2a$10$N/qM0.XNqRMbGv9sGT89OOklrhIwrWH1ZhCxLHjuYnk2P.y8B1Dj2', 'admin@booksphere.com', 'Admin', 'User', true, true, 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Create author user
INSERT INTO users (username, password, email, first_name, last_name, active, enabled, user_role, created_at, updated_at)
VALUES ('author', '$2a$10$N/qM0.XNqRMbGv9sGT89OOklrhIwrWH1ZhCxLHjuYnk2P.y8B1Dj2', 'author@booksphere.com', 'Author', 'User', true, true, 'AUTHOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Create regular user
INSERT INTO users (username, password, email, first_name, last_name, active, enabled, user_role, created_at, updated_at)
VALUES ('user', '$2a$10$N/qM0.XNqRMbGv9sGT89OOklrhIwrWH1ZhCxLHjuYnk2P.y8B1Dj2', 'user@booksphere.com', 'Regular', 'User', true, true, 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'author' AND r.name = 'AUTHOR';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'user' AND r.name = 'USER';

-- Create genres
INSERT INTO genres (name, description, active, created_at, updated_at) VALUES 
('Fiction', 'Literary works created from the imagination', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Non-Fiction', 'Literature based on facts and real events', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Science Fiction', 'Fiction based on imagined future scientific discoveries', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mystery', 'Fiction dealing with the solution of a crime or puzzle', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Romance', 'Fiction focused on romantic love stories', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fantasy', 'Fiction featuring magical and supernatural elements', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Biography', 'Non-fiction accounts of peoples lives', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('History', 'Non-fiction about past events', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Self-Help', 'Books for personal development', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Technology', 'Books about technological subjects', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Create sample books
INSERT INTO books (title, author_id, isbn, description, published_date, price, rental_price, available_copies, total_copies, cover_image, genre_id, created_at, updated_at, active)
SELECT 
    'The Great Adventure',
    u.id,
    '9781234567890',
    'An epic tale of adventure and discovery',
    '2023-01-15',
    19.99,
    2.99,
    10,
    10,
    '/images/book1.svg',
    g.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    true
FROM users u, genres g
WHERE u.username = 'author' AND g.name = 'Fiction';

INSERT INTO books (title, author_id, isbn, description, published_date, price, rental_price, available_copies, total_copies, cover_image, genre_id, created_at, updated_at, active)
SELECT 
    'Beyond the Stars',
    u.id,
    '9780987654321',
    'A journey through the cosmos and human potential',
    '2022-07-22',
    24.99,
    3.99,
    5,
    5,
    '/images/book2.svg',
    g.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    true
FROM users u, genres g
WHERE u.username = 'author' AND g.name = 'Science Fiction';

INSERT INTO books (title, author_id, isbn, description, published_date, price, rental_price, available_copies, total_copies, cover_image, genre_id, created_at, updated_at, active)
SELECT 
    'The Secret Garden',
    u.id,
    '9783456789012',
    'Discovering magic in everyday places',
    '2021-12-10',
    14.99,
    1.99,
    7,
    8,
    '/images/book3.svg',
    g.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    true
FROM users u, genres g
WHERE u.username = 'author' AND g.name = 'Fantasy';

INSERT INTO books (title, author_id, isbn, description, published_date, price, rental_price, available_copies, total_copies, cover_image, genre_id, created_at, updated_at, active)
SELECT 
    'Mind Mastery',
    u.id,
    '9786789012345',
    'Techniques for controlling your thoughts and achieving mental clarity',
    '2023-03-28',
    29.99,
    4.99,
    3,
    3,
    '/images/book4.svg',
    g.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    true
FROM users u, genres g
WHERE u.username = 'author' AND g.name = 'Self-Help';

-- Create sample transactions
INSERT INTO transactions (user_id, book_id, issue_date, due_date, return_date, rental_price, type, created_at, updated_at)
SELECT
    u.id,
    b.id,
    DATEADD('DAY', -10, CURRENT_TIMESTAMP),
    DATEADD('DAY', 20, CURRENT_TIMESTAMP),
    NULL,
    b.rental_price,
    'ISSUE',
    DATEADD('DAY', -10, CURRENT_TIMESTAMP),
    DATEADD('DAY', -10, CURRENT_TIMESTAMP)
FROM users u, books b
WHERE u.username = 'user' AND b.isbn = '9781234567890';

-- Create sample notifications
INSERT INTO notifications (user_id, message, type, is_read, created_at)
SELECT
    u.id,
    'Welcome to BookSphere! Start exploring our vast collection of books.',
    'SYSTEM',
    false,
    CURRENT_TIMESTAMP
FROM users u
WHERE u.username = 'user';