package org.noear.solon.maven.plugin;

import org.apache.commons.compress.archivers.examples.Archiver;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.archiver.war.WarArchiver;
import org.noear.solon.maven.plugin.tools.tool.ArtifactsLibraries;
import org.noear.solon.maven.plugin.tools.tool.Libraries;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Mojo(name = "repackage", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class RepackageMojo extends AbstractMojo {

    @Parameter(required = false)
    private String mainClass;

    @Parameter(readonly = false)
    private String jvmArguments;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project.build.finalName}", required = true)
    private String finalName;

    @Parameter(defaultValue = "${project.packaging}", required = true)
    private String packaging;

    @Parameter
    private String classifier;

    @Component
    private MavenProject project;

    public static PluginType PLUGIN_TYPE;


    private final Log logger = getLog();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        logger.info("打包类型：" + packaging);
        if (packaging != null) {
            if ("jar".equalsIgnoreCase(packaging)) {
                PLUGIN_TYPE=PluginType.JAR;
                //移动数据
                ClassesMove.change(project.getArtifact().getFile());
                //处理依赖
                repackage();
                //处理loader
                try {
                    CopyLoader.start(getTargetFile());
                } catch (Exception e) {
                    throw new MojoExecutionException("write loader exception", e);
                }
            } else if ("war".equalsIgnoreCase(packaging)) {
                PLUGIN_TYPE=PluginType.WAR;
                //移动数据
                ClassesMove.change(project.getArtifact().getFile());
            }
        } else {
            throw new MojoExecutionException("打包方式不是JAR或者WAR类型");
        }

    }

    private void repackage() throws MojoExecutionException, MojoFailureException {
        try {
            File sourceFile = project.getArtifact().getFile();
            Repackager repackager = new Repackager(sourceFile, logger, mainClass);
            File target = getTargetFile();
            Set<Artifact> artifacts = project.getArtifacts();
            Libraries libraries = new ArtifactsLibraries(artifacts, Collections.emptyList(), getLog());
            repackager.repackage(target, libraries);
        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    private File getTargetFile() {
        String classifier = (this.classifier != null ? this.classifier.trim() : "");
        if (classifier.length() > 0 && !classifier.startsWith("-")) {
            classifier = "-" + classifier;
        }
        if (!this.outputDirectory.exists()) {
            this.outputDirectory.mkdirs();
        }
        String name = this.finalName + classifier + "." + this.project.getArtifact().getArtifactHandler().getExtension();
        return new File(this.outputDirectory, name);
    }

}
