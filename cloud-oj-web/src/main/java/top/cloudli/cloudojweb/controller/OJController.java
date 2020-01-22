package top.cloudli.cloudojweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import top.cloudli.cloudojweb.model.Problem;

@Slf4j
@Controller
public class OJController {

    private RestTemplate restTemplate = new RestTemplate();

    @RequestMapping("index")
    public String index() {
        return "index";
    }

    @RequestMapping("problems")
    public String problems() {
        return "problems";
    }

    @RequestMapping("commit")
    public ModelAndView commit(int problemId, Integer contestId) {
        Problem problem = restTemplate.getForObject("http://localhost/api/manager/problem/" + problemId, Problem.class);
        assert problem != null;
        return new ModelAndView("commit").addObject(problem);
    }

    @RequestMapping("manager_problem")
    public String manager() {
        return "manager/manager_problem";
    }

    @RequestMapping("results")
    public String results() {
        return "results";
    }

    @RequestMapping("ranking")
    public String ranking() {
        return "ranking";
    }

    @RequestMapping("contests")
    public String contests() {
        return "contests";
    }
}
