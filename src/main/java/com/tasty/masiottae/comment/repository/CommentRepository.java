package com.tasty.masiottae.comment.repository;

import com.tasty.masiottae.comment.domain.Comment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c left join fetch c.account a "
        + "left join fetch c.menu ")
    Optional<Comment> findByIdFetch(Long id);

}
