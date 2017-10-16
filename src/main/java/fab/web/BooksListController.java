package fab.web;

import fab.api.BooksApiHandler;
import fab.model.BookList;
import fab.model.Filter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;

/**
 * Handler for serving HTML content for /books endpoint.
 */
@Controller
@RequestMapping(value = {"/books", "/"})
public class BooksListController {

  private final BooksApiHandler api;

  @Inject
  BooksListController(BooksApiHandler api) {
    this.api = api;
  }

  @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
  public String loadPage(@RequestParam(name = "page", defaultValue = "0") Integer start,
                         @RequestParam(name = "substring", defaultValue = "") String substring,
                         @RequestParam(name = "before", defaultValue = "") String before,
                         @RequestParam(name = "after", defaultValue = "") String after,
                         @RequestParam(name = "unread", defaultValue = "false") Boolean unread,
                         Model model) {
    // Get one page of books matching the filter.
    BookList bookList = api.list(start, Filter.create(substring, before, after, unread));

    model.addAttribute("books", bookList.getBooks());
    model.addAttribute("pagination", bookList.getPagination());
    return "books";
  }
}