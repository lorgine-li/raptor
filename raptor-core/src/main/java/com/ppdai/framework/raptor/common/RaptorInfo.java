package com.ppdai.framework.raptor.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Properties;

@Data
@Slf4j
public class RaptorInfo {

    private static volatile RaptorInfo raptorInfo;
    private static String INFO_FILE = "META-INF/raptor-info.properties";

    public static RaptorInfo getInstance() {
        if (raptorInfo != null) {
            return raptorInfo;
        }
        synchronized (RaptorInfo.class) {
            if (raptorInfo != null) {
                return raptorInfo;
            }
            raptorInfo = new RaptorInfo();
        }
        return raptorInfo;
    }

    private String version;
    private String metricPrefix = RaptorConstants.METRIC_NAME;

    private RaptorInfo() {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(INFO_FILE)) {
            Properties p = new Properties();
            p.load(in);
            this.version = p.getProperty("version");
        } catch (Exception e) {
            log.error("Can not load raptorInfo", e);
        }
    }
}
