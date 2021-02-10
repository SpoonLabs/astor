package fr.inria.astor.test.repair.core.solutionsearch;

import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.junit.Test;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class ProgramVariantFactoryTest {

    @Test
    public void testProgramVariantFactory() throws Exception {

        CommandSummary command = MathCommandsTests.getMath32Command();
        IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

        command.command.put("-mode", ExecutionMode.CARDUMEN.name());
        command.command.put("-flthreshold", "0.8");
        command.command.put("-maxtime", "60");
        command.command.put("-seed", "400");
        command.command.put("-maxgen", "0");
        command.command.put("-population", "1");
        command.command.put("-scope", scope.toString().toLowerCase());
        command.command.put("-parameters", "disablelog:false:tmax2:6000");
        command.command.put("-maxVarCombination", "100");
        AstorMain main = new AstorMain();

        main.execute(command.flat());

        AstorCoreEngine core = main.getEngine();

        List<SourcePosition> sourcePositions = core.getVariants().get(0).getModificationPoints()
                .stream().map(modificationPoint -> modificationPoint.getCodeElement().getPosition())
                .collect(Collectors.toList());

        // Confirm that there are no source positions that are instance of NoSourcePosition
        assertTrue(sourcePositions.stream().noneMatch(sourcePosition -> sourcePosition instanceof NoSourcePosition));
    }
}
