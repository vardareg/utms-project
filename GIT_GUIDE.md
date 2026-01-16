# 🎓 Practical Guide to Git & GitHub

This guide explains the core concepts of Git and the commands we used to update the UTMS project. It is designed for learning purposes.

---

## 1. Core Concepts

### 📚 What is Git?

Git is a **Version Control System**. Think of it as a time machine for your code. It tracks every change you make, allowing you to:

- Revert to previous versions if something breaks.
- See who changed what and why.
- Work on new features without breaking the working code.

### ☁️ What is GitHub?

GitHub is a **Remote Hosting Service** for Git repositories.

- **Git** is the tool on your computer (Local).
- **GitHub** is the website where you store your code online (Remote).

---

## 2. The Golden Workflow

Every change follows this 3-step journey:

1. **Working Directory** 📝 (Where you edit files)
    - *You save a file in VS Code.*
2. **Staging Area** 📦 (Preparing the shipment)
    - *You choose which files you want to include in the next snapshot.*
3. **Local Repository** 📸 (The snapshot)
    - *You permanently record the snapshot locally.*
4. **Remote Repository** 🚀 (GitHub)
    - *You upload your snapshots to the cloud.*

---

## 3. Commands We Used (Explained)

Here is a breakdown of the specific commands we used to update your project:

### 🔍 Check Status

```bash
git status
```

**What it does:** Shows you which files have changed.

- **Red:** Changed but not staged (modified).
- **Green:** Staged and ready to commit.

### ➕ Stage Changes

```bash
git add -A
```

**What it does:** Moves changes from "Working Directory" to "Staging Area".

- `-A` (All): Adds all new, modified, and deleted files.
- `git add filename`: Adds only a specific file.

### 📸 Create Commit

```bash
git commit -m "feat: add user management"
```

**What it does:** Takes a snapshot of the files in the Staging Area and saves it to your Local Repository.

- `-m`: Allows you to write the message inline.
- **Best Practice:** Use clear messages like `feat:` (new feature), `fix:` (bug fix), or `docs:` (documentation).

### 🚀 Push to GitHub

```bash
git push origin main
```

**What it does:** Uploads your local commits to GitHub.

- `origin`: The nickname for your remote repository URL.
- `main`: The branch you are working on (usually the default branch).

---

## 4. Other Essential Commands

### 📥 Get Updates (Pull)

```bash
git pull origin main
```

**What it does:** Downloads changes from GitHub and merges them into your local code. Run this if you changed code on another computer.

### 📜 View History

```bash
git log --oneline
```

**What it does:** Shows a concise list of past commits.

- You'll see the **Commit Hash** (e.g., `a1b2c3d`) and the message.

### 🐑 Clone (Download)

```bash
git clone https://github.com/username/repo-name.git
```

**What it does:** Downloads an entire repository from GitHub to your computer for the first time.

---

## 5. Summary Cheat Sheet

| I want to... | Command |
| :--- | :--- |
| **Check** what changed | `git status` |
| **Stage** all files | `git add -A` |
| **Save** a snapshot | `git commit -m "message"` |
| **Upload** to GitHub | `git push origin main` |
| **Download** updates | `git pull origin main` |
| **See** history | `git log` |

---
*Created for UTMS Project Learning - 2026*
