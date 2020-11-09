package org.example;



import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * 
 * @phase process-sources
 */
@Mojo( name = "rest-request" )
public class MyMojo
    extends AbstractMojo
{

    private File outputDirectory;


    @Parameter( property = "endpoint" )
    private URI endpoint;


    @Parameter( property = "resource" )
    private String resource;


    @Parameter( property = "method" )
    private String method = "GET";
    @Parameter
    private String requestType = MediaType.APPLICATION_JSON;


    @Parameter
    private String responseType = MediaType.APPLICATION_JSON;


    public void execute()
        throws MojoExecutionException
    {



        Client client = ClientBuilder.newClient();

        WebTarget baseTarget = client.target( getEndpoint() );

        if ( null != getResource() )
        {
            getLog().debug( String.format( "Setting resource [%s]", getResource() ) );
            baseTarget = baseTarget.path( getResource() );
        }

        Invocation.Builder builder = baseTarget.request( getRequestType() ).accept( getResponseType() );
       // builder.method( getMethod() ).getEntity().toString();
        ErrorInfo result = processResponse(builder.method( getMethod()));
        if(result==null){
            getLog().debug( String.format( "Error ...." ) );
        }


    }

    private ErrorInfo processResponse(Response response) throws MojoExecutionException {
        File f = outputDirectory;
        if ( response.getStatus()== Response.Status.OK.getStatusCode())
        {
            if ( !f.exists() )
            {
                f.mkdirs();
            }

            File touch = new File( f, "touch.txt" );

            FileWriter w = null;
            try
            {
                w = new FileWriter( touch );

                w.write( response.getEntity().toString() );
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "Error creating file " + touch, e );
            }
            finally
            {
                if ( w != null )
                {
                    try
                    {
                        w.close();
                    }
                    catch ( IOException e )
                    {
                        // ignore
                    }
                }
            }

        }
        else
        {
            getLog().warn( String.format( "Error code: [%d]", response.getStatus() ) );
            getLog().debug( response.getEntity().toString() );
            return new ErrorInfo( response.getStatus(), response.getEntity().toString() );
        }
        return null;
    }


    public URI getEndpoint()
    {
        return endpoint;
    }


    public String getResource()
    {
        return resource;
    }


//    public List<FileSet> getFilesets()
//    {
//        return filesets;
//    }

    /**
     * @return the fileset
     */
//    public FileSet getFileset()
//    {
//        return fileset;
//    }

    /**
     * @return the outputDir
     */
//    public File getOutputDir()
//    {
//        return outputDir;
//    }

    /**
     * @return the outputFilename
     */
//    public File getOutputFilename()
//    {
//        return outputFilename;
//    }

    /**
     * @return the requestType
     */
    public String getRequestType()
    {
        return requestType;
    }

    /**
     * @return the responseType
     */
    public String getResponseType()
    {
        return responseType;
    }

    /**
     * @return the queryParams
     */
//    public Map<String, String> getQueryParams()
//    {
//        return queryParams;
//    }

    /**
     * @return the headers
     */
//    public Map<String, String> getHeaders()
//    {
//        return headers;
//    }

    /**
     * @return the fileMapper
     */
//    public FileMapper getFileMapper()
//    {
//        return fileMapper;
//    }

    /**
     * @return the fileMappers
     */
//    public List<FileMapper> getFileMappers()
//    {
//        return fileMappers;
//    }

    /**
     * @return the basedir
     */
//    public File getBasedir()
//    {
//        return basedir;
//    }

    /**
     * @return the target
     */
//    public File getTarget()
//    {
//        return target;
//    }

    /**
     * @return the projectHelper
     */
//    public MavenProjectHelper getProjectHelper()
//    {
//        return projectHelper;
//    }

    /**
     * @return the method
     */
    public String getMethod()
    {
        return method;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod( String method )
    {
        this.method = method;
    }

    public class ErrorInfo
    {

        private final int errorCode;
        private final String message;

        public ErrorInfo( int code, String msg )
        {
            errorCode = code;
            message = msg;
        }

        public ErrorInfo( String msg )
        {
            errorCode = -1;
            message = msg;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append( " [" ).append( errorCode ).append( ":" ).append( message ).append( "]" );
            return sb.toString();
        }
    }

}
