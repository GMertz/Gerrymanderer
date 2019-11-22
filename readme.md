## Pseudo Code
1. Stripe
   * Put each row into disjoint sets
   * initialize bool array and vector 
2. For each set (i)
   * Merge with set i+1
   * **Partition** set into two sets
   * Mark i'th set as optimized 
3. Return 

---

## Bookkeeping 
Assume there are D districts with v = D*D voters
1. A v-length bit vector
2. D disjoint sets
3. d-length boolean array

---

### Partition
* Merge sets a&b
* Find first black node b in a
* DFS from b to find optimized district of length D
* Check if 'other set' is valid (has a connection to the next set)
* Put everything attached to b in set a, everything else in b

 
