import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Graph;

import java.util.BitSet;
import java.util.Set;
import java.util.Stack;


public class Gerry implements Gerrymanderer
{
    BitSet OptimizedVoters;
    int[] Districts;
    BitSet OptimizedDistricts;

    int D; //number of districts
    int v; //number of voters
    int r; //required number of voters to win a district

    @Override
    public int[][] gerrymander(Electorate electorate, boolean party)
    {
        D = electorate.getNumberOfDistricts();
        v = D*D;
        r = (int)Math.ceil(D/2);

        OptimizedVoters = new BitSet();
        OptimizedDistricts = new BitSet();
        Districts = new int[v];
        Stripe();

        for (int i = 0; i < D; i++)
        {
            // considering i and i+1 i.e. districts[k] == i or i+1
        }

        return Publish();
    }

    private void Stripe()
    {
        for (int i = 0; i < v; i++)
            Districts[i] = (i / D);
    }

    private void Partition(int a, int b)
    {
        // BFS out from node, counting black nodes in path
        // When length is D check if its valid
        // if number of black nodes is r, done
        // Otherwise if number of black nodes is 0, done
        // Otherwise save result with number of black nodes and continue
           // Only have to store one of such sets, whichever is closest to optimal
        // At the end, if we haven't found a suitable set use the best we've found so far

    }

    private boolean PartitionIsValid(int[] proposal)
    {
        return false;
    }

    private int[][] Publish()
    {
        return new int[0][];
    }


}
