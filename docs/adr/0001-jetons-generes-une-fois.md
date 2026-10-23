# ADR 0001 — jetons générés une fois par les sources Java de Material Color Utilities, figés dans le code

- **Date :** 2026-10-23
- **Statut :** proposée
- **Proposée par :** Claude (orchestrateur)
- **Tranchée par :** Pascal

## La décision en une phrase

Les jetons de `terrain-style` sont calculés une fois par un script JVM qui compile les
sources Java de Material Color Utilities (MCU), recopiées dans
`terrain-style/generation/mcu/` à un commit inscrit, en `SPEC_2021`, puis écrits en
Kotlin et figés ; le module publié ne dépend pas de MCU.

## Contexte

L'issue #1 exige des valeurs générées par MCU puis figées, avec le script et ses réglages
inscrits, et pas de dépendance à l'exécution. Reste à choisir **comment exécuter MCU** et
**quelle spec**. MCU ne publie pas d'artefact JVM : la table « Library availability » de
son README renvoie Java à MDC-Android, un AAR Android.

Options envisagées :

1. **Sources Java recopiées** au commit inscrit, compilées par une tâche Gradle séparée
   (`:terrain-style:generer`), hors du build publié.
2. **Paquet npm** `@material/material-color-utilities` à une version épinglée, script Node.
3. **AAR de Material Components Android** : exige Android, contraire à la règle du module.
4. **Calcul HCT réécrit en Kotlin** dans le module : plus de dépendance du tout, mais on
   réinvente et l'on perd la référence.
5. **Valeurs exportées de Material Theme Builder** (outil web) : pas de script rejouable.

Spec : `SpecVersion` compte `SPEC_2021`, `SPEC_2025` et `SPEC_2026` ;
`DEFAULT_SPEC_VERSION` vaut encore `SPEC_2021`. L'issue #1 demande `SPEC_2021`.

## Le crible FRANCE

| Lettre | Question | Réponse (une ligne) | Ce que ça coûte | Prêts à payer ? |
|---|---|---|---|---|
| **F**rugale | La plus simple qui tienne ? | Option 1 : même langage (JVM) que le projet, une seule chaîne d'outils | quelques milliers de lignes Java recopiées dans le dépôt | _à trancher_ |
| **R**esponsable | Qui fait quoi ? | `dev` écrit le script et le test ; `relecteur` vérifie le rejeu à l'octet près | — | _à trancher_ |
| **A**nticiper | Si MCU change ou disparaît ? | les sources au commit inscrit restent dans le dépôt ; le rejeu ne dépend pas du réseau | on ne suit pas les corrections de MCU sans nouvelle ADR | _à trancher_ |
| **N**ovateur | Toutes les idées ? | cinq options, dont la réécriture et l'export manuel | — | — |
| **C**oût | Dépense inutile ? | pas de Node ni d'Android à installer ; le build publié n'est pas alourdi | dépôt plus lourd de la taille des sources MCU | _à trancher_ |
| **É**cologique | Réutilisation ? | réutilise MCU (Apache-2.0, compatible avec GPL-3.0) ; CSV de contrôle lisible par tous | mention de licence et `NOTICE` à tenir | _à trancher_ |

## Valeurs sacrifiées

- **Suivre l'évolution de Material** (SPEC_2025, SPEC_2026) : on fige SPEC_2021 pour
  avoir des valeurs stables et rejouables ; en changer est une nouvelle ADR.
- **Légèreté du dépôt** : les sources MCU y sont recopiées, pour qu'un successeur rejoue
  sans réseau.

## Sources

- Material Color Utilities, README (« Library availability ») et `java/dynamiccolor/`
  (`ColorSpec`, `DynamicScheme.DEFAULT_SPEC_VERSION`, `ColorSpec2021`) —
  https://github.com/material-foundation/material-color-utilities
- Licence Apache-2.0 et compatibilité GPL-3.0 — https://www.gnu.org/licenses/license-list.html#apache2
- Issue #1 du dépôt — https://github.com/pobsteta/terrain/issues/1

## Question de contrôle

Cette décision tient-elle nos six valeurs, et que sommes-nous prêts à payer pour cela ?
