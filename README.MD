## fork
fork from https://github.com/jmnarloch/ribbon-discovery-filter-spring-cloud-starter

#changelog
##version 0.0.2
- fix bug:clear context thread local
- move parent to dependency management

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
