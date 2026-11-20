#!/usr/bin/env bash
# Test de scripts/verifier-tag.sh (lancé par `./gradlew test`).
set -uo pipefail
cd "$(dirname "$0")"
echec=0
verifie() { # attendu(0|1) tag [date-commit date-jour]
  local attendu="$1"; shift
  ./verifier-tag.sh "$@" >/dev/null 2>&1; local code=$?
  if [ "$code" -ne "$attendu" ]; then echo "ÉCHEC : verifier-tag.sh $* → $code (attendu $attendu)"; echec=1; fi
}
D=2026-11-20
verifie 0 v2026.11.1 $D $D
verifie 0 v2026.11.12 $D $D
verifie 0 v2026.1.1 2026-01-05 2026-01-05
verifie 0 v2026.12.3 2026-12-31 2026-12-31
verifie 0 v2026.10.2 2026-10-31 2026-11-01   # mois du commit
verifie 0 v2026.11.2 2026-10-31 2026-11-01   # mois du jour
verifie 1 v0.8.0 $D $D
verifie 1 v0.7.1 $D $D
verifie 1 v2026.01.1 2026-01-05 2026-01-05   # zéro en tête
verifie 1 v2026.13.1 $D $D
verifie 1 v2026.0.1 $D $D
verifie 1 v2026.11.0 $D $D
verifie 1 v2026.11.01 $D $D
verifie 1 v2026.11 $D $D
verifie 1 2026.11.1 $D $D
verifie 1 v2026.11.1-rc1 $D $D
verifie 1 v2026.12.1 $D $D                   # ni le mois du commit ni celui du jour
verifie 1 v2025.11.1 $D $D
[ "$echec" -eq 0 ] && echo "verifier-tag : tous les cas passent"
exit "$echec"
