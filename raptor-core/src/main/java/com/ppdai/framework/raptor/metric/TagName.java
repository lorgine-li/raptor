package com.ppdai.framework.raptor.metric;

import com.ppdai.framework.raptor.util.TagNameUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TagName {

    private String name;
    private Map<String, String> tags;

    public TagName(String name, Map<String, String> tags) {
        this.name = name;
        this.tags = tags;
    }

    public static TagName name(String name) {
        return new TagName(name, new ConcurrentHashMap<>());
    }

    public TagName addTag(String name, String value) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
            return this;
        }
        this.tags.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        return TagNameUtil.format(this);
    }
}
