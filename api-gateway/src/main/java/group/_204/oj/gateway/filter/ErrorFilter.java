package group._204.oj.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ErrorFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().containsKey("throwable");
    }

    @SneakyThrows
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Object e = ctx.get("throwable");

        if (e instanceof ZuulException) {
            ctx.remove("throwable");
            Throwable error = ((ZuulException) e).getCause().getCause().getCause();
            log.error(error.getMessage());
            InputStreamReader inputStreamReader = new InputStreamReader(new ClassPathResource("static/error/502.html").getInputStream());
            String errorPage = new BufferedReader(inputStreamReader)
                    .lines().collect(Collectors.joining("\n"));

            ctx.setResponseStatusCode(502);
            ctx.getResponse().setContentType("text/html");
            ctx.getResponse().setCharacterEncoding("utf-8");
            ctx.getResponse().getWriter().write(errorPage);
        }

        return null;
    }
}
