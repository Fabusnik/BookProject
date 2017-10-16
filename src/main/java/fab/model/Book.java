package fab.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Data class representing a book.
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity
@Table(name = "book")
public class Book {

  @Id
  @GeneratedValue
  private Integer id;

  @Column
  @NonNull
  private String title = "";

  @Column
  @NonNull
  private String description = "";

  @Column
  @NonNull
  private String author = "";

  @Column
  @NonNull
  private String isbn = "";

  @Column(name = "printYear")
  @NonNull
  private Integer printYear = 1980;

  @Column(name = "readAlready")
  @NonNull
  private Boolean readAlready = false;
}
