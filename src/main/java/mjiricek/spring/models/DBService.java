package mjiricek.spring.models;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Model class implementing a table of a virtual database together with
 * actions that can be performed on that table.
 * This class makes sure that internal entries have unique entryId.
 * Entries are always sorted by entryId.
 */
@Service
public class DBService {
    /**
     * id for the next entry to be added to database - for the sake of generating unique id for each entity
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
     * @param entityDTO DTO with the attributes (entryName and entryContent) for the new db entry
     */
    public void addEntry(EntityDTO entityDTO) {
        tableData.add(new DBEntity(nextId, entityDTO.getEntryName(), entityDTO.getEntryContent()));
        nextId++;
    }

    /**
     * add new intry into the DB
     * @param name name attribute of the new entry
     * @param content content attribute of the new entry
     */
    public void addEntry(String name, String content) {
        tableData.add(new DBEntity(nextId, name, content));
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

        return originalEntry.copy();
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
     * we receive fewer elements than we asked for.
     * @param startIndex index where to start the copy, inclusive
     * @param copySize requested length of the copy
     * @return partial copy of the table given by the range
     */
    public CopyOnWriteArrayList<DBEntity> showEntriesByIndexRange(int startIndex, int copySize) {
        CopyOnWriteArrayList<DBEntity> tableDataPartialCopy = new CopyOnWriteArrayList<>();
        int dbSize = getDBSize();

        if (startIndex < dbSize && copySize > 0) { // prevent nonsense
            if (startIndex < 0) // can't start below zero
                startIndex = 0;
            if (startIndex + copySize > dbSize) // cant end beyond dbSize
                copySize = dbSize - startIndex;

            for (int i = startIndex; i < copySize; i++) {
                tableDataPartialCopy.add(tableData.get(i).copy());
            }
        } // else TODO throw exception about nonsensical arguments

        return tableDataPartialCopy;
    }

    /**
     * Returns number of ocurrences of an entry with a given name attribute value
     * we receive fewer elements than we asked for.
     * @param entryName name of the entry
     * @return number of ocurrences with a given name
     */
    public int howManyEntriesOfName(String entryName) {
        return (int)tableData.stream().filter(x -> x.getEntryName().equals(entryName)).count();
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
    public CopyOnWriteArrayList<DBEntity> showEntriesByName(String entryName, int startIndex, int copySize) {
        CopyOnWriteArrayList<DBEntity> entriesWithNamePartialCopy = new CopyOnWriteArrayList<>();
        int dbSize = getDBSize();

        if (startIndex < dbSize && copySize > 0) { // prevent nonsense
            if (startIndex < 0) // can't start below zero
                startIndex = 0;
            if (startIndex + copySize > dbSize) // cant end beyond dbSize
                copySize = dbSize - startIndex;

            // references to the entries of the given name, within requested index range
            CopyOnWriteArrayList<DBEntity> tableDataPartialCopy = tableData.stream()
                    .filter(x -> x.getEntryName().equals(entryName))
                    .skip(startIndex)
                    .limit(copySize)
                    .collect(Collectors.toCollection(CopyOnWriteArrayList::new));

            // creating copy of list for presentation (breaking the references to original entries)
            for (DBEntity entry : tableDataPartialCopy) {
                entriesWithNamePartialCopy.add(entry.copy());
            }

        } // else TODO throw exception about nonsensical arguments

        return entriesWithNamePartialCopy;
    }

}
