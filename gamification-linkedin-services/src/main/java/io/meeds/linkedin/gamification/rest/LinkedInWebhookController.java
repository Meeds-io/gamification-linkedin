/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.linkedin.gamification.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

import static io.meeds.linkedin.gamification.utils.Utils.*;

@RestController
@RequestMapping("/linkedin/webhook")
@Tag(name = "webhooks", description = "An endpoint to receive linkedin webhooks")
public class LinkedInWebhookController {

  private static final Log LOG            = ExoLogger.getLogger(LinkedInWebhookController.class);

  @Autowired
  private ListenerService listenerService;

  @PostMapping
  @Operation(summary = "Project webhook for Remote LinkedIn connector.", description = "Project webhook for Remote LinkedIn connector.", method = "POST")
  @ApiResponse(responseCode = "200", description = "Ok")
  @ApiResponse(responseCode = "509", description = "Internal Server Error")
  public ResponseEntity<Object> linkedInEvent(
          @Parameter(required = true) @RequestParam("senderId") String senderId,
          @Parameter(required = true) @RequestParam("receiverId") String receiverId,
          @Parameter(required = true) @RequestParam("ruleTitle") String ruleTitle
          ) {
    try {
      try {

//        String eventDetails = "{" + PROJECT_ID + ": " + event.getProjectId() + ", " + LANGUAGE_ID + ": " + event.getLanguageId()
//                + ", " + MUST_BE_HUMAN + ": " + event.isMustBeHuman() + ", " + DIRECTORY_ID + ": " + event.getDirectoryId() + "}";

        Map<String, String> gam = new HashMap<>();
        gam.put("senderId", senderId);
        gam.put("receiverId", receiverId);
        gam.put("objectId", "");
        gam.put("objectType", "");
//        gam.put("eventDetails", eventDetails);

        gam.put("ruleTitle", ruleTitle);

        listenerService.broadcast(GAMIFICATION_GENERIC_EVENT, gam, "");
        LOG.info("LinkedIn action {} broadcast for user {}", ruleTitle, senderId);

      } catch (Exception e) {
        LOG.error("Cannot broadcast linkedIn event", e);
      }

      return ResponseEntity.status(HttpStatus.OK).build();
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }
}
