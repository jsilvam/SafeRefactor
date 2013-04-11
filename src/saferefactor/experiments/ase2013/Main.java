package saferefactor.experiments.ase2013;

import java.util.ArrayList;
import java.util.List;

import saferefactor.core.Report;
import saferefactor.experiments.SafeRefactorFacade;

public class Main {

	public static void main(String[] args) {

		String sourcePath = "/Users/gustavoas/Dropbox/mutation/subjects/jaxen_subject/jaxen-original/";
		List<Report> reports = new ArrayList<Report>();
		int timelimit = 120;
		for (int i = 1; i <= 1; i++) {
			
//			String targetPath = "/Users/gustavoas/Dropbox/mutation/subjects/jaxen_subject/jaxen-m"
//					+ i + "/";
			String targetPath = "/Users/gustavoas/Dropbox/mutation/subjects/jaxen_subject/jaxen-m7/";
			System.out.println("mutante: " + targetPath);
			try {
				
				Report checkTransformation = SafeRefactorFacade
						.checkTransformation(sourcePath, targetPath, "bin",
								"src/java/main", "lib", false, timelimit, false);
				System.out.println("is refactoring? "
						+ checkTransformation.isRefactoring());
				reports.add(checkTransformation);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("------REPORT------------");
		for (Report report : reports) {
			System.out.println(report.isRefactoring());
		}

	}

}
