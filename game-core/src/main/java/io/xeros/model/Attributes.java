package io.xeros.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A map for easily storing and accessing miscellaneous variables.
 * 
 * Be aware when using this class that if you use set(), setBoolean(), setInt() (for example)
 * you must use the corresponding get(), getBoolean(), getInt() or you will not get the intended results.
 * 
 * @author unknown
 * @author Michael Sasse
 *
 */
public class Attributes {
	
	/**
	 * Object map.
	 */
	private final Map<String, Object> attributes = new HashMap<>();
	/**
	 * Integer map.
	 */
	private final Map<String, Integer> intMap = new HashMap<>();
	/**
	 * Double map.
	 */
	private final Map<String, Double> doubleMap = new HashMap<>();
	/**
	 * Boolean map.
	 */
	private final Map<String, Boolean> booleanMap = new HashMap<>();
	/**
	 * Long map.
	 */
	private final Map<String, Long> longMap = new HashMap<>();
	/**
	 * String map.
	 */
	private final Map<String, String> stringMap = new HashMap<>();
	/**
	 * List map.
	 */
	private final Map<String, List<?>> listMap = new HashMap<>();
	/**
	 * List map.
	 */
	private final Map<String, HashSet<?>> hashSetMap = new HashMap<>();

	/**
	 * Create a new Attributes instance.
	 */
	public Attributes() {}

	/**
	 * Gets an attribute from the map.
	 * 
	 * @param key
	 *            The key of the attribute
     */
	public Object get(Object key) {
		return attributes.get(key);
	}

	/**
	 * Gets an attribute
	 * 
	 * @param key
	 *            The key of the attribute
	 * @param fail
	 *            return fail if not present
     */
	public Object get(Object key, Object fail) {
		Object value = attributes.get(key);
		if (value == null) {
			return fail;
		}
		return value;
	}
	
	/**
	 * Sets an attribute with a key and value
	 * 
	 * @param key
	 *            The key of the attribute
	 * @param value
	 *            The value of an attribute
	 */
	public void set(String key, Object value) {
		attributes.remove(key);

		attributes.put(key, value);
	}
	
	/**
	 * Gets if an attribute is added
	 * 
	 * @param key
	 *            The key to check if eixists
     */
	public boolean contains(Object key) {
		return attributes.containsKey(key);
	}

	/**
	 * Removes an attribute from the map
	 *
     */
	public void remove(Object key) {
		attributes.remove(key);
	}

	/**
	 * Set an Integer on the map.
	 * @param key
	 * 			the key.
	 * @param set
	 * 			the value.
	 */
	public void setInt(String key, int set) {
		intMap.put(key, set);
	}
	
	/**
	 * Remove a int from the map
	 * @param key
	 * 			the key.
	 */
	public void removeInt(String key) {
		intMap.remove(key);
	}
	
	/**
	 * Get an Integer from the map.
	 * @param key
	 * 			the key.
	 * @return
	 * 			the value if any, -1 if not present.
	 */
	public int getInt(String key) {
		return intMap.getOrDefault(key, -1);
	}
	
	/**
	 * Get an Integer from the map.
	 * @param key
	 * 			the key.
	 * @return
	 * 			the value if any, default value if not present.
	 */
	public int getInt(String key, int fail) {
		return intMap.getOrDefault(key, fail);
	}
	
	/**
	 * Does the map contain the key.
	 * @param key
	 * 			the key
	 * @return
	 * 		if the map contains the key
	 */
	public boolean containsInt(String key) {
		return intMap.containsKey(key);
	}
	
	/**
	 * Set an Double on the map.
	 * @param key
	 * 			the key.
	 * @param set
	 * 			the value.
	 */
	public void setDouble(String key, double set) {
		doubleMap.put(key, set);
	}
	
	/**
	 * Remove a double from the map
	 * @param key
	 * 			the key.
	 */
	public void removeDouble(String key) {
		doubleMap.remove(key);
	}
	
	/**
	 * Get an Double from the map.
	 * @param key
	 * 			the key.
	 * @return
	 * 			the value if any, -1 if not present.
	 */
	public double getDouble(String key) {
		return doubleMap.getOrDefault(key, -1d);
	}
	
	/**
	 * Get an Double from the map.
	 * @param key
	 * 			the key.
	 * @return
	 * 			the value if any, default value if not present.
	 */
	public double getDouble(String key, double fail) {
		return doubleMap.getOrDefault(key, fail);
	}
	
	/**
	 * Does the map contain the key.
	 * @param key
	 * 			the key
	 * @return
	 * 		if the map contains the key
	 */
	public boolean containsDouble(String key) {
		return doubleMap.containsKey(key);
	}
	
	/**
	 * Set an Boolean on the map.
	 * @param key
	 * 			the key.
	 * @param set
	 * 			the value.
	 */
	public void setBoolean(String key, boolean set) {
		booleanMap.put(key, set);
	}
	
	/**
	 * If the boolean is true set to false, if false set to true
	 * @param key
	 * 			the boolean key
	 */
	public boolean flipBoolean(String key) {
        setBoolean(key, !getBoolean(key));
		return getBoolean(key);
	}
	
	/**
	 * Remove a boolean from the map
	 * @param key
	 * 			the key.
	 */
	public void removeBoolean(String key) {
		booleanMap.remove(key);
	}
	
	/**
	 * Get an Boolean from the map.
	 * @param key
	 * 			the key.
	 * @return
	 * 			the value if any, default value if not present.
	 */
	public boolean getBoolean(String key) {
		return booleanMap.getOrDefault(key, false);
	}
	
	/**
	 * Get an Boolean from the map.
	 * @param key
	 * 			the key.
	 * @return
	 * 			the value if any, default value if not present.
	 */
	public boolean getBoolean(String key, boolean fail) {
		return booleanMap.getOrDefault(key, fail);
	}
	
	/**
	 * Does the map contain the key.
	 * @param key
	 * 			the key
	 * @return
	 * 		if the map contains the key
	 */
	public boolean containsBoolean(String key) {
		return booleanMap.containsKey(key);
	}
	
	/**
	 * Set an Long on the map.
	 * @param key
	 * 			the key.
	 * @param set
	 * 			the value.
	 */
	public void setLong(String key, long set) {
		longMap.put(key, set);
	}
	
	/**
	 * Remove a long from the map
	 * @param key
	 * 			the key.
	 */
	public void removeLong(String key) {
		longMap.remove(key);
	}
	
	/**
	 * Get an Long from the map.
	 * @param key
	 * 			the key.
	 * @return
	 * 			the value if any, -1 if not present.
	 */
	public long getLong(String key) {
		return longMap.getOrDefault(key, -1L);
	}
	
	/**
	 * Get an Long from the map.
	 * @param key
	 * 			the key.
	 * @return
	 * 			the value if any, default value if not present.
	 */
	public long getLong(String key, long fail) {
		return longMap.getOrDefault(key, fail);
	}
	
	/**
	 * Does the map contain the key.
	 * @param key
	 * 			the key
	 * @return
	 * 		if the map contains the key
	 */
	public boolean containsLong(String key) {
		return longMap.containsKey(key);
	}
	
	/**
	 * Set an String on the map.
	 * @param key
	 * 			the key.
	 * @param set
	 * 			the value.
	 */
	public void setString(String key, String set) {
		stringMap.put(key, set);
	}
	
	/**
	 * Remove a string from the map
	 * @param key
	 * 			the key.
	 */
	public void removeString(String key) {
		stringMap.remove(key);
	}
	
	/**
	 * Get an String from the map.
	 * @param key
	 * 			the key.
	 * @return
	 * 			the value if any, null if not present.
	 */
	public String getString(String key) {
		return stringMap.getOrDefault(key, null);
	}

	/**
	 * Get an String from the map.
	 * @param key
	 * 			the key.
	 * @return
	 * 			the value if any, default value if not present.
	 */
	public String getString(String key, String fail) {
		return stringMap.getOrDefault(key, fail);
	}

	public List<?> getList(String key) {
		return listMap.get(key);
	}

	public List<?> getList(String key, List fail) {
		return listMap.getOrDefault(key, fail);
	}

	public void setList(String key, List list) {
		listMap.put(key, list);
	}

	public HashSet<?> getHashSet(String key) {
		return hashSetMap.get(key);
	}

	public void setHashSet(String key, HashSet hashSet) {
		hashSetMap.put(key, hashSet);
	}

	/**
	 * Does the map contain the key.
	 * @param key
	 * 			the key
	 * @return
	 * 		if the map contains the key
	 */
	public boolean containsString(String key) {
		return stringMap.containsKey(key);
	}
}
