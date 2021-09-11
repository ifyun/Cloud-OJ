package group._204.oj.judge.component;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@ConfigurationPropertiesBinding()
public class BooleanConverter implements Converter<String, Boolean> {

    @Override
    public Boolean convert(@NonNull String s) {
        return Boolean.parseBoolean(s);
    }
}
