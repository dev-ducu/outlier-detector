package outlierdetector.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/")
    @ApiOperation(value = "${HomeController.root}")
    public String root() {
        log.info("Handling HTTP request on root path. Redirecting to /swagger-ui.html");

        return "redirect:/swagger-ui.html";
    }
}
