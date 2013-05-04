package saferefactor.experiments.ase2013;

import java.util.ArrayList;
import java.util.List;

import saferefactor.core.Report;
import saferefactor.experiments.SafeRefactorFacade;

public class Main {

	public static void main(String[] args) {

		double timelimit = 1;
		String sourcePath = "/Users/alan/Documents/workspace/original";
		String targetPath = "/Users/alan/Documents/workspace/mutant";
		System.out.println("mutante: " + targetPath);
		List<Report> reports = new ArrayList<Report>();
		try {
			Report report = SafeRefactorFacade.checkTransformation(sourcePath,
					targetPath, "bin", "src", "lib", false, timelimit, false);
			reports.add(report);
			report = SafeRefactorFacade.checkTransformation(sourcePath,
					targetPath, "bin", "src", "lib", false, timelimit, true);
			reports.add(report);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Report report : reports) {
			System.out.println("------REPORT------------");
			System.out.println("equivalent: " + report.isRefactoring());
//			System.out.println(report.getChanges());
			System.out.println(report.getTmpFolder());
		}

	}

}
