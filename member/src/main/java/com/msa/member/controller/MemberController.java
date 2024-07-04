package com.msa.member.controller;

import com.msa.member.data.vo.SignInReqVo;
import com.msa.member.data.vo.SignUpReqVo;
import com.msa.member.data.vo.resVo.MemberDetailResVo;
import com.msa.member.data.vo.resVo.SignInResVo;
import com.msa.member.error.ErrorVO;
import com.msa.member.response.Response;
import com.msa.member.service.MemberService;
import com.msa.member.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.Callable;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @Operation(operationId = "signUp", summary = "signUp", description = "회원가입합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "4XX, 5XX", description = "회원가입 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))})
    @PostMapping("/sign-up")
    public Callable<Response<Void>> signUp(
            @Parameter(description = "회원가입 vo") @Valid @RequestBody SignUpReqVo reqVo
    ) {
        return () -> {
            memberService.signUp(reqVo);
            return Response.OK;
        };
    }

    @Operation(operationId = "sign-in", summary = "sign-in", description = "로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SignInResVo.class))}),
            @ApiResponse(responseCode = "4XX, 5XX", description = "로그인 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))})
    @PostMapping("/sign-in")
    public Callable<Response<SignInResVo>> signIn(
            @Parameter(description = "로그인 vo") @Valid @RequestBody SignInReqVo reqVo
    ) {
        return () -> Response.of(memberService.signIn(reqVo));
    }

    @Operation(operationId = "detail", summary = "detail", description = "사용자의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 상세 정보를 조회 성공",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemberDetailResVo.class))}),
            @ApiResponse(responseCode = "4XX, 5XX", description = "사용자의 상세 정보를 조회 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorVO.class)))})
    @GetMapping("/detail")
    public Callable<Response<MemberDetailResVo>> detail(
        @Parameter(hidden = true) @RequestHeader(Constants.X_MEMBER_ID) Long memberId
    ) {
        return () -> Response.of(memberService.detail(memberId));
    }

}
