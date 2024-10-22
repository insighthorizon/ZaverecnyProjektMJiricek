package mjiricek.spring.models;

import mjiricek.spring.models.entities.Food;
import mjiricek.spring.models.entities.FoodDTO;
import mjiricek.spring.models.entities.FoodData;
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
 * Empty string names are allowed for testing purposses (it's quicker to not fill anything)
 *
 */
@Service
public class DBService {

    /**
     * reference to the virtual database the service will work with
     */
    private final DBSimulator dbSimulator;

    /**
     * Constructor
     * Instance of DBSimulator is given by springs dependency injection
     * - @Qualifier annotation to make the dependency injection explicit
     */
    public DBService(@Qualifier("createDBSimulator") DBSimulator dbSimulator) {
        this.dbSimulator = dbSimulator;
    }

    /**
     * transform DTO into FoodData
     * - performs parsing and logical input validation (no negative weights)
     * @param foodDTO FoodDTO being transformed into FoodData
     * @return FoodData parsed from validated FoodDTO
     */
    public FoodData dTOToFood(FoodDTO foodDTO) throws IllegalArgumentException {
        // parsing
        try {
            // parsing from string to double
            double kcal = Double.parseDouble(foodDTO.getKcalContent());
            double prot = Double.parseDouble(foodDTO.getProteinContent());
            double carb = Double.parseDouble(foodDTO.getCarbContent());
            double fat = Double.parseDouble(foodDTO.getFatContent());

            // validating that weight is not negative
            if (kcal < 0 || prot < 0 || carb < 0 || fat < 0) {
                throw new IllegalArgumentException();
            }

            return new FoodData(foodDTO.getFoodName(), kcal, prot, carb, fat);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nutrient values must be non-negative numbers.");
        }
    }

    /**
     * returns number of entries in the database
     * @return number of entries in DB
     */
    public int getDBSize() {
        return dbSimulator.getTableSize();
    }

    /**
     * Returns number of ocurrences of an entry with a given name attribute value
     * we receive fewer elements than we asked for.
     * @param entryName name of the entry
     * @return number of ocurrences with a given name
     */
    public int howManyEntriesOfName(String entryName) {
        return dbSimulator.getNameCount(entryName);
    }

    /**
     * adds new entry in DB
     * @param foodDTO DTO with the attributes (entryName and entryContent) for the new db entry
     */
    public void addEntry(FoodDTO foodDTO) throws IllegalArgumentException {
        FoodData foodData = dTOToFood(foodDTO);
        dbSimulator.addEntity(foodData);
    }

    /**
     * Finds and copies (avoid exposing original) entry with given id.
     * Relies on binary search.
     * @param id entry id
     * @return copy of the entry with desired id
     */
    public Food showEntryById(Integer id) {
        if (id != null && id >= 0) // active prevention of nonsense
            return dbSimulator.getEntityCopyById(id);

        return null;
    }

    /**
     * Finds an entry by id and deletes it (if it exists)
     * @param id id of the deleted entry
     * @return true if entry found, false if not
     */
    public boolean deleteEntry(Integer id) {
        if (id != null && id >= 0) // active prevention of nonsense
            return dbSimulator.deleteEntityById(id);

        return false;
    }

    /**
     * Finds an entry by id and changes its attributes to provided values (if found)
     * @param id id of udpated entry
     * @param foodDTO new entity contents
     * @return true if entry found, false if not
     */
    public boolean updateEntry(Integer id, FoodDTO foodDTO) throws IllegalArgumentException {
        if (id != null && id >= 0) { // active prevention of nonsense
            FoodData foodData = dTOToFood(foodDTO);
            return dbSimulator.updateEntityById(id, foodData);
        }

        return false;
    }

    /**
     * Copies entries within given index range.
     * If the index range reaches out of arraylist indices,
     * the copying still happens for the valid part of the index range.
     * - This means that when we input start or end out of bounds,
     * we receive fewer elements than we asked for.
     * @param startIndex index where to start the copy, inclusive
     * @param copySize   requested length of the copy
     * @return partial copy of the table given by the range
     */
    public ArrayList<Food> showEntriesByIndexRange(int startIndex, int copySize) {
        if (startIndex < 0) // active prevention of nonsense
            startIndex = 0;
        if (copySize <= 0)
            copySize = 1;

        return dbSimulator.getTableSubcopy(startIndex, copySize);
    }

    /**
     * Search and copy entries with given name. The copy is also restricted by start and end indices.
     * If the index range reaches out of arraylist indices,
     * the copying still happens for the valid part of the index range.
     * - This means that when we input start or end out of bounds,
     * @param entryName  name to search by
     * @param startIndex index where to start the copy, inclusive
     * @param copySize   requested length of the copy
     * @return list of found entries
     */
    public ArrayList<Food> showEntriesByName(String entryName, int startIndex, int copySize) {
        if (startIndex < 0) // active prevention of nonsense
            startIndex = 0;
        if (copySize <= 0)
            copySize = 1;

        return dbSimulator.getTableSubcopy(entryName, startIndex, copySize);
    }

    /**
     * custom toString method for general debugging purposes
     * - returns text representation of DBService instance
     * @return text representation of DBService instance
     */
    @Override
    public String toString() {
        // String.format("%n") is portable, "\n" is not
        return String.format("Printout of DBService " + super.toString() +":%n" +
                "=======================================================%n" +
                "___Contained in DBService:%n" +
                dbSimulator.toString());
    }

}
