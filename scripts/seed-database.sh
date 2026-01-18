#!/bin/bash
# UTMS Database Seeder Script
# Seeds PostgreSQL with initial data (faculties, departments, users)

echo "🌱 Seeding UTMS Database..."
echo "======================================"

# Check if Docker containers are running
if ! docker compose ps | grep -q "utms-postgres.*Up"; then
    echo "❌ Error: PostgreSQL container is not running!"
    echo "Please run './start-backend.sh' first."
    exit 1
fi

echo "Creating faculties, departments, and default users..."

# Execute SQL commands
docker exec -i utms-postgres psql -U utms_user -d utmsdb << 'EOF'
-- Check if data already exists
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM users WHERE username = 'admin') THEN
        RAISE NOTICE '⚠️  Data already exists. Skipping seed.';
    ELSE
        -- Create Faculties
        INSERT INTO faculties (id, name) VALUES 
          (1, 'Faculty of Engineering'),
          (2, 'Faculty of Architecture');

        -- Create Departments
        INSERT INTO departments (id, name, faculty_id, quota) VALUES
          (1, 'Computer Engineering', 1, 5),
          (2, 'Mechanical Engineering', 1, 3),
          (3, 'Architecture', 2, 5);

        -- Create Admin User (password: Password123!)
        INSERT INTO users (username, email, password_hash, role, user_type, first_name, last_name, enabled) 
        VALUES ('admin', 'admin@iztech.edu.tr', 
          '$2a$10$GYahuALPg96iRTIujctHsunmIAgAb83uV/x5BHqoDNJSqryt9.Kb.',
          'ROLE_ADMIN', 'STAFF', 'Admin', 'User', true);

        -- Create OIDB User (password: Password123!)
        INSERT INTO users (username, email, password_hash, role, user_type, first_name, last_name, enabled) 
        VALUES ('oidb', 'oidb@iztech.edu.tr', 
          '$2a$10$GYahuALPg96iRTIujctHsunmIAgAb83uV/x5BHqoDNJSqryt9.Kb.',
          'ROLE_OIDB', 'STAFF', 'OIDB', 'Officer', true);

        -- Create YGK Users (password: Password123!)
        INSERT INTO users (username, email, password_hash, role, user_type, first_name, last_name, enabled) 
        VALUES ('ygk_cse', 'ygk.cse@iztech.edu.tr',
          '$2a$10$GYahuALPg96iRTIujctHsunmIAgAb83uV/x5BHqoDNJSqryt9.Kb.',
          'ROLE_YGK', 'FACULTY', 'YGK', 'Computer', true);

        INSERT INTO administrative_profiles (user_id, department_id) 
        VALUES ((SELECT id FROM users WHERE username = 'ygk_cse'), 1);

        INSERT INTO users (username, email, password_hash, role, user_type, first_name, last_name, enabled) 
        VALUES ('ygk_mech', 'ygk.mech@iztech.edu.tr',
          '$2a$10$GYahuALPg96iRTIujctHsunmIAgAb83uV/x5BHqoDNJSqryt9.Kb.',
          'ROLE_YGK', 'FACULTY', 'YGK', 'Mechanical', true);

        INSERT INTO administrative_profiles (user_id, department_id) 
        VALUES ((SELECT id FROM users WHERE username = 'ygk_mech'), 2);

        INSERT INTO users (username, email, password_hash, role, user_type, first_name, last_name, enabled) 
        VALUES ('ygk_arch', 'ygk.arch@iztech.edu.tr',
          '$2a$10$GYahuALPg96iRTIujctHsunmIAgAb83uV/x5BHqoDNJSqryt9.Kb.',
          'ROLE_YGK', 'FACULTY', 'YGK', 'Architecture', true);

        INSERT INTO administrative_profiles (user_id, department_id) 
        VALUES ((SELECT id FROM users WHERE username = 'ygk_arch'), 3);

        -- Create Dean Users (password: Password123!)
        INSERT INTO users (username, email, password_hash, role, user_type, first_name, last_name, enabled) 
        VALUES ('dean_eng', 'dean.eng@iztech.edu.tr',
          '$2a$10$GYahuALPg96iRTIujctHsunmIAgAb83uV/x5BHqoDNJSqryt9.Kb.',
          'ROLE_DEAN_OFFICE_STAFF', 'FACULTY', 'Dean', 'Engineering', true);

        INSERT INTO administrative_profiles (user_id, faculty_id) 
        VALUES ((SELECT id FROM users WHERE username = 'dean_eng'), 1);

        INSERT INTO users (username, email, password_hash, role, user_type, first_name, last_name, enabled) 
        VALUES ('dean_arch', 'dean.arch@iztech.edu.tr',
          '$2a$10$GYahuALPg96iRTIujctHsunmIAgAb83uV/x5BHqoDNJSqryt9.Kb.',
          'ROLE_DEAN_OFFICE_STAFF', 'FACULTY', 'Dean', 'Architecture', true);

        INSERT INTO administrative_profiles (user_id, faculty_id) 
        VALUES ((SELECT id FROM users WHERE username = 'dean_arch'), 2);

        RAISE NOTICE '✅ Database seeded successfully!';
    END IF;
END $$;

-- Show created users
SELECT username, email, role FROM users ORDER BY id;
EOF

echo ""
echo "✅ Database seed complete!"
echo ""
echo "Default user accounts (password: Password123!):"
echo "  👤 admin     - System Administrator"
echo "  👤 oidb      - Student Affairs Officer"
echo "  👤 ygk_cse   - Transfer Commission (Computer Engineering)"
echo "  👤 ygk_mech  - Transfer Commission (Mechanical Engineering)"
echo "  👤 ygk_arch  - Transfer Commission (Architecture)"
echo "  👤 dean_eng  - Dean of Engineering"
echo "  👤 dean_arch - Dean of Architecture"
