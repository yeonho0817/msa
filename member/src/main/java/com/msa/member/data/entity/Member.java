package com.msa.member.data.entity;

import com.msa.member.data.enumerate.Permission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("이름")
    @Column(length = 255, nullable = false)
    private String name;

    @Comment("로그인ID")
    @Column(name="username", nullable = false)
    private String username;

    @Comment("패스워드")
    private String password;

    @Comment("전화번호")
    private String phoneNumber;

    @Comment("권한")
    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private Permission permission;

    @Comment("계정만료여부")
    @Column(nullable = false) @ColumnDefault(value = "true")
    private boolean accountNonExpired;
    @Comment("계정락여부")
    @Column(nullable = false) @ColumnDefault(value = "true")
    private boolean accountNonLocked;
    @Comment("계정인증만료여부")
    @Column(nullable = false) @ColumnDefault(value = "true")
    private boolean credentialsNonExpired;
    @Comment("사용여부")
    @Column(nullable = false) @ColumnDefault(value = "true")
    private boolean enabled;

    @Comment("삭제여부")
    @Column(nullable = false) @ColumnDefault(value = "false")
    private boolean deleted = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + permission.getDescription()));
    }


}
