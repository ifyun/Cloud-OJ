package group._204.oj.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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
            Throwable error = ((ZuulException) e).getCause().getCause();
            log.error(error.getMessage() + ": " + error.getCause().getMessage());

            ctx.setResponseStatusCode(502);
            ctx.getResponse().setContentType(MediaType.APPLICATION_JSON_VALUE);
            ctx.getResponse().setCharacterEncoding("utf-8");
            ctx.getResponse().getWriter().write("{\"msg\": \"网关找不到服务\"}");
        }

        return null;
    }
}
