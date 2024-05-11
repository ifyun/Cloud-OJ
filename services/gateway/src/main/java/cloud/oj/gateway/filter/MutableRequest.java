package cloud.oj.gateway.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

/**
 * HttpServletRequest 包装，用于修改 Headers
 */
public class MutableRequest extends HttpServletRequestWrapper {

    private final Map<String, String> headers;

    public MutableRequest(HttpServletRequest request) {
        super(request);
        headers = new HashMap<>();
    }

    public void putHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        var value = headers.get(name);

        if (value != null) {
            return value;
        }

        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        var set = new HashSet<String>();

        var e = super.getHeaderNames();

        while (e.hasMoreElements()) {
            set.add(e.nextElement());
        }

        set.addAll(headers.keySet());

        return Collections.enumeration(set);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (headers.containsKey(name)) {
            return Collections.enumeration(Collections.singletonList(headers.get(name)));
        }

        return super.getHeaders(name);
    }
}
