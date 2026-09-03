package GamePlatformY5;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a video game with its static data loaded from the CSV source.
 * A game can exist on multiple supports (platforms).
 */
public class Game {

    /** Game title (e.g. "Virtua Tennis 4") */
    protected String name;

    /** Main genre/category (e.g. "Sports") */
    protected String category;

    /** Publisher/editor (e.g. "Sega") */
    protected String editor;

    /** Age rating (e.g. "E", "T", "M") */
    protected String rating;

    /** List of supports (platforms) this game is available on */
    protected List<Support> supports;

    /** Number of tokens placed by players to request a review */
    private int tokenVotes;

    /**
     * Constructs a Game with its core static data.
     *
     * @param name     game title
     * @param category main genre
     * @param editor   publisher
     * @param rating   age rating
     */
    public Game(String name, String category, String editor, String rating) {
        this.name = name;
        this.category = category;
        this.editor = editor;
        this.rating = rating;
        this.supports = new ArrayList<>();
        this.tokenVotes = 0;
    }

    /**
     * Adds a support entry for this game.
     *
     * @param support the platform support to add
     */
    public void addSupport(Support support) {
        this.supports.add(support);
    }

    /**
     * Retrieves the Support entry matching the given platform name.
     *
     * @param platformName the platform name (e.g. "PC")
     * @return the matching Support, or null if not found
     */
    public Support getSupportByName(String platformName) {
        for (Support s : supports) {
            if (s.getName().equalsIgnoreCase(platformName)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Adds tokens to this game's vote pool.
     *
     * @param amount number of tokens to add (must be positive)
     */
    public void addTokenVotes(int amount) {
        if (amount > 0) this.tokenVotes += amount;
    }

    /**
     * Removes tokens from this game's vote pool (e.g. when player withdraws).
     *
     * @param amount number of tokens to remove
     */
    public void removeTokenVotes(int amount) {
        this.tokenVotes = Math.max(0, this.tokenVotes - amount);
    }

    /**
     * Resets all token votes to zero (called when a test is published for this game).
     */
    public void resetTokenVotes() {
        this.tokenVotes = 0;
    }

    /** @return game title */
    public String getName() { return name; }

    /** @return main genre/category */
    public String getCategory() { return category; }

    /** @return publisher */
    public String getEditor() { return editor; }

    /** @return age rating */
    public String getRating() { return rating; }

    /** @return list of supported platforms */
    public List<Support> getSupports() { return supports; }

    /** @return total token votes placed on this game */
    public int getTokenVotes() { return tokenVotes; }

    @Override
    public String toString() {
        return name + " [" + category + ", " + editor + ", Rating: " + rating + "]";
    }
}
