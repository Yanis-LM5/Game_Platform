package GamePlatformY5;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
public class GameTest{

    private Tester tester;
    private Support support;
    private Game testedGame;

    private LocalDate dateTest;
    private String testTxt;
    private String gameVersion;
    private Map<String, Float> gradePerCategory; //interface, gameplay, optimisation...

    private List<String> pros;
    private List<String> cons;
    private String testConditions; //set up (Ram ?, SSD?,...)
    private List<Game> similarGame;
    private Map<String, Float> gradeSpecificCategory;

    public GameTest(Tester testeur, Support support, Game jeu, LocalDate dateTest,
                    String testTxt, String gameVersion, Map<String, Float> gradePerCategory) {
        this.tester = testeur;
        this.support = support;
        this.testedGame = jeu;
        this.dateTest = dateTest;
        this.testTxt = testTxt;
        this.gameVersion = gameVersion;
        this.gradePerCategory = gradePerCategory;
    }

    public LocalDate getDateTest() {
        return dateTest;
    }

    public void setDateTest(LocalDate dateTest) {
        this.dateTest = dateTest;
    }

    public List<String> getPointsForts() {
        return pros;
    }

    public void setPointsForts(List<String> pointsForts) {
        this.pros = pointsForts;
    }

}


class Tester {}  // to modify