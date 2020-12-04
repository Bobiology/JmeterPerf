package abstracta;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class AppTest {
    public static void main(String [] arg)throws FileNotFoundException,
            IOException {
        // Set jmeter home for the jmeter utils to load
        File jmeterHome = new File("/Users/Shared/SOFTWARE/MIDDLEWARE/apache-jmeter-5.3");
        String slash = "/";

        if (jmeterHome.exists()) {
            File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
            if (jmeterProperties.exists()) {
                StandardJMeterEngine jmeter = new StandardJMeterEngine();

                JMeterUtils.setJMeterHome(jmeterHome.getPath());
                JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
                // JMeterUtils.initLogging();// you can comment this line out to
                // see extra log messages of i.e. DEBUG level
                JMeterUtils.initLocale();

                HashTree testPlanTree = new HashTree();

                HTTPSamplerProxy httpsampler = new HTTPSamplerProxy();
                httpsampler.setDomain("jsonplaceholder.typicode.com");
                //httpsampler.setPort(GlobalVariables.DEFAULT_HTTP_PORT);
                httpsampler.setMethod("POST");
                httpsampler.setPath("/posts");

                httpsampler.addNonEncodedArgument("","{ data: {\"title\": \"venkatachalam\", \"body\": \"Venkata\", \"userId\": 3} }","");
                httpsampler.setPostBodyRaw(true);

                httpsampler.setName("Posting POSTS");
                httpsampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
                httpsampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
                LoopController loopController = new LoopController();
                loopController.setLoops(1);
                loopController.setFirst(true);
                loopController.setProperty(TestElement.TEST_CLASS,LoopController.class.getName());
                loopController.setProperty(TestElement.GUI_CLASS,LoopControlPanel.class.getName());
                loopController.initialize();

                ThreadGroup threadGroup = new ThreadGroup();
                threadGroup.setName("Sample Thread Group");
                threadGroup.setNumThreads(1);
                threadGroup.setRampUp(1);
                threadGroup.setSamplerController(loopController);
                threadGroup.setProperty(TestElement.TEST_CLASS,ThreadGroup.class.getName());
                threadGroup.setProperty(TestElement.GUI_CLASS,ThreadGroupGui.class.getName());

                TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");

                testPlan.setProperty(TestElement.TEST_CLASS,TestPlan.class.getName());
                testPlan.setProperty(TestElement.GUI_CLASS,TestPlanGui.class.getName());
                testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

                testPlanTree.add(testPlan);
                HashTree threadGroupHashTree = testPlanTree.add(testPlan,threadGroup);
                threadGroupHashTree.add(httpsampler);

                SaveService.saveTree(testPlanTree,new FileOutputStream("C:\\Users\\rvj03\\Documents\\JMETER results\\jmeter_api_sample.jmx"));

                Summariser summer = null;
                String summariserName = JMeterUtils.getPropDefault(
                        "summariser.name", "summary");
                if (summariserName.length() > 0) {
                    summer = new Summariser(summariserName);
                }

                String reportFile = "C:\\Users\\rvj03\\Documents\\JMETER results\\report.jtl";
                String csvFile = "C:\\Users\\rvj03\\Documents\\JMETER results\\report.csv";
                ResultCollector logger = new ResultCollector(summer);
                logger.setFilename(reportFile);
                ResultCollector csvlogger = new ResultCollector(summer);
                csvlogger.setFilename(csvFile);
                testPlanTree.add(testPlanTree.getArray()[0], logger);
                testPlanTree.add(testPlanTree.getArray()[0], csvlogger);
                jmeter.configure(testPlanTree);
                jmeter.run();

                System.out.println("Test completed. See " + jmeterHome + slash + "report.jtl file for results");
                System.out.println("JMeter .jmx script is available at " + jmeterHome + slash + "jmeter_api_sample.jmx");
                System.exit(0);

            }
        }

        System.err
                .println("jmeterHome property is not set or pointing to incorrect location");
        System.exit(1);
    }
}

