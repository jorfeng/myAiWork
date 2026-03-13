package com.auth.letter.api.impl;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.auth.letter.api.ILookupApi;
import com.auth.letter.service.LookupService;
import com.auth.letter.vo.ApiResponse;
import com.auth.letter.vo.LookupValueVO;

@Component
@Path("/api/lookup")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LookupApiImpl implements ILookupApi {

    @Autowired
    private LookupService lookupService;

    @Override
    @GET
    @Path("/{type}")
    public ApiResponse<List<LookupValueVO>> getLookupValues(@PathParam("type") String type) {
        List<LookupValueVO> result = lookupService.getLookupValues(type);
        return ApiResponse.success(result);
    }
}