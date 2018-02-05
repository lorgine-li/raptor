package com.ppdai.framework.raptor.client.repository;

import com.ppdai.framework.raptor.rpc.URL;

public interface UrlChangeListener {

    void onChange(String key, URL newUrl);
}
