package mjiricek.spring.models;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple wrapper class
 * "Database" needs to provide limited ammount of found results,
 * BUT at the same time it has to return number of ALL results that match the search criteria
 * - number of all results is needed for pagination in search page
 * - I need to copy only limited fixed ammount of entities, yet know how many there are
 * - alternative solutions are returning the integer and have arrayList as modified argument, Integer or int[0] as modified argument
 *    - those alternatives are ugly, by having a wrapper, everything is in the returned object
 */
public class SearchResultWrapper {
    /**
     * number of elements satisfying search criteria
     */
    public int sizeOfWholeResult;

    /**
     * limited list of elements satisfying search criteria (for example, we display max 10)
     */
    public CopyOnWriteArrayList<DBEntity> partialResult;
}
