package com.axcessfinancial.drools.maven.plugin;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author mlescano
 *
 */
public class MavenDroolsCompiler extends AbstractDroolsCompiler {
    
    private ClassLoader classLoader;
    
    public MavenDroolsCompiler(String binFormat, String builderType, ClassLoader classLoader) {
        this.binFormat = binFormat;
        this.builderType = builderType;
        this.classLoader = classLoader;
    }
    

    @Override
    ClassLoader getLoader() {
        if (classLoader == null) {
            ClassLoader pluginClassLoader = getClass().getClassLoader();
            //ClassLoader parentClassLoader = pluginClassLoader.getParent();
            return pluginClassLoader;
        }
        return classLoader;

    }

    @Override
    File getFile(String srcdir, String packageName, String filename) {
        File ruleFile = null;
        if (!"".equals(packageName)) {
            String packageSlashPath = packageName.replace('.', File.separatorChar);
            ruleFile = new File(srcdir + File.separatorChar + packageSlashPath + File.separatorChar + filename);
        }
        else {
            ruleFile = new File(srcdir + File.separatorChar + filename);
        }

        return ruleFile;
    }

    @Override
    String[] getFileList(String srcdir, String packageName) {
        String packageSlashPath = packageName.replace('.', File.separatorChar);
        File packageDir = new File(srcdir + File.separatorChar + packageSlashPath);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                File file = new File(dir, name);
                return file.isFile() && !file.isDirectory();
            }
        };
        return packageDir.list(filter);
    }

    @Override
    File getDestFile(String packageName, String destdir) {
        File destDirFile = new File(destdir);
        if (!destDirFile.exists()) {
            destDirFile.mkdir();
        }
        if ("".equals(packageName)) {
            packageName = "root";
        }
        File packageFile = new File(destDirFile, packageName);
        return packageFile;
    }

}
