-- Roles (These are inserted from the application code as well, so we'll skip them here)
-- INSERT INTO roles (name, description) VALUES 
--   ('USER', 'Regular user role with basic permissions'),
--   ('ADMIN', 'Administrative role with full system access'),
--   ('AUTHOR', 'Content creator role for book authors');

-- Admin User (password: admin123)
INSERT INTO users (username, password, email, first_name, last_name, active, enabled, created_at, updated_at)
VALUES ('admin', '$2a$10$WL7eRzWLe1.kqG5VtXTuS.UR75Hgcq6oqEMCKGRj2IIEwCymWa8YO', 'admin@booksphere.com', 'Admin', 'User', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Regular User (password: user123)
INSERT INTO users (username, password, email, first_name, last_name, active, enabled, created_at, updated_at)
VALUES ('user', '$2a$10$J1DKzY6U/wPPCdTdpGXDcepxv9Jyj/7B.JNsB/QJlTpiqPAyZRn8K', 'user@booksphere.com', 'Regular', 'User', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Author User (password: author123)
INSERT INTO users (username, password, email, first_name, last_name, active, enabled, created_at, updated_at)
VALUES ('author', '$2a$10$Vj1D1JvQ.l4nYWzLJx1vp.2EKrw2tJUqSJhKKEVf2RsmK5F/.Kz4.', 'author@booksphere.com', 'Famous', 'Author', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (username) DO NOTHING;

-- Assign roles to users (Join table)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'user' AND r.name = 'USER'
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'author' AND r.name = 'AUTHOR'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Genres
INSERT INTO genres (name, description)
VALUES 
  ('Fiction', 'Fictional literature'),
  ('Non-Fiction', 'Factual writing'),
  ('Science Fiction', 'Speculative fiction with scientific concepts'),
  ('Fantasy', 'Fiction with magical or supernatural elements'),
  ('Mystery', 'Fiction dealing with solving a crime or puzzle'),
  ('Romance', 'Stories about romantic love'),
  ('Biography', 'Account of a person''s life written by someone else'),
  ('History', 'Study of past events'),
  ('Self-Help', 'Books for personal improvement'),
  ('Children', 'Books for young readers')
ON CONFLICT (name) DO NOTHING;

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