package tests;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import tests.bug.Bugs;
import tests.safira.Laws;
import tests.toys.ToyExamples;



public class MainTest {
	

	

	public class AllTests {

	}
	  public static Test suite() {
		    TestSuite result = new TestSuite();
		    result.addTest(new TestSuite(Bugs.class));
		    result.addTest(new TestSuite(Laws.class));
		    result.addTest(new TestSuite(ToyExamples.class));
		    return result;
		  }
	
	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
	    TestResult result = runner.doRun(suite(), false);
	    if (! result.wasSuccessful()) {
	      System.exit(1);
	    }
	}
	
}
