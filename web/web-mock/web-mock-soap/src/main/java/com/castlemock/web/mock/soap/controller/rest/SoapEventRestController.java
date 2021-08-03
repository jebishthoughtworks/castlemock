/*
 * Copyright 2020 Karl Dahlgren
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.castlemock.web.mock.soap.controller.rest;

import com.castlemock.model.core.ServiceProcessor;
import com.castlemock.model.mock.soap.domain.SoapEvent;
import com.castlemock.service.mock.soap.event.input.ReadSoapEventInput;
import com.castlemock.service.mock.soap.event.output.ReadSoapEventOutput;
import com.castlemock.web.core.controller.rest.AbstractRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api/rest/soap")
@Api(value="SOAP - Event", tags = {"SOAP - Event"})
public class SoapEventRestController extends AbstractRestController {

    @Autowired
    public SoapEventRestController(final ServiceProcessor serviceProcessor){
        super(serviceProcessor);
    }

    @ApiOperation(value = "Get SOAP event", response = SoapEvent.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved SOAP event")})
    @RequestMapping(method = RequestMethod.GET, value = "/event/{eventId}")
    @PreAuthorize("hasAuthority('READER') or hasAuthority('MODIFIER') or hasAuthority('ADMIN')")
    public @ResponseBody
    ResponseEntity<SoapEvent> getSoapEvent(
            @ApiParam(name = "eventId", value = "The id of the event")
            @PathVariable(value = "eventId") final String eventId) {
        final ReadSoapEventOutput output = super.serviceProcessor.process(ReadSoapEventInput.builder()
                .soapEventId(eventId)
                .build());

        return ResponseEntity.ok(output.getSoapEvent());
    }

}
