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
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.Validate;
import org.ehcache.sizeof.SizeOf;
import org.openrefine.ProjectManager;
import org.openrefine.ProjectMetadata;
import org.openrefine.browsing.Engine.Mode;
import org.openrefine.browsing.Engine;
import org.openrefine.browsing.EngineConfig;
import org.openrefine.browsing.columns.ColumnStats;
import org.openrefine.history.History;
import org.openrefine.io.FileProjectManager;
import org.openrefine.model.Runner;
import org.openrefine.model.Grid;
import org.openrefine.model.IndexedRow;
import org.openrefine.runners.local.LocalRunner;
import org.openrefine.model.Project;
import org.openrefine.model.RunnerConfiguration;
import org.openrefine.model.RunnerConfigurationImpl;
import org.openrefine.operations.exceptions.OperationException;
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
    
    public static void main( String[] args ) throws IOException, InterruptedException, ExecutionException, OperationException
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
            writeLine("name", "size", "rows", "columns", "reconColumns", "reconCells");
            
            ProjectManager projectManager = FileProjectManager.singleton;
            for (long projectId : projectManager.getAllProjectMetadata().keySet()) {
                ProjectMetadata metadata = projectManager.getAllProjectMetadata().get(projectId);
                logger.info("Benchmarking memory usage on project " + projectId +": " + metadata.getName());
                Project project = projectManager.getProject(projectId);
                History history = project.getHistory();
                history.waitForCaching();
                int cachedPosition = history.getCachedPosition();
                long historyEntryId = cachedPosition == 0 ? 0L : history.getEntries().get(cachedPosition - 1).getId();
                history.undoRedo(historyEntryId);
                history.waitForCaching();
                
                inspectGrid(metadata.getName(), project.getCurrentGrid());
                
                logger.info("Unloading project");
                project.dispose();
            }
            
            writer.close();
        }
    }
    
    protected static void inspectGrid(String name, Grid grid) throws IOException {
        long columns = grid.getColumnModel().getColumns().size();
        long rows = grid.rowCount();
        long sampleSize = 500L;
        EngineConfig engineConfig = new EngineConfig(Collections.emptyList(), Mode.RowBased, sampleSize);
        Engine engine = new Engine(grid, engineConfig, 0L);
        List<ColumnStats> columnStats = engine.getColumnStats();
        long reconColumns = columnStats.stream().filter(c -> c.getReconciled() > 0).count();
        long reconCells = (rows / sampleSize) * columnStats.stream().mapToLong(c -> c.getReconciled()).sum();
        logger.info("Rows: " + rows);
        if (rows > 500000) {
            logger.warn("temporarily skipping this one");
            return;
        }
        logger.info("Columns: " + columns);
        logger.info("Recon columns: " + reconColumns);
        long size = estimateSizeViaGC(grid);
        logger.info("Estimated size: " + size);
        writeLine(name, size, rows, columns, reconColumns, reconCells);
    }
    
    protected static long estimateSizeWithSizeOf(Grid grid) {
        List<IndexedRow> rows = grid.collectRows();
        return sizeOf.deepSizeOf(rows);
    }
    
    protected static long estimateSizeViaGC(Grid grid) {
        grid.cache();
        Validate.isTrue(grid.isCached(), "we failed to cache this grid");
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long withCachedGrid = runtime.totalMemory() - runtime.freeMemory();
        grid.uncache();
        System.gc();
        long withoutCachedGrid = runtime.totalMemory() - runtime.freeMemory();
        return withCachedGrid - withoutCachedGrid;
    }
    
    protected static void writeLine(Object... args) throws IOException {
        String[] strings = Arrays.asList(args).stream().map(Object::toString).toArray(String[]::new);
        writer.write(String.join("\t", strings) + "\n");
    }
}
