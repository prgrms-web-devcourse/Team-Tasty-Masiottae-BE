package com.tasty.masiottae.account.domain;

import com.tasty.masiottae.comment.domain.Comment;
import com.tasty.masiottae.common.base.BaseTimeEntity;
import com.tasty.masiottae.likemenu.domain.LikeMenu;
import com.tasty.masiottae.menu.domain.Menu;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nick_name", unique = true, nullable = false)
    private String nickName;

    @Column(name = "image", unique = true)
    private String image;

    @Column(name = "sns_account")
    private String snsAccount;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menuList = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeMenu> likeMenuList = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    private Account(String email, String password, String nickName, String image, String snsAccount) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.image = image;
        this.snsAccount = snsAccount;
        this.role = Role.ACCOUNT;
        this.createdAt = LocalDateTime.now();
    }

    public static Account createAccount(String email, String password, String nickName, String image) {
        return Account.builder()
                .email(email)
                .password(password)
                .nickName(nickName)
                .image(image)
                .build();
    }

    public static Account createAccount(String email, String password, String nickName, String image, String snsAccount) {
        return Account.builder()
                .email(email)
                .password(password)
                .nickName(nickName)
                .image(image)
                .snsAccount(snsAccount)
                .build();
    }

    public void encryptPassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateSnsAccount(String snsAccount) {
        this.snsAccount = snsAccount;
    }

    public void updateImage(String image) {
        this.image = image;
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
    }

}