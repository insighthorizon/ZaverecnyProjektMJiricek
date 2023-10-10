package mjiricek.spring.models;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * This class provides controller with methods to operate the database
 * "business logic layer"
 * Class includes input validation
 * (ensuring that the values are meaningful, aka nonegative weight or calories,
 * parsing and parsing exceptions are in the competency of a controller class)
 * - This class should have no internal state on its own, except the reference to virtualDatabase
 * which should be immutable. So this class is supposed to be thread safe by default
 * TODO add input validation (ban negative mass, and so on)
 * Empty string names are allowed for testing purposses (it's quicker to not fill anything)
 */
@Service
public class DBService {

    /**
     * reference to the virtual database the service will work with
     */
    private final DBSimulator virtualDatabase;

    /**
     * Constructor
     * Instance of DBSimulator is given by springs dependency injection
     * - @Qualifier annotation to make the dependency injection explicit
     */
    public DBService(@Qualifier("createDBSimulator") DBSimulator dbSimulator) {
        this.virtualDatabase = dbSimulator;
    }

    /**
     * returns number of entries in the database
     * @return number of entries in DB
     */
    public int getDBSize() {
        return virtualDatabase.getTableSize();
    }

    /**
     * Returns number of ocurrences of an entry with a given name attribute value
     * we receive fewer elements than we asked for.
     * @param entryName name of the entry
     * @return number of ocurrences with a given name
     */
    public int howManyEntriesOfName(String entryName) {
        return virtualDatabase.getNameCount(entryName);
    }

    /**
     * adds new entry in DB
     * @param DBEntityDTO DTO with the attributes (entryName and entryContent) for the new db entry
     */
    public void addEntry(DBEntityDTO DBEntityDTO) {
        virtualDatabase.addEntity(DBEntityDTO);
    }

    /**
     * Finds and copies (avoid exposing original) entry with given id.
     * Relies on binary search.
     * @param id entry id
     * @return copy of the entry with desired id
     */
    public DBEntity showEntryById(int id) {
        return virtualDatabase.getEntityCopyById(id);
    }

    /**
     * Finds an entry by id and deletes it (if it exists)
     * @param id id of the deleted entry
     * @return true if entry found, false if not
     */
    public boolean deleteEntry(int id) {
        return virtualDatabase.deleteEntityById(id);
    }

    /**
     * Finds an entry by id and changes its attributes to provided values (if found)
     * @param id id of udpated entry
     * @param DBEntityDTO new entity contents
     * @return true if entry found, false if not
     */
    public boolean updateEntry(int id, DBEntityDTO DBEntityDTO) {
        return virtualDatabase.updateEntityById(id, DBEntityDTO);
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
        return virtualDatabase.getTableSubcopy(startIndex, copySize);
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
        return virtualDatabase.getTableSubcopy(entryName, startIndex, copySize);
    }

}
