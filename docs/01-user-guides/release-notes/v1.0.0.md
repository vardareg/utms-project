# 🎓 UTMS v1.0.0 - Beta Testing Release

Undergraduate Transfer Management System - Ready for Testing

## 📦 What's Included

Complete, production-ready application with:

- **Backend**: Spring Boot + PostgreSQL (Docker)
- **Frontend**: React + Vite
- **50 Test Accounts**: Pre-seeded with realistic data
- **Full Workflow**: Student → OIDB → YGK → Dean

## 🚀 Quick Start

### System Requirements

- Docker & Docker Compose (20.10+)
- Node.js & npm (18+)
- 2GB RAM, 1GB disk space

### Installation (3 Steps)

1. **Clone the repository**

   ```bash
   git clone https://github.com/vardareg/utms-project.git
   cd utms-project
   ```

2. **Install frontend dependencies**

   ```bash
   cd utms-frontend
   npm install
   cd ..
   ```

3. **Start the application**

   ```bash
   bash scripts/start-utms.sh
   ```

   The application will automatically:
   - Start PostgreSQL and backend in Docker
   - Seed the database with test data
   - Launch the React frontend
   - Open your browser to <http://localhost:5173>

## 👤 Test Credentials

All users have password: **`Password123!`**

| Username | Role | Description |
|----------|------|-------------|
| `admin` | Administrator | Full system access |
| `oidb` | Student Affairs | Document validation |
| `ygk_cse` | Transfer Commission | Evaluate CSE applications |
| `dean_eng` | Dean of Engineering | Final approval |
| `student_1` - `student_50` | Students | Test application workflow |

## 📚 Documentation

- **[DISTRIBUTION_GUIDE.md](./DISTRIBUTION_GUIDE.md)** - Complete setup guide
- **[QUICK_START.md](./QUICK_START.md)** - Quick reference
- **[README.md](./README.md)** - Full documentation
- **[docs/user-manual.md](./docs/user-manual.md)** - API documentation

## ✨ Key Features

- ✅ Multi-role authentication (Student, OIDB, YGK, Dean, Admin)
- ✅ Student self-registration and profile management
- ✅ Application workflow with document upload
- ✅ Evaluation and approval system
- ✅ Real-time audit logging
- ✅ System health monitoring
- ✅ Announcement system
- ✅ Fully Dockerized deployment

## 🐛 Known Issues

- Backend may take 30-60 seconds to fully start on first run
- File uploads limited to PDF, JPEG, PNG (max 5MB)
- Browser cache may need hard refresh (`Ctrl+Shift+R`)

## 📞 Support

If you encounter issues:

1. Check the [DISTRIBUTION_GUIDE.md](./DISTRIBUTION_GUIDE.md) troubleshooting section
2. Review logs: `docker compose logs backend -f`
3. Try the complete reset: `docker compose down -v && bash scripts/start-utms.sh`

## 🎯 What to Test

1. **Admin Functions**: User management, system monitoring, announcements
2. **Student Workflow**: Registration → Profile → Application → Document upload
3. **OIDB Processing**: Document validation, forwarding to YGK
4. **YGK Evaluation**: Draft and finalize evaluations
5. **Dean Decision**: Approve/reject applications, request revisions
6. **Audit Logs**: Track all system actions

---

**Happy Testing! 🚀**

For questions or feedback, please open an issue on GitHub.
