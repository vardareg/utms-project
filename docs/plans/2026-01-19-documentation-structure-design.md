# Documentation Structure Proposal

This proposal reorganizes the project documentation to clearly separate user-facing guides, developer resources, and project management artifacts.

## Proposed Directory Tree

```
docs/
├── 01-user-guides/          # For End Users & Admins
│   ├── user-manual.md       # (Move from docs/user-manual.md)
│   ├── distribution.md      # (Move from DISTRIBUTION_GUIDE.md)
│   └── release-notes/       # (Move from .github/release-notes-*.md)
│
├── 02-developer-guides/     # For Contributors & Developers
│   ├── quick-start.md       # (Move from QUICK_START.md)
│   ├── git-workflow.md      # (Move from docs/git-guide.md & docs/GIT_WORKFLOW.md)
│   ├── docker-setup.md      # (Move from docs/docker-guide.md)
│   ├── release-process.md   # (Move from GITHUB_RELEASE_GUIDE.md)
│   ├── agents.md            # (Move from AGENTS.md)
│   └── test-credentials.txt # (Move from docs/test-credentials.txt)
│
├── 03-project-specs/        # Requirements & Design (Static/Reference)
│   ├── srs.md               # (Move from .instructions/TEAM3_..._SRS.md)
│   ├── sdd.md               # (Move from .instructions/TEAM3_SDD_updated-1.md)
│   └── spmp.md              # (Move from .instructions/TEAM3 SPMP.md)
│
└── README.md                # (New index file linking to all sections)
```

## Rationale

1.  **Numbered Folders:** Uses `01-`, `02-` prefixes to keep the most important/common folders at the top and clearly ordered.
2.  **Audience Separation:**
    *   `01-user-guides`: Documents for people *using* or *deploying* the built application.
    *   `02-developer-guides`: Documents for people *building* or *modifying* the code.
    *   `03-project-specs`: Historical or reference documents defining *what* was built.
3.  **Root Cleanup:** Moves cluttered markdown files (`AGENTS.md`, `QUICK_START.md`, etc.) out of the root, leaving only the main `README.md` as the entry point.

## Action Plan

1.  Create the new directory structure.
2.  Move existing files to their new locations.
3.  Update the main `README.md` to link to the new locations.
4.  Delete empty source directories (`.instructions`).

Does this structure look right to you?
