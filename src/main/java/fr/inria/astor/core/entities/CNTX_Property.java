package fr.inria.astor.core.entities;

public enum CNTX_Property {

	// All vars in scope
	VARS_IN_SCOPE,
	// Return type of the parent method
	METHOD_RETURN_TYPE,
	// Type of the parents
	PARENTS_TYPE,
	//
	METHOD_PARAMETERS,
	//
	METHOD_MODIFIERS,
	//
	METHOD_COMMENTS,
	//
	CODE,
	//
	BUGGY_STATEMENT,
	//
	CODE_TREE,
	//
	FILE_LOCATION,
	//
	LINE_LOCATION,
	//
	SPOON_PATH,
	//
	PATH_ELEMENTS,
	//
	PARENT_CLASS,
	//
	VAR_NAME,
	//
	VARS,
	//
	VAR_TYPE,
	//
	VAR_VISIB,
	//
	VAR_MODIF,
	// Statement type:
	TYPE,
	// Involved relational/arithmetic operato
	involved_relation_bin_operators,
	//
	BIN_PROPERTIES,
	// whether involves
	involve_GE_relation_operators, involve_AND_relation_operators, involve_OR_relation_operators,
	involve_BITOR_relation_operators, involve_BITXOR_relation_operators, involve_BITAND_relation_operators,
	involve_EQ_relation_operators, involve_LT_relation_operators, involve_NE_relation_operators,
	involve_GT_relation_operators, involve_LE_relation_operators, involve_SL_relation_operators,
	involve_SR_relation_operators, involve_USR_relation_operators, involve_PLUS_relation_operators,
	involve_MINUS_relation_operators, involve_MUL_relation_operators, involve_DIV_relation_operators,
	involve_MOD_relation_operators, involve_INSTANCEOF_relation_operators,
	// involved unary
	involved_relation_unary_operators, UNARY_PROPERTIES,
	//// whether involves
	involve_POS_relation_operators, involve_NEG_relation_operators, involve_NOT_relation_operators,
	involve_COMPL_relation_operators, involve_PREINC_relation_operators, involve_PREDEC_relation_operators,
	involve_POSTINC_relation_operators, involve_POSTDEC_relation_operators,
	// Involves primitive type
	NUMBER_PRIMITIVE_VARS_IN_STMT,
	// Involves object reference,
	NUMBER_OBJECT_REFERENCE_VARS_IN_STMT,
//	If statement involves variables, whether has methods in scope that return the type of the involved variable	
	IS_METHOD_RETURN_TYPE_VAR,
//

	NUMBER_TOTAL_VARS_IN_STMT,

	// If statement involves variables, whether has methods in scope that take the
	// type of the involved variable as parameter

	IS_METHOD_PARAM_TYPE_VAR,
	// is there any other variable in scope that is similar in name We can have
	// based on Levenstein distance
	HAS_VAR_SIM_NAME,
	// For an involved variable, is there any other variable in scope that is
	// assigned to a certain function transformation of the involved variable
	HAS_VAR_IN_TRANSFORMATION,
	// Whether uses constants we can have
	USES_CONSTANT,
	// Whether uses enum we can have
	USES_ENUM,
	// If involves object reference, whether the variable has been assigned in other
	// statements after its initial introduction
	NR_VARIABLE_ASSIGNED,

	NR_VARIABLE_NOT_ASSIGNED,

	NR_OBJECT_ASSIGNED_LOCAL, NR_OBJECT_NOT_ASSIGNED_LOCAL,

	// If involves object reference, whether the variable has been used in other
	// statements after its initial introduction.
	NR_OBJECT_USED, NR_OBJECT_NOT_USED,

	// Is field (of an object type) initialization statement? If so, whether the
	// object type has other fields which are not initialized since the definition
	// of the object
	NR_FIELD_INCOMPLETE_INIT,
	// whether has other variables in scope that are type compatible
	HAS_VAR_SIM_TYPE,
	//
	PSPACE,
	//
	BUG_INFO,
	//
	PATCH_INFO,

	// The element corresponding to the patch
	PATCH_CODE_ELEMENT, PATCH_CODE_STATEMENT, POSITION, AFFECTED_PARENT, AFFECTED, OPERATION, AST_PARENT, AST,
	//
	S1_NOT_ASSIGNED, S2, S3, LE1, LE2, LE3, LE4, LE5, V1, V2, M1, M2,;

}
