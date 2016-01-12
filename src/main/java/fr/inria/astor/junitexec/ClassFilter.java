package fr.inria.astor.junitexec;

public interface ClassFilter {
	boolean acceptClass(Class<?> clazz);
	boolean acceptClassName(String className);
	boolean acceptInnerClass();
	boolean searchInJars();
}