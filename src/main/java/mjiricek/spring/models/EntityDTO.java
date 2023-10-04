package mjiricek.spring.models;

/**
 * Defines the "part" of the data in DBEntity (DBEntity extends EntityDTO) which is fully accessible to the client
 * - modifiable by the client
 * (user may see the id#, but can't change it, nor choose id when creating new entity)
 */
public class EntityDTO {

    private String entryName;
    private String entryContent;

    public String getEntryName() {
        return entryName;
    }
    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }
    public String getEntryContent() {
        return entryContent;
    }
    public void setEntryContent(String entryContent) {
        this.entryContent = entryContent;
    }

}
