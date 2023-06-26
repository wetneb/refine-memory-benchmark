package org.openrefine.benchmarks.memory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.ehcache.sizeof.SizeOf;
import org.openrefine.ProjectManager;
import org.openrefine.ProjectMetadata;
import org.openrefine.browsing.Engine.Mode;
import org.openrefine.browsing.Engine;
import org.openrefine.browsing.EngineConfig;
import org.openrefine.browsing.columns.ColumnStats;
import org.openrefine.io.FileProjectManager;
import org.openrefine.model.Runner;
import org.openrefine.model.Grid;
import org.openrefine.model.IndexedRow;
import org.openrefine.runners.local.LocalRunner;
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
        Runner runner = new LocalRunner(conf);
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
                
                inspectGrid(metadata.getName(), project.getHistory().getInitialGrid());
                if (project.getHistory().getPosition() != 0) {
                    inspectGrid(metadata.getName(), project.getHistory().getCurrentGrid());
                }
                
                logger.info("Unloading project");
                project.dispose();
            }
            
            writer.close();
        }
    }
    
    protected static void inspectGrid(String name, Grid grid) throws IOException {
        List<IndexedRow> rows = grid.collectRows();
        long columns = grid.getColumnModel().getColumns().size();
        EngineConfig engineConfig = new EngineConfig(Collections.emptyList(), Mode.RowBased, 500L);
        Engine engine = new Engine(grid, engineConfig, 0L);
        List<ColumnStats> columnStats = engine.getColumnStats();
        long reconColumns = columnStats.stream().filter(c -> c.getReconciled() > 0).count();
        logger.info("Rows: " + rows.size());
        if (rows.size() > 500000) {
            logger.warn("temporarily skipping this one");
            return;
        }
        logger.info("Columns: " + columns);
        logger.info("Recon columns: " + reconColumns);
        for (int i = 1; i < 11; i++) {
            long subsetSize = i*(rowCount / 10);
            List<IndexedRow> subset = grid.getRowsAfter(0, (int) subsetSize);
            long size = sizeOf.deepSizeOf(subset);
            logger.info("Deep size: " + size);
            writeLine(name, size, subsetSize, columns, reconColumns);
        }
    }
    
    protected static void writeLine(Object... args) throws IOException {
        String[] strings = Arrays.asList(args).stream().map(Object::toString).toArray(String[]::new);
        writer.write(String.join("\t", strings) + "\n");
    }
}
