import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.*;

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

            // Invalid Arrangements are coming out
            // I think it's because if the algorithm doesn't find a partition better than the default, it will
            //revert to default, which can sometimes be fragmented

            bestCount = Integer.MAX_VALUE;

            DNext = D2 == D-1 ? D2 : D2+1; // this doesn't work, will strand nodes at the end
            int[] path = new int[D];
            for (int member: Districts[a])
            {
                boolean[] visited = new boolean[v];
                DFSUtil(member, visited,0,0, path);
            }
            //StdOut.println("\n-------------------");
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
                if((voters == 0 || (voters >= r && voters < bestCount)  || bestCount == Integer.MAX_VALUE) && partitionIsValid(path))
                {
                    bestCount = voters;
                    P1 = Arrays.copyOf(path, D);
                    return voters;
                }
                visited[v] = false;
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
            visited[v] = false;
            return bestCount;
        }


        private boolean EndPartitionIsValid(int[] proposal)
        {
            HashSet<Integer> InProposal = new HashSet<>(D);

            for (int e : proposal) InProposal.add(e);

            boolean[] visited = new boolean[v];
            Stack<Integer> st = new Stack<>();

            int[] other = new int[D]; // if proposal is valid, this will replace P2
            int k = -1; //index for filling 'other
            int count = 0, p;

            for (int i = 0; i < D*2; i++)
            {
                // for the first D iterations, use D1, then use D2
                int Dist = (i >= D)? D2 : D1;
                int e = Districts[Dist][i%D];

                // we are only considering the 'left-overs' (skip ones in proposal)
                if (!InProposal.contains(e))
                {
                    st.push(e);

                    while(!st.isEmpty())
                    {
                        p = st.pop();
                        if (visited[p]) continue;

                        other[count] = p;
                        visited[p] = true;
                        count += 1;

                        for (int n : G.adj(p))
                        {
                            if(!visited[n] && !InProposal.contains(n) && (DistrictLookup[n] == D1 || DistrictLookup[n] == D2))
                                st.push(n);
                        }
                    }

                }
                if (count != D) return false;
                break;
            }
            P2 = other;
            return true;
        }


        // not working for the final district
        private boolean partitionIsValid(int[] proposal)
        {
            //if (D2 == DNext) return EndPartitionIsValid(proposal);

            HashSet<Integer> InProposal = new HashSet<>(D);

            for (int e : proposal) InProposal.add(e);

            boolean[] visited = new boolean[v];
            boolean[] valid = new boolean[v];


            int[] other = new int[D]; // if proposal is valid, this will replace P2
            int k = -1; //index for filling 'other'
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
                    if (!valid[e])
                    {
                        return false;
                    }
                }
            }
            P2 = other;
            return true;
        }

        // edit this to count how many members of 'other' it is connected to
        private boolean partitionIsValidUtil(int v, boolean[] visited, boolean[] valid, HashSet Proposal)
        {
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
                else if(DistrictLookup[e] == D2 || DistrictLookup[e] == D1)
                {
                    return (valid[e] = partitionIsValidUtil(e, visited,valid,Proposal));
                }
            }
            return false;
        }
    }

    // references to commonly used members
    private boolean Party;
    private Graph G;
    private boolean[] VoterAlignments;
    private int D; //number of districts
    private int v; //number of voters
    private int r; //required number of voters to win a district
    private Electorate e;

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
        e = electorate;


        Stripe();

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

    // Partition district a and b into new districts, prioritizing the optimality of a
    private void Partition(int a, int b)
    {
        Partition p = new Partition(a, b); //create partitions

        // Apply partitions
        Districts[a] = p.P1;
        Districts[b] = p.P2;

        // Update lookup table
        for (int i = 0; i < D; i++)
        {
            DistrictLookup[p.P1[i]] = a;
            DistrictLookup[p.P2[i]] = b;
        }
    }
}
