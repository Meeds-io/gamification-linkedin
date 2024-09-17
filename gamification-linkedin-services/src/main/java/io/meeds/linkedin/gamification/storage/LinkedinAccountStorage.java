package io.meeds.linkedin.gamification.storage;

import io.meeds.linkedin.gamification.services.LinkedinService;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.security.codec.CodecInitializer;
import org.exoplatform.web.security.security.TokenServiceInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LinkedinAccountStorage {

    private static final Log LOG            = ExoLogger.getLogger(LinkedinAccountStorage.class);

    private static final String BEARER_TOKEN_KEY        = "BEARER_TOKEN";
    private static final Scope LINKEDIN_CONNECTOR_SCOPE = Scope.APPLICATION.id("linkedinConnector");

    @Autowired
    private SettingService settingService;
    @Autowired
    private CodecInitializer codecInitializer;

    public void saveLinkedinBearerToken(String bearerToken) {
        String encodedBearerToken = encode(bearerToken);
        this.settingService.set(Context.GLOBAL, LINKEDIN_CONNECTOR_SCOPE, BEARER_TOKEN_KEY, SettingValue.create(encodedBearerToken));
        LOG.debug("Finished saving LinkedIn Bearer Token");
    }

    public String getLinkedinBearerToken() {
        SettingValue<?> settingValue = settingService.get(Context.GLOBAL, LINKEDIN_CONNECTOR_SCOPE, BEARER_TOKEN_KEY);
        if (settingValue != null && settingValue.getValue() != null && StringUtils.isNotBlank(settingValue.getValue().toString())) {
            return decode(settingValue.getValue().toString());
        }
        return null;
    }

    private String encode(String token) {
        try {
            return codecInitializer.getCodec().encode(token);
        } catch (TokenServiceInitializationException e) {
            throw new IllegalStateException("Error encrypting token", e);
        }
    }

    private String decode(String encryptedToken) {
        try {
            return codecInitializer.getCodec().decode(encryptedToken);
        } catch (TokenServiceInitializationException e) {
            throw new IllegalStateException("Error decrypting token", e);
        }
    }
}
