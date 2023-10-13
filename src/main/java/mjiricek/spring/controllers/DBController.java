package mjiricek.spring.controllers;

import mjiricek.spring.models.entities.Food;
import mjiricek.spring.models.DBService;
import mjiricek.spring.models.entities.FoodData;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling GET, POST, PUT and DELETE http requests
 * TODO user input validation
 * - performs validation of the URL parameters (after TODO is done)
 */
@Controller
public class DBController {
    /**
     * service used by the controller
     * - we want all fields to be immutable, so dbService instance is passed by constructor
     */
    private final DBService dbService;

    /**
     * number of entries displayed in one page in browse card (one page in paging)
     */
    private final int pageLength; // can't be less than 1

    /**
     * read/write lock
     * There is need for synchronization (despite DBSimulator having synchronization already)
     * because get mappings perform multiple reading operations in sequence
     * and if the data changes during that, we may get race condition.
     * - we want to block access to data only when some thread is writing (deleting, updating, adding)
     * - to prevent reading when writting is going on in other thread
     * - to allow as many threads reading as possible when no writting happens
     */
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);


    /**
     * constructor (Spring uses it in dependency injection)
     *
     * @param pageLength
     * @param dbService
     */
    public DBController(@Qualifier("getPageLength") int pageLength,
                        @Autowired DBService dbService) {
        this.pageLength = pageLength;
        this.dbService = dbService;
    }

    /**
     * Helper method to
     *
     * @param index paging index
     * @param upperBound upper bound exclusive
     * @return new paging index value
     */
    private int adjustIndexOutOfBounds(int index, int upperBound) {
        if (index < 0) {
            index = 0;
        } else if (index >= upperBound) {
            index = upperBound - 1;
        }

        return index;
    }

    /**
     * Helper method to compute how many views will be in browse card
     *
     * @param numberOfEntitiesToBrowse number of entities that can be browsed through
     * @return number of views
     */
    private int computeNumberOfPages(int numberOfEntitiesToBrowse) {
        // intentionally truncating with integer division
        return numberOfEntitiesToBrowse == 0
                ? 1
                : (int) Math.ceil((double) numberOfEntitiesToBrowse / pageLength);
    }

    /**
     * Browse card contains paging
     * ! mutates Model and foodDTO arguments
     * fill in the data for the browse card (table view)
     *
     * @param pageContent
     * @param pageIndex
     * @param totalPages
     * @param model
     */
    private void setTemplateAttributes(ArrayList<Food> pageContent,
                                       int pageIndex,
                                       int totalPages,
                                       Model model,
                                       FoodData dBEntityDTO,
                                       String selectedID) {
        // set Browsing Card Attributes (fill in the data for the browsing card)
        // attribute names correspond to the variables used in thymeleaf templates
        model.addAttribute("entries", pageContent);
        model.addAttribute("viewIndex", pageIndex);
        model.addAttribute("numberOfViews", totalPages);
        // set Detail Card Attributes (fill in the data for the detail card)
        // DTO is also used in the template (for getting data from and to the client)
        if (selectedID != null /*&& !selectedID.equals("null") && !selectedID.equals("")*/) {
            model.addAttribute("displayDetail", true);

            try {
                Food foodCopy = dbService.showEntryById(Integer.parseInt(selectedID));
                if (foodCopy != null) {

                    model.addAttribute("selectedID", selectedID);
                    dBEntityDTO.setAllAttributes(foodCopy);
                }
            } catch (IllegalArgumentException e) {
                model.addAttribute("selectedID", "Illegal URL argument value");
            }

        }
    }

    /**
     * Handler of the GET request on the URL "/" (with url arguments)
     *
     * @param viewIndex value of url parameter reresenting in which view is being card displayed (paging)
     * @param model     Model parameter for data to be presented to the client
     * @return name of the html template being presented to the client
     */
    @GetMapping("/")
    public String renderIndexPage(@RequestParam(value = "view", defaultValue = "0") String viewIndex,
                                  @RequestParam(value = "id", required = false) String selectedID,
                                  @ModelAttribute("foodDTO") FoodData dBEntityDTO,
                                  Model model) {
        int pageIndex = Integer.parseInt(viewIndex); // TODO handle exception
        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            // handle paging
            int numberOfPages = computeNumberOfPages(dbService.getDBSize()); // find how many view cards we have depending on the VIEW_LENGTH and dBSize
            pageIndex = adjustIndexOutOfBounds(pageIndex, numberOfPages); // handle index out of bounds
            ArrayList<Food> shownEntries = dbService.showEntriesByIndexRange(pageIndex * pageLength, pageLength);
            // set variables accessed by the template
            setTemplateAttributes(shownEntries, pageIndex, numberOfPages, model, dBEntityDTO, selectedID);
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }
        return "views/index";
    }

    /**
     * Handler of the GET request on the URL "/" (with url arguments)
     *
     * @param viewIndex value of url parameter reresenting in which view is being card displayed (paging)
     * @param model     Model parameter for data to be presented to the client
     * @return name of the html template being presented to the client
     */
    @GetMapping("/search")
    public String renderSearchPage(@RequestParam(value = "view", defaultValue = "0") String viewIndex,
                                   @RequestParam(value = "id", required = false) String selectedID,
                                   @RequestParam(value = "searchedName", required = false) String searchedName,
                                   @ModelAttribute("foodDTO") FoodData dBEntityDTO,
                                   Model model) {
        int pageIndex = Integer.parseInt(viewIndex); // TODO handle exception
        int numberOfPages;
        ArrayList<Food> shownEntries;

        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            // paging depending on whether searchedName was given
            if (searchedName == null) { // no name to search was given
                numberOfPages = 1;
                pageIndex = 0; // will result 1/1 in pagination
                shownEntries = null;
            } else { // name to search by was given
                // find how many view cards we have depending on the VIEW_LENGTH and how many ocurrences of searched name there are
                numberOfPages = computeNumberOfPages(dbService.howManyEntriesOfName(searchedName));
                pageIndex = adjustIndexOutOfBounds(pageIndex, numberOfPages); // handle index out of bounds
                shownEntries = dbService.showEntriesByName(searchedName, pageIndex * pageLength, pageLength);
                model.addAttribute("searchedName", searchedName); // extra template attribute
            }

            setTemplateAttributes(shownEntries, pageIndex, numberOfPages, model, dBEntityDTO, selectedID);
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }
        return "views/search";
    }

    /**
     * Handler of the GET request on the URL "/" (with url arguments)
     *
     * @param model Model parameter for data to be presented to the client
     * @return name of the html template being presented to the client
     */
    @GetMapping("/create")
    public String renderCreatePage(@RequestParam(value = "id", required = false) String selectedID,
                                   @ModelAttribute("foodDTO") FoodData dBEntityDTO,
                                   Model model) {
        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            // handling paging
            int numberOfPages = computeNumberOfPages(dbService.getDBSize()); // find how many view cards we have depending on the VIEW_LENGTH and dBSize
            int pageIndex = numberOfPages - 1; // in create page, jump to the last entries in view
            ArrayList<Food> shownEntries = dbService.showEntriesByIndexRange(pageIndex * pageLength, pageLength);
            // filling in variables for the template
            model.addAttribute("displayDetail", true); // always display detail card for creating a new entry
            setTemplateAttributes(shownEntries, pageIndex, numberOfPages, model, dBEntityDTO, selectedID);
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }

        return "views/create";
    }

    /**
     * TODO handle "entry not found"
     *
     * @param viewIndex
     * @param selectedID
     * @param foodData
     * @param model
     * @return
     */
    @DeleteMapping("/")
    public String deleteAtIndexPage(@RequestParam(value = "view", defaultValue = "0") String viewIndex,
                                    @RequestParam(value = "id", required = false) String selectedID,
                                    @ModelAttribute("foodDTO") FoodData foodData,
                                    Model model) {
        rwLock.writeLock().lock(); // start of synchronized code block (write)
        try {
            dbService.deleteEntry(Integer.parseInt(selectedID)); // TODO handle exception
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }

        return renderIndexPage(viewIndex, selectedID, foodData, model);
    }

    /**
     * TODO handle "entry not found"
     *
     * @param viewIndex
     * @param selectedID
     * @param foodData
     * @param model
     * @return
     */
    @DeleteMapping("/search")
    public String deleteAtSearchPage(@RequestParam(value = "view", defaultValue = "0") String viewIndex,
                                     @RequestParam(value = "id", required = false) String selectedID,
                                     @RequestParam(value = "searchedName", required = false) String searchedName,
                                     @ModelAttribute("foodDTO") FoodData foodData,
                                     Model model) {
        rwLock.writeLock().lock(); // start of synchronized code block (write)
        try {
            dbService.deleteEntry(Integer.parseInt(selectedID)); // TODO handle exception
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }

        return renderSearchPage(viewIndex, selectedID, searchedName, foodData, model);
    }

    /**
     * TODO handle "entry not found"
     *
     * @param viewIndex
     * @param selectedID
     * @param foodData
     * @param model
     * @return
     */
    @PutMapping("/")
    public String updateAtIndex(@RequestParam(value = "view", defaultValue = "0") String viewIndex,
                                @RequestParam(value = "id", required = false) String selectedID,
                                @ModelAttribute("foodDTO") FoodData foodData,
                                Model model) {
        rwLock.writeLock().lock(); // start of synchronized code block (write)
        try {
            dbService.updateEntry(Integer.parseInt(selectedID), foodData);
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }

        return renderIndexPage(viewIndex, selectedID, foodData, model);
    }

    /**
     *
     * @param viewIndex
     * @param selectedID
     * @param searchedName
     * @param foodData
     * @param model
     * @return
     */
    @PutMapping("/search")
    public String updateAtSearch(@RequestParam(value = "view", defaultValue = "0") String viewIndex,
                                 @RequestParam(value = "id", required = false) String selectedID,
                                 @RequestParam(value = "searchedName", required = false) String searchedName,
                                 @ModelAttribute("foodDTO") FoodData foodData,
                                 Model model) {
        rwLock.writeLock().lock(); // start of synchronized code block (write)
        try {
            dbService.updateEntry(Integer.parseInt(selectedID), foodData);
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }

        return renderSearchPage(viewIndex, selectedID, searchedName, foodData, model);
    }

    /**
     * Creates new entity in the "database"
     *
     * @param foodData
     * @param model
     * @return
     */
    @PostMapping("/create")
    public String createEntry(@ModelAttribute("foodDTO") FoodData foodData,
                              Model model) {
        rwLock.writeLock().lock(); // start of synchronized code block (write)
        try {
            dbService.addEntry(foodData);
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }
        // next, we need to clean the DTO after the new entry has been saved
        // otherwise, the data will stay in the form
        foodData.setAllAttributes(new FoodData());

        // no detail of item should be displayed
        return renderCreatePage(null, foodData, model);
    }

}
