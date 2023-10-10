package mjiricek.spring.config;

import mjiricek.spring.models.DBEntityDTO;
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

        dbSimulator.addEntity(new DBEntityDTO("rohliiiii iiiiiiiiii iiiiiiiik", "8 60 10"));
        for (char i = 'a'; i <= 'z'; i++) {
            dbSimulator.addEntity(new DBEntityDTO(String.valueOf(i), String.valueOf(i) + i + i + i + i + i));
        }
        dbSimulator.addEntity(new DBEntityDTO("rohliiiiii ooooooo iiiiiiiik", "8 60 10"));
        for (char i = 'a'; i <= 'z'; i++) {
            dbSimulator.addEntity(new DBEntityDTO(String.valueOf(i), String.valueOf(i) + i + i + i + i + i));
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


