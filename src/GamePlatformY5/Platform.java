package GamePlatformY5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central class managing the platform's data: members, games, evaluations, and tests.
 * Loads static game data from the CSV source URL.
 */
public class Platform {

    /** URL of the CSV data source */
    private static final String CSV_URL =
            "https://raw.githubusercontent.com/charlesbrantstec/VGSalesRatings/" +
            "28980b2078f851b30d449186a45cb5127d81ea60/VG/output_csv/vg_data.csv";

    /** All registered members (keyed by pseudo) */
    private Map<String, Member> members;

    /** All games loaded from the CSV (keyed by name+platform composite) */
    private List<Game> games;

    /** All player evaluations on the platform */
    private List<PlayerEvaluation> allEvaluations;

    /** All published game tests on the platform */
    private List<GameTest> allTests;

    /**
     * Constructs and initialises the platform.
     * Creates the default admin account and loads game data from CSV.
     */
    public Platform() {
        this.members = new HashMap<>();
        this.games = new ArrayList<>();
        this.allEvaluations = new ArrayList<>();
        this.allTests = new ArrayList<>();

        // Create default admin account
        Administrator admin = new Administrator("admin");
        members.put("admin", admin);
    }


    // CSV LOADING
    /**
     * Loads game data from the remote CSV source.
     * Merges entries with the same game name into one Game object with multiple supports.
     *
     * @return number of entries loaded
     */
    public int loadGamesFromCSV() {
        // CSV format: index,Name,Platform,Year,Genre,Publisher,
        //             NA_Sales,EU_Sales,JP_Sales,Other_Sales,Global_Sales,
        //             Critic_Score,Critic_Count,User_Score,User_Count,Developer,Rating
        Map<String, Game> gameMap = new HashMap<>();
        int count = 0;

        try {
            URL url = new URL(CSV_URL);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                String[] parts = line.split(",", -1);
                if (parts.length < 17) continue;

                try {
                    String name      = parts[1].trim();
                    String platform  = parts[2].trim();
                    int    year      = parts[3].trim().isEmpty() ? 0 : (int) Double.parseDouble(parts[3].trim());
                    String genre     = parts[4].trim();
                    String publisher = parts[5].trim();
                    float  global    = parseFloat(parts[10]);
                    float  critic    = parseFloat(parts[11]);
                    int    criticCnt = parseInt(parts[12]);
                    float  user      = parseFloat(parts[13]);
                    int    userCnt   = parseInt(parts[14]);
                    String developer = parts[15].trim();
                    String rating    = parts[16].trim();

                    // Merge by name
                    Game game = gameMap.get(name);
                    if (game == null) {
                        game = new Game(name, genre, publisher, rating);
                        gameMap.put(name, game);
                    }

                    Support support = new Support(platform, year, developer,
                            global, critic, criticCnt, user, userCnt);
                    game.addSupport(support);
                    count++;
                } catch (NumberFormatException ignored) {}
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du CSV : " + e.getMessage());
        }

        games.addAll(gameMap.values());
        System.out.println(games.size() + " jeux chargés (" + count + " entrées CSV).");
        return count;
    }

    private float parseFloat(String s) {
        try { return s == null || s.trim().isEmpty() ? 0 : Float.parseFloat(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private int parseInt(String s) {
        try { return s == null || s.trim().isEmpty() ? 0 : (int) Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    // MEMBER MANAGEMENT

    /**
     * Registers a new player with the given pseudo.
     *
     * @param pseudo the desired pseudo
     * @return the newly created Player
     * @throws IllegalArgumentException if the pseudo is already taken
     */
    public Player registerPlayer(String pseudo) {
        if (members.containsKey(pseudo)) {
            throw new IllegalArgumentException("Le pseudo '" + pseudo + "' est déjà pris.");
        }
        Player player = new Player(pseudo, 3); // initial 3 tokens
        members.put(pseudo, player);
        System.out.println("Joueur '" + pseudo + "' inscrit avec succès.");
        return player;
    }

    /**
     * Removes a member from the platform.
     * Can be done by the member themselves or an administrator.
     *
     * @param pseudo    pseudo of the member to remove
     * @param requester the member initiating the removal
     * @throws IllegalArgumentException if the member doesn't exist
     * @throws SecurityException        if the requester is not authorised
     */
    public void unregisterMember(String pseudo, Member requester) {
        Member target = getMember(pseudo);
        if (target == null) throw new IllegalArgumentException("Membre introuvable.");
        if (!requester.getPseudo().equals(pseudo) && !(requester instanceof Administrator)) {
            throw new SecurityException("Vous n'avez pas la permission de désinscrire cet utilisateur.");
        }
        members.remove(pseudo);
        System.out.println("Membre '" + pseudo + "' désinscrit.");
    }

    /**
     * Promotes a Player to Tester, or a Tester to Administrator.
     * Only an Administrator can promote members.
     *
     * @param pseudo    pseudo of the member to promote
     * @param admin     the administrator performing the promotion
     * @return the promoted Member
     * @throws SecurityException        if requester is not an Administrator
     * @throws IllegalArgumentException if member not found or already at max level
     */
    public Member promoteMember(String pseudo, Administrator admin) {
        Member target = getMember(pseudo);
        if (target == null) throw new IllegalArgumentException("Membre introuvable.");

        Member promoted;
        if (target instanceof Administrator) {
            throw new IllegalArgumentException("Ce membre est déjà administrateur.");
        } else if (target instanceof Tester) {
            // Promote Tester -> Administrator
            Administrator newAdmin = new Administrator(pseudo);
            copyPlayerData((Player) target, newAdmin);
            promoted = newAdmin;
        } else if (target instanceof Player) {
            // Promote Player -> Tester
            Tester newTester = new Tester(pseudo, target.getTokens());
            copyPlayerData((Player) target, newTester);
            promoted = newTester;
        } else {
            throw new IllegalArgumentException("Profil inconnu.");
        }

        members.put(pseudo, promoted);
        System.out.println("'" + pseudo + "' promu au rang de " + promoted.getProfileType() + ".");
        return promoted;
    }

    /** Copies player game/eval data when promoting */
    private void copyPlayerData(Player source, Player dest) {
        dest.playedGames.putAll(source.playedGames);
        dest.evaluations.addAll(source.evaluations);
        dest.tokenPlacedOnGame.putAll(source.tokenPlacedOnGame);
        dest.tokens = source.tokens;
    }

    /**
     * Looks up a member by pseudo.
     *
     * @param pseudo the pseudo to look up
     * @return the Member, or null if not found
     */
    public Member getMember(String pseudo) {
        return members.get(pseudo);
    }

    // GAME SEARCH

    /**
     * Searches games by name (case-insensitive partial match).
     *
     * @param query the search string
     * @return list of matching games
     */
    public List<Game> searchGamesByName(String query) {
        List<Game> results = new ArrayList<>();
        String lower = query.toLowerCase();
        for (Game g : games) {
            if (g.getName().toLowerCase().contains(lower)) results.add(g);
        }
        return results;
    }

    /**
     * Searches games by genre/category (case-insensitive).
     *
     * @param genre the genre to filter by
     * @return list of matching games
     */
    public List<Game> searchGamesByGenre(String genre) {
        List<Game> results = new ArrayList<>();
        String lower = genre.toLowerCase();
        for (Game g : games) {
            if (g.getCategory().toLowerCase().contains(lower)) results.add(g);
        }
        return results;
    }

    /**
     * Searches games by platform name.
     *
     * @param platform the platform to filter by
     * @return list of games available on that platform
     */
    public List<Game> searchGamesByPlatform(String platform) {
        List<Game> results = new ArrayList<>();
        String lower = platform.toLowerCase();
        for (Game g : games) {
            for (Support s : g.getSupports()) {
                if (s.getName().toLowerCase().equals(lower)) {
                    results.add(g);
                    break;
                }
            }
        }
        return results;
    }

    /**
     * Returns games sorted by descending token votes (for testers looking to review).
     *
     * @return sorted list of all games
     */
    public List<Game> getGamesByTokenVotes() {
        List<Game> sorted = new ArrayList<>(games);
        sorted.sort((a, b) -> b.getTokenVotes() - a.getTokenVotes());
        return sorted;
    }

    // EVALUATIONS & TESTS

    /**
     * Registers a player evaluation on the platform.
     *
     * @param eval the evaluation to register
     */
    public void registerEvaluation(PlayerEvaluation eval) {
        allEvaluations.add(eval);
    }

    /**
     * Registers a game test on the platform.
     *
     * @param test the test to register
     */
    public void registerTest(GameTest test) {
        allTests.add(test);
    }

    /**
     * Returns all non-deleted evaluations for a given game and support,
     * sorted by score descending then date ascending.
     *
     * @param game    the game
     * @param support the platform
     * @return sorted list of evaluations
     */
    public List<PlayerEvaluation> getEvaluationsForGame(Game game, Support support) {
        List<PlayerEvaluation> result = new ArrayList<>();
        for (PlayerEvaluation e : allEvaluations) {
            if (!e.isDeleted() && e.getGame().equals(game) && e.getSupport().equals(support)) {
                result.add(e);
            }
        }
        // Sort: best score first, then oldest first
        result.sort((a, b) -> {
            int cmp = Float.compare(b.getGlobalScore(), a.getGlobalScore());
            if (cmp != 0) return cmp;
            return a.getDate().compareTo(b.getDate());
        });
        return result;
    }

    /**
     * Returns the GameTest for a given game and support, if it exists.
     *
     * @param game    the game
     * @param support the platform
     * @return the GameTest, or null
     */
    public GameTest getTestForGame(Game game, Support support) {
        for (GameTest t : allTests) {
            if (t.getTestedGame().equals(game) && t.getSupport().equals(support)) return t;
        }
        return null;
    }

    // GETTERS

    /** @return all registered members */
    public Map<String, Member> getMembers() { return members; }

    /** @return all games */
    public List<Game> getGames() { return games; }

    /** @return all evaluations */
    public List<PlayerEvaluation> getAllEvaluations() { return allEvaluations; }

    /** @return all tests */
    public List<GameTest> getAllTests() { return allTests; }
}
