package com.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Test;

import com.sample.domain.Account;
import com.sample.domain.AnnotatedMessage;
import com.sample.domain.Applicant;
import com.sample.domain.Application;
import com.sample.domain.ListenerSupportMessage;
import com.sample.domain.Message;
import com.sample.domain.building.Fire;
import com.sample.domain.building.Room;
import com.sample.domain.building.Sprinkler;
import com.sample.support.TestDroolsCompiler;

/**
 * This is a sample class to launch a rule.
 */
public class DroolsTest {

    @Test
    public void test01() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/Sample.drl");
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            Message message = new Message();
            message.setMessage("Hello World");
            message.setStatus(Message.HELLO);
            ksession.insert(message);
            ksession.fireAllRules();
            ksession.dispose();
            logger.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    /**
     * 1) In case of Stateless Knowledge Session, while rules execution i.e. 
     *   once fireRules method is called, external modification in the inserted facts (in the then part) is not available to the rules engine. 
     *   In case of Stateful Knowledge Session, any changes in the facts is available to the rule engine.

        2) Once rules are fired, Stateful Knowledge Session object must call the method dispose() to release the session and avoid memory leaks.

        3) In case of Stateful Knowledge Session, any external changes to facts is available to the rule engine. So the rules are called iteratively. 
          If Fact A is modified in last rule of DRL, then this change will re-activate all the rules and fire the rules that are build on Fact A. 
          This is not the case with Stateless Knowledge Session.

        The hidden fact is Stateless session uses a Stateful session behind it
     * 
     * @see http://stackoverflow.com/questions/17175037/droolsstateless-vs-stateful-knowledge-session
     */
    @Test
    public void testStateless01() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/Sample.drl");
            StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            Message message = new Message();
            message.setMessage("Hello World");
            message.setStatus(Message.HELLO);
            ksession.execute(message);
            logger.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Test
    public void testStateless02() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/withoutupdate/SampleWithoutUpdate.drl");
            StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            Message message = new Message();
            message.setMessage("Hello World");
            message.setStatus(Message.HELLO);
            ksession.execute(message);
            logger.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Test
    public void testStateless03() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/withmodify/SampleWithModify.drl");
            StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            Message message = new Message();
            message.setMessage("Hello World");
            message.setStatus(Message.HELLO);
            
            ksession.execute(message);
            logger.close();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Test(timeout=5000)
    public void testStateless04() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/withmodifyinfiniteloop/SampleWithModifyInfiniteLoop.drl");
            StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            Message message = new Message();
            message.setMessage("Hello World");
            message.setStatus(Message.HELLO);
            
            ksession.execute(message);
            logger.close();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Test
    public void testStateless05() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/SampleWithModifyPropertyReactiveAnnotated.drl");
            StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            AnnotatedMessage message = new AnnotatedMessage();
            message.setMessage("Hello World");
            message.setStatus(AnnotatedMessage.HELLO);
            
            ksession.execute(message);
            logger.close();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Test
    public void testStateless06() {
        try {
            //load up the knowledge base
            KnowledgeBaseImpl kbase = (KnowledgeBaseImpl) readKnowledgeBase("com/sample/listenersupport/SampleWithoutUpdateButListenerSupport.drl");
            
            StatefulSession session = kbase.ruleBase.newStatefulSession();
            WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(session);
            logger.setFileName( "test" );
            
            // go !
            ListenerSupportMessage message = new ListenerSupportMessage();
            message.setMessage("Hello World");
            message.setStatus(ListenerSupportMessage.HELLO);
            
            session.insert(message, true);
            session.fireAllRules();
            session.dispose();
            
            logger.writeToDisk();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Test
    public void testStateless07() {
        try {
            //load up the knowledge base
            KnowledgeBaseImpl kbase = (KnowledgeBaseImpl) readKnowledgeBase("com/sample/listenersupportinferred/SampleWithoutUpdateButListenerSupportInferredEvaluation.drl");
            
            StatefulSession session = kbase.ruleBase.newStatefulSession();
            WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger(session);
            logger.setFileName( "test" );
            
            // go !
            ListenerSupportMessage message = new ListenerSupportMessage();
            message.setMessage("Hello World");
            message.setStatus(ListenerSupportMessage.HELLO);
            
            session.insert(message, true);
            session.fireAllRules();
            session.dispose();
            
            logger.writeToDisk();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    /**
     * it's not working
     * 
     */
    @Test
    public void testStateless08() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase(new String[]{"com/sample/listenersupportinferred/SampleWithoutUpdateButListenerSupportInferredEvaluation.drl", "com/sample/listenersupportinferred/Declarative02.model"});
            StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            ListenerSupportMessage message = new ListenerSupportMessage();
            message.setMessage("Hello World");
            message.setStatus(ListenerSupportMessage.HELLO);
            
            ksession.addEventListener( new DebugAgendaEventListener() );
            ksession.addEventListener( new DebugWorkingMemoryEventListener() );
            
            ksession.execute(message);
            logger.close();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Test
    public void test02() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/Account.drl");
            StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            Account account = new Account(200);
            account.withdraw(150);
            /*ksession.insert(account);
            ksession.fireAllRules();*/
            ksession.execute(account);
            logger.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Test
    public void test03() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/EvaluateApp.drl");
            StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            Applicant app = new Applicant();
            app.setAge(16);
            app.setName("Mr Jhon Smith");
            app.setValid(true);

            Application apt = new Application();
            apt.setDateApplied(new Date());
            apt.setValid(true);
                        
            Assert.assertTrue(app.isValid());
            Assert.assertTrue(apt.isValid());
            
            ksession.execute(Arrays.asList(new Object[]{app, apt}));
            
            Assert.assertFalse("expected true", app.isValid());
            Assert.assertFalse("expected true", apt.isValid());
            
            logger.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Test
    public void test04() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/EvaluateApp.drl");
            StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            Applicant app = new Applicant();
            app.setAge(16);
            app.setName("Mr Jhon Smith");
            app.setValid(true);

            Application apt = new Application();
            apt.setDateApplied(new Date());
            apt.setValid(true);
                        
            Assert.assertTrue(app.isValid());
            Assert.assertTrue(apt.isValid());
            
            List<Command> cmds = new ArrayList<Command>();
            cmds.add(CommandFactory.newInsert(app, "app"));
            cmds.add(CommandFactory.newInsert(apt, "apt"));
            
            ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(cmds));
            
            Assert.assertNotNull(results.getValue("app"));
            Assert.assertNotNull(results.getValue("apt"));
            
            
            Assert.assertFalse("expected true", app.isValid());
            Assert.assertFalse("expected true", apt.isValid());
            
            logger.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Test
    public void test05() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/building/fireAlarm.drl");
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            
            String[] names = new String[]{"kitchen", "bedroom", "office", "livingroom"};
            Map<String, Room> rooms = new HashMap<String, Room>();
            for (String name : names) {
                Room room = new Room(name);
                rooms.put(name, room);
                ksession.insert(room);
                
                Sprinkler sprin = new Sprinkler(room, false);
                ksession.insert(sprin);
            }
            
            ksession.fireAllRules();
            

            Fire kitchenFire = new Fire(rooms.get("kitchen"));
            Fire officeFire = new Fire(rooms.get("office"));
            
            FactHandle kitchenFireFactHandle = ksession.insert(kitchenFire);
            FactHandle officeFireHandle = ksession.insert(officeFire);

            ksession.fireAllRules();
            
            ksession.retract(officeFireHandle);
            ksession.retract(kitchenFireFactHandle);
            
            ksession.fireAllRules();
            
            logger.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    @Test
    public void test06() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/building/NotCrossProduct.drl");
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            
            String[] names = new String[]{"kitchen", "bedroom", "office", "livingroom"};
            Map<String, Room> rooms = new HashMap<String, Room>();
            for (String name : names) {
                Room room = new Room(name);
                rooms.put(name, room);
                ksession.insert(room);
                
                Sprinkler sprin = new Sprinkler(room, false);
                ksession.insert(sprin);
            }
            
            ksession.fireAllRules();

            
            logger.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void test07() {
        try {
            // load up the knowledge base
            KnowledgeBase kbase = readKnowledgeBase("com/sample/building/CrossedProduct.drl");
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
            // go !
            
            String[] names = new String[]{"kitchen", "bedroom", "office", "livingroom"};
            Map<String, Room> rooms = new HashMap<String, Room>();
            for (String name : names) {
                Room room = new Room(name);
                rooms.put(name, room);
                ksession.insert(room);
                
                Sprinkler sprin = new Sprinkler(room, false);
                ksession.insert(sprin);
            }
            
            ksession.fireAllRules();

            
            logger.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    private static KnowledgeBase readKnowledgeBase(String pathFile) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(pathFile), ResourceType.DRL);
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

    private static KnowledgeBase readKnowledgeBase(String[] pathFiles) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (String pathFile : pathFiles) {
            kbuilder.add(ResourceFactory.newClassPathResource(pathFile), ResourceType.DRL);
        }
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
    
    @Test
    public void test08() throws Exception {
        TestDroolsCompiler compiler = new TestDroolsCompiler(TestDroolsCompiler.PACKAGE_BIN_FORMAT, TestDroolsCompiler.KNOWLEDGE_BUILDER_TYPE);
        compiler.compile("src/main/rules", "com.sample", "target/rules");
        
        //
    }
}
