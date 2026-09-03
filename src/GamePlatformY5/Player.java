package GamePlatformY5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a registered player on the platform.
 * Players can own games, write evaluations, rate other evaluations,
 * and spend tokens to vote for game tests.
 */
public class Player extends Member {

    /** Map of games owned by this player with their total playtime in hours */
    protected Map<Game, Float> playedGames;

    /** List of evaluations written by this player */
    protected List<PlayerEvaluation> evaluations;

    /** Map tracking how many tokens this player has placed on each game */
    protected Map<Game, Integer> tokenPlacedOnGame;

    /** Minimum playtime required to write an evaluation (in hours) */
    public static final float MIN_PLAYTIME_FOR_EVAL = 2.0f;

    /**
     * Constructs a Player with the given pseudo and initial token balance.
     *
     * @param pseudo        unique username
     * @param initialTokens starting token count (normally 3)
     */
    public Player(String pseudo, int initialTokens) {
        super(pseudo, initialTokens);
        this.playedGames = new HashMap<>();
        this.evaluations = new ArrayList<>();
        this.tokenPlacedOnGame = new HashMap<>();
    }

    /**
     * Adds a game to this player's library.
     *
     * @param game the game to add
     */
    public void addGame(Game game) {
        if (!playedGames.containsKey(game)) {
            playedGames.put(game, 0.0f);
        }
    }

    /**
     * Records additional playtime for a game the player owns.
     *
     * @param game        the game played
     * @param hoursPlayed number of hours to add
     * @throws IllegalArgumentException if the player doesn't own the game
     */
    public void playGame(Game game, float hoursPlayed) {
        if (!playedGames.containsKey(game)) {
            throw new IllegalArgumentException("Vous ne possédez pas ce jeu !");
        }
        float current = playedGames.getOrDefault(game, 0.0f);
        playedGames.put(game, current + hoursPlayed);
    }

    /**
     * Writes a player evaluation for a game on a given support.
     * Requires the player to own the game and have played at least MIN_PLAYTIME_FOR_EVAL hours.
     *
     * @param game        the game to evaluate
     * @param support     the platform
     * @param text        review text
     * @param gameVersion build/version evaluated
     * @param globalScore overall score (0-10)
     * @return the created PlayerEvaluation
     * @throws IllegalArgumentException if the player doesn't own the game
     * @throws IllegalStateException    if playtime is insufficient
     */
    public PlayerEvaluation writeEvaluation(Game game, Support support,
                                            String text, String gameVersion, float globalScore) {
        if (!playedGames.containsKey(game)) {
            throw new IllegalArgumentException("Vous ne possédez pas ce jeu !");
        }
        if (playedGames.get(game) < MIN_PLAYTIME_FOR_EVAL) {
            throw new IllegalStateException("Temps de jeu insuffisant pour évaluer ("
                    + MIN_PLAYTIME_FOR_EVAL + "h requises).");
        }
        PlayerEvaluation eval = new PlayerEvaluation(this, game, support, text, gameVersion, globalScore);
        evaluations.add(eval);
        return eval;
    }

    /**
     * Rates another player's evaluation as helpful or not.
     *
     * @param eval      the evaluation to rate
     * @param isHelpful true for helpful (+), false for not helpful (-)
     */
    public void rateOtherEvaluation(PlayerEvaluation eval, boolean isHelpful) {
        eval.addVote(isHelpful);
    }

    /**
     * Places tokens on a game to request a professional test.
     *
     * @param game           the game to vote for
     * @param tokensToSpend  number of tokens to place
     * @throws IllegalArgumentException if amount is not positive
     * @throws IllegalStateException    if balance is insufficient
     */
    public void voteForGameTest(Game game, int tokensToSpend) {
        if (tokensToSpend <= 0) {
            throw new IllegalArgumentException("La quantité de jetons doit être positive.");
        }
        spendTokens(tokensToSpend);
        game.addTokenVotes(tokensToSpend);
        tokenPlacedOnGame.merge(game, tokensToSpend, Integer::sum);
        System.out.println(pseudo + " a placé " + tokensToSpend + " jeton(s) pour : " + game.getName());
    }

    /**
     * Withdraws tokens previously placed on a game.
     *
     * @param game          the game to withdraw from
     * @param tokensToTake  number of tokens to withdraw
     * @throws IllegalStateException if not enough tokens placed on that game
     */
    public void withdrawTokensFromGame(Game game, int tokensToTake) {
        int placed = tokenPlacedOnGame.getOrDefault(game, 0);
        if (tokensToTake > placed) {
            throw new IllegalStateException("Vous n'avez pas placé autant de jetons sur ce jeu.");
        }
        this.tokens += tokensToTake;
        game.removeTokenVotes(tokensToTake);
        tokenPlacedOnGame.put(game, placed - tokensToTake);
        System.out.println(pseudo + " a retiré " + tokensToTake + " jeton(s) de : " + game.getName());
    }

    /** @return map of owned games and their playtimes */
    public Map<Game, Float> getPlayedGames() { return playedGames; }

    /** @return list of evaluations written by this player */
    public List<PlayerEvaluation> getEvaluations() { return evaluations; }

    /** @return map of tokens placed per game */
    public Map<Game, Integer> getTokenPlacedOnGame() { return tokenPlacedOnGame; }

    @Override
    public String getProfileType() { return "Joueur"; }
}
