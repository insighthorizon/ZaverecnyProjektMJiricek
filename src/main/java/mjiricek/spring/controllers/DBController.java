package mjiricek.spring.controllers;

import mjiricek.spring.models.DBEntity;
import mjiricek.spring.models.DBService;
import mjiricek.spring.models.EntityDTO;
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

    /**
     * TODO REFACTOR this method, maybe put reusable code into private method
     * TODO user input validation
     * Handler of the GET request on the URL "/" (with url arguments)
     * @param viewIndex value of url parameter reresenting which view is being card displayed
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
        CopyOnWriteArrayList<DBEntity> shownEntries;
        int numberOfViews;

        if (searchOrCreate != null && searchOrCreate.equals("search")) {
            if (nameOfSearched != null) { // search by name
                // get the entities from db (search by name)
                shownEntries = dbService.showEntriesByName(nameOfSearched); // TODO show only limited number of search results, but maybe still show position of the current view (example: 4/6)
                // find how many view cards we have depending on the VIEW_LENGTH and dBSize
                numberOfViews = (int) Math.ceil((double) shownEntries.size() / VIEW_LENGTH);
            } else {
                numberOfViews = 1; // before we search anything, empty results
                shownEntries = null;
            }
        } else if (searchOrCreate != null && searchOrCreate.equals("create")) {
            // find how many view cards we have depending on the VIEW_LENGTH and dBSize
            numberOfViews = (int) Math.ceil((double) dbService.getDBSize() / VIEW_LENGTH);
            viewIndex = numberOfViews - 1; // in create view - display the last entries
            // get the entities from db (lookup by index)
            shownEntries = dbService.showEntriesByIndexRange(viewIndex * VIEW_LENGTH, (viewIndex + 1) * VIEW_LENGTH);

        } else { // "/"
            // find how many view cards we have depending on the VIEW_LENGTH and dBSize
            numberOfViews = (int) Math.ceil((double) dbService.getDBSize() / VIEW_LENGTH);
            // get the entities from db (lookup by index)
            shownEntries = dbService.showEntriesByIndexRange(viewIndex * VIEW_LENGTH, (viewIndex + 1) * VIEW_LENGTH);
        }

        // handle wrong index
        if (viewIndex < 0) {
            viewIndex = 0;
        } else if (viewIndex >= numberOfViews) {
            viewIndex = numberOfViews - 1;
        }

        // fill in the data for the browse card (table view)
        model.addAttribute("entries", shownEntries);
        model.addAttribute("viewIndex", viewIndex);
        model.addAttribute("numberOfViews", numberOfViews);

        // fill in the data for detail card
        if (id != null && !id.equals("null") && !id.equals("")) {
            DBEntity dbEntityCopy = dbService.showEntryById(Integer.parseInt(id));
            if (dbEntityCopy != null) {
                model.addAttribute("entrySelected", true);
                model.addAttribute("displayDetail", true);
                model.addAttribute("selectedID", id);
                entityDTO.setEntryName(dbEntityCopy.getEntryName());
                entityDTO.setEntryContent(dbEntityCopy.getEntryContent());
            }
        }

        // decide which template to use based on the URL path variable
        if (searchOrCreate == null || searchOrCreate.equals("") || searchOrCreate.equals("null"))
            return "index";
        if (searchOrCreate.equals("search"))
            return "search";

        model.addAttribute("displayDetail", true); // create view - always display detail card
        return "create";
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

        dbService.addEntry(newEntryDTO.getEntryName(), newEntryDTO.getEntryContent());
        // next, we need to clean the DTO after the new entry has been saved
        // otherwise, the data will stay in the form
        newEntryDTO.setEntryName("");
        newEntryDTO.setEntryContent("");

        // create page should display the end of the table, that's why MAX_VALUE
        // no detail of item should be displayed
        return renderPage("create", Integer.MAX_VALUE, null, null, newEntryDTO, model);
    }

}
