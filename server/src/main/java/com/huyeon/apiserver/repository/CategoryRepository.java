package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Category;
import com.huyeon.apiserver.model.entity.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByGroup(Groups group);

    List<Category> findAllByParent(Category parent);

    Optional<Category> findByName(String categoryName);

    void deleteAllByGroup(Groups group);
}
