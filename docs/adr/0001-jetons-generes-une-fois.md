# ADR 0001 — jetons générés une fois par les sources de Material Color Utilities, figés dans le code

- **Date :** 2026-10-23
- **Statut :** acceptée par Pascal le 2026-10-23 ; variante amendée par l'ADR 0004 (`CONTENT` au lieu de `FIDELITY`) ; **remplacée par l'ADR 0006 le 2026-10-24** (le style de Marculus est la règle)
- **Proposée par :** Claude (orchestrateur)
- **Tranchée par :** Pascal

## La décision en une phrase

Les jetons de `terrain-style` sont calculés une fois par un script qui compile les sources
**Kotlin** de Material Color Utilities (MCU), recopiées dans
`terrain-style/generation/mcu/` à un commit inscrit, puis écrits en Kotlin et figés ; le
module publié ne dépend pas de MCU, et la CI rejoue la génération.

## Contexte

L'issue #1 exige des valeurs générées par MCU puis figées, avec le script et ses réglages
inscrits, et pas de dépendance à l'exécution. Reste à choisir **comment exécuter MCU**.
MCU ne publie pas d'artefact JVM, mais son dépôt porte des sources Java (`java/`) et
Kotlin (`kotlin/`), mêmes paquets.

Options envisagées :

1. **Sources Kotlin de MCU recopiées** au commit inscrit, compilées par une tâche Gradle à
   part (`:terrain-style:generer`), hors du build publié. Pas d'import Android ni
   errorprone.
2. **Sources Java de MCU recopiées** : elles importent `androidx.annotation.NonNull` /
   `Nullable` et `com.google.errorprone.annotations` ; il faut deux dépendances
   `compileOnly` ou des annotations bouche-trou.
3. **MaterialKolor** (`com.materialkolor:material-color-utilities`, portage Kotlin
   Multiplatform tiers sur Maven Central) : artefact prêt, mais portage non officiel.
4. **Paquet npm** `@material/material-color-utilities` à une version épinglée : une
   seconde chaîne d'outils (Node).
5. **AAR de Material Components Android** : exige Android, contraire à la règle du module.
6. **Calcul HCT réécrit à la main** : on réinvente et l'on perd la référence.
7. **Export de Material Theme Builder** : pas de script rejouable.

**Version de spec** : `DynamicScheme.maybeFallbackSpecVersion` ramène toute variante
autre que CMF, EXPRESSIVE, VIBRANT, TONAL_SPOT et NEUTRAL à `SPEC_2021`. Avec `FIDELITY`
(arbitrage de Pascal), `SPEC_2021` est donc **imposée**, pas choisie. Le script vérifie
`scheme.specVersion == SPEC_2021`.

**Commit MCU retenu** (Pascal, 2026-10-23) : `bd811e4ca2ab94c4ce3cadc89c19b1820b9704b3`
(2026-10-19), plutôt que `dd62982` ; constats dans `docs/referentiels/mcu-compose.md`.

À vérifier par l'`acquereur` avant la mission : parité des sources Kotlin et Java (générer
les deux, comparer les ARGB), et version de Kotlin exigée par les sources Kotlin (le projet
compile en Kotlin 2.2).

## Le crible FRANCE

| Lettre | Question | Réponse (une ligne) | Ce que ça coûte | Prêts à payer ? |
|---|---|---|---|---|
| **F**rugale | La plus simple qui tienne ? | Option 1 : même langage que le projet, aucune dépendance ajoutée | quelques milliers de lignes Kotlin recopiées | inconnu |
| **R**esponsable | Qui fait quoi ? | `dev` écrit le script et le test ; la CI rejoue la génération et échoue sur toute différence (`git diff --exit-code`) ; `relecteur` en plus | une étape de CI | inconnu |
| **A**nticiper | Si MCU change ou disparaît ? | les sources au commit inscrit restent dans le dépôt ; le rejeu ne dépend pas du réseau ; le commit est aussi en tête du code généré | suivre une correction de MCU = nouvelle ADR et régénération | inconnu |
| **N**ovateur | Toutes les idées ? | sept options, dont le portage tiers et la réécriture | — | — |
| **C**oût | Dépense inutile ? | ni Node ni Android à installer ; le build publié n'est pas alourdi | dépôt plus lourd de la taille des sources MCU | inconnu |
| **É**cologique | Réutilisation ? | réutilise MCU (Apache-2.0, compatible avec GPL-3.0 dans ce sens) ; LICENSE de MCU copiée dans `generation/mcu/`, en-têtes gardés, fichiers modifiés marqués (Apache-2.0 §4 ; MCU n'a pas de NOTICE) ; tables CSV lisibles par tous | tenir ces mentions | inconnu |

## Valeurs sacrifiées

- **Accès à Material 3 Expressive** (SPEC_2025 et suivantes, qui n'offrent que
  TONAL_SPOT, VIBRANT, EXPRESSIVE et NEUTRAL) : c'est le prix de `FIDELITY`, arbitré par
  Pascal ; cette ADR le rend visible.
- **Légèreté du dépôt** : les sources MCU y sont recopiées, pour qu'un successeur rejoue
  sans réseau.

## Sources

- Material Color Utilities : README (« Library availability »), `kotlin/` et `java/`
  (`dynamiccolor/DynamicScheme` : `maybeFallbackSpecVersion` ; `ColorSpec2021` ; imports
  `androidx.annotation` et `errorprone` des sources Java) —
  https://github.com/material-foundation/material-color-utilities
- MaterialKolor — https://central.sonatype.com/artifact/com.materialkolor/material-kolor
- Licence Apache-2.0, §4 — https://www.apache.org/licenses/LICENSE-2.0
- Compatibilité Apache-2.0 → GPL-3.0 — https://www.gnu.org/licenses/license-list.html#apache2
- Issue #1 du dépôt — https://github.com/pobsteta/terrain/issues/1

## Notes datées (ajoutées, rien n'est effacé ci-dessus)

- **Levé le 2026-10-23 : « À vérifier par l'`acquereur` avant la mission ».** Vérifié par la
  mission 006 (journal) : les 43 sources Kotlin de MCU à `bd811e4` compilent en Kotlin 2.2.10 ;
  parité Kotlin/Java de 0 écart sur 1 872 ARGB, mesurée avec la variante `FIDELITY` puis, après
  l'ADR 0004, avec `CONTENT`.
- **Écart E3 (option 2).** « Deux dépendances `compileOnly` » est inexact : huit fichiers Java
  importent `androidx.annotation` ou `errorprone` (`docs/referentiels/mcu-compose.md`, E3). Cela
  n'a pas changé la décision : l'option 1 (sources Kotlin) a été retenue.
- **Écart E1 (version de spec).** La phrase « toute variante autre que CMF, EXPRESSIVE,
  VIBRANT, TONAL_SPOT et NEUTRAL » est trop simple : `CMF` garde toujours la version demandée,
  et `CONTENT` sur `Platform.WATCH` aussi (`mcu-compose.md`, E1). Le constat utile tient :
  `FIDELITY`, comme `CONTENT` sur téléphone, retombe à `SPEC_2021`.

## Question de contrôle

Cette décision tient-elle nos six valeurs, et que sommes-nous prêts à payer pour cela ?

## Note du 2026-10-24 : remplacée par l'ADR 0006

Le 2026-10-24, Pascal a décidé que `terrain-style` reprend le style de Marculus (ADR 0006). Les 48 rôles ne sont plus générés par Material Color Utilities, et cette décision ne s'applique plus. Elle est gardée pour la mémoire.
