package io.meeds.linkedin.gamification.rest;

import io.meeds.gamification.model.RemoteConnectorSettings;
import io.meeds.linkedin.gamification.model.RemoteOrganization;
import io.meeds.linkedin.gamification.services.LinkedinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@RestController
@RequestMapping("settings")
@Tag(name = "linkedin/settings", description = "Manage and access linkedin settings") // NOSONAR
public class LinkedinSettingsRest {

    private static final Log LOG            = ExoLogger.getLogger(LinkedinSettingsRest.class);

    @Autowired
    private LinkedinService linkedinService;


//    @GetMapping("check-secret")
//    @Secured("rewarding")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Operation(summary = "Get LinkedIn connector settings", method = "GET")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Request fulfilled"),
//            @ApiResponse(responseCode = "400", description = "Invalid query input"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public Response getSettings() throws IllegalAccessException {
//
//        LOG.debug("LinkedinSettingsRest.getSettings: started");
//
//        org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
//        RemoteConnectorSettings remoteConnectorSettings = linkedinService.getConnectorSettings(identity);
//
//        boolean hasSecret = remoteConnectorSettings.getSecretKey() != null;
//
//        return Response.ok().entity("{\"hasSecret\": " + hasSecret + "}").build();
//    }

    @GetMapping("oauthCallback")
    @Secured("rewarding")
    @Produces(MediaType.TEXT_HTML)
    @Operation(summary = "Validate OAuth code returned from linkedin.", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request fulfilled"),
            @ApiResponse(responseCode = "400", description = "Invalid query input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public String validateOathCode(
            @Parameter(description = "Access Token", required = true)
            @RequestParam(value = "code", required = true)
            String code) throws IllegalAccessException {

        LOG.debug("LinkedinSettingsRest.connect: started");
        LOG.debug("LinkedinSettingsRest.connect: code : " + code);

        org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();

        linkedinService.validateOathCode(identity, code);

        return "<html><body><script type=text/javascript>window.close();</script></body></html>";
    }


    @GetMapping("remote/organization/lookup")
    @Secured("rewarding")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Find Organization by Vanity Name", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request fulfilled"),
            @ApiResponse(responseCode = "400", description = "Invalid query input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized operation"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<RemoteOrganization> searchOrganizationsByName(
            @Parameter(description = "Search keyword", required = true)
            @RequestParam(value = "keyword", required = true)
            String keyword) {

        LOG.debug("LinkedinSettingsRest.searchOrganizationsByVanityName: started");
        LOG.debug("LinkedinSettingsRest.searchOrganizationsByVanityName: keyword : " + keyword);

        org.exoplatform.services.security.Identity identity = ConversationState.getCurrent().getIdentity();
        try {
            return linkedinService.searchOrganizationsByName(keyword, identity);
        }  catch (IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
