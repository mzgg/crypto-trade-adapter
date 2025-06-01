package com.crypto.adapter.out.binance;

import com.binance.connector.client.common.configuration.ClientConfiguration;
import com.binance.connector.client.common.configuration.SignatureConfiguration;
import com.binance.connector.client.spot.rest.SpotRestApiUtil;
import com.binance.connector.client.spot.rest.api.SpotRestApi;
import com.crypto.application.domain.model.User;
import okhttp3.Authenticator;
import okhttp3.Credentials;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class ApiUtil {

    public static SpotRestApi getApi(User user) {
        String proxyHost = System.getenv("PROXY_HOST");
        String proxyPort = System.getenv("PROXY_PORT");
        String proxyUser = System.getenv("PROXY_USER");
        String proxyPass = System.getenv("PROXY_PASS");

        SignatureConfiguration signatureConfiguration = new SignatureConfiguration();
        signatureConfiguration.setApiKey(user.getApiKey());
        signatureConfiguration.setSecretKey(user.getSecretKey());

        // Basic client config
        ClientConfiguration clientConfiguration = SpotRestApiUtil.getClientConfiguration();
        clientConfiguration.setSignatureConfiguration(signatureConfiguration);

        // Create the HTTP proxy
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost,  Integer.parseInt(proxyPort)));
        // Add the proxy to the configuration
        clientConfiguration.setProxy(proxy);
        // Create the Proxy Authenticator
        Authenticator proxyAuthenticator = (route, response) -> {
            String credential = Credentials.basic(proxyUser, proxyPass);
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };
        // Add the proxy authenticator to the configuration
        clientConfiguration.setProxyAuthenticator(proxyAuthenticator);
        // Use with API
        return new SpotRestApi(clientConfiguration);
    }

}
