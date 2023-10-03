package mjiricek.spring.models;

/**
 * Class represents database entry (entity)
 * - contains attributes of primitive and String data types
 * with their getters and setters
 * (not worth of writing javadoc for each separately)
 * - implements the Comparable interface for allowing binary search
 */
public class DBEntity extends EntityDTO implements Comparable<DBEntity> {
    /**
     * entryId is supposed to be unique and unchangeable
     */
    private final int entryId;

    public int getEntryId() {
        return entryId;
    }

    /**
     * constructor
     * @param entryId unique entry id (entry attribute)
     * @param entryName entry attribute
     * @param entryContent entry attribute
     */
    public DBEntity(int entryId, String entryName, String entryContent) {
        this.entryId = entryId;
        setEntryContent(entryContent);
        setEntryName(entryName);
    }

    /**
     * needed in order for the class to implement Comparable interface
     * @param otherEntry the object to be compared.
     * @return "the value 0 if x == y; a value less than 0 if x < y; and a value greater than 0 if x > y"
     * (from Integer.compare() javadoc)
     */
    @Override
    public int compareTo(DBEntity otherEntry) {
        return Integer.compare(getEntryId(), otherEntry.getEntryId());
    }
}
