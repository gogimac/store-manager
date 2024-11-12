package ro.ing.api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registers the WebSocket endpoint that clients will use to connect to the WebSocket server.
     * The endpoint is configured with SockJS support to enable fallback options for browsers that don't support WebSocket.
     *
     * @param registry the registry to add the endpoint to
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }

    /**
     * Configures the message broker used to route messages from one client to another.
     * A simple in-memory message broker is enabled with destinations prefixed with "/topic" and "/queue".
     * Application destination prefixes are set to "/app" to filter destinations targeting application annotated methods.
     *
     * @param config the registry to configure the message broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }
}