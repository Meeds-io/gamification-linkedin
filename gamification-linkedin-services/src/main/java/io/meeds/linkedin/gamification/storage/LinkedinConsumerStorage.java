package io.meeds.linkedin.gamification.storage;

import io.meeds.linkedin.gamification.exception.LinkedinConnectionException;
import io.meeds.linkedin.gamification.model.RemoteOrganization;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static io.meeds.linkedin.gamification.utils.Utils.*;

@Component
public class LinkedinConsumerStorage {
    private static final Log LOG                      = ExoLogger.getLogger(LinkedinConsumerStorage.class);
    private HttpClient         client;
    public static final String TOKEN_EXPIRED_OR_INVALID = "linkedin.tokenExpiredOrInvalid";

    public List<RemoteOrganization> findOrganizationsByVanityName(String accessToken, String keyword) throws IllegalAccessException {
        try {

            LOG.debug("findOrganizationsByVanityName: accessToken : " + accessToken);

            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            URI uri = URI.create(LINKEDIN_API_URL + ORGANIZATIONS + "?q=vanityName&vanityName=" + encodedKeyword);
            String response = processGet(uri, accessToken);

            LOG.debug("response: " + response);

            JSONArray jsonArray = new JSONObject(response).getJSONArray("elements");

            List<RemoteOrganization> organizationsList = new ArrayList<>();

            // loop through the array and parse the organizationsList
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject elementObject = jsonArray.getJSONObject(i);

                // parse the project
                RemoteOrganization organization = new RemoteOrganization();
                organization.setId(elementObject.getLong("id"));
                organization.setVanityName(elementObject.getString("vanityName"));
                organization.setLocalizedName(elementObject.getString("localizedName"));

                organizationsList.add(organization);
            }
            return organizationsList;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (LinkedinConnectionException e) {
            throw new IllegalAccessException(e.getMessage());
        }
    }

    private String processGet(URI uri, String accessToken) throws LinkedinConnectionException {
        HttpClient httpClient = getHttpClient();
        HttpGet request = new HttpGet(uri);
        try {
            return processRequest(httpClient, request, accessToken);
        } catch (IOException e) {
            throw new LinkedinConnectionException(LINKEDIN_CONNECTION_ERROR, e);
        }
    }

    private HttpClient getHttpClient() {
        if (client == null) {
            HttpClientConnectionManager clientConnectionManager = getClientConnectionManager();
            HttpClientBuilder httpClientBuilder = HttpClients.custom()
                    .setConnectionManager(clientConnectionManager)
                    .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy());
            client = httpClientBuilder.build();
        }
        return client;
    }

    private String processRequest(HttpClient httpClient, HttpRequestBase request, String accessToken) throws IOException, LinkedinConnectionException {

        request.setHeader(AUTHORIZATION, TOKEN + accessToken);
        request.setHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        request.setHeader("LinkedIn-Version", LINKEDIN_VERSION);
        request.setHeader("X-Restli-Protocol-Version", LINKEDIN_PROTOCOL_VERSION);

        HttpResponse response = httpClient.execute(request);
        LOG.debug("response: " + response);
        LOG.debug("response status code: " + response.getStatusLine().getStatusCode());

        boolean isSuccess = response != null
                && (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300);
        if (isSuccess) {
            return processSuccessResponse(response);
        } else if (response != null && response.getStatusLine().getStatusCode() == 404) {
            return null;
        } else {
            processErrorResponse(response);
            return null;
        }
    }

    private String processSuccessResponse(HttpResponse response) throws IOException {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
            return String.valueOf(HttpStatus.SC_NO_CONTENT);
        } else if ((response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED
                || response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) && response.getEntity() != null
                && response.getEntity().getContentLength() != 0) {
            try (InputStream is = response.getEntity().getContent()) {
                return IOUtils.toString(is, StandardCharsets.UTF_8);
            }
        } else {
            return null;
        }
    }

    private void processErrorResponse(HttpResponse response) throws LinkedinConnectionException, IOException {
        if (response == null) {
            throw new LinkedinConnectionException("Error when connecting linkedin");
        } else if (response.getEntity() != null) {
            try (InputStream is = response.getEntity().getContent()) {
                String errorMessage = IOUtils.toString(is, StandardCharsets.UTF_8);
                LOG.debug("errorMessage: " + errorMessage);
                throw new LinkedinConnectionException(errorMessage);
            }
        } else {
            throw new LinkedinConnectionException(LINKEDIN_CONNECTION_ERROR + response.getStatusLine().getStatusCode());
        }
    }

    private HttpClientConnectionManager getClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(10);
        return connectionManager;
    }

}
