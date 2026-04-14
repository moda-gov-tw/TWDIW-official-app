package gov.moda.dw.manager.util;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageUtils {

  public static final Pageable MAX_CONTENT = PageRequest.of(0, Integer.MAX_VALUE);

  public static <T> Page<T> convertToPage(List<T> source, Pageable pageable) {
    int start = (int) pageable.getOffset();
    int end = (start + pageable.getPageSize()) > source.size() ? source.size() : (start + pageable.getPageSize());
    Page<T> result = new PageImpl<>(source.subList(start, end), pageable, source.size());
    return result;
  }
}
