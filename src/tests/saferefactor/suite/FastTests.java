package tests.saferefactor.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import tests.saferefactor.SafeRefactorTest;
import tests.saferefactor.core.comparator.ComparatorImpTest;
import tests.saferefactor.core.execution.AntJunitRunnerTest;
import tests.saferefactor.core.execution.CoverageMeterTest;
import tests.saferefactor.core.generation.RandoopAdapterTest;
import tests.saferefactor.util.CompilerTest;



@RunWith(Suite.class)
@SuiteClasses({
	ComparatorImpTest.class,
	AntJunitRunnerTest.class,
	CoverageMeterTest.class,
	RandoopAdapterTest.class,
	SafeRefactorTest.class,
	CompilerTest.class})
public class FastTests {

}
