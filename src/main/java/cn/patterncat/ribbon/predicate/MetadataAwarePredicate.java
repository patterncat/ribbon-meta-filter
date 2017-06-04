package cn.patterncat.ribbon.predicate;

import cn.patterncat.ribbon.api.RibbonFilterContext;
import cn.patterncat.ribbon.support.RibbonFilterContextHolder;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by patterncat on 2017-05-31.
 */
public class MetadataAwarePredicate extends AbstractServerPredicate{

    @Override
    public boolean apply(PredicateKey input) {
        if(input == null || !(input.getServer() instanceof DiscoveryEnabledServer)){
            return true;
        }
        return applyByServer((DiscoveryEnabledServer) input.getServer());
    }

    protected boolean applyByServer(DiscoveryEnabledServer server) {
        final RibbonFilterContext context = RibbonFilterContextHolder.getCurrentContext();
        final Set<Map.Entry<String, String>> attributes = Collections.unmodifiableSet(context.getAttributes().entrySet());
        final Map<String, String> metadata = server.getInstanceInfo().getMetadata();
        boolean result = metadata.entrySet().containsAll(attributes);
        //fix bug : clear thread local
        RibbonFilterContextHolder.clearCurrentContext();
        return result;
    }
}
