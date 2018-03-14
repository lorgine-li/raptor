package com.ppdai.framework.raptor.filter;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.ppdai.framework.raptor.common.RaptorInfo;
import com.ppdai.framework.raptor.metric.MetricContext;
import com.ppdai.framework.raptor.metric.TagName;
import com.ppdai.framework.raptor.rpc.Caller;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;

import java.util.concurrent.TimeUnit;

public abstract class AbstractMetricFilter extends AbstractFilter {

    public static final String CONCURRENCE_NAME = RaptorInfo.getInstance().getMetricPrefix() + ".concurrence";
    public static final String CONCURRENCE_HISTOGRAM_NAME = RaptorInfo.getInstance().getMetricPrefix() + ".concurrenceHistogram";
    public static final String TIME_NAME = RaptorInfo.getInstance().getMetricPrefix() + ".time";
    public static final String COUNT_NAME = RaptorInfo.getInstance().getMetricPrefix() + ".count";

    protected MetricRegistry metricRegistry = MetricContext.getMetricRegistry();

    protected Response doFilter(URL serviceUrl, Caller caller, Request request) {
        Counter concurrenceCounter = concurrenceCounter(serviceUrl, request);
        concurrenceCounter.inc();

        Histogram concurrenceHistogram = concurrenceHistogram(serviceUrl, request);
        concurrenceHistogram.update(concurrenceCounter.getCount());

        long t = System.currentTimeMillis();
        Response response = null;
        try {
            response = caller.call(request);
            return response;
        } finally {
            long requestTime = System.currentTimeMillis() - t;

            concurrenceCounter.dec();
            concurrenceHistogram.update(concurrenceCounter.getCount());
            time(serviceUrl, request, response, requestTime);
            count(serviceUrl, request, response);
        }
    }

    protected abstract String getNodeType();

    protected Counter concurrenceCounter(URL serviceUrl, Request request) {
        TagName tagName = getTagName(CONCURRENCE_NAME);
        tagServiceUrl(tagName, serviceUrl);
        tagRequest(tagName, request);
        return metricRegistry.counter(tagName.toString());
    }

    protected Histogram concurrenceHistogram(URL serviceUrl, Request request) {
        TagName tagName = getTagName(CONCURRENCE_HISTOGRAM_NAME);
        tagServiceUrl(tagName, serviceUrl);
        tagRequest(tagName, request);
        return metricRegistry.histogram(tagName.toString());
    }

    protected void time(URL serviceUrl, Request request, Response response, long requestTime) {
        TagName tagName = tagAll(TIME_NAME, serviceUrl, request, response);
        Timer timer = metricRegistry.timer(tagName.toString());
        timer.update(requestTime, TimeUnit.MILLISECONDS);
    }

    protected void count(URL serviceUrl, Request request, Response response) {
        TagName tagName = tagAll(COUNT_NAME, serviceUrl, request, response);
        Counter counter = metricRegistry.counter(tagName.toString());
        counter.inc();
    }

    protected TagName tagAll(String name, URL serviceUrl, Request request, Response response) {
        TagName tagName = getTagName(name);
        tagServiceUrl(tagName, serviceUrl);
        tagRequest(tagName, request);
        tagResponse(tagName, response);
        return tagName;
    }

    protected TagName getTagName(String name) {
        return TagName.name(name)
                .addTag("raptorVersion", getRaptorVersion())
                .addTag("appId", getAppId())
                .addTag("nodeType", getNodeType());
    }

    protected TagName tagServiceUrl(TagName tagName, URL serviceUrl) {
        return tagName.addTag("version", getInterfaceVersion(serviceUrl));
    }

    protected TagName tagRequest(TagName tagName, Request request) {
        return tagName.addTag("interface", request.getInterfaceName())
                .addTag("method", request.getMethodName())
                .addTag("clientHost", getClientHost(request));
    }

    protected TagName tagResponse(TagName tagName, Response response) {
        return tagName.addTag("status", getStatusCode(response))
                .addTag("serverHost", getServerHost(response));
    }
}
