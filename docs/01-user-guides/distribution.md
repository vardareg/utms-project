# UTMS - Distribution Guide

**Undergraduate Transfer Management System**  
Version 1.1.0 | January 2026

---

## 📦 What's Included

This package contains a complete, ready-to-run UTMS application:

- **Backend**: Java Spring Boot application (runs in Docker)
- **Frontend**: React + Vite application  
- **Database**: PostgreSQL with pre-configured test data
- **Scripts**: Automated startup and management scripts

---

## 🖥️ System Requirements

Before starting, ensure you have:

- **Docker** (version 20.10+) - [Install Guide](https://docs.docker.com/get-docker/)
- **Docker Compose** (version 2.0+) - Usually included with Docker Desktop
- **Node.js & npm** (version 18+) - [Install Guide](https://nodejs.org/)
- **Operating System**: Linux, macOS, or Windows with WSL2

### Quick Check

```bash
# Verify installations
docker --version
docker compose version
node --version
npm --version
```

---

## 🚀 Quick Start (3 Steps)

### Step 1: Extract the Package

```bash
# Extract the archive
tar -xzf utms-project-v1.1.0.tar.gz
cd utms-project
```

### Step 2: Install Frontend Dependencies

```bash
# Install React dependencies (one-time setup)
cd utms-frontend
npm install
cd ..
```

**Note**: This step is required because `node_modules` is excluded from the package to keep it small.

### Step 3: Start the Application

```bash
# Run the all-in-one startup script
bash scripts/start-utms.sh
```

This script will:

1. ✅ Start PostgreSQL and backend in Docker
2. ✅ Seed the database with test data  
3. ✅ Start the React frontend
4. ✅ Open your browser to <http://localhost:5173>

**Wait for**: "Frontend ready" message, then the browser opens automatically.

---

## 👤 Test Credentials

All users have the password: **`Password123!`**

| Username | Role | What You Can Test |
|----------|------|-------------------|
| `admin` | Administrator | User management, system monitoring, announcements |
| `oidb` | Student Affairs | Document validation, forwarding applications |
| `ygk_cse` | Transfer Commission | Evaluate Computer Engineering applications |
| `dean_eng` | Dean of Engineering | Final approval/rejection of applications |

### Student Accounts (for testing applications)

You can also test as a student:

- Username: `student01_cse_new` to `student50_arch_rejected` (password: `Student123!`)
- **Format:** `student[01-50]_[dept]_[status]`
- These have pre-populated applications in various stages

---

## 🎯 Testing Workflows

### Try These Key Features

1. **Admin Dashboard** (login as `admin`)
   - Create new users
   - View system health
   - Manage announcements
   - Check audit logs

2. **Student Application Flow** (login as `student_1`)
   - View your application status
   - Upload documents
   - Check announcements

3. **OIDB Processing** (login as `oidb`)
   - Validate documents
   - Forward applications to YGK
   - Return applications for corrections

4. **YGK Evaluation** (login as `ygk_cse`)
   - Evaluate applications
   - Create draft evaluations
   - Finalize and send to Dean

5. **Dean Decision** (login as `dean_eng`)
   - Review evaluations
   - Approve or reject applications
   - Request revisions

---

## 📱 Application URLs

- **Frontend**: <http://localhost:5173>
- **Backend API**: <http://localhost:8080>
- **Health Check**: <http://localhost:8080/actuator/health>
- **Database**: localhost:5432 (credentials in `.env` file)

---

## 🛑 Stopping the Application

### Stop Frontend

Press `Ctrl+C` in the terminal running the frontend

### Stop Backend & Database

```bash
# Stop containers (data persists)
bash scripts/stop-utms.sh

# OR stop and DELETE all data
docker compose down -v
```

---

## 🔧 Troubleshooting

### Port Already in Use

If port 8080 or 5173 is already in use:

```bash
# Find and kill process on port 8080
lsof -ti:8080 | xargs kill -9

# Or change ports in docker-compose.yml and vite.config.js
```

### Frontend Won't Start

```bash
# Make sure you ran npm install
cd utms-frontend
npm install
npm run dev
```

### Backend Connection Failed

```bash
# Check Docker containers
docker compose ps

# View backend logs
docker compose logs backend -f

# Restart everything
docker compose restart
```

### Database Seeding Failed

```bash
# Check if database is ready
docker compose logs db

# Manually re-seed
bash scripts/seed-database.sh
```

### Complete Reset

```bash
# Nuclear option - delete everything and start fresh
docker compose down -v
rm -rf utms-frontend/node_modules
npm install --prefix utms-frontend
bash scripts/start-utms.sh
```

---

## 📂 Project Structure

```
utms-project/
├── utms-backend/          # Spring Boot application
├── utms-frontend/         # React application
├── scripts/               # Convenience scripts
│   ├── start-utms.sh     # All-in-one starter
│   ├── stop-utms.sh      # Stop all services
│   ├── seed-database.sh  # Populate test data
│   └── ...
├── docs/                  # Documentation
├── docker-compose.yml     # Docker configuration
├── .env                   # Environment variables
├── QUICK_START.md        # Quick reference guide
└── README.md             # Full documentation
```

---

## 📚 Additional Resources

- **Quick Start Guide**: `QUICK_START.md` - Condensed reference
- **Full README**: `README.md` - Complete documentation
- **User Manual**: `docs/user-manual.md` - API endpoints & features
- **Docker Guide**: `docs/docker-guide.md` - Docker details
- **Test Credentials**: `docs/test-credentials.txt` - All login info

---

## 💡 Tips for Testing

1. **Start with Admin**: Login as `admin` to see the full system
2. **Check Multiple Roles**: Each role has different capabilities
3. **Test Full Workflow**: Create a student → Application → OIDB → YGK → Dean
4. **Use Audit Logs**: See who did what and when (Admin/Dean dashboards)
5. **Check Announcements**: Visible to all users on login

---

## 🐛 Known Issues

- **Browser Cache**: If you see stale data, hard refresh with `Ctrl+Shift+R`
- **First Load**: Backend may take 30-60 seconds to fully start
- **File Uploads**: Only PDF, JPEG, PNG files are accepted (max 5MB)

---

## 📞 Support

If you encounter issues:

1. Check the troubleshooting section above
2. Review logs: `docker compose logs backend -f`
3. Ensure all system requirements are met
4. Try the "Complete Reset" option

---

## ✅ Success Checklist

After setup, you should be able to:

- [ ] Access frontend at <http://localhost:5173>
- [ ] Login as `admin` with password `Password123!`
- [ ] See the Admin Dashboard with system metrics
- [ ] View 50 test students in user management
- [ ] See applications at various stages
- [ ] Switch between different user roles

---

**Happy Testing! 🎉**

For the complete documentation and developer guide, see `README.md`.
