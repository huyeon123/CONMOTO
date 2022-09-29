package com.huyeon.apiserver.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class GroupManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User manager;

    @OneToOne(fetch = FetchType.LAZY)
    private WorkGroup group;


    public GroupManager(User manager, WorkGroup group) {
        this.group = group;
        this.manager = manager;
    }
}
