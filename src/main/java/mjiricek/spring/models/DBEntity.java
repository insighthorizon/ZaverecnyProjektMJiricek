package mjiricek.spring.models;

/**
 * Class represents database entry (entity / one row of a table)
 * - contains attributes of primitive and String data types
 * with their getters and setters
 * (not worth of writing javadoc for each separately)
 * - implements the Comparable interface for allowing binary search
 */
public class DBEntity extends DBEntityDTO implements Comparable<DBEntity> {
    /**
     * entryId is supposed to be unique and unchangeable
     */
    private final int entityId;

    /**
     * returns unique id of the entity
     * @return unique id
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * Constructor
     * @param entityId unique entry id (entry attribute)
     * @param DBEntityDTO object with all the other attributes
     */
    public DBEntity(int entityId, DBEntityDTO DBEntityDTO) {
        super(DBEntityDTO); // calling copy constructor of the parent class
        this.entityId = entityId;
    }

    /**
     * Copy constructor (short definition - thanks to the parent class already having a copy constructor)
     * @param dbEntity to be copied
     */
    public DBEntity(DBEntity dbEntity) {
        // all the fields, including inherited are primitive, so we don't need to worry about copying them
        // this is already a deep copy, in this case
        this(dbEntity.getEntityId(), dbEntity);
    }

    /**
     * create and return copy of the entity
     * - this is used to expose the data (in a list) to the outside of the module without allowing modification
     * of the original data
     * @return copy of the original entity (equivalent to deep copy in this case)
     */
    @Override
    public DBEntity copy() {
        return new DBEntity(this);
    }

    /**
     * needed in order for the class to implement Comparable interface
     * @param otherEntity the object to be compared.
     * @return 0 if x equals y; a negative value if x less than y; and positive value is if x greater than y
     * (from Integer.compare() javadoc)
     */
    @Override
    public int compareTo(DBEntity otherEntity) {
        return Integer.compare(getEntityId(), otherEntity.getEntityId());
    }
}
