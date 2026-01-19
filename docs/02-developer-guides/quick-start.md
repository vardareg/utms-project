# UTMS Quick Start Guide

## 🚀 One-Command Startup

```bash
bash scripts/start-utms.sh
```

This will automatically:

1. Start PostgreSQL and backend in Docker
2. Seed the database with initial data
3. Start the React frontend
4. Open your browser to <http://localhost:5173>

## 📋 Manual Control

### Start Components Individually

```bash
# 1. Start backend (Docker)
bash scripts/start-backend.sh

# 2. Seed database (one-time)
bash scripts/seed-database.sh

# 3. Start frontend (separate terminal)
bash scripts/start-frontend.sh
```

### Stop Services

```bash
# Stop everything
bash scripts/stop-utms.sh

# Stop and DELETE all data
docker compose down -v
```

## 👤 Default Login Credentials

All default users have password: `Password123!`

| Username | Role | Description |
|----------|------|-------------|
| `admin` | Administrator | Full system access |
| `oidb` | Student Affairs | Document validation, forwarding |
| `ygk_cse` | Transfer Commission | Evaluate Computer Engineering applications |
| `dean_eng` | Dean's Office | Final approval for Engineering Faculty |

## 📖 First Time Setup

If this is your first time running UTMS:

> **Note**: Anyone who clones the repo will need to run `npm install` in the frontend directory to get their own copy of dependencies.

1. Run the all-in-one script:

   ```bash
   bash scripts/start-utms.sh
   ```

2. Wait for "Frontend ready" message

3. Browser opens automatically to <http://localhost:5173>

4. Login with `admin` / `password123`

5. Start creating users and testing workflows!

## 🔧 Troubleshooting

### Port Already in Use

```bash
# Kill process on port 8080
fuser -k 8080/tcp

# Or change port in docker-compose.yml
```

### Frontend Dependencies Missing

```bash
cd utms-frontend
npm install
npm run dev
```

### Database Connection Failed

```bash
# Check Docker status
docker compose ps

# View backend logs
docker compose logs backend

# Restart containers
docker compose restart
```

### Reset Everything

```bash
# Stop and remove ALL data
docker compose down -v

# Start fresh
bash scripts/start-utms.sh
```

## 📊 Daily Usage

```bash
# Morning: Start UTMS
bash scripts/start-utms.sh

# Work on your project...

# Evening: Stop UTMS (data persists)
bash scripts/stop-utms.sh
```

## 🎯 Application URLs

- **Frontend:** <http://localhost:5173>
- **Backend API:** <http://localhost:8080>
- **Health Check:** <http://localhost:8080/actuator/health>
- **PostgreSQL:** localhost:5432 (accessible via psql or database tools)

## 📚 More Information

- Full documentation: [README.md](README.md)
- Docker guide: [docs/docker-guide.md](docs/docker-guide.md)
- API documentation: [docs/user-manual.md](docs/user-manual.md)
- All login credentials: [docs/test-credentials.txt](docs/test-credentials.txt)

---

**Need Help?** Check the logs:

```bash
docker compose logs backend -f
```
