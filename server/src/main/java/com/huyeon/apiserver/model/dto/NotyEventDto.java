package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.Noty;
import com.huyeon.apiserver.model.entity.ReceivedNoty;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NotyEventDto {
    private Noty noty;
    private List<ReceivedNoty> receivers;
}
