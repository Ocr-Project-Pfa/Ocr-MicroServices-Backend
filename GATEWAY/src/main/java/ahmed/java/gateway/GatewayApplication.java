package ahmed.java.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}


	@Bean
	public DiscoveryClientRouteDefinitionLocator routesDynamic(
			ReactiveDiscoveryClient reactiveDiscoveryClient,
			DiscoveryLocatorProperties discoveryLocatorProperties
	){
		return new  DiscoveryClientRouteDefinitionLocator(reactiveDiscoveryClient,discoveryLocatorProperties);
	}
}
