package mjiricek.spring.controllers;

import mjiricek.spring.models.entities.Food;
import mjiricek.spring.models.DBService;
import mjiricek.spring.models.entities.FoodDTO;

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
     * @param index      paging index
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
                                       FoodDTO dBEntityDTO,
                                       Integer selectedID) {
        // set Browsing Card attributes (fill in the templane variables for the browsing card)
        model.addAttribute("entries", pageContent);
        model.addAttribute("viewIndex", pageIndex);
        model.addAttribute("numberOfViews", totalPages);

        // set Detail Card attributes (fill in the template variables for the detail card)
        // DTO is also used in the template (for getting data from and to the client)
        if (selectedID != null) {
            Food foodCopy = dbService.showEntryById(selectedID);
            if (foodCopy != null) {
                model.addAttribute("displayDetail", true);
                model.addAttribute("selectedID", selectedID);
                dBEntityDTO.setAllAttributes(foodCopy);
            } else {
                model.addAttribute("operationStatus", "Entity with id "
                        + selectedID + " wasn't found");
            }

        }
    }

    /**
     * make sure that URL parameters contain numbers
     * mutates model in the case that validation fails
     *
     * @param pageIndex
     * @param selectedID
     * @param model
     * @return
     */
    private Integer[] validateURLParameters(String pageIndex, String selectedID, Model model) {
        Integer[] validatedURLParameters = new Integer[2];

        try {
            try {
                validatedURLParameters[0] = Integer.parseInt(pageIndex);
                if (validatedURLParameters[0] < 0)
                    validatedURLParameters[0] = 0;
            } catch (IllegalArgumentException e) {
                validatedURLParameters[0] = 0;
                throw new IllegalArgumentException();
            }

            try {
                if (selectedID != null && !selectedID.equals("null") && !selectedID.equals(""))
                    validatedURLParameters[1] = Integer.parseInt(selectedID);
                else
                    validatedURLParameters[1] = null;
            } catch (IllegalArgumentException e) {
                validatedURLParameters[1] = null;
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("uRLParameterError", "Illegal URL argument value");
        }

        return validatedURLParameters;
    }

    /**
     * Handler of the GET request on the URL "/" (with url arguments)
     * (index page is browse page)
     *
     * @param pageIndex value of url parameter indicating which page in browsing card is displayed (paging)
     * @param model     Model parameter for data to be presented to the client
     * @return name of the html template being presented to the client
     */
    @GetMapping("/")
    public String renderIndexPage(@RequestParam(value = "view", defaultValue = "0") String pageIndex,
                                  @RequestParam(value = "id", required = false) String selectedID,
                                  @ModelAttribute("foodDTO") FoodDTO dBEntityDTO,
                                  Model model) {
        // validate that parameters are valid numbers
        // first element is pageIndex, second is selectedID
        Integer[] validatedURLParameters = validateURLParameters(pageIndex, selectedID, model);

        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            // handle paging
            int numberOfPages = computeNumberOfPages(dbService.getDBSize()); // find how many view cards we have depending on the VIEW_LENGTH and dBSize
            validatedURLParameters[0] = adjustIndexOutOfBounds(validatedURLParameters[0], numberOfPages); // handle index out of bounds
            ArrayList<Food> shownEntries = dbService.showEntriesByIndexRange(validatedURLParameters[0] * pageLength, pageLength);
            // set variables accessed by the template
            setTemplateAttributes(shownEntries, validatedURLParameters[0], numberOfPages, model, dBEntityDTO, validatedURLParameters[1]);
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }
        return "views/index";
    }

    /**
     * Handler of the GET request on the URL "/" (with url arguments)
     *
     * @param pageIndex value of url parameter reresenting in which view is being card displayed (paging)
     * @param model     Model parameter for data to be presented to the client
     * @return name of the html template being presented to the client
     */
    @GetMapping("/search")
    public String renderSearchPage(@RequestParam(value = "view", defaultValue = "0") String pageIndex,
                                   @RequestParam(value = "id", required = false) String selectedID,
                                   @RequestParam(value = "searchedName", required = false) String searchedName,
                                   @ModelAttribute("foodDTO") FoodDTO dBEntityDTO,
                                   Model model) {
        // validate that parameters are valid numbers
        // first element is pageIndex, second is selectedID
        Integer[] validatedURLParameters = validateURLParameters(pageIndex, selectedID, model);
        int numberOfPages;
        ArrayList<Food> shownEntries;

        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            // paging depending on whether searchedName was given
            if (searchedName == null) { // no name to search was given
                numberOfPages = 1;
                validatedURLParameters[0] = 0; // will result 1/1 in pagination
                shownEntries = null;
            } else { // name to search by was given
                // find how many view cards we have depending on the VIEW_LENGTH and how many ocurrences of searched name there are
                numberOfPages = computeNumberOfPages(dbService.howManyEntriesOfName(searchedName));
                validatedURLParameters[0] = adjustIndexOutOfBounds(validatedURLParameters[0], numberOfPages); // handle index out of bounds
                shownEntries = dbService.showEntriesByName(searchedName, validatedURLParameters[0] * pageLength, pageLength);
                model.addAttribute("searchedName", searchedName); // extra template attribute
            }

            setTemplateAttributes(shownEntries, validatedURLParameters[0], numberOfPages, model, dBEntityDTO, validatedURLParameters[1]);
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
                                   @ModelAttribute("foodDTO") FoodDTO dBEntityDTO,
                                   Model model) {
        // validate that parameters are valid numbers
        // first element is pageIndex, second is selectedID
        Integer[] validatedURLParameters = validateURLParameters("0", selectedID, model);

        rwLock.readLock().lock(); // start of synchronized code block (read)
        try {
            // handling paging
            int numberOfPages = computeNumberOfPages(dbService.getDBSize()); // find how many view cards we have depending on the VIEW_LENGTH and dBSize
            int pageIndex = numberOfPages - 1; // in create page, jump to the last entries in view
            ArrayList<Food> shownEntries = dbService.showEntriesByIndexRange(pageIndex * pageLength, pageLength);
            // filling in variables for the template
            model.addAttribute("displayDetail", true); // always display detail card for creating a new entry
            setTemplateAttributes(shownEntries, pageIndex, numberOfPages, model, dBEntityDTO, validatedURLParameters[1]);
        } finally {
            rwLock.readLock().unlock(); // end of synchronized code block (read)
        }

        return "views/create";
    }

    /**
     * Attempts to delete entry from the database
     * - performs mutation of model
     *
     * @param selectedID
     * @param model
     */
    private void delete(String selectedID, Model model) {
        // validate that parameters are valid numbers
        // first element is pageIndex, second is selectedID
        Integer[] validatedURLParameters = validateURLParameters("0", selectedID, model);

        rwLock.writeLock().lock(); // start of synchronized code block (write)
        try {
            if (dbService.deleteEntry(validatedURLParameters[1]))
                model.addAttribute("operationStatus", "Entity with id "
                        + selectedID + " deleted");
            else
                model.addAttribute("operationStatus",
                        "Attempt to delete entity with non-existent id " + selectedID);
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }
    }

    /**
     * @param pageIndex
     * @param selectedID
     * @param foodDTO
     * @param model
     * @return
     */
    @DeleteMapping("/")
    public String deleteAtIndexPage(@RequestParam(value = "view", defaultValue = "0") String pageIndex,
                                    @RequestParam(value = "id", required = false) String selectedID,
                                    @ModelAttribute("foodDTO") FoodDTO foodDTO,
                                    Model model) {
        delete(selectedID, model); // mutates model
        // selectedID changes to null because it has been deleted (if it even existed before)
        return renderIndexPage(pageIndex, null, foodDTO, model);
    }

    /**
     * @param pageIndex
     * @param selectedID
     * @param foodDTO
     * @param model
     * @return
     */
    @DeleteMapping("/search")
    public String deleteAtSearchPage(@RequestParam(value = "view", defaultValue = "0") String pageIndex,
                                     @RequestParam(value = "id", required = false) String selectedID,
                                     @RequestParam(value = "searchedName", required = false) String searchedName,
                                     @ModelAttribute("foodDTO") FoodDTO foodDTO,
                                     Model model) {
        delete(selectedID, model); // mutates model
        // selectedID changes to null because it has been deleted (if it even existed)
        return renderSearchPage(pageIndex, null, searchedName, foodDTO, model);
    }

    /**
     * attempts to update database entity
     * - mutates model
     *
     * @param selectedID
     * @param foodDTO
     * @param model
     */
    private void update(String selectedID, FoodDTO foodDTO, Model model) {
        // validate that parameters are valid numbers
        // first element is pageIndex, second is selectedID
        Integer[] validatedURLParameters = validateURLParameters("0", selectedID, model);

        rwLock.writeLock().lock(); // start of synchronized code block (write)
        try {
            if (dbService.updateEntry(validatedURLParameters[1], foodDTO))
                model.addAttribute("operationStatus", "Entity with id "
                        + selectedID + " updated");
            else
                model.addAttribute("operationStatus",
                        "Attempt to update entity with non-existent id " + selectedID);
        } catch (IllegalArgumentException e) {
            model.addAttribute("inputError", e.getMessage()); // add client message about failed input validation
        }
        finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }
    }

    /**
     * @param pageIndex
     * @param selectedID
     * @param foodDTO
     * @param model
     * @return
     */
    @PutMapping("/")
    public String updateAtIndexPage(@RequestParam(value = "view", defaultValue = "0") String pageIndex,
                                    @RequestParam(value = "id", required = false) String selectedID,
                                    @ModelAttribute("foodDTO") FoodDTO foodDTO,
                                    Model model) {
        update(selectedID, foodDTO, model);

        return renderIndexPage(pageIndex, selectedID, foodDTO, model);
    }

    /**
     * TODO handle "entry not found" (indication whether update was succesful to the client)
     *
     * @param viewIndex
     * @param selectedID
     * @param searchedName
     * @param foodDTO
     * @param model
     * @return
     */
    @PutMapping("/search")
    public String updateAtSearchPage(@RequestParam(value = "view", defaultValue = "0") String viewIndex,
                                     @RequestParam(value = "id", required = false) String selectedID,
                                     @RequestParam(value = "searchedName", required = false) String searchedName,
                                     @ModelAttribute("foodDTO") FoodDTO foodDTO,
                                     Model model) {
        update(selectedID, foodDTO, model);

        return renderSearchPage(viewIndex, selectedID, searchedName, foodDTO, model);
    }

    /**
     * Creates new entity in the "database"
     *
     * @param foodDTO
     * @param model
     * @return
     */
    @PostMapping("/create")
    public String createEntry(@ModelAttribute("foodDTO") FoodDTO foodDTO,
                              Model model) {
        rwLock.writeLock().lock(); // start of synchronized code block (write)
        try {
            dbService.addEntry(foodDTO);
            model.addAttribute("operationStatus", "New entry created with the id "
                    + dbService.showEntriesByIndexRange(dbService.getDBSize() - 1, 1).get(0).getFoodID());
        } catch (IllegalArgumentException e) {
            model.addAttribute("inputError", e.getMessage()); // add client message about failed input validation
        } finally {
            rwLock.writeLock().unlock(); // end of synchronized code block (write)
        }
        // next, we need to clean the DTO after the new entry has been saved
        // otherwise, the data will stay in the form
        foodDTO.resetAllAttributes();

        // no detail of item should be displayed
        return renderCreatePage(null, foodDTO, model);
    }

}
