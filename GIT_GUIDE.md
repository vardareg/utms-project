# 🎓 Practical Guide to Git & GitHub

This guide explains the core concepts of Git and the commands we used to update the UTMS project. It is designed for learning purposes and team collaboration.

---

## 📖 Table of Contents

1. [Core Concepts](#1-core-concepts)
2. [The Golden Workflow](#2-the-golden-workflow)
3. [Visual Workflow Diagram](#3-visual-workflow-diagram)
4. [Basic Commands](#4-basic-commands)
5. [Branching Strategy](#5-branching-strategy)
6. [Common Scenarios & Solutions](#6-common-scenarios--solutions)
7. [Understanding .gitignore](#7-understanding-gitignore)
8. [Collaboration Best Practices](#8-collaboration-best-practices)
9. [GitHub-Specific Features](#9-github-specific-features)
10. [Emergency Commands](#10-emergency-commands)
11. [Summary Cheat Sheet](#11-summary-cheat-sheet)

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

Every change follows this 4-step journey:

1. **Working Directory** 📝 (Where you edit files)
    - *You save a file in VS Code.*
2. **Staging Area** 📦 (Preparing the shipment)
    - *You choose which files you want to include in the next snapshot.*
3. **Local Repository** 📸 (The snapshot)
    - *You permanently record the snapshot locally.*
4. **Remote Repository** 🚀 (GitHub)
    - *You upload your snapshots to the cloud.*

---

## 3. Visual Workflow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    GIT WORKFLOW VISUALIZATION                    │
└─────────────────────────────────────────────────────────────────┘

  Working Directory          Staging Area         Local Repo        Remote Repo
  ─────────────────         ──────────────       ────────────      ─────────────
       📝                        📦                   📸                ☁️
  (Your Files)              (Ready to Save)       (Committed)        (GitHub)
       │                         │                    │                 │
       │   git add -A            │                    │                 │
       ├────────────────────────>│                    │                 │
       │                         │                    │                 │
       │                         │  git commit -m     │                 │
       │                         ├───────────────────>│                 │
       │                         │                    │                 │
       │                         │                    │  git push       │
       │                         │                    ├────────────────>│
       │                         │                    │                 │
       │                         │                    │  git pull       │
       │<────────────────────────┴────────────────────┴─────────────────┤
       │                                                                 │
                              (Updates from team)
```

---

## 4. Basic Commands

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
- `git add .`: Adds all files in current directory.

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
- Use `git log --graph --oneline --all` for a visual branch history.

### 🐑 Clone (Download)

```bash
git clone https://github.com/username/repo-name.git
```

**What it does:** Downloads an entire repository from GitHub to your computer for the first time.

---

## 5. Branching Strategy

Branches allow you to work on features without affecting the main codebase.

### 🌿 Why Use Branches?

- **Isolation:** Work on new features without breaking working code.
- **Collaboration:** Multiple team members can work simultaneously.
- **Safety:** The `main` branch stays stable.

### Creating a New Branch

```bash
# Create and switch to a new branch
git checkout -b feature/user-authentication

# Or using newer syntax
git switch -c feature/admin-panel
```

**Naming Convention:**
- `feature/description` - New features (e.g., `feature/login-page`)
- `fix/description` - Bug fixes (e.g., `fix/validation-error`)
- `docs/description` - Documentation updates

### Switching Between Branches

```bash
# Switch to existing branch
git checkout main
# Or
git switch main
```

### Viewing All Branches

```bash
# List local branches
git branch

# List all branches (including remote)
git branch -a
```

### Merging a Branch

```bash
# Switch to main branch first
git checkout main

# Merge your feature branch
git merge feature/user-authentication
```

### Deleting a Branch

```bash
# Delete local branch (after merging)
git branch -d feature/user-authentication

# Force delete (if not merged)
git branch -D feature/old-experiment
```

### Typical Workflow with Branches

```bash
# 1. Start from main
git checkout main
git pull origin main

# 2. Create feature branch
git checkout -b feature/new-dashboard

# 3. Make changes and commit
git add -A
git commit -m "feat: add new dashboard layout"

# 4. Push feature branch to GitHub
git push origin feature/new-dashboard

# 5. Create Pull Request on GitHub (see section 9)

# 6. After approval, merge and delete
git checkout main
git pull origin main
git branch -d feature/new-dashboard
```

---

## 6. Common Scenarios & Solutions

### 🔧 Scenario 1: "I made a mistake in my last commit message"

```bash
# Fix the last commit message
git commit --amend -m "fix: correct typo in validation logic"

# If already pushed, you'll need to force push (use carefully!)
git push origin main --force
```

⚠️ **Warning:** Only use `--force` if you're sure no one else has pulled your changes!

### 🔧 Scenario 2: "I want to undo changes before committing"

```bash
# Undo changes to a specific file
git restore UserController.java

# Undo all changes in working directory
git restore .
```

### 🔧 Scenario 3: "I accidentally committed to main instead of a feature branch"

```bash
# 1. Create the branch you should have been on
git branch feature/my-feature

# 2. Reset main to previous commit (keep changes)
git reset --soft HEAD~1

# 3. Switch to the feature branch
git checkout feature/my-feature

# 4. Commit again
git add -A
git commit -m "feat: add feature properly"
```

### 🔧 Scenario 4: "Merge Conflicts - What to do?"

When two people edit the same file, Git can't automatically merge:

```bash
# After git pull or git merge, you see:
# CONFLICT (content): Merge conflict in UserService.java
```

**How to resolve:**

1. Open the conflicted file in VS Code
2. Look for conflict markers:
```java
<<<<<<< HEAD
// Your changes
String name = "John";
=======
// Their changes
String name = "Jane";
>>>>>>> feature/other-branch
```
3. Edit the file to keep what you want
4. Remove the conflict markers (`<<<<<<<`, `=======`, `>>>>>>>`)
5. Stage and commit:
```bash
git add UserService.java
git commit -m "fix: resolve merge conflict in UserService"
```

### 🔧 Scenario 5: "I need to switch branches but have uncommitted changes"

```bash
# Save your changes temporarily
git stash

# Switch branches
git checkout other-branch

# Come back and restore changes
git checkout original-branch
git stash pop
```

### 🔧 Scenario 6: "I want to see what changed in a specific commit"

```bash
# Show changes in a commit
git show a1b2c3d

# Show changes in the last commit
git show HEAD
```

---

## 7. Understanding .gitignore

### 📛 What is .gitignore?

A `.gitignore` file tells Git which files or folders to **ignore** and never commit.

### Why Use It?

**Never commit:**
- ❌ Dependencies (`node_modules/`, `target/`)
- ❌ Build outputs (`dist/`, `build/`, `*.class`)
- ❌ Sensitive data (`.env`, `application-secrets.properties`)
- ❌ IDE files (`.idea/`, `.vscode/`, `*.iml`)
- ❌ OS files (`.DS_Store`, `Thumbs.db`)
- ❌ Log files (`*.log`)

### Example .gitignore for UTMS

```gitignore
# Java
*.class
*.jar
*.war
target/
*.log

# Node.js
node_modules/
npm-debug.log
dist/
build/

# Environment Variables
.env
.env.local
application-secrets.properties

# IDE
.idea/
.vscode/
*.iml
*.swp

# OS
.DS_Store
Thumbs.db

# Database
*.db
*.sqlite
```

### How to Use

1. Create a file named `.gitignore` in your project root
2. Add patterns (one per line)
3. Commit the `.gitignore` file itself:
```bash
git add .gitignore
git commit -m "chore: add gitignore"
```

---

## 8. Collaboration Best Practices

### 🤝 Team Guidelines for UTMS Project

#### 1. **Always Pull Before Starting Work**

```bash
# Start of every work session
git checkout main
git pull origin main
```

This prevents conflicts and ensures you have the latest code.

#### 2. **Write Meaningful Commit Messages**

**Good Examples:**
- ✅ `feat: add student application form validation`
- ✅ `fix: resolve null pointer in ApplicationService`
- ✅ `docs: update API documentation for evaluation endpoints`

**Bad Examples:**
- ❌ `update`
- ❌ `fix stuff`
- ❌ `asdfasdf`

**Commit Message Format:**
```
<type>: <short description>

[optional body explaining WHY]
```

**Types:**
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation
- `style:` - Formatting, missing semicolons
- `refactor:` - Code restructuring
- `test:` - Adding tests
- `chore:` - Maintenance tasks

#### 3. **Never Commit Sensitive Data**

❌ **Never commit:**
- Passwords
- API keys
- Database credentials
- JWT secrets

✅ **Instead:**
- Use environment variables
- Add sensitive files to `.gitignore`
- Use `application-secrets.properties` (gitignored)

#### 4. **Keep Commits Focused**

- One commit = One logical change
- Don't mix unrelated changes
- Makes code review easier

#### 5. **Pull Before Push**

```bash
# Before pushing
git pull origin main
# Resolve any conflicts
git push origin main
```

#### 6. **Communicate with Your Team**

- Announce when working on a major feature
- Use GitHub Issues to track tasks
- Review each other's Pull Requests

---

## 9. GitHub-Specific Features

### 🔀 Pull Requests (PRs)

A Pull Request is a way to propose changes and get them reviewed before merging.

**Workflow:**

1. **Create a feature branch and push it:**
```bash
git checkout -b feature/admin-dashboard
# Make changes
git add -A
git commit -m "feat: add admin dashboard"
git push origin feature/admin-dashboard
```

2. **On GitHub:**
   - Go to your repository
   - Click "Pull Requests" → "New Pull Request"
   - Select your branch (`feature/admin-dashboard`)
   - Add description of changes
   - Click "Create Pull Request"

3. **Code Review:**
   - Team members review your code
   - They can comment and request changes
   - You can push more commits to address feedback

4. **Merge:**
   - Once approved, click "Merge Pull Request"
   - Delete the branch on GitHub
   - Pull the updated main locally:
   ```bash
   git checkout main
   git pull origin main
   git branch -d feature/admin-dashboard
   ```

### 📋 GitHub Issues

Track bugs, features, and tasks.

**Creating an Issue:**
1. Go to "Issues" tab
2. Click "New Issue"
3. Add title and description
4. Assign to team member
5. Add labels (bug, enhancement, documentation)

**Linking Commits to Issues:**
```bash
git commit -m "fix: resolve login timeout issue (#42)"
```
This automatically links to Issue #42.

### 🔍 Code Review on GitHub

**Reviewing a PR:**
1. Go to "Pull Requests"
2. Click on the PR
3. Go to "Files changed"
4. Click line numbers to add comments
5. Click "Review changes" → "Approve" or "Request changes"

### ⚡ GitHub Actions (CI/CD Basics)

GitHub Actions can automatically:
- Run tests when you push code
- Build your application
- Deploy to servers

**Example:** Auto-run tests on every push to `main`.

---

## 10. Emergency Commands

### 🚨 Use These Carefully!

#### git stash - Temporarily Save Changes

```bash
# Save uncommitted changes
git stash

# List stashed changes
git stash list

# Restore most recent stash
git stash pop

# Restore specific stash
git stash apply stash@{1}

# Delete a stash
git stash drop stash@{0}
```

**Use case:** You need to switch branches but aren't ready to commit.

#### git reset - Undo Commits

```bash
# Undo last commit, keep changes staged
git reset --soft HEAD~1

# Undo last commit, keep changes unstaged
git reset HEAD~1

# Undo last commit, DELETE changes (DANGEROUS!)
git reset --hard HEAD~1
```

⚠️ **Warning:** `--hard` permanently deletes changes!

**Use case:** You committed too early or to the wrong branch.

#### git revert - Safely Undo a Commit

```bash
# Create a new commit that undoes a previous commit
git revert a1b2c3d
```

**Difference from reset:**
- `reset`: Rewrites history (dangerous if pushed)
- `revert`: Creates new commit (safe for shared branches)

**Use case:** Undo a commit that's already pushed to GitHub.

#### git clean - Remove Untracked Files

```bash
# See what would be deleted
git clean -n

# Delete untracked files
git clean -f

# Delete untracked files and directories
git clean -fd
```

⚠️ **Warning:** This permanently deletes files!

#### git reflog - Recover "Lost" Commits

```bash
# View all actions (even deleted commits)
git reflog

# Restore a commit
git checkout a1b2c3d
```

**Use case:** You accidentally deleted commits and need them back.

---

## 11. Summary Cheat Sheet

### Basic Operations

| I want to... | Command |
| :--- | :--- |
| **Check** what changed | `git status` |
| **Stage** all files | `git add -A` |
| **Stage** specific file | `git add filename` |
| **Save** a snapshot | `git commit -m "message"` |
| **Upload** to GitHub | `git push origin main` |
| **Download** updates | `git pull origin main` |
| **See** history | `git log --oneline` |
| **Clone** repository | `git clone <url>` |

### Branching

| I want to... | Command |
| :--- | :--- |
| **Create** new branch | `git checkout -b feature/name` |
| **Switch** to branch | `git checkout branch-name` |
| **List** all branches | `git branch -a` |
| **Merge** branch | `git merge feature/name` |
| **Delete** branch | `git branch -d feature/name` |

### Fixing Mistakes

| I want to... | Command |
| :--- | :--- |
| **Undo** file changes | `git restore filename` |
| **Fix** last commit message | `git commit --amend -m "new message"` |
| **Undo** last commit (keep changes) | `git reset HEAD~1` |
| **Temporarily** save changes | `git stash` |
| **Restore** stashed changes | `git stash pop` |
| **Safely undo** a pushed commit | `git revert <commit-hash>` |

### Collaboration

| I want to... | Command |
| :--- | :--- |
| **See** who changed what | `git blame filename` |
| **View** specific commit | `git show <commit-hash>` |
| **Compare** branches | `git diff main..feature/name` |
| **Fetch** without merging | `git fetch origin` |

---

## 📚 Additional Resources

- [Official Git Documentation](https://git-scm.com/doc)
- [GitHub Guides](https://guides.github.com/)
- [Interactive Git Tutorial](https://learngitbranching.js.org/)
- [Git Cheat Sheet (PDF)](https://education.github.com/git-cheat-sheet-education.pdf)

---

## 🆘 Need Help?

**Common Issues:**

1. **"Permission denied (publickey)"** → Check SSH keys setup
2. **"Merge conflict"** → See [Section 6, Scenario 4](#-scenario-4-merge-conflicts---what-to-do)
3. **"Detached HEAD state"** → Run `git checkout main`
4. **"Your branch is behind"** → Run `git pull origin main`

**Ask Your Team:**
- Use GitHub Issues for project-specific questions
- Check with team lead for workflow questions

---

*Created for UTMS Project Learning - Team 3 - 2026*  
*Last Updated: January 2026*
