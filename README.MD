## fork
fork from https://github.com/jmnarloch/ribbon-discovery-filter-spring-cloud-starter

#changelog
##version 0.0.3
remove clear thread local in MetadataAwarePredicate
either remove in post filter or use zuul's reuest context

##version 0.0.2
- fix bug:clear context thread local
- move parent to dependency management

this is still a bug
```
protected boolean applyByServer(DiscoveryEnabledServer server) {
        final RibbonFilterContext context = RibbonFilterContextHolder.getCurrentContext();
        final Set<Map.Entry<String, String>> attributes = Collections.unmodifiableSet(context.getAttributes().entrySet());
        final Map<String, String> metadata = server.getInstanceInfo().getMetadata();
        boolean result = metadata.entrySet().containsAll(attributes);
        //fix bug : clear thread local
        RibbonFilterContextHolder.clearCurrentContext();
        return result;
    }
```
ribbon-loadbalancer-2.2.0-sources.jar!/com/netflix/loadbalancer/AbstractServerPredicate.java
```
public List<Server> getEligibleServers(List<Server> servers, Object loadBalancerKey) {
        if (loadBalancerKey == null) {
            return ImmutableList.copyOf(Iterables.filter(servers, this.getServerOnlyPredicate()));
        } else {
            List<Server> results = Lists.newArrayList();
            for (Server server: servers) {
                if (this.apply(new PredicateKey(loadBalancerKey, server))) {
                    results.add(server);
                }
            }
            return results;
        }
    }
```
这里使用server list去挨个apply.
http://localhost:10000/recommend/recommend?productId=1&x-route=canary
加上recommend有两个实例,一个是meta是uat,一个meta是test
那么第一个实例apply为false,然后清空了threadlocal的话,第二个实例apply就是true
这显示不能路由到指定实例.
极端的情况下,假设只有一个实例的时候,就报正常的,找不到
假设第一个实例就是canary,那么恭喜,正好路由到,但是多个实例下,这个不能保证的.

##version 0.0.1
```
demo:
  ribbon:
    NIWSServerListClassName: com.netflix.loadbalancer.ConfigurationBasedServerList
    listOfServers: http://192.168.99.100:8088
zuul:
  ignored-services: config, eureka
  sslHostnameValidationEnabled: false
  routes:
    demo:
      path: /demo/**
      stripPrefix: true
      serviceId: demo
```

fix for server which is not DiscoveryEnabledServer
io/jmnarloch/spring/cloud/ribbon/predicate/DiscoveryEnabledPredicate.java
```
    @Override
    public boolean apply(@Nullable PredicateKey input) {
        return input != null
                && input.getServer() instanceof DiscoveryEnabledServer
                && apply((DiscoveryEnabledServer) input.getServer());
    }
```
if server is not instance of DiscoveryEnabledServer,let it go,return true
