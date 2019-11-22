public class DisjointSet
{
    private int[] set;
    private int capacity;
    public DisjointSet(int capacity)
    {
        this.capacity = capacity;
        set = new int[capacity];
        for (int i = 0; i < capacity; i++)
        {
            set[i] = i;
        }
    }

    public void Merge(int i, int j)
    {
        for (int k = 0; k < capacity; k++)
            if(set[k] == j) set[k] = i;
    }

    public int Count(int i)
    {
        int count = 0;
        for (int k = 0; k < capacity; k++)
            if(set[k] == i) count++;

        return count;
    }
}
