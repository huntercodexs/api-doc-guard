package com.huntercodexs.sample.apidocguard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiDocGuardRepository extends JpaRepository<ApiDocGuardEntity, Long> {
    @Query(value = "SELECT * FROM api_doc_guard adg WHERE adg.username = ?1 AND adg.password = ?2", nativeQuery = true)
    ApiDocGuardEntity findByLogin(String username, String password);
}
