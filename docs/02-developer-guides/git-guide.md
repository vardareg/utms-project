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
12. [Practice Exercises](#12-practice-exercises)

---

## 1. Core Concepts

### 📚 What is Git?

Git is a **Version Control System** — a time machine for your code. It tracks every change you make, allowing you to:

- Revert to previous versions if something breaks
- See who changed what and why
- Work on new features without breaking working code

### ☁️ What is GitHub?

GitHub is a **remote hosting service** for Git repositories.

- **Git** runs on your computer (local)
- **GitHub** stores your code online (remote)

Think of Git as your local save system and GitHub as your cloud backup that also enables team collaboration.

---

## 2. The Golden Workflow

Every change follows this 4-step journey:

1. **Working Directory** 📝 — Where you edit files
   - *You save a file in VS Code*

2. **Staging Area** 📦 — Preparing the shipment
   - *You choose which files to include in the next snapshot*

3. **Local Repository** 📸 — The snapshot
   - *You permanently record the snapshot locally*

4. **Remote Repository** 🚀 — GitHub
   - *You upload your snapshots to the cloud*

💡 **Tip:** This workflow ensures you have full control over what gets saved and shared with your team.

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
       │                         ├──────────────────>│                 │
       │                         │                    │                 │
       │                         │                    │  git push       │
       │                         │                    ├───────────────>│
       │                         │                    │                 │
       │                         │                    │  git pull       │
       │<────────────────────────┴────────────────────┴─────────────────┤
       │                                                                 │
                              (Updates from team)
```

---

## 4. Basic Commands

Here are the essential commands with clear explanations of what they do and when to use them.

### 🔍 Check Status

```bash
git status
```

**What it does:** Shows which files have changed.

- **Red text:** Changed but not staged (modified)
- **Green text:** Staged and ready to commit

📝 **Note:** Run this frequently to understand the current state of your repository.

---

### ➕ Stage Changes

```bash
git add -A
```

**What it does:** Moves changes from "Working Directory" to "Staging Area".

**Options:**

- `git add -A` — Adds all new, modified, and deleted files
- `git add filename` — Adds only a specific file
- `git add .` — Adds all files in the current directory

💡 **Tip:** Use `git add -A` when you want to stage everything. Use specific filenames when you want more control.

---

### 📸 Create Commit

```bash
git commit -m "feat: add user management"
```

**What it does:** Takes a snapshot of staged files and saves it to your local repository.

- `-m` — Lets you write the message inline

**Best practices for commit messages:**

- `feat:` — New feature
- `fix:` — Bug fix
- `docs:` — Documentation changes
- `refactor:` — Code improvements
- `test:` — Adding or updating tests

---

### 🚀 Push to GitHub

```bash
git push origin main
```

**What it does:** Uploads your local commits to GitHub.

- `origin` — The nickname for your remote repository URL
- `main` — The branch you're working on (default branch)

⚠️ **Warning:** Always pull before pushing to avoid conflicts.

---

### 📥 Get Updates (Pull)

```bash
git pull origin main
```

**What it does:** Downloads changes from GitHub and merges them into your local code.

**When to use:**

- At the start of each work session
- Before pushing your changes
- When teammates notify you of updates

---

### 📜 View History

```bash
git log --oneline
```

**What it does:** Shows a concise list of past commits.

You'll see the **commit hash** (e.g., `a1b2c3d`) and the message.

**Enhanced view:**

```bash
git log --graph --oneline --all
```

This shows a visual branch history.

---

### 📦 Clone (Download)

```bash
git clone https://github.com/username/repo-name.git
```

**What it does:** Downloads an entire repository from GitHub to your computer for the first time.

📝 **Note:** You only need to clone once. After that, use `git pull` to get updates.

---

## 5. Branching Strategy

Branches let you work on features without affecting the main codebase.

### 🌿 Why Use Branches?

- **Isolation:** Work on new features without breaking working code
- **Collaboration:** Multiple team members can work simultaneously
- **Safety:** The `main` branch stays stable

---

### Creating a New Branch

```bash
# Create and switch to a new branch
git checkout -b feature/user-authentication

# Or using newer syntax
git switch -c feature/admin-panel
```

**Naming conventions:**

- `feature/description` — New features (e.g., `feature/login-page`)
- `fix/description` — Bug fixes (e.g., `fix/validation-error`)
- `docs/description` — Documentation updates

💡 **Tip:** Use descriptive names that clearly indicate what the branch is for.

---

### Switching Between Branches

```bash
# Switch to existing branch
git checkout main

# Or using newer syntax
git switch main
```

---

### Viewing All Branches

```bash
# List local branches
git branch

# List all branches (including remote)
git branch -a
```

The current branch is marked with an asterisk (*).

---

### Merging a Branch

```bash
# Switch to main branch first
git checkout main

# Merge your feature branch
git merge feature/user-authentication
```

📝 **Note:** Always ensure `main` is up to date before merging: `git pull origin main`

---

### Deleting a Branch

```bash
# Delete local branch (after merging)
git branch -d feature/user-authentication

# Force delete (if not merged)
git branch -D feature/old-experiment
```

⚠️ **Warning:** Only force delete (`-D`) if you're certain you don't need those changes.

---

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

# 6. After approval, merge and clean up
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

# If already pushed, you'll need to force push
git push origin main --force
```

⚠️ **Warning:** Only use `--force` if you're sure no one else has pulled your changes!

---

### 🔧 Scenario 2: "I want to undo changes before committing"

```bash
# Undo changes to a specific file
git restore UserController.java

# Undo all changes in working directory
git restore .
```

💡 **Tip:** This only works for uncommitted changes. Once committed, use `git reset` or `git revert`.

---

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

---

### 🔧 Scenario 4: "Merge Conflicts - What to do?"

When two people edit the same file, Git can't automatically merge. You'll see:

```bash
CONFLICT (content): Merge conflict in UserService.java
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

💡 **Tip:** VS Code has built-in merge conflict tools. Look for "Accept Current Change" and "Accept Incoming Change" buttons.

---

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

---

### 🔧 Scenario 6: "I want to see what changed in a specific commit"

```bash
# Show changes in a commit
git show a1b2c3d

# Show changes in the last commit
git show HEAD
```

---

## 7. Understanding .gitignore

### 🚫 What is .gitignore?

A `.gitignore` file tells Git which files or folders to **ignore** and never commit.

---

### Why Use It?

**Never commit:**

- ❌ Dependencies (`node_modules/`, `target/`)
- ❌ Build outputs (`dist/`, `build/`, `*.class`)
- ❌ Sensitive data (`.env`, `application-secrets.properties`)
- ❌ IDE files (`.idea/`, `.vscode/`, `*.iml`)
- ❌ OS files (`.DS_Store`, `Thumbs.db`)
- ❌ Log files (`*.log`)

**Why?** These files are either:

- Generated automatically (can be rebuilt)
- Contain sensitive information (security risk)
- Specific to your machine (cause conflicts for teammates)

---

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

---

### How to Use

1. Create a file named `.gitignore` in your project root
2. Add patterns (one per line)
3. Commit the `.gitignore` file itself:

   ```bash
   git add .gitignore
   git commit -m "chore: add gitignore"
   ```

📝 **Note:** Files already committed won't be ignored. You need to remove them first:

```bash
git rm --cached filename
git commit -m "chore: remove file from tracking"
```

---

## 8. Collaboration Best Practices

### 🤝 Team Guidelines for UTMS Project

#### 1. Always Pull Before Starting Work

```bash
# Start of every work session
git checkout main
git pull origin main
```

This prevents conflicts and ensures you have the latest code.

---

#### 2. Write Meaningful Commit Messages

**Good examples:**

- ✅ `feat: add student application form validation`
- ✅ `fix: resolve null pointer in ApplicationService`
- ✅ `docs: update API documentation for evaluation endpoints`

**Bad examples:**

- ❌ `update`
- ❌ `fix stuff`
- ❌ `asdfasdf`

**Commit message format:**

```
<type>: <short description>

[optional body explaining WHY, if needed]
```

**Types:**

- `feat:` — New feature
- `fix:` — Bug fix
- `docs:` — Documentation
- `style:` — Formatting, missing semicolons (no logic change)
- `refactor:` — Code restructuring (no feature change)
- `test:` — Adding or updating tests
- `chore:` — Maintenance tasks (dependencies, config)

---

#### 3. Never Commit Sensitive Data

❌ **Never commit:**

- Passwords
- API keys
- Database credentials
- JWT secrets

✅ **Instead:**

- Use environment variables
- Add sensitive files to `.gitignore`
- Use `application-secrets.properties` (gitignored)

⚠️ **Warning:** If you accidentally commit a secret, changing it in a new commit is NOT enough. The secret is still in Git history. You must rotate the credential immediately.

---

#### 4. Keep Commits Focused

- One commit = one logical change
- Don't mix unrelated changes (e.g., don't combine a bug fix with a new feature)
- Makes code review easier and debugging faster

💡 **Tip:** If your commit message needs "and" more than once, consider splitting it into multiple commits.

---

#### 5. Pull Before Push

```bash
# Before pushing
git pull origin main

# Resolve any conflicts if they appear

# Then push
git push origin main
```

This prevents the "Your branch is behind" error.

---

#### 6. Communicate with Your Team

- Announce when working on a major feature
- Use GitHub Issues to track tasks
- Review each other's Pull Requests
- Ask questions when unsure

---

## 9. GitHub-Specific Features

### 🔀 Pull Requests (PRs)

A Pull Request is how you propose changes and get them reviewed before merging into `main`.

---

**Workflow:**

**1. Create a feature branch and push it:**

```bash
git checkout -b feature/admin-dashboard
# Make your changes
git add -A
git commit -m "feat: add admin dashboard"
git push origin feature/admin-dashboard
```

**2. On GitHub:**

- Go to your repository
- Click "Pull Requests" → "New Pull Request"
- Select your branch (`feature/admin-dashboard`)
- Add a description of your changes
- Click "Create Pull Request"

**3. Code Review:**

- Team members review your code
- They can comment and request changes
- You can push more commits to address feedback

**4. Merge:**

- Once approved, click "Merge Pull Request"
- Delete the branch on GitHub
- Pull the updated main locally:

  ```bash
  git checkout main
  git pull origin main
  git branch -d feature/admin-dashboard
  ```

💡 **Tip:** Write clear PR descriptions. Explain what you changed and why. Include screenshots for UI changes.

---

### 📋 GitHub Issues

Track bugs, features, and tasks in a centralized location.

**Creating an Issue:**

1. Go to "Issues" tab
2. Click "New Issue"
3. Add title and description
4. Assign to team member (optional)
5. Add labels (`bug`, `enhancement`, `documentation`)

**Linking commits to issues:**

```bash
git commit -m "fix: resolve login timeout issue (#42)"
```

This automatically links to Issue #42 and adds a reference in the issue thread.

---

### 🔍 Code Review on GitHub

**Reviewing a PR:**

1. Go to "Pull Requests"
2. Click on the PR you want to review
3. Go to "Files changed" tab
4. Click on line numbers to add inline comments
5. Click "Review changes" → "Approve" or "Request changes"

**Best practices:**

- Be constructive and specific
- Explain why, not just what
- Praise good code too
- Ask questions instead of making demands

---

### ⚡ GitHub Actions (CI/CD Basics)

GitHub Actions can automatically run tasks when you push code:

- Run tests
- Build your application
- Deploy to servers
- Check code quality

**Example:** Automatically run tests on every push to `main`.

📝 **Note:** Setting up GitHub Actions is beyond this guide's scope, but it's a powerful feature worth exploring as you advance.

---

## 10. Emergency Commands

### 🚨 Use These Carefully

These commands are powerful and can cause data loss if misused. Read carefully before using.

---

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

---

#### git reset - Undo Commits

```bash
# Undo last commit, keep changes staged
git reset --soft HEAD~1

# Undo last commit, keep changes unstaged
git reset HEAD~1

# Undo last commit, DELETE changes (DANGEROUS!)
git reset --hard HEAD~1
```

⚠️ **Warning:** `--hard` permanently deletes changes. There's no undo!

**Use case:** You committed too early or to the wrong branch.

---

#### git revert - Safely Undo a Commit

```bash
# Create a new commit that undoes a previous commit
git revert a1b2c3d
```

**Difference from reset:**

- `reset` — Rewrites history (dangerous if already pushed)
- `revert` — Creates new commit (safe for shared branches)

**Use case:** Undo a commit that's already pushed to GitHub.

---

#### git clean - Remove Untracked Files

```bash
# See what would be deleted (dry run)
git clean -n

# Delete untracked files
git clean -f

# Delete untracked files and directories
git clean -fd
```

⚠️ **Warning:** This permanently deletes files not tracked by Git!

---

#### git reflog - Recover "Lost" Commits

```bash
# View all actions (even deleted commits)
git reflog

# Restore a commit
git checkout a1b2c3d
```

**Use case:** You accidentally deleted commits with `git reset --hard` and need them back.

💡 **Tip:** `git reflog` is your safety net. Git keeps deleted commits for about 30 days.

---

## 11. Summary Cheat Sheet

### Basic Operations

| I want to...              | Command                     |
|:--------------------------|:----------------------------|
| **Check** what changed    | `git status`                |
| **Stage** all files       | `git add -A`                |
| **Stage** specific file   | `git add filename`          |
| **Save** a snapshot       | `git commit -m "message"`   |
| **Upload** to GitHub      | `git push origin main`      |
| **Download** updates      | `git pull origin main`      |
| **See** history           | `git log --oneline`         |
| **Clone** repository      | `git clone <url>`           |

---

### Branching

| I want to...              | Command                          |
|:--------------------------|:---------------------------------|
| **Create** new branch     | `git checkout -b feature/name`   |
| **Switch** to branch      | `git checkout branch-name`       |
| **List** all branches     | `git branch -a`                  |
| **Merge** branch          | `git merge feature/name`         |
| **Delete** branch         | `git branch -d feature/name`     |

---

### Fixing Mistakes

| I want to...                       | Command                              |
|:-----------------------------------|:-------------------------------------|
| **Undo** file changes              | `git restore filename`               |
| **Fix** last commit message        | `git commit --amend -m "new msg"`    |
| **Undo** last commit (keep changes)| `git reset HEAD~1`                   |
| **Temporarily** save changes       | `git stash`                          |
| **Restore** stashed changes        | `git stash pop`                      |
| **Safely undo** a pushed commit    | `git revert <commit-hash>`           |

---

### Collaboration

| I want to...              | Command                          |
|:--------------------------|:---------------------------------|
| **See** who changed what  | `git blame filename`             |
| **View** specific commit  | `git show <commit-hash>`         |
| **Compare** branches      | `git diff main..feature/name`    |
| **Fetch** without merging | `git fetch origin`               |

---

## 12. Practice Exercises

Ready to solidify your Git skills? Try these hands-on exercises.

### Exercise 1: Basic Workflow

1. Create a new file called `test.txt` in your project
2. Stage and commit it with a meaningful message
3. Make a change to the file
4. Stage and commit the change
5. View your commit history with `git log --oneline`

---

### Exercise 2: Branching

1. Create a new branch called `feature/practice`
2. Make some changes and commit them
3. Switch back to `main`
4. Merge your feature branch
5. Delete the feature branch

---

### Exercise 3: Handling Conflicts

1. Create a branch called `conflict-test`
2. Edit line 1 of `test.txt` and commit
3. Switch to `main`
4. Edit the same line differently and commit
5. Try to merge `conflict-test` into `main`
6. Resolve the conflict manually
7. Complete the merge

---

### Exercise 4: Using Stash

1. Make some changes but don't commit
2. Stash your changes
3. Make different changes and commit them
4. Pop your stash and resolve any conflicts
5. Commit the restored changes

---

### Next Steps

Once you're comfortable with these basics:

- Learn about **rebasing** for cleaner history
- Explore **Git hooks** for automation
- Study **GitHub Actions** for CI/CD
- Practice **cherry-picking** specific commits
- Master **interactive rebase** for commit organization

---

## 📚 Additional Resources

- [Official Git Documentation](https://git-scm.com/doc)
- [GitHub Guides](https://guides.github.com/)
- [Interactive Git Tutorial](https://learngitbranching.js.org/) — Highly recommended!
- [Git Cheat Sheet (PDF)](https://education.github.com/git-cheat-sheet-education.pdf)
- [Oh Shit, Git!?!](https://ohshitgit.com/) — Real solutions to common problems

---

## 🆘 Need Help?

**Common Issues:**

| Issue | Solution |
|:------|:---------|
| "Permission denied (publickey)" | Check SSH keys setup in GitHub settings |
| "Merge conflict" | See [Section 6, Scenario 4](#-scenario-4-merge-conflicts---what-to-do) |
| "Detached HEAD state" | Run `git checkout main` |
| "Your branch is behind" | Run `git pull origin main` |
| "Cannot push" | Pull first: `git pull origin main` |

**Ask Your Team:**

- Use GitHub Issues for project-specific questions
- Check with your team lead for workflow questions
- Don't hesitate to ask — everyone was a beginner once!

---

*Created for UTMS Project Learning - Team 3 - 2026*  
*Last Updated: January 2026*

---

**Remember:** Git is a skill that improves with practice. Don't be afraid to experiment in a test repository. The more you use it, the more natural it becomes!
