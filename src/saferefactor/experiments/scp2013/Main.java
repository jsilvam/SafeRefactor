package saferefactor.experiments.scp2013;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import saferefactor.core.Parameters;
import saferefactor.core.Report;
import saferefactor.core.SafeRefactor;
import saferefactor.core.SafeRefactorImp;
import saferefactor.core.util.Project;
import saferefactor.ui.SafeRefactorFacade;

public class Main {

	private static String workspace = "/Users/gustavoas/workspaces/scp/";
	
	private static int numberofexecutions = 1;

	

	public static void runSubjects(String[] subjects, int timelimit, String sourceSuffix, String targetSuffix) {
		
		List<Result> results = new ArrayList<Result>();
		
		for (String subject : subjects) {
			Result result = new Result();
			result.setSubject(subject);
			result.getTechniqueReports().put("original", new ArrayList<Report>());
			result.getTechniqueReports().put("occ", new ArrayList<Report>());
			
			for (int i = 0; i < Main.numberofexecutions; i++) {
				Report original = runSubject(subject, timelimit, false, sourceSuffix,targetSuffix);
				result.getTechniqueReports().get("original").add(original);
				Report occ = runSubject(subject, timelimit,  true, sourceSuffix,targetSuffix);
				result.getTechniqueReports().get("occ").add(occ);
			}
			results.add(result);
		}
		
		for (Result result : results) {			
			printPatternResult(result);
		}
	}
	
	

	private static void printPatternResult(Result result) {
		System.out.println(result.getSubject());
		Map<String, List<Report>> techniqueReports = result.getTechniqueReports();
		List<Report> originalResults = techniqueReports.get("original");
		System.out.println("Original");		
		int i = 1;
		for (Report report : originalResults) {
			printResult(i, report);
			i++;
		}
		List<Report> occResults = techniqueReports.get("occ");
		System.out.println("OCC");
		i = 0;
		for (Report report : occResults) {
			printResult(i, report);
			i++;
		}		
	}



	private static void printResult(int i, Report report) {
		System.out.println("Execution: " + i);
		System.out.println("Methods to test: " + report.getTotalMethodsToTest());
		System.out.println("Is refactoring? " + report.isRefactoring());
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
		String[] eclipse = {"subject1","subject2","subject3","subject5","subject6","subject7","subject8"};
		String[] jhotdraw = {"JHotDraw"};
		String[] ajml = {"jaccounting-","jspider-"};
				
		
		
		switch (option) {
		case 1:
			Main.runSubjects(eclipse, 1, "source", "target");
			break;
		case 2:
			Main.runSubjects(jhotdraw, 10, "_OO", "_AO");
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
