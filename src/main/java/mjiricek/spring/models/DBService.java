package mjiricek.spring.models;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Model class implementing a table of a virtual database together with
 * actions that can be performed on that table.
 * This class makes sure that internal entries have unique entryId.
 * Entries are always sorted by entryId.
 */
@Service
public class DBService {
    /**
     * id for the next entry to be added to database
     */
    private int nextId = 0;
    /**
     * Entities are stored in thread safe ArrayList
     */
    private final CopyOnWriteArrayList<DBEntity> tableData = new CopyOnWriteArrayList<>();

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
     * @return number of entries in DB
     */
    public int getDBSize() {
        return tableData.size();
    }

    /**
     * adds new entry in DB
     * @param entryName entry attribute
     * @param entryContent entry attribute
     */
    public void addEntry(String entryName, String entryContent) {
        tableData.add(new DBEntity(nextId, entryName, entryContent));
        nextId++;
    }

    /**
     * Finds entry with given id. Relies on binary search.
     * @param id unique id of the entry
     * @return found entry (can be null)
     */
    private DBEntity findEntryById(int id) {
        int index = Collections.binarySearch(tableData, new DBEntity(id, null, null));
        return (index >= 0) ? tableData.get(index) : null;
        //JAK SERAZOVAT nebo vyhledavat DLE DANEHO ATRIBUTU - ASC/DESC:
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
        DBEntity originalEntry = findEntryById(id);

        if (originalEntry == null)
            return null;

        return new DBEntity(originalEntry.getEntryId(),
                originalEntry.getEntryName(),
                originalEntry.getEntryContent());
    }

    /**
     * Finds and copies (avoid exposing original) entry with given id
     * @param index index of entry in the arraylist (not id)
     * @return entry
     */
    public DBEntity showEntryByIndex(int index) {
        DBEntity originalEntry = tableData.get(index);

        if (originalEntry == null)
            return null;

        return new DBEntity(originalEntry.getEntryId(),
                originalEntry.getEntryName(),
                originalEntry.getEntryContent());
    }

    /**
     * Finds an entry by id and deletes it (if it exists)
     * @param id id of the deleted entry
     * @return true if entry found, false if not
     */
    public boolean deleteEntry(int id) {
        return tableData.remove(findEntryById(id));
    }

    /**
     * Finds an entry by id and changes its attributes to provided values (if found)
     * @param id id of udpated entry
     * @param entryName new name of the entry
     * @param entryContent new content of the entry
     * @return true if entry found, false if not
     */
    public boolean updateEntry(int id, String entryName, String entryContent) {
        DBEntity modifiedEntry = findEntryById(id);
        if (modifiedEntry == null)
            return false;

        modifiedEntry.setEntryName(entryName);
        modifiedEntry.setEntryContent(entryContent);
        return true;
    }

    /**
     * Copies entries within given index range.
     * If the index range reaches out of arraylist indices,
     * the copying still happens for the valid part of the index range.
     * - This means that when we input start or end out of bounds,
     * we receive fewer elements thatn we asked for.
     * @param start inclusive
     * @param end exclusive
     * @return partial copy of the table given by the range
     */
    public CopyOnWriteArrayList<DBEntity> showEntriesByIndexRange(int start, int end) {
        CopyOnWriteArrayList<DBEntity> tableDataPartialCopy = new CopyOnWriteArrayList<>();

        if (start < getDBSize()) { // can't copy anything starting out of index bound
            // prevent index out of bounds
            if (start < 0)
                start = 0;
            if (end > getDBSize())
                end = getDBSize();

            for (int i = start; i < end; i++) {
                tableDataPartialCopy.add(showEntryByIndex(i));
            }
        }

        return tableDataPartialCopy;
    }

    /**
     * Search and copy entries with given name.
     * Goes through the entire arraylist with linear search.
     * (data is not ordered by name)
     * ? We could maintain extra hashmap, mapping name to list of indices to entries.
     * @param entryName name to search by
     * @return list of found entries
     */
    public CopyOnWriteArrayList<DBEntity> showEntriesByName(String entryName) {
        CopyOnWriteArrayList<DBEntity> tableDataPartialCopy = new CopyOnWriteArrayList<>();

        for (DBEntity entry : tableData) {
            if (entry.getEntryName().equals(entryName)) {
                tableDataPartialCopy.add(showEntryByIndex(tableData.indexOf(entry)));
            }
        }

        return tableDataPartialCopy;
    }

}
