package com.ncc.repository;
import com.ncc.domain.Book;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Book entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
