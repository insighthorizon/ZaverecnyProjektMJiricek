package mjiricek.spring.controllers;

import mjiricek.spring.models.DBEntity;
import mjiricek.spring.models.DBService;
import mjiricek.spring.models.EntityDTO;
import mjiricek.spring.models.QueryMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CopyOnWriteArrayList;

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
    private static final int VIEW_LENGTH = 10;


    int adjustIndexOutOfBounds(int index, int upperBound) {
        if (index < 0) {
            index = 0;
        } else if (index >= upperBound) {
            index = upperBound - 1;
        }

        return index;
    }

    int divideAndRoundUp(int dividend, int divisor) {
        return (int) Math.ceil((double) dividend / divisor);
    }

    CopyOnWriteArrayList<DBEntity> getEntriesByIndexOrName(QueryMode queryMode, String nameOfSearched, int viewIndex, int viewLength) {
        // switch is overkill, and we could just check nameOfSearched for null - but we may implement more ways to get data from DB in the database
        // (searching according to different attributes, sorting and so on)
        switch (queryMode) {
            case GET_BY_INDEX -> { return dbService.showEntriesByIndexRange(viewIndex * viewLength, viewLength); }
            case SEARCH_BY_NAME -> { return dbService.showEntriesByName(nameOfSearched, viewIndex * viewLength, viewLength); }
        }
        return null;
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
                             @RequestParam(value = "entryName", required = false) String nameOfSearched,
                             @ModelAttribute EntityDTO entityDTO,
                             Model model) {
        CopyOnWriteArrayList<DBEntity> shownEntries = null; // table data to fill in before presentation to client
        int numberOfViews = 1; // for pagination
        String templateToRender = null; // template name that will be returned by this method
        QueryMode queryMode = null; // way to retrieve displayed data

        // conditions for how to fill in the above variables (based on URL path and one URL argument)
        if (searchOrCreate == null) { // "/" url path
            templateToRender = "index"; // index page will be rendered
            queryMode = QueryMode.GET_BY_INDEX; // data for the browsing card is retriewed through plain indexing
            numberOfViews = divideAndRoundUp(dbService.getDBSize(), VIEW_LENGTH); // find how many view cards we have depending on the VIEW_LENGTH and dBSize
            viewIndex = adjustIndexOutOfBounds(viewIndex, numberOfViews); // handle index out of bounds

        } else if (searchOrCreate.equals("search")) { // "/search" url path
            templateToRender = "search"; // search page will be rendered
            queryMode = QueryMode.SEARCH_BY_NAME; // retrieve data for display through search by name
            if (nameOfSearched == null) {
                viewIndex = 1; // will result 1/1 in pagination
            }
            else { // search by name
                // find how many view cards we have depending on the VIEW_LENGTH and how many ocurrences of searched name there are
                numberOfViews = divideAndRoundUp(dbService.howManyEntriesOfName(nameOfSearched), VIEW_LENGTH);
                viewIndex = adjustIndexOutOfBounds(viewIndex, numberOfViews); // handle index out of bounds
            }
        } else if (searchOrCreate.equals("create")) { // "/create" url path
            templateToRender = "create"; // create page will be rendered
            queryMode = QueryMode.GET_BY_INDEX; // data for the browsing card is retriewed through plain indexing
            numberOfViews = divideAndRoundUp(dbService.getDBSize(), VIEW_LENGTH); // find how many view cards we have depending on the VIEW_LENGTH and dBSize
            viewIndex = numberOfViews - 1; // in create page, jump to the last entries in view
            model.addAttribute("displayDetail", true); // always display detail card for creating a new entry
        } // else? TODO if no condition is met, something went wrong (that's probably wrong url address)

        // fill in the data for the browse card (table view)
        shownEntries = getEntriesByIndexOrName(queryMode, nameOfSearched, viewIndex, VIEW_LENGTH); // get the entities from db
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
    @DeleteMapping({"/", "/search"})
    public String deleteEntry(@PathVariable(required = false) String searchOrCreate,
                              @RequestParam(value = "view", defaultValue = "0") int viewIndex,
                              @RequestParam(value = "id", required = false) String id,
                              @ModelAttribute EntityDTO selectedEntryDTO,
                              Model model) {

        dbService.deleteEntry(Integer.parseInt(id));

        return renderPage(searchOrCreate, viewIndex, id, null, selectedEntryDTO, model);
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
    @PutMapping({"/", "/search"})
    public String updateEntry(@PathVariable(required = false) String searchOrCreate,
                              @RequestParam(value = "view", defaultValue = "0") int viewIndex,
                              @RequestParam(value = "id", required = false) String id,
                              @ModelAttribute EntityDTO selectedEntryDTO,
                              Model model) {

        dbService.updateEntry(Integer.parseInt(id), selectedEntryDTO.getEntryName(), selectedEntryDTO.getEntryContent());

        return renderPage(searchOrCreate, viewIndex, id, null, selectedEntryDTO, model);
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
