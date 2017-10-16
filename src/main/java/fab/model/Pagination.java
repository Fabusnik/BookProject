package fab.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Pagination {
  /**
   * Ordinal number of the previous page, or -1 if it doesn't exist.
   */
  @Builder.Default
  private int prev = -1;
  /**
   * Ordinal number of the next page, or -1 if it doesn't exist.
   */
  @Builder.Default
  private int next = -1;
}
