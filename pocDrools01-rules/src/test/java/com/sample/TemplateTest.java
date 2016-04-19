package com.sample;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.template.ObjectDataCompiler;
import org.junit.Test;

import com.sample.domain.template.Cheese;
import com.sample.domain.template.Item;
import com.sample.domain.template.ItemCode;
import com.sample.domain.template.Person;
import com.sample.support.ParamSet;

public class TemplateTest {

    @Test
    public void testTemplate01() throws Exception {
        Collection<ParamSet> cfl = new ArrayList<ParamSet>();
        cfl.add(new ParamSet(EnumSet.of(ItemCode.LOCK, ItemCode.STOCK), "weight", 10, 99));
        cfl.add(new ParamSet(EnumSet.of(ItemCode.BARREL), "price", 10, 50));

        InputStream ruleStream = ResourceFactory.newClassPathResource("com/sample/template/rangeFields.drt").getInputStream();

        KnowledgeBase kBase = expandByObjects(ruleStream, cfl);

        StatefulKnowledgeSession ksession = kBase.newStatefulKnowledgeSession();
        KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
        ksession.insert(new Item("A", 130, 42, ItemCode.LOCK));
        ksession.insert(new Item("B", 44, 100, ItemCode.STOCK));
        ksession.insert(new Item("C", 123, 180, ItemCode.BARREL));
        ksession.insert(new Item("D", 85, 9, ItemCode.LOCK));

        ksession.fireAllRules();
        logger.close();
    }

    @Test
    public void testTemplate02() throws Exception {
        InputStream ruleStream = ResourceFactory.newClassPathResource("com/sample/template/cheeseFan.drt").getInputStream();
        InputStream excelStream = ResourceFactory.newClassPathResource("com/sample/template/DataRuleCheese.xls").getInputStream();
        
        KnowledgeBase kBase = expandByExcel(ruleStream, excelStream);
        
        StatefulKnowledgeSession ksession = kBase.newStatefulKnowledgeSession();
        KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
        ksession.insert(new Cheese("stilton", 100));
        ksession.insert(new Cheese("cheddar", 100));
        ksession.insert(new Person("michael", "stilton", 42));
        ksession.insert(new Person("jose", "cheddar", 21));
        
        List<String> lstLog = new ArrayList<String>();
        ksession.setGlobal("list", lstLog);
        ksession.fireAllRules();
        
        System.out.println("log:" + lstLog);
        
        logger.close();
    }
    
    private KnowledgeBase expandByObjects(InputStream ruleTemplateStream, Collection<?> act) throws Exception {
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        ObjectDataCompiler converter = new ObjectDataCompiler();

        String drl = converter.compile(act, ruleTemplateStream);

        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kBuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);

        if (kBuilder.hasErrors()) {
            for (KnowledgeBuilderError err : kBuilder.getErrors()) {
                System.err.println(err.toString());
            }
            throw new IllegalStateException("DRL errors");
        }
        kBase.addKnowledgePackages(kBuilder.getKnowledgePackages());
        return kBase;
    }

    private KnowledgeBase expandByExcel(InputStream ruleTemplateStream, InputStream excelData) throws Exception {
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        
        String drl = converter.compile(excelData, ruleTemplateStream, 2, 2);
        
        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        kBuilder.add(ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        
        if (kBuilder.hasErrors()) {
            for (KnowledgeBuilderError err : kBuilder.getErrors()) {
                System.err.println(err.toString());
            }
            throw new IllegalStateException("DRL errors");
        }
        kBase.addKnowledgePackages(kBuilder.getKnowledgePackages());
        return kBase;
    }
}
