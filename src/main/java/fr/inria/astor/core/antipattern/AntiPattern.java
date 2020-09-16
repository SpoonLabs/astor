package fr.inria.astor.core.antipattern;

import fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp;
import fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp;
import fr.inria.astor.approaches.jgenprog.operators.RemoveOp;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.code.*;

import java.util.List;
/** Implementation of anti-patterns
 * @author Yi Wu
 */
public class AntiPattern {

  /**
   * Check whether a newly generated program variant belongs to anti-patterns
   * @param variant the generated program variant
   * @param currentGeneration the current generation
   * @return true if the program variant belongs to anti-patterns
   * */
  public static boolean isAntiPattern(ProgramVariant variant, int currentGeneration) {
    if (variant == null) return true; // discard

    List<OperatorInstance> operations = variant.getOperations().get(currentGeneration);
    if (operations == null || operations.isEmpty()) return true;

    for (OperatorInstance genOperation : operations) { // As we observed, there is only one OperatorInstance in operations. The
                                                       // operations.size() = 1.
      AstorOperator operator = genOperation.getOperationApplied();

      if (operator instanceof RemoveOp) return isControlStatement(genOperation); // Anti-delete Control Statement

      else if (operator instanceof ReplaceOp) return isTrivialCondition(genOperation); // Anti-append Trivial Condition

      else if (operator instanceof InsertBeforeOp) return isInsertReturnBeforeStatement(genOperation);  // Anti-append early return

      else if (operator instanceof InsertAfterOp) return isInsertReturnAfterButNotLast(genOperation);  // Anti-append early return

    }

    return false;
  }

  /**
   * Check whether the modification is inserting early return
   * @param operation
   * @return true if insert return after a statement but not as the last statement.
   * */
  public static boolean isInsertReturnAfterButNotLast(OperatorInstance operation) {
    CtElement fix = operation.getModified();
    if (fix instanceof CtReturnImpl) {
      StatementOperatorInstance stmtoperator = (StatementOperatorInstance) operation;
      CtBlock parentBlock = stmtoperator.getParentBlock();
      int location = stmtoperator.getLocationInParent();
      List<CtStatement> stList = parentBlock.getStatements();
      // minus 2 : the addition of fix statement and the index starting from 0.
      return location != stList.size() - 2;
    }
    return false;
  }

  /**
   * Check whether the modification is inserting early return
   * @param operation
   * @return true if insert return before a statement.
   * */
  public static boolean isInsertReturnBeforeStatement(OperatorInstance operation) {
    CtElement original = operation.getOriginal();
    CtElement fix = operation.getModified();
    return ((original != null) && (fix instanceof CtReturnImpl));
  }

  /**
   * Check whether the removed element is a control statement
   * @param operation
   * @return true if remove for, if, while, switch statement
   * */
  public static boolean isControlStatement(OperatorInstance operation) {
    CtElement element = operation.getModificationPoint().getCodeElement();
    return element instanceof CtFor || element instanceof CtIf || element instanceof CtForEach
           || element instanceof CtWhile || element instanceof CtSwitch;
  }

  /**
   * Check whether the condition of if/while/for statement is Contradiction or Tautology (Binary condition)
   * @param  operation
   * @return true if the left and right operators of the binary condition are the same
   * */
  public static boolean isTrivialCondition(OperatorInstance operation) {
    CtStatement fix = (CtStatement) operation.getModified();
    CtExpression<Boolean> condition;
    if (fix instanceof CtIf) {
      condition = ((CtIfImpl) fix).getCondition();
    } else if (fix instanceof CtWhile) {
      condition = ((CtWhile) fix).getLoopingExpression();
    } else if (fix instanceof CtFor) {
      condition = ((CtFor) fix).getExpression();
    } else {
      return false;
    }

    return compareConditionOperand(condition);

  }

  /**
   * Check whether the left operator equals to the right operators of the binary condition
   * @param condition
   * @return true if the condition is binary condition and the left and right operators equal with each other
   * */
  public static boolean compareConditionOperand(CtExpression<Boolean> condition) {
    if (condition instanceof CtBinaryOperator) {
      CtBinaryOperator c = (CtBinaryOperatorImpl<Boolean>) condition;
      return c.getLeftHandOperand().equals(c.getRightHandOperand());
    }
    return false;
  }

}
