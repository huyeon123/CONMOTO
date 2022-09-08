package com.huyeon.apiserver.model.dto;

import com.huyeon.apiserver.model.entity.Noty;
import com.huyeon.apiserver.model.entity.ReceivedNoty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotyEventDto {
    private Noty noty;
    private List<ReceivedNoty> receivers;
}
