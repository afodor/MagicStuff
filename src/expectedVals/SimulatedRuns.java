package expectedVals;

import java.util.Random;

// from ChatGPT
public class SimulatedRuns {

    // Per-game win probability
    private static final double P_WIN = 0.5;

    // Number of simulated drafts
    private static final int NUM_TOURNAMENTS = 1_000_000;

    // Premier Draft gem rewards for 0..7 wins
    private static final int[] GEM_REWARDS = {
            50,   // 0 wins
            100,  // 1 win
            250,  // 2 wins
            1000, // 3 wins
            1400, // 4 wins
            1600, // 5 wins
            1800, // 6 wins
            2200  // 7 wins
    };

    // Entry fee in gems
    private static final int ENTRY_COST = 1500;

    public static void main(String[] args) {
        Random rng = new Random();

        double totalGemsEarned = 0.0;
        double totalNetGems = 0.0;

        // Track how often we end with 0..7 wins
        int[] winCounts = new int[8];

        for (int t = 0; t < NUM_TOURNAMENTS; t++) {
            int wins = simulateOneDraft(rng, P_WIN);
            int gemsThisDraft = GEM_REWARDS[wins];
            double netThisDraft = gemsThisDraft - ENTRY_COST;

            totalGemsEarned += gemsThisDraft;
            totalNetGems += netThisDraft;
            winCounts[wins]++;
        }

        double avgGemsEarned = totalGemsEarned / NUM_TOURNAMENTS;
        double avgNetGems = totalNetGems / NUM_TOURNAMENTS;

        System.out.println("Simulated " + NUM_TOURNAMENTS + " Premier Drafts");
        System.out.println("Per-game win rate p = " + P_WIN);
        System.out.println("Average gems earned per draft: " + avgGemsEarned);
        System.out.println("Average net gems per draft (after 1500 entry): " + avgNetGems);
        System.out.println();

        System.out.println("Empirical distribution of final wins (0..7):");
        for (int w = 0; w <= 7; w++) {
            double freq = (double) winCounts[w] / NUM_TOURNAMENTS;
            System.out.printf("%d wins: %d (%.4f)%n", w, winCounts[w], freq);
        }
    }

    /**
     * Simulate one Premier Draft:
     * - Stop at 7 wins or 3 losses, whichever comes first.
     * - Return final number of wins (0..7).
     */
    private static int simulateOneDraft(Random rng, double pWin) {
        int wins = 0;
        int losses = 0;

        while (wins < 7 && losses < 3) {
            double r = rng.nextDouble();
            if (r < pWin) {
                wins++;
            } else {
                losses++;
            }
        }

        // wins is in [0,7] at this point
        return wins;
    }
}
