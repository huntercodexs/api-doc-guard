package com.huntercodexs.sample.config.oauth2.repository;

import com.huntercodexs.sample.config.oauth2.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    @Query(value = "SELECT * FROM RoleEntity WHERE id = ?1",nativeQuery = true)
    RoleEntity findRole(Long id);
}
