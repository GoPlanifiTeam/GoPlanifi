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

-------------------------------------------------------------------
# Guía de Contribución

## Estrategia de Ramificación

Nuestro equipo sigue un **flujo de trabajo basado en ramas de características** para garantizar una colaboración fluida y mantener la estabilidad del código.

### Estructura de Ramas

- **`main`**: La rama principal donde se fusiona el código estable, revisado y probado.
- **`origin/daniel/dev`** & **`origin/ferran/dev`**: Cada desarrollador trabaja en su propia rama de características para garantizar la separación de responsabilidades.
- Se pueden crear ramas adicionales según nuevas tareas.

### Flujo de Trabajo

1. **Crear una nueva rama** antes de trabajar en una característica o corrección de errores:
   ```sh
   git checkout -b feature/nombre-de-rama
   ```
2. **Realizar cambios** y hacer commits regularmente con mensajes descriptivos:
   ```sh
   git commit -m "Agregar funcionalidad XYZ"
   ```
3. **Subir tu rama** al repositorio remoto:
   ```sh
   git push origin feature/nombre-de-rama
   ```
4. **Crear un pull request (PR)** apuntando a `main`.
5. **Solicitar una revisión** a otro miembro del equipo.
6. **Fusionar** el PR después de su aprobación.
