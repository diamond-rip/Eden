package rip.diamond.practice.util.menu.pagination;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class PageFilter<T> {

	@Getter private final String name;
	@Getter @Setter private boolean enabled;
	private final Predicate<T> predicate;

	public boolean test(T t) {
		return !enabled || predicate.test(t);
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof PageFilter && ((PageFilter) object).getName().equals(name);
	}

}
