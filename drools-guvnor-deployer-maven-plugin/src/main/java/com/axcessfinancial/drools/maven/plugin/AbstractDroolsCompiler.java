package com.axcessfinancial.drools.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;

/**
 * @author mlescano
 *
 */
public abstract class AbstractDroolsCompiler {
    
    public static String BPMNFILEEXTENSION = ".bpmn";
    public static String BRLFILEEXTENSION = ".brl";
    public static String XMLFILEEXTENSION = ".xml";
    public static String RULEFLOWMODELFILEEXTENSION = ".rfm";
    public static String RULEFLOWFILEEXTENSION = ".rf";
    public static String DSLFILEEXTENSION = ".dsl";
    public static String DSLRFILEEXTENSION = ".dslr";
    public static String XLSFILEEXTENSION = ".xls";
    public static String TEMPLATERULEFILEEXTENSION = ".drt";
    public static String DATAXLSFILEEXTENSION = "data-rules.xls";
    public static String DROOLSPACKAGEEXTENSION = ".package";

    public static String PACKAGE_BIN_FORMAT = "package";
    public static String BASE_BIN_FORMAT = "base";
    public static String KNOWLEDGE_BUILDER_TYPE = "knowledge";
    public static String PACKAGE_BUILDER_TYPE = "package";

    protected String binFormat;
    protected String builderType;
   
    abstract ClassLoader getLoader();
    
    public void compile(String srcdir, String packageName, String destdir) throws Exception {

        try {
            if (KNOWLEDGE_BUILDER_TYPE.equals(builderType)) {
                createWithKnowledgeBuilder(getLoader(), srcdir, packageName, destdir);
            } else {
                createWithPackageBuilder(getLoader(), srcdir, packageName, destdir);
            }
        } catch (Exception e) {
            throw new Exception("RuleBaseTask failed: " + e.getMessage(),
                    e);
        } finally {
//            if (getLoader() != null) {
//                getLoader().resetThreadContextLoader();
//            }
        }
    }
    
    private KnowledgeBuilder getKnowledgeBuilder(ClassLoader loader) {
        // creating package builder configured with the give classloader
        PackageBuilderConfiguration conf = new PackageBuilderConfiguration(
                loader);

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder(conf);
        return kbuilder;
    }
    
    /**
     * @param builder
     * @param fileName
     * @return
     * @throws FileNotFoundException
     * @throws DroolsParserException
     * @throws IOException
     */
    private void compileAndAddFile(KnowledgeBuilder kbuilder, File droolFile)
            throws FileNotFoundException, DroolsParserException, IOException {

        FileReader fileReader = new FileReader(droolFile);

        if (droolFile.getName().endsWith(BRLFILEEXTENSION)) {

            kbuilder.add(ResourceFactory.newReaderResource(fileReader),
                    ResourceType.BRL);

        } else if (droolFile.getName()
                .endsWith(RULEFLOWMODELFILEEXTENSION)
                || droolFile.getName()
                .endsWith(RULEFLOWFILEEXTENSION)) {

            kbuilder.add(ResourceFactory.newReaderResource(fileReader),
                    ResourceType.DRF);

        } else if (droolFile.getName().endsWith(XMLFILEEXTENSION)) {
            kbuilder.add(ResourceFactory.newReaderResource(fileReader), ResourceType.XDRL);
        } else if (droolFile.getName().endsWith(TEMPLATERULEFILEEXTENSION)) {
            try {
                InputStream ruleStream = new FileInputStream(droolFile);
                String dataRulesExcelFilename = droolFile.getName().substring(0, droolFile.getName().lastIndexOf('.')) + DATAXLSFILEEXTENSION;
                InputStream excelStream = new FileInputStream(new File(droolFile.getAbsolutePath(), dataRulesExcelFilename));
                ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
                String generatedDrl = converter.compile(excelStream, ruleStream, 2, 2);
                
                kbuilder.add(ResourceFactory.newReaderResource(new StringReader(generatedDrl)), ResourceType.DRL);
            }
            catch (Exception objEx) {
                /*getLog().info("Skipping " + resource.getName() + " because " + objEx.getMessage());
                continue;*/
            }
        } else if (droolFile.getName().endsWith(DATAXLSFILEEXTENSION)) {
            //nothing
        } else if (droolFile.getName().endsWith(XLSFILEEXTENSION)) {

            DecisionTableConfiguration dtableconfiguration = KnowledgeBuilderFactory.newDecisionTableConfiguration();
            dtableconfiguration.setInputType(DecisionTableInputType.XLS);
            kbuilder.add(ResourceFactory.newFileResource(droolFile), ResourceType.DTABLE, dtableconfiguration);

            // } else if
            // (fileName.endsWith(DroolsCompilerAntTask.DSLFILEEXTENSION)) {
            //
            // kbuilder.add(ResourceFactory.newReaderResource(fileReader),
            // ResourceType.DSL);

        } else if (droolFile.getName().endsWith(DSLRFILEEXTENSION)) {

            // Get the DSL too.
//            String[] dsls = resolveDSLFilesToArray();
//            for (int i = 0; i < dsls.length; i++) {
//                kbuilder.add(ResourceFactory.newFileResource(new File(
//                        this.srcdir, dsls[i])), ResourceType.DSL);
//            }
//
//            kbuilder.add(ResourceFactory.newReaderResource(fileReader),
//                    ResourceType.DSLR);

        } else if (droolFile.getName().endsWith(BPMNFILEEXTENSION)) {

            kbuilder.add(ResourceFactory.newReaderResource(fileReader), ResourceType.BPMN2);

        } else {
            kbuilder.add(ResourceFactory.newReaderResource(fileReader), ResourceType.DRL);
        }
    }
    
    private void compileAndAddFiles(KnowledgeBuilder kbuilder, String srcdir, String packageName)
            throws FileNotFoundException, DroolsParserException, IOException {
        // get the list of drools package files
//        String[] droolsPackageNames = getDroolsPackageFileList();
//        if (droolsPackageNames != null) {
//            for (int i = 0; i < droolsPackageNames.length; i++) {
//                // compile rule file and add to the builder
//                compileAndAddFile(kbuilder, droolsPackageNames[i]);
//            }
//        }

        // get the list of files to be added to the rulebase
        String[] fileNames = getFileList(srcdir, packageName);

        for (int i = 0; i < fileNames.length; i++) {
            // compile rule file and add to the builder
            System.out.println("Compiling: " + fileNames[i]);
            compileAndAddFile(kbuilder, getFile(srcdir, packageName, fileNames[i]));
            if (kbuilder.hasErrors()) {
                System.err.println(fileNames[i] + ": " + kbuilder.getErrors().toString());
            }
        }

    }

    abstract File getFile(String srcdir, String packageName, String string);

    abstract String[] getFileList(String srcdir, String packageName);

    private void createWithKnowledgeBuilder(ClassLoader loader, String srcdir, String packageName, String destdir)
            throws FileNotFoundException, DroolsParserException, IOException {
        // create a package builder configured to use the given classloader
        KnowledgeBuilder kbuilder = getKnowledgeBuilder(loader);

        // add files to rulebase
        compileAndAddFiles(kbuilder, srcdir, packageName);

        // gets the packages
        Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

        processPackages(pkgs);
        
        // creates the knowledge base
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(
                KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, loader));

        // adds the packages
        kbase.addKnowledgePackages(pkgs);

        if (PACKAGE_BIN_FORMAT.equals(binFormat)) {
            Iterator<KnowledgePackage> iter = pkgs.iterator();
            while (iter.hasNext()) {
                KnowledgePackage pkg = iter.next();
                processPackage(pkg);
                serializeObject(pkg, getDestFile(packageName, destdir));
            }
        } else {
            processKbase(kbase);
            // serialize the knowledge base to the destination file
            serializeObject(kbase, getDestFile(packageName, destdir));
        }
    }

    abstract File getDestFile(String packageName, String destdir);

    protected void processKbase(KnowledgeBase kbase) {
//        if (verboseoption) {
//            log("** Serializing KnowledgeBase to destination file.");
//        }
    }

    protected void processPackage(KnowledgePackage pkg) {
//        if (verboseoption) {
//            log("** Serializing package [" + pkg.getName() + "] to destination file. **** THIS WILL OVERRIDE ANY PREVIOUSLY SERIALIZED PACKAGE ****");
//        }
    }

    protected void processPackages(Collection<KnowledgePackage> pkgs) {
//      if (verboseoption) {
//      Iterator<KnowledgePackage> iter = pkgs.iterator();
//      while (iter.hasNext()) {
//          KnowledgePackage pkg = iter.next();
//          log("** Content of package: " + pkg.getName());
//          Iterator<Rule> riter = pkg.getRules().iterator();
//          while (riter.hasNext()) {
//              log("\tRule name: " + riter.next().getName());
//          }
//      }
//  }
    }
    
    /**
     * @param ruleBase
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void serializeObject(Object object, File toFile) throws FileNotFoundException,
            IOException {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(toFile);
            DroolsStreamUtils.streamOut(fout, object);
        } finally {
            if (fout != null) {
                fout.close();
            }
        }
    }
    
    /**
     * @param loader
     * @return
     */
    private PackageBuilder getPackageBuilder(ClassLoader loader) {
        // creating package builder configured with the give classloader
        PackageBuilderConfiguration conf = new PackageBuilderConfiguration(
                loader);
        PackageBuilder builder = new PackageBuilder(conf);
        return builder;
    }
    
    /**
     * @param builder
     * @param fileName
     * @return
     * @throws FileNotFoundException
     * @throws DroolsParserException
     * @throws IOException
     */
    private void compileAndAddFile(PackageBuilder builder, File droolFile)
            throws FileNotFoundException, DroolsParserException, IOException {
        InputStreamReader instream = null;

        try {

            instream = new InputStreamReader(new FileInputStream(droolFile));

            if (droolFile.getName().endsWith(RULEFLOWMODELFILEEXTENSION) || 
                    droolFile.getName().endsWith(RULEFLOWFILEEXTENSION)) {
                builder.addRuleFlow(instream);
            } else if (droolFile.getName().endsWith(XMLFILEEXTENSION)) {
                builder.addPackageFromXml(instream);

            } else if (droolFile.getName().endsWith(BRLFILEEXTENSION)) {
                builder.addPackageFromBrl(ResourceFactory.newReaderResource(instream));
            } else if (droolFile.getName().endsWith(XLSFILEEXTENSION)) {

                final SpreadsheetCompiler converter = new SpreadsheetCompiler();
                final String drl = converter.compile(ResourceFactory.newFileResource(droolFile).getInputStream(),
                        InputType.XLS);

                System.out.println(drl);

                builder.addPackageFromDrl(new StringReader(drl));

            } else if (droolFile.getName().endsWith(DSLRFILEEXTENSION)) {
//                DrlParser parser = new DrlParser();
//                String expandedDRL = parser.getExpandedDRL(
//                        loadResource(droolFile.getName()), resolveDSLFiles());
//                builder.addPackageFromDrl(new StringReader(expandedDRL));
            } else if (droolFile.getName().endsWith(BPMNFILEEXTENSION)) {

                //kbuilder.add(ResourceFactory.newReaderResource(fileReader), ResourceType.BPMN2);
                builder.addPackageFromXml(instream);

            } else {
                builder.addPackageFromDrl(instream);
            }
        } finally {
            if (instream != null) {
                instream.close();
            }
        }
    }
    
    private void compileAndAddFiles(PackageBuilder pbuilder, String srcdir, String packageName)
            throws FileNotFoundException, DroolsParserException, IOException {
        // get the list of drools package files
//        String[] droolsPackageNames = getDroolsPackageFileList();
//        if (droolsPackageNames != null) {
//            for (int i = 0; i < droolsPackageNames.length; i++) {
//                // compile rule file and add to the builder
//                compileAndAddFile(pbuilder, droolsPackageNames[i]);
//            }
//        }

        // get the list of files to be added to the rulebase
        String[] fileNames = getFileList(srcdir, packageName);

        for (int i = 0; i < fileNames.length; i++) {
            // compile rule file and add to the builder
            compileAndAddFile(pbuilder, getFile(srcdir, packageName, fileNames[i]));
        }

        if (pbuilder.hasErrors()) {
            System.err.println(pbuilder.getErrors().toString());
        }
    }

    private void createWithPackageBuilder(ClassLoader loader, String srcdir, String packageName, String destdir)
            throws FileNotFoundException, DroolsParserException, IOException {
        // create a package builder configured to use the given classloader
        PackageBuilder builder = getPackageBuilder(loader);

        compileAndAddFiles(builder, srcdir, packageName);

        org.drools.rule.Package[] packages = builder.getPackages();

        processPackages(packages);

        // gets the package
        //org.drools.rule.Package pkg = builder.getPackage();

        // creates the rulebase
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        // adds the packages
        ruleBase.addPackages(packages);

        if (PACKAGE_BIN_FORMAT.equals(binFormat)) {
            for (org.drools.rule.Package pkg : packages) {
                processPackage(pkg);
                serializeObject(pkg, getDestFile(packageName, destdir));
            }
        } else {
            processRuleBase(ruleBase);
            // serialize the rule base to the destination file
            serializeObject(ruleBase, getDestFile(packageName, destdir));
        }
    }

    protected void processRuleBase(RuleBase ruleBase) {
//        if (verboseoption) {
//            log("** Serializing RuleBase to destination file.");
//        }
    }

    protected void processPackage(Package pkg) {
        // TODO Auto-generated method stub
        
    }

    protected void processPackages(Package[] packages) {
        // TODO Auto-generated method stub
        
    }
}
