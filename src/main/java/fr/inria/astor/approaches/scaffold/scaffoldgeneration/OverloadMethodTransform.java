package fr.inria.astor.approaches.scaffold.scaffoldgeneration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.inria.astor.approaches.scaffold.ScaffoldRepairEngine;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.generator.ExpressionGenerator;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.libinfo.LibParser;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;

public class OverloadMethodTransform extends TransformStrategy {

	@SuppressWarnings("rawtypes")
	private Map<String, CtExpression> typeCandidates = new HashMap<String, CtExpression>();
	private String querytype;
	private LibParser parser;
	
	@SuppressWarnings("serial")
	private final static Map<String, String> objToPrim = new HashMap<String, String>() {
		{
			put("java.lang.Integer", "int");
			put("java.lang.Double", "double");
			put("java.lang.Long", "long");
			put("java.lang.Float", "float");
			put("java.lang.Boolean", "boolean");
		}
	};
	
	public OverloadMethodTransform (ModificationPoint modPoint, int modificationPointIndex, MutationSupporter supporter, ProjectRepairFacade facade, 
			ScaffoldRepairEngine engine) {
		super(modPoint, modificationPointIndex, supporter,facade, engine);
		pre = "OMT";
		querytype="EXP";
		parser=new LibParser(modPoint, supporter, facade);
		parser.analyzeLib();
	}
	
	@Override
	public List<String> transform() {
		return super.transform();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		super.visitCtConstructorCall(ctConstructorCall);
		
		String type = ctConstructorCall.getType().getQualifiedName();
		List<CtExpression<?>>  argumentlist = ctConstructorCall.getArguments();
		List<String> orig = resolveTypes(argumentlist);

		CtClass classname = parser.getClassMap().get(type); 
		
		if(classname!=null) {
			Set<CtConstructor<T>> constructors=classname.getConstructors();
            for(CtConstructor constructor:constructors) {
            	List<CtParameter> paramlist=constructor.getParameters();
            	List<String> target=resolveParams(paramlist);
            	transformOneMethod(orig,target,ctConstructorCall);
            }
		} else {
			List<Class[]> params = parser.fetchMethods(type, type);
			for (Class[] p : params)
				transformOneConstructor(orig, ctConstructorCall, p);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private List<String> resolveParams(List<CtParameter> origParam) {
		List<String> types = new ArrayList<String>();
		for (int i = 0; i < origParam.size(); i++) {
			String type =  origParam.get(i).getType().getQualifiedName();
		//	type = objToPrim.containsKey(type) ? objToPrim.get(type) : type;
			types.add(type);
			if (!typeCandidates.containsKey(type))
				typeCandidates.put(type, ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, type, querytype));
		}
		return types; 
	}
	
	private List<String> resolveTypes(List<CtExpression<?>> origParam) {
		List<String> types = new ArrayList<String>();
		for (int i = 0; i < origParam.size(); i++) {
			String type = origParam.get(i).getType().getQualifiedName();
		//	type = objToPrim.containsKey(type) ? objToPrim.get(type) : type;
			types.add(type);
			if (!typeCandidates.containsKey(type))
				typeCandidates.put(type, ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, type, querytype));
		}
		return types;
	}
	
	@SuppressWarnings("rawtypes")
	private void transformOneConstructor(List<String> origTypes, CtAbstractInvocation invocation, Class[] param) {
		List<String> target = new ArrayList<String>();
		for (Class c : param) {
			String name = c.getName();
		//	name = name.substring(name.lastIndexOf(".")+1);
			target.add(name);
			if (!typeCandidates.containsKey(name))
				typeCandidates.put(name, ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, name, querytype));
		}
		transformOneMethod(origTypes, target, invocation);
	}
	
	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		super.visitCtInvocation(invocation);
		
		if (fetchInClassOverload(invocation, this.modificationPoint.getCtClass())) {
			 log.info("sucesfully find overload methdods in the same class");
		 } else {
			 log.info("seek overload methdods in the imported libs");
			 fetchLibOverload(invocation);	
		 }
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private boolean fetchInClassOverload(CtInvocation call, CtClass classname) {
		
		if(!call.getTarget().getType().getQualifiedName().equals(classname.getQualifiedName())) {
			return false;
		}
		
		String name = call.getExecutable().getSimpleName();
		Set<CtMethod> methods=classname.getMethods();
		List<CtMethod> overloadmethods = new ArrayList<CtMethod>();
		for(CtMethod method: methods) {
			if(method.getSimpleName().equals(name))
				overloadmethods.add(method);
		}
		
		if (overloadmethods.size() == 0)
			return false;
		if (overloadmethods.size()== 1) {
			return true;
		}
		
		List<String> origTypes = resolveTypes(call.getArguments());

		if (origTypes == null)
			return true;
		for (CtMethod mtd : overloadmethods) {
			transformOneMethod(origTypes, resolveParams(mtd.getParameters()), call);
		}
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void fetchLibOverload(CtInvocation call) {
        
		List<String> origTypes = resolveTypes(call.getArguments());
		CtExpression exp = call.getTarget();
		String typename=exp.getType().getQualifiedName();
		if(typename==null||typename.isEmpty())
			return;
		
        CtClass classname = parser.getClassMap().get(typename); 

		if(classname!=null) {
			Set<CtMethod> overloadmethods=classname.getMethods();
            for(CtMethod methodoverloaded : overloadmethods) {
            	if(call.getExecutable().getSimpleName().equals(methodoverloaded.getSimpleName())) {
            	  List<CtParameter> paramlist=methodoverloaded.getParameters();
            	  List<String> target=resolveParams(paramlist);
            	  transformOneMethod(origTypes,target, call);
            	}
            }
		} else {
			List<Class[]> params = parser.fetchMethods(typename, call.getExecutable().getSimpleName());
			for (Class[] p : params)
				transformOneConstructor(origTypes, call, p);
		}
	}
	
	public List<CtExpression<?>> cloneList(List<CtExpression<?>> list) {
	    List<CtExpression<?>> clone = new ArrayList<CtExpression<?>>(list.size());
	    for (CtExpression<?> item : list) clone.add(item.clone());
	    return clone;
	}

	@SuppressWarnings("rawtypes")
	private void transformOneMethod(List<String> origTypes, List<String> target, CtAbstractInvocation invocation) {
		// do not transform more than one delta
		List<CtExpression<?>> origin=invocation.getArguments();
		List<CtExpression<?>> copyed=cloneList(origin);
		
		if (target.size() - origTypes.size() > 1 || target.size() < origTypes.size())
	//	if (target.size() - origTypes.size() > 1)
			return;
		List<Edit> edits = editDistance(target, origTypes);
		
		if (edits == null)
			return;
		for (Edit e : edits) {
			switch (e.type) {
			case EDIT:
				CtExpression orig = copyed.get(e.id);
				if (!typeCandidates.containsKey(e.target))	break;
				copyed.set(e.id, typeCandidates.get(e.target));
				invocation.setArguments(copyed);
				saveSketchAndSynthesize();
				copyed.set(e.id, orig);
				invocation.setArguments(copyed);
				resoreDiskFile();
				break;
			case INSERT:
				if (!typeCandidates.containsKey(e.target))	break;
				copyed.add(e.id, typeCandidates.get(e.target));
				invocation.setArguments(copyed);
				saveSketchAndSynthesize();
				copyed.remove(e.id);
				invocation.setArguments(copyed);
				resoreDiskFile();
				break;
			case DELETE:
				orig = copyed.get(e.id);
				copyed.remove(e.id);
				invocation.setArguments(copyed);
				saveSketchAndSynthesize();
				copyed.add(e.id, orig);
				invocation.setArguments(copyed);
				resoreDiskFile();
				break;
			default:
				break;
			}
		}
	}

	private List<Edit> editDistance(List<String> target, List<String> orig) {
		int m = target.size(), n = orig.size();
		int[][] dp = new int[m + 1][n + 1];
		List<Edit> edits = new ArrayList<Edit>();
		int num = 0;
		for (int i = 0; i <= m; i++)
			dp[i][0] = i;
		for (int i = 0; i <= n; i++)
			dp[0][i] = i;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (target.get(i).equals(orig.get(j)))
					dp[i + 1][j + 1] = dp[i][j];
				else
					dp[i + 1][j + 1] = Math.min(dp[i][j] + 1, Math.min(dp[i][j + 1] + 1, dp[i + 1][j] + 1));
			}
		}
		int i = m, j = n;
		while (i > 0 && j > 0) {
			int min = Math.min(dp[i - 1][j - 1], Math.min(dp[i][j - 1], dp[i - 1][j]));
			if (min==dp[i - 1][j - 1] && dp[i - 1][j - 1] == dp[i][j]) {
				edits.add(0, new Edit(EditType.NOCHANGE, target.get(i - 1), orig.get(j - 1), j - 1));
				i--;
				j--;
				continue;
			}
			else if (dp[i - 1][j - 1] == min) {
				edits.add(0, new Edit(EditType.EDIT, target.get(i - 1), orig.get(j - 1), j - 1));
				i--;
				j--;
				num++;
			} else if (min == dp[i - 1][j]) {
				edits.add(0, new Edit(EditType.INSERT, target.get(i - 1), null, j));
				i--;
				num++;
			} else if (min == dp[i][j - 1]) {
				edits.add(0, new Edit(EditType.DELETE, null, orig.get(j - 1), j - 1));
				j--;
				num++;
			}
		}
		while (i > 0) {
			edits.add(0, new Edit(EditType.INSERT, target.get(--i), null, j));
			num++;
		}
		while (j > 0) {
			edits.add(0, new Edit(EditType.DELETE, target.get(--j), null, j));
			num++;
		}
		return num > 1 ? null : edits;
	}

	class Edit {
		EditType type;
		String orig;
		String target;
		int id;

		public Edit(EditType ty, String t, String o, int id) {
			type = ty;
			orig = o;
			target = t;
			this.id = id;
		}

		public String toString() {
			return type.toString() + "  " + orig + " " + target + " " + id;
		}
	}

	enum EditType {
		EDIT, INSERT, DELETE, NOCHANGE
	}
}
