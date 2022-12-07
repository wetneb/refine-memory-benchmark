package org.openrefine.benchmarks.memory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import org.ehcache.sizeof.SizeOf;
import org.openrefine.ProjectManager;
import org.openrefine.ProjectMetadata;
import org.openrefine.io.FileProjectManager;
import org.openrefine.model.DatamodelRunner;
import org.openrefine.model.GridState;
import org.openrefine.model.IndexedRow;
import org.openrefine.model.LocalDatamodelRunner;
import org.openrefine.model.Project;
import org.openrefine.model.RunnerConfiguration;
import org.openrefine.model.RunnerConfigurationImpl;
import org.testng.log4testng.Logger;


/**
 * Hello world!
 *
 */
public class Benchmark 
{
    static Logger logger = Logger.getLogger(Benchmark.class);
    static Writer writer;
    static SizeOf sizeOf = SizeOf.newInstance();
    
    public static void main( String[] args ) throws IOException
    {
        RunnerConfiguration conf = new RunnerConfigurationImpl();
        // Initialize OpenRefine
        logger.info("Initializing OpenRefine");
        WorkflowElements.register();
        File workspace = new File("workspace");
        DatamodelRunner runner = new LocalDatamodelRunner(conf);
        FileProjectManager.initialize(runner, workspace);
        logger.info("Done initializing OpenRefine");
        
        // Initialize output stream
        File outputTsv = new File("stats.tsv");
        
        try (OutputStream outputStream = new FileOutputStream(outputTsv)) {
            writer = new OutputStreamWriter(outputStream);
            writeLine("name", "size", "rows", "columns", "reconColumns");
            
        
            ProjectManager projectManager = FileProjectManager.singleton;
            for (long projectId : projectManager.getAllProjectMetadata().keySet()) {
                ProjectMetadata metadata = projectManager.getAllProjectMetadata().get(projectId);
                logger.info("Benchmarking memory usage on project " + projectId +": " + metadata.getName());
                Project project = projectManager.getProject(projectId);
                
                inspectGrid(metadata.getName(), project.getHistory().getInitialGridState());
                if (project.getHistory().getPosition() != 0) {
                    inspectGrid(metadata.getName(), project.getHistory().getCurrentGridState());
                }
                
                logger.info("Unloading project");
                project.dispose();
            }
            
            writer.close();
        }
    }
    
    protected static void inspectGrid(String name, GridState grid) throws IOException {
        List<IndexedRow> rows = grid.collectRows();
        long columns = grid.getColumnModel().getColumns().size();
        long reconColumns = grid.getColumnModel().getColumns().stream().filter(c -> c.getReconStats() != null).count();
        logger.info("Rows: " + rows.size());
        if (rows.size() > 500000) {
            logger.warn("temporarily skipping this one");
            return;
        }
        logger.info("Columns: " + columns);
        logger.info("Recon columns: " + reconColumns);
        long size = sizeOf.deepSizeOf(rows);
        logger.info("Deep size: " + size);
        writeLine(name, size, rows.size(), columns, reconColumns);
    }
    
    protected static void writeLine(Object... args) throws IOException {
        String[] strings = Arrays.asList(args).stream().map(Object::toString).toArray(String[]::new);
        writer.write(String.join("\t", strings) + "\n");
    }
}
