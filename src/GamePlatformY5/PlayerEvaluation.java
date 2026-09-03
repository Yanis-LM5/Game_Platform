package GamePlatformY5;

import java.time.LocalDate;

/**
 * Represents a player's evaluation (review) of a game on a specific support.
 * Players can rate the usefulness of other players' evaluations.
 */
public class PlayerEvaluation {

    /** The player who wrote this evaluation */
    private Player author;

    /** The game being evaluated */
    private Game game;

    /** The platform/support for this evaluation */
    private Support support;

    /** Date the evaluation was written */
    private LocalDate date;

    /** Text content of the evaluation */
    private String text;

    /** Game build/version number at the time of writing */
    private String gameVersion;

    /** Overall score given by the player (0-10) */
    private float globalScore;

    /** Number of "helpful" votes from other players */
    private int helpfulVotes;

    /** Number of "not helpful" votes from other players */
    private int notHelpfulVotes;

    /** Whether this evaluation has been flagged as problematic */
    private boolean flagged;

    /** Whether this evaluation has been deleted by an admin */
    private boolean deleted;

    /**
     * Constructs a PlayerEvaluation.
     *
     * @param author      the player writing the evaluation
     * @param game        the game being reviewed
     * @param support     the platform
     * @param text        review text
     * @param gameVersion build/version of the game
     * @param globalScore overall score (0-10)
     */
    public PlayerEvaluation(Player author, Game game, Support support,
                            String text, String gameVersion, float globalScore) {
        this.author = author;
        this.game = game;
        this.support = support;
        this.date = LocalDate.now();
        this.text = text;
        this.gameVersion = gameVersion;
        this.globalScore = globalScore;
        this.helpfulVotes = 0;
        this.notHelpfulVotes = 0;
        this.flagged = false;
        this.deleted = false;
    }

    /**
     * Adds a vote on the usefulness of this evaluation.
     * Grants a token to the author every 10 helpful votes.
     *
     * @param isHelpful true for helpful (+), false for not helpful (-)
     */
    public void addVote(boolean isHelpful) {
        if (isHelpful) {
            helpfulVotes++;
            // Every 10 helpful votes, the author earns 1 token
            if (helpfulVotes % 10 == 0) {
                author.addTokens(1);
            }
        } else {
            notHelpfulVotes++;
        }
    }

    /**
     * Flags this evaluation as problematic (done by a Tester).
     */
    public void flag() {
        this.flagged = true;
    }

    /**
     * Deletes/censors this evaluation (done by an Administrator).
     */
    public void delete() {
        this.deleted = true;
    }

    /** @return the author player */
    public Player getAuthor() { return author; }

    /** @return the game */
    public Game getGame() { return game; }

    /** @return the platform/support */
    public Support getSupport() { return support; }

    /** @return the date written */
    public LocalDate getDate() { return date; }

    /** @return review text */
    public String getText() { return text; }

    /** @return game version */
    public String getGameVersion() { return gameVersion; }

    /** @return overall score (0-10) */
    public float getGlobalScore() { return globalScore; }

    /** @return number of helpful votes */
    public int getHelpfulVotes() { return helpfulVotes; }

    /** @return number of not-helpful votes */
    public int getNotHelpfulVotes() { return notHelpfulVotes; }

    /** @return whether this evaluation is flagged */
    public boolean isFlagged() { return flagged; }

    /** @return whether this evaluation is deleted */
    public boolean isDeleted() { return deleted; }

    @Override
    public String toString() {
        return String.format("[%s] %s - Score: %.1f/10 | +%d/-%d votes%s",
                date, author.getPseudo(), globalScore,
                helpfulVotes, notHelpfulVotes,
                flagged ? " [FLAGGED]" : "");
    }
}
