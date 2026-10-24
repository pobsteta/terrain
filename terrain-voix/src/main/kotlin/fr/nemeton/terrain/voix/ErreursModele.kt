package fr.nemeton.terrain.voix

/**
 * Erreurs typées de l'installation du modèle Vosk (spec de `terrain-voix`, ADR 0005), sur le
 * modèle de `SystemeNonGere` : elles sont portées par le `Result` d'[InstallationModele] (ou
 * levées par [Empreinte.Sha256]), l'application les traduit en messages, les tests vérifient le
 * type et les champs.
 *
 * Les erreurs d'entrée-sortie hors de cette liste (disque plein en cours d'écriture, droits,
 * renommage refusé) remontent telles quelles (`java.io.IOException`) : le modèle en place reste
 * garanti.
 */
sealed class ErreurModele(message: String) : Exception(message)

/** [valeur] n'est pas une empreinte SHA-256 : 64 caractères hexadécimaux exactement (FIPS 180-4). */
class EmpreinteMalFormee(val valeur: String) :
    ErreurModele("Empreinte SHA-256 mal formée (64 caractères hexadécimaux attendus) : « $valeur »")

/** L'archive n'a pas l'empreinte [attendue] ; [obtenue] est la sienne. Les deux en minuscules. */
class EmpreinteInattendue(val attendue: String, val obtenue: String) :
    ErreurModele("Empreinte SHA-256 inattendue : attendue $attendue, obtenue $obtenue")

/**
 * L'archive ne se lit pas ou ne se décompresse pas sans risque : pas un zip, zip tronqué, CRC
 * faux, entrée hors du dossier ou à segment `..`, nom absolu, entrée en double, taille inconnue
 * ou incohérente, plusieurs modèles ; ou le réseau a manqué (raison « réseau : … »).
 */
class ArchiveIllisible(val raison: String) : ErreurModele("Archive du modèle illisible : $raison")

/** Il manque au modèle un fichier obligatoire ; [manquant] est le premier, en chemin relatif. */
class StructureInvalide(val manquant: String) : ErreurModele("Modèle incomplet : $manquant manquant")

/**
 * Le modèle contient `graph/HCLG.fst` : Vosk le charge dès qu'il existe et n'y applique pas de
 * grammaire (« Runtime graphs are not supported by this model »), or terrain ne sert que la
 * grammaire fermée.
 */
class SansGrammaireDynamique :
    ErreurModele("Modèle sans grammaire dynamique (graph/HCLG.fst) : la grammaire fermée ne s'y applique pas")

/** Il manque [manquant] octets libres dans le dossier parent du modèle (marge comprise). */
class EspaceInsuffisant(val manquant: Long) :
    ErreurModele("Espace insuffisant pour le modèle : il manque $manquant octets")

/** L'opération a été annulée (`continuer` est devenu faux). */
class Interrompu : ErreurModele("Installation du modèle interrompue")
