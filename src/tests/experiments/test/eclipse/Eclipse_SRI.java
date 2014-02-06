package tests.experiments.test.eclipse;
import io.OutputManager;
import io.OutputManagerASCII;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import saferefactor.safira.analyzer.Tools;
import tests.SRImpact;



public class Eclipse_SRI {
		
	
	private static final String WARNING_STATUS = "REFACTORING_INAPPLICABLE";
	private static final String COMPILATION_ERROR = "POST_REFACTOR_NOT_COMPILE";
	private static final String INPUT_COMPILATION_ERROR = "PRE_REFACTOR_NOT_COMPILE";
	private static final String ENGINE_CRASH = "REFACTORING_EXECUTION_ERROR";
	private static final String POSTCONDITION_FAILURE = "POSTCONDITION_FAILURE";
	private static final String NO_ERROR = "NO_ERROR";
	
	List<Integer> compilation_errors = new ArrayList<Integer>();
	List<Integer> input_compilation_errors = new ArrayList<Integer>();
	List<Integer> engine_crash = new ArrayList<Integer>();
	List<Integer> warning_status = new ArrayList<Integer>();
	List<Integer> pos_condition_failure = new ArrayList<Integer>();
	List<Integer> behavioral_changes = new ArrayList<Integer>();
		
	public String getError(String dir, int number) {
		File testDir = new File(dir);
		String[] files = testDir.list();
		for (String file : files) {
			if (file.equals(WARNING_STATUS)) {
				warning_status.add(number);
				return file;
			} else if (file.equals(COMPILATION_ERROR)) {
				compilation_errors.add(number);
				return file;
			}else if(file.equals(INPUT_COMPILATION_ERROR)) {
				input_compilation_errors.add(number);
				return file;
			} else if (file.equals(ENGINE_CRASH)) {
				engine_crash.add(number);
				return file;
			} else if(file.equals(POSTCONDITION_FAILURE)) {
				pos_condition_failure.add(number);
				return file;
			}
		}
		return NO_ERROR;
	}
	
	public void writeMessages(OutputManager file) throws IOException {
		file.writeLine("compilation_errors");
		file.writeLine(compilation_errors.toString());
		
		file.writeLine("input_compilation_errors");
		file.writeLine(input_compilation_errors.toString());
		
		file.writeLine("engine_crash");
		file.writeLine(engine_crash.toString());
		
		file.writeLine("warning_status");
		file.writeLine(warning_status.toString());
		
		file.writeLine("pos_condition_failure");
		file.writeLine(pos_condition_failure.toString());
		
		file.writeLine("behavioral_changes");
		file.writeLine(behavioral_changes.toString());
	}
	
	
	public void evaluate(String refactoring) throws IOException {
		
		
		String path = "/private/var/folders/uB/uBSypadvEUa5OUqqynr2Tk+++TI/-Tmp-/"+refactoring+"/alloyASTGenerator1/";
		path = "/Users/melmongiovi/Documents/doutorado/experimentos/eclipse/pushdownmethod/skip5/";
//		path = "/Users/melmongiovi/Documents/doutorado/experimentos/jrrt3/pullupmethod_noSkip/";
//		path = "/Users/melmongiovi/Documents/doutorado/experimentos/eclipse/pullupmethod/noskip/";
		String name ="eclipse";
		String timeLimit = "0.3";
		
		String pathOut = Tools.getAspectPath();
		String arquivoMessages = pathOut +"/SCP/SRI/"+name+"/"+name+"_sri_message_"+refactoring+"_"+timeLimit+".txt";
		OutputManager outMessages = new OutputManagerASCII(arquivoMessages);
		outMessages.createFile();
		
		String arquivoResult = pathOut +"/SCP/SRI/"+name+"/"+name+"_sri_result_"+refactoring+"_"+timeLimit+".txt";
		OutputManager outResult = new OutputManagerASCII(arquivoResult);
		outResult.createFile();
		
		String arquivoMethods = pathOut +"/SCP/SRI/"+name+"/"+name+"_sri_methods_"+refactoring+"_"+timeLimit+".txt";
		OutputManager outMethods = new OutputManagerASCII(arquivoMethods);
		outMethods.createFile();
		
		String arquivoTime = pathOut +"/SCP/SRI/"+name+"/"+name+"_sri_time_"+refactoring+"_"+timeLimit+".txt";
		OutputManager outTime = new OutputManagerASCII(arquivoTime);
		outTime.createFile();

		
		
		
		
		
		long totalTimeExper = System.currentTimeMillis();
		
//		for (Integer number : numbers) {
		int [] bc = { 13, 14, 15, 18, 19, 21, 71, 72, 130, 131, 134, 137, 161, 163, 165, 218, 222, 244, 245, 302, 303, 361, 363, 366, 390, 391, 395, 446, 447, 532, 533, 593, 643, 644, 653, 654, 722, 724, 725, 740, 741, 782, 783, 876, 879, 894, 935, 936, 990, 1027, 1028, 1072, 1128, 1130, 1132, 1134, 1183, 1184, 1185, 1188, 1189, 1191, 1212, 1213, 1300, 1301, 1305, 1327, 1328, 1329, 1332, 1333, 1335, 1418, 1471, 1472, 1473, 1476, 1477, 1479, 1524, 1590, 1634, 1635, 1639, 1640, 1658, 1667, 1694, 1790, 1791, 1795, 1816, 1818, 1823, 1850, 1965, 1993, 1994, 1996, 1998, 2021, 2023, 2025, 2027, 2136, 2138, 2140, 2142, 2193, 2194, 2198, 2220, 2221, 2222, 2225, 2226, 2228, 2278, 2279, 2307, 2308, 2396, 2398, 2400, 2402, 2446, 2512, 2595, 2596, 2597, 2600, 2601, 2603, 2623, 2625, 2630, 2653, 2654, 2771, 2793, 2796, 2870, 2873, 2889, 2890, 2947, 2950, 2963, 2965, 2968, 3024, 3027, 3061, 3064, 3097, 3123, 3174, 3200, 3351, 3354, 3440, 3441, 3480, 3483, 3484, 3504, 3505, 3616, 3650, 3677, 3715, 3735, 3986, 3987};

		for (int number = 1; number <= 4108	; number++) {
			for (int i : bc) {
			if (number != i) continue;	
			
			try {
			String source = path + "test"+number+"/in/";
			
			String target = path + "test"+number+"/out/eclipse/";
			
			if (!getError(target, number).equals(NO_ERROR)) {
				continue;
			}
			
			int numberOfTests = 0;
			int affectedTests = 0;
			
			long randoopTime = 0;
			
			 
			int methods = 0;
			int impactedMethods = 0;
			boolean isRefactoring = true;
			
			String tests = "";
			String lib = "";
			String bin = "";
//			long time = System.currentTimeMillis();	
			
			SRImpact c;
			long analysisTime;
			long totalTime;
			c = new SRImpact(tests, source, target, lib, timeLimit,bin, true);
			totalTime = c.getTotalTime()/1000;
			isRefactoring = c.isRefactoring();
			System.out.println(isRefactoring);
			System.out.println("is compilation error? "+c.isTestCompilationError());
			
			c.getIa().print();
			
			analysisTime = c.getAnalysisTime()/1000;
			randoopTime = c.getRandoopTime()/1000;
			methods = c.getIa().getMethodsToGenerateTests();
			impactedMethods = c.getIa().getImpactedMethods();
			numberOfTests = 0;
			
			//calcular os testes afetados, os testes vao ser gerados e compilados
			
			
				
			if (!isRefactoring) {
				behavioral_changes.add(number);
			}
				
			outResult.writeLine(""+isRefactoring);
			outMethods.writeLine(""+methods);
			outTime.writeLine(""+totalTime);
			
			}catch(Exception e) {
				outResult.closeFile();
				outMethods.closeFile();
				outTime.closeFile();

			}
			}
		}
			totalTimeExper = System.currentTimeMillis() - totalTimeExper;
			System.out.println("totalTime "+totalTimeExper);
			writeMessages(outMessages);
			outResult.closeFile();
			outMethods.closeFile();
			outTime.closeFile();
			outMessages.closeFile();
		
		
	}
	public static void main(String[] args) throws IOException {
		Eclipse_SRI e = new Eclipse_SRI();
		e.evaluate("pushdownmethod");
	}
	
	
}
