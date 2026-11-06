/**
 * Missions 1 and 2. Compare two strains with dynamic programming.
 *
 * Both are the same kind of table: d[i][j] answers a question about the first i letters of a and the first j letters of b.
 * Think about three things before you type: what d[i][j] means, how it follows from smaller cells, and what the first row and column are.
 */
public class Strains {

    /**
     * Mission 1. The smallest number of single-letter changes that turn a into b. One change is: replace a letter,
     * insert a letter, or delete a letter. This number is the "Mutations" line.
     * Example: "ACGT" -> "AGT" is 1 (delete C). "GATTACA" -> "GCATGCA" needs 3.
     *
     * d[i][j] = the smallest number of changes that turn a[0..i-1] (first i letters) into b[0..j-1] (first j letters).
     * Base: d[i][0] = i (delete all i letters), d[0][j] = j (insert all j letters).
     * Step: the last operation is a replacement/match (diagonal), a deletion (top) or an insertion (left).
     */
    public static int editDistance(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) d[i][0] = i;
        for (int j = 0; j <= m; j++) d[0][j] = j;
        for (int i = 1; i <= n; i++) {
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                int replace = d[i - 1][j - 1] + (ca == b.charAt(j - 1) ? 0 : 1);
                int delete = d[i - 1][j] + 1;
                int insert = d[i][j - 1] + 1;
                d[i][j] = Math.min(replace, Math.min(delete, insert));
            }
        }
        return d[n][m];
    }

    /**
     * Mission 2. The length of the longest common subsequence: letters that appear in both strains in the same order,
     * but not necessarily next to each other. This is the "Shared core" line.
     * Example: "ACGTT" and "AGTC": the longest is "AGT", length 3.
     *
     * L[i][j] = the length of the LCS of a[0..i-1] and b[0..j-1]. Row 0 and column 0 are 0 (empty prefix).
     * If the last letters are equal, they end a common subsequence: L[i-1][j-1] + 1.
     * Otherwise one of the two last letters is not used: max(L[i-1][j], L[i][j-1]).
     */
    public static int lcsLength(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] L = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                if (ca == b.charAt(j - 1)) L[i][j] = L[i - 1][j - 1] + 1;
                else L[i][j] = Math.max(L[i - 1][j], L[i][j - 1]);
            }
        }
        return L[n][m];
    }
}
