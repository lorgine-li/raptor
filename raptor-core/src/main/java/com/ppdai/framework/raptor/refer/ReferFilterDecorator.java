package com.ppdai.framework.raptor.refer;

import com.ppdai.framework.raptor.filter.refer.ReferFilter;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReferFilterDecorator {

    @Getter
    @Setter
    private List<ReferFilter> referFilters;

    public ReferFilterDecorator(List<ReferFilter> referFilters) {
        this.referFilters = referFilters;
    }

    public <T> Refer<T> decorate(Refer<T> refer) {
        if (CollectionUtils.isEmpty(referFilters)) {
            return refer;
        }
        sort();
        Refer<T> lastRefer = refer;
        for (ReferFilter filter : referFilters) {
            final ReferFilter filterFinal = filter;
            final Refer<T> lastReferFinal = lastRefer;
            lastRefer = new Refer<T>() {

                @Override
                public Response call(Request request) {
                    return filterFinal.filter(lastReferFinal, request);
                }

                @Override
                public Class<T> getInterface() {
                    return refer.getInterface();
                }

                @Override
                public URL getServiceUrl() {
                    return refer.getServiceUrl();
                }
            };
        }
        return lastRefer;
    }

    private void sort() {
        if (!CollectionUtils.isEmpty(this.referFilters)) {
            this.referFilters.sort(new Comparator<ReferFilter>() {
                @Override
                public int compare(ReferFilter o1, ReferFilter o2) {
                    return o1.getOrder() - o2.getOrder();
                }
            });
            Collections.reverse(this.referFilters);
        }
    }

}
