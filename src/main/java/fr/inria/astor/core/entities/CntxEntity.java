package fr.inria.astor.core.entities;

/**
 * 
 * @author Matias Martinez
 *
 */
public enum CntxEntity {
	Annotation, AnnotationFieldAccess, ArrayAccess, ArrayRead, ArrayWrite, Assert, Assignment, BinaryOperator, Block,
	Case, Catch, CatchVariableImpl, CFlowBreak, CodeSnippetExpression, Comment, Conditional, Constructor,
	ConstructorCall, Do, Enum, EnumValue, Field, FieldAccess, FieldRead, FieldWrite, For, ForEach, If, Import,
	Interface, Invocation, JavaDoag, LabelledFlowBreak, Lambda, Literal, LocalVariable, Method, NewArray, NewClass,
	OperatorAssignment, Parameter, Return, SuperAccess, Synchronized, TargetedExpression, ThisAccess, Throw, Try,
	TryWithResource, Type, TypeAccess, TypeMember, UnaryOperator, VariableRead, VariableWrite, While,
}
