package top.cloudli.cloudojweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import top.cloudli.cloudojweb.model.Problem;

@Slf4j
@Controller
public class OJController {

    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("index")
    public String index() {
        return "index";
    }

    @GetMapping("problems")
    public String problems() {
        return "problems";
    }

    @GetMapping("commit")
    public ModelAndView commit(int problemId, Integer contestId) {
        Problem problem = restTemplate.getForObject("http://localhost/api/manager/problem/" + problemId, Problem.class);
        assert problem != null;
        return new ModelAndView("commit").addObject(problem);
    }

    @GetMapping("manager_problem")
    public String manager() {
        return "manager/manager_problem";
    }

    @GetMapping("manager_user")
    public String managerUser() {
        return "manager/manager_user";
    }

    @GetMapping("manager_contest")
    public String managerContest() {
        return "manager/manager_contest";
    }

    @GetMapping("results")
    public String results() {
        return "results";
    }

    @GetMapping("ranking")
    public String ranking() {
        return "ranking";
    }

    @GetMapping("contests")
    public String contests() {
        return "contests";
    }
}
