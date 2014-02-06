package CH.ifa.draw.test.samples.javadraw;



import junit.framework.TestSuite;
// JUnitDoclet begin import
// JUnitDoclet end import

/*
* Generated by JUnitDoclet, a tool provided by
* ObjectFab GmbH under LGPL.
* Please see www.junitdoclet.org, www.gnu.org
* and www.objectfab.de for informations about
* the tool, the licence and the authors.
*/


// JUnitDoclet begin javadoc_class
/**
* TestSuite JavadrawSuite
*/
// JUnitDoclet end javadoc_class
public class JavadrawSuite
// JUnitDoclet begin extends_implements
// JUnitDoclet end extends_implements
{
  // JUnitDoclet begin class
  // JUnitDoclet end class
  
  public static TestSuite suite() {
    
    TestSuite suite;
    
    suite = new TestSuite("CH.ifa.draw.test.samples.javadraw");
    
    suite.addTestSuite(CH.ifa.draw.test.samples.javadraw.URLToolTest.class);
    suite.addTestSuite(CH.ifa.draw.test.samples.javadraw.PatternPainterTest.class);
    suite.addTestSuite(CH.ifa.draw.test.samples.javadraw.MySelectionToolTest.class);
    suite.addTestSuite(CH.ifa.draw.test.samples.javadraw.JavaDrawViewerTest.class);
    suite.addTestSuite(CH.ifa.draw.test.samples.javadraw.JavaDrawAppletTest.class);
    suite.addTestSuite(CH.ifa.draw.test.samples.javadraw.JavaDrawAppTest.class);
    suite.addTestSuite(CH.ifa.draw.test.samples.javadraw.BouncingDrawingTest.class);
    suite.addTestSuite(CH.ifa.draw.test.samples.javadraw.AnimatorTest.class);
    suite.addTestSuite(CH.ifa.draw.test.samples.javadraw.AnimationDecoratorTest.class);
    
    
    
    // JUnitDoclet begin method suite()
    // JUnitDoclet end method suite()
    
    return suite;
  }
  
  /**
  * Method to execute the TestSuite from command line
  * using JUnit's textui.TestRunner .
  */
  public static void main(String[] args) {
    // JUnitDoclet begin method testsuite.main
    junit.textui.TestRunner.run(suite());
    // JUnitDoclet end method testsuite.main
  }
}
