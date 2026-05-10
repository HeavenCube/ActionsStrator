# ActionsStrator

ActionsStrator est un plugin Paper pour Minecraft permettant de contrôler un serveur via l'API MineStrator.
Il fournit des commandes simples (restart / stop / kill) pour envoyer des actions d'alimentation (power actions) à votre serveur géré par MineStrator.

## Caractéristiques
- Envoyer des signaux de redémarrage, arrêt ou kill au serveur via l'API MineStrator
- Messages en jeu formatés (MiniMessage)
- Configuration minimale (clé API + server ID)

## Installation
### Installation simple :
Téléchargez directement la dernière version du plugin ici : https://github.com/HeavenCube/ActionsStrator/actions
### Installation avancée :
1. Construisez le plugin avec Gradle :
```shell
./gradlew build
```
2. Copiez le fichier JAR généré (dans `build/libs/`) dans le dossier `plugins/` de votre serveur Paper.
3. Démarrez le serveur pour générer le fichier de configuration par défaut (`config.yml`).

## Configuration
Après le premier démarrage, éditez `plugins/ActionsStrator/config.yml` et renseignez :
- `api_key`: votre clé API MineStrator (générée depuis votre panneau MineStrator)
- `server_id`: l'identifiant de votre serveur (visible dans l'URL de votre dashboard MineStrator)

### Configuration :
```yaml
# ActionsStrator Configuration File
# -----------------------------------------------------------------------------------------------------------

# MineStrator API Key
# You can generate one on your MineStrator panel
# 🔗 Link: https://minestrator.com/my/account?section=api
api_key: "YOUR_API_KEY_HERE"

# Server ID
# The ID of the server you want to control (you can find this in your server URL)
# 🔗 Link: https://minestrator.com/my/dashboard
server_id: "YOUR_SERVER_ID_HERE"

# -----------------------------------------------------------------------------------------------------------

```

Conseils :
- Assurez-vous que la clé API a les droits nécessaires sur le panel MineStrator.
- Vérifiez le `server_id` (souvent présent dans l'URL de la page de votre serveur).

## Commandes
Les commandes exposées par le plugin :

- `/msrestart` — Envoie un signal de redémarrage
	- Permission : `actionsstrator.restart` (par défaut : `op`)
- `/msstop` — Envoie un signal d'arrêt
	- Permission : `actionsstrator.stop` (par défaut : `op`)
- `/mskill` — Envoie un signal "kill"
	- Permission : `actionsstrator.kill` (par défaut : op)

Les commandes renvoient des messages en jeu pour indiquer l'envoi et le résultat (succès/échec). Les actions sont exécutées de façon asynchrone via l'API HTTP de MineStrator.

## Permissions
Les permissions sont définies dans `paper-plugin.yml`. Par défaut, elles sont configurées pour les opérateurs (`op`). Vous pouvez les personnaliser selon vos besoins.

Permissions disponibles :
- `actionsstrator.restart`
- `actionsstrator.stop`
- `actionsstrator.kill`

## Journalisation et dépannage
- Les erreurs et réponses HTTP sont enregistrées dans la console du serveur (logger du plugin). Si une action échoue, consultez la console pour le code HTTP et la réponse complète.
- Vérifiez que `api_key` et `server_id` dans `config.yml` ne contiennent pas les valeurs par défaut (`YOUR_API_KEY_HERE`, `YOUR_SERVER_ID_HERE`).
- Assurez-vous que le serveur a un accès sortant vers `https://mine.sttr.io/`.

## Développement

### Ajouter une nouvelle action

Le plugin est volontairement structuré de façon simple : **chaque action possède son propre fichier Java**.

Pour ajouter une nouvelle action :

1. Créez une nouvelle classe dans `src/main/java/fr/heavencube/actionsstrator/commands/`
2. Faites-la étendre `Command`
3. Ajoutez votre logique dans `execute(...)`
4. Ajoutez les messages nécessaires dans `Messages.java`
5. Déclarez la permission correspondante dans `paper-plugin.yml`

Exemple : `/msbackup` peut être ajouté avec son propre fichier `BackupCommand.java`.

### Architecture

Le plugin privilégie la lisibilité et la simplicité :
- une classe par commande
- des messages clairs et centralisés dans `Messages.java`
- des appels API asynchrones via `CompletableFuture`

### Build optimisé

Le build a été optimisé pour réduire la taille du JAR :
- ✓ Suppression de shadowJar (inutile)
- ✓ Dépendances fournies par Paper API
- ✓ JAR réduit de ~70-80%

## Crédits
- Plugin développé pour interagir avec l'API MineStrator

---
Pour plus d'informations sur MineStrator : https://minestrator.com/