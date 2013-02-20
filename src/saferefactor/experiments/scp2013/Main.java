package saferefactor.experiments.scp2013;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import saferefactor.core.Report;
import saferefactor.core.util.FileUtil;
import saferefactor.ui.SafeRefactorFacade;

public class Main {

	private static final String ORIGINAL = "original";

	private static final String NEW_TECHNIQUE = "occ";
	
	private static final String LOG_DIR = "/Users/gustavoas/Dropbox/scp_results";

	private static String workspace = "/Users/gustavoas/workspaces/aspects/";

	private static int numberofexecutions = 1;

	private static boolean runOCC = true;

	private static boolean runOriginal = true;

	public static List<Result> runSubjects(String[] subjects, String bin,
			String src, double timelimit, String sourceSuffix,
			String targetSuffix) throws Exception {

		List<Result> results = new ArrayList<Result>();

		for (String subject : subjects) {
			Result result = new Result();
			String id = subject + sourceSuffix + "_" + targetSuffix;
			result.setSubject(id);
			if (runOriginal)
				result.getTechniqueReports().put(ORIGINAL,
						new ArrayList<Report>());
			if (runOCC)
				result.getTechniqueReports().put(NEW_TECHNIQUE,
						new ArrayList<Report>());

			for (int i = 0; i < Main.numberofexecutions; i++) {
				if (runOriginal) {
					Report original = runSubject(subject, bin, src, timelimit,
							false, sourceSuffix, targetSuffix);
					System.out.println("Original. Is refactoring? "
							+ original.isRefactoring());
					log(original,ORIGINAL,timelimit,id);
					if (!original.isRefactoring())
						System.out.println(original.getChanges());
					result.getTechniqueReports().get(ORIGINAL).add(original);
				}
				if (runOCC) {
					Report occ = runSubject(subject, bin, src, timelimit, true,
							sourceSuffix, targetSuffix);
					result.getTechniqueReports().get(NEW_TECHNIQUE).add(occ);
					System.out.println("Occ. Is refactoring? "
							+ occ.isRefactoring());
					log(occ,NEW_TECHNIQUE,timelimit,id);
					if (!occ.isRefactoring())
						System.out.println(occ.getChanges());
				}
			}
			results.add(result);
		}

		return results;
	}

	private static void log(Report report, String technique, double timelimit, String id) throws IOException {
		String time = String.valueOf(timelimit);
		time = time.replace(".", "_");
		String folderName = id + "_" + technique + "_" + time;
		
		File dest = new File(LOG_DIR,folderName);
		if (dest.exists())
			FileUtils.deleteDirectory(dest);
		
		String tmpFolder = report.getTmpFolder();
		FileUtil.copyFolder(new File(tmpFolder), dest);
		if (!report.isRefactoring()) {			
			FileUtil.makeFile(dest.getAbsolutePath() + "/changes.txt", report.getChanges());	
		}
		
	}

	private static void printResults(List<Result> results) {
		// System.out.println("Subject\tTechnique\tMethods\tResult\tTimeLimit\tTotalTime");

		for (Result result : results) {
			printSubjectResult(result);
		}
	}

	private static void printSubjectResult(Result result) {

		Map<String, List<Report>> techniqueReports = result
				.getTechniqueReports();

		if (runOriginal) {
			System.out.print(result.getSubject() + "\tV1\t");
			List<Report> originalResults = techniqueReports.get(ORIGINAL);
			System.out.print(originalResults.get(0).getTotalMethodsToTest()
					+ "\t");
			for (Report report : originalResults) {
				System.out.print(report.isRefactoring() + "\t");
				System.out.print(report.getTotalTime() + "\t");
				System.out.print(report.getTimelimit() + "\t");

			}
			// System.out.println();
		}
		if (runOCC) {
			System.out.print(result.getSubject() + "\tV2\t");
			List<Report> v2Results = techniqueReports.get(NEW_TECHNIQUE);
			System.out.print(v2Results.get(0).getTotalMethodsToTest() + "\t");
			for (Report report : v2Results) {
				System.out.print(report.isRefactoring() + "\t");
				System.out.print(report.getTotalTime() + "\t");
				System.out.print(report.getTimelimit() + "\t");

			}
			System.out.println();
		}

	}

	private static void printResult(Report report) {
		// System.out.println("Methods to test: " +
		// report.getTotalMethodsToTest());
		System.out.print(report.isRefactoring() + "\t");

	}

	private static Report runSubject(String patternName, String bin,
			String src, double timelimit, boolean occ, String sourceSuffix,
			String targetSuffix) throws Exception {
		Report stateNaive = SafeRefactorFacade.checkTransformation(workspace
				+ patternName + sourceSuffix, workspace + patternName
				+ targetSuffix, bin, src, "lib", false, timelimit, occ);
		return stateNaive;
	}

	public static void main(String[] args) {

		if (args.length < 1)
			throw new IllegalArgumentException("Usage: main option");

		int option = Integer.parseInt(args[0]);

		String[] patterns = { "abstractfactory", "adapter", "bridge",
				"builder", "chain", "command", "composite", "decorator",
				"facade", "factorymethod", "flyweight", "interpreter",
				"iterator", "mediator", "memento", "observer", "prototype",
				"proxy", "singleton", "state", "strategy", "template",
				"visitor" };

		String[] eclipse = { "subject1", "subject2", "subject3", "subject5",
				"subject6", "subject7", "subject8", "subject9" };
		// String[] eclipse = { "subject1" };
		String[] jhotdraw = { "JHotDraw" };
		String[] checkstyle = { "CheckstylePlugin" };
		String[] ajml = { "jaccounting-", "jspider-" };
		String[] jss = { "JHotDraw_650_" };
		String[] jss2 = { "JHotDraw_344_" };
		String[] mobilemedia = { "MobileMedia03_" };

		Map<Double, List<Result>> results = new HashMap<Double, List<Result>>();
		// List<Result> results = new ArrayList<Result>();
		try {
			switch (option) {
			case 1:
				if (true) {
					double timelimit = 0.1;
					List<Result> runSubjects = Main.runSubjects(eclipse, "bin",
							"src", timelimit, "source", "target");

					results.put(timelimit, runSubjects);
				}
				if (true) {
					double timelimit = 0.2;
					List<Result> runSubjects = Main.runSubjects(eclipse, "bin",
							"src", timelimit, "source", "target");

					results.put(timelimit, runSubjects);
				}
				if (true) {
					double timelimit = 0.5;
					List<Result> runSubjects = Main.runSubjects(eclipse, "bin",
							"src", timelimit, "source", "target");

					results.put(timelimit, runSubjects);
				}
				if (true) {
					for (Entry<Double, List<Result>> result : results
							.entrySet()) {
						printResults(result.getValue());
					}
				}
				break;
			case 2:
				if (true) {
					int timelimit = 2;
					List<Result> runSubjects2 = Main.runSubjects(jhotdraw,
							"bin", "src", timelimit, "_OO", "_AO");
					printResults(runSubjects2);

					results.put(Double.valueOf(timelimit), runSubjects2);
				}
				if (true) {
					int timelimit = 5;
					List<Result> runSubjects2 = Main.runSubjects(jhotdraw,
							"bin", "src", timelimit, "_OO", "_AO");
					printResults(runSubjects2);
					results.put(Double.valueOf(timelimit), runSubjects2);
				}
				if (true) {
					int timelimit = 10;
					List<Result> runSubjects2 = Main.runSubjects(jhotdraw,
							"bin", "src", timelimit, "_OO", "_AO");
					printResults(runSubjects2);
					results.put(Double.valueOf(timelimit), runSubjects2);
				}

				if (true) {
					for (Entry<Double, List<Result>> result : results
							.entrySet()) {

						printResults(result.getValue());
					}
				}

				break;
			case 3:
				if (true) {
					double timelimit = 0.2;
					List<Result> runSubjects = Main.runSubjects(patterns,
							"bin", "src", timelimit, "OO", "AO");
					System.out.println("Time Limit: " + timelimit);
					printResults(runSubjects);
				}
				if (true) {
					for (Entry<Double, List<Result>> result : results
							.entrySet()) {
						System.out.println("Time Limit: " + result.getKey());
						printResults(result.getValue());
					}
				}
				break;
			case 4:
				if (true) {
					double timelimit = 0.5;
					List<Result> runSubjects = Main.runSubjects(ajml, "bin",
							"src", timelimit, "non-opt", "opt");
					System.out.println("Time Limit: " + timelimit);
					printResults(runSubjects);
				}
				if (true) {
					for (Entry<Double, List<Result>> result : results
							.entrySet()) {
						System.out.println("Time Limit: " + result.getKey());
						printResults(result.getValue());
					}
				}
				break;
			case 5:
				if (true) {
					double timelimit = 2;

					List<Result> runSubjects = new ArrayList<Result>();
					String bin = "bin";
					String src = "src/main/java";
//					runSubjects.addAll(Main.runSubjects(jss2, bin, src,
//							timelimit, "BEFORE", "AFTER"));
//
//					bin = "build/classes";
//					src = "src/main/java";
//					runSubjects.addAll(Main.runSubjects(jss, bin, src,
//							timelimit, "BEFORE", "AFTER"));

					bin = "bin";
					src = "src";
					runSubjects.addAll(Main.runSubjects(checkstyle, bin, src,
							timelimit, "Source", "AO"));
					runSubjects.addAll(Main.runSubjects(checkstyle, bin, src,
							timelimit, "OO", "AO"));

					results.put(timelimit, runSubjects);
					printResults(runSubjects);
				}
				if (true) {
					double timelimit = 5;
					List<Result> runSubjects = new ArrayList<Result>();
					String bin = "bin";
					String src = "src/main/java";
//					runSubjects.addAll(Main.runSubjects(jss2, bin, src,
//							timelimit, "BEFORE", "AFTER"));
//
//					bin = "build/classes";
//					src = "src/main/java";
//					runSubjects.addAll(Main.runSubjects(jss, bin, src,
//							timelimit, "BEFORE", "AFTER"));
					
					bin = "bin";
					src = "src";
					runSubjects.addAll(Main.runSubjects(checkstyle, bin, src,
							timelimit, "Source", "AO"));
					runSubjects.addAll(Main.runSubjects(checkstyle, bin, src,
							timelimit, "OO", "AO"));

					printResults(runSubjects);
					results.put(timelimit, runSubjects);
				}
				if (true) {
					double timelimit = 10;
					List<Result> runSubjects = new ArrayList<Result>();
					String bin = "bin";
					String src = "src/main/java";
//					runSubjects.addAll(Main.runSubjects(jss2, bin, src,
//							timelimit, "BEFORE", "AFTER"));
//
//					bin = "build/classes";
//					src = "src/main/java";
//					runSubjects.addAll(Main.runSubjects(jss, bin, src,
//							timelimit, "BEFORE", "AFTER"));
					bin = "bin";
					src = "src";
//					runSubjects.addAll(Main.runSubjects(checkstyle, bin, src,
//							timelimit, "Source", "OO"));
					runSubjects.addAll(Main.runSubjects(checkstyle, bin, src,
							timelimit, "OO", "AO"));
					runSubjects.addAll(Main.runSubjects(checkstyle, bin, src,
							timelimit, "Source", "AO"));
//					printResults(runSubjects);
					results.put(timelimit, runSubjects);
					
				}

				break;
			case 6:
				if (true) {
					double timelimit = 10;
					String bin = "bin";
					String src = "src/main/java";
					List<Result> runSubjects = Main.runSubjects(jss2, bin, src,
							timelimit, "BEFORE", "AFTER");
					System.out.println("Time Limit: " + timelimit);
					results.put(timelimit, runSubjects);
					printResults(runSubjects);
				}
				if (true) {
					double timelimit = 20;
					String bin = "bin";
					String src = "src/main/java";
					List<Result> runSubjects = Main.runSubjects(jss2, bin, src,
							timelimit, "BEFORE", "AFTER");
					System.out.println("Time Limit: " + timelimit);
					results.put(timelimit, runSubjects);
					printResults(runSubjects);
				}
				if (true) {
					double timelimit = 30;
					String bin = "bin";
					String src = "src/main/java";
					List<Result> runSubjects = Main.runSubjects(jss2, bin, src,
							timelimit, "BEFORE", "AFTER");
					System.out.println("Time Limit: " + timelimit);
					results.put(timelimit, runSubjects);
					printResults(runSubjects);
				}
				break;
			case 7:
				if (true) {
					double timelimit = 0.1;
					String bin = "bin";
					String src = "src";
					List<Result> runSubjects = Main.runSubjects(mobilemedia,
							bin, src, timelimit, "OO", "AO");
					System.out.println("Time Limit: " + timelimit);
					results.put(timelimit, runSubjects);
					printResults(runSubjects);
				}
				if (true) {
					double timelimit = 0.2;
					String bin = "bin";
					String src = "src";
					List<Result> runSubjects = Main.runSubjects(mobilemedia,
							bin, src, timelimit, "OO", "AO");
					System.out.println("Time Limit: " + timelimit);
					results.put(timelimit, runSubjects);
					printResults(runSubjects);
				}
				if (true) {
					double timelimit = 0.3;
					String bin = "bin";
					String src = "src";
					List<Result> runSubjects = Main.runSubjects(mobilemedia,
							bin, src, timelimit, "OO", "AO");
					System.out.println("Time Limit: " + timelimit);
					results.put(timelimit, runSubjects);
					printResults(runSubjects);
				}

				if (true) {
					for (Entry<Double, List<Result>> result : results
							.entrySet()) {
						System.out.println("Time Limit: " + result.getKey());
						printResults(result.getValue());
					}
				}
				break;
			default:
				break;
			}
			if (true) {
				for (Entry<Double, List<Result>> result : results.entrySet()) {
					printResults(result.getValue());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
