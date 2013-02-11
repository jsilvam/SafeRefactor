package saferefactor.experiments.scp2013;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import saferefactor.core.Report;
import saferefactor.ui.SafeRefactorFacade;

public class Main {

	private static String workspace = "/Users/gustavoas/workspaces/scp/";
	
	private static int numberofexecutions = 1;
	
	private static boolean runOCC= false;
	
	private static boolean runOriginal= true;

	

	public static List<Result> runSubjects(String[] subjects, int timelimit, String sourceSuffix, String targetSuffix) {
		
		List<Result> results = new ArrayList<Result>();
		
		for (String subject : subjects) {
			Result result = new Result();
			result.setSubject(subject);
			if (runOriginal)
			result.getTechniqueReports().put("original", new ArrayList<Report>());
			if (runOCC)
			result.getTechniqueReports().put("occ", new ArrayList<Report>());
			
			for (int i = 0; i < Main.numberofexecutions; i++) {
				if (runOriginal) {
					Report original = runSubject(subject, timelimit, false, sourceSuffix,targetSuffix);
					result.getTechniqueReports().get("original").add(original);	
				}
				if (runOCC) {
					Report occ = runSubject(subject, timelimit,  true, sourceSuffix,targetSuffix);
					result.getTechniqueReports().get("occ").add(occ);	
				}
			}
			results.add(result);
		}
		
		return results;
	}
	
	private void printResults(List<Result> results	) {
		System.out.println("\t\t\tOriginal\t\t|\tOCC");
		System.out.println("\t\tMethods\t\tIsRefactoring\t|\tMethods\t\tIsRefactoring");
		for (Result result : results) {			
			printPatternResult(result);
		}
	}
	
	

	private static void printPatternResult(Result result) {
		System.out.print(result.getSubject() + "\t");
		Map<String, List<Report>> techniqueReports = result.getTechniqueReports();
		
		if (runOriginal) {
			List<Report> originalResults = techniqueReports.get("original");
			System.out.print(originalResults.get(0).getTotalMethodsToTest() + "\t");
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
//		System.out.println("Methods to test: " + report.getTotalMethodsToTest());
		System.out.print(report.isRefactoring() + "\t");
		
	}

	private static Report runSubject(String patternName, int timelimit, boolean occ, String sourceSuffix, String targetSuffix) {
		Report stateNaive = SafeRefactorFacade.checkTransformation(
				workspace + patternName + sourceSuffix,
				workspace + patternName + targetSuffix, timelimit, occ);
		return stateNaive;
	}

	public static void main(String[] args) {
	
		if (args.length < 1)
			throw new IllegalArgumentException("Usage: main option");
		
		int option = Integer.parseInt(args[0]);
		
		String[] patterns = {"state", "mediator", "prototype", "visitor"};
//		String[] eclipse = {"subject1","subject2","subject3","subject5","subject6","subject7","subject8"};
		String[] eclipse = {"subject1","subject2"};
		String[] jhotdraw = {"JHotDraw"};
		String[] ajml = {"jaccounting-"};
				
		
		
		switch (option) {
		case 1:
			Main.runSubjects(eclipse, 1, "source", "target");
			break;
		case 2:
			Main.runSubjects(jhotdraw, 3, "_OO", "_AO");			
			break;
		case 3:
			Main.runSubjects(patterns, 1, "OO", "AO");
			break;
		case 4:
			Main.runSubjects(ajml, 1, "non-opt", "opt");
			break;
		default:
			break;
		}
		
		
	}
	
	
}
