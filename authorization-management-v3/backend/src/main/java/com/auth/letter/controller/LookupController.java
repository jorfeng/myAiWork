package com.auth.letter.controller;

import com.auth.letter.dto.ApiResponse;
import com.auth.letter.service.AuthLetterService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Lookup Controller
 * Uses JAX-RS annotations (@Path) for URL mapping
 * TODO: Implement real lookup service integration
 */
@Component
@Path("/api/lookup")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class LookupController {

    private final AuthLetterService authLetterService;

    /**
     * Get lookup values by code
     * TODO: Implement real lookup service
     */
    @GET
    @Path("/{code}")
    public Response getLookupValues(@PathParam("code") String code) {
        return Response.ok(ApiResponse.success(authLetterService.getLookupValues(code))).build();
    }

    /**
     * Get organization tree
     * TODO: Implement real organization tree from lookup service
     */
    @GET
    @Path("/org/tree")
    public Response getOrgTree() {
        return Response.ok(ApiResponse.success(authLetterService.getOrgTree())).build();
    }
}