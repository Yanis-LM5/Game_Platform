package GamePlatformY5;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Represents a structured test written by a Tester for a game on a specific support.
 * At most one GameTest can exist per tester per support.
 */
public class GameTest {

    /** The tester who wrote this test */
    private Tester tester;

    /** The platform/support this test was done on */
    private Support support;

    /** The game being tested */
    private Game testedGame;

    /** Date the test was published */
    private LocalDate dateTest;

    /** Full text of the test */
    private String testTxt;

    /** Game build/version number tested */
    private String gameVersion;

    /** Scores per category (e.g. "gameplay" -> 8.5, "interface" -> 7.0) */
    private Map<String, Float> gradePerCategory;

    /** Optional: list of positive points */
    private List<String> pros;

    /** Optional: list of negative points */
    private List<String> cons;

    /** Optional: hardware/setup conditions (RAM, SSD, etc.) */
    private String testConditions;

    /** Optional: list of similar games recommended */
    private List<Game> similarGames;

    /** Optional: genre-specific category scores */
    private Map<String, Float> gradeSpecificCategory;

    /**
     * Constructs a GameTest with mandatory fields.
     *
     * @param tester           the tester writing the test
     * @param support          the platform tested on
     * @param testedGame       the game being tested
     * @param dateTest         publication date
     * @param testTxt          full test text
     * @param gameVersion      build/version tested
     * @param gradePerCategory scores per standard category
     */
    public GameTest(Tester tester, Support support, Game testedGame, LocalDate dateTest,
                    String testTxt, String gameVersion, Map<String, Float> gradePerCategory) {
        this.tester = tester;
        this.support = support;
        this.testedGame = testedGame;
        this.dateTest = dateTest;
        this.testTxt = testTxt;
        this.gameVersion = gameVersion;
        this.gradePerCategory = gradePerCategory;
    }

    /**
     * Computes the overall score as the average of all category grades.
     *
     * @return average score across all categories, or 0 if none
     */
    public float getOverallScore() {
        if (gradePerCategory == null || gradePerCategory.isEmpty()) return 0;
        float sum = 0;
        for (float v : gradePerCategory.values()) sum += v;
        return sum / gradePerCategory.size();
    }

    // --- Getters and Setters ---

    /** @return the tester */
    public Tester getTester() { return tester; }

    /** @return the platform */
    public Support getSupport() { return support; }

    /** @return the tested game */
    public Game getTestedGame() { return testedGame; }

    /** @return test publication date */
    public LocalDate getDateTest() { return dateTest; }

    /** @param dateTest new publication date */
    public void setDateTest(LocalDate dateTest) { this.dateTest = dateTest; }

    /** @return test text */
    public String getTestTxt() { return testTxt; }

    /** @return game version tested */
    public String getGameVersion() { return gameVersion; }

    /** @return scores per category */
    public Map<String, Float> getGradePerCategory() { return gradePerCategory; }

    /** @return list of pros */
    public List<String> getPros() { return pros; }

    /** @param pros list of positive points */
    public void setPros(List<String> pros) { this.pros = pros; }

    /** @return list of cons */
    public List<String> getCons() { return cons; }

    /** @param cons list of negative points */
    public void setCons(List<String> cons) { this.cons = cons; }

    /** @return test hardware/setup conditions */
    public String getTestConditions() { return testConditions; }

    /** @param testConditions hardware conditions string */
    public void setTestConditions(String testConditions) { this.testConditions = testConditions; }

    /** @return list of similar games */
    public List<Game> getSimilarGames() { return similarGames; }

    /** @param similarGames list of similar recommended games */
    public void setSimilarGames(List<Game> similarGames) { this.similarGames = similarGames; }

    /** @return genre-specific category grades */
    public Map<String, Float> getGradeSpecificCategory() { return gradeSpecificCategory; }

    /** @param gradeSpecificCategory genre-specific scores */
    public void setGradeSpecificCategory(Map<String, Float> gradeSpecificCategory) {
        this.gradeSpecificCategory = gradeSpecificCategory;
    }

    @Override
    public String toString() {
        return String.format("[TEST] %s on %s by %s (%s) - Overall: %.1f/10",
                testedGame.getName(), support.getName(),
                tester.getPseudo(), dateTest, getOverallScore());
    }
}
