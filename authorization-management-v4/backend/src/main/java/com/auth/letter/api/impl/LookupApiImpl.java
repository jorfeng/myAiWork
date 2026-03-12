package com.auth.letter.api.impl;

import com.auth.letter.api.iLookupApi;
import com.auth.letter.dto.ApiResponse;
import com.auth.letter.service.AuthLetterService;
import com.auth.letter.service.LookupValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Component
@Path("/api/lookup")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class LookupApiImpl implements iLookupApi {

    private final AuthLetterService authLetterService;

    @Override
    public Response getLookupValues(String code) {
        List<LookupValue> values = authLetterService.getLookupValues(code);
        return Response.ok(ApiResponse.success(values)).build();
    }

    @Override
    public Response getOrgTree() {
        Object tree = authLetterService.getOrgTree();
        return Response.ok(ApiResponse.success(tree)).build();
    }
}