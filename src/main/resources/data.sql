-- Users (password for all seeded users is: Test@123)
INSERT INTO users (id, name, email, password, role, created_at, updated_at) VALUES
    (1, 'Super Admin', 'admin@gym.com', '$2b$10$98e/qFJGw7OyMw.z8Z9OFee2/RV2PirKssfidz89k34y7yXME5swa', 'SUPER_ADMIN', now(), now()),
    (2, 'Gym Admin', 'gymadmin@gym.com', '$2b$10$98e/qFJGw7OyMw.z8Z9OFee2/RV2PirKssfidz89k34y7yXME5swa', 'GYM_ADMIN', now(), now()),
    (3, 'Branch Manager', 'manager@gym.com', '$2b$10$98e/qFJGw7OyMw.z8Z9OFee2/RV2PirKssfidz89k34y7yXME5swa', 'BRANCH_MANAGER', now(), now()),
    (4, 'Trainer One', 'trainer1@gym.com', '$2b$10$98e/qFJGw7OyMw.z8Z9OFee2/RV2PirKssfidz89k34y7yXME5swa', 'TRAINER', now(), now()),
    (5, 'Receptionist One', 'reception@gym.com', '$2b$10$98e/qFJGw7OyMw.z8Z9OFee2/RV2PirKssfidz89k34y7yXME5swa', 'RECEPTIONIST', now(), now()),
    (6, 'John Doe', 'member1@gym.com', '$2b$10$98e/qFJGw7OyMw.z8Z9OFee2/RV2PirKssfidz89k34y7yXME5swa', 'MEMBER', now(), now()),
    (7, 'Jane Smith', 'member2@gym.com', '$2b$10$98e/qFJGw7OyMw.z8Z9OFee2/RV2PirKssfidz89k34y7yXME5swa', 'MEMBER', now(), now()),
    (8, 'Trainer Two', 'trainer2@gym.com', '$2b$10$98e/qFJGw7OyMw.z8Z9OFee2/RV2PirKssfidz89k34y7yXME5swa', 'TRAINER', now(), now())
ON CONFLICT (id) DO NOTHING;

-- Gyms
INSERT INTO gyms (id, name, description, address, created_at) VALUES
    (1, 'FitZone Gym', 'A premium fitness chain', '100 Main Street', now()),
    (2, 'PowerHouse Gym', 'Strength and conditioning specialists', '200 Market Avenue', now())
ON CONFLICT (id) DO NOTHING;

-- Branches
INSERT INTO branches (id, gym_id, name, address, opening_time, closing_time, facilities, manager_id, created_at) VALUES
    (1, 1, 'Downtown Branch', '101 Downtown Rd', '06:00:00', '22:00:00', 'Pool, Sauna, Free Weights', 3, now()),
    (2, 1, 'Uptown Branch', '202 Uptown Rd', '05:30:00', '23:00:00', 'Cardio Zone, Group Studio', 3, now()),
    (3, 2, 'Westside Branch', '303 Westside Ave', '06:00:00', '21:00:00', 'CrossFit Box, Sauna', NULL, now())
ON CONFLICT (id) DO NOTHING;

-- Members
INSERT INTO members (id, user_id, branch_id, first_name, last_name, email, phone, date_of_birth, status, join_date, created_at) VALUES
    (1, 6, 1, 'John', 'Doe', 'member1@gym.com', '555-0101', '1995-04-12', 'ACTIVE', '2024-01-10', now()),
    (2, 7, 1, 'Jane', 'Smith', 'member2@gym.com', '555-0102', '1998-08-23', 'ACTIVE', '2024-02-15', now()),
    (3, NULL, 2, 'Mike', 'Brown', 'mike.brown@example.com', '555-0103', '1990-01-05', 'INACTIVE', '2023-11-01', now())
ON CONFLICT (id) DO NOTHING;

-- Trainers
INSERT INTO trainers (id, user_id, branch_id, specialization, bio, status, created_at) VALUES
    (1, 4, 1, 'Strength Training', 'Certified strength and conditioning coach', 'ACTIVE', now()),
    (2, 8, 2, 'Cardio & Yoga', 'Yoga instructor with 8 years experience', 'ACTIVE', now())
ON CONFLICT (id) DO NOTHING;

-- Memberships
INSERT INTO memberships (id, member_id, plan_name, price, duration_days, start_date, end_date, status, created_at) VALUES
    (1, 1, 'Gold Plan', 49.99, 30, '2024-06-01', '2024-07-01', 'ACTIVE', now()),
    (2, 2, 'Silver Plan', 29.99, 30, NULL, NULL, 'PENDING', now()),
    (3, 3, 'Platinum Plan', 79.99, 90, '2024-01-01', '2024-04-01', 'EXPIRED', now())
ON CONFLICT (id) DO NOTHING;

-- Workouts
INSERT INTO workouts (id, name, description, trainer_id, member_id, scheduled_date, exercises, status, created_at) VALUES
    (1, 'Strength Basics', 'Foundational strength program', 1, 1, '2024-06-05', 'Squats, Deadlifts, Bench Press', 'ASSIGNED', now()),
    (2, 'Cardio Blast', 'High intensity cardio program', 2, 2, '2024-06-10', 'Running, Jump Rope, Burpees', 'IN_PROGRESS', now())
ON CONFLICT (id) DO NOTHING;

-- Fitness Classes
INSERT INTO fitness_classes (id, name, type, branch_id, trainer_id, schedule_time, capacity, registered_count, status, created_at) VALUES
    (1, 'Morning Yoga', 'YOGA', 1, 1, '2024-07-01 07:00:00', 20, 1, 'SCHEDULED', now()),
    (2, 'HIIT Session', 'HIIT', 2, 2, '2024-07-02 18:00:00', 15, 0, 'SCHEDULED', now())
ON CONFLICT (id) DO NOTHING;

-- Class Registrations
INSERT INTO class_registrations (id, class_id, member_id, status, registered_at) VALUES
    (1, 1, 1, 'REGISTERED', now())
ON CONFLICT (id) DO NOTHING;

-- Appointments
INSERT INTO appointments (id, member_id, trainer_id, scheduled_at, duration_minutes, status, created_at) VALUES
    (1, 1, 1, '2024-07-05 10:00:00', 60, 'SCHEDULED', now())
ON CONFLICT (id) DO NOTHING;

-- Attendances
INSERT INTO attendances (id, member_id, check_in_time, check_out_time, created_at) VALUES
    (1, 1, '2024-06-20 08:00:00', '2024-06-20 09:15:00', now())
ON CONFLICT (id) DO NOTHING;

-- Payments
INSERT INTO payments (id, member_id, membership_id, amount, status, method, transaction_date, created_at) VALUES
    (1, 1, 1, 49.99, 'SUCCESS', 'CARD', '2024-06-01 09:00:00', now())
ON CONFLICT (id) DO NOTHING;

-- Progress Trackers
INSERT INTO progress_trackers (id, member_id, title, description, progress_percentage, status, recorded_at, created_at) VALUES
    (1, 1, 'Weight Loss Goal', 'Target: lose 5kg over 3 months', 40, 'IN_PROGRESS', '2024-06-15', now())
ON CONFLICT (id) DO NOTHING;

-- Notifications
INSERT INTO notifications (id, user_id, type, message, is_read, progress_tracker_id, created_at) VALUES
    (1, 6, 'MEMBERSHIP_ACTIVATED', 'Your membership plan Gold Plan has been activated.', false, NULL, now()),
    (2, 6, 'PROGRESS_UPDATE', 'Your weight loss goal is now 40% complete.', false, 1, now())
ON CONFLICT (id) DO NOTHING;

-- Re-align identity sequences with the highest seeded id so future inserts do not collide
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval(pg_get_serial_sequence('gyms', 'id'), (SELECT COALESCE(MAX(id), 1) FROM gyms));
SELECT setval(pg_get_serial_sequence('branches', 'id'), (SELECT COALESCE(MAX(id), 1) FROM branches));
SELECT setval(pg_get_serial_sequence('members', 'id'), (SELECT COALESCE(MAX(id), 1) FROM members));
SELECT setval(pg_get_serial_sequence('trainers', 'id'), (SELECT COALESCE(MAX(id), 1) FROM trainers));
SELECT setval(pg_get_serial_sequence('memberships', 'id'), (SELECT COALESCE(MAX(id), 1) FROM memberships));
SELECT setval(pg_get_serial_sequence('workouts', 'id'), (SELECT COALESCE(MAX(id), 1) FROM workouts));
SELECT setval(pg_get_serial_sequence('fitness_classes', 'id'), (SELECT COALESCE(MAX(id), 1) FROM fitness_classes));
SELECT setval(pg_get_serial_sequence('class_registrations', 'id'), (SELECT COALESCE(MAX(id), 1) FROM class_registrations));
SELECT setval(pg_get_serial_sequence('appointments', 'id'), (SELECT COALESCE(MAX(id), 1) FROM appointments));
SELECT setval(pg_get_serial_sequence('attendances', 'id'), (SELECT COALESCE(MAX(id), 1) FROM attendances));
SELECT setval(pg_get_serial_sequence('payments', 'id'), (SELECT COALESCE(MAX(id), 1) FROM payments));
SELECT setval(pg_get_serial_sequence('progress_trackers', 'id'), (SELECT COALESCE(MAX(id), 1) FROM progress_trackers));
SELECT setval(pg_get_serial_sequence('notifications', 'id'), (SELECT COALESCE(MAX(id), 1) FROM notifications));
