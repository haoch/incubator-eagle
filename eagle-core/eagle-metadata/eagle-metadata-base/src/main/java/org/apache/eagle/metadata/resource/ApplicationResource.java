/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.eagle.metadata.resource;


import com.google.inject.servlet.RequestScoped;
import org.apache.eagle.metadata.model.ApplicationSpec;
import org.apache.eagle.metadata.service.ApplicationSpecService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/apps")
@RequestScoped
public class ApplicationResource {
    private final ApplicationSpecService applicationSpecService;

    @Inject
    public ApplicationResource( ApplicationSpecService applicationSpecService){
        this.applicationSpecService = applicationSpecService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApplicationSpec> getAvailableApplications(){
        return applicationSpecService.getAllApplicationSpecs();
    }

    @Path("/{appType}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationSpec getApplication(@PathParam("appType") String appType){
        return applicationSpecService.getApplicationSpecByType(appType);
    }

    @Path("/install")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationSpec getApplication(){
        return null;
    }
}