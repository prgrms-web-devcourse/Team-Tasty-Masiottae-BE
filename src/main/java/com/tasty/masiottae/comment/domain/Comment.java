package com.tasty.masiottae.comment.domain;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.common.base.BaseTimeEntity;
import com.tasty.masiottae.menu.domain.Menu;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "content", nullable = false)
    private String content;

    @Builder
    private Comment(Account account, Menu menu, String content) {
        this.account = account;
        this.menu = menu;
        this.content = content;
    }

    public static Comment createComment(Account account, Menu menu, String content) {
        Comment comment = new Comment(account, menu, content);
        account.addComment(comment);
        menu.addComment(comment);
        return comment;
    }

    public void changeContent(String newContent) {
        this.content = newContent;
    }
}
