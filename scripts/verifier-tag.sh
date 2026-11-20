#!/usr/bin/env bash
# Contrôle du nom d'un tag de publication : vAAAA.M.N (mois sans zéro en tête, N >= 1).
# Usage : verifier-tag.sh <tag> [<date-du-commit AAAA-MM-JJ> [<date-du-jour AAAA-MM-JJ>]]
# Sans dates, elles sont lues sur HEAD (UTC) et sur l'horloge. Code 0 : tag admis ; 1 : refusé.
set -euo pipefail

tag="${1:?usage: verifier-tag.sh <tag> [date-commit [date-jour]]}"
date_commit="${2:-$(TZ=UTC git log -1 --format=%cd --date=format-local:%Y-%m-%d)}"
date_jour="${3:-$(date -u +%Y-%m-%d)}"

if ! [[ "$tag" =~ ^v([0-9]{4})\.([1-9]|1[0-2])\.([1-9][0-9]*)$ ]]; then
  echo "::error::Tag $tag refusé : la notation est vAAAA.M.N (mois sans zéro en tête, N >= 1), par exemple v2026.11.1."
  exit 1
fi
annee="${BASH_REMATCH[1]}"
mois="${BASH_REMATCH[2]}"

# Année et mois du tag : ceux du commit taggé ou ceux du jour (l'un ou l'autre, pour qu'un tag
# posé le lendemain d'une fusion de fin de mois reste admis).
for d in "$date_commit" "$date_jour"; do
  m="${d#*-}"; m="${m%%-*}"
  if [ "${d%%-*}" = "$annee" ] && [ "$((10#$m))" = "$mois" ]; then
    exit 0
  fi
done
echo "::error::Tag $tag refusé : $annee.$mois n'est ni le mois du commit ($date_commit) ni celui du jour ($date_jour)."
exit 1
