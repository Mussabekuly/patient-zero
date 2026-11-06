import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     *
     * k is the length of the current border. When p[i] does not extend it, fall back to the next shorter border
     * pi[k-1] (a border of a border is again a border). Amortised O(m): k grows by at most 1 per step.
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];
        int k = 0;
        for (int i = 1; i < m; i++) {
            while (k > 0 && p.charAt(i) != p.charAt(k)) k = pi[k - 1];
            if (p.charAt(i) == p.charAt(k)) k++;
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     *
     * k = how many letters of p are matched right now. i only moves forward; on a mismatch k jumps to pi[k-1].
     * After a full match k = pi[m-1], so overlapping occurrences are found too.
     */
    public static int[] findAll(String text, String p) {
        int n = text.length(), m = p.length();
        if (m == 0 || m > n) return new int[0];
        int[] pi = prefixFunction(p);
        int[] found = new int[n];
        int count = 0;
        int k = 0;
        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            while (k > 0 && c != p.charAt(k)) k = pi[k - 1];
            if (c == p.charAt(k)) k++;
            if (k == m) {
                found[count++] = i - m + 1;
                k = pi[m - 1];
            }
        }
        return Arrays.copyOf(found, count);
    }

    static final long BASE = 131;
    static final long MOD = 1_000_000_007L;

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        if (k <= 0 || a.length() < k || b.length() < k) return 0;

        // BASE^(k-1) mod MOD: the weight of the letter that leaves the window
        long high = 1;
        for (int i = 0; i < k - 1; i++) high = high * BASE % MOD;

        // hash of every window of b -> start positions of the windows with that hash
        Map<Long, List<Integer>> windowsOfB = new HashMap<>();
        long h = 0;
        for (int i = 0; i < b.length(); i++) {
            if (i >= k) h = roll(h, b.charAt(i - k), high);
            h = (h * BASE + b.charAt(i)) % MOD;
            if (i >= k - 1) windowsOfB.computeIfAbsent(h, x -> new ArrayList<>()).add(i - k + 1);
        }

        int count = 0;
        h = 0;
        for (int i = 0; i < a.length(); i++) {
            if (i >= k) h = roll(h, a.charAt(i - k), high);
            h = (h * BASE + a.charAt(i)) % MOD;
            if (i >= k - 1) {
                int start = i - k + 1;
                List<Integer> candidates = windowsOfB.get(h);
                if (candidates == null) continue;
                for (int s : candidates) {
                    if (sameLetters(a, start, b, s, k)) { count++; break; }
                }
            }
        }
        return count;
    }

    /** Removes the letter that leaves the window. Adding MOD keeps the value non-negative. */
    private static long roll(long h, char out, long high) {
        return ((h - out * high % MOD) % MOD + MOD) % MOD;
    }

    /** Letter-by-letter check after equal hashes: equal hashes do not prove equal words. */
    private static boolean sameLetters(String a, int i, String b, int j, int k) {
        for (int t = 0; t < k; t++) if (a.charAt(i + t) != b.charAt(j + t)) return false;
        return true;
    }
}
