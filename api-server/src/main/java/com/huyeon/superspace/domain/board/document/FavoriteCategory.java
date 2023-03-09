package com.huyeon.superspace.domain.board.document;

import com.huyeon.superspace.global.model.DocumentAudit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.LinkedList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "favorite_category")
public class FavoriteCategory extends DocumentAudit {
    @Id
    private String id;

    private String userEmail;

    private String groupUrl;

    private List<String> categoryIds;

    public FavoriteCategory(String userEmail, String groupUrl) {
        this.userEmail = userEmail;
        this.groupUrl = groupUrl;
        this.categoryIds = new LinkedList<>();
    }
}
