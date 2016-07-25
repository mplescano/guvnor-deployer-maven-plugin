package org.boretti.drools.integration.drools5;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Display help information on drools5-integration-helper-maven-plugin.<br/> Call <pre>  mvn drools5:help -Ddetail=true -Dgoal=&lt;goal-name&gt;</pre> to display parameter details.
 *
 * @version generated on Wed Dec 02 22:03:25 CET 2009
 * @author org.apache.maven.tools.plugin.generator.PluginHelpGenerator (version 2.5.1)
 * @goal help
 * @requiresProject false
 */
public class HelpMojo
    extends AbstractMojo
{
    /**
     * If <code>true</code>, display all settable properties for each goal.
     * 
     * @parameter expression="${detail}" default-value="false"
     */
    private boolean detail;

    /**
     * The name of the goal for which to show help. If unspecified, all goals will be displayed.
     * 
     * @parameter expression="${goal}"
     */
    private java.lang.String goal;

    /**
     * The maximum length of a display line, should be positive.
     * 
     * @parameter expression="${lineLength}" default-value="80"
     */
    private int lineLength;

    /**
     * The number of spaces per indentation level, should be positive.
     * 
     * @parameter expression="${indentSize}" default-value="2"
     */
    private int indentSize;


    /** {@inheritDoc} */
    public void execute()
        throws MojoExecutionException
    {
        if ( lineLength <= 0 )
        {
            getLog().warn( "The parameter 'lineLength' should be positive, using '80' as default." );
            lineLength = 80;
        }
        if ( indentSize <= 0 )
        {
            getLog().warn( "The parameter 'indentSize' should be positive, using '2' as default." );
            indentSize = 2;
        }

        StringBuffer sb = new StringBuffer();

        append( sb, "org.boretti.drools.integration:drools5-integration-helper-maven-plugin:1.3.0", 0 );
        append( sb, "", 0 );

        append( sb, "Drools 5 Integration Helper Maven Plugin", 0 );
        append( sb, "This is the Maven 2 plugin to support Drools 5 integration. This plugin provides compilation of the drools file, validation of the drools file and also code to instrumentalize the class files.", 1 );
        append( sb, "", 0 );

        if ( goal == null || goal.length() <= 0 )
        {
            append( sb, "This plugin has 8 goals:", 0 );
            append( sb, "", 0 );
        }

        if ( goal == null || goal.length() <= 0 || "drools-compile".equals( goal ) )
        {
            append( sb, "drools5:drools-compile", 0 );
            append( sb, "This goal compile drools.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "compiledExtension", 2 );
                append( sb, "The default extension for compiled drools file", 3 );
                append( sb, "", 0 );

                append( sb, "extension", 2 );
                append( sb, "The default extension for drools file", 3 );
                append( sb, "", 0 );

                append( sb, "inputDirectory", 2 );
                append( sb, "The input directory from where to copy the rules.", 3 );
                append( sb, "", 0 );

                append( sb, "outputDirectory", 2 );
                append( sb, "The output directory into which to copy the rules.", 3 );
                append( sb, "", 0 );

                append( sb, "reportDirectory", 2 );
                append( sb, "The output directory into which to write report file.", 3 );
                append( sb, "", 0 );

                append( sb, "reportFile", 2 );
                append( sb, "The output file name for the report file.", 3 );
                append( sb, "", 0 );

                append( sb, "xmlExtension", 2 );
                append( sb, "The default extension for xml drools file", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "drools-compile-test".equals( goal ) )
        {
            append( sb, "drools5:drools-compile-test", 0 );
            append( sb, "This goal compile drools for test.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "compiledExtension", 2 );
                append( sb, "The default extension for compiled drools file", 3 );
                append( sb, "", 0 );

                append( sb, "extension", 2 );
                append( sb, "The default extension for drools file", 3 );
                append( sb, "", 0 );

                append( sb, "inputDirectory", 2 );
                append( sb, "The input directory from where to copy the rules.", 3 );
                append( sb, "", 0 );

                append( sb, "outputDirectory", 2 );
                append( sb, "The output directory into which to copy the rules.", 3 );
                append( sb, "", 0 );

                append( sb, "reportDirectory", 2 );
                append( sb, "The output directory into which to write report file.", 3 );
                append( sb, "", 0 );

                append( sb, "reportFile", 2 );
                append( sb, "The output file name for the report file.", 3 );
                append( sb, "", 0 );

                append( sb, "xmlExtension", 2 );
                append( sb, "The default extension for xml drools file", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "drools-copy-validate".equals( goal ) )
        {
            append( sb, "drools5:drools-copy-validate", 0 );
            append( sb, "This goal copy drools file and validate them.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "extension", 2 );
                append( sb, "The default extension for drools file", 3 );
                append( sb, "", 0 );

                append( sb, "inputDirectory", 2 );
                append( sb, "The input directory from where to copy the rules.", 3 );
                append( sb, "", 0 );

                append( sb, "outputDirectory", 2 );
                append( sb, "The output directory into which to copy the rules.", 3 );
                append( sb, "", 0 );

                append( sb, "reportDirectory", 2 );
                append( sb, "The output directory into which to write report file.", 3 );
                append( sb, "", 0 );

                append( sb, "reportFile", 2 );
                append( sb, "The output file name for the report file.", 3 );
                append( sb, "", 0 );

                append( sb, "xmlExtension", 2 );
                append( sb, "The default extension for xml drools file", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "drools-copy-validate-test".equals( goal ) )
        {
            append( sb, "drools5:drools-copy-validate-test", 0 );
            append( sb, "This goal copy drools file and validate them for test.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "extension", 2 );
                append( sb, "The default extension for drools file", 3 );
                append( sb, "", 0 );

                append( sb, "inputDirectory", 2 );
                append( sb, "The input directory from where to copy the rules.", 3 );
                append( sb, "", 0 );

                append( sb, "outputDirectory", 2 );
                append( sb, "The output directory into which to copy the rules.", 3 );
                append( sb, "", 0 );

                append( sb, "reportDirectory", 2 );
                append( sb, "The output directory into which to write report file.", 3 );
                append( sb, "", 0 );

                append( sb, "reportFile", 2 );
                append( sb, "The output file name for the report file.", 3 );
                append( sb, "", 0 );

                append( sb, "xmlExtension", 2 );
                append( sb, "The default extension for xml drools file", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "drools-postprocessor".equals( goal ) )
        {
            append( sb, "drools5:drools-postprocessor", 0 );
            append( sb, "This goal post process class to instrumentalize the classes.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "excludes", 2 );
                append( sb, "This is an optional list of excludes pattern. If this parameter is not used, the excludes files are all file not ending with the extension defined by the extension parameter.", 3 );
                append( sb, "", 0 );

                append( sb, "extension", 2 );
                append( sb, "The default extension for class file.", 3 );
                append( sb, "", 0 );

                append( sb, "includes", 2 );
                append( sb, "This is an optional list of includes pattern. If this parameter is not used, the includes files are all file ending with the extension defined by the extension parameter. In any case, file must have the right extension.", 3 );
                append( sb, "", 0 );

                append( sb, "inputDirectory", 2 );
                append( sb, "The input directory from where to copy the rules.", 3 );
                append( sb, "", 0 );

                append( sb, "reportDirectory", 2 );
                append( sb, "The output directory into which to write report file.", 3 );
                append( sb, "", 0 );

                append( sb, "reportFile", 2 );
                append( sb, "The output file name for the report file.", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "drools-postprocessor-test".equals( goal ) )
        {
            append( sb, "drools5:drools-postprocessor-test", 0 );
            append( sb, "This goal post process test class to instrumentalize the classes.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "excludes", 2 );
                append( sb, "This is an optional list of excludes pattern. If this parameter is not used, the excludes files are all file not ending with the extension defined by the extension parameter.", 3 );
                append( sb, "", 0 );

                append( sb, "extension", 2 );
                append( sb, "The default extension for class file", 3 );
                append( sb, "", 0 );

                append( sb, "includes", 2 );
                append( sb, "This is an optional list of includes pattern. If this parameter is not used, the includes files are all file ending with the extension defined by the extension parameter. In any case, file must have the right extension.", 3 );
                append( sb, "", 0 );

                append( sb, "inputDirectory", 2 );
                append( sb, "The input directory from where to copy the rules.", 3 );
                append( sb, "", 0 );

                append( sb, "reportDirectory", 2 );
                append( sb, "The output directory into which to write report file.", 3 );
                append( sb, "", 0 );

                append( sb, "reportFile", 2 );
                append( sb, "The output file name for the report file.", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "drools-report".equals( goal ) )
        {
            append( sb, "drools5:drools-report", 0 );
            append( sb, "This goal analyse report from xml log of the various plugin. The result is a readable log, computed from the report XML file, integrated into the site.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "reportInputDirectory", 2 );
                append( sb, "Directory from where to read logs.", 3 );
                append( sb, "", 0 );

                append( sb, "reportOutputDirectory", 2 );
                append( sb, "Directory where reports will go.", 3 );
                append( sb, "", 0 );
            }
        }

        if ( goal == null || goal.length() <= 0 || "help".equals( goal ) )
        {
            append( sb, "drools5:help", 0 );
            append( sb, "Display help information on drools5-integration-helper-maven-plugin.\nCall\n\u00a0\u00a0mvn\u00a0drools5:help\u00a0-Ddetail=true\u00a0-Dgoal=<goal-name>\nto display parameter details.", 1 );
            append( sb, "", 0 );
            if ( detail )
            {
                append( sb, "Available parameters:", 1 );
                append( sb, "", 0 );

                append( sb, "detail (Default: false)", 2 );
                append( sb, "If true, display all settable properties for each goal.", 3 );
                append( sb, "", 0 );

                append( sb, "goal", 2 );
                append( sb, "The name of the goal for which to show help. If unspecified, all goals will be displayed.", 3 );
                append( sb, "", 0 );

                append( sb, "indentSize (Default: 2)", 2 );
                append( sb, "The number of spaces per indentation level, should be positive.", 3 );
                append( sb, "", 0 );

                append( sb, "lineLength (Default: 80)", 2 );
                append( sb, "The maximum length of a display line, should be positive.", 3 );
                append( sb, "", 0 );
            }
        }

        if ( getLog().isInfoEnabled() )
        {
            getLog().info( sb.toString() );
        }
    }

    /**
     * <p>Repeat a String <code>n</code> times to form a new string.</p>
     *
     * @param str String to repeat
     * @param repeat number of times to repeat str
     * @return String with repeated String
     * @throws NegativeArraySizeException if <code>repeat < 0</code>
     * @throws NullPointerException if str is <code>null</code>
     */
    private static String repeat( String str, int repeat )
    {
        StringBuffer buffer = new StringBuffer( repeat * str.length() );

        for ( int i = 0; i < repeat; i++ )
        {
            buffer.append( str );
        }

        return buffer.toString();
    }

    /** 
     * Append a description to the buffer by respecting the indentSize and lineLength parameters.
     * <b>Note</b>: The last character is always a new line.
     * 
     * @param sb The buffer to append the description, not <code>null</code>.
     * @param description The description, not <code>null</code>.
     * @param indent The base indentation level of each line, must not be negative.
     */
    private void append( StringBuffer sb, String description, int indent )
    {
        for ( Iterator it = toLines( description, indent, indentSize, lineLength ).iterator(); it.hasNext(); )
        {
            sb.append( it.next().toString() ).append( '\n' );
        }
    }

    /** 
     * Splits the specified text into lines of convenient display length.
     * 
     * @param text The text to split into lines, must not be <code>null</code>.
     * @param indent The base indentation level of each line, must not be negative.
     * @param indentSize The size of each indentation, must not be negative.
     * @param lineLength The length of the line, must not be negative.
     * @return The sequence of display lines, never <code>null</code>.
     * @throws NegativeArraySizeException if <code>indent < 0</code>
     */
    private static List toLines( String text, int indent, int indentSize, int lineLength )
    {
        List lines = new ArrayList();

        String ind = repeat( "\t", indent );
        String[] plainLines = text.split( "(\r\n)|(\r)|(\n)" );
        for ( int i = 0; i < plainLines.length; i++ )
        {
            toLines( lines, ind + plainLines[i], indentSize, lineLength );
        }

        return lines;
    }

    /** 
     * Adds the specified line to the output sequence, performing line wrapping if necessary.
     * 
     * @param lines The sequence of display lines, must not be <code>null</code>.
     * @param line The line to add, must not be <code>null</code>.
     * @param indentSize The size of each indentation, must not be negative.
     * @param lineLength The length of the line, must not be negative.
     */
    private static void toLines( List lines, String line, int indentSize, int lineLength )
    {
        int lineIndent = getIndentLevel( line );
        StringBuffer buf = new StringBuffer( 256 );
        String[] tokens = line.split( " +" );
        for ( int i = 0; i < tokens.length; i++ )
        {
            String token = tokens[i];
            if ( i > 0 )
            {
                if ( buf.length() + token.length() >= lineLength )
                {
                    lines.add( buf.toString() );
                    buf.setLength( 0 );
                    buf.append( repeat( " ", lineIndent * indentSize ) );
                }
                else
                {
                    buf.append( ' ' );
                }
            }
            for ( int j = 0; j < token.length(); j++ )
            {
                char c = token.charAt( j );
                if ( c == '\t' )
                {
                    buf.append( repeat( " ", indentSize - buf.length() % indentSize ) );
                }
                else if ( c == '\u00A0' )
                {
                    buf.append( ' ' );
                }
                else
                {
                    buf.append( c );
                }
            }
        }
        lines.add( buf.toString() );
    }

    /** 
     * Gets the indentation level of the specified line.
     * 
     * @param line The line whose indentation level should be retrieved, must not be <code>null</code>.
     * @return The indentation level of the line.
     */
    private static int getIndentLevel( String line )
    {
        int level = 0;
        for ( int i = 0; i < line.length() && line.charAt( i ) == '\t'; i++ )
        {
            level++;
        }
        for ( int i = level + 1; i <= level + 4 && i < line.length(); i++ )
        {
            if ( line.charAt( i ) == '\t' )
            {
                level++;
                break;
            }
        }
        return level;
    }
}
