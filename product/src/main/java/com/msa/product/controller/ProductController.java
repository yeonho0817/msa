package com.msa.product.controller;

import com.msa.product.error.ErrorVO;
import com.msa.product.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    @Operation(operationId = "test", summary = "test", description = "test")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "test 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "4XX, 5XX", description = "test 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))})
    @GetMapping("")
    public Callable<Response<Void>> test(
    ) {
        log.info("2");
        return () -> Response.OK;
    }

    @Operation(operationId = "test", summary = "test", description = "test")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "test 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "4XX, 5XX", description = "test 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))})
    @GetMapping("/no")
    public Callable<Response<Void>> test2(
    ) {
        log.info("no 2");
        return () -> Response.OK;
    }

}
