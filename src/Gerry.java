import edu.princeton.cs.algs4.Graph;

import java.util.BitSet;

/*
There is a number of code prettification things that can happen, also im sure theres a bunch of off by one errors with
'D'

 */

public class Gerry implements Gerrymanderer
{
    private class Partition
    {
        int[] best;
        int[] partition1;
        int[] partition2;
        int bestCount;
        int D1;
        int D2;
        int DNext;

        public Partition(int a, int b)
        {
            best = new int[D];
            bestCount = Integer.MAX_VALUE;
            partition1 = new int[D];
            partition2 = new int[D];
            D1 = a;
            D2 = b;
            DNext = b == D-1 ? b : b+1;

            for (int member: Districts[a])
            {
                boolean[] visited = new boolean[v];
                int[] path = new int[D];

                DFSUtil(member, visited,0,0, path);
            }
        }
        private int DFSUtil(int v, boolean[] visited, int count, int voters, int[] path)
        {
            visited[v] = true;
            if (count == D  && Math.abs(voters-r) < bestCount  && partitionIsValid(path, D1, DNext))
            {
                bestCount = Math.abs(voters-r);
                best = path;
                return voters;
            }
            path[count++] = v;
            if (VoterAlignments[v] == Party) voters++;

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

        for (int i = 0; i < D-1; i++)
        {
            Partition(i,i+1);
        }

        return Publish();
    }

    private void Stripe()
    {
        for (int i = 0; i < v; i++)
        {
            int dist = i / D;
            DistrictLookup[i] = (dist);
            Districts[D][i] = (D*dist)+i;
        }
    }

    private void Partition(int a, int b)
    {
        Partition p = new Partition(a, b);
        // apply the partition!
        //update DistrictLookup
        //update Districts
        //Idk if we need OptimizedVoters, but if we do update it



//        for (int member: Districts[a])
//        {
//            boolean[] visited = new boolean[v];
//            int[] path = new int[D];
//
//            DFSUtil(member, a,b,visited,0,0,path);
//        }
        // BFS out from node, counting black nodes in path
        // When length is D check if its valid
        // if number of black nodes is r, done
        // Otherwise if number of black nodes is 0, done
        // Otherwise save result with number of black nodes and continue
           // Only have to store one of such sets, whichever is closest to optimal
        // At the end, if we haven't found a suitable set use the best we've found so far
    }

//    private int DFSUtil(int v, int a, int b, boolean[] visited, int count, int voters, int[] path, int[] best, int bestCount)
//    {
//        visited[v] = true;
//        if (count == D  && (voters > r || voters == 0)  && partitionIsValid(path))
//        {
//            if (voters > r)
//            {
//
//            }
//        }
//        for (int edge : G.adj(v))
//        {
//            //if (should traverse)
//            /*
//                if continue partition == 0 or == r return same thing
//                //else continue looking
//             */
//
//            if(!visited[edge] && DistrictLookup[edge] == a || DistrictLookup[edge] == b)
//            {
//
//            }
//        }
//        //return best_count
//    }


    // check if every element in proposal (district D) can reach the next district (NextD)
    private boolean partitionIsValid(int[] proposal, int Dist, int NextD)
    {
        // get partition from proposal
        /*
            We want to check all the nodes not in proposal (but are being considered in Partition)
            to see if they connect to a node in NextD
            Since we will calculate this 'other district', we should store it in some manner so we dont have to
            do it again later
         */
        boolean[] visited = new boolean[D];
        boolean[] valid = new boolean[D];
        for (int i = 0; i < D; i++)
        {
            valid[i] = partitionIsValidUtil(i,visited,valid,Dist,NextD);
            if(!valid[i]) return false;
        }
        return true;
    }

    private boolean partitionIsValidUtil(int v, boolean[] visited, boolean[] valid, int Dist, int NextD)
    {
        visited[v] = true;
        boolean validity = false;

        for (int e : G.adj(v))
        {
            if (DistrictLookup[e] == NextD)
            {
                validity = true;
            }
            else if(DistrictLookup[e] == Dist)
            {

                if (validity)
                {
                    visited[e] = true;
                    valid[e] = true;
                }
                else if (visited[e])
                {
                    validity = valid[e];
                }
                else valid[e] = partitionIsValidUtil(e, visited, valid, Dist, NextD);
            }
        }
        return validity;
    }

    private int[][] Publish()
    {
        return new int[0][];
    }


}
