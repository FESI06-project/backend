package site.fitmon.challenge.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.fitmon.auth.domain.CustomUserDetails;
import site.fitmon.challenge.dto.request.ChallengeCreateRequest;
import site.fitmon.challenge.dto.request.ChallengeEvidenceRequest;
import site.fitmon.challenge.dto.request.ChallengeSearchCondition;
import site.fitmon.challenge.dto.request.ChallengeSearchCondition.ChallengeStatus;
import site.fitmon.challenge.dto.response.ChallengeCreateResponse;
import site.fitmon.challenge.dto.response.GatheringChallengesResponse;
import site.fitmon.challenge.dto.response.PopularChallengeResponse;
import site.fitmon.challenge.service.ChallengeService;
import site.fitmon.common.dto.ApiResponse;
import site.fitmon.common.dto.SliceResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChallengeController implements ChallengeSwaggerController {

    private final ChallengeService challengeService;

    @PostMapping("/gatherings/{gatheringId}/challenges")
    public ResponseEntity<ChallengeCreateResponse> createChallenge(
        @Valid @RequestBody ChallengeCreateRequest request,
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long challenge = challengeService.createChallenge(request, gatheringId, userDetails.getUsername());

        return ResponseEntity.ok(new ChallengeCreateResponse(challenge));
    }

    @PostMapping("/challenges/{challengeId}/verification")
    public ResponseEntity<ApiResponse> verifyChallenge(
        @Valid @RequestBody ChallengeEvidenceRequest request,
        @PathVariable Long challengeId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        challengeService.verifyChallenge(request, challengeId, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.of("챌린지 인증 성공"));
    }

    @PostMapping("/challenges/{challengeId}/participants")
    public ResponseEntity<ApiResponse> joinChallenge(
        @PathVariable Long challengeId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

        challengeService.joinChallenge(challengeId, userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.of("챌린지 참가 성공"));
    }

    @GetMapping("/challenges")
    public ResponseEntity<List<PopularChallengeResponse>> getPopularChallenges() {
        return ResponseEntity.ok(challengeService.getPopularChallenges());
    }

    @GetMapping("/gatherings/{gatheringId}/challenges")
    public ResponseEntity<SliceResponse<GatheringChallengesResponse>> getGatheringChallenges(
        @RequestParam(required = false) ChallengeStatus status,
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int pageSize) {

        PageRequest pageable = PageRequest.of(page, pageSize, Sort.by(Direction.DESC, "createdAt"));
        ChallengeSearchCondition condition = new ChallengeSearchCondition(status);
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(challengeService.getGatheringChallenges(condition, gatheringId, email, pageable));
    }

    @DeleteMapping("/challenges/{challengeId}")
    public ResponseEntity<ApiResponse> deleteChallenge(
        @PathVariable Long challengeId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        challengeService.deleteChallenge(challengeId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of("챌린지 삭제 완료"));
    }

    @GetMapping("/gatherings/{gatheringId}/challenges/all")
    public ResponseEntity<List<GatheringChallengesResponse>> getAllGatheringChallenges(
        @PathVariable Long gatheringId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        List<GatheringChallengesResponse> challenges = challengeService.getAllGatheringChallenges(gatheringId, email);
        return ResponseEntity.ok(challenges);
    }
}
