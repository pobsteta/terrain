# Mission 024 — décisions et contrat avant lancement (plugin)

> Fiche de mission. Tirée du bilan n° 3 du 2026-10-24 (causes B et C du Pareto, décision de Pascal).

## Mission

**En une phrase (quoi, et pourquoi) :** Ajouter au gabarit de fiche du plugin `imperatrice` une rubrique « Décisions et contrat avant lancement » (cas limites tranchés, contrat d'API et version visée d'un lot test + code, sources figées par commit), et à `/boucle` et `/mission` la lecture des briefs entrants (`~/dev/briefs/vers-<projet>/` et issues) avant toute mission et tout tag, parce que 15 des 28 incidents du bilan n° 3 de terrain (causes B et C) viennent d'une consigne incomplète ou d'un contrat posé trop tard.

**Redite par l'agent :** _à compléter._

**Temps de la main :** appuyer.

## L'Homme

- **Agent :** `archiviste`, sonnet.

## Harnais

- **Peut :**
  - travailler dans le dépôt du plugin `/home/pascal/dev/methode-imperatrice` : branche, PR ;
  - modifier `skills/mission/fiche-mission.md`, `skills/mission/SKILL.md` et `skills/boucle/SKILL.md`, plus leurs tests (`tests/test_*.py`) ;
  - monter la version (`scripts/verifier_version.py`) en 0.7.0 ;
  - écrire dans le journal du plugin, en ajout seul.
- **Ne doit pas :** toucher à `main` ; publier un tag (`release.yml` publie à la fusion) ; modifier d'autres skills.
- **Sources :** Pareto du bilan n° 3 de terrain (`/home/pascal/dev/terrain/pilotage/journal.md`, ligne « analyste · bilan 3 ») ; les rubriques « Valeur provisoire » et « Dépend de » du gabarit actuel, à ne pas dupliquer.

## Dépend de / rouge attendu jusqu'à

Dépend de : rien. CI rouge attendue : aucune.

## Tests

- tests du plugin verts un par un ;
- `claude plugin validate .` passe ;
- la nouvelle rubrique ne répète pas « Valeur provisoire » ni « Dépend de » : elle y renvoie.

## Terrain

- **Échantillon :** la nouvelle rubrique, lue par Pascal : aurait-elle évité le renvoi de 016 (`disambig_tid.int`) et la v0.6.1 publiée sans le brief de Marculus ?

## Ma réponse d'abord

_Rempli par Pascal seul, avant de lire le travail de l'agent._

## Valeur provisoire

Aucune.

## Qui veille, qui tranche

- **Relecteur :** `relecteur`, puis Pascal.
- **Critère d'arrêt de la relecture :** 3 allers-retours.
- **Qui tranche :** Pascal.
