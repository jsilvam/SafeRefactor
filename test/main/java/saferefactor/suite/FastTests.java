package saferefactor.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import saferefactor.core.analysis.naive.test.TransformationAnalyzerTest;
import saferefactor.core.comparator.test.ComparatorImpTest;
import saferefactor.core.execution.test.AntJunitRunnerTest;
import saferefactor.core.execution.test.CoverageMeterTest;
import saferefactor.core.generation.test.RandoopAdapterTest;
import saferefactor.core.test.SafeRefactorTest;
import saferefactor.core.util.test.CompilerTest;
import experiments.test.IEEESoftwareTest;

@RunWith(Suite.class)
@SuiteClasses({
	ComparatorImpTest.class,
	AntJunitRunnerTest.class,
	CoverageMeterTest.class,
	RandoopAdapterTest.class,
	SafeRefactorTest.class,
	TransformationAnalyzerTest.class,
	CompilerTest.class})
public class FastTests {

}
