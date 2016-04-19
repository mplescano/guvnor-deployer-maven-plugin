package com.sample;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Assert;
import org.junit.Test;

import com.sample.domain.Message;
import com.sample.domain.insuring.QuickQuoteInputProfile;
import com.sample.domain.insuring.QuickQuoteResult;
import com.sample.domain.insuring.SmokerProfile;

/**
 * This is a sample class to launch a decision table.
 */
public class DecisionTableTest {

    @Test
    public void test01() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/Sample.xls");
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            Message message = new Message();
            message.setMessage("Hello World");
            message.setStatus(Message.HELLO);
            ksession.insert(message);
            ksession.fireAllRules();
            logger.close();
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
            t.printStackTrace();
        }
    }
    
    /**
     * 
     * @see https://technicalmumbojumbo.wordpress.com/2009/03/28/jboss-drools-decision-tables/
     */
    @Test
    public void test02() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/insuring/EligibilityRules.xls");
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !

            QuickQuoteInputProfile input = new QuickQuoteInputProfile();
            input.setAge(21);
            input.setFaceAmount(Double.valueOf(200000));
            input.setState("VA");
            input.setGender("MALE");
            input.setAdverseDiagnosis(false);
            
            SmokerProfile prof = new SmokerProfile("Y");
            input.setSmokingProf(prof);
            
            QuickQuoteResult result = new QuickQuoteResult(false);
            
            ksession.insert(input);
            ksession.setGlobal("result", result);
            
            ksession.fireAllRules();
            logger.close();
            
            Assert.assertTrue(result.isEligible());
            Assert.assertNotNull(result.getProfile());
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
            t.printStackTrace();
        }
    }
    
    @Test
    public void test03() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/insuring/EligibilityRulesV2.xls");
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !

            QuickQuoteInputProfile input = new QuickQuoteInputProfile();
            input.setAge(21);
            input.setFaceAmount(Double.valueOf(200000));
            input.setState("VA");
            input.setGender("MALE");
            input.setAdverseDiagnosis(false);
            
            SmokerProfile prof = new SmokerProfile("Y");
            input.setSmokingProf(prof);
            
            QuickQuoteResult result = new QuickQuoteResult(false);
            
            ksession.insert(input);
            ksession.setGlobal("result", result);
            
            ksession.fireAllRules();
            logger.close();
            
            Assert.assertTrue(result.isEligible());
            Assert.assertNotNull(result.getProfile());
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
            t.printStackTrace();
        }
    }
    
    @Test
    public void test04() {
        try {
            SpreadsheetCompiler comp = new SpreadsheetCompiler();
            
            String drl = comp.compile( false, ResourceFactory.newClassPathResource("com/sample/insuring/EligibilityRulesV2.xls").getInputStream(), InputType.XLS);
            
            System.out.println("DRL:" + drl);
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
            t.printStackTrace();
        }
    }

    private static KnowledgeBase readKnowledgeBase(String pathFile) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        DecisionTableConfiguration config = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        config.setInputType(DecisionTableInputType.XLS);
        kbuilder.add(ResourceFactory.newClassPathResource(pathFile), ResourceType.DTABLE, config);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error: errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase;
    }
}
