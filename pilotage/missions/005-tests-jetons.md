# Mission 005 — tests-jetons

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de `docs/spec.md` (validée le 2026-10-23), issue #1.

## Mission

**En une phrase (quoi, et pourquoi) :** Écrire, sans lire le code de génération, les tests d'acceptation des jetons (couples à 7:1 du critère 2, plage ARGB, rôles « interdit en texte », marge affichée), pour que les valeurs générées soient jugées par la spec.

**Redite par l'agent :** _à compléter par l'agent avant d'agir — s'il n'y arrive pas, il s'arrête._

## Les trois questions (règle de staffing)

| Question | Réponse | Conséquence |
|---|---|---|
| Peut-il redire la mission en une phrase, avec son but ? | oui | — |
| Sait-il comment faire ? (sources, conventions nommées ci-dessous) | oui : critères 2 et 5 de la spec, API `Schema` / `Semantique` proposée | — |
| A-t-il les moyens ? (outils, données d'essai, tests) | oui | — |

**Temps de la main :** appuyer.

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent : ce qu'il attend de la
mission, et le résultat qu'il prévoit. Une ligne suffit. L'orchestrateur ne le remplit
pas._

## L'Homme

- **Agent :** `testeur`
- **Modèle :** sonnet
- **Ce qui ne lui est pas demandé :** générer les jetons ; choisir les couples (la spec les fixe).

## Harnais

- **Peut :** écrire les tests Kotlin de `terrain-style` contre l'API de la spec, ouvrir une PR.
- **Ne doit pas :** toucher à `main`, sortir du périmètre, inventer une valeur ou une source, trancher une question de valeur (la poser à Pascal) ; lire le code de génération ; changer la liste des couples
- **Sources autorisées :** `docs/spec.md` (critères 2 et 5, API, rôles couverts), `docs/brief.md` (arbitrages).

## Tests

- **Comment saura-t-on que c'est juste :** un test par couple et par thème, rapport tronqué ≥ 7,0, sauf rôle listé « interdit en texte » ; sortie lisible de la marge de chaque couple ; un rôle interdit en texte qui n'est pas documenté fait échouer le test.

## Terrain

- **Échantillon que Pascal vérifiera :** la sortie du test (marges par couple).

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours ; au-delà, la décision remonte à
  Pascal.
- **Point de suivi :** à la PR ; elle accompagne la mission 006.
- **Qui tranche :** Pascal.

## Comment saura-t-on que ça marche ?

Les tests compilent contre l'API, échouent tant que la mission 006 n'est pas faite, puis jugent ses valeurs.
