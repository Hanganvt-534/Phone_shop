package com.project.phone_shop.Repository;

import com.project.phone_shop.Entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntrospectRepository extends JpaRepository<InvalidatedToken, Long> {
}
