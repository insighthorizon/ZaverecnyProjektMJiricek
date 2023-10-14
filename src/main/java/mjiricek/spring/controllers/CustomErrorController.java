package mjiricek.spring.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * this class defines how spring should process errors that are not handled otherwise
 * - created mainly in order to have custom 404 page
 */
@Controller
public class CustomErrorController implements ErrorController {

    /**
     * mapping for error
     * - I copied this from the course
     * @param request http request
     * @return template to use
     */
    @RequestMapping("/error")
    public String handleError (HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status !=  null) {
            int statusCode = Integer.parseInt(status.toString());
            // this is where we ensure that NOT_FOUND error opens error404.html page
            if (statusCode == HttpStatus.NOT_FOUND.value())
                return "error404";
        }

        return "error";
    }
}
