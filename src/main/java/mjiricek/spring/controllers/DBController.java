package mjiricek.spring.controllers;

import mjiricek.spring.models.DBEntity;
import mjiricek.spring.models.DBService;
import mjiricek.spring.models.EntityDTO;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling GET, POST, PUT and DELETE http requests
 */
@Controller
public class DBController {
    /**
     * service used by the controller
     * - instance passed by dependency injection
     */
    @Autowired
    private DBService dbService;

    /**
     * number of entries displayed in one view card
     */
    private static final int VIEW_LENGTH = 10; // can't be less than 1


    /**
     * Helper method to prevent paging (in the browse card) index out of bounds
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
     * @param numberOfEntitiesToBrowse number of entities that can be browsed through
     * @return number of views
     */
    private int computeNumberOfViews(int numberOfEntitiesToBrowse) {
        // intentionally truncating with integer division
        return numberOfEntitiesToBrowse == 0
                ? 1
                : (int) Math.ceil( (double)numberOfEntitiesToBrowse / VIEW_LENGTH );
    }

    /**
     * TODO user input validation
     * Handler of the GET request on the URL "/" (with url arguments)
     * @param viewIndex value of url parameter reresenting in which view is being card displayed (paging)
     * @param model Model parameter for data to be presented to the client
     * @return name of the html template being presented to the client
     */
    @GetMapping({"/", "/{searchOrCreate}"})
    public String renderPage(@PathVariable(required = false) String searchOrCreate,
                             @RequestParam(value = "view", defaultValue = "0") int viewIndex,
                             @RequestParam(value = "id", required = false) String id,
                             @RequestParam(value = "searchedName", required = false) String searchedName,
                             @ModelAttribute EntityDTO entityDTO,
                             Model model) {
        // local variables initialized with default values (they are normally reassigned later)
        CopyOnWriteArrayList<DBEntity> shownEntries = null; // get the entities of table from db
        int numberOfViews = 1; // for pagination
        String templateToRender = null; // template name that will be returned by this method

        // following logic decides how to render the table and detail view for all valid pages (browse, search, create)
        // conditions for how to fill in the above variables (based on URL path and one URL argument)
        if (searchOrCreate == null) { // "/" url path
            templateToRender = "index"; // index page will be rendered
            numberOfViews = computeNumberOfViews(dbService.getDBSize()); // find how many view cards we have depending on the VIEW_LENGTH and dBSize
            viewIndex = adjustIndexOutOfBounds(viewIndex, numberOfViews); // handle index out of bounds
            shownEntries = dbService.showEntriesByIndexRange(viewIndex * VIEW_LENGTH, VIEW_LENGTH);

        } else if (searchOrCreate.equals("search")) { // "/search" url path
            templateToRender = "search"; // search page will be rendered
            if (searchedName == null) { // no name to search was given
                viewIndex = 0; // will result 1/1 in pagination
            }
            else { // name to search by was given
                // find how many view cards we have depending on the VIEW_LENGTH and how many ocurrences of searched name there are
                numberOfViews = computeNumberOfViews(dbService.howManyEntriesOfName(searchedName));
                viewIndex = adjustIndexOutOfBounds(viewIndex, numberOfViews); // handle index out of bounds
                shownEntries = dbService.showEntriesByName(searchedName, viewIndex * VIEW_LENGTH, VIEW_LENGTH);
                model.addAttribute("searchedName", searchedName);
            }
        } else if (searchOrCreate.equals("create")) { // "/create" url path
            templateToRender = "create"; // create page will be rendered
            numberOfViews = computeNumberOfViews(dbService.getDBSize()); // find how many view cards we have depending on the VIEW_LENGTH and dBSize
            viewIndex = numberOfViews - 1; // in create page, jump to the last entries in view
            shownEntries = dbService.showEntriesByIndexRange(viewIndex * VIEW_LENGTH, VIEW_LENGTH);
            model.addAttribute("displayDetail", true); // always display detail card for creating a new entry
        } // else no condition is met - wrong url address

        // fill in the data for the browse card (table view)
        model.addAttribute("entries", shownEntries);
        model.addAttribute("viewIndex", viewIndex);
        model.addAttribute("numberOfViews", numberOfViews);

        // fill in the data for the detail card
        if (id != null && !id.equals("null") && !id.equals("")) {
            DBEntity dbEntityCopy = dbService.showEntryById(Integer.parseInt(id));
            if (dbEntityCopy != null) {
                model.addAttribute("displayDetail", true);
                model.addAttribute("selectedID", id);
                entityDTO.setEntryName(dbEntityCopy.getEntryName());
                entityDTO.setEntryContent(dbEntityCopy.getEntryContent());
            }
        }


        return templateToRender;
    }

    /**
     * TODO handle "entry not found"
     * @param searchOrCreate
     * @param viewIndex
     * @param id
     * @param selectedEntryDTO
     * @param model
     * @return
     */
    @DeleteMapping({"/", "/{searchOrCreate}"})
    public String deleteEntry(@PathVariable(required = false) String searchOrCreate,
                              @RequestParam(value = "view", defaultValue = "0") int viewIndex,
                              @RequestParam(value = "id", required = false) String id,
                              @RequestParam(value = "entryName", required = false) String searchedName,
                              @ModelAttribute EntityDTO selectedEntryDTO,
                              Model model) {
        // for which url paths is this operation allowed
        if (searchOrCreate == null || searchOrCreate.equals("search"))
            dbService.deleteEntry(Integer.parseInt(id));

        return renderPage(searchOrCreate, viewIndex, id, searchedName, selectedEntryDTO, model);
    }

    /**
     * TODO handle "entry not found"
     * @param searchOrCreate
     * @param viewIndex
     * @param id
     * @param selectedEntryDTO
     * @param model
     * @return
     */
    @PutMapping({"/", "/{searchOrCreate}"})
    public String updateEntry(@PathVariable(required = false) String searchOrCreate,
                              @RequestParam(value = "view", defaultValue = "0") int viewIndex,
                              @RequestParam(value = "id", required = false) String id,
                              @RequestParam(value = "entryName", required = false) String searchedName,
                              @ModelAttribute EntityDTO selectedEntryDTO,
                              Model model) {
        // for which url paths is this operation allowed
        if (searchOrCreate == null || searchOrCreate.equals("search"))
            dbService.updateEntry(Integer.parseInt(id), selectedEntryDTO.getEntryName(), selectedEntryDTO.getEntryContent());

        return renderPage(searchOrCreate, viewIndex, id, searchedName, selectedEntryDTO, model);
    }

    /**
     * Creates new entity in the "database"
     * @param newEntryDTO
     * @param model
     * @return
     */
    @PostMapping("/create")
    public String createEntry(@ModelAttribute EntityDTO newEntryDTO,
                              Model model) {

        dbService.addEntry(newEntryDTO);
        // next, we need to clean the DTO after the new entry has been saved
        // otherwise, the data will stay in the form
        newEntryDTO.setEntryName("");
        newEntryDTO.setEntryContent("");

        // create page should display the end of the table, that's why MAX_VALUE
        // no detail of item should be displayed
        return renderPage("create", Integer.MAX_VALUE, null, null, newEntryDTO, model);
    }

}
