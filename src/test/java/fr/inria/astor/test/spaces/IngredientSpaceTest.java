package fr.inria.astor.test.spaces;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.VirtualFile;
import spoon.support.compiler.jdt.JDTSnippetCompiler;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.IFExpressionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.LoopExpressionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.BasicFixSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.FixLocationSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.GlobalBasicFixSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.IngredientAnalyzer;

/**
 * 
 * @author Matias Martinez
 *
 */
public class IngredientSpaceTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testLocalStrategy() throws JSAPException {
		
		List<AbstractFixSpaceProcessor<?>> procFix = new ArrayList<AbstractFixSpaceProcessor<?>>();
		procFix.add(new LoopExpressionFixSpaceProcessor());
		procFix.add(new IFExpressionFixSpaceProcessor());
		
		
		 FixLocationSpace ingredientSpace = new BasicFixSpace(procFix);
		 List classes = getScenario1Classes();
		 assertTrue(classes.size() == 2);
		 ingredientSpace.defineSpace(classes);
		 
		 List<CtCodeElement> ingredients = ingredientSpace.getFixSpace("ClassX");
		 assertEquals(3, ingredients.size());
		
		 //Randomly takes one element
		 CtElement ce1 =  ingredientSpace.getElementFromSpace("ClassX");
		 assertTrue(ingredients.contains(ce1));
			 
		 //Analysis by type
		 
		 List<CtCodeElement> ingredientsTypeUn = ingredientSpace.getFixSpace("ClassX","CtUnaryOperatorImpl");
		 assertEquals(1, ingredientsTypeUn.size());
		
		 List<CtCodeElement> ingredientsTypeBin = ingredientSpace.getFixSpace("ClassX","CtBinaryOperatorImpl");
		 assertEquals(2, ingredientsTypeBin.size());
		 
		 //Crossing results
		 
		 CtElement un= ingredientSpace.getElementFromSpace("ClassX","CtUnaryOperatorImpl");
		 assertTrue(ingredientsTypeUn.contains(un));
		 assertFalse(ingredientsTypeBin.contains(un));
		 
		 System.out.println("Space-->\n"+ingredientSpace);
		 
		 List<CtCodeElement> ingredients2 = ingredientSpace.getFixSpace("ClassY");
		 assertEquals(2, ingredients2.size());
		 
		 //Class not managed
		 List<CtCodeElement> ingredientsAll = ingredientSpace.getFixSpace("noClassZZZZ");
		 assertNull(ingredientsAll);
	
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testGlobalStrategy() throws JSAPException {
		//fail("Not yet implemented");
		
		List<AbstractFixSpaceProcessor<?>> procFix = new ArrayList<AbstractFixSpaceProcessor<?>>();
		procFix.add(new LoopExpressionFixSpaceProcessor());
		procFix.add(new IFExpressionFixSpaceProcessor());
		
		
		 FixLocationSpace ingredientSpace = new GlobalBasicFixSpace(procFix);
		 List classes = getScenario1Classes();
		 assertTrue(classes.size() == 2);
		 ingredientSpace.defineSpace(classes);
		 
		 List<CtCodeElement> ingredientsAll = ingredientSpace.getFixSpace("noClassZZZZ");
		 assertEquals(5, ingredientsAll.size());
		
				 
		 //Analysis by type
		 List<CtCodeElement> ingredientsTypeUn = ingredientSpace.getFixSpace("noClassZZZZ","CtUnaryOperatorImpl");
		 assertEquals(2, ingredientsTypeUn.size());
		
		 List<CtCodeElement> ingredientsTypeBin = ingredientSpace.getFixSpace("noClassZZZZ","CtBinaryOperatorImpl");
		 assertEquals(3, ingredientsTypeBin.size());
		 
		 //Crossing results
		 
		 CtElement un= ingredientSpace.getElementFromSpace("ClassX","CtUnaryOperatorImpl");
		 assertTrue(ingredientsTypeUn.contains(un));
		 assertFalse(ingredientsTypeBin.contains(un));
		 
		 System.out.println("Space-->\n"+ingredientSpace);
		
		
	
	}
	
	
	
	public List<CtSimpleType<?>> getScenario1Classes(){
		Factory factory = new Launcher().createFactory();

	
		String content = "" + "class ClassX {" + "public Object foo() {"
				//+ " Integer.toString(" + unboundVarAccess + ");"
				+" int a = 0; if(a>2){return this;}"
				+" while(a<100){a++;}"
				+" boolean found = false; while(!found){a++;}"
				+" return null;" + "}};";

		
		
		String content2 = "" + "class ClassY {" + "public Object foo() {"
				//+ " Integer.toString(" + unboundVarAccess + ");"
				+" int b = 0; "
				+ "if(b>2){return this;}"
			//	+" while(b<100){b++;}"
				+" boolean found2 = false; while(!found2){b++;}"
				+" return null;" + "}};";
		
		SpoonCompiler builder = new JDTSnippetCompiler(factory, content);

		builder.addInputSource(new VirtualFile(content2,""));
		
			try {
				builder.build();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return factory.Type().getAll();
	}
	
	public List<CtSimpleType<?>> getScenario2Classes(){
		Factory factory = new Launcher().createFactory();

	
		String content = "" + "class ClassZ {" + "public Object foo() {"
				//+ " Integer.toString(" + unboundVarAccess + ");"
				+" int a = 0; if(a>2){return this;}"
				+" while(a<100){a++;}"
				+" boolean found = false; while(a<100){a++;}"
				+" return null;" + "}};";

		
		
		String content2 = "" + "class ClassY {" + "public Object foo() {"
				//+ " Integer.toString(" + unboundVarAccess + ");"
				+" int b = 0; "
				+ "if(b>2){return this;}"
			//	+" while(b<100){b++;}"
				+" boolean found2 = false; while(!found2){b++;}"
				+" return null;" + "}};";
		
		SpoonCompiler builder = new JDTSnippetCompiler(factory, content);

		builder.addInputSource(new VirtualFile(content2,""));
		
			try {
				builder.build();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return factory.Type().getAll();
	}
	
	@Test
	public void testAnalysisLocalStrategy() throws JSAPException {
		
		List<AbstractFixSpaceProcessor<?>> procFix = new ArrayList<AbstractFixSpaceProcessor<?>>();
		procFix.add(new LoopExpressionFixSpaceProcessor());
		procFix.add(new IFExpressionFixSpaceProcessor());
		
		
		 FixLocationSpace ingredientSpace = new BasicFixSpace(procFix);
		 List classes = getScenario2Classes();
		 assertTrue(classes.size() == 2);
		 ingredientSpace.defineSpace(classes);
		 
		 List<CtCodeElement> ingredients = ingredientSpace.getFixSpace("ClassZ");
		 assertEquals(3, ingredients.size());
		
		 //Randomly takes one element
		 CtElement ce1 =  ingredientSpace.getElementFromSpace("ClassZ");
		 assertTrue(ingredients.contains(ce1));
			 
		 //Analysis by type
		 List<CtCodeElement> ingredientsTypeBin = ingredientSpace.getFixSpace("ClassZ","CtBinaryOperatorImpl");
		 assertEquals(3, ingredientsTypeBin.size());
		 
		 //---
		 Map<String,Integer> filter = IngredientAnalyzer.spaceDensity(ingredients);
		 Set<String> filterS = filter.keySet();
		 assertTrue(filterS.size() > 0);
		 assertTrue(filterS.size() < ingredients.size());
	}
	
}
