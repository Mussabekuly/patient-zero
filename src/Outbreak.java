import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     *
     * in[v]  = cost[v] + sum over children c of min(in[c], out[c])   (v tested: each child is free to choose)
     * out[v] = sum over children c of in[c]                           (v not tested: every child must be tested)
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        boolean[] tested = new boolean[n];
        if (n == 0) return tested;

        int[] parent = new int[n];
        int[] order = bfsOrder(n, nb, parent);

        long[] in = new long[n], out = new long[n];
        // leaves first: go through the BFS order backwards, so every child is done before its parent
        for (int idx = n - 1; idx >= 0; idx--) {
            int v = order[idx];
            in[v] = cost[v];
            out[v] = 0;
            for (int c : nb[v]) {
                if (c == parent[v]) continue;
                in[v] += Math.min(in[c], out[c]);
                out[v] += in[c];
            }
        }

        // rebuild from the root down with the same rule
        tested[order[0]] = in[order[0]] <= out[order[0]];
        for (int idx = 1; idx < n; idx++) {
            int v = order[idx];
            if (!tested[parent[v]]) tested[v] = true;          // the link to the parent must be covered by v
            else tested[v] = in[v] <= out[v];                    // parent is tested: take the cheaper state of v
        }
        return tested;
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     *
     * down[v] = the longest way down from v (in links). The best chain whose highest person is v is
     * best1 + best2, the two largest values of down[c] + 1 over the children c.
     */
    public static int longestChain(int n, int[][] nb) {
        if (n <= 1) return 0;
        int[] parent = new int[n];
        int[] order = bfsOrder(n, nb, parent);
        int[] down = new int[n];
        int best = 0;
        for (int idx = n - 1; idx >= 0; idx--) {
            int v = order[idx];
            int best1 = 0, best2 = 0;
            for (int c : nb[v]) {
                if (c == parent[v]) continue;
                int len = down[c] + 1;
                if (len > best1) { best2 = best1; best1 = len; }
                else if (len > best2) best2 = len;
            }
            down[v] = best1;
            best = Math.max(best, best1 + best2);
        }
        return best;
    }

    /** BFS from person 0. Fills parent[] (-1 for the root) and returns the visiting order (parents before children). */
    private static int[] bfsOrder(int n, int[][] nb, int[] parent) {
        int[] order = new int[n];
        boolean[] seen = new boolean[n];
        Arrays.fill(parent, -1);
        int head = 0, tail = 0;
        order[tail++] = 0;
        seen[0] = true;
        while (head < tail) {
            int v = order[head++];
            for (int c : nb[v]) {
                if (!seen[c]) {
                    seen[c] = true;
                    parent[c] = v;
                    order[tail++] = c;
                }
            }
        }
        return order;
    }
}
