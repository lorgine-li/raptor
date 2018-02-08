package com.ppdai.framework.raptor.codegen.core.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RaptorCodegenInfo {

    private static volatile RaptorCodegenInfo raptorCodegenInfo;
    private static String INFO_FILE = "META-INF/raptor-codegen.properties";

    public static RaptorCodegenInfo getInstance() throws Exception {
        if (raptorCodegenInfo != null) {
            return raptorCodegenInfo;
        }
        synchronized (RaptorCodegenInfo.class) {
            if (raptorCodegenInfo != null) {
                return raptorCodegenInfo;
            }
            raptorCodegenInfo = new RaptorCodegenInfo();
        }
        return raptorCodegenInfo;
    }

    private String version;

    private RaptorCodegenInfo() throws IOException {
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(INFO_FILE)) {
            Properties p = new Properties();
            p.load(in);
            this.version = p.getProperty("version");
        }
    }

    public String getVersion() {
        return version;
    }
}
