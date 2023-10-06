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

    /**
     * returns unique id of the entity
     * @return unique id
     */
    public int getEntryId() {
        return entryId;
    }

    /**
     * Constructor
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
     * Copy constructor
     * @param dbEntity to be copied
     */
    public DBEntity(DBEntity dbEntity) {
        // all the fields, including inherited are primitive, so we don't need to worry about copying them
        // this is already a deep copy, in this case
        this(dbEntity.getEntryId(), dbEntity.getEntryName(), dbEntity.getEntryContent());
    }

    /**
     * create and return copy of the entity
     * - this is used to expose the data (in a copy) to the controller layer without allowing modification
     * of the original data
     * @return copy of the original entity (equivalent to deep copy in this case)
     */
    public DBEntity copy() {
        return new DBEntity(this);
    }

    /**
     * needed in order for the class to implement Comparable interface
     * @param otherEntry the object to be compared.
     * @return 0 if x equals y; a negative value if x less than y; and positive value if if x greater than y
     * (from Integer.compare() javadoc)
     */
    @Override
    public int compareTo(DBEntity otherEntry) {
        return Integer.compare(getEntryId(), otherEntry.getEntryId());
    }
}
