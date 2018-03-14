package com.ppdai.framework.raptor.metric;

import com.codahale.metrics.MetricRegistry;

public class MetricContext {

    private final static MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    public static MetricRegistry getMetricRegistry() {
        return METRIC_REGISTRY;
    }
}
