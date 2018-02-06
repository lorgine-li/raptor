package com.ppdai.framework.raptor.refer.repository;

import com.ppdai.framework.raptor.rpc.URL;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 根据一个urlKey保存Listener
 */
public abstract class UrlRepository {

    protected Map<String, Set<UrlChangeListener>> listeners = new ConcurrentHashMap<>();

    protected volatile Map<String, URL> cache = new ConcurrentHashMap<>();

    public void subscribe(String urlKey, UrlChangeListener listener) {
        listeners.putIfAbsent(urlKey, Collections.synchronizedSet(new HashSet<>()));
        Set<UrlChangeListener> set = listeners.get(urlKey);
        set.add(listener);
        URL url = getUrl(urlKey);
        if (url != null) {
            cache.put(urlKey, url);
        }
    }

    protected abstract String getUrlString(String urlKey);

    public URL getUrl(String urlKey) {
        String url = getUrlString(urlKey);
        if (StringUtils.isNotBlank(url)) {
            return URL.valueOf(url);
        }
        return null;
    }

    public synchronized void refresh() {
        Map<String, URL> newCache = new ConcurrentHashMap<>();
        for (String key : listeners.keySet()) {
            URL oldUrl = cache.get(key);
            URL newUrl = getUrl(key);
            if (newUrl != null) {
                newCache.put(key, newUrl);
                if (!newUrl.equals(oldUrl)) {
                    notifyListeners(key, newUrl);
                }
            } else {
                if (oldUrl != null) {
                    notifyListeners(key, null);
                }
            }
            this.cache = newCache;
        }
    }

    public void notifyListeners(String key, URL newUrl) {
        Set<UrlChangeListener> set = listeners.get(key);
        if (set != null) {
            for (UrlChangeListener urlChangeListener : set) {
                urlChangeListener.onChange(key, newUrl);
            }
        }
    }
}
