package prog10;

import java.util.*;


public class OpenHashTable<K, V> extends AbstractMap<K, V> {
    private static class Entry<K, V> implements Map.Entry<K, V> {
	K key;
	V value;

        public K getKey () { return key; }
        public V getValue () { return value; }
        public V setValue (V value) { return this.value = value; }

	Entry (K key, V value) {
	    this.key = key;
	    this.value = value;
	}
    }

    private final static int DEFAULT_CAPACITY = 5;
    private Entry<K,V>[] table = new Entry[DEFAULT_CAPACITY];
    private Entry<K,V> DELETED = new Entry<K,V>(null, null);
    private int size;
    private int nonNull;

    private int hashIndex (Object key) {
	int index = key.hashCode() % table.length;
	if (index < 0)
	    index += table.length;
	return index;
    }

    // Linear probe sequence: start at hashIndex(key) and increment,
    // but go back to zero at the end of the table.

    // Return the index of the Entry with key if it is in the probe
    // sequence.

    // If it is not there, return the index where the Entry with key
    // should be inserted.  If there is a deleted Entry in the probe
    // sequence, return the index of the *first* deleted Entry in the
    // sequence.

    // Otherwise return the index of the first null in the probe
    // sequence.
    
    private int find (Object key) {
        // IMPLEMENT
      	int index = hashIndex(key);
      	int firstCone = -1;
      	while(table[index] != null && (table[index] == DELETED || !key.equals(table[index].key))){
      		//while there's a collision & (that index has a traffic cone OR that index doesn't have right person)
      		if(table[index] == DELETED && firstCone == -1)
     			 firstCone = index;    	
      		if(index == table.length-1) {
      			index = 0;
      		}
      		index = (index + 1) % table.length;
      		//mod because we might have to roll over to the beginning when at end of the array
      	}
      	if(table[index] != null || firstCone == -1) {
      		//if it exists or the index of the first null
      		return index;
      	}
      	else {
      		return firstCone;
      	}
      }

    public boolean containsKey (Object key) {
    	Entry<K,V> entry = table[find(key)];
    	return entry != null && entry != DELETED;
    }

    public V get (Object key) {
    	Entry<K,V> entry = table[find(key)];
    	if (entry == null || entry == DELETED)
    	    return null;
    	return entry.value;
    }

    public V put (K key, V value) {
    	System.out.println("put " + key + " " + value + " hash index " + hashIndex(key));
    	int index = find(key);
    	Entry<K,V> entry = table[index];
    	if (entry != null && entry != DELETED) {
    		return entry.setValue(value);
    	}
    	if (entry == null) {
    		nonNull++;
    	}
        table[index] = new Entry<K,V>(key, value);
        size++;
        if (nonNull > table.length / 2)
           rehash(4 * size);
        return null;
    }

    public V remove (Object key) {
    	System.out.println("remove " + key + " hash index " + hashIndex(key));
    	int index = find(key);
    	Entry<K,V> entry = table[index];
    	if (entry == null || entry == DELETED)
    	    return null;
    	table[index] = DELETED;
    	size--;
    	return entry.value;    
    }

    private void rehash (int newCapacity) {
        //IMPLEMENT
    	Entry<K,V>[] oldTable = table;
    	table = new Entry[newCapacity];
    	size = 0;
    	nonNull = 0;
    	for (Entry<K,V> entry : oldTable) {
    		if(entry != null && entry != DELETED) {
    			//the old[i] isn't null & there isn't a traffic cone
    			put(entry.key, entry.value);
    		}
    		
    	}
    }

    private Iterator<Map.Entry<K, V>> entryIterator () {
      return new EntryIterator();
    }

    private class EntryIterator implements Iterator<Map.Entry<K, V>> {
      // EXERCISE
    	
    	int index;
    	
    	public EntryIterator() {
    		index = 0;
    		while (index < table.length && (table[index]==null || table[index] == DELETED)) {
    			index++;
    		}
    	}

      public boolean hasNext () {
        // EXERCISE
    	  if (index >= table.length || table[index] == DELETED || table[index] == null) {
    		  return false;
    	  }
        return true;
      }

      public Map.Entry<K, V> next () {
        // EXERCISE
    	  if (!hasNext()) {
    		  throw new NoSuchElementException();
    	  }
    	  int current = index;
    	  index++;
    	  while( index < table.length && (table[index] == null || table[index] == DELETED)) {
    		  index++;
    	  }
        return table[current];
      }

      public void remove () {}
    }

    public Set<Map.Entry<K,V>> entrySet() { return new EntrySet(); }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
      public int size() { return size; }

      public Iterator<Map.Entry<K, V>> iterator () {
        return entryIterator();
      }

      public void remove () {}
    }

    public String toString () {
    	String ret = "--------------------------------\n";
    	for (int i = 0; i < table.length; i++) {
    	    ret = ret + i + ": ";
    	    Entry<K,V> entry = table[i];
    	    if (entry == null)
    		ret = ret + "null\n";
    	    else if (entry == DELETED)
    		ret = ret + "DELETED\n";
    	    else
    		ret = ret + entry.key + " " + entry.value + "\n";
    	}
    	return ret;
        }

        public static void main (String[] args) {
    	OpenHashTable<String, Integer> table =
    	    new OpenHashTable<String, Integer>();

    	table.put("Brad", 46);
    	System.out.println(table);
    	table.put("Hal", 10);
    	System.out.println(table);
    	table.put("Kyle", 6);
    	System.out.println(table);
    	table.put("Lisa", 43);
    	System.out.println(table);
    	table.put("Lynne", 43);
    	System.out.println(table);
    	table.put("Victor", 46);
    	System.out.println(table);
    	table.put("Zoe", 6);
    	System.out.println(table);
    	table.put("Zoran", 76);
    	System.out.println(table);

            for (String key : table.keySet())
              System.out.print(key + " ");
            System.out.println();

    	table.remove("Zoe");
    	System.out.println(table);
    	table.remove("Kyle");
    	System.out.println(table);
    	table.remove("Brad");
    	System.out.println(table);
    	table.remove("Zoran");
    	System.out.println(table);
    	table.remove("Lisa");
    	System.out.println(table);
    	table.remove("Hal");
    	System.out.println(table);
    	table.remove("Lynne");
    	System.out.println(table);

    	table.put("Ant", 3);
    	System.out.println(table);
    	table.remove("Ant");
    	System.out.println(table);
    	table.put("Bug", 1);
    	System.out.println(table);
    	table.remove("Bug");
    	System.out.println(table);
    	table.put("Cat", 4);
    	System.out.println(table);
    	table.remove("Cat");
    	System.out.println(table);
    	table.put("Dog", 1);
    	System.out.println(table);
    	table.remove("Dog");
    	System.out.println(table);
    	table.put("Eel", 5);
    	System.out.println(table);
    	table.remove("Eel");
    	System.out.println(table);
    	table.put("Fox", 9);
    	System.out.println(table);
    	table.remove("Fox");
    	System.out.println(table);
    	table.put("Gnu", 2);
    	System.out.println(table);
    	table.remove("Gnu");
    	System.out.println(table);

    	table.put("Hen", 2);
    	System.out.println(table);
    	table.remove("Hen");
    	System.out.println(table);
    	table.put("Jay", 2);
    	System.out.println(table);
    	table.remove("Jay");
    	System.out.println(table);
    	table.put("Owl", 2);
    	System.out.println(table);
    	table.remove("Owl");
    	System.out.println(table);
    	table.put("Pig", 2);
    	System.out.println(table);
    	table.remove("Pig");
    	System.out.println(table);
    	table.put("Rat", 2);
    	System.out.println(table);
    	table.remove("Rat");
    	System.out.println(table);
    	table.put("Yak", 2);
    	System.out.println(table);
    	table.remove("Yak");
    	System.out.println(table);
      }
}
