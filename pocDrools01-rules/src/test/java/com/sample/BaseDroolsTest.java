package com.sample;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.io.ResourceFactory;
import org.drools.util.codec.Base64;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

/**
 * @author mlescano
 *
 * @see https://developer.jboss.org/wiki/AtomPubinterfaceforGuvnor
 * @see https://simplesassim.wordpress.com/2013/08/04/how-to-create-a-package-snapshot-with-the-drools-guvnor-rest-api/
 * 
 * /packages/{packageName}/binary
    http://localhost:8080/guvnor-5.5.0.Final/rest/packages/{packageName}/binary
    The GET method produces MIME-Types:
        application/octet-stream
    The GET method returns the compiled binary of the package {packageName} as a binary stream. If the package has not been compiled yet or its binary is not up to date, this will compile the package first.
 *
 */
public class BaseDroolsTest {

    @Test
    public void listAllPackages() throws IOException {
        Sardine sardine = SardineFactory.begin("guvnor", "123456");
        List<DavResource> resources = sardine.list("http://chq02-i2bpm01-dev2.cngfinancial.com:8080/drools-guvnor/org.drools.guvnor.Guvnor/webdav/packages/axcessfinancial.datareporting.cais");//
        for (DavResource res : resources) {
            System.out.println("folder: " + res.getDisplayName());
        }
        //cannot list the packages which contain excel rules exported into guvnor
    }
    
    @Test
    public void testGetPackagesForAtom() throws MalformedURLException, IOException {
        Abdera abdera = new Abdera();
        InputStream in = doRequest("http://chq02-i2bpm01-dev2.cngfinancial.com:8080/drools-guvnor/rest/packages", MediaType.APPLICATION_ATOM_XML);
        Document<Feed> doc = abdera.getParser().parse(in);
        Feed feed = doc.getRoot();
        System.out.println("BaseUriPath: " + feed.getBaseUri().getPath());
        System.out.println("Base Title: " + feed.getTitle());
        Iterator<Entry> it = feed.getEntries().iterator();

        while (it.hasNext()) {
            Entry entry = it.next();
            System.out.println("Title: " + entry.getTitle());
            List<Link> links = entry.getLinks();
            System.out.println("Href: " + links.get(0).getHref().getPath());
        }
    }
    
    @Test
    public void testGetPackagesForJson() throws MalformedURLException, IOException {
        InputStream in = doRequest("http://chq02-i2bpm01-dev2.cngfinancial.com:8080/drools-guvnor/rest/packages", MediaType.APPLICATION_JSON);
        System.out.println(IOUtils.toString(in));
    }
    
    @Test
    public void testGetPackagesForJsonV2() throws MalformedURLException, IOException {
        WebClient client = doRequestV2("http://chq02-i2bpm01-dev2.cngfinancial.com:8080/drools-guvnor/rest/packages", MediaType.APPLICATION_JSON);
        
        /*Response response = client.get();
        InputStream in = (InputStream) response.getEntity();
        System.out.println(IOUtils.toString(in));
        */
        
        System.out.println(client.get(String.class));
    }

    @Test
    public void uploadCompleteAssets() throws Throwable {
        WebClient client = doRequestV2("http://chq02-i2bpm01-dev2.cngfinancial.com:8080/drools-guvnor/rest/packages/", MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_XML);
        //delete package
        //ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        
        String rootPackagePath = "com/sample";
        
        Resource[] resources = resolver.getResources("classpath*:" + rootPackagePath + "/**/*.*");
        
        Map<String, List<Resource>> mpPackages = new HashMap<String, List<Resource>>(); 
        for (Resource resource : resources) {
            if (!resource.getFilename().endsWith("class") && resource instanceof FileSystemResource) {
                List<Resource> lstResources;
                FileSystemResource clResource = (FileSystemResource) resource;
                //System.out.println("path: " + clResource.getPath());
                //System.out.println("root dir path: " + clResource.getPath().substring(0, clResource.getPath().lastIndexOf(rootPackagePath)));
                String resourceAbsolutePath = clResource.getPath();
                //String rootDirPath = resourceAbsolutePath.substring(0, resourceAbsolutePath.lastIndexOf(rootPackagePath));
                String resourcePackagePath = resourceAbsolutePath.substring(resourceAbsolutePath.indexOf(rootPackagePath), resourceAbsolutePath.indexOf(clResource.getFilename()) - 1);
                System.out.println("package path: " + resourcePackagePath);
                
                if (!mpPackages.containsKey(resourcePackagePath)) {
                    lstResources = new ArrayList<Resource>();
                    mpPackages.put(resourcePackagePath, lstResources);
                }
                else {
                    lstResources = mpPackages.get(resourcePackagePath);
                }
                lstResources.add(clResource);
            }
        }
        //System.out.println("lstResources: " + mpPackages);
        File dirModelLibs = new File("target/models");
        
        File[] models = dirModelLibs.listFiles();
        
        for (java.util.Map.Entry<String, List<Resource>> entry : mpPackages.entrySet()) {
            String packageSlashPath = entry.getKey();
            String packageDotPath = entry.getKey().replace('/', '.');
            client.replacePath("/" + packageDotPath).delete();
            
            Map<String, String> mpBody = new HashMap<String, String>();
            mpBody.put("title", packageDotPath);
            mpBody.put("description", packageDotPath);
            Response responseCreatePackage = client.replacePath("/").type(MediaType.APPLICATION_JSON).post(mpBody);
            //InputStream in = (InputStream) responseCreatePackage.getEntity();
            //System.out.println(IOUtils.toString(in));
            Assert.assertTrue(responseCreatePackage.getStatus() == 200);
            if (responseCreatePackage.getStatus() == 200) {
                //upload model files
                for (File file : models) {
                    //System.out.println("model file:" + file.getAbsolutePath());
                    Response responsePostModel = client.replacePath("/" + packageDotPath + "/assets")
                            .type(MediaType.APPLICATION_OCTET_STREAM)
                            .replaceHeader("slug", file.getName())
                            .post(file);
                    Assert.assertTrue(responsePostModel.getStatus() == 200);
                }
                //upload drool files
                for (Resource resource : entry.getValue()) {
                    System.out.println("drool file:" + resource.getFilename());
                    if (resource.getFilename().endsWith("model") || resource.getFilename().endsWith("function") ||
                            resource.getFilename().endsWith("drt") || resource.getFilename().endsWith("data-rules.xls")) {
                        client.type(MediaType.MULTIPART_FORM_DATA);
                        List<Attachment> atts = new ArrayList<Attachment>();
                        
                        ContentDisposition cdBinary = new ContentDisposition("form-data;name=\"binary\";filename=" + resource.getFilename());
                        MultivaluedMap<String, String> headersBinary = new MetadataMap<String, String>();
                        headersBinary.putSingle("Content-Disposition", cdBinary.toString());
                        headersBinary.putSingle("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
                        
                        Map<String, Object> mpAsset = new HashMap<String, Object>();
                        Map<String, String> mpMetadataAsset = new HashMap<String, String>();
                        mpAsset.put("title", resource.getFilename());
                        mpAsset.put("description", resource.getFilename());
                        mpAsset.put("binaryContentAttachmentFileName", resource.getFilename());
                        mpAsset.put("metadata", mpMetadataAsset);
                        if (resource.getFilename().endsWith("model")) {
                            atts.add(new Attachment(resource.getInputStream(), headersBinary));
                            mpMetadataAsset.put("format", "model.drl");//constante
                        }
                        else if (resource.getFilename().endsWith("function")) {
                            atts.add(new Attachment(resource.getInputStream(), headersBinary));
                            mpMetadataAsset.put("format", "function");//constante
                        }
                        else if (resource.getFilename().endsWith("drt")) {
                            //importing template into guvnor by drt file doesn't work
                            //you have to create the xml file from org.drools.ide.common.client.modeldriven.brl.templates.TemplateModel class
                            //and the header has to be org.drools.guvnor.client.modeldriven.dt.TemplateModel
                            //in conclution, guvnor doesn't support importation template rules.
                            //one solution is to create a listener class when compiling the drt together with source file of rules and then...
                            //other solution is to generate the drl file combining the drt file and the spreadsheet
                            try {
                                InputStream ruleStream = ResourceFactory.newClassPathResource(packageSlashPath + "/" + resource.getFilename()).getInputStream();
                                String dataRulesExcelFilename = resource.getFilename().substring(0, resource.getFilename().lastIndexOf('.')) + ".data-rules.xls";
                                InputStream excelStream = ResourceFactory.newClassPathResource(packageSlashPath + "/" + dataRulesExcelFilename).getInputStream();
                                ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
                                String generatedDrl = converter.compile(excelStream, ruleStream, 2, 2);
                                
                                atts.add(new Attachment(new ByteArrayInputStream(generatedDrl.getBytes()), headersBinary));
                                mpMetadataAsset.put("format", "drl");//constante
                            }
                            catch (Exception objEx) {
                                System.out.println("Skipping " + resource.getFilename() + " because " + objEx.getMessage());
                                continue;
                            }
                        }
                        else if (resource.getFilename().endsWith("data-rules.xls")) {
                            //mpMetadataAsset.put("format", "bin");//constante
                            continue;
                        }
                        else {
                            atts.add(new Attachment(resource.getInputStream(), headersBinary));
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
                                .replaceHeader("slug", resource.getFilename().replace('.', '-') + resource.getFilename().substring(resource.getFilename().indexOf('.')))
                                .post(resource.getFile());
                        Assert.assertTrue(responsePostDroolFile.getStatus() == 200);
                    }
                }
                
                //create a snapshot
                Response responseCreateSnapshot = client.replacePath("/" + packageDotPath + "/snapshot" + "/" + packageDotPath).type(MediaType.APPLICATION_JSON).post("");
                Assert.assertTrue(responseCreateSnapshot.getStatus() == 204);//no content
            }
        }
    }
    
    @Test
    public void uploadSimpleAssets() throws Throwable {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        WebClient client = doRequestV2("http://chq02-i2bpm01-dev2.cngfinancial.com:8080/drools-guvnor/rest/packages/", MediaType.APPLICATION_JSON, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_XML);
        client.type(MediaType.MULTIPART_FORM_DATA);
        List<Attachment> atts = new ArrayList<Attachment>();
        Resource resourceModel = resolver.getResource("com/sample/Declarative01.model");
        ContentDisposition cdBinary = new ContentDisposition("form-data;name=\"binary\";filename=" + resourceModel.getFilename());
        MultivaluedMap<String, String> headersBinary = new MetadataMap<String, String>();
        headersBinary.putSingle("Content-Disposition", cdBinary.toString());
        headersBinary.putSingle("Content-Type", MediaType.APPLICATION_OCTET_STREAM);
        atts.add(new Attachment(resourceModel.getInputStream(), headersBinary));
        
        Map<String, Object> mpAsset = new HashMap<String, Object>();
        Map<String, String> mpMetadataAsset = new HashMap<String, String>();
        mpAsset.put("title", resourceModel.getFilename());
        mpAsset.put("description", resourceModel.getFilename());
        mpAsset.put("binaryContentAttachmentFileName", resourceModel.getFilename());
        mpAsset.put("metadata", mpMetadataAsset);
        mpMetadataAsset.put("format", "model.drl");
        
        ContentDisposition cdAsset = new ContentDisposition("form-data;name=\"asset\"");
        MultivaluedMap<String, String> headersAsset = new MetadataMap<String, String>();
        headersAsset.putSingle("Content-Disposition", cdAsset.toString());
        headersAsset.putSingle("Content-Type", MediaType.APPLICATION_JSON);
        atts.add(new Attachment(headersAsset, mpAsset));
        final MultipartBody multiPart = new MultipartBody(atts);
        
        client.replacePath("/com.sample/assets");
        Response responsePostAsset = client.post(multiPart);
    }
    
    private WebClient doRequestV2(String strUrl, String... mediaType) {
        WebClient client = WebClient.create(strUrl, Arrays.asList(new JacksonJaxbJsonProvider()), Arrays.asList(new LoggingFeature()), null);
        client
            .accept(mediaType)
            .header("Authorization",
                "Basic " + new Base64().encodeToString(( "guvnor:123456".getBytes())));
        
        return client;
    }

    private InputStream doRequest(String strUrl, String mediaType) throws MalformedURLException, IOException,
            ProtocolException {
        URL url = new URL(strUrl);

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        connection.setRequestProperty("Authorization",
                "Basic " + new Base64().encodeToString(( "guvnor:123456".getBytes() )));
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", mediaType);
        connection.connect();

        //System.out.println(IOUtils.toString(connection.getInputStream()));

        InputStream in = connection.getInputStream();
        return in;
    }
}