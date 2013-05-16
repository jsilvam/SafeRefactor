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
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import saferefactor.core.Report;
import saferefactor.core.util.Constants;
import saferefactor.experiments.SafeRefactorFacade;

public class ExperimentStep5_Randoop {

	private String experimentPath;

	public ExperimentStep5_Randoop(String experimentPath) {
		this.experimentPath = experimentPath;
	}

	/**
	 * @param args
	 *            args[0] = experimentPath
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		ExperimentStep5_Randoop randoopStep = new ExperimentStep5_Randoop(args[0]);
		randoopStep.run();
		
	}

	private void run() throws IOException {

		File experiment = new File(experimentPath);
		File treatments = new File(experiment, "treatments");
		File projects = new File(experiment, "projects");

		FileFilter dirFilter = DirectoryFileFilter.DIRECTORY;
		for (File treatment : treatments.listFiles(dirFilter)) {
			if (dontInclude(treatment)) continue;
			
			String[] parameters = treatment.getName().split("__");
			double timelimit = Double.parseDouble(parameters[0].split("_")[1]);
			boolean minimize = Boolean.parseBoolean(parameters[1].split("_")[1]);
			boolean mixMethods = Boolean.parseBoolean(parameters[2].split("_")[1]);
			
			System.out.println(timelimit + " " + minimize + " " + mixMethods);

			File randoop = new File(treatment, "randoop");
			for (File project : randoop.listFiles(dirFilter)) {
				if (dontInclude(project))
					continue;

				for (File replication : project.listFiles(dirFilter)) {
					if (dontInclude(replication))
						continue;

					doRandoop(replication, new File(projects, project.getName()), timelimit, mixMethods);
				}
			}
		}
	}

	public static boolean dontInclude(File treatment) {
		return isDsStore(treatment) || isResults(treatment);
	}

	private static boolean isResults(File treatment) {
		return treatment.getName().equals("results");
	}

	private static boolean isDsStore(File replication) {
		return replication.getName().contains("DS_Store");
	}

	public void doRandoop(File replication, File project,
			double timelimit, boolean mixMethods) throws IOException {

		System.out.println("project: " + project.getName() + " replication: " + replication.getName());
		
		generateTests(project, timelimit, mixMethods, replication);
		
//		// Generate randoop tests
//		generateTests(classes, sourcePath, timelimit, reports, true);
//
//		// Copy generated tests to randoop experiment dir
//		File randoopTests = copyToExperimentDir(testsPath, reports);
//
//		// Create aggregate RandoopTest.java file
//		generateRandoopTestFile(randoopTests);
//
//		// Report
//		report(experimentPath, reports);
	}

	private static void generateTests(File project, double timelimit,
			boolean mixMethods, File replication) throws IOException {
		String classesFileName = "classes.txt";

		List<String> classes = FileUtils.readLines(new File(project, classesFileName));
		
		File sourcePath = new File(project, "original");
		
		System.out.println(sourcePath.getAbsolutePath());
		
		StringBuilder sb = new StringBuilder();
		
		for (String clazz : classes) {
			System.out.println("class " + clazz);
			try {
				Report safeRefactorReport;
				safeRefactorReport = SafeRefactorFacade.checkTransformation(sourcePath.getAbsolutePath(),
						sourcePath.getAbsolutePath(), "bin", "src", "lib", false, timelimit,
						false, clazz, true, mixMethods);
				sb.append(clazz + "\t" + safeRefactorReport.getTotalTime() + "\n");
				
				File generatedTestsFolder = new File(safeRefactorReport.getTmpFolder(), "tests");
				File replicationSrcFolder = new File(replication, "tests/src");
				FileUtils.copyDirectory(generatedTestsFolder, replicationSrcFolder);
				
				FileFilter dirFilter = null;

				for (File file : replicationSrcFolder.listFiles(dirFilter)) {
					if (file.getName().endsWith("RandoopTest.java")) {
						file.delete();
					}
				}
			} 
			catch (Exception e) {
				System.err.println("class " + clazz);
				e.printStackTrace();
			}
		}
		
		File resultsFolder = new File(replication, "results");
		File classTime = new File(resultsFolder, "CLASS-TIME.txt");
		FileUtils.writeStringToFile(classTime, sb.toString());
		

	}

	private static void copyTests(File file, File replication) {
		// TODO Auto-generated method stub
		
	}

	private static void createEclipseProject(String randoopProjectPath,
			String sourcePath, String eclipseResourcesPath) throws IOException {
		File randoopProject = new File(randoopProjectPath);
		FileUtils.deleteQuietly(randoopProject);

		File original = new File(sourcePath);
		File src = new File(original, "src");
		FileUtils.copyDirectoryToDirectory(src, randoopProject);
		File lib = new File(original, "lib");
		FileUtils.copyDirectoryToDirectory(lib, randoopProject);

		File resources = new File(eclipseResourcesPath);
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
		for (Report report : reports) {
			sb.append(report.getClazz() + "\t" + report.getTotalTime() + "\n");
		}

		File randoopReport = new File(experimentPath, "REPORT-RANDOOP.txt");
		FileUtils.writeStringToFile(randoopReport, sb.toString());
	}

	private static void generateTests(List<String> classes, String sourcePath,
			double timelimit, List<Report> reports, boolean mixMethods) {
		int i = 0;
		for (String classe : classes) {
			System.out.println("class " + i + ": " + classe);
			i++;
			try {
				Report report;
				report = SafeRefactorFacade.checkTransformation(sourcePath,
						sourcePath, "bin", "src", "lib", false, timelimit,
						false, classe, true, mixMethods);
				report.addClazz(classe);
				reports.add(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void generateRandoopTestFile(File tests) throws IOException {
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
				System.out.println("!!! No test case found for "
						+ report.getClazz());
				System.out.println(report.getTmpFolder());
			}

			for (File test : testsDir.listFiles(filter)) {
				FileUtils.copyFileToDirectory(test, randoopTests);
			}
		}
		return randoopTests;
	}
}
