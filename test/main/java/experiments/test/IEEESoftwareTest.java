package experiments.test;

//package br.edu.ufcg.saferefactor.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;


import saferefactor.core.SafeRefactor;
import saferefactor.core.Parameters;
import saferefactor.core.Report;
import saferefactor.core.SafeRefactorImp;
import saferefactor.core.util.Project;

/*
 * Author: Gustavo Soares
 * Tests using the subjects presented in the paper Making program refactoring safer (IEEE Software 2010)
 */
public class IEEESoftwareTest {

	@Test
	public void test1() throws Exception {
		Project source = new Project();
		source.setProjectFolder(new File("test/resources/jhotdraw_source"));
		source.setBuildFolder(new File("test/resources/jhotdraw_source/bin"));
		source.setSrcFolder(new File("test/resources/jhotdraw_source/src"));
		source.setLibFolder(new File("test/resources/jhotdraw_source/lib"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/jhotdraw_target"));
		target.setBuildFolder(new File("test/resources/jhotdraw_target/bin"));
		target.setSrcFolder(new File("test/resources/jhotdraw_target/src"));
		target.setLibFolder(new File("test/resources/jhotdraw_source/lib"));

		Parameters parameters = new Parameters();
		parameters.setTimeLimit(30);
		parameters.setCheckCoverage(true);
		parameters.setCompileProjects(true);
		SafeRefactor saferefactor = new SafeRefactorImp(source, target,
				parameters);

		saferefactor.checkTransformation();
		Report report = saferefactor.getReport();
		assertEquals(false, report.isRefactoring());
		assertTrue(report.getCoverage().getLineRate() > 17.0);
	}
	
	@Test
	public void test2() throws Exception {
		Project source = new Project();
		source.setProjectFolder(new File("test/resources/jhotdraw_source"));
		source.setBuildFolder(new File("test/resources/jhotdraw_source/bin"));
		source.setSrcFolder(new File("test/resources/jhotdraw_source/src"));
		source.setLibFolder(new File("test/resources/jhotdraw_source/lib"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/jhotdraw_target"));
		target.setBuildFolder(new File("test/resources/jhotdraw_target/bin"));
		target.setSrcFolder(new File("test/resources/jhotdraw_target/src"));
		target.setLibFolder(new File("test/resources/jhotdraw_source/lib"));

		Parameters parameters = new Parameters();
		parameters.setTimeLimit(30);
		parameters.setCheckCoverage(true);
		parameters.setCompileProjects(true);
		parameters.setEnableImpactAnalysis(true);
		SafeRefactor saferefactor = new SafeRefactorImp(source, target,
				parameters);

		saferefactor.checkTransformation();
		Report report = saferefactor.getReport();
		assertEquals(false, report.isRefactoring());
		assertTrue(report.getCoverage().getLineRate() > 17.0);
	}
	
	@Test
	public void test3() throws Exception {
		Project source = new Project();
		source.setProjectFolder(new File("test/resources/jhotdraw_source"));
		source.setBuildFolder(new File("test/resources/jhotdraw_source/bin"));
		source.setSrcFolder(new File("test/resources/jhotdraw_source/src"));
		source.setLibFolder(new File("test/resources/jhotdraw_source/lib"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/jhotdraw_target"));
		target.setBuildFolder(new File("test/resources/jhotdraw_target/bin"));
		target.setSrcFolder(new File("test/resources/jhotdraw_target/src"));
		target.setLibFolder(new File("test/resources/jhotdraw_source/lib"));

		Parameters parameters = new Parameters();
		parameters.setTimeLimit(30);
		parameters.setCheckCoverage(true);
		parameters.setCompileProjects(true);
		parameters.setDependenceAnalaysis(true);
		SafeRefactor saferefactor = new SafeRefactorImp(source, target,
				parameters);

		saferefactor.checkTransformation();
		Report report = saferefactor.getReport();
		assertEquals(false, report.isRefactoring());
		assertTrue(report.getCoverage().getLineRate() > 17.0);
	}

}
