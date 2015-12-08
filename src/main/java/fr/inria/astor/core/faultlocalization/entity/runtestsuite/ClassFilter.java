package fr.inria.astor.core.faultlocalization.entity.runtestsuite;

public interface ClassFilter {
	boolean acceptClass(Class<?> clazz);
	boolean acceptClassName(String className);
	boolean acceptInnerClass();
	boolean searchInJars();
}