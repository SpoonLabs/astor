package fr.inria.astor.core.manipulation.synthesis.dynamoth.spoon;


import java.util.List;

public interface Operator {
    String getSymbol();

    Class getReturnType();

    List<Class> getTypeParameters();
}

