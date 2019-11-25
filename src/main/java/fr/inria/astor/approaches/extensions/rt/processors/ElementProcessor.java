package fr.inria.astor.approaches.extensions.rt.processors;

import java.util.List;

import spoon.reflect.code.CtStatement;

/**
 * 
 * @author Matias Martinez
 *
 * @param <T>
 */
public abstract class ElementProcessor<T> {

	public abstract List<T> findElements(CtStatement stmts);

	public abstract List<T> classifyElements(CtStatement stmts);

}