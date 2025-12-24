package com.zipjung.backend.dto;

import lombok.Builder;

@Builder
public class RankTop5SpotDto {
    // TODO: 장소 이름 클릭하면 바로 이동할 수 있도록 주소 담아주기
    private String rankSpot1;
    private String rank1Url;

    private String rankSpot2;
    private String rank2Url;

    private String rankSpot3;
    private String rank3Url;

    private String rankSpot4;
    private String rank4Url;

    private String rankSpot5;
    private String rank5Url;
}
