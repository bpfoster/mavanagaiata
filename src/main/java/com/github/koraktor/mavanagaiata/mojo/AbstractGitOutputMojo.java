/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2012, Sebastian Staudt
 */

package com.github.koraktor.mavanagaiata.mojo;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * This abstract Mojo implements writing output to a <code>PrintStream</code>
 *
 * This is either <code>System.out</code> by default, but maybe another
 * <code>PrintStream</code> object wrapped around a file given by
 * <code>outputFile</code>.
 *
 * @author Sebastian Staudt
 * @see File
 * @see PrintStream
 * @since 0.2.2
 */
public abstract class AbstractGitOutputMojo extends AbstractGitMojo {

    /**
     * The encoding to use for generated output
     *
     * @parameter property="mavanagaiata.encoding"
     */
    protected String encoding = "UTF-8";

    /**
     * The footer to print below the output
     *
     * @parameter property="mavanagaiata.footer"
     */
    protected String footer = "\nGenerated by Mavanagaiata at %s";

    protected PrintStream outputStream;

    /**
     * Flushes the <code>PrintStream</code> and closes it if it is not
     * <code>System.out</code>
     *
     * @see PrintStream#close
     */
    @Override
    protected void cleanup() {
        if(this.outputStream != null) {
            this.outputStream.flush();
            if(this.getOutputFile() != null) {
                this.outputStream.close();
            }
        }

        super.cleanup();
    }

    /**
     * Initializes the output stream for the generated content
     *
     * @throws MojoExecutionException if the output file can not be opened
     */
    @Override
    protected void init() throws MojoExecutionException {
        this.initOutputStream();

        super.init();
    }

    /**
     * Initializes the <code>PrintStream</code> to use
     *
     * This is <code>System.out</code> if no output file is given (default).
     * Otherwise the parent directories of <code>outputFile</code> are created
     * and a new <code>PrintStream</code> for that file is created.
     *
     * @throws MojoExecutionException if the file specified by
     *         <code>outputFile</code> cannot be found
     */
    protected void initOutputStream() throws MojoExecutionException {
        if(this.getOutputFile() == null) {
            this.outputStream = System.out;
        } else {
            try {
                if(!this.getOutputFile().getParentFile().exists()) {
                    this.getOutputFile().getParentFile().mkdirs();
                }
                this.outputStream = new PrintStream(this.getOutputFile(), this.encoding);
            } catch (IOException e) {
                throw new MojoExecutionException("Could not initialize output file.", e);
            }
        }
    }

    /**
     * Inserts a footer into the output stream if it is not empty
     */
    protected void insertFooter() {
        if(this.footer.length() > 0) {
            SimpleDateFormat baseDateFormat = new SimpleDateFormat(this.baseDateFormat);
            this.outputStream.println(String.format(this.footer, baseDateFormat.format(new Date())));
        }
    }

    /**
     * Returns the output file for the generated content
     * <p>
     * Has to be implemented by subclassing mojos, so that the output file
     * can be easily configured.
     *
     * @return The output file for the generated content
     */
    public abstract File getOutputFile();

    /**
     * Sets the output file for the generated content
     *
     * @param outputFile The output file for the generated content
     */
    public abstract void setOutputFile(File outputFile);

}
