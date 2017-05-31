package cn.patterncat.ribbon.rule;

import cn.patterncat.ribbon.predicate.MetadataAwarePredicate;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

/**
 * Created by patterncat on 2017-05-31.
 */
public class MetadataAwareRule extends ZoneAvoidanceRule {

    @Override
    public AbstractServerPredicate getPredicate() {
        return new MetadataAwarePredicate();
    }
}
