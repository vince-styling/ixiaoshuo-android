package com.vincestyling.ixiaoshuo.net;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

public final class GObjectMapper {
    private GObjectMapper() {
    }

    private static ObjectMapper mapper;

    public static ObjectMapper get() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return mapper;
    }

}
