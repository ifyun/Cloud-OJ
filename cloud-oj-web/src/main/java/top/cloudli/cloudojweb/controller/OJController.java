package top.cloudli.cloudojweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import top.cloudli.cloudojweb.model.Problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
public class OJController {

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("problems")
    public ModelAndView problem() {
        Problem[] problems = restTemplate.getForObject("http://localhost:5000/api/manager/problem", Problem[].class);
        List<Problem> problemList = problems == null ? new ArrayList<>() : Arrays.asList(problems);
        log.info(String.valueOf(problemList));
        ModelAndView modelAndView = new ModelAndView("problems");
        modelAndView.addObject(problemList);
        return modelAndView;
    }
}
