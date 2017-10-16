package fab.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Stores a list of books and pagination information.
 */
@Builder
@Getter
public class BookList {
  private List<Book> books;
  private Pagination pagination;
}
