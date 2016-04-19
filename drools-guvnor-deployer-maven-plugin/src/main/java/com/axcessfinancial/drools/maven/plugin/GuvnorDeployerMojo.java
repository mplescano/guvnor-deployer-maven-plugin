package com.axcessfinancial.drools.maven.plugin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.utils.io.DirectoryScanner;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.components.io.fileselectors.IncludeExcludeFileSelector;
import org.codehaus.plexus.util.StringUtils;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.junit.Assert;

@Mojo(name = "guvnor-deployer", defaultPhase = LifecyclePhase.INSTALL, requiresProject = false)
public class GuvnorDeployerMojo extends AbstractDependencyMojo {
    
    /**
     * To look up Archiver/UnArchiver implementations
     */
    @Component
    private ArchiverManager archiverManager;
    
    /**
     * A comma separated list of file patterns to include when unpacking the artifact. i.e. **\/*.xml,**\/*.properties
     * NOTE: Excludes patterns override the includes. (component code = return isIncluded( name ) AND !isExcluded( name
     * );)
     *
     * @since 2.0-alpha-5
     */
    @Parameter( property = "guvnor.deployer.includes", defaultValue = "**/*.*")
    private String includes;

    /**
     * A comma separated list of file patterns to exclude when unpacking the artifact. i.e. **\/*.xml,**\/*.properties
     * NOTE: Excludes patterns override the includes. (component code = return isIncluded( name ) AND !isExcluded( name
     * );)
     *
     * @since 2.0-alpha-5
     */
    @Parameter( property = "guvnor.deployer.excludes", defaultValue = "**/*.class,META-INF/**" )
    private String excludes;

    /**
     * Collection of ArtifactItems to work on. (ArtifactItem contains groupId, artifactId, version, type, classifier,
     * outputDirectory, destFileName and overWrite.) See <a href="./usage.html">Usage</a> for details.
     *
     * @since 1.0
     */
    @Parameter
    private List<ArtifactItem> artifactRules;
    
    @Parameter
    private List<ArtifactItem> artifactModels;
    
    @Parameter( property = "guvnor.webapp.url", defaultValue = "http://localhost:8080/drools-guvnor/rest/packages/", required = true )
    private String webAppUrl;
    
    @Parameter( property = "guvnor.user", defaultValue = "guvnor", required = true )
    private String userGuvnor;
    
    @Parameter( property = "guvnor.pass", defaultValue = "123456", required = true )
    private String passGuvnor;
    
    @Override
    public void doExecute() throws MojoExecutionException, MojoFailureException {

        verifyRequirements(artifactRules);
        verifyRequirements(artifactModels);
        
        getProcessedArtifactItems( artifactRules );
        getProcessedArtifactItems(artifactModels);
        
        List<File> lstModelResources = new ArrayList<File>();
        for ( ArtifactItem artifactModel : artifactModels ) {
            getLog().debug( "Model file:" +  artifactModel.getArtifact().getFile().getName());
            lstModelResources.add(artifactModel.getArtifact().getFile());
        }
        
        for ( ArtifactItem artifactRule : artifactRules )
        {
            unpack( artifactRule.getArtifact(), artifactRule.getType(), artifactRule.getOutputDirectory(),
                    artifactRule.getIncludes(), artifactRule.getExcludes() );
        }
        
        DirectoryScanner dirScanner = new DirectoryScanner();
        dirScanner.setBasedir(outputDirectory);
        dirScanner.setIncludes(includes);
        dirScanner.setExcludes(excludes);
        dirScanner.setCaseSensitive( true );
        dirScanner.scan();

        WebClient client = doRequestV2(webAppUrl, userGuvnor, passGuvnor);
        Map<String, List<File>> mpPackages = new HashMap<String, List<File>>();
        String[] files = dirScanner.getIncludedFiles();
        for (String resourceRelativePath : files)
        {
            getLog().debug( "Rule file:" +  resourceRelativePath);
            File resourceRule = new File(outputDirectory, resourceRelativePath);
            List<File> lstRuleResources;
            //String resourceAbsolutePath = resource.getAbsolutePath();
            String resourcePackagePath = resourceRelativePath.substring(0, resourceRelativePath.indexOf(resourceRule.getName()) - 1);
            getLog().debug( "PACKAGE:" +  resourcePackagePath);
            if (!mpPackages.containsKey(resourcePackagePath)) {
                lstRuleResources = new ArrayList<File>();
                mpPackages.put(resourcePackagePath, lstRuleResources);
            }
            else {
                lstRuleResources = mpPackages.get(resourcePackagePath);
            }
            lstRuleResources.add(resourceRule);
        }
        
        try {
            for (java.util.Map.Entry<String, List<File>> entry : mpPackages.entrySet())
            {
                String packageSlashPath = entry.getKey();
                String packageDotPath = entry.getKey().replace(File.separatorChar, '.');
                client.replacePath("/" + packageDotPath).delete();
                
                Map<String, String> mpBody = new HashMap<String, String>();
                mpBody.put("title", packageDotPath);
                mpBody.put("description", packageDotPath);
                Response responseCreatePackage = client.replacePath("/").type(MediaType.APPLICATION_JSON).post(mpBody);
                //InputStream in = (InputStream) responseCreatePackage.getEntity();
                //System.out.println(IOUtils.toString(in));
                Assert.assertTrue(responseCreatePackage.getStatus() == 200);
                
                //upload model files
                for (File modelFile : lstModelResources) {
                    //System.out.println("model file:" + file.getAbsolutePath());
                    Response responsePostModel = client.replacePath("/" + packageDotPath + "/assets")
                            .type(MediaType.APPLICATION_OCTET_STREAM)
                            .replaceHeader("slug", modelFile.getName())
                            .post(modelFile);
                    Assert.assertTrue(responsePostModel.getStatus() == 200);
                }
                //upload drool files
                for (File resource : entry.getValue()) {
                    if (resource.getName().endsWith("model") || resource.getName().endsWith("function") ||
                            resource.getName().endsWith("drt") || resource.getName().endsWith("data-rules.xls")) {
                        client.type(MediaType.MULTIPART_FORM_DATA);
                        List<Attachment> atts = new ArrayList<Attachment>();
                        
                        ContentDisposition cdBinary = new ContentDisposition("form-data;name=\"binary\";filename=" + resource.getName());
                        MultivaluedMap<String, String> headersBinary = new MetadataMap<String, String>();
                        headersBinary.putSingle("Content-Disposition", cdBinary.toString());
                        headersBinary.putSingle("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
                        
                        Map<String, Object> mpAsset = new HashMap<String, Object>();
                        Map<String, String> mpMetadataAsset = new HashMap<String, String>();
                        mpAsset.put("title", resource.getName());
                        mpAsset.put("description", resource.getName());
                        mpAsset.put("binaryContentAttachmentFileName", resource.getName());
                        mpAsset.put("metadata", mpMetadataAsset);
                        if (resource.getName().endsWith("model")) {
                            atts.add(new Attachment(new FileInputStream(resource), headersBinary));
                            mpMetadataAsset.put("format", "model.drl");//constante
                        }
                        else if (resource.getName().endsWith("function")) {
                            atts.add(new Attachment(new FileInputStream(resource), headersBinary));
                            mpMetadataAsset.put("format", "function");//constante
                        }
                        else if (resource.getName().endsWith("drt")) {
                            //importing template into guvnor by drt file doesn't work
                            //you have to create the xml file from org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel class
                            //and the header has to be org.drools.guvnor.client.modeldriven.dt.TemplateModel
                            //in conclution, guvnor doesn't support importation template rules.
                            //one solution is to create a listener class when compiling the drt together with source file of rules and then...
                            //other solution is to generate the drl file combining the drt file and the spreadsheet
                            try {
                                InputStream ruleStream = new FileInputStream(resource);
                                String dataRulesExcelFilename = resource.getName().substring(0, resource.getName().lastIndexOf('.')) + ".data-rules.xls";
                                InputStream excelStream = new FileInputStream(new File(outputDirectory, packageSlashPath + "/" + dataRulesExcelFilename));
                                ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
                                String generatedDrl = converter.compile(excelStream, ruleStream, 2, 2);
                                
                                atts.add(new Attachment(new ByteArrayInputStream(generatedDrl.getBytes()), headersBinary));
                                mpMetadataAsset.put("format", "drl");//constante
                            }
                            catch (Exception objEx) {
                                getLog().info("Skipping " + resource.getName() + " because " + objEx.getMessage());
                                continue;
                            }
                        }
                        else if (resource.getName().endsWith("data-rules.xls")) {
                            //mpMetadataAsset.put("format", "bin");//constante
                            continue;
                        }
                        else {
                            atts.add(new Attachment(new FileInputStream(resource), headersBinary));
                        }
                        
                        ContentDisposition cdAsset = new ContentDisposition("form-data;name=\"asset\"");
                        MultivaluedMap<String, String> headersAsset = new MetadataMap<String, String>();
                        headersAsset.putSingle("Content-Disposition", cdAsset.toString());
                        headersAsset.putSingle("Content-Type", MediaType.APPLICATION_JSON);
                        atts.add(new Attachment(headersAsset, mpAsset));
                        final MultipartBody multiPart = new MultipartBody(atts);
                        
                        client.replacePath("/" + packageDotPath + "/assets");
                        Response responsePostAsset = client.post(multiPart);
                        Assert.assertTrue(responsePostAsset.getStatus() == 200);
                    }
                    else {
                        Response responsePostDroolFile = client.replacePath("/" + packageDotPath + "/assets")
                                .type(MediaType.APPLICATION_OCTET_STREAM)
                                .replaceHeader("slug", resource.getName().replace('.', '-') + resource.getName().substring(resource.getName().indexOf('.')))
                                .post(resource);
                        Assert.assertTrue(responsePostDroolFile.getStatus() == 200);
                    }
                }
                //create a snapshot
                Response responseCreateSnapshot = client.replacePath("/" + packageDotPath + "/snapshot" + "/" + packageDotPath).type(MediaType.APPLICATION_JSON).post("");
                Assert.assertTrue(responseCreateSnapshot.getStatus() == 204);//no content
            }
        }
        catch (Exception objEx) {
            throw new MojoFailureException("", objEx);
        }

    }
    
    protected void unpack( Artifact artifact, String type, File location, String includes, String excludes )
            throws MojoExecutionException
    {
        File file = artifact.getFile(); 
        
        try
        {
            if (location.exists()) {
                location.delete();
            }
            location.mkdirs();

            if ( file.isDirectory() )
            {
                // usual case is a future jar packaging, but there are special cases: classifier and other packaging
                throw new MojoExecutionException( "Artifact has not been packaged yet. When used on reactor artifact, "
                    + "unpack should be executed after packaging: see MDEP-98." );
            }

            UnArchiver unArchiver = archiverManager.getUnArchiver(file);
            
            unArchiver.setSourceFile( file );

            unArchiver.setDestDirectory( location );

            if ( StringUtils.isNotEmpty( excludes ) || StringUtils.isNotEmpty( includes ) )
            {
                // Create the selectors that will filter
                // based on include/exclude parameters
                // MDEP-47
                IncludeExcludeFileSelector[] selectors =
                    new IncludeExcludeFileSelector[]{ new IncludeExcludeFileSelector() };

                if ( StringUtils.isNotEmpty( excludes ) )
                {
                    selectors[0].setExcludes( excludes.split( "," ) );
                }

                if ( StringUtils.isNotEmpty( includes ) )
                {
                    selectors[0].setIncludes( includes.split( "," ) );
                }

                unArchiver.setFileSelectors( selectors );
            }
            
            unArchiver.extract();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Unknown archiver type", e );
        }

        
    }
    
    /**
     * @return Returns a comma separated list of excluded items
     */
    public String getExcludes()
    {
        return this.excludes;
    }

    /**
     * @param excludes A comma separated list of items to exclude i.e. **\/*.xml, **\/*.properties
     */
    public void setExcludes( String excludes )
    {
        this.excludes = excludes;
    }

    /**
     * @return Returns a comma separated list of included items
     */
    public String getIncludes()
    {
        return this.includes;
    }

    /**
     * @param includes A comma separated list of items to include i.e. **\/*.xml, **\/*.properties
     */
    public void setIncludes( String includes )
    {
        this.includes = includes;
    }
    
    protected List<ArtifactItem> getProcessedArtifactItems(List<ArtifactItem> artifactItems )
            throws MojoExecutionException
        {
            List<ArtifactItem> items =
                super.getProcessedArtifactItems(artifactItems );
            for ( ArtifactItem artifactItem : items )
            {
                if ( StringUtils.isEmpty( artifactItem.getIncludes() ) )
                {
                    artifactItem.setIncludes( getIncludes() );
                }
                if ( StringUtils.isEmpty( artifactItem.getExcludes() ) )
                {
                    artifactItem.setExcludes( getExcludes() );
                }
            }
            return items;
        }

    /**
     * @return the archiverManager
     */
    public ArchiverManager getArchiverManager() {
        return archiverManager;
    }

    /**
     * @param archiverManager the archiverManager to set
     */
    public void setArchiverManager(ArchiverManager archiverManager) {
        this.archiverManager = archiverManager;
    }
    

    private WebClient doRequestV2(String strWebAppUrl, String strUser, String strPass) {
        WebClient client = WebClient.create(strWebAppUrl, Arrays.asList(new JacksonJaxbJsonProvider()), Arrays.asList(new LoggingFeature()), null);
        client
            .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_XML)
            .header("Authorization",
                "Basic " + DatatypeConverter.printBase64Binary(((strUser + ":" + strPass).getBytes())));
        
        return client;
    }
}
