package saferefactor.experiments.eclipse;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import saferefactor.core.Parameters;
import saferefactor.core.Report;
import saferefactor.core.SafeRefactor;
import saferefactor.core.SafeRefactorImp;
import saferefactor.core.util.Project;

public class EclipseSuiteTest {
	private String pathTest = "/home/uian/Dropbox/Pesquisa_Uian/Testes_eclipse/PullUp/test";
	private String pathTestGenerics = "/home/uian/Dropbox/Pesquisa_Uian/Testes_eclipse/PullUp/testGenerics";
	
	
	@Test
	public void testCheckPullUp () throws IOException {
        File dir = new File("/home/uian/Dropbox/Pesquisa_Uian/DadosTestesEclipse");
        File arq = new File(dir, "EclipsePullUp_SFtest.txt");
        arq.createNewFile();
        FileWriter fileWriter = new FileWriter(arq, false);;
        PrintWriter printWriter = new PrintWriter(fileWriter);
        
		for (int i = 0 ; i <= 49 ; i++) {
			String pathIn = pathTest + Integer.toString(i) + "/in";
			File f1 = new File(pathIn,"p");
			FileUtils.deleteDirectory(f1);			
			String pathOut = pathTest + Integer.toString(i) + "/out";
			File f2 = new File(pathOut,"p");
			FileUtils.deleteDirectory(f2);
			String msg = "test" + Integer.toString(i);
			aplicaSF(printWriter, pathIn, pathOut, msg);
		}
		for (int i = 0 ; i <= 15 ; i++) {
			String pathIn = pathTestGenerics + Integer.toString(i) + "/in";
			String pathOut = pathTestGenerics + Integer.toString(i) + "/out";;
			String msg = "testGenerics" + Integer.toString(i);
			aplicaSF(printWriter, pathIn, pathOut, msg);
		}
		
		printWriter.close();
	}

	private void aplicaSF(PrintWriter printWriter, String pathIn, String pathOut, String msg) {
		Project source = new Project();
		source.setProjectFolder(new File(pathIn));
		source.setBuildFolder(new File(pathIn));
		source.setSrcFolder(new File(pathIn));

		Project target = new Project();
		target.setProjectFolder(new File(pathOut));
		target.setBuildFolder(new File(pathOut));
		target.setSrcFolder(new File(pathOut));

		Parameters parameters = new Parameters();
		parameters.setCompileProjects(true);		
		parameters.setTimeLimit(1);
		parameters.setTestNotPublic(true);
		parameters.getTestGeneratorParameters().add("--junit-package-name=p");
		parameters.getTestGeneratorParameters().add("--junit-output-dir="+ source.getSrcFolder());
		SafeRefactor saferefactor;
		
		try {
			saferefactor = new SafeRefactorImp(source, target, parameters);
			saferefactor.checkTransformation();
			Report report = saferefactor.getReport();
			
			printWriter.println(msg + " " + report.isRefactoring());	
			if (!report.isRefactoring()) {
				printWriter.println(report.getTmpFolder());
				if (report.hasCompilationError())
					printWriter.println("erro de compilacao");
				else
					printWriter.println(report.getChanges());
			}

				
		} catch (Exception e) {
			printWriter.println(msg + " " + "exception");
			e.printStackTrace();
		} finally {
			printWriter.flush();
		}
		
	}
	
	@Test
	public void testOneCase() throws Exception {
		String pathIn = "/home/uian/Dropbox/Pesquisa_Uian/Testes_eclipse/PullUp/testGenerics7/in";
		String pathOut = "/home/uian/Dropbox/Pesquisa_Uian/Testes_eclipse/PullUp/testGenerics7/out";
		
		File f1 = new File(pathIn,"p");
		FileUtils.deleteDirectory(f1);
		File f2 = new File(pathOut,"p");
		FileUtils.deleteDirectory(f2);
		Project source = new Project();
		source.setProjectFolder(new File(pathIn));
		source.setBuildFolder(new File(pathIn));
		source.setSrcFolder(new File(pathIn));

		Project target = new Project();
		target.setProjectFolder(new File(pathOut));
		target.setBuildFolder(new File(pathOut));
		target.setSrcFolder(new File(pathOut));

		Parameters parameters = new Parameters();
		parameters.setCompileProjects(true);		
		parameters.setTimeLimit(1);
		parameters.setTestNotPublic(true);
		
		parameters.getTestGeneratorParameters().add("--junit-package-name=p");
		parameters.getTestGeneratorParameters().add("--junit-output-dir="+ source.getSrcFolder());
		SafeRefactor saferefactor;
		
		try {
			saferefactor = new SafeRefactorImp(source, target, parameters);
			saferefactor.checkTransformation();
			Report report = saferefactor.getReport();

			System.out.println("test 7" + " " + report.isRefactoring());	
			if (!report.isRefactoring()) {
				if (report.hasCompilationError())
					System.out.println("erro de compilacao");
				else
					System.out.println(report.getChanges());
			}
				
		} catch (Exception e) {
			System.out.println("test 7" + " " + "exception");
			e.printStackTrace();
		} finally {
		//	printWriter.flush();
		}
		
	}
	
}
