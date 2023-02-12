package com.huyeon.superspace.domain.board.document;

import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class FavoriteCategory extends DocumentAudit {
    private String userEmail;

    private String groupUrl;

    private List<String> categoryId;
}
