package com.ppdai.framework.raptor.filter;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.ppdai.framework.raptor.common.RaptorInfo;
import com.ppdai.framework.raptor.metric.MetricContext;
import com.ppdai.framework.raptor.metric.TagName;
import com.ppdai.framework.raptor.refer.Refer;
import com.ppdai.framework.raptor.rpc.Caller;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;

import java.util.concurrent.TimeUnit;

public abstract class AbstractMetricFilter extends AbstractFilter {

    public static final String concurrenceName = RaptorInfo.getInstance().getMetricPrefix() + ".concurrence";
    public static final String concurrenceHistogramName = RaptorInfo.getInstance().getMetricPrefix() + ".concurrenceHistogram";
    public static final String timeName = RaptorInfo.getInstance().getMetricPrefix() + ".time";
    public static final String countName = RaptorInfo.getInstance().getMetricPrefix() + ".count";
    public static final String requestPayloadName = RaptorInfo.getInstance().getMetricPrefix() + ".payload.request";
    public static final String responsePayloadName = RaptorInfo.getInstance().getMetricPrefix() + ".payload.response";

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
            payload(serviceUrl, request, response);
        }
    }

    protected abstract String getNodeType();

    protected Counter concurrenceCounter(URL serviceUrl, Request request) {
        TagName tagName = getTagName(concurrenceName);
        tagServiceUrl(tagName, serviceUrl);
        tagRequest(tagName, request);
        return metricRegistry.counter(tagName.toString());
    }

    protected Histogram concurrenceHistogram(URL serviceUrl, Request request) {
        TagName tagName = getTagName(concurrenceHistogramName);
        tagServiceUrl(tagName, serviceUrl);
        tagRequest(tagName, request);
        return metricRegistry.histogram(tagName.toString());
    }

    protected void time(URL serviceUrl, Request request, Response response, long requestTime) {
        TagName tagName = tagAll(timeName, serviceUrl, request, response);
        Timer timer = metricRegistry.timer(tagName.toString());
        timer.update(requestTime, TimeUnit.MILLISECONDS);
    }

    protected void count(URL serviceUrl, Request request, Response response) {
        TagName tagName = tagAll(countName, serviceUrl, request, response);
        Counter counter = metricRegistry.counter(tagName.toString());
        counter.inc();
    }

    protected void payload(URL serviceUrl, Request request, Response response) {
        Long requestPayload = getRequestPayloadSize(request);
        if (requestPayload != null) {
            TagName tagName = tagAll(requestPayloadName, serviceUrl, request, response);
            Histogram requestPayloadHistogram = metricRegistry.histogram(tagName.toString());
            requestPayloadHistogram.update(requestPayload);
        }

        Long responsePayload = getResponsePayloadSize(response);
        if (responsePayload != null) {
            TagName tagName = tagAll(responsePayloadName, serviceUrl, request, response);
            Histogram responsePayloadHistogram = metricRegistry.histogram(tagName.toString());
            responsePayloadHistogram.update(responsePayload);
        }
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
