package com.huyeon.superspace.global.dto;

import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NotyEventDto {
    private Noty noty;
    private List<ReceivedNoty> receivers;
}
