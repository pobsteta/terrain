# Journal de terrain

> **Règle du sommier : on n'efface pas, on ajoute.**
> Chaque inscription est datée et signée (agent ou personne). Une correction est une
> nouvelle ligne qui renvoie à l'ancienne. Un hook refuse toute modification qui
> supprimerait ou réécrirait une inscription existante.
>
> Format : `AAAA-MM-JJ · auteur · mission · ce qui a été fait / décidé / bloqué`
>
> **Le carnet des écarts** (fiche n° 3), en une ligne marquée `écart` :
> `AAAA-MM-JJ · auteur · mission · écart · attendu : … · vu : … · vérifié : comment (ou « non ») · décision : …`
> La décision est l'une des trois : **garder** (vérifié : on adopte), **creuser** (prochain
> essai, daté), **abandonner** (vrai hasard : on le note).
> Règle : on ne sanctionne pas un écart signalé ; on regarde d'abord, on corrige ensuite,
> s'il le faut.
>
> **Les changements de cap** (fiche n° 13), en une ligne marquée `cap`, signée par qui l'a
> décidé : `AAAA-MM-JJ · auteur · mission · cap · ancien → nouveau · pourquoi : … · fait : …`

2026-10-23 · Claude (orchestrateur) · — · méthode installée (plugin imperatrice 0.3.0) : CLAUDE.md, docs/brief.md, docs/adr/, pilotage/ (équipe, kanban, journal, missions). Reste à remplir : le brief (Pascal), puis /imperatrice:brief, la spec, .claude/test-command (pressenti : ./gradlew test) et pilotage/equipe.md (rythme de relecture, validation).
2026-10-23 · Claude (orchestrateur) · — · demande entrante : issue #1 (Surculus, module terrain-style, jetons du style commun, ADR 0008 de Surculus) reportée dans docs/brief.md avec son lien ; « Ma réponse d'abord » et le coût complet laissés à Pascal. Prochaine étape : /imperatrice:brief, en commençant par le point à vérifier (7:1 de primary/secondary/tertiary contre surfaceDim et surfaceContainerHighest).
2026-10-23 · Pascal (recopié par Claude, orchestrateur) · terrain-style · « Ma réponse d'abord » inscrite dans docs/brief.md avant toute critique. Coût complet : non renseigné. Contradicteur lancé sur le brief.
2026-10-23 · Pascal (recopié par Claude, orchestrateur) · terrain-style · règle : un coût complet non renseigné s'écrit « inconnu » ; docs/brief.md mis à jour (renvoie à l'inscription « Coût complet : non renseigné »).
2026-10-23 · contradicteur · terrain-style · critique du brief rendue : 3 bloquants (niveau 0,5 ≠ « grand contraste » ; 7:1 WCAG n'est pas un critère soleil ; variante de schéma absente, TONAL_SPOT écarte la chroma de VertMarculus), 4 importants (« À vérifier » levé par construction mais primary plafonne à 7,0 avec tolérance MCU 0,04 ; table des couples à écrire, repli « moins de couples » à retirer ; cohérence non garantie : ColorScheme recopié, dynamicColor, palette d'état ; thème sombre à trancher), 4 mineurs (SPEC_2021/2025/2026 et sources Java, constante 0,04045, JitPack par SHA et CI sur tags, successeur). Décisions posées à Pascal.
2026-10-23 · Pascal (recopié par Claude, orchestrateur) · terrain-style · arbitrages sur la critique : contraste 1,0 seul ; variante FIDELITY ; clair par défaut, sombre fourni ; retenus : 7:1 = plancher + essai soleil, couleurs sémantiques communes ; non retenus : table des couples / retrait du repli, garanties de cohérence. Reportés dans docs/brief.md.
2026-10-23 · Claude (orchestrateur) · terrain-style · docs/spec.md rédigée (proposée) ; ADR 0001 (jetons générés une fois, sources Java MCU, SPEC_2021), 0002 (couleurs sémantiques), 0003 (publication et épinglage) proposées ; .claude/test-command : ./gradlew test (stack Gradle/Kotlin déjà en place). Contradicteur lancé sur la spec et les trois ADR.
2026-10-23 · contradicteur · terrain-style · critique de la spec et des ADR 0001-0003 rendue : bloquants : couleurs sémantiques « au contraste 1,0 » sans mécanisme MCU (customColor à tons fixes ; errorPalette de DynamicScheme proposé), option « sources Kotlin officielles de MCU » oubliée dans l'ADR 0001, SPEC_2021 imposée par FIDELITY (maybeFallbackSpecVersion), ADR 0002 contradictoire (erreur = error ou harmonisée) et jaune « alerte » méconnaissable au niveau 1,0 ; importants : rôles à définir par ColorScheme M3, couples manquants (surfaceBright en sombre, inversePrimary/inverseSurface, onXFixedVariant), CSV du script contre référence tierce, rejeu à l'octet sur des doubles, règle de tag non contrôlée, valeurs sacrifiées manquantes. Décisions posées à Pascal.
2026-10-23 · Pascal (recopié par Claude, orchestrateur) · terrain-style · arbitrages sur la critique de la spec et des ADR : erreur = error de Material ; alerte = jaune clair + noir (convention de sécurité), jaune interdit en texte ; repli : rôle en échec documenté « interdit en texte » (remplace le repli de l'issue #1) ; corrections techniques 1 à 6 appliquées. Brief, spec et ADR 0001-0003 mis à jour.
