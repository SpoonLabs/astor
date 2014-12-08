package fr.inria.astor.core.loop.mutation.muttest;



import com.google.common.base.Function;

public enum ClassName implements Function<Class<?>, String> {
	INSTANCE;

	@Override
	//@Nullable
	public String apply(/*@Nullable*/ final Class<?> input) {
		return input.getName();
	}
}