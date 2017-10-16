package fab.web;

import fab.api.BooksApiHandler;
import fab.model.Book;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;

/**
 * Handler for serving HTML content for /edit endpoint.
 */
@Controller
@RequestMapping(value = {"/edit"})
public class BookEditController {

  /**
   * Book pre-filled with example values for new book creation.
   */
  private static final Book DEFAULT_BOOK;

  static {
    DEFAULT_BOOK = new Book();
    DEFAULT_BOOK.setAuthor("Example author");
    DEFAULT_BOOK.setTitle("Example title");
    DEFAULT_BOOK.setDescription("Example description");
    DEFAULT_BOOK.setIsbn("Example isbn");
    DEFAULT_BOOK.setPrintYear(1980);
    DEFAULT_BOOK.setReadAlready(false);
  }

  private final BooksApiHandler api;

  @Inject
  BookEditController(BooksApiHandler api) {
    this.api = api;
  }

  /**
   * Generates edit.html, either for new book creation (when 'id' is not set or book doesn't exist), or
   * for editing the existing book otherwise.
   */
  @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
  public String loadPage(@RequestParam(name = "id", required = false) Integer id, Model model) {
    // The page /edit.html without 'id' creates new book.
    Book book = (id == null) ? DEFAULT_BOOK : api.get(id);
    // If requested book is still not found, create new book.
    if (book == null) {
      book = DEFAULT_BOOK;
    }
    model.addAttribute("book", book);
    return "edit";
  }
}