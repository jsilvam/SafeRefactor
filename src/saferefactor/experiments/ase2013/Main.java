package saferefactor.experiments.ase2013;

import java.util.ArrayList;
import java.util.List;

import saferefactor.core.Report;
import saferefactor.experiments.SafeRefactorFacade;

public class Main {

	public static void main(String[] args) {

		String[] classes = {
				"org.apache.commons.collections.BoundedFifoBuffer",
				"org.apache.commons.collections.IteratorUtils",
				"org.apache.commons.collections.LRUMap",
				"org.apache.commons.collections.MultiHashMap",
				"org.apache.commons.collections.map.FixedSizeMap",
				"org.apache.commons.collections.map.ReferenceMap",
				"org.apache.commons.collections.map.TransformedSortedMap",
				"org.apache.commons.collections.map.UnmodifiableOrderedMap",
				};

		double timelimit = 10;
//		String sourcePath = "/Users/alan/Dropbox/experiments/ase2013/jaxen/subject/jaxen";
		// sourcePath = "/Users/alan/Documents/workspace/original";
//		String targetPath = "/Users/alan/Dropbox/experiments/ase2013/jaxen/mutants/jaxen-m1";
		// targetPath =
		// "/Users/alan/Dropbox/experiments/ase2013/jaxen/subject/jaxen";

		String sourcePath = "/Users/alan/Dropbox/ase2013_/artefatos/experiments/commons-collections/original";

		List<Report> reports = new ArrayList<Report>();

		int i = 0;
		for (String classe : classes) {
			System.out.println("class " + i + ": " + classe);
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
			i++;
		}

		System.out.println("------REPORT------------");
		System.out.println("class\ttime\ttests");
		for (Report report : reports) {
			System.out.println(report.getClazz() + "\t" + report.getTotalTime());
		}
		
		for (Report report : reports) {
			System.out.println(report.getTmpFolder());
		}

	}
}
