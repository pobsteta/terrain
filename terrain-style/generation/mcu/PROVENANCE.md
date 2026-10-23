# Provenance des sources Material Color Utilities

- **Dépôt :** https://github.com/material-foundation/material-color-utilities
- **Commit :** `bd811e4ca2ab94c4ce3cadc89c19b1820b9704b3` (2026-10-19, « Automated Code Change »),
  retenu par Pascal (ADR 0001)
- **Recopié :** le dossier `kotlin/` entier (43 fichiers `.kt` et `LICENSE`), le 2026-10-23,
  par `git fetch --depth 1 origin <commit>`
- **Licence :** Apache-2.0 (`LICENSE`, copie de `kotlin/LICENSE`) ; en-têtes de droit d'auteur
  gardés ; MCU n'a pas de fichier NOTICE
- **Fichiers modifiés :** aucun. Les 43 fichiers sont identiques à ceux du commit (sha256
  comparés à la copie) et compilent tels quels avec Kotlin 2.2.10. Toute modification future
  doit être marquée en tête du fichier modifié (Apache-2.0 §4(b)) et listée ici.

Ces sources ne servent qu'au source set `generation` de `terrain-style` (tâche
`:terrain-style:generer`) ; elles n'entrent pas dans le jar publié.
