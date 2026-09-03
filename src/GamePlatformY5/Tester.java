package GamePlatformY5;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a Tester on the platform.
 * Testers have all Player privileges plus the ability to write structured GameTests
 * and flag problematic evaluations.
 * Testers earn 5 tokens per published test.
 */
public class Tester extends Player {

    /** List of structured tests written by this tester */
    private List<GameTest> publishedTests;

    /** Tokens earned per published test */
    public static final int TOKENS_PER_TEST = 5;

    /** Minimum playtime required to write a test (in hours) */
    public static final float MIN_PLAYTIME_FOR_TEST = 5.0f;

    /**
     * Constructs a Tester with the given pseudo and initial token balance.
     *
     * @param pseudo        unique username
     * @param initialTokens starting token count
     */
    public Tester(String pseudo, int initialTokens) {
        super(pseudo, initialTokens);
        this.publishedTests = new ArrayList<>();
    }

    /**
     * Writes and publishes a structured GameTest for a game on a specific support.
     * Releases all player token votes placed on that game.
     * Awards the tester TOKENS_PER_TEST tokens.
     *
     * @param game             the game being tested
     * @param support          the platform tested on
     * @param testTxt          full test text
     * @param gameVersion      build/version tested
     * @param gradePerCategory scores per standard category
     * @return the published GameTest
     * @throws IllegalArgumentException if tester doesn't own the game
     * @throws IllegalStateException    if playtime is insufficient
     * @throws IllegalStateException    if a test already exists for this game/support
     */
    public GameTest writeTest(Game game, Support support, String testTxt,
                              String gameVersion, Map<String, Float> gradePerCategory) {
        if (!playedGames.containsKey(game)) {
            throw new IllegalArgumentException("Vous ne possédez pas ce jeu !");
        }
        if (playedGames.get(game) < MIN_PLAYTIME_FOR_TEST) {
            throw new IllegalStateException("Temps de jeu insuffisant pour tester ("
                    + MIN_PLAYTIME_FOR_TEST + "h requises).");
        }
        // Check no test already exists for this game+support
        for (GameTest t : publishedTests) {
            if (t.getTestedGame().equals(game) && t.getSupport().equals(support)) {
                throw new IllegalStateException("Un test existe déjà pour ce jeu sur ce support.");
            }
        }

        GameTest test = new GameTest(this, support, game, LocalDate.now(),
                testTxt, gameVersion, gradePerCategory);
        publishedTests.add(test);

        // Release token votes on this game
        game.resetTokenVotes();

        // Award tokens to tester
        this.addTokens(TOKENS_PER_TEST);

        System.out.println("Test publié par " + pseudo + " pour " + game.getName()
                + " sur " + support.getName() + ". +" + TOKENS_PER_TEST + " jetons.");
        return test;
    }

    /**
     * Flags a player evaluation as problematic.
     *
     * @param eval the evaluation to flag
     */
    public void flagEvaluation(PlayerEvaluation eval) {
        eval.flag();
        System.out.println(pseudo + " a signalé l'évaluation de " + eval.getAuthor().getPseudo());
    }

    /** @return list of tests published by this tester */
    public List<GameTest> getPublishedTests() { return publishedTests; }

    @Override
    public String getProfileType() { return "Testeur"; }
}
