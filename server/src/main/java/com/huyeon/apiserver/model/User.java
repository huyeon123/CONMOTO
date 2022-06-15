package com.huyeon.apiserver.model;

import com.huyeon.apiserver.model.listener.Auditable;
import com.huyeon.apiserver.model.listener.PersistListener;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(value = {AuditingEntityListener.class, PersistListener.class})
public class User extends AuditEntity implements Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    private String password;

    private LocalDate birthday;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", insertable = false, updatable = false) //참조 테이블은 건드리지 않음(오작동 및 성능하락 방지)
    private List<Board> boardList = new ArrayList<>();
}
