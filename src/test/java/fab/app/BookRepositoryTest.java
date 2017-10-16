package fab.app;

import fab.api.BooksApiHandler;
import fab.model.Book;
import fab.model.BookList;
import fab.model.Filter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BookRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private BooksApiHandler handler;

  @Before
  public void setUp() {
    // Create 25 books.
    for (int i = 0; i < 25; i++) {
      Book book = new Book();
      book.setAuthor("author-" + (i % 3));  // Each author wrote 3 books.
      book.setTitle("title-" + i);
      book.setReadAlready(i % 2 == 0);  // Mark every second book as read.
      book.setPrintYear(1980 + i);

      entityManager.persist(book);
    }
    entityManager.flush();
  }

  @Test
  public void testGetFirstPage() {
    BookList result = handler.list(0);
    assertEquals(10, result.getBooks().size());
    assertEquals(1, result.getPagination().getNext());
    assertEquals(-1, result.getPagination().getPrev());
  }

  @Test
  public void testGetLastPage() {
    BookList result = handler.list(2);
    assertEquals(5, result.getBooks().size());
    assertEquals(-1, result.getPagination().getNext());
    assertEquals(1, result.getPagination().getPrev());
  }

  @Test
  public void testFilterOnlyUnread() {
    Filter filter = Filter.builder()
                        .unread(true)
                        .build();

    BookList result = handler.list(0, filter);
    assertEquals(10, result.getBooks().size());

    // Check that only unread books are returned.
    assertTrue(result.getBooks().stream().noneMatch(Book::getReadAlready));
  }

  @Test
  public void testFilterOnlyUnread_lastPage() {
    Filter filter = Filter.builder()
                        .unread(true)
                        .build();

    // There is only 12 "read" books.
    BookList result = handler.list(1, filter);
    assertEquals(2, result.getBooks().size());
    assertEquals(-1, result.getPagination().getNext());

    // Check that only unread books are returned.
    assertTrue(result.getBooks().stream().noneMatch(Book::getReadAlready));
  }

  @Test
  public void testFilterByAuthor() {
    Filter filter = Filter.builder()
                        .substring("author-2")
                        .build();

    // Each author wrote ~1/3 of the books.
    BookList result = handler.list(0, filter);
    assertEquals(8, result.getBooks().size());
    // There is only one page of results.
    assertEquals(-1, result.getPagination().getPrev());
    assertEquals(-1, result.getPagination().getNext());

    // Check that only books by author-2 are returned.
    assertTrue(result.getBooks().stream()
                   .map(Book::getAuthor)
                   .allMatch(author -> author.equals("author-2")));
  }

  @Test
  public void testFilterByAuthor_substring() {
    Filter filter = Filter.builder()
                        .substring("thor-0")
                        .build();

    // Each author wrote ~1/3 of the books.
    BookList result = handler.list(0, filter);
    assertEquals(9, result.getBooks().size());
    // There is only one page of results.
    assertEquals(-1, result.getPagination().getPrev());
    assertEquals(-1, result.getPagination().getNext());

    // Check that only books by author-2 are returned.
    assertTrue(result.getBooks().stream()
                   .map(Book::getAuthor)
                   .allMatch(author -> author.equals("author-0")));
  }

  @Test
  public void testFilterByAuthorAndYear() {
    Filter filter = Filter.builder()
                        .substring("author-2")
                        .after(1986)
                        .build();

    // Each author wrote ~1/3 of the books.
    BookList result = handler.list(0, filter);
    assertEquals(6, result.getBooks().size());
    // There is only one page of results.
    assertEquals(-1, result.getPagination().getPrev());
    assertEquals(-1, result.getPagination().getNext());

    // Check that only books by author-2, published after 1986 are returned.
    assertTrue(result.getBooks().stream()
                   .allMatch(book -> book.getAuthor().equals("author-2") && book.getPrintYear() > 1986));
  }
}