package saferefactor.experiments.scp2013;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import saferefactor.core.Report;
import saferefactor.ui.SafeRefactorFacade;

public class Main {

	private static String workspace = "/Users/gustavoas/workspaces/scp/";

	private static int numberofexecutions = 1;

	private static boolean runOCC = true;

	private static boolean runOriginal = true;

	public static List<Result> runSubjects(String[] subjects, double timelimit,
			String sourceSuffix, String targetSuffix) throws Exception {

		List<Result> results = new ArrayList<Result>();

		for (String subject : subjects) {
			Result result = new Result();
			result.setSubject(subject);
			if (runOriginal)
				result.getTechniqueReports().put("original",
						new ArrayList<Report>());
			if (runOCC)
				result.getTechniqueReports()
						.put("occ", new ArrayList<Report>());

			for (int i = 0; i < Main.numberofexecutions; i++) {
				if (runOriginal) {
					Report original = runSubject(subject, timelimit, false,
							sourceSuffix, targetSuffix);
					result.getTechniqueReports().get("original").add(original);
				}
				if (runOCC) {
					Report occ = runSubject(subject, timelimit, true,
							sourceSuffix, targetSuffix);
					result.getTechniqueReports().get("occ").add(occ);
				}
			}
			results.add(result);
		}

		return results;
	}

	private static void printResults(List<Result> results) {
		System.out.println("\t\t\tOriginal\t\t|\tOCC");
		System.out
				.println("\t\tMethods\t\tIsRefactoring\t|\tMethods\t\tIsRefactoring");
		for (Result result : results) {
			printPatternResult(result);
		}
	}

	private static void printPatternResult(Result result) {
		System.out.print(result.getSubject() + "\t");
		Map<String, List<Report>> techniqueReports = result
				.getTechniqueReports();

		if (runOriginal) {
			List<Report> originalResults = techniqueReports.get("original");
			System.out.print(originalResults.get(0).getTotalMethodsToTest()
					+ "\t");
			for (Report report : originalResults) {
				printResult(report);
			}
		}
		if (runOCC) {
			System.out.print("|\t");
			List<Report> occResults = techniqueReports.get("occ");
			System.out.print(occResults.get(0).getTotalMethodsToTest() + "\t");
			for (Report report : occResults) {
				printResult(report);
			}
		}
		System.out.println();
	}

	private static void printResult(Report report) {
		// System.out.println("Methods to test: " +
		// report.getTotalMethodsToTest());
		System.out.print(report.isRefactoring() + "\t");

	}

	private static Report runSubject(String patternName, double timelimit,
			boolean occ, String sourceSuffix, String targetSuffix)
			throws Exception {
		Report stateNaive = SafeRefactorFacade.checkTransformation(workspace
				+ patternName + sourceSuffix, workspace + patternName
				+ targetSuffix, timelimit, occ);
		return stateNaive;
	}

	public static void main(String[] args) {

		if (args.length < 1)
			throw new IllegalArgumentException("Usage: main option");

		int option = Integer.parseInt(args[0]);

		String[] patterns = { "abstractfactory", "adapter", "bridge",
				"builder", "chain", "command", "composite", "decorator",
				"facade", "factorymethod", "flyweight", "interpreter",
				"iterator", "mediator", "memento", "observer", "prototype", "proxy", "singleton", "state",
				"strategy", "template", "visitor"};
		
//		String[] eclipse = { "subject1", "subject2", "subject3", "subject5",
//				"subject6", "subject7", "subject8", "subject9" };
		String[] eclipse = { "subject10" };
		String[] jhotdraw = { "JHotDraw" };
		String[] ajml = { "jaccounting-","jspider-" };

		Map<Double, List<Result>> results = new HashMap<Double, List<Result>>();
		try {
			switch (option) {
			case 1:
				if (true) {
					double timelimit = 1;
					List<Result> runSubjects = Main.runSubjects(eclipse,
							timelimit, "source", "target");
					System.out.println("Time Limit: " + timelimit);
					printResults(runSubjects);
					results.put(timelimit, runSubjects);
				}

				break;
			case 2:

				if (true) {
					int timelimit = 2;
					List<Result> runSubjects2 = Main.runSubjects(jhotdraw,
							timelimit, "_OO", "_AO");
					System.out.println("Time Limit: " + timelimit);
					printResults(runSubjects2);
					results.put(Double.valueOf(timelimit), runSubjects2);
				}
				if (true) {
					int timelimit = 5;
					List<Result> runSubjects5 = Main.runSubjects(jhotdraw,
							timelimit, "_OO", "_AO");
					System.out.println("Time Limit: " + timelimit);
					printResults(runSubjects5);
					results.put(Double.valueOf(timelimit), runSubjects5);
				}
				if (true) {
					int timelimit = 10;
					List<Result> runSubjects10 = Main.runSubjects(jhotdraw,
							timelimit, "_OO", "_AO");
					System.out.println("Time Limit: " + timelimit);
					printResults(runSubjects10);
					results.put(Double.valueOf(timelimit), runSubjects10);
				}
				if (false) {
					List<Result> runSubjects20 = Main.runSubjects(jhotdraw, 20,
							"_OO", "_AO");
				}
				if (false) {
					List<Result> runSubjects30 = Main.runSubjects(jhotdraw, 30,
							"_OO", "_AO");
				}

				if (true) {
					for (Entry<Double, List<Result>> result : results
							.entrySet()) {
						System.out.println("Time Limit: " + result.getKey());
						printResults(result.getValue());
					}
				}

				break;
			case 3:
				if (true) {
					double timelimit = 0.2;
					List<Result> runSubjects = Main.runSubjects(patterns,
							timelimit, "OO", "AO");
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
					List<Result> runSubjects = Main.runSubjects(ajml,
							timelimit, "non-opt", "opt");
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
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
