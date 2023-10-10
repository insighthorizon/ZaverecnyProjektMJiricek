package mjiricek.spring.models;

/**
 * Defines the "part" of the data in DBEntity (DBEntity extends EntityDTO) which is fully accessible to the client
 * - that this the "table row" without id
 * - modifiable by the client
 * (user may see the id#, but can't change it, nor choose id when creating new entity)
 */
public class DBEntityDTO {

    private String entityName;

    private String entityContent ;

    /**
     * default constructor
     */
    public DBEntityDTO() {
        this("","");
    }

    /**
     * constructor
     * @param entityName name of the entity
     * @param entityContent content of the entity
     */
    public DBEntityDTO(String entityName, String entityContent) {
        this.entityName = entityName;
        this.entityContent = entityContent;
    }

    /**
     * copy constructor
     * @param DBEntityDTO entity being copied
     */
    public DBEntityDTO(DBEntityDTO DBEntityDTO) {
        this(DBEntityDTO.getEntityName(), DBEntityDTO.entityContent);
    }

    /**
     * copy method
     */
    public DBEntityDTO copy() {
        return new DBEntityDTO(this);
    }

    public String getEntityName() {
        return entityName;
    }
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    public String getEntityContent() {
        return entityContent;
    }
    public void setEntityContent(String entityContent) {
        this.entityContent = entityContent;
    }

    /**
     * set's all DTO attributes by passing just one EntityDTO parameter
     * @param DBEntityDTO entity containing the new parameters
     */
    public void setAllAttributes(DBEntityDTO DBEntityDTO) {
        setEntityName(DBEntityDTO.getEntityName());
        setEntityContent(DBEntityDTO.getEntityContent());
    }

}
