package com.msa.member.data.repository;

import com.msa.member.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsernameAndDeletedAndEnabledAndAccountNonLocked(String username, boolean deleted, boolean enabled, boolean accountNonLocked);
}
