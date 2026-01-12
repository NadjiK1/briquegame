# Cassebrique

Projet [libGDX](https://libgdx.com/) généré avec [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

Ce projet a été créé à partir d’un modèle incluant des lanceurs simples pour l’application ainsi qu’une extension de `ApplicationAdapter` qui affiche le logo libGDX.

IMPORTANT ::
Alors Nous avons Malheuresement pas compris l'utilités au départ de github donc nous avions tout fait sur ma machine a la maison car Benguesmia-chadly Mehdi n'en possédait pas une machine et s'en est achetés que récemment pour noel donc les commits ne sont pas fiables par rapport au travaille effectués.Merci et désoler du dérangement occasionée.


## Plateformes

### core
Module principal qui contient la logique de l’application.  
Ce code est partagé par toutes les plateformes.

### lwjgl3
Plateforme principale pour ordinateur (desktop) utilisant **LWJGL3**.  
Dans les anciennes documentations, cette plateforme était appelée **desktop**.

## Gradle

Ce projet utilise [Gradle](https://gradle.org/) pour gérer les dépendances.  
Le **Gradle Wrapper** est inclus, ce qui permet d’exécuter les commandes Gradle avec `gradlew.bat` (Windows) ou `./gradlew` (Linux / macOS).

### Commandes et options Gradle utiles

- `--continue` : permet de continuer l’exécution des tâches même en cas d’erreur.
- `--daemon` : utilise le démon Gradle pour accélérer l’exécution des tâches.
- `--offline` : utilise uniquement les dépendances déjà téléchargées.
- `--refresh-dependencies` : force la mise à jour de toutes les dépendances (utile pour les versions snapshot).
- `build` : compile les sources et génère les fichiers pour tous les projets.
- `cleanEclipse` : supprime les fichiers de configuration Eclipse.
- `cleanIdea` : supprime les fichiers de configuration IntelliJ IDEA.
- `clean` : supprime les dossiers `build` contenant les fichiers compilés.
- `eclipse` : génère les fichiers de projet pour Eclipse.
- `idea` : génère les fichiers de projet pour IntelliJ IDEA.
- `lwjgl3:jar` : crée un fichier JAR exécutable dans `lwjgl3/build/libs`.
- `lwjgl3:run` : lance l’application.
- `test` : exécute les tests unitaires (s’il y en a).

La plupart des tâches qui ne sont pas spécifiques à un seul module peuvent être exécutées en ajoutant le préfixe `nomDuProjet:`.  
Par exemple, `core:clean` supprime uniquement le dossier `build` du module **core**.


