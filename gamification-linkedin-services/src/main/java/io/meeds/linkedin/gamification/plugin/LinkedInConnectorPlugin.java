package io.meeds.linkedin.gamification.plugin;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import io.meeds.gamification.model.RemoteConnectorSettings;
import io.meeds.gamification.plugin.ConnectorPlugin;
import io.meeds.gamification.service.ConnectorService;
import io.meeds.gamification.service.ConnectorSettingService;
import io.meeds.linkedin.gamification.oauth.LinkedInApi20;
import io.meeds.oauth.exception.OAuthException;
import io.meeds.oauth.exception.OAuthExceptionCode;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

@Component
public class LinkedInConnectorPlugin extends ConnectorPlugin {

    private static final Log LOG                = ExoLogger.getLogger(LinkedInConnectorPlugin.class);

    public static final String     CONNECTOR_NAME     = "linkedin";

    private static final String     NAME               = "linkedin";

    private static final String     CONNECTOR_SCOPE    = "r_basicprofile";
    
    private static final String     CONNECTOR_REST_API = "https://api.linkedin.com/v2/me";

    @Autowired
    private ConnectorService connectorService;

    @Autowired
    private ConnectorSettingService connectorSettingService;

    private OAuth20Service          oAuthService;

    private long                    remoteConnectorId;

    @PostConstruct
    public void initData() {
        LOG.info("Adding LinkedIn connector plugin");
        connectorService.addPlugin(this);
    }


    @Override
    public String getConnectorName() {
        LOG.info("Getting LinkedIn connector name");
        return CONNECTOR_NAME;
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * THIS METHOD IS NOT NECESSARY BECAUSE IT DOES NOT SAVE THE ACCESS TOKEN
     * IT HERE ONLY FOR DEMONSTRATION PURPOSES COZ IT FETCHES THE USERNAME FROM LINKEDIN
     * AFTER DEMONSTRATION, WE ARE USING A CUSTOM REST API TO HANDLE THE ACCESS TOKEN
     */

    @Override
    public String validateToken(String code) throws OAuthException {
        try {
            LOG.info("code: " + code);
            OAuth2AccessToken oAuth2AccessToken = getAccessToken(code, CONNECTOR_SCOPE);
            String linkedinIdentifier = fetchUsernameFromAccessToken(oAuth2AccessToken.getAccessToken());

            if (StringUtils.isBlank(linkedinIdentifier)) {
                throw new OAuthException(OAuthExceptionCode.INVALID_STATE, "User LinkedIn identifier is empty");
            }
            return linkedinIdentifier;
        } catch (IOException e) { // NOSONAR
            throw new OAuthException(OAuthExceptionCode.IO_ERROR, e);
        }
    }

    public OAuth2AccessToken getAccessToken(String code, String scope) {
        RemoteConnectorSettings remoteConnectorSettings = connectorSettingService.getConnectorSettings(CONNECTOR_NAME);
        remoteConnectorSettings.setSecretKey(connectorSettingService.getConnectorSecretKey(CONNECTOR_NAME));

        if (StringUtils.isBlank(remoteConnectorSettings.getApiKey()) || StringUtils.isBlank(remoteConnectorSettings.getSecretKey())) {
            LOG.warn("Missing '{}' connector settings", CONNECTOR_NAME);
            return null;
        }

        if (StringUtils.isNotBlank(code)) {
            try {
                return getOAuthService(remoteConnectorSettings, scope).getAccessToken(code);
            } catch (InterruptedException | IOException e) { // NOSONAR
                throw new OAuthException(OAuthExceptionCode.IO_ERROR, e);
            } catch (ExecutionException e) {
                throw new OAuthException(OAuthExceptionCode.UNKNOWN_ERROR, e);
            }
        } else {
            throw new OAuthException(OAuthExceptionCode.USER_DENIED_SCOPE, "User denied scope on LinkedIn authorization page");
        }
    }

    public OAuth20Service getOAuthService(RemoteConnectorSettings remoteConnectorSettings, String scope) {
        if (oAuthService == null || remoteConnectorSettings.hashCode() != remoteConnectorId) {
            remoteConnectorId = remoteConnectorSettings.hashCode();
            oAuthService = new ServiceBuilder(remoteConnectorSettings.getApiKey())
                    .apiSecret(remoteConnectorSettings.getSecretKey())
                    .defaultScope(scope)
                    .callback(remoteConnectorSettings.getRedirectUrl())
                    .build(LinkedInApi20.instance());
        }
        return oAuthService;
    }

    private static String fetchUsernameFromAccessToken(String accessToken) throws IOException {
        URL url = new URL(CONNECTOR_REST_API);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Accept", "application/json");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                String resp = response.toString();
                LOG.info(resp);
                return resp.split("\"vanityName\":")[1].split(",")[0].replace("\"", "").trim();
            }
        } else {
            throw new IOException("Error retrieving user information from LinkedIn. Response code: " + responseCode);
        }
    }
}
