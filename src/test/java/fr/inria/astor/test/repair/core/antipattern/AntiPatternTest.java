package fr.inria.astor.test.repair.core.antipattern;

import fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp;
import fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp;
import fr.inria.astor.approaches.jgenprog.operators.RemoveOp;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import org.junit.BeforeClass;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.antipattern.AntiPattern;

import static org.junit.jupiter.api.Assertions.*;

/** Tests the implementation of anti-pattern
 * @author Yi Wu
 */
public class AntiPatternTest {
  static int id = 0;
  static int generation = 1;
  static CtClass ctClass;

  @BeforeClass
  public static void loadClass() throws IOException {
    String contents = new String(Files.readAllBytes(Paths.get("./src/test/java/fr/inria/astor/test/repair/core/antipattern/AntiPatternDemoClass.java")));
    ctClass = Launcher.parseClass(contents);
  }

  /**
   * Tests isAntiPattern method and each branch method that checks different types of anti-patterns.
   * @param operationApplied Replace, Remove, InsertBefore, or InsertAfter
   * @param original The original code
   * @param  modified  The code used to modify the original code
   * @param  methodName The name of the branch method that checks different anti-patterns
   * */
  public static void antiPatternAssertion(AstorOperator operationApplied, CtElement original, CtElement modified,
                                          String methodName)
                                                             throws IllegalAccessException, NoSuchMethodException,
                                                             InvocationTargetException {
    ModificationPoint modificationPoint = new ModificationPoint(id, original, ctClass, null, generation);
    StatementOperatorInstance statementOperatorInstance = new StatementOperatorInstance(modificationPoint, operationApplied,
                                                                                        original, modified);
    statementOperatorInstance.defineParentInformation(modificationPoint);
    assertTrue(operationApplied.applyChangesInModel(statementOperatorInstance, null)); // ensure the operation is applied
    assertTrue(invokeBranchMethod(methodName, statementOperatorInstance)); // assert each branch method that checks anti-pattern
    ProgramVariant p = constructProgramVariantForTest(modificationPoint, statementOperatorInstance);
    assertTrue(AntiPattern.isAntiPattern(p, generation)); // assert isAntiPattern method

  }

  /**
   * Constructs a program variant
   * @param modificationPoint
   * @param operatorInstance
   * */
  public static ProgramVariant constructProgramVariantForTest(ModificationPoint modificationPoint,
                                                              OperatorInstance operatorInstance) {
    ProgramVariant p = new ProgramVariant();
    List<ModificationPoint> modificationPointList = new ArrayList<>();
    modificationPointList.add(modificationPoint);
    p.addModificationPoints(modificationPointList);
    p.putModificationInstance(generation, operatorInstance);
    return p;

  }

  /**
   * Invokes the branch method that checks different types of anti-patterns.
   * @param methodName
   * @param statementOperatorInstance
   * */
  public static Boolean invokeBranchMethod(String methodName,
                                           StatementOperatorInstance statementOperatorInstance) throws InvocationTargetException,
                                                                                                IllegalAccessException,
                                                                                                NoSuchMethodException {
    Class antiPattern = AntiPattern.class;
    Method method = antiPattern.getMethod(methodName, OperatorInstance.class);
    return (Boolean) method.invoke(null, statementOperatorInstance);
  }

  @Test
  public void InsertReturnBeforeStatementTest() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    String methodName = "isInsertReturnBeforeStatement";
    CtMethod returnStmt = (CtMethod) ctClass.getMethodsByName("returnStatement").get(0);
    CtReturn ctReturn = (CtReturn) returnStmt.getElements(new TypeFilter(CtReturn.class)).get(0);
    CtMethod insertReturn = (CtMethod) ctClass.getMethodsByName("insertReturn").get(0);
    CtIf ctIf = (CtIf) insertReturn.getElements(new TypeFilter(CtIf.class)).get(0);
    antiPatternAssertion(new InsertBeforeOp(), ctIf, ctReturn, methodName);

  }

  @Test
  public void InsertReturnAfterButNotLastTest() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    String methodName = "isInsertReturnAfterButNotLast";
    CtMethod returnStmt = (CtMethod) ctClass.getMethodsByName("returnStatement").get(0);
    CtReturn ctReturn = (CtReturn) returnStmt.getElements(new TypeFilter(CtReturn.class)).get(0);
    CtMethod insertReturn = (CtMethod) ctClass.getMethodsByName("insertReturn").get(0);
    CtIf ctIf = (CtIf) insertReturn.getElements(new TypeFilter(CtIf.class)).get(0);
    antiPatternAssertion(new InsertAfterOp(), ctIf, ctReturn, methodName);
  }

  @Test
  public void DeleteControlStatementTest() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    String methodName = "isControlStatement";
    CtMethod controlStatement = (CtMethod) ctClass.getMethodsByName("controlStatement").get(0);
    CtForEach ctForEach = (CtForEach) controlStatement.getElements(new TypeFilter(CtForEach.class)).get(0);
    antiPatternAssertion(new RemoveOp(), ctForEach, null, methodName);
    CtFor ctFor = (CtFor) controlStatement.getElements(new TypeFilter(CtFor.class)).get(0);
    antiPatternAssertion(new RemoveOp(), ctFor, null, methodName);
    CtWhile ctWhile = (CtWhile) controlStatement.getElements(new TypeFilter(CtWhile.class)).get(0);
    antiPatternAssertion(new RemoveOp(), ctWhile, null, methodName);
    CtSwitch ctSwitch = (CtSwitch) controlStatement.getElements(new TypeFilter(CtSwitch.class)).get(0);
    antiPatternAssertion(new RemoveOp(), ctSwitch, null, methodName);
    CtIf ctIf = (CtIf) controlStatement.getElements(new TypeFilter(CtIf.class)).get(0);
    antiPatternAssertion(new RemoveOp(), ctIf, null, methodName);
  }

  @Test
  public void AppendTrivialConditionTest() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    String methodName = "isTrivialCondition";
    CtMethod trivialCondition = (CtMethod) ctClass.getMethodsByName("trivialCondition").get(0);
    List<CtIf> ctIfList = trivialCondition.getElements(new TypeFilter(CtIf.class));
    List<CtFor> ctForList = trivialCondition.getElements(new TypeFilter(CtFor.class));
    List<CtWhile> CtWhileList = trivialCondition.getElements(new TypeFilter(CtWhile.class));
    for (CtIf ctIf : ctIfList)
      antiPatternAssertion(new ReplaceOp(), ctIf, ctIf, methodName);

    for (CtFor ctFor : ctForList)
      antiPatternAssertion(new ReplaceOp(), ctFor, ctFor, methodName);

    for (CtWhile ctWhile : CtWhileList)
      antiPatternAssertion(new ReplaceOp(), ctWhile, ctWhile, methodName);

  }

}