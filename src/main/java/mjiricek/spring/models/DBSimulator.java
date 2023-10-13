package mjiricek.spring.models;

import mjiricek.spring.models.entities.Food;
import mjiricek.spring.models.entities.FoodData;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * This class represents a simulated/virtual database table of entities.
 * It has to only store data CREATED AT RUNTIME and LOST AT TERMINATION of the application,
 * and it has to support various CRUD operations (db queries) that could be performed with a real database.
 * - it contains mutable state (and will be instantiated by spring in singleton scope),
 * yet has to be thread safe (the CRUD operations have to be atomic)
 * - that requires synchronization (prevention of interleaved read/write from multiple threads)
 * - I've chosen ReentrantReadWriteLock as my approach to achieve thread safety
 */
@Repository
public class DBSimulator {
    /**
     * unique entity id counter
     * id for the next entry to be added to database - for the sake of generating unique id for each entity
     * This class makes sure that internal entries have unique entryId.
     * Entries are always sorted by entryId.
     * using volatile keyword to prevent caching
     * AtomicInteger could be used for the sake of thread safety, but we need to implement
     * thread safety on higher level anyway.
     * (Need for locking all DBSErvice fields at once for some entire blocks of code)
     */
    private volatile int nextId = 0;

    /**
     * Entities are stored in an ArrayList
     * A thread safe version CopyOnWriteArrayList could be used, but we need to implement
     * thread safety on higher level anyway.
     * (Need for locking all DBSErvice fields at once for some entire blocks of code)
     */
    private final ArrayList<Food> nutritionalDBTable = new ArrayList<>();

    /**
     * read/write lock
     * - we want to block access to data only when some thread is writing (deleting, updating, adding)
     * - to prevent reading when writting is going on in other thread
     * - to allow as many threads reading as possible when no writting happens
     */
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    /**
     * returns number of entries in the table
     * @return number of entries in DB
     */
    public int getTableSize() {
        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            return nutritionalDBTable.size(); // get the data
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }
    }

    /**
     * Returns number of ocurrences of an entry with a given name attribute value
     * we receive fewer elements than we asked for.
     * @param entryName name of the entry
     * @return number of ocurrences with a given name
     */
    public int getNameCount(String entryName) {
        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            return (int) nutritionalDBTable.stream().filter(x -> x.getFoodName().equals(entryName)).count();
        } finally {
            rwLock.readLock().unlock(); // end of synchronzied code block (read)
        }
    }

    /**
     * adds new entity in DB
     * @param FoodData DTO with the attributes (entryName and entryContent) for the new db entry
     */
    public void addEntity(FoodData FoodData) {
        rwLock.writeLock().lock(); // start of sychronized code block (write)
        try { // add the new entry
            nutritionalDBTable.add(new Food(nextId, FoodData));
            nextId++; // unique id counter incrementation - warning about non-atomicity is ok since non-atomic operations are performed inside of synchronization block
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }
    }

    /**
     * Finds entry with given id. Relies on binary search.
     * @param id unique id of the entry
     * @return found entry (can be null)
     */
    private Food findEntityById(int id) {
        rwLock.readLock().lock(); // start of synchronized code block (read)
        try { // find and return the id
            int index = Collections.binarySearch(nutritionalDBTable, new Food(id, new FoodData()));
            return (index >= 0) ? nutritionalDBTable.get(index) : null;
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }
        //JAK SERAZOVAT nebo vyhledavat dle jakehokoliv atributu tabulky - ASC/DESC:
        //tableData.sort(Comparator.comparing(DBEntity::getEntryName).reversed());
        //tableData.sort(Comparator.comparing(DBEntity::getEntryName));
        //System.out.println(Collections.binarySearch(tableData,new DBEntity(0,"rohliiiiii ooooooo iiiiiiiik", "") ,Comparator.comparing(DBEntity::getEntryName)));
        //int index = Collections.binarySearch(tableData, new DBEntity(id, null, null), Comparator.comparing(DBEntity::getEntryId));
    }

    /**
     * Finds and copies (avoid exposing original) entry with given id.
     * Relies on binary search.
     * @param id entry id
     * @return copy of the entry with desired id
     */
    public Food getEntityCopyById(int id) {
        rwLock.readLock().lock();  // start of synchronized code block (read)
        try { // try to find the entity
            Food originalEntry = findEntityById(id);

            if (originalEntry == null)
                return null;

            return originalEntry.copy(); // create copy and return it
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }
    }

    /**
     * Finds an entry by id and deletes it (if it exists)
     * @param id id of the deleted entry
     * @return true if entry found, false if not
     */
    public boolean deleteEntityById(int id) {
        rwLock.writeLock().lock();  // start of synchronized code block (write)
        try { // try to delete the entity
            return nutritionalDBTable.remove(findEntityById(id));
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }
    }

    /**
     * Finds an entry by id and changes its attributes to provided values (if found)
     * @param id id of an udpated entry
     * @param FoodData new attribute values of the entry
     * @return true if entry found, false if not
     */
    public boolean updateEntityById(int id, FoodData FoodData) {
        rwLock.writeLock().lock();  // start of synchronized code block (write)
        try { // try to update the entity
            Food modifiedEntity = findEntityById(id);
            if (modifiedEntity == null)
                return false;

            modifiedEntity.setAllAttributes(FoodData);
            return true;
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }
    }

    /**
     * Copies entries within given index range.
     * If the index range reaches out of arraylist indices,
     * the copying still happens for the valid part of the index range.
     * - This means that when we input start or end out of bounds,
     * we receive fewer elements than we asked for.
     * @param startIndex index where to start the copy, inclusive
     * @param maxLength   requested length of the copy
     * @return partial copy of the table given by the range
     */
    public ArrayList<Food> getTableSubcopy(int startIndex, int maxLength) {
        rwLock.readLock().lock();  // start of synchronized code block (read)
        try {
            return nutritionalDBTable.stream()
                    .skip(startIndex)
                    .limit(maxLength)
                    .map(Food::copy) // creating copy - breaking the references to original entries
                    .collect(Collectors.toCollection(ArrayList::new));
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }
    }


    /**
     * Search and copy entries with given name. The copy is also restricted by start and end indices.
     * If the index range reaches out of arraylist indices,
     * the copying still happens for the valid part of the index range.
     * - This means that when we input start or end out of bounds,
     * @param entryName  name to search by
     * @param startIndex index where to start the copy, inclusive
     * @param maxLength requested length of the copy
     * @return list of found entries
     */
    public ArrayList<Food> getTableSubcopy(String entryName, int startIndex, int maxLength) {
        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            return nutritionalDBTable.stream()
                    .filter(x -> x.getFoodName().equals(entryName)) // find entries with the required name
                    .skip(startIndex)
                    .limit(maxLength)
                    .map(Food::copy) // creating copy - breaking the references to original entries
                    .collect(Collectors.toCollection(ArrayList::new));
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }
    }

}
