-- Create roles
INSERT INTO roles (name, description) VALUES ('ADMIN', 'Administrator role with full access');
INSERT INTO roles (name, description) VALUES ('USER', 'Regular user role with basic access');
INSERT INTO roles (name, description) VALUES ('AUTHOR', 'Author role with book management access');

-- Create admin user
INSERT INTO users (username, password, email, first_name, last_name, active, enabled, created_at, updated_at, user_role)
VALUES ('admin', '$2a$10$N/qM0.XNqRMbGv9sGT89OOklrhIwrWH1ZhCxLHjuYnk2P.y8B1Dj2', 'admin@booksphere.com', 'Admin', 'User', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ADMIN');

-- Assign roles to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- Create genres
INSERT INTO genres (name, description, active, created_at)
VALUES ('Fiction', 'Literary works created from the imagination', true, CURRENT_TIMESTAMP),
       ('Non-Fiction', 'Literature based on facts and real events', true, CURRENT_TIMESTAMP),
       ('Science Fiction', 'Fiction based on imagined future scientific discoveries', true, CURRENT_TIMESTAMP),
       ('Mystery', 'Fiction dealing with the solution of a crime or puzzle', true, CURRENT_TIMESTAMP),
       ('Romance', 'Fiction focused on romantic love stories', true, CURRENT_TIMESTAMP),
       ('Fantasy', 'Fiction featuring magical and supernatural elements', true, CURRENT_TIMESTAMP),
       ('Biography', 'Non-fiction accounts of peoples lives', true, CURRENT_TIMESTAMP),
       ('History', 'Non-fiction about past events', true, CURRENT_TIMESTAMP),
       ('Self-Help', 'Books for personal development', true, CURRENT_TIMESTAMP),
       ('Technology', 'Books about technological subjects', true, CURRENT_TIMESTAMP);

-- Books (Associate with author user)
INSERT INTO books (title, author_id, isbn, description, published_date, price, available_copies, total_copies, cover_image, genre_id)
SELECT 
  'The Great Adventure',
  (SELECT id FROM users WHERE username = 'author'),
  '9781234567890',
  'An epic tale of adventure and discovery',
  '2023-01-15',
  19.99,
  10,
  10,
  '/images/book1.svg',
  (SELECT id FROM genres WHERE name = 'Fiction')
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9781234567890');

INSERT INTO books (title, author_id, isbn, description, published_date, price, available_copies, total_copies, cover_image, genre_id)
SELECT 
  'Beyond the Stars',
  (SELECT id FROM users WHERE username = 'author'),
  '9780987654321',
  'A journey through the cosmos and human potential',
  '2022-07-22',
  24.99,
  5,
  5,
  '/images/book2.svg',
  (SELECT id FROM genres WHERE name = 'Science Fiction')
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780987654321');

INSERT INTO books (title, author_id, isbn, description, published_date, price, available_copies, total_copies, cover_image, genre_id)
SELECT 
  'The Secret Garden',
  (SELECT id FROM users WHERE username = 'author'),
  '9783456789012',
  'Discovering magic in everyday places',
  '2021-12-10',
  14.99,
  7,
  8,
  '/images/book3.svg',
  (SELECT id FROM genres WHERE name = 'Fantasy')
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9783456789012');

INSERT INTO books (title, author_id, isbn, description, published_date, price, available_copies, total_copies, cover_image, genre_id)
SELECT 
  'Mind Mastery',
  (SELECT id FROM users WHERE username = 'author'),
  '9786789012345',
  'Techniques for controlling your thoughts and achieving mental clarity',
  '2023-03-28',
  29.99,
  3,
  3,
  '/images/book4.svg',
  (SELECT id FROM genres WHERE name = 'Self-Help')
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9786789012345');

-- Sample transactions for user
INSERT INTO transactions (user_id, book_id, issue_date, due_date, type, created_at, updated_at)
SELECT
  (SELECT id FROM users WHERE username = 'user'),
  (SELECT id FROM books WHERE isbn = '9781234567890'),
  CURRENT_TIMESTAMP - INTERVAL '10 days',
  CURRENT_TIMESTAMP + INTERVAL '20 days',
  'ISSUE',
  CURRENT_TIMESTAMP - INTERVAL '10 days',
  CURRENT_TIMESTAMP - INTERVAL '10 days'
WHERE NOT EXISTS (
  SELECT 1 FROM transactions 
  WHERE user_id = (SELECT id FROM users WHERE username = 'user') 
  AND book_id = (SELECT id FROM books WHERE isbn = '9781234567890')
  AND type = 'ISSUE'
);

-- Sample notification
INSERT INTO notifications (user_id, message, type, created_at)
SELECT
  (SELECT id FROM users WHERE username = 'user'),
  'Welcome to BookSphere! Explore our collection of books.',
  'SYSTEM',
  CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM notifications 
  WHERE user_id = (SELECT id FROM users WHERE username = 'user') 
  AND type = 'SYSTEM'
  AND message LIKE 'Welcome%'
);