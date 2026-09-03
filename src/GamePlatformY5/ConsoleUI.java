package GamePlatformY5;

import java.util.*;

/**
 * Console based user interface for the game platform.
 * Handles all user interactions via stdin/stdout.
 */
public class ConsoleUI {

    private final Platform platform;
    private final Scanner scanner;

    /** Currently logged-in member (null = guest/invité) */
    private Member currentUser;

    public ConsoleUI(Platform platform) {
        this.platform = platform;
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
    }


    // MAIN LOOP

    /** Starts the console application. */
    public void start() {
        System.out.println("==============================================");
        System.out.println("         Bienvenue sur GamePlatform - Y5  ");
        System.out.println("==============================================");
        System.out.println("Chargement des données...");
        platform.loadGamesFromCSV();
        System.out.println();

        while (true) {
            printMainMenu();
            int choice = readInt("Votre choix : ");
            System.out.println();

            switch (choice) {
                case 1  -> handleLogin();
                case 2  -> handleLogout();
                case 3  -> handleRegister();
                case 4  -> handleUnregister();
                case 5  -> handleSearchGame();
                case 6  -> handleViewGame();
                case 7  -> handleViewMember();
                case 8  -> handleAddGameToLibrary();
                case 9  -> handleWriteEvaluation();
                case 10 -> handleTokenActions();
                case 11 -> handleTesterMenu();
                case 12 -> handleAdminMenu();
                case 0  -> { System.out.println("À la prochaine !"); return; }
                default -> System.out.println("Choix invalide.");
            }
            System.out.println();
        }
    }

    private void printMainMenu() {
        String userLabel = currentUser == null
                ? "Invité"
                : currentUser.getPseudo() + " [" + currentUser.getProfileType() + "]"
                  + " | Jetons: " + currentUser.getTokens();
        System.out.println("--- MENU PRINCIPAL | " + userLabel + " ---");
        System.out.println("  1. Se connecter");
        System.out.println("  2. Se déconnecter");
        System.out.println("  3. S'inscrire (nouveau joueur)");
        System.out.println("  4. Se désinscrire");
        System.out.println("  5. Rechercher un jeu");
        System.out.println("  6. Voir les infos d'un jeu");
        System.out.println("  7. Voir le profil d'un membre");
        if (currentUser instanceof Player) {
            System.out.println("  8. Ajouter un jeu / temps de jeu");
            System.out.println("  9. Écrire une évaluation");
            System.out.println(" 10. Placer/retirer des jetons pour un test");
        }
        if (currentUser instanceof Tester) {
            System.out.println(" 11. Menu Testeur");
        }
        if (currentUser instanceof Administrator) {
            System.out.println(" 12. Menu Administrateur");
        }
        System.out.println("  0. Quitter");
    }

    // AUTH

    private void handleLogin() {
        if (currentUser != null) {
            System.out.println("Vous êtes déjà connecté en tant que " + currentUser.getPseudo() + ".");
            return;
        }
        String pseudo = readLine("Pseudo : ");
        Member member = platform.getMember(pseudo);
        if (member == null) {
            System.out.println("Pseudo introuvable. Utilisez l'option 3 pour vous inscrire.");
        } else if (member.isBlocked()) {
            System.out.println("Ce compte est bloqué.");
        } else {
            currentUser = member;
            System.out.println("Connecté en tant que " + currentUser.getPseudo()
                    + " [" + currentUser.getProfileType() + "].");
        }
    }

    private void handleLogout() {
        if (currentUser == null) {
            System.out.println("Vous n'êtes pas connecté.");
            return;
        }
        System.out.println("Déconnexion de " + currentUser.getPseudo() + ".");
        currentUser = null;
    }

    private void handleRegister() {
        String pseudo = readLine("Pseudo souhaité : ");
        try {
            Player p = platform.registerPlayer(pseudo);
            System.out.println("Inscription réussie ! Vous avez " + p.getTokens() + " jetons.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void handleUnregister() {
        if (currentUser == null) { System.out.println("Connectez-vous d'abord."); return; }
        String pseudo = readLine("Pseudo à désinscrire (laisser vide = vous-même) : ");
        if (pseudo.isBlank()) pseudo = currentUser.getPseudo();
        try {
            platform.unregisterMember(pseudo, currentUser);
            if (pseudo.equals(currentUser.getPseudo())) currentUser = null;
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // GAME SEARCH & VIEW

    private void handleSearchGame() {
        System.out.println("Rechercher par : 1) Nom  2) Genre  3) Plateforme");
        int type = readInt("Type : ");
        String query = readLine("Recherche : ");

        List<Game> results = switch (type) {
            case 1 -> platform.searchGamesByName(query);
            case 2 -> platform.searchGamesByGenre(query);
            case 3 -> platform.searchGamesByPlatform(query);
            default -> { System.out.println("Choix invalide."); yield Collections.emptyList(); }
        };

        if (results.isEmpty()) {
            System.out.println("Aucun résultat.");
            return;
        }
        System.out.println(results.size() + " résultat(s) :");
        int shown = Math.min(results.size(), 20);
        for (int i = 0; i < shown; i++) {
            System.out.printf("  %2d. %s%n", i + 1, results.get(i));
        }
        if (results.size() > 20) System.out.println("  ... et " + (results.size() - 20) + " autres.");
    }

    private void handleViewGame() {
        String name = readLine("Nom du jeu (partiel) : ");
        List<Game> results = platform.searchGamesByName(name);
        if (results.isEmpty()) { System.out.println("Jeu introuvable."); return; }

        Game game = selectFromList(results, "Choisir un jeu");
        if (game == null) return;

        printGameInfo(game);
    }

    private void printGameInfo(Game game) {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.printf ("║  %-44s ║%n", game.getName());
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println("  Genre    : " + game.getCategory());
        System.out.println("  Éditeur  : " + game.getEditor());
        System.out.println("  Rating   : " + game.getRating());
        System.out.println("  Jetons   : " + game.getTokenVotes());
        System.out.println("  Supports disponibles :");

        for (Support s : game.getSupports()) {
            System.out.printf("    - %-8s | Année: %d | Ventes: %.2fM | " +
                              "Critiques: %.0f/100 (%d) | Joueurs: %.1f/10 (%d)%n",
                    s.getName(), s.getReleaseYear(), s.getGlobalSales(),
                    s.getCriticScore(), s.getCriticCount(),
                    s.getUserScore(), s.getUserCount());
        }

        if (game.getSupports().isEmpty()) { System.out.println("Aucun support."); return; }

        Support support = game.getSupports().size() == 1
                ? game.getSupports().get(0)
                : selectFromList(game.getSupports(), "Choisir un support pour voir les évaluations");
        if (support == null) return;

        // Show player evaluations
        List<PlayerEvaluation> evals = platform.getEvaluationsForGame(game, support);
        System.out.println();
        System.out.println("  Évaluations joueurs (" + evals.size() + ") :");
        if (evals.isEmpty()) {
            System.out.println("    Aucune évaluation.");
        } else {
            for (int i = 0; i < Math.min(evals.size(), 10); i++) {
                PlayerEvaluation e = evals.get(i);
                System.out.printf("    [%d] %s%n", i + 1, e);
                if (currentUser instanceof Player) {
                    // Player can rate
                    System.out.println("        \"" + truncate(e.getText(), 80) + "\"");
                    System.out.print("        Évaluer (1=utile / 2=inutile / 0=passer) : ");
                    int vote = readInt("");
                    if (vote == 1) { ((Player) currentUser).rateOtherEvaluation(e, true);  System.out.println("        Vote enregistré (+)."); }
                    else if (vote == 2) { ((Player) currentUser).rateOtherEvaluation(e, false); System.out.println("        Vote enregistré (-)."); }

                    // Admin can delete
                    if (currentUser instanceof Administrator) {
                        System.out.print("        [ADMIN] Supprimer cette évaluation ? (o/n) : ");
                        if (readLine("").equalsIgnoreCase("o")) {
                            ((Administrator) currentUser).deleteEvaluation(e);
                        }
                    }
                }
            }
        }

        // Show test if user is a Player+
        if (currentUser instanceof Player) {
            GameTest test = platform.getTestForGame(game, support);
            System.out.println();
            if (test == null) {
                System.out.println("  Aucun test professionnel disponible pour ce support.");
            } else {
                System.out.println("  Test professionnel de " + test.getTester().getPseudo()
                        + " (" + test.getDateTest() + ") - Score moyen: "
                        + String.format("%.1f", test.getOverallScore()) + "/10");
                System.out.println("  Version : " + test.getGameVersion());
                System.out.println("  " + truncate(test.getTestTxt(), 200));
                if (test.getGradePerCategory() != null) {
                    System.out.println("  Notes par catégorie :");
                    for (Map.Entry<String, Float> e : test.getGradePerCategory().entrySet()) {
                        System.out.printf("    %-20s : %.1f/10%n", e.getKey(), e.getValue());
                    }
                }
                if (test.getPros() != null && !test.getPros().isEmpty()) {
                    System.out.println("  Points forts : " + String.join(", ", test.getPros()));
                }
                if (test.getCons() != null && !test.getCons().isEmpty()) {
                    System.out.println("  Points faibles : " + String.join(", ", test.getCons()));
                }
            }
        }
    }


    // MEMBER VIEW

    private void handleViewMember() {
        String pseudo = readLine("Pseudo du membre : ");
        Member m = platform.getMember(pseudo);
        if (m == null) { System.out.println("Membre introuvable."); return; }

        System.out.println();
        System.out.println("=== Profil : " + m.getPseudo() + " ===");
        System.out.println("  Type    : " + m.getProfileType());
        System.out.println("  Bloqué  : " + (m.isBlocked() ? "Oui" : "Non"));
        System.out.println("  Jetons  : " + m.getTokens());

        if (m instanceof Player p) {
            System.out.println("  Jeux possédés (" + p.getPlayedGames().size() + ") :");

            // Sort by playtime descending for admin/tester, normal list for basic player view
            List<Map.Entry<Game, Float>> entries = new ArrayList<>(p.getPlayedGames().entrySet());
            if (currentUser instanceof Tester) {
                entries.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));
            }
            for (Map.Entry<Game, Float> e : entries) {
                System.out.printf("    - %-40s : %.0f h%n", e.getKey().getName(), e.getValue());
            }

            System.out.println("  Évaluations écrites : " + p.getEvaluations().size());

            if (currentUser instanceof Tester) {
                // Show vote stats on evaluations
                int totalHelpful = 0, totalNotHelpful = 0;
                for (PlayerEvaluation ev : p.getEvaluations()) {
                    totalHelpful += ev.getHelpfulVotes();
                    totalNotHelpful += ev.getNotHelpfulVotes();
                }
                System.out.println("  Votes sur ses évaluations : +" + totalHelpful + " / -" + totalNotHelpful);
            }

            if (m instanceof Tester t) {
                System.out.println("  Tests publiés : " + t.getPublishedTests().size());
            }
        }
    }


    // PLAYER ACTIONS

    private void handleAddGameToLibrary() {
        if (!(currentUser instanceof Player player)) { System.out.println("Connectez-vous en tant que joueur."); return; }

        System.out.println("1) Ajouter un jeu  2) Ajouter du temps de jeu");
        int choice = readInt("Choix : ");

        String gameName = readLine("Nom du jeu : ");
        List<Game> results = platform.searchGamesByName(gameName);
        if (results.isEmpty()) { System.out.println("Jeu introuvable."); return; }
        Game game = selectFromList(results, "Choisir un jeu");
        if (game == null) return;

        if (choice == 1) {
            player.addGame(game);
            System.out.println("'" + game.getName() + "' ajouté à votre bibliothèque.");
        } else if (choice == 2) {
            float hours = readFloat("Heures à ajouter : ");
            try {
                player.playGame(game, hours);
                System.out.println(hours + "h ajoutées pour '" + game.getName() + "'.");
            } catch (IllegalArgumentException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void handleWriteEvaluation() {
        if (!(currentUser instanceof Player player)) { System.out.println("Connectez-vous en tant que joueur."); return; }

        String gameName = readLine("Nom du jeu à évaluer : ");
        List<Game> results = platform.searchGamesByName(gameName);
        if (results.isEmpty()) { System.out.println("Jeu introuvable."); return; }
        Game game = selectFromList(results, "Choisir un jeu");
        if (game == null) return;

        if (game.getSupports().isEmpty()) { System.out.println("Aucun support disponible."); return; }
        Support support = selectFromList(game.getSupports(), "Choisir un support");
        if (support == null) return;

        String text    = readLine("Texte de l'évaluation : ");
        String version = readLine("Version/build du jeu : ");
        float  score   = readFloat("Note globale (0-10) : ");

        try {
            PlayerEvaluation eval = player.writeEvaluation(game, support, text, version, score);
            platform.registerEvaluation(eval);
            System.out.println("Évaluation publiée !");
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void handleTokenActions() {
        if (!(currentUser instanceof Player player)) { System.out.println("Connectez-vous en tant que joueur."); return; }

        System.out.println("1) Placer des jetons  2) Retirer des jetons");
        int choice = readInt("Choix : ");

        String gameName = readLine("Nom du jeu : ");
        List<Game> results = platform.searchGamesByName(gameName);
        if (results.isEmpty()) { System.out.println("Jeu introuvable."); return; }
        Game game = selectFromList(results, "Choisir un jeu");
        if (game == null) return;

        int amount = readInt("Nombre de jetons : ");
        try {
            if (choice == 1) player.voteForGameTest(game, amount);
            else if (choice == 2) player.withdrawTokensFromGame(game, amount);
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }


    // TESTER MENU
    private void handleTesterMenu() {
        if (!(currentUser instanceof Tester tester)) { System.out.println("Accès réservé aux testeurs."); return; }

        System.out.println("--- Menu Testeur ---");
        System.out.println("  1. Voir les jeux les plus demandés (jetons)");
        System.out.println("  2. Écrire un test pour un jeu");
        System.out.println("  3. Signaler une évaluation");
        int choice = readInt("Choix : ");

        switch (choice) {
            case 1 -> {
                List<Game> byTokens = platform.getGamesByTokenVotes();
                System.out.println("Jeux par nombre de jetons (top 20) :");
                int shown = 0;
                for (Game g : byTokens) {
                    if (g.getTokenVotes() == 0) break;
                    if (shown >= 20) break;
                    System.out.printf("  %2d. %-40s | Jetons: %d%n", ++shown, g.getName(), g.getTokenVotes());
                }
                if (shown == 0) System.out.println("Aucun jeu avec des jetons placés.");
            }
            case 2 -> handleWriteTest(tester);
            case 3 -> handleFlagEvaluation(tester);
            default -> System.out.println("Choix invalide.");
        }
    }

    private void handleWriteTest(Tester tester) {
        String gameName = readLine("Nom du jeu à tester : ");
        List<Game> results = platform.searchGamesByName(gameName);
        if (results.isEmpty()) { System.out.println("Jeu introuvable."); return; }
        Game game = selectFromList(results, "Choisir un jeu");
        if (game == null) return;

        if (game.getSupports().isEmpty()) { System.out.println("Aucun support disponible."); return; }
        Support support = selectFromList(game.getSupports(), "Choisir un support");
        if (support == null) return;

        String text    = readLine("Texte du test : ");
        String version = readLine("Version/build du jeu : ");

        Map<String, Float> grades = new LinkedHashMap<>();
        System.out.println("Entrez les notes par catégorie (laisser vide pour finir) :");
        while (true) {
            String cat = readLine("  Catégorie (ex: gameplay, interface, optimisation) : ");
            if (cat.isBlank()) break;
            float note = readFloat("  Note pour '" + cat + "' (0-10) : ");
            grades.put(cat, note);
        }

        try {
            GameTest test = tester.writeTest(game, support, text, version, grades);
            platform.registerTest(test);
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void handleFlagEvaluation(Tester tester) {
        String gameName = readLine("Nom du jeu : ");
        List<Game> results = platform.searchGamesByName(gameName);
        if (results.isEmpty()) { System.out.println("Jeu introuvable."); return; }
        Game game = selectFromList(results, "Choisir un jeu");
        if (game == null) return;

        if (game.getSupports().isEmpty()) return;
        Support support = selectFromList(game.getSupports(), "Choisir un support");
        if (support == null) return;

        List<PlayerEvaluation> evals = platform.getEvaluationsForGame(game, support);
        if (evals.isEmpty()) { System.out.println("Aucune évaluation."); return; }

        for (int i = 0; i < evals.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, evals.get(i));
        }
        int idx = readInt("Numéro de l'évaluation à signaler : ") - 1;
        if (idx < 0 || idx >= evals.size()) { System.out.println("Numéro invalide."); return; }
        tester.flagEvaluation(evals.get(idx));
    }

    // ADMIN MENU

    private void handleAdminMenu() {
        if (!(currentUser instanceof Administrator admin)) { System.out.println("Accès réservé aux administrateurs."); return; }

        System.out.println("--- Menu Administrateur ---");
        System.out.println("  1. Promouvoir un membre");
        System.out.println("  2. Bloquer un membre");
        System.out.println("  3. Débloquer un membre");
        int choice = readInt("Choix : ");

        String pseudo = readLine("Pseudo du membre : ");
        Member target = platform.getMember(pseudo);
        if (target == null) { System.out.println("Membre introuvable."); return; }

        switch (choice) {
            case 1 -> {
                try { platform.promoteMember(pseudo, admin); }
                catch (Exception e) { System.out.println("Erreur : " + e.getMessage()); }
            }
            case 2 -> admin.blockMember(target);
            case 3 -> admin.unblockMember(target);
            default -> System.out.println("Choix invalide.");
        }
    }

    // HELPERS

    private String readLine(String prompt) {
        if (!prompt.isBlank()) System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                if (!prompt.isBlank()) System.out.print(prompt);
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Entrez un nombre entier.");
            }
        }
    }

    private float readFloat(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Float.parseFloat(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrez un nombre décimal.");
            }
        }
    }

    private <T> T selectFromList(List<T> list, String label) {
        if (list.size() == 1) return list.get(0);
        System.out.println(label + " :");
        for (int i = 0; i < list.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + list.get(i));
        }
        int idx = readInt("Numéro : ") - 1;
        if (idx < 0 || idx >= list.size()) { System.out.println("Choix invalide."); return null; }
        return list.get(idx);
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
