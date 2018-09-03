package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.candidate;

@SuppressWarnings("rawtypes")
public class ExpressionValueCandidate implements Comparable<ExpressionValueCandidate> {
	private Class candClass;
	private String name;
	private Object value;

	public ExpressionValueCandidate(Class clazz, String name, Object value) {
		this.candClass = clazz;
		this.name = name;
		this.value = value;
	}

	public Class getCandClass() {
		return candClass;
	}

	public void setCandClass(Class candClass) {
		this.candClass = candClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean valueMatch(ExpressionValueCandidate cand) {
		return value == null ? cand.getValue() == null : value.equals(cand.getValue());
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		return ((ExpressionValueCandidate) o).getName().equals(name) 
				&& ((ExpressionValueCandidate)o).getCandClass().toString().equals(candClass.toString());
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public int compareTo(ExpressionValueCandidate o) {
		return name.compareTo(o.getName());
	}
}
