package GamePlatformY5;

/**
 * Administrator on the platform.
 */
public class Administrator extends Tester {

    /**
     * Constructs an Administrator with the given pseudo
     * @param pseudo unique username
     */
    public Administrator(String pseudo) {
        super(pseudo, 0); // Admins start with 0 tokens (they manage, not play)
    }

    /**
     * Deletes/censors a player evaluation.
     * @param eval the evaluation to delete
     */
    public void deleteEvaluation(PlayerEvaluation eval) {
        eval.delete();
        System.out.println("[ADMIN] Évaluation de " + eval.getAuthor().getPseudo() + " supprimée.");
    }

    /**
     * Blocks a member account, preventing them from performing actions.
     * @param member the member to block
     */
    public void blockMember(Member member) {
        member.block();
        System.out.println("[ADMIN] Membre " + member.getPseudo() + " bloqué.");
    }

    /**
     * Unblocks a member account.
     *
     * @param member the member to unblock
     */
    public void unblockMember(Member member) {
        member.unblock();
        System.out.println("[ADMIN] Membre " + member.getPseudo() + " débloqué.");
    }

    @Override
    public String getProfileType() { return "Administrateur"; }
}
