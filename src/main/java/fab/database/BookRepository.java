package fab.database;

import fab.model.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Simple interface for accessing books database.
 */
@Repository
public interface BookRepository extends CrudRepository<Book, Integer> {
}
