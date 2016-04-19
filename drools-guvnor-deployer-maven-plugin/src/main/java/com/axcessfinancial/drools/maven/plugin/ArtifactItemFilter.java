package com.axcessfinancial.drools.maven.plugin;

import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;

public interface ArtifactItemFilter {

    boolean isArtifactIncluded( ArtifactItem item )
            throws ArtifactFilterException;
}
