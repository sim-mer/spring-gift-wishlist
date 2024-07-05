package gift.member.presentation.restcontroller;

import gift.global.authentication.jwt.JwtToken;
import gift.member.business.service.MemberService;
import gift.member.presentation.dto.RequestMemberDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<JwtToken> registerMember(@RequestBody @Valid RequestMemberDto requestMemberDto) {
        var jwtToken = memberService.registerMember(requestMemberDto.toMemberRegisterDto());
        return ResponseEntity.status(HttpStatus.CREATED).body(jwtToken);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtToken> loginMember(@RequestBody @Valid RequestMemberDto requestMemberDto) {
        var jwtToken = memberService.loginMember(requestMemberDto.toMemberLoginDto());
        return ResponseEntity.ok(jwtToken);
    }

}