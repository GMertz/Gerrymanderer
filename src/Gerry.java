import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

/*
There is a number of code prettification things that can happen, also im sure theres a bunch of off by one errors with
'D'

 */

public class Gerry implements Gerrymanderer
{
    private class Partition
    {
        private int[] P1;
        private int[] P2;
        private int bestCount;
        private int D1;
        private int D2;
        private int DNext;

        public Partition(int a, int b)
        {
            D1 = a;
            D2 = b;

            P1 = Districts[D1];
            P2 = Districts[D2];

            bestCount = Integer.MAX_VALUE;


            DNext = D2 == D-1 ? D2 : D2+1;
            int[] path = new int[D];

            for (int member: Districts[a])
            {
                boolean[] visited = new boolean[v];
                DFSUtil(member, visited,0,0, path);
            }
        }

        public int[] GetP1()
        {
            return P1;
        }

        public int[] GetP2()
        {
            return P2;
        }

        private int DFSUtil(int v, boolean[] visited, int count, int voters, int[] path)
        {
            visited[v] = true;
            path[count++] = v;
            if (VoterAlignments[v] == Party) voters++;

            if (count == D)
            {
                if((voters == 0 || (voters >= r && voters < bestCount)) && partitionIsValid(path))
                {
                    bestCount = voters;
                    P1 = Arrays.copyOf(path, D);
                    return voters;
                }
                return -1;
            }

            for (int edge : G.adj(v))
            {
                if(!visited[edge] && (DistrictLookup[edge] == D1 || DistrictLookup[edge] == D2))
                {
                    int amt = DFSUtil(edge, visited, count, voters, path);
                    if (amt == 0 || amt == r) return amt;
                }
            }
            return bestCount;
        }
        private boolean partitionIsValid(int[] proposal)
        {
            HashSet<Integer> InProposal = new HashSet<>(D/2);

            for (int e : proposal) InProposal.add(e);

            boolean[] visited = new boolean[v];
            boolean[] valid = new boolean[v];


            int[] other = new int[D]; // if proposal is valid, this will replace P2
            int k = -1; //index for filling 'other'
            //StdOut.print("\n---------------------------\n");
            for (int i = 0; i < D*2; i++)
            {
                // for the first D iterations, use D1, then use D2
                int Dist = (i >= D)? D2 : D1;
                int e = Districts[Dist][i%D];

                // we are only considering the 'left-overs' (skip ones in proposal)
                if (!InProposal.contains(e))
                {
                    other[++k] = e;

                    valid[e] = partitionIsValidUtil(e, visited, valid, InProposal);
                    if (!valid[e]) return false;
                }
            }
            P2 = other;
            return true;
        }

        private boolean partitionIsValidUtil(int v, boolean[] visited, boolean[] valid, HashSet Proposal)
        {
            // something is not working here
            visited[v] = true;

            for (int e : G.adj(v))
            {
                if (Proposal.contains(e)) continue;

                // if v has an edge leading to the next district up, we are valid
                if(visited[e])
                {
                    return valid[e];
                }
                if (DistrictLookup[e] == DNext)
                {
                    return true;
                }
                else if(DistrictLookup[e] == D2)
                {
                    return (valid[e] = partitionIsValidUtil(e, visited,valid,Proposal));
                }
            }
            return false;
        }
    }

    BitSet OptimizedVoters;
    int[] DistrictLookup;
    int[][] Districts;
    final boolean Party = true;
    Graph G;
    boolean[] VoterAlignments;

    BitSet OptimizedDistrictLookup;

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
        OptimizedDistrictLookup = new BitSet();
        DistrictLookup = new int[v];
        Districts = new int[D][D];
        G = electorate.getGraph();
        VoterAlignments = electorate.getVoters();
        Stripe();
        //if (true) return Districts;

        for (int i = 0; i < D-1; i++)
        {
            Partition(i,i+1);
        }

        return Districts;
    }

    private void Stripe()
    {
        int dist;
        for (int i = 0; i < v; i++)
        {
            dist = i / D;
            DistrictLookup[i] = (dist);
            Districts[dist][i % D] = i;
        }
    }

    private void Partition(int a, int b)
    {
        Partition p = new Partition(a, b);
        Districts[a] = p.P1;
        Districts[b] = p.P2;

        for (int i = 0; i < D; i++)
        {
            DistrictLookup[p.P1[i]] = a;
            DistrictLookup[p.P2[i]] = b;
        }


        // BFS out from node, counting black nodes in path
        // When length is D check if its valid
        // if number of black nodes is r, done
        // Otherwise if number of black nodes is 0, done
        // Otherwise save result with number of black nodes and continue
           // Only have to store one of such sets, whichever is closest to optimal
        // At the end, if we haven't found a suitable set use the best we've found so far
    }

    private int[][] Publish()
    {
        return new int[0][];
    }


}
