import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.StdOut;

import java.util.*;

public class Gerry implements Gerrymanderer {
    // references to commonly used members
    private boolean Party;
    private Graph G;
    private boolean[] VoterAlignments;
    private int D; //number of districts
    private int v; //number of voters
    private int r; //required number of voters to win a district

    private int[] DistrictLookup; //DAT for voters -> district
    private int[][] Districts; // District partitions

    @Override
    public int[][] gerrymander(Electorate electorate, boolean party)
    {
        Party = party;
        D = electorate.getNumberOfDistricts();
        v = D*D;
        r = (int)Math.ceil(D/2f);
        DistrictLookup = new int[v];
        Districts = new int[D][D];
        G = electorate.getGraph();
        VoterAlignments = electorate.getVoters();

        Stripe(); // Initially assign voters to districts according to their row in the matrix

        // Partition district i and i+1 into two districts, prioritizing the optimality of i
        for (int i = 0; i < D-1; i++) Partition(i,i+1);

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

    // Partition district a and b into new districts, prioritizing the optimality of a
    private void Partition(int a, int b)
    {
        Partition p = new Partition(a, b); //create Partitions

        // Apply partitions
        Districts[a] = p.GetP1();
        Districts[b] = p.GetP2();

        // Update lookup table
        for (int i = 0; i < D; i++)
        {
            DistrictLookup[Districts[a][i]] = a;
            DistrictLookup[Districts[b][i]] = b;
        }
    }

    // Partition class declaration
    private class Partition {

        private int[] P1; //first partition (i.e. optimal partition)
        private int[] P2; // everything that isnt in the first partition

        private int bestVoters; // the amount of voters in P1 at any given time

        // districts we are currently considering
        private int D1;
        private int D2;
        private int DNext; // Next district up (used in checking validity)
        //when this is false, it allows for stranded nodes, unfortunately this breaks on D>5
        private boolean RequireConnectivity = true;


        Partition(int a, int b)
        {
            //D1 and D2 represent the districts which we are attempting to 'partition'
            D1 = a;
            D2 = b;
            DNext = D2+1;


            // RequireConnectivity = D2 == D - 1;

            // Set default values for P1 and P2
            P1 = Districts[D1];
            P2 = Districts[D2];

            // The best amount of voters we have seen so far
            // We try to get either 0, or the closest value to r that is also greater than r
            // this value is associated with the partition stored in P1
            bestVoters = Integer.MAX_VALUE;

            // Path is created recursively
            int[] path = new int[D];
            boolean[] visited = new boolean[v];
            for (int v : Districts[a])
            {
                DFSUtil(v, visited, 0, 0, path);
                visited[v] = true; // once we have called DFSUtil, all paths containing v have been considered
            }
        }

        private int[] GetP1() { return P1; }
        private int[] GetP2() { return P2; }

        // recursively build the best district containing nodes from D1 and D2
        private int DFSUtil(int v, boolean[] visited, int len, int voters, int[] dist)
        {
            visited[v] = true;
            dist[len++] = v;
            if (VoterAlignments[v] == Party) voters++;

            // If dist is full, we consider the amount of voters in the district. If the new voter count is better
            // than our previous, we check if it is valid
            // if it's valid, we have found a new 'best' district, which we store along with its voter count
            if (len == D)
            {
                if (((voters == 0 || (voters >= r && voters < bestVoters) || bestVoters == Integer.MAX_VALUE) && partitionIsValid(dist)))
                {
                    bestVoters = voters;
                    P1 = Arrays.copyOf(dist, D);

                    visited[v] = false;
                    return voters;
                }

                visited[v] = false;
                return -1;
            }

            for (int edge : G.adj(v))
            {
                // Consider only unvisited neighbors that are currently being considered (i.e. in district D1 or D2)
                if (!visited[edge] && (DistrictLookup[edge] == D1 || DistrictLookup[edge] == D2))
                {
                    int amt = DFSUtil(edge, visited, len, voters, dist);
                    if (amt == 0 || amt == r) //optimal arrangement found
                    {
                        visited[v] = false;
                        return amt;
                    }
                }
            }
            visited[v] = false;
            return bestVoters;
        }

        // Checks whether a proposed district is valid
        private boolean partitionIsValid(int[] proposal)
        {
            // hash everything in the proposal so we can easier assemble all nodes not in the proposal
            HashSet<Integer> InProposal = new HashSet<>(D);
            for (int e : proposal) InProposal.add(e);

            boolean[] visited = new boolean[v];
            boolean[] valid = new boolean[v];

            int[] other = new int[D]; // if proposal is valid, other will replace P2
            int k = -1; //index for filling 'other'

            // collect all vertices that aren't in proposal into other
            for (int i = 0; i < D * 2; i++)
            {
                int Dist = (i >= D) ? D2 : D1;
                int e = Districts[Dist][i % D];
                if (InProposal.contains(e)) continue;
                other[++k] = e;
            }

            int[] count = new int[]{0}; // We use an array so it is passed by reference
            for (int v : other)
            {
                count[0] = 0;

                if (!visited[v]) partitionIsValidUtil(v, visited, valid, InProposal, count);
                if (RequireConnectivity && count[0] != D)  return false;
                else if(RequireConnectivity) break;
                else if (!valid[v]) return false;
            }
            P2 = other;
            return true;
        }

        // Recursively determine if a vertex v is 'valid'
        // a valid vertex is one that connects to the 'next district up'
        // also counts the number of vertices in 'other' that are reachable from v (used for RequireConnectivity)
        private boolean partitionIsValidUtil(int v, boolean[] visited, boolean[] valid, HashSet Proposal, int[] count)
        {
            if (visited[v]) return valid[v];

            visited[v] = true;
            count[0] += 1; // count is incremented every time a node is visited

            for (int e : G.adj(v))
            {
                // only extend to members of D1 and D2 that aren't in proposal
                if (Proposal.contains(e)) continue;

                int district = DistrictLookup[e]; //what district is e in?
                if (district == D1|| district == D2)
                {
                    if (visited[e] && valid[e]) valid[v] = true;
                    else if (!visited[e])
                    {
                        boolean val = partitionIsValidUtil(e, visited, valid, Proposal, count);
                        if(!valid[v]) valid[v] = val;
                    }
                }
                else if (district == DNext) valid[v] = true;
            }
            return valid[v];
        }
    }
}
