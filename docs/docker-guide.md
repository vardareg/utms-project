# PostgreSQL Production Database - Quick Start Guide

## Overview

The UTMS application now supports production deployment with PostgreSQL database using Docker.

## Prerequisites

1. Install Docker:

   ```bash
   # Ubuntu/Debian
   sudo apt-get update
   sudo apt-get install docker.io docker-compose-plugin
   
   # Verify
   docker --version
   docker compose version
   ```

2. Ensure ports are available:
   - 8080 (Backend API)
   - 5432 (PostgreSQL - optional, for debugging)

## Quick Start

### 1. Configure Credentials (Optional)

Edit `.env` file in project root:

```bash
DB_USERNAME=utms_user
DB_PASSWORD=your_secure_password_here
```

### 2. Start Services

```bash
# Build and start in detached mode
docker compose up --build -d

# Or start with live logs
docker compose up --build
```

### 3. Verify Deployment

```bash
# Check service status
docker compose ps

# View backend logs
docker compose logs backend

# View database logs
docker compose logs db

# Follow logs in real-time
docker compose logs -f backend
```

### 4. Access Application

- **Backend API**: <http://localhost:8080>
- **Frontend**: Run separately in `utms-frontend/`:

  > **Note**: If you just cloned the repository, run `npm install` first to download frontend dependencies (since `node_modules/` is not tracked by Git).

  ```bash
  cd utms-frontend
  npm install  # Only needed after cloning or if dependencies change
  npm run dev
  ```

## Common Commands

### Service Management

```bash
# Stop services (data persists)
docker compose down

# Stop and remove volumes (deletes ALL data)
docker compose down -v

# Restart services
docker compose restart

# Rebuild backend only
docker compose up --build backend -d
```

### Logs and Debugging

```bash
# View all logs
docker compose logs

# View specific service
docker compose logs backend
docker compose logs db

# Follow logs (live)
docker compose logs -f backend

# Last 50 lines
docker compose logs --tail=50 backend
```

### Database Access

```bash
# Connect to PostgreSQL CLI
docker exec -it utms-postgres psql -U utms_user -d utmsdb

# Inside psql:
\dt                    # List tables
\d users               # Describe users table
SELECT * FROM users;   # Query users
\q                     # Quit
```

### Volume Management

```bash
# List volumes
docker volume ls

# Inspect postgres volume
docker volume inspect utms-project_postgres_data

# Remove volume (DELETES DATA)
docker volume rm utms-project_postgres_data
```

## Testing Data Persistence

1. Start containers:

   ```bash
   docker compose up -d
   ```

2. Create test data:

   ```bash
   # Option 1: Use init_data.py
   python3 tools/init_data.py
   
   # Option 2: Login via frontend and create data manually
   ```

3. Stop containers:

   ```bash
   docker compose down
   ```

4. Restart containers:

   ```bash
   docker compose up -d
   ```

5. Verify data exists:
   - Login with previously created credentials
   - Check applications, users, etc.

## Troubleshooting

### Backend won't start

```bash
# Check logs
docker compose logs backend

# Common issues:
# - Database not ready: Wait a few seconds, backend will retry
# - Port 8080 in use: Stop other services using port 8080
# - Build errors: Check Java 17 is used in Dockerfile
```

### Database connection failed

```bash
# Verify database is healthy
docker compose ps

# Should show:
# utms-postgres  ... Up (healthy)

# Check database logs
docker compose logs db

# Restart database
docker compose restart db
```

### Can't connect from frontend

- Ensure backend is running: `docker compose ps`
- Check backend is accessible: `curl http://localhost:8080/api/auth/login`
- Verify frontend API baseURL points to `http://localhost:8080`

### Data not persisting

```bash
# Check volume exists
docker volume ls | grep postgres_data

# If missing, you may have used 'docker compose down -v'
# which deletes volumes. Restart without -v flag:
docker compose down
docker compose up -d
```

### Out of disk space

```bash
# Remove unused Docker resources
docker system prune -a

# Remove specific volumes
docker volume rm utms-project_postgres_data
```

## Environment Variables Reference

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_USERNAME` | `utms_user` | PostgreSQL username |
| `DB_PASSWORD` | `utms_secure_password_123` | PostgreSQL password |
| `SPRING_PROFILES_ACTIVE` | `prod` | Spring profile (set by docker-compose) |

## Production Deployment Notes

For actual production (not local testing):

1. **Security**:
   - Change default credentials in `.env`
   - Use secrets management (Kubernetes Secrets, AWS Secrets Manager)
   - Don't commit `.env` to Git (already in `.gitignore`)
   - Consider removing port 5432 exposure in `docker-compose.yml`

2. **Monitoring**:
   - Enable Spring Boot Actuator endpoints
   - Set up log aggregation
   - Configure health checks for orchestration

3. **Scaling**:
   - Use external PostgreSQL (AWS RDS, etc.) instead of container
   - Deploy backend to multiple instances
   - Use load balancer

4. **Backup**:
   - Regular PostgreSQL backups
   - Volume snapshots
   - Export critical data

## Development vs Production

| Feature | Development | Production |
|---------|-------------|------------|
| **Database** | H2 In-Memory | PostgreSQL 15 |
| **Profile** | `dev` (default) | `prod` |
| **Persistence** | No (resets) | Yes (Docker volume) |
| **Deployment** | Maven | Docker Compose |
| **Data Seeding** | Automatic (`data.sql`) | Manual |
| **Logging** | DEBUG | INFO/WARN |

## Next Steps

1. ✅ Configuration files created
2. ⏳ Install Docker on your system
3. ⏳ Test deployment with `docker compose up`
4. ⏳ Verify data persistence
5. ⏳ Run full application workflow
6. ⏳ Consider production security hardening

---

For detailed technical documentation, see:

- [README.md](../README.md) - User guide
- [docs/user-manual.md](user-manual.md) - API documentation
