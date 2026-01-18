# GitHub Release Guide - UTMS v1.0

Complete guide to creating a professional GitHub Release for your friend to test the UTMS application.

---

## 🎯 Overview

**Repository**: <https://github.com/vardareg/utms-project>  
**Release Version**: v1.0.0  
**Release Type**: Beta / Testing Release

---

## 📋 Pre-Release Checklist

### Step 1: Commit New Distribution Files

```bash
cd /home/egehan/Documents/Projects/IYTE/utms-project

# Add the new distribution-related files
git add DISTRIBUTION_GUIDE.md
git add scripts/package-for-distribution.sh

# Commit with a clear message
git commit -m "docs: add distribution guide and packaging script for v1.0 release"
```

### Step 2: Ensure Everything is Clean

```bash
# Check status - should show no uncommitted changes
git status

# Push to GitHub
git push origin main
```

### Step 3: Create a Git Tag

```bash
# Create an annotated tag for version 1.0.0
git tag -a v1.0.0 -m "Release v1.0.0 - UTMS Beta Testing Release

Features:
- Student self-registration and application workflow
- Multi-role dashboard (Admin, OIDB, YGK, Dean, Student)
- Document upload and validation
- Application evaluation and approval workflow
- Audit logging and system monitoring
- Announcement system
- 50 pre-seeded test accounts

See DISTRIBUTION_GUIDE.md for setup instructions."

# Push the tag to GitHub
git push origin v1.0.0
```

---

## 🚀 Creating the GitHub Release

### Option A: Via GitHub Web Interface (Recommended)

1. **Go to your repository**:

   ```
   https://github.com/vardareg/utms-project
   ```

2. **Navigate to Releases**:
   - Click on "Releases" in the right sidebar
   - Click "Draft a new release"

3. **Configure the Release**:
   - **Tag**: Select `v1.0.0` (or type it if not created yet)
   - **Target**: `main` branch
   - **Release title**: `UTMS v1.0.0 - Beta Testing Release`

4. **Write Release Notes** (copy the template below):

```markdown
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

1. **Install frontend dependencies**

   ```bash
   cd utms-frontend
   npm install
   cd ..
   ```

2. **Start the application**

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

```

5. **Set Release Type**:
   - ☑️ Check "Set as a pre-release" (since this is for testing)
   - ☐ Leave "Set as the latest release" unchecked (optional)

6. **Publish**:
   - Click "Publish release"

### Option B: Via GitHub CLI (gh)

If you have GitHub CLI installed:

```bash
# Create the release with notes
gh release create v1.0.0 \
  --title "UTMS v1.0.0 - Beta Testing Release" \
  --notes-file release-notes.md \
  --prerelease \
  --target main
```

---

## 📤 Sharing with Your Friend

Once the release is published, share this link:

```
https://github.com/vardareg/utms-project/releases/tag/v1.0.0
```

### What They Do

1. Click "Code" → "Download ZIP" or clone:

   ```bash
   git clone https://github.com/vardareg/utms-project.git
   cd utms-project
   git checkout v1.0.0
   ```

2. Follow `DISTRIBUTION_GUIDE.md`

3. Run:

   ```bash
   cd utms-frontend && npm install && cd ..
   bash scripts/start-utms.sh
   ```

---

## 🎁 Optional: Add Release Assets

You can also attach the `.tar.gz` package as a release asset:

1. In the GitHub Release page, click "Edit"
2. Drag and drop `utms-project-v1.0.tar.gz` into the "Attach binaries" section
3. Click "Update release"

This gives users the option to download either:

- The full Git repository (clone/download)
- Or the pre-packaged `.tar.gz` file

---

## 🔄 Future Updates

To release updates:

```bash
# Make your changes
git add .
git commit -m "fix: your bug fix"
git push

# Create a new tag
git tag -a v1.0.1 -m "Release v1.0.1 - Bug Fixes"
git push origin v1.0.1

# Create a new GitHub Release for v1.0.1
```

---

## ✅ Benefits of GitHub Release

- ✅ **Version Control**: Track different versions
- ✅ **Professional**: Looks official and trustworthy
- ✅ **Easy Sharing**: Just send a link
- ✅ **Changelog**: Document what's new
- ✅ **Download Options**: Clone or download ZIP
- ✅ **No File Size Limits**: Unlike email
- ✅ **Persistent**: Always available

---

## 📊 After Release

Monitor feedback:

- Enable GitHub Issues for bug reports
- Check GitHub Insights for clone statistics
- Update documentation based on feedback

---

**Ready to create your release?** Follow the steps above!
