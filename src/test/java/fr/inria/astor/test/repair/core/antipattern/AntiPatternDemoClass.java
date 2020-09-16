package fr.inria.astor.test.repair.core.antipattern;

/** Code demo class used for test implementation of anti-pattern.
 * @author Yi Wu
 */
public class AntiPatternDemoClass {

  public void insertReturn() {
    int a = 0;
    int num = returnStatement();
    if (num > 1) {
      a = a + 1;
    }
    for (int i = 0; i < 3; i++) {
      a++;
    }
  }

  public void controlStatement() {
    int a = 0;

    int num = returnStatement();
    int[] array = new int[] { 1, 2, 3, 4 };
    for (int n : array) {
      a = a + n;
    }
    if (num > 1) a = a + 1;

    for (int i = 0; i < 3; i++)
      a++;

    while (a > 0)
      a--;

    switch (a) {
    case 6:
      System.out.println("Today is Saturday");
      break;
    case 7:
      System.out.println("Today is Sunday");
      break;
    default:
      System.out.println("Looking forward to the Weekend");
    }
  }

  public void trivialCondition() {
    int a = 1;
    boolean b = true;
    boolean c = false;
    if (a > a) return;
    if (b && b) return;
    if (b && c || b && c) return;
    if (a == a) return;
    if (a - 4 < a - 4) return;
    for (int i = 0; i < i; i++) {
      return;
    }
    while (a != a) {
      return;
    }
  }

  public int returnStatement() {
    return 1;
  }

}
