package com.axcessfinancial.drools.maven.plugin;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.DefaultArtifactCoordinate;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;
import org.apache.maven.shared.repository.RepositoryManager;
import org.codehaus.plexus.util.StringUtils;

public abstract class AbstractDependencyMojo extends AbstractMojo {

    /**
     * To look up Archiver/UnArchiver implementations
     */
    /*@Component
    private ArchiverManager archiverManager;*/
    
    /**
     * POM
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;
    
    /**
     * The Maven session
     */
    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    protected MavenSession session;
    
    @Component
    private ArtifactResolver artifactResolver;
    
    @Component
    private RepositoryManager repositoryManager;
    
    @Component
    private ArtifactHandlerManager artifactHandlerManager;
    
    /**
     * Default output location used for mojo, unless overridden in ArtifactItem.
     *
     * @since 1.0
     */
    @Parameter( property = "guvnor.deployer.outputDirectory", defaultValue = "${project.build.directory}/rules" )
    protected File outputDirectory;
    
    /*
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public final void execute()
        throws MojoExecutionException, MojoFailureException
    {
        doExecute();
    }
    
    protected abstract void doExecute()
            throws MojoExecutionException, MojoFailureException;

    /**
     * @return Returns the archiverManager.
     */
    /*public ArchiverManager getArchiverManager()
    {
        return this.archiverManager;
    }*/
    
    /**
     * @return Returns the project.
     */
    public MavenProject getProject()
    {
        return this.project;
    }

    /**
     * @param archiverManager The archiverManager to set.
     */
    /*public void setArchiverManager( ArchiverManager archiverManager )
    {
        this.archiverManager = archiverManager;
    }*/
    
    // artifactItems is filled by either field injection or by setArtifact()
    protected void verifyRequirements(List<ArtifactItem> artifactItems) throws MojoFailureException
    {
        if ( artifactItems == null || artifactItems.isEmpty() )
        {
            throw new MojoFailureException( "Either artifact or artifactItems is required " );
        }
    }
    
    /**
     * Preprocesses the list of ArtifactItems. This method defaults the outputDirectory if not set and creates the
     * output Directory if it doesn't exist.
     *
     * @param removeVersion remove the version from the filename.
     * @param prependGroupId prepend the groupId to the filename.
     * @param useBaseVersion use the baseVersion of the artifact instead of version for the filename.
     * @return An ArrayList of preprocessed ArtifactItems
     * @throws MojoExecutionException with a message if an error occurs.
     * @see ArtifactItem
     */
    protected List<ArtifactItem> getProcessedArtifactItems(List<ArtifactItem> artifactItems  )
        throws MojoExecutionException
    {
        if ( artifactItems == null || artifactItems.size() < 1 )
        {
            throw new MojoExecutionException( "There are no artifactItems configured." );
        }

        for ( ArtifactItem artifactItem : artifactItems )
        {
            this.getLog().info( "Configured Artifact: " + artifactItem.toString() );

            if ( artifactItem.getOutputDirectory() == null )
            {
                artifactItem.setOutputDirectory( this.outputDirectory );
            }
            artifactItem.getOutputDirectory().mkdirs();
            
            // make sure we have a version.
            if ( StringUtils.isEmpty( artifactItem.getVersion() ) )
            {
                fillMissingArtifactVersion( artifactItem );
            }

            artifactItem.setArtifact( this.getArtifact( artifactItem ) );

        }
        return artifactItems;
    }


    /**
     * Resolves the Artifact from the remote repository if necessary. If no version is specified, it will be retrieved
     * from the dependency list or from the DependencyManagement section of the pom.
     *
     * @param artifactItem containing information about artifact from plugin configuration.
     * @return Artifact object representing the specified file.
     * @throws MojoExecutionException with a message if the version can't be found in DependencyManagement.
     */
    protected Artifact getArtifact( ArtifactItem artifactItem )
        throws MojoExecutionException
    {
        Artifact artifact;

        try
        {
            // mdep-50 - rolledback for now because it's breaking some functionality.
            /*
             * List listeners = new ArrayList(); Set theSet = new HashSet(); theSet.add( artifact );
             * ArtifactResolutionResult artifactResolutionResult = artifactCollector.collect( theSet, project
             * .getArtifact(), managedVersions, this.local, project.getRemoteArtifactRepositories(),
             * artifactMetadataSource, null, listeners ); Iterator iter =
             * artifactResolutionResult.getArtifactResolutionNodes().iterator(); while ( iter.hasNext() ) {
             * ResolutionNode node = (ResolutionNode) iter.next(); artifact = node.getArtifact(); }
             */
            
            ProjectBuildingRequest buildingRequest;
            
            buildingRequest = new DefaultProjectBuildingRequest( session.getProjectBuildingRequest() );
            
            // Map dependency to artifact coordinate
            DefaultArtifactCoordinate coordinate = new DefaultArtifactCoordinate();
            coordinate.setGroupId( artifactItem.getGroupId() );
            coordinate.setArtifactId( artifactItem.getArtifactId() );
            coordinate.setVersion( artifactItem.getVersion() );
            coordinate.setClassifier( artifactItem.getClassifier() );
            
            final String extension;
            ArtifactHandler artifactHandler = artifactHandlerManager.getArtifactHandler( artifactItem.getType() );
            if ( artifactHandler != null )
            {
                extension = artifactHandler.getExtension();
            }
            else
            {
                extension = artifactItem.getType();
            }
            coordinate.setExtension( extension );
            
            artifact = artifactResolver.resolveArtifact( buildingRequest, coordinate ).getArtifact();
        }
        catch ( ArtifactResolverException e )
        {
            throw new MojoExecutionException( "Unable to find/resolve artifact.", e );
        }

        return artifact;
    }

    /**
     * Tries to find missing version from dependency list and dependency management. If found, the artifact is updated
     * with the correct version. It will first look for an exact match on artifactId/groupId/classifier/type and if it
     * doesn't find a match, it will try again looking for artifactId and groupId only.
     *
     * @param artifact representing configured file.
     * @throws MojoExecutionException
     */
    private void fillMissingArtifactVersion( ArtifactItem artifact )
        throws MojoExecutionException
    {
        MavenProject project = getProject();
        List<Dependency> deps = project.getDependencies();
        List<Dependency> depMngt = project.getDependencyManagement() == null
            ? Collections.<Dependency>emptyList()
            : project.getDependencyManagement().getDependencies();

        if ( !findDependencyVersion( artifact, deps, false )
            && ( project.getDependencyManagement() == null || !findDependencyVersion( artifact, depMngt, false ) )
            && !findDependencyVersion( artifact, deps, true )
            && ( project.getDependencyManagement() == null || !findDependencyVersion( artifact, depMngt, true ) ) )
        {
            throw new MojoExecutionException(
                "Unable to find artifact version of " + artifact.getGroupId() + ":" + artifact.getArtifactId()
                    + " in either dependency list or in project's dependency management." );
        }
    }

    /**
     * Tries to find missing version from a list of dependencies. If found, the artifact is updated with the correct
     * version.
     *
     * @param artifact     representing configured file.
     * @param dependencies list of dependencies to search.
     * @param looseMatch   only look at artifactId and groupId
     * @return the found dependency
     */
    private boolean findDependencyVersion( ArtifactItem artifact, List<Dependency> dependencies, boolean looseMatch )
    {
        for ( Dependency dependency : dependencies )
        {
            if ( StringUtils.equals( dependency.getArtifactId(), artifact.getArtifactId() )
                && StringUtils.equals( dependency.getGroupId(), artifact.getGroupId() )
                && ( looseMatch || StringUtils.equals( dependency.getClassifier(), artifact.getClassifier() ) )
                && ( looseMatch || StringUtils.equals( dependency.getType(), artifact.getType() ) ) )
            {
                artifact.setVersion( dependency.getVersion() );

                return true;
            }
        }

        return false;
    }

}
