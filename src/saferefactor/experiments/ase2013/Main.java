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

public class Main {

	public static void main(String[] args) throws IOException {

		String experimentPath = "/home/alan/Dropbox/ase2013_/artefatos/experiments/commons-collections-10-mutants";
		double timelimit = 0.1;

		String classesFileName = "classes.txt";
		String originalDir = "original";
		String randoopTestsPath = experimentPath + Constants.SEPARATOR + "tests"
				+ Constants.SEPARATOR + "randoop" + Constants.SEPARATOR
				+ "tests";

		// Read classes.txt into classes list

		@SuppressWarnings("unchecked")
		List<String> classes = FileUtils.readLines(new File(experimentPath,
				classesFileName));

		String sourcePath = experimentPath + Constants.SEPARATOR + originalDir;
		
		List<Report> reports = new ArrayList<Report>();

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

		System.out.println("------REPORT------------");
		System.out.println("class\ttime\ttests");
		for (Report report : reports) {
			System.out.println(report.getClazz() + "\t" + report.getTotalTime());
		}


		// Copy generated tests to randoop tests dir of the experiment
		File randoopTests = new File(randoopTestsPath);
		for (Report report : reports) {
			File testsDir = new File(report.getTmpFolder(), "tests");
			FileFilter filter = new RegexFileFilter(".*RandoopTest[0-9]+.java");
			
			if (testsDir.listFiles(filter).length == 0) {
				System.out.println("!!!!!! " + report.getClazz());
				System.out.println(report.getTmpFolder());
			}
			
			for (File test : testsDir.listFiles(filter)) {
				System.out.println(test.getName());
				FileUtils.copyFileToDirectory(test, randoopTests);
			}
		}

		String header = "import org.junit.runner.RunWith; \n"
				+ "import org.junit.runners.Suite; \n"
				+ "import org.junit.runners.Suite.SuiteClasses; \n"
				+ "@RunWith(Suite.class) \n" + "@SuiteClasses({ \n";

		StringBuilder sb = new StringBuilder();

		for (String clazz : randoopTests.list()) {
			if (!clazz.endsWith("RandoopTest.java")) {
				sb.append("\t" + clazz.replace(".java", "") + ".class,\n");
			}
		}

		String footer = "}) \n" + "public class RandoopTest {}\n";

		String javaClass = header + sb.toString() + footer;

		File file = new File(randoopTests, "RandoopTest.java");
		FileUtils.writeStringToFile(file, javaClass);

	}
}
