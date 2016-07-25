package com.axcessfinancial.drools.maven.plugin;

/**
 * @author mlescano
 *
 */
public class RemoteDeployHost {

    private String urlHostPath;
    
    private String user;
    
    private String pass;
    
    public RemoteDeployHost() {
        
    }

    /**
     * @return the urlHostPath
     */
    public String getUrlHostPath() {
        return urlHostPath;
    }

    /**
     * @param urlHostPath the urlHostPath to set
     */
    public void setUrlHostPath(String urlHostPath) {
        this.urlHostPath = urlHostPath;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the pass
     */
    public String getPass() {
        return pass;
    }

    /**
     * @param pass the pass to set
     */
    public void setPass(String pass) {
        this.pass = pass;
    }
    
    
}
