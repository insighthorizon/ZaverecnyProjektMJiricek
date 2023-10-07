package mjiricek.spring.models;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Model class implementing a table of a virtual database together with
 * actions that can be performed on that table.
 * Class includes input validation
 * (ensuring that the values are meaningful, aka nonegative weight or calories,
 * parsing and parsing exceptions are in the competency of a controller class)
 * This class makes sure that internal entries have unique entryId.
 * Entries are always sorted by entryId.
 * This class contains mutable fields, but Spring will instantiate it in singleton scope
 * and spring processes multiple http requests in parallel.
 * - that requires synchronization (prevention of interleaved read/write from multiple threads)
 * - I've chosen ReentrantReadWriteLock as a solution
 */
@Service
public class DBService {
    /**
     * unique entity id counter
     * id for the next entry to be added to database - for the sake of generating unique id for each entity
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
    private final ArrayList<DBEntity> tableData = new ArrayList<>();

    /**
     * read/write lock
     * - we want to allow only on thread writing (deleting, updating, adding)
     * - to prevent reading when writting is going on in other thread
     * - to allow as many threads reading as possible when no writting happens
     */
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    /**
     * Constructor
     * Since we are only simulating CRUD access to database,
     * there is some initialisation with initial test data.
     */
    public DBService() {
        addEntry("rohliiiii iiiiiiiiii iiiiiiiik", "8 60 10");
        for (char i = 'a'; i <= 'z'; i++) {
            addEntry(String.valueOf(i), String.valueOf(i) + i + i + i + i + i);
        }
        addEntry("rohliiiiii ooooooo iiiiiiiik", "8 60 10");
        for (char i = 'a'; i <= 'z'; i++) {
            addEntry(String.valueOf(i), String.valueOf(i) + i + i + i + i + i);
        }
    }

    /**
     * returns number of entries in the database
     * @return number of entries in DB
     */
    public int getDBSize() {
        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            return tableData.size(); // get the data
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }
    }

    /**
     * adds new entry in DB
     * @param entityDTO DTO with the attributes (entryName and entryContent) for the new db entry
     */
    public void addEntry(EntityDTO entityDTO) {
        rwLock.writeLock().lock(); // start of sychronized code block (write)
        try { // add the new entry
            tableData.add(new DBEntity(nextId, entityDTO.getEntryName(), entityDTO.getEntryContent()));
            nextId++; // unique id counter incrementation
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }
    }

    /**
     * add new intry into the DB
     * @param name name attribute of the new entry
     * @param content content attribute of the new entry
     */
    public void addEntry(String name, String content) {
        rwLock.writeLock().lock();  // start of synchronized code block (write)
        try { // add the new entity
            tableData.add(new DBEntity(nextId, name, content)); // add the new entry
            nextId++; // unique id counter incrementation
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }
    }

    /**
     * Finds entry with given id. Relies on binary search.
     * @param id unique id of the entry
     * @return found entry (can be null)
     */
    private DBEntity findEntryById(int id) {
        rwLock.readLock().lock(); // start of synchronized code block (read)
        try { // find and return the id
            int index = Collections.binarySearch(tableData, new DBEntity(id, null, null));
            return (index >= 0) ? tableData.get(index) : null;
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
    public DBEntity showEntryById(int id) {
        rwLock.readLock().lock();  // start of synchronized code block (read)
        try { // try to find the entity
            DBEntity originalEntry = findEntryById(id);

            if (originalEntry == null)
                return null;

            return originalEntry.copy();
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }
    }

    /**
     * Finds an entry by id and deletes it (if it exists)
     * @param id id of the deleted entry
     * @return true if entry found, false if not
     */
    public boolean deleteEntry(int id) {
        rwLock.writeLock().lock();  // start of synchronized code block (write)
        try { // try to delete the entity
            return tableData.remove(findEntryById(id));
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }
    }

    /**
     * Finds an entry by id and changes its attributes to provided values (if found)
     * @param id id of udpated entry
     * @param entryName new name of the entry
     * @param entryContent new content of the entry
     * @return true if entry found, false if not
     */
    public boolean updateEntry(int id, String entryName, String entryContent) {
        rwLock.writeLock().lock();  // start of synchronized code block (write)
        try { // try to update the entity
            DBEntity modifiedEntry = findEntryById(id);
            if (modifiedEntry == null)
                return false;

            modifiedEntry.setEntryName(entryName);
            modifiedEntry.setEntryContent(entryContent);
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
     * @param copySize requested length of the copy
     * @return partial copy of the table given by the range
     */
    public ArrayList<DBEntity> showEntriesByIndexRange(int startIndex, int copySize) {
        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            ArrayList<DBEntity> tableDataPartialCopy = new ArrayList<>();
            int dbSize = getDBSize();

            if (startIndex < dbSize && copySize > 0) { // prevent nonsense
                if (startIndex < 0) // can't start below zero
                    startIndex = 0;
                if (startIndex + copySize > dbSize) // cant end beyond dbSize
                    copySize = dbSize - startIndex;

                for (int i = startIndex; i < startIndex + copySize; i++) {
                    tableDataPartialCopy.add(tableData.get(i).copy());
                }
            } // else TODO throw exception about nonsensical arguments

            return tableDataPartialCopy;
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
    public int howManyEntriesOfName(String entryName) {
        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            return (int) tableData.stream().filter(x -> x.getEntryName().equals(entryName)).count();
        } finally {
            rwLock.readLock().unlock(); // end of synchronzied code block (read)
        }
    }

    /**
     * Search and copy entries with given name. The copy is also restricted by start and end indices.
     * If the index range reaches out of arraylist indices,
     * the copying still happens for the valid part of the index range.
     * - This means that when we input start or end out of bounds,
     * @param entryName name to search by
     * @param startIndex index where to start the copy, inclusive
     * @param copySize requested length of the copy
     * @return list of found entries
     */
    public ArrayList<DBEntity> showEntriesByName(String entryName, int startIndex, int copySize) {
        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            ArrayList<DBEntity> entriesWithNamePartialCopy = new ArrayList<>();

            int numberOfNameOcurrences = howManyEntriesOfName(entryName);

            if (startIndex < numberOfNameOcurrences && copySize > 0) { // prevent nonsense
                if (startIndex < 0) // can't start below zero
                    startIndex = 0;
                if (startIndex + copySize > numberOfNameOcurrences) // cant end beyond dbSize
                    copySize = numberOfNameOcurrences - startIndex;

                // references to the entries of the given name, within requested index range
                ArrayList<DBEntity> tableDataPartialCopy = tableData.stream()
                        .filter(x -> x.getEntryName().equals(entryName))
                        .skip(startIndex)
                        .limit(copySize)
                        .collect(Collectors.toCollection(ArrayList::new));

                // creating copy of list for presentation (breaking the references to original entries)
                for (DBEntity entry : tableDataPartialCopy) {
                    entriesWithNamePartialCopy.add(entry.copy());
                }

            } // else TODO throw exception about nonsensical arguments

            return entriesWithNamePartialCopy;
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }
    }

}
