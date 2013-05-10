package saferefactor.core.analysis.naive.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import saferefactor.core.analysis.Report;
import saferefactor.core.analysis.TransformationAnalyzer;
import saferefactor.core.analysis.impact.ASMBasedDependenceAnalyzer;
import saferefactor.core.analysis.impact.ASMBasedImpactAnalyzer;
import saferefactor.core.analysis.naive.ASMBasedAnalyzer;
import saferefactor.core.analysis.naive.ReflectionBasedAnalyzer;
import saferefactor.core.util.Project;
import saferefactor.core.util.ast.Method;


public class TransformationAnalyzerTest {

	@Test
	public void testAnalyze() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/subject14source"));
		source.setBuildFolder(new File("test/resources/subject14source/bin"));
		source.setSrcFolder(new File("test/resources/subject14source/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/subject14target"));
		target.setBuildFolder(new File("test/resources/subject14target/bin"));
		target.setSrcFolder(new File("test/resources/subject14target/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		TransformationAnalyzer analyzer = new ReflectionBasedAnalyzer(
				source, target,tmpFolder);
		Report report = analyzer.analyze();

		assertEquals(8, report.getMethodsToTest().size());
		assertEquals(
				"[cons : A.<init>(), method : A.k() : A, cons : B.<init>(), method : B.k() : B;C, cons : C.<init>(), method : C.main([Ljava.lang.String;) : C, method : C.test() : C, method : B.m() : C]",
				report.getMethodsToTest().toString());

	}
	
	@Test
	public void testAsmBasedAnalyze() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/subject14source"));
		source.setBuildFolder(new File("test/resources/subject14source/bin"));
		source.setSrcFolder(new File("test/resources/subject14source/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/subject14target"));
		target.setBuildFolder(new File("test/resources/subject14target/bin"));
		target.setSrcFolder(new File("test/resources/subject14target/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		TransformationAnalyzer analyzer = new ASMBasedAnalyzer(
				source, target,tmpFolder);
		Report report = analyzer.analyze();

		assertEquals(8, report.getMethodsToTest().size());
		assertEquals(
				"[method : B.k() : B;C, cons : B.<init>(), method : C.test() : C, cons : A.<init>(), method : A.k() : A, method : C.main([Ljava.lang.String;) : C, cons : C.<init>(), method : B.m() : C]",
				report.getMethodsToTest().toString());

	}
	


	@Test(expected = RuntimeException.class)
	public void testAnalyzeProjectNotFound() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/subject14source"));
		source.setBuildFolder(new File("resources/subject14source/bin"));
		source.setSrcFolder(new File("resources/subject14source/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/subject14target"));
		target.setBuildFolder(new File("test/resources/subject14target/bin"));
		target.setSrcFolder(new File("test/resources/subject14target/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		
		TransformationAnalyzer analyzer = new ReflectionBasedAnalyzer(
				source, target,tmpFolder);
		Report report = analyzer.analyze();
		List<Method> methodsToTest = report.getMethodsToTest();
		for (Method abstractMethod : methodsToTest) {
			System.out.println(abstractMethod);
		}

	}
	
	@Test
	public void testImpactAnalysis() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/subject14source"));
		source.setBuildFolder(new File("test/resources/subject14source/bin"));
		source.setSrcFolder(new File("test/resources/subject14source/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/subject14target"));
		target.setBuildFolder(new File("test/resources/subject14target/bin"));
		target.setSrcFolder(new File("test/resources/subject14target/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		TransformationAnalyzer analyzer = new ASMBasedImpactAnalyzer(
				source, target,tmpFolder);
		Report report = analyzer.analyze();

		assertEquals(6, report.getMethodsToTest().size());
		assertEquals(
				"[method : B.k() : B;C, cons : B.<init>(), method : C.test() : C, method : C.main([Ljava.lang.String;) : C, cons : C.<init>(), method : B.m() : C]",
				report.getMethodsToTest().toString());

	}
	
	@Test
	public void testImpactAnalysis2() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/impactSource"));
		source.setBuildFolder(new File("test/resources/impactSource/bin"));
		source.setSrcFolder(new File("test/resources/impactSource/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/impactTarget"));
		target.setBuildFolder(new File("test/resources/impactTarget/bin"));
		target.setSrcFolder(new File("test/resources/impactTarget/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		TransformationAnalyzer analyzer = new ASMBasedImpactAnalyzer(
				source, target,tmpFolder);
		Report report = analyzer.analyze();

		assertEquals(7, report.getMethodsToTest().size());
		assertEquals(
				"[method : C.test(D) : C, method : B.k() : B, cons : B.<init>(), method : A.x() : A, cons : A.<init>(), cons : C.<init>(), cons : D.<init>()]",
				report.getMethodsToTest().toString());

	}
	
	@Test
	public void testImpactAnalysis3() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/impactSource2"));
		source.setBuildFolder(new File("test/resources/impactSource2/bin"));
		source.setSrcFolder(new File("test/resources/impactSource2/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/impactTarget2"));
		target.setBuildFolder(new File("test/resources/impactTarget2/bin"));
		target.setSrcFolder(new File("test/resources/impactTarget2/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		TransformationAnalyzer analyzer = new ASMBasedImpactAnalyzer(
				source, target,tmpFolder);
		Report report = analyzer.analyze();

		assertEquals(2, report.getMethodsToTest().size());
		assertEquals(
				"[cons : B.<init>(), method : A.x() : B]",
				report.getMethodsToTest().toString());

	}
	
	@Test
	public void testImpactAnalysis4() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/inheritanceImpactAnalysisSource"));
		source.setBuildFolder(new File("test/resources/inheritanceImpactAnalysisSource/bin"));
		source.setSrcFolder(new File("test/resources/inheritanceImpactAnalysisSource/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/inheritanceImpactAnalysisTarget"));
		target.setBuildFolder(new File("test/resources/inheritanceImpactAnalysisTarget/bin"));
		target.setSrcFolder(new File("test/resources/inheritanceImpactAnalysisTarget/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		TransformationAnalyzer analyzer = new ASMBasedImpactAnalyzer(
				source, target,tmpFolder);
		Report report = analyzer.analyze();

		assertEquals(3, report.getMethodsToTest().size());
		assertEquals(
				"[cons : B.<init>(), method : A.test() : A;B;C, cons : C.<init>()]",
				report.getMethodsToTest().toString());
	}
	
	@Test
	public void testImpactAnalysis5() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/inheritanceImpactAnalysisSource"));
		source.setBuildFolder(new File("test/resources/inheritanceImpactAnalysisSource/bin"));
		source.setSrcFolder(new File("test/resources/inheritanceImpactAnalysisSource/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/inheritanceImpactAnalysisTarget"));
		target.setBuildFolder(new File("test/resources/inheritanceImpactAnalysisTarget/bin"));
		target.setSrcFolder(new File("test/resources/inheritanceImpactAnalysisTarget/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		TransformationAnalyzer analyzer = new ASMBasedAnalyzer(
				source, target,tmpFolder);
		Report report = analyzer.analyze();

		assertEquals(4, report.getMethodsToTest().size());
		assertEquals(
				"[cons : B.<init>(), cons : A.<init>(), method : A.test() : A;B;C, cons : C.<init>()]",
				report.getMethodsToTest().toString());

	}
	
	@Test
	public void testDependenceAnalyzer() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/dependenceSource"));
		source.setBuildFolder(new File("test/resources/dependenceSource/bin"));
		source.setSrcFolder(new File("test/resources/dependenceSource/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/dependenceTarget"));
		target.setBuildFolder(new File("test/resources/dependenceTarget/bin"));
		target.setSrcFolder(new File("test/resources/dependenceTarget/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		TransformationAnalyzer analyzer = new ASMBasedDependenceAnalyzer(
				source, target,tmpFolder,"A");
		Report report = analyzer.analyze();

		assertEquals(1, report.getMethodsToTest().size());
		
		assertEquals(
		"[method : A.m() : B]",
		report.getMethodsToTest().toString());
	}
	
	@Test
	public void testDependenceAnalyzer2() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/dependenceSource"));
		source.setBuildFolder(new File("test/resources/dependenceSource/bin"));
		source.setSrcFolder(new File("test/resources/dependenceSource/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/dependenceTarget"));
		target.setBuildFolder(new File("test/resources/dependenceTarget/bin"));
		target.setSrcFolder(new File("test/resources/dependenceTarget/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		TransformationAnalyzer analyzer = new ASMBasedDependenceAnalyzer(
				source, target,tmpFolder,"B");
		Report report = analyzer.analyze();

		assertEquals(2, report.getMethodsToTest().size());
		assertEquals(
				"[cons : B.<init>(), method : A.m() : B]",
				report.getMethodsToTest().toString());

	}
	
	@Test
	public void testDependenceAnalyzer3() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/dependenceSource"));
		source.setBuildFolder(new File("test/resources/dependenceSource/bin"));
		source.setSrcFolder(new File("test/resources/dependenceSource/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/dependenceTarget"));
		target.setBuildFolder(new File("test/resources/dependenceTarget/bin"));
		target.setSrcFolder(new File("test/resources/dependenceTarget/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		TransformationAnalyzer analyzer = new ASMBasedDependenceAnalyzer(
				source, target,tmpFolder,"D");
		Report report = analyzer.analyze();

		assertEquals(7, report.getMethodsToTest().size());
		assertEquals(
				"[cons : B.<init>(), method : D.m(C) : D, cons : E.<init>(), method : C.m(java.lang.String) : C, cons : C.<init>(), method : A.m() : B, cons : D.<init>()]",
				report.getMethodsToTest().toString());

	}
	
	@Test
	public void testDependenceAnalyzer4() throws Exception {

		Project source = new Project();
		source.setProjectFolder(new File("test/resources/dependenceSource"));
		source.setBuildFolder(new File("test/resources/dependenceSource/bin"));
		source.setSrcFolder(new File("test/resources/dependenceSource/src"));

		Project target = new Project();
		target.setProjectFolder(new File("test/resources/dependenceTarget"));
		target.setBuildFolder(new File("test/resources/dependenceTarget/bin"));
		target.setSrcFolder(new File("test/resources/dependenceTarget/src"));

		String tmpFolder = System
				.getProperty("java.io.tmpdir");
		TransformationAnalyzer analyzer = new ASMBasedDependenceAnalyzer(
				source, target,tmpFolder,"F");
		Report report = analyzer.analyze();

		assertEquals(8, report.getMethodsToTest().size());
		assertEquals(
				"[cons : B.<init>(), method : D.m(C) : D, cons : E.<init>(), cons : F.<init>(), method : C.m(java.lang.String) : C, cons : C.<init>(), method : A.m() : B, cons : D.<init>()]",
				report.getMethodsToTest().toString());

	}

}
