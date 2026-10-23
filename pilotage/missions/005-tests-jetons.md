# Mission 005 — tests-jetons

> Fiche de mission (fiches n° 9 et 15 de la méthode). Une mission = une phrase = une
> branche = une PR. Tirée de `docs/spec.md` (validée le 2026-10-23), issue #1.

## Mission

**En une phrase (quoi, et pourquoi) :** Écrire, sans lire le code de génération, les tests d'acceptation des jetons (couples à 7:1 du critère 2, plage ARGB, rôles « interdit en texte », marge affichée), pour que les valeurs générées soient jugées par la spec.

**Redite par l'agent :** Écrire, depuis la spec seule et sans lire le code de génération, les tests qui jugeront les jetons (couples à 7:1 par thème avec leur marge, plage ARGB, rôles interdits en texte documentés), pour que la mission 006 soit jugée par la spec et non par elle-même.

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

## Décisions de Pascal et remarques du relecteur — aller-retour 1/3 (2026-10-23)

À reprendre dans `JetonsTest.kt`, branche `mission/005-tests-jetons` :

1. **Test README exact** : aujourd'hui `texte.contains(n)` compte `primary` documenté dès
   que le README cite `primaryContainer`. Chercher le nom exact entre accents graves
   (`` `primary` ``, `` `alerte.couleur` ``). Le test reste dans 005 : **les missions 006 et
   007 écriront chacune leur ligne au README** (décision de Pascal).
2. **Anti-complaisance : gardé, limité** (décision de Pascal) aux rôles qui ont au moins un
   couple de texte ; `alerte.couleur` reste exclu. Le dire dans le commentaire du test.
3. **`Schema` refuse aussi ses 48 valeurs hors plage** par `IllegalArgumentException`
   (décision de Pascal) : ajouter un test qui construit un `Schema` avec une valeur fautive.
4. **Commentaire de troncature** : expliquer la tolérance de 1e-9 avant l'arrondi par le bas
   (bruit flottant, cohérent avec `ContrasteTest`), puisque la spec dit « jamais arrondi ».
5. Convention de nom validée : `primary` (nom du champ), `reussi.couleur` / `alerte.couleur`
   ; c'est le rôle qui sert de texte qui passe « interdit en texte ».
6. Remplir « Redite par l'agent » en tête de fiche.

**Fusion** (décision de Pascal) : la PR 005 reste en brouillon ; la mission 006 part de cette
branche, la 007 de la 006 ; les trois fusionnent ensemble, vertes.
