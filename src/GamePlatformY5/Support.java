package GamePlatformY5;

/**
 * Represents a gaming platform/support (e.g. PC, PS3, Xbox).
 * Each game can have a specific release on a given support with its own statistics.
 */
public class Support {

    /** Name of the support (e.g. "PC", "PS3") */
    protected String name;

    /** Year of release on this support */
    protected int releaseYear;

    /** Developer on this support */
    protected String developer;

    /** Creator/manufacturer of the support (e.g. "Sony", "Microsoft") */
    protected String creator;

    /** Worldwide sales in millions */
    protected float globalSales;

    /** Number of critic/tester reviews */
    protected int criticCount;

    /** Average normalized critic score (0-100) */
    protected float criticScore;

    /** Number of player ratings */
    protected int userCount;

    /** Average normalized player score (0-10) */
    protected float userScore;

    /**
     * Constructs a Support with all fields from CSV data.
     *
     * @param name        platform name
     * @param releaseYear year of release on this platform
     * @param developer   developer on this platform
     * @param globalSales worldwide sales in millions
     * @param criticScore average critic score (0-100)
     * @param criticCount number of critic reviews
     * @param userScore   average user score (0-10)
     * @param userCount   number of user ratings
     */
    public Support(String name, int releaseYear, String developer,
                   float globalSales, float criticScore, int criticCount,
                   float userScore, int userCount) {
        this.name = name;
        this.releaseYear = releaseYear;
        this.developer = developer;
        this.globalSales = globalSales;
        this.criticScore = criticScore;
        this.criticCount = criticCount;
        this.userScore = userScore;
        this.userCount = userCount;
    }

    /** @return platform name */
    public String getName() { return name; }

    /** @return year of release on this platform */
    public int getReleaseYear() { return releaseYear; }

    /** @return developer on this platform */
    public String getDeveloper() { return developer; }

    /** @return worldwide sales in millions */
    public float getGlobalSales() { return globalSales; }

    /** @return average critic score (0-100) */
    public float getCriticScore() { return criticScore; }

    /** @return number of critic reviews */
    public int getCriticCount() { return criticCount; }

    /** @return average user score (0-10) */
    public float getUserScore() { return userScore; }

    /** @return number of user ratings */
    public int getUserCount() { return userCount; }

    @Override
    public String toString() {
        return name + " (" + releaseYear + ")";
    }
}
