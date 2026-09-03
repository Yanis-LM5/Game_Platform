package GamePlatformY5;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class GameTest {

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
    private Map<String, Float> gradePerCategory; // interface, gameplay, optimisation...

    /** Scores per category (e.g. "gameplay" -> 8.5, "interface" -> 7.0) */
    private Map<String, Float> gradePerCategory;

    /** Optional: list of positive points */
    private List<String> pros;

    /** Optional: list of negative points */
    private List<String> cons;
    private String testConditions; // set up (Ram ?, SSD?,...)
    private List<Game> similarGame;
    private Map<String, Float> gradeSpecificCategory;

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

    // --- Getters et Setters ---

    public Tester getTester() {
        return tester;
    }

    public void setTester(Tester tester) {
        this.tester = tester;
    }

    public Support getSupport() {
        return support;
    }

    public void setSupport(Support support) {
        this.support = support;
    }

    public Game getTestedGame() {
        return testedGame;
    }

    public void setTestedGame(Game testedGame) {
        this.testedGame = testedGame;
    }

    public LocalDate getDateTest() {
        return dateTest;
    }

    // --- Getters and Setters ---

    public String getTestTxt() {
        return testTxt;
    }

    public void setTestTxt(String testTxt) {
        this.testTxt = testTxt;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public Map<String, Float> getGradePerCategory() {
        return gradePerCategory;
    }

    public void setGradePerCategory(Map<String, Float> gradePerCategory) {
        this.gradePerCategory = gradePerCategory;
    }

    public List<String> getPros() {
        return pros;
    }

    public void setPros(List<String> pros) {
        this.pros = pros;
    }

    public List<String> getCons() {
        return cons;
    }

    public void setCons(List<String> cons) {
        this.cons = cons;
    }

    public String getTestConditions() {
        return testConditions;
    }

    public void setTestConditions(String testConditions) {
        this.testConditions = testConditions;
    }

    public List<Game> getSimilarGame() {
        return similarGame;
    }

    public void setSimilarGame(List<Game> similarGame) {
        this.similarGame = similarGame;
    }

    public Map<String, Float> getGradeSpecificCategory() {
        return gradeSpecificCategory;
    }

    public void setGradeSpecificCategory(Map<String, Float> gradeSpecificCategory) {
        this.gradeSpecificCategory = gradeSpecificCategory;
    }
}

    /** @param dateTest new publication date */
    public void setDateTest(LocalDate dateTest) { this.dateTest = dateTest; }

