package site.fitmon.gathering.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GatheringModifyRequest {

    @Schema(description = "수정할 모임 제목", example = "3대 600이 되어 보아요")
    private String title;

    @Schema(description = "모임 상세", example = "근 성장에 진심입니다.")
    @Size(max = 50, message = "50자 이하로 입력해주세요.")
    private String description;

    @Schema(description = "모임 대표 이미지 URL", example = "https://fitmon-bucket.s3.amazonaws.com/gatherings/af61233a-ed83-432c-b685-1d29a6c75de1_whale.jpg")
    private String imageUrl;

    @Schema(description = "모임 시작 일시", example = "2025-04-10T14:00:00")
    private LocalDateTime startDate;

    @Schema(description = "모임 종료 일시", example = "2025-05-20T16:00:00")
    private LocalDateTime endDate;

    @Schema(description = "모임 메인 장소", example = "서울시")
    private String mainLocation;

    @Schema(description = "모임 상세 장소", example = "송파구")
    private String subLocation;

    @Schema(description = "모임 최대 인원", example = "15")
    @Max(value = 30, message = "최대 인원은 30명을 초과할 수 없습니다.")
    private Integer totalCount;

    @Schema(description = "모임 태그 목록 (최대 3개)", example = "[\"오운완\", \"하체귀신\", \"프로틴추천\"]")
    private List<String> tags;

    @AssertTrue(message = "종료일은 시작일 이후여야 합니다.")
    private boolean isValidDateRange() {
        return startDate != null && endDate != null && !endDate.isBefore(startDate);
    }

}
