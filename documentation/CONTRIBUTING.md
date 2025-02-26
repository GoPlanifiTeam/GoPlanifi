# Contributing Guide

## Branching Strategy

Our team follows a **feature-branching workflow** to ensure smooth collaboration and maintain code stability.

### Branch Structure

- **`main`**: The default branch where stable, reviewed, and tested code is merged.
- **`origin/daniel/dev`** & **`origin/ferran/dev`**: Each developer works on their own feature branch to ensure separation of concerns.
- Additional feature branches can be created based on new tasks.

### Workflow

1. **Create a new branch** before working on a feature or bug fix:
   ```sh
   git checkout -b feature/branch-name
   ```
2. **Make changes** and commit regularly with descriptive messages:
   ```sh
   git commit -m "Add feature XYZ"
   ```
3. **Push your branch** to the remote repository:
   ```sh
   git push origin feature/branch-name
   ```
4. **Create a pull request (PR)** targeting `main`.
5. **Request a review** from another team member.
6. **Merge** the PR after approval.
