package fab.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents search criteria specified from the GUI.
 */
@Setter
@Getter
@ToString
@Builder
public class Filter {

  @Builder.Default
  @NonNull
  private String substring = "";

  @Builder.Default
  private Integer before = null;

  @Builder.Default
  private Integer after = null;

  @NonNull
  @Builder.Default
  private Boolean unread = false;

  /**
   * Constructs the filter, ignoring invalid arguments.
   */
  public static Filter create(String substring, String before, String after, Boolean unread) {
    Filter.FilterBuilder builder = Filter.builder();
    if (substring != null && !substring.trim().isEmpty()) {
      builder.substring(substring);
    }

    Integer i = parseInteger(before);
    if (i != null) {
      builder.before(i);
    }

    i = parseInteger(after);
    if (i != null) {
      builder.after(i);
    }

    if (unread != null) {
      builder.unread(unread);
    }
    return builder.build();
  }

  private static Integer parseInteger(String from) {
    if (from == null) {
      return null;
    }
    try {
      return Integer.parseInt(from);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
