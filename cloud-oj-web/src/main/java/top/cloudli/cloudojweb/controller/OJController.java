package top.cloudli.cloudojweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import top.cloudli.cloudojweb.model.Problem;

@Slf4j
@Controller
public class OJController {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${project.gateway-host:localhost}")
    private String gatewayHost;

    @GetMapping("index")
    public String index() {
        return "index";
    }


    @GetMapping("problems")
    public String problems() {
        return "problems";
    }

    @GetMapping("commit")
    public ModelAndView commit(Integer problemId, Integer contestId) {
        String url = String.format("http://%s/api/manager/problem/", gatewayHost) + problemId;
        if (contestId != null) {
            url += "?contestId=" + contestId;
        }
        Problem problem = restTemplate.getForObject(url, Problem.class);
        assert problem != null;
        return new ModelAndView("commit").addObject(problem);
    }

    @GetMapping("pro_commit")
    public ModelAndView commitPro(Integer problemId, String userId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", token);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        Problem problem = restTemplate.exchange("http://localhost/api/manager/problem/pro/" + problemId + "?userId=" + userId,
                HttpMethod.GET, httpEntity, Problem.class).getBody();
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

    @GetMapping("help")
    public String help() {
        return "help";
    }
}
