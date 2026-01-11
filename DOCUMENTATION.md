# Documentation et Structure du Jeu Casse-Brique

## 1. Vue d'ensemble du Projet
Ce projet est un jeu de type "Casse-Brique" (Breakout) développé en Java avec le framework **LibGDX**. Il utilise une architecture modulaire séparant les données (Model), l'affichage (View/Screen) et le contrôle.

## 2. Structure des Dossiers
L'architecture suit le standard LibGDX :

```
briquegame/
├── core/                # COEUR DU JEU (Code commun multiplateforme)
│   └── src/main/java/com/laymoo/cassebrique/
│       ├── config/      # Configuration globale (Constantes)
│       ├── model/       # Modèle de données (Logique pure sans rendu)
│       ├── screen/      # Écrans de jeu (Menu, Jeu, Game Over)
│       └── CasseBriqueMain.java  # Classe principale du jeu
├── lwjgl3/              # Lanceur Desktop (PC)
└── assets/              # Ressources graphiques et niveaux
    └── maps/            # Cartes Tiled (.tmx)
```

## 3. Détails des Composants Techniques

### A. Point d'Entrée : `CasseBriqueMain`
*   **Chemin** : `core/.../CasseBriqueMain.java`
*   **Description** : C'est la classe racine qui hérite de `Game`.
*   **Responsabilités** :
    *   Initialiser le moteur graphique (`SpriteBatch`).
    *   Lancer le premier écran (`MenuScreen`).
    *   Gérer la méthode `dispose()` pour nettoyer la mémoire à la fermeture.

### B. Configuration : `GameConfig`
*   **Chemin** : `core/.../config/GameConfig.java`
*   **Description** : Fichier centralisant toutes les constantes du jeu.
*   **Paramètres Clés** :
    *   `WORLD_WIDTH` / `WORLD_HEIGHT` : Dimensions de l'aire de jeu (800x600).
    *   `PADDLE_WIDTH` : Taille de la raquette.
    *   `INITIAL_LIVES` : Nombre de vies au départ (3).

### C. Le Cœur du Jeu : `GameScreen`
*   **Chemin** : `core/.../screen/GameScreen.java`
*   **Description** : Contient la boucle principale du jeu (`show`, `render`, `dispose`).
*   **Logique Implémentée** :
    1.  **Initialisation (`show`)** : Charge la caméra, les sons, et le niveau via `TiledMap`.
    2.  **Mise à jour (`update`)** :
        *   Gère les entrées joueur (Clavier/Souris).
        *   Déplace la balle et la raquette.
        *   **Physique** : Vérifie les collisions (Balle vs Brique, Balle vs Raquette).
        *   **Logique de Jeu** : Vérifie si le joueur a perdu (balle en bas) ou gagné (plus de briques).
    3.  **Affichage (`draw`)** : Utilise `ShapeRenderer` pour dessiner les formes géométriques.

### D. Gestion des Données : `GameState`
*   **Chemin** : `core/.../model/GameState.java`
*   **Description** : Gère l'état courant de la partie indépendamment de l'affichage.
*   **Données** :
    *   Score du joueur.
    *   Nombre de vies restantes.
    *   Méthodes pour ajouter des points ou retirer une vie.

## 4. Fonctionnalités Actuelles

| Module | Statut | Description |
| :--- | :---: | :--- |
| **Moteur Physique** | ✅ | Rebonds sur les murs, la raquette et destruction des briques. |
| **Système de Niveaux**| ✅ | Chargement dynamique de fichiers `.tmx` (Tiled Map Editor). |
| **Contrôles** | ✅ | Support hybride Clavier (Flèches) et Souris. |
| **Cycle de Vie** | ✅ | Menu -> Jeu -> Game Over -> Menu. |
| **Interface (HUD)** | ✅ | Affichage du Score et des Vies en temps réel. |

## 5. Comment exporter ce fichier ?
Ce fichier est au format **Markdown (.md)**. Pour l'avoir en PDF ou Word :
1.  **PDF** : Dans VS Code, faites `Clic Droit` dans ce fichier -> `Markdown to PDF` (si vous avez une extension installée) ou copiez le texte dans un convertisseur en ligne.
2.  **Word** : Copiez-collez simplement le contenu (le rendu visuel sera conservé) dans Microsoft Word ou Google Docs.
