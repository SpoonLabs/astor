package fr.inria.astor.core.faultlocalization.cocospoon.code;

import fr.inria.astor.core.faultlocalization.cocospoon.metrics.Metric;

/**
 * Created by bdanglot on 10/3/16.
 */
public class StatementSourceLocation extends AbstractStatement {

	private final SourceLocation location;

	public StatementSourceLocation(Metric metric, SourceLocation location) {
		super(metric);
		this.location = location;
	}

	public SourceLocation getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return "StatementSourceLocation [location=" + location + ", getSuspiciousness()=" + getSuspiciousness() + "]";
	}
	
	
	
}
