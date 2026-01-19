# Undergraduate Transfer Management System (UTMS)

**Welcome to the UTMS Project!**

This repository contains the source code and documentation for the Undergraduate Transfer Management System, designed to automate and streamline the student transfer process at IZTECH.

## 🚀 Quick Start

To get up and running immediately:

```bash
# Start the entire system (Backend + Frontend + Database)
bash scripts/start-utms.sh
```

For more detailed setup instructions, see the [Developer Quick Start Guide](docs/02-developer-guides/quick-start.md).

## 📚 Documentation

The documentation is organized as follows:

### 👤 For Users & Admins
*   **[User Manual](docs/01-user-guides/user-manual.md)**: Detailed guide on how to use the system (Student, OIDB, YGK, Dean, Admin).
*   **[Distribution Guide](docs/01-user-guides/distribution.md)**: Instructions for installing the pre-packaged release.
*   **[Release Notes](docs/01-user-guides/release-notes/)**: History of changes and updates.

### 💻 For Developers
*   **[Quick Start](docs/02-developer-guides/quick-start.md)**: detailed setup for local development.
*   **[Docker Setup](docs/02-developer-guides/docker-setup.md)**: Guide to running the production environment with Docker.
*   **[Agent & Code Guide](docs/02-developer-guides/agents.md)**: Rules, commands, and conventions for AI agents and contributors.
*   **[Git Guide](docs/02-developer-guides/git-guide.md)**: Comprehensive guide to version control in this project.
*   **[Test Credentials](docs/02-developer-guides/test-credentials.txt)**: List of default accounts for testing.

### 📋 Project Specifications
*   **[SRS (Requirements)](docs/03-project-specs/srs.md)**: Software Requirements Specification.
*   **[SDD (Design)](docs/03-project-specs/sdd.md)**: Software Design Description.
*   **[SPMP (Management)](docs/03-project-specs/spmp.md)**: Software Project Management Plan.

## 📦 Repository Structure

```
/
├── utms-backend/       # Spring Boot Backend
├── utms-frontend/      # React Frontend
├── scripts/            # Automation scripts
├── tools/              # Helper tools (data generation)
├── docs/               # Documentation (see above)
└── README.md           # This file
```

---
© 2026 IZTECH Team 3
