# Team 3 - Git & GitHub Workflow

This document outlines the standard version control practices for the **Undergraduate Transfer Management System (UTMS)**.

## 1. Branching Strategy

We use the **Feature Branch Workflow**.

* **`main`**: The production-ready code. Never commit directly to `main`.
* **`feature/...`**: For new features (e.g., `feature/login-page`, `feature/score-calculation`).
* **`fix/...`**: For bug fixes (e.g., `fix/cors-error`).
* **`docs/...`**: For documentation updates.

### Creating a Branch

```bash
# Ensure you are on the latest main
git checkout main
git pull origin main

# Create a new branch
git checkout -b feature/my-cool-feature
```

## 2. Commit Convention

We follow **Conventional Commits** to keep history readable.

**Format**: `type(scope): subject`

**Types**:

* `feat`: A new feature
* `fix`: A bug fix
* `docs`: Documentation only changes
* `style`: Formatting, missing semi-colons, etc; no code change
* `refactor`: A code change that neither fixes a bug nor adds a feature
* `test`: Adding missing tests or correcting existing tests
* `chore`: Changes to build process or auxiliary tools (e.g., pom.xml changes)

**Examples**:

* `feat(auth): implement JWT token generation`
* `fix(frontend): resolve CORS error on login`
* `docs(readme): add setup instructions`

## 3. Workflow Steps

1. **Work**: Make changes in your feature branch.
2. **Stage**: `git add .` (or specific files).
3. **Commit**: `git commit -m "feat(api): add student endpoints"`
4. **Push**: `git push -u origin feature/my-cool-feature`
5. **Pull Request (PR)**:
    * Go to GitHub.
    * Open a Pull Request from your branch to `main`.
    * Describe your changes.
    * Request review.
6. **Merge**: Once approved, merge into `main`.

## 4. Keeping Up-to-Date

If `main` has changed while you were working:

```bash
git checkout main
git pull origin main
git checkout feature/my-cool-feature
git merge main
# Resolve conflicts if any
```
