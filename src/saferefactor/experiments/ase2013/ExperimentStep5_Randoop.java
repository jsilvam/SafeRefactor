package saferefactor.experiments.ase2013;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import saferefactor.core.Report;
import saferefactor.core.util.Constants;
import saferefactor.experiments.SafeRefactorFacade;

public class ExperimentStep5_Randoop {

	public static void main(String[] args) throws IOException {

		String experimentPath = "/home/alan/Dropbox/ase2013_/artefatos/experiments/commons-collections/experiment";
		double timelimit = 0.1;

		String classesFileName = "classes.txt";
		String originalDir = "original";
		String projectPath = experimentPath + Constants.SEPARATOR + "tests" + Constants.SEPARATOR + "randoop"; 
		String testsPath = projectPath + Constants.SEPARATOR + "tests";
		String sourcePath = experimentPath + Constants.SEPARATOR + originalDir;
		
		// Create the Eclipse project
		createEclipseProject(projectPath, sourcePath);
		

		// Read classes.txt into classes list
		@SuppressWarnings("unchecked")
		List<String> classes = FileUtils.readLines(new File(experimentPath,
				classesFileName));

		List<Report> reports = new ArrayList<Report>();

		// Generate randoop tests
		generateTests(classes, sourcePath, timelimit, reports);

		// Copy generated tests to randoop experiment dir
		File randoopTests = copyToExperimentDir(testsPath, reports);

		// Create aggregate RandoopTest.java file
		generateRandoopTestFile(randoopTests);
		
		// Report
		report(experimentPath, reports);		

	}

	private static void createEclipseProject(String randoopProjectPath, String sourcePath)
			throws IOException {
		File randoopProject = new File(randoopProjectPath);
		FileUtils.deleteQuietly(randoopProject);
		
		File original = new File(sourcePath);
		File src = new File(original, "src");
		FileUtils.copyDirectoryToDirectory(src, randoopProject);
		File lib = new File(original, "lib");
		FileUtils.copyDirectoryToDirectory(lib, randoopProject);
		
		File resources = new File("/home/alan/Dropbox/ase2013_/artefatos/experiments/resources");
		File srcFile = new File(resources, "project_randoop");
		File destFile = new File(randoopProject, ".project");
		FileUtils.copyFile(srcFile, destFile);
		
		srcFile = new File(resources, "classpath");
		destFile = new File(randoopProject, ".classpath");
		FileUtils.copyFile(srcFile, destFile);
	}

	private static void report(String experimentPath, List<Report> reports)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("CLASS\tTIME\n");
		for (Report report : reports) {
			sb.append(report.getClazz() + "\t" + report.getTotalTime() + "\n");
		}
		sb.append("\n");
		
		sb.append("CLASS\tTESTS\n");
		for (Report report : reports) {
			sb.append(report.getClazz() + "\t" + report.getTmpFolder() + "\n");
		}
		sb.append("\n");
		
		File randoopReport = new File(experimentPath, "REPORT-RANDOOP.txt");
		FileUtils.writeStringToFile(randoopReport, sb.toString());
	}

	private static void generateTests(List<String> classes, String sourcePath,
			double timelimit, List<Report> reports) {
		int i = 0;
		for (String classe : classes) {
			System.out.println("class " + i + ": " + classe);
			i++;
			try {
				Report report;
				report = SafeRefactorFacade.checkTransformation(sourcePath,
						sourcePath, "bin", "src", "", false, timelimit, false,
						classe, true);
				report.addClazz(classe);
				reports.add(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void generateRandoopTestFile(File tests)
			throws IOException {
		String header = "import org.junit.runner.RunWith; \n"
				+ "import org.junit.runners.Suite; \n"
				+ "import org.junit.runners.Suite.SuiteClasses; \n"
				+ "@RunWith(Suite.class) \n" + "@SuiteClasses({ \n";

		StringBuilder sb = new StringBuilder();
		for (String javaTest : tests.list()) {
			if (!javaTest.endsWith("RandoopTest.java")) {
				sb.append("\t" + javaTest.replace(".java", "") + ".class,\n");
			}
		}

		String footer = "}) \n" + "public class RandoopTest {}\n";

		String javaClass = header + sb.toString() + footer;
		File file = new File(tests, "RandoopTest.java");
		FileUtils.writeStringToFile(file, javaClass);
	}

	private static File copyToExperimentDir(String testsPath,
			List<Report> reports) throws IOException {
		File randoopTests = new File(testsPath);
		for (Report report : reports) {
			File testsDir = new File(report.getTmpFolder(), "tests");
			FileFilter filter = new RegexFileFilter(".*RandoopTest[0-9]+.java");
			
			if (testsDir.listFiles(filter).length == 0) {
				System.out.println("!!! No test case found for " + report.getClazz());
				System.out.println(report.getTmpFolder());
			}
			
			for (File test : testsDir.listFiles(filter)) {
				FileUtils.copyFileToDirectory(test, randoopTests);
			}
		}
		return randoopTests;
	}
}
