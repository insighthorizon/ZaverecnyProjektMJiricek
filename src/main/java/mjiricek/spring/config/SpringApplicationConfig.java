package mjiricek.spring.config;

import mjiricek.spring.models.entities.FoodData;
import mjiricek.spring.models.DBSimulator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class defines instantiation and initialization of those objects in applications which
 * contain internal state (mutable or immutable) and not just methods
 * - Allowing us to initialize everything though dependency injection
 * instead of hard-coding those states into the classes of those objects themselves
 * - Each method returns an instance of an object or a primitive constant
 * to be injected
 */

@Configuration
public class SpringApplicationConfig {
    /**
     * Creates and initializes instance of DBSimulator.
     * Since we are only simulating CRUD access to database,
     * there is some initialization with initial data entries.
     * - for the DBService constructor
     * @return instance of DBService to be injected in controller
     */
    @Bean
    public DBSimulator createDBSimulator() {
        DBSimulator dbSimulator = new DBSimulator();

        // innitial data
        dbSimulator.addEntity(new FoodData("white roll", 310, 9.78, 57.47, 3.68));
        dbSimulator.addEntity(new FoodData("bread", 244, 8, 45, 1.1));
        dbSimulator.addEntity(new FoodData("chicken egg", 151, 12.38, 0.94, 10.87));
        // TODO add more foods

        for (char i = 'A'; i <= 'z'; i++) {
            dbSimulator.addEntity(new FoodData((String.valueOf(i) + i + i + i + i + i), i, i, i, i));
        }

        return dbSimulator;
    }

    /**
     * - for the DBController constructor
     * @return integer constant to be injected in controller
     * as pageLength
     */
    @Bean
    public int getPageLength() {
        return 10;
    }

}

// Those beans are automatically used for dependency injection, but we can explicitly specify which bean should be injected where:
// this might be necessary if we have more beans of the same type and Spring doesn't know which one to use
// It's done by using annotation @Qualifier for constructor parameter:
//public Controller(@Qualifier("getPageLength") int pageLength,
//                  @Autowired DBService dbService) {
//    this.pageLength = pageLength;
//    this.dbService = dbService;
//}
//public DBService(@Qualifier("createDBSimulator") DBSimulator dbSimulator) {
//    this.virtualDatabase = dbSimulator;
//}


