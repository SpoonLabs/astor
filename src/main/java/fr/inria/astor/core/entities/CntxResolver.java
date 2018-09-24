package fr.inria.astor.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.impl.CtPathElement;
import spoon.reflect.path.impl.CtPathImpl;

public class CntxResolver {

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	public Cntx<?> retrieveCntx(ModificationPoint modificationPoint) {
		return retrieveCntx(modificationPoint.getCodeElement());
	}

	@SuppressWarnings("unused")
	public Cntx<?> retrieveCntx(CtElement element) {
		Cntx<Object> context = new Cntx<>(determineKey(element));
		retrievePosition(element, context);
		retrieveVarsInScope(element, context);
		retrieveMethodInformation(element, context);
		retrieveParentTypes(element, context);
		retrievePath(element, context);

		return context;
	}

	private void retrievePosition(CtElement element, Cntx<Object> context) {
		if (element.getPosition() != null && element.getPosition().getFile() != null) {
			context.getInformation().put(CNTX_Property.FILE_LOCATION,
					element.getPosition().getFile().getAbsolutePath());

			context.getInformation().put(CNTX_Property.LINE_LOCATION, element.getPosition().getLine());
		} else {
			context.getInformation().put(CNTX_Property.FILE_LOCATION, "");
			context.getInformation().put(CNTX_Property.LINE_LOCATION, "");

		}
		CtType parentClass = element.getParent(spoon.reflect.declaration.CtType.class);

		context.getInformation().put(CNTX_Property.PARENT_CLASS,
				(parentClass != null) ? parentClass.getQualifiedName() : "");

	}

	public Object determineKey(CtElement element) {
		String key = null;
		if (element.getPosition() != null && element.getPosition().getFile() != null) {
			key = element.getPosition().getFile().getName().toString();
		} else {
			key = element.getShortRepresentation();// To see.
		}
		return key;
	}

	private void retrieveVarsInScope(CtElement element, Cntx<Object> context) {
		// Vars in scope at the position of element
		context.getInformation().put(CNTX_Property.VARS_IN_SCOPE, VariableResolver.searchVariablesInScope(element));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void retrieveMethodInformation(CtElement element, Cntx<Object> context) {
		//
		CtMethod parentMethod = element.getParent(CtMethod.class);
		if (parentMethod != null) {
			// Return
			context.getInformation().put(CNTX_Property.METHOD_RETURN_TYPE, parentMethod.getType().getQualifiedName());
			// Param
			List<CtParameter> parameters = parentMethod.getParameters();
			List<String> parametersTypes = new ArrayList<>();
			for (CtParameter ctParameter : parameters) {
				parametersTypes.add(ctParameter.getType().getSimpleName());
			}
			context.getInformation().put(CNTX_Property.METHOD_PARAMETERS, parametersTypes);

			// Modif
			context.getInformation().put(CNTX_Property.METHOD_MODIFIERS, parentMethod.getModifiers());

			// Comments
			context.getInformation().put(CNTX_Property.METHOD_COMMENTS, parentMethod.getComments());

		}
	}

	private void retrievePath(CtElement element, Cntx<Object> context) {
		CtPath path = element.getPath();

		context.getInformation().put(CNTX_Property.SPOON_PATH, path.toString());
		if (path instanceof CtPathImpl) {
			CtPathImpl pi = (CtPathImpl) path;
			List<CtPathElement> elements = pi.getElements();
			List<String> paths = elements.stream().map(e -> e.toString()).collect(Collectors.toList());
			context.getInformation().put(CNTX_Property.PATH_ELEMENTS, paths);
		}

	}

	private void retrieveParentTypes(CtElement element, Cntx<Object> context) {
		CtElement parent = element.getParent();
		List<String> parentNames = new ArrayList<>();
		do {
			parentNames.add(parent.getClass().getSimpleName());
			parent = parent.getParent();
		} while (parent != null);

		if (!parentNames.isEmpty()) {
			context.getInformation().put(CNTX_Property.PARENTS_TYPE, parentNames);
		}
	}

}
