package com.axcessfinancial.drools.maven.plugin;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.shared.dependency.DependencyCoordinate;
import org.codehaus.plexus.util.StringUtils;

public class ArtifactItem implements DependencyCoordinate {

    /**
     * Group Id of Artifact
     *
     * @parameter
     * @required
     */
    private String groupId;

    /**
     * Name of Artifact
     *
     * @parameter
     * @required
     */
    private String artifactId;

    /**
     * Version of Artifact
     *
     * @parameter
     */
    private String version = null;

    /**
     * Type of Artifact (War,Jar,etc)
     *
     * @parameter
     * @required
     */
    private String type = "jar";

    /**
     * Classifier for Artifact (tests,sources,etc)
     *
     * @parameter
     */
    private String classifier;
    
    /**
     * Artifact Item
     */
    private Artifact artifact;
    
    /**
     * A comma separated list of file patterns to include when unpacking the
     * artifact.
     */
    private String includes;

    /**
     * A comma separated list of file patterns to exclude when unpacking the
     * artifact.
     */
    private String excludes;
    
    /**
     * Location to use for this Artifact. Overrides default location.
     *
     * @parameter
     */
    private File outputDirectory;
    
    public ArtifactItem()
    {
        // default constructor
    }

    public ArtifactItem( Artifact artifact )
    {
        this.setArtifact( artifact );
        this.setArtifactId( artifact.getArtifactId() );
        this.setClassifier( artifact.getClassifier() );
        this.setGroupId( artifact.getGroupId() );
        this.setType( artifact.getType() );
        this.setVersion( artifact.getVersion() );
    }
    
    private String filterEmptyString( String in )
    {
        if ( "".equals( in ) )
        {
            return null;
        }
        return in;
    }

    /**
     * @return Returns the artifactId.
     */
    public String getArtifactId()
    {
        return artifactId;
    }

    /**
     * @param artifactId
     *            The artifactId to set.
     */
    public void setArtifactId( String artifact )
    {
        this.artifactId = filterEmptyString( artifact );
    }

    /**
     * @return Returns the groupId.
     */
    public String getGroupId()
    {
        return groupId;
    }

    /**
     * @param groupId
     *            The groupId to set.
     */
    public void setGroupId( String groupId )
    {
        this.groupId = filterEmptyString( groupId );
    }

    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType( String type )
    {
        this.type = filterEmptyString( type );
    }

    /**
     * @return Returns the version.
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version
     *            The version to set.
     */
    public void setVersion( String version )
    {
        this.version = filterEmptyString( version );
    }

    /**
     * @return Returns the base version.
     */
    public String getBaseVersion()
    {
        return ArtifactUtils.toSnapshotVersion( version );
    }

    /**
     * @return Classifier.
     */
    public String getClassifier()
    {
        return classifier;
    }

    /**
     * @param classifier
     *            Classifier.
     */
    public void setClassifier( String classifier )
    {
        this.classifier = filterEmptyString( classifier );
    }

    @Override
    public String toString()
    {
        if ( this.classifier == null )
        {
            return groupId + ":" + artifactId + ":" + StringUtils.defaultString( version, "?" ) + ":" + type;
        }
        else
        {
            return groupId + ":" + artifactId + ":" + classifier + ":" + StringUtils.defaultString( version, "?" )
                + ":" + type;
        }
    }
    
    /**
     * @return Returns the artifact.
     */
    public Artifact getArtifact()
    {
        return this.artifact;
    }

    /**
     * @param artifact
     *            The artifact to set.
     */
    public void setArtifact( Artifact artifact )
    {
        this.artifact = artifact;
    }
    
    /**
     * @return Returns a comma separated list of excluded items
     */
    public String getExcludes()
    {
        return DependencyUtil.cleanToBeTokenizedString( this.excludes );
    }

    /**
     * @param excludes A comma separated list of items to exclude i.e. <code>**\/*.xml, **\/*.properties</code>
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
        return DependencyUtil.cleanToBeTokenizedString( this.includes );
    }

    /**
     * @param includes A comma separated list of items to include i.e. <code>**\/*.xml, **\/*.properties</code>
     */
    public void setIncludes( String includes )
    {
        this.includes = includes;
    }
    
    /**
     * @return Returns the location.
     */
    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * @param location
     *            The location to set.
     */
    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }
}
