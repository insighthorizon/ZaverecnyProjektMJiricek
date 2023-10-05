package mjiricek.spring.config;

import mjiricek.spring.models.DBService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

///**
// * This class defines how to instantiate and initialize
// * services with internal data and constants for controllers
// * - Allowing us to initialize everything though dependency injection
// * instead of relying on statics or over-specific initialization within constructor
// * - Each method returns an instance of an object or a primitive constant
// * to be injected
// */
//@Configuration
//public class DBConfiguration {
//    /**
//     * Creates and initializes instance of DBService.
//     * Since we are only simulating CRUD access to database,
//     * there is some initialisation with initial data entries.
//     * - for the DBController constructor
//     * @return instance of DBService to be injected in controller
//     */
//    @Bean
//    public DBService createDBService() {
//        DBService testTable = new DBService();
//        testTable.addEntry("rohliiiii iiiiiiiiii iiiiiiiik", "8 60 10");
//        for (char i = 'a'; i <= 'z'; i++) {
//            testTable.addEntry(String.valueOf(i), String.valueOf(i) + i + i + i + i + i);
//        }
//        testTable.addEntry("rohliiiiii ooooooo iiiiiiiik", "8 60 10");
//        for (char i = 'a'; i <= 'z'; i++) {
//            testTable.addEntry(String.valueOf(i), String.valueOf(i) + i + i + i + i + i);
//        }
//
//        return testTable;
//    }
//
//    /**
//     * - for the DBController constructor
//     * @return integer constant to be injected in controller
//     * as viewLength
//     */
//    @Bean
//    public int getViewLength() {
//        return 12;
//    }
//
//}
//
//
// DBController constructor - if we would actually use those beans
//    public DBController(@Qualifier("createDBService") DBService dbService,
//                        @Qualifier("getViewLength") int viewLength) {
//        this.dbService = dbService;
//        this.viewLength = viewLength;
//    }
