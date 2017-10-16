package fab.api;

import fab.database.BookRepository;
import fab.model.Book;
import fab.model.BookList;
import fab.model.Filter;
import fab.model.Pagination;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler for RESTful API for accessing books.
 * <p>
 * Returns JSON objects, and provides the following endpoints:
 * GET /v1/books/{id} - get a single book by id.
 * GET /v1/books?sort={s}&page={n}&substring={x}&etc - get one page of books (size 10) from the book storage.
 * DELETE /v1/books/{id} - delete a book.
 * POST /v1/books - create new book (request body is JSON Book object).
 * POST /v1/books/{id} - update existing book.
 * POST /v1/books/{id}/read - mark the book as "read already".
 */
@RestController
@RequestMapping("/v1/books")
public class BooksApiHandler {

  private static final int PAGE_SIZE = 10;

  private final BookRepository repository;

  private final EntityManager entityManager;

  @Inject
  public BooksApiHandler(BookRepository repository, EntityManager entityManager) {
    this.repository = repository;
    this.entityManager = entityManager;
  }

  /**
   * Reads one book by id.
   */
  @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Book get(@PathVariable(value = "id") Integer id) {
    return repository.findOne(id);
  }

  /**
   * Reads 10 books starting from {@code start}, sorted by {@code sort} criteria.
   */
  @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public BookList list(@RequestParam(name = "page", defaultValue = "0") Integer start) {
    return list(start, Filter.builder().build());
  }

  /**
   * Reads 10 books starting from {@code start}, sorted, and filtered according to the filter.
   */
  public BookList list(@RequestParam(name = "page", defaultValue = "0") Integer start,
                       Filter filter) {
    if (start < 0) {
      // Return empty response if start page is invalid.
      return BookList.builder()
                 .pagination(Pagination.builder().build())
                 .build();
    }

    // Build a query, looking approximately like this:
    // select * from book where author like 'x' or title like 'x' ... and readAlready = false order by id asc.
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

    CriteriaQuery<Book> query = criteriaBuilder.createQuery(Book.class);

    Root<Book> from = query.from(Book.class);

    // TODO: support different sorting criteria, if needed.
    query.orderBy(criteriaBuilder.asc(from.get("id")));

    // This adds 'where' clause to the query when filter has some restriction criteria.
    addFilterPredicate(query, criteriaBuilder, from, filter);

    TypedQuery<Book> typedQuery = entityManager.createQuery(query.select(from));

    // Request PAGE_SIZE + 1 results, so we know if next page exists.
    typedQuery.setFirstResult(start * PAGE_SIZE);
    typedQuery.setMaxResults(PAGE_SIZE + 1);

    // Read one page (+1) of books passing the filter.
    List<Book> books = typedQuery.getResultList();

    boolean hasNextPage = books.size() > PAGE_SIZE;
    if (hasNextPage) {
      books = books.subList(0, PAGE_SIZE);
    }

    // Set -1 as previous or next page if they don't exist.
    Pagination pagination = Pagination.builder()
                                .prev(start - 1)
                                .next(hasNextPage ? start + 1 : -1)
                                .build();

    return BookList.builder()
               .books(books)
               .pagination(pagination)
               .build();
  }

  /**
   * Deletes specified book.
   */
  @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
  public void delete(@PathVariable(value = "id") Integer id) {
    repository.delete(id);
  }

  /**
   * Marks one book as read.
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{id}/read")
  public void markRead(@PathVariable(value = "id") Integer id) {
    Book book = repository.findOne(id);
    if (book == null || book.getReadAlready()) {
      return;
    }

    book.setReadAlready(true);
    repository.save(book);
  }

  /**
   * POST to /v1/books/id updates the existing book.
   */
  @RequestMapping(method = RequestMethod.POST, value = "/{id}")
  public void update(@PathVariable(value = "id") Integer id, @RequestBody Book book) {
    if (!isValid(book)) {
      throw new InvalidBookException();
    }

    Book existing = repository.findOne(id);
    if (existing == null) {
      // Return 404 - user updates non-existing resource.
      throw new BookNotFoundException();
    }

    if (book.getId() != existing.getId()) {
      // Book object's ID doesn't match resource id - this is a deliberate bad request from the user.
      throw new InvalidBookException();
    }

    // When the book is edited, drop readAlready flag as requested.
    book.setReadAlready(false);

    // Editing the author is forbidden - overwrite if someone managed to do it via POST.
    book.setAuthor(existing.getAuthor());

    repository.save(book);
  }

  /**
   * POST to /v1/books creates a new book.
   */
  @RequestMapping(method = RequestMethod.POST)
  public void create(@RequestBody Book book) {
    // When create is called, the Book object is ready, and the id should be null.
    // Drop the id just in case
    book.setId(null);
    repository.save(book);
  }

  /**
   * Checks if the Book passed as JSON object is valid.
   */
  private boolean isValid(Book book) {
    return (book.getTitle() != null
                && book.getDescription() != null
                && book.getAuthor() != null
                && book.getIsbn() != null
                && book.getPrintYear() != null);
    // book.readAlready is allowed to be null, because users cannot edit it directly in the GUI.
  }

  private void addFilterPredicate(CriteriaQuery<Book> query, CriteriaBuilder criteria, Root<Book> from, Filter filter) {
    List<Predicate> predicates = new ArrayList<>();

    if (!filter.getSubstring().isEmpty()) {
      // Search for substring in any of [title, author, description, isbn].
      String substring = "%" + filter.getSubstring() + "%";
      predicates.add(criteria.or(
          criteria.like(from.get("title"), substring),
          criteria.like(from.get("author"), substring),
          criteria.like(from.get("description"), substring),
          criteria.like(from.get("isbn"), substring)
      ));
    }

    if (filter.getAfter() != null) {
      predicates.add(criteria.greaterThan(from.get("printYear"), filter.getAfter()));
    }

    if (filter.getBefore() != null) {
      predicates.add(criteria.lessThan(from.get("printYear"), filter.getBefore()));
    }

    if (filter.getUnread()) {
      predicates.add(criteria.isFalse(from.get("readAlready")));
    }

    if (!predicates.isEmpty()) {
      query.where(criteria.and(predicates.toArray(new Predicate[] {})));
    }
  }
}
