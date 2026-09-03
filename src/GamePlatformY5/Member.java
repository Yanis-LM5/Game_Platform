package GamePlatformY5;

/**
 * Abstract base class representing any registered member of the platform.
 * Defines common attributes shared by Players, Testers, and Administrators.
 */
public abstract class Member {

    /** Unique username/pseudo */
    protected String pseudo;

    /** Token balance (used to vote for game tests) */
    protected int tokens;

    /** Whether this member account is blocked by an administrator */
    protected boolean blocked;

    /**
     * Constructs a Member with a given pseudo and initial token count.
     *
     * @param pseudo         the unique username
     * @param initialTokens  starting token balance
     */
    public Member(String pseudo, int initialTokens) {
        this.pseudo = pseudo;
        this.tokens = initialTokens;
        this.blocked = false;
    }

    /**
     * Adds tokens to this member's balance.
     *
     * @param amount number of tokens to add (must be positive)
     */
    public void addTokens(int amount) {
        if (amount > 0) this.tokens += amount;
    }

    /**
     * Spends tokens from this member's balance.
     *
     * @param amount number of tokens to spend
     * @throws IllegalStateException if balance is insufficient
     */
    public void spendTokens(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Le montant doit être positif.");
        if (this.tokens < amount) throw new IllegalStateException("Solde de jetons insuffisant !");
        this.tokens -= amount;
    }

    /**
     * Blocks this member account.
     */
    public void block() { this.blocked = true; }

    /**
     * Unblocks this member account.
     */
    public void unblock() { this.blocked = false; }

    /** @return the pseudo/username */
    public String getPseudo() { return pseudo; }

    /** @param pseudo new pseudo */
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    /** @return current token balance */
    public int getTokens() { return tokens; }

    /** @return whether this account is blocked */
    public boolean isBlocked() { return blocked; }

    /** @return profile type label for display */
    public abstract String getProfileType();

    @Override
    public String toString() {
        return pseudo + " [" + getProfileType() + "]" + (blocked ? " (BLOQUÉ)" : "");
    }
}
