package org.openrefine.benchmarks.memory;

import org.openrefine.browsing.facets.FacetConfigResolver;
import org.openrefine.expr.MetaParser;
import org.openrefine.importing.FormatRegistry;
import org.openrefine.model.recon.ReconConfig;
import org.openrefine.operations.OperationRegistry;

public class WorkflowElements {

    public static void register() {

        OperationRegistry.registerOperation("core", "text-transform", org.openrefine.operations.cell.TextTransformOperation.class);
        OperationRegistry.registerOperation("core", "mass-edit", org.openrefine.operations.cell.MassEditOperation.class);

        OperationRegistry.registerOperation("core", "multivalued-cell-join", org.openrefine.operations.cell.MultiValuedCellJoinOperation.class);
        OperationRegistry.registerOperation("core", "multivalued-cell-split", org.openrefine.operations.cell.MultiValuedCellSplitOperation.class);
        OperationRegistry.registerOperation("core", "fill-down", org.openrefine.operations.cell.FillDownOperation.class);
        OperationRegistry.registerOperation("core", "blank-down", org.openrefine.operations.cell.BlankDownOperation.class);
        OperationRegistry.registerOperation("core", "transpose-columns-into-rows", org.openrefine.operations.cell.TransposeColumnsIntoRowsOperation.class);
        OperationRegistry.registerOperation("core", "transpose-rows-into-columns", org.openrefine.operations.cell.TransposeRowsIntoColumnsOperation.class);
        OperationRegistry.registerOperation("core", "key-value-columnize", org.openrefine.operations.cell.KeyValueColumnizeOperation.class);

        OperationRegistry.registerOperation("core", "column-addition", org.openrefine.operations.column.ColumnAdditionOperation.class);
        OperationRegistry.registerOperation("core", "column-removal", org.openrefine.operations.column.ColumnRemovalOperation.class);
        OperationRegistry.registerOperation("core", "column-rename", org.openrefine.operations.column.ColumnRenameOperation.class);
        OperationRegistry.registerOperation("core", "column-move", org.openrefine.operations.column.ColumnMoveOperation.class);
        OperationRegistry.registerOperation("core", "column-split", org.openrefine.operations.column.ColumnSplitOperation.class);
        OperationRegistry.registerOperation("core", "column-addition-by-fetching-urls", org.openrefine.operations.column.ColumnAdditionByFetchingURLsOperation.class);
        OperationRegistry.registerOperation("core", "column-reorder", org.openrefine.operations.column.ColumnReorderOperation.class);

        OperationRegistry.registerOperation("core", "row-removal", org.openrefine.operations.row.RowRemovalOperation.class);
        OperationRegistry.registerOperation("core", "row-star", org.openrefine.operations.row.RowStarOperation.class);
        OperationRegistry.registerOperation("core", "row-flag", org.openrefine.operations.row.RowFlagOperation.class);
        OperationRegistry.registerOperation("core", "row-reorder", org.openrefine.operations.row.RowReorderOperation.class);

        OperationRegistry.registerOperation("core", "recon", org.openrefine.operations.recon.ReconOperation.class);
        OperationRegistry.registerOperation("core", "recon-mark-new-topics", org.openrefine.operations.recon.ReconMarkNewTopicsOperation.class);
        OperationRegistry.registerOperation("core", "recon-match-best-candidates", org.openrefine.operations.recon.ReconMatchBestCandidatesOperation.class);
        OperationRegistry.registerOperation("core", "recon-discard-judgments", org.openrefine.operations.recon.ReconDiscardJudgmentsOperation.class);
        OperationRegistry.registerOperation("core", "recon-match-specific-topic-to-cells", org.openrefine.operations.recon.ReconMatchSpecificTopicOperation.class);
        OperationRegistry.registerOperation("core", "recon-judge-similar-cells", org.openrefine.operations.recon.ReconJudgeSimilarCellsOperation.class);
        OperationRegistry.registerOperation("core", "recon-clear-similar-cells", org.openrefine.operations.recon.ReconClearSimilarCellsOperation.class);
        OperationRegistry.registerOperation("core", "recon-copy-across-columns", org.openrefine.operations.recon.ReconCopyAcrossColumnsOperation.class);
        OperationRegistry.registerOperation("core", "extend-reconciled-data", org.openrefine.operations.recon.ExtendDataOperation.class);
        OperationRegistry.registerOperation("core", "recon-use-values-as-identifiers", org.openrefine.operations.recon.ReconUseValuesAsIdentifiersOperation.class);

        /*
         *  Formats and their UI class names and parsers:
         *  - UI class names are used on the client-side in Javascript to instantiate code that lets the user
         *    configure the parser's options.
         *  - Parsers are server-side code that do the actual parsing. Because they have access to the raw files,
         *    they also generate defaults for the client-side UIs to initialize.
         */

        FormatRegistry.registerFormat("text", "core-import-formats/text" ); // generic format, no parser to handle it
        FormatRegistry.registerFormat("text/line-based", "core-import-formats/text/line-based", "LineBasedParserUI",
            new org.openrefine.importers.LineBasedImporter());
        FormatRegistry.registerFormat("text/line-based/*sv", "core-import-formats/text/line-based/*sv", "SeparatorBasedParserUI",
            new org.openrefine.importers.SeparatorBasedImporter());
        FormatRegistry.registerFormat("text/line-based/fixed-width", "core-import-formats/text/line-based/fixed-width", "FixedWidthParserUI",
            new org.openrefine.importers.FixedWidthImporter());

        FormatRegistry.registerFormat("text/rdf/nt", "core-import-formats/text/rdf/nt", "RdfTriplesParserUI", 
                    new org.openrefine.importers.RdfTripleImporter(org.openrefine.importers.RdfTripleImporter.Mode.NT));
        FormatRegistry.registerFormat("text/rdf/n3", "core-import-formats/text/rdf/n3", "RdfTriplesParserUI", 
                new org.openrefine.importers.RdfTripleImporter(org.openrefine.importers.RdfTripleImporter.Mode.N3));
        FormatRegistry.registerFormat("text/rdf/ttl", "core-import-formats/text/rdf/ttl", "RdfTriplesParserUI", 
                        new org.openrefine.importers.RdfTripleImporter(org.openrefine.importers.RdfTripleImporter.Mode.TTL));
        FormatRegistry.registerFormat("text/rdf/xml", "core-import-formats/text/rdf/xml", "RdfTriplesParserUI", new org.openrefine.importers.RdfXmlTripleImporter());
        FormatRegistry.registerFormat("text/rdf/ld+json", "core-import-formats/text/rdf/ld+json", "RdfTriplesParserUI", new org.openrefine.importers.RdfJsonldTripleImporter());

        FormatRegistry.registerFormat("text/xml", "core-import-formats/text/xml", "XmlParserUI", new org.openrefine.importers.XmlImporter());
        FormatRegistry.registerFormat("binary/text/xml/xls/xlsx", "core-import-formats/binary/text/xml/xls/xlsx", "ExcelParserUI", new org.openrefine.importers.ExcelImporter());
        FormatRegistry.registerFormat("text/xml/ods", "core-import-formats/text/xml/ods", "ExcelParserUI", new org.openrefine.importers.OdsImporter());
        FormatRegistry.registerFormat("text/json", "core-import-formats/text/json", "JsonParserUI", new org.openrefine.importers.JsonImporter());
        FormatRegistry.registerFormat("text/marc", "core-import-formats/text/marc", "XmlParserUI", new org.openrefine.importers.MarcImporter());
        FormatRegistry.registerFormat("text/wiki", "core-import-formats/text/wiki", "WikitextParserUI", new org.openrefine.importers.WikitextImporter());
        FormatRegistry.registerFormat("openrefine-legacy", null, null, new org.openrefine.importers.LegacyProjectImporter());

        FormatRegistry.registerFormat("binary", "core-import-formats/binary"); // generic format, no parser to handle it

        FormatRegistry.registerFormat("service", "core-import-formats/service"); // generic format, no parser to handle it

        /*
         *  Extension to format mappings
         */
        FormatRegistry.registerExtension(".txt", "text");
        FormatRegistry.registerExtension(".csv", "text/line-based/*sv");
        FormatRegistry.registerExtension(".tsv", "text/line-based/*sv");

        FormatRegistry.registerExtension(".xml", "text/xml");
        FormatRegistry.registerExtension(".atom", "text/xml");
        
        FormatRegistry.registerExtension(".json", "text/json");
        FormatRegistry.registerExtension(".js", "text/json");

        FormatRegistry.registerExtension(".xls", "binary/text/xml/xls/xlsx");
        FormatRegistry.registerExtension(".xlsx", "binary/text/xml/xls/xlsx");

        FormatRegistry.registerExtension(".ods", "text/xml/ods");
        
        FormatRegistry.registerExtension(".n3", "text/rdf/n3");
        FormatRegistry.registerExtension(".ttl", "text/rdf/ttl");
        FormatRegistry.registerExtension(".jsonld", "text/rdf/ld+json");
        FormatRegistry.registerExtension(".rdf", "text/rdf/xml");

        FormatRegistry.registerExtension(".marc", "text/marc");
        FormatRegistry.registerExtension(".mrc", "text/marc");

        FormatRegistry.registerExtension(".wiki", "text/wiki");

        /*
         *  Mime type to format mappings
         */
        FormatRegistry.registerMimeType("text/plain", "text");
        FormatRegistry.registerMimeType("text/csv", "text/line-based/*sv");
        FormatRegistry.registerMimeType("text/x-csv", "text/line-based/*sv");
        FormatRegistry.registerMimeType("text/tab-separated-value", "text/line-based/*sv");
        FormatRegistry.registerMimeType("text/tab-separated-values", "text/line-based/*sv");

        FormatRegistry.registerMimeType("text/fixed-width", "text/line-based/fixed-width");
        
        FormatRegistry.registerMimeType("application/n-triples", "text/rdf/nt");
        FormatRegistry.registerMimeType("text/n3", "text/rdf/n3");
        FormatRegistry.registerMimeType("text/rdf+n3", "text/rdf/n3");
        FormatRegistry.registerMimeType("text/turtle", "text/rdf/ttl");
        FormatRegistry.registerMimeType("application/xml", "text/xml");
        FormatRegistry.registerMimeType("text/xml", "text/xml");
        FormatRegistry.registerMimeType("+xml", "text/xml"); // suffix will be tried only as fallback
        FormatRegistry.registerMimeType("application/rdf+xml", "text/rdf/xml");
        FormatRegistry.registerMimeType("application/ld+json", "text/rdf/ld+json");
        FormatRegistry.registerMimeType("application/atom+xml", "text/xml");

        FormatRegistry.registerMimeType("application/msexcel", "binary/text/xml/xls/xlsx");
        FormatRegistry.registerMimeType("application/x-msexcel", "binary/text/xml/xls/xlsx");
        FormatRegistry.registerMimeType("application/x-ms-excel", "binary/text/xml/xls/xlsx");
        FormatRegistry.registerMimeType("application/vnd.ms-excel", "binary/text/xml/xls/xlsx");
        FormatRegistry.registerMimeType("application/x-excel", "binary/text/xml/xls/xlsx");
        FormatRegistry.registerMimeType("application/xls", "binary/text/xml/xls/xlsx");
        FormatRegistry.registerMimeType("application/x-xls", "binary/text/xml/xls/xlsx");
        FormatRegistry.registerMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "binary/text/xml/xls/xlsx");
        FormatRegistry.registerMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.template", "binary/text/xml/xls/xlsx");

        FormatRegistry.registerMimeType("application/vnd.oasis.opendocument.spreadsheet","text/xml/ods");

        FormatRegistry.registerMimeType("application/json", "text/json");
        FormatRegistry.registerMimeType("application/javascript", "text/json");
        FormatRegistry.registerMimeType("text/json", "text/json");
        FormatRegistry.registerMimeType("+json", "text/json"); // suffix will be tried only as fallback

        FormatRegistry.registerMimeType("application/marc", "text/marc");
        
        /*
         *  Format guessers: these take a format derived from extensions or mime-types,
         *  look at the actual files' content, and try to guess a better format.
         */
        FormatRegistry.registerFormatGuesser("text", new org.openrefine.importers.TextFormatGuesser());
        FormatRegistry.registerFormatGuesser("text/line-based", new org.openrefine.importers.LineBasedFormatGuesser());

        MetaParser.registerLanguageParser("grel", "General Refine Expression Language (GREL)", org.openrefine.grel.Parser.grelParser, "value");

        FacetConfigResolver.registerFacetConfig("core", "list", org.openrefine.browsing.facets.ListFacet.ListFacetConfig.class);
        FacetConfigResolver.registerFacetConfig("core", "range", org.openrefine.browsing.facets.RangeFacet.RangeFacetConfig.class);
        FacetConfigResolver.registerFacetConfig("core", "timerange", org.openrefine.browsing.facets.TimeRangeFacet.TimeRangeFacetConfig.class);
        FacetConfigResolver.registerFacetConfig("core", "text", org.openrefine.browsing.facets.TextSearchFacet.TextSearchFacetConfig.class);
        FacetConfigResolver.registerFacetConfig("core", "scatterplot", org.openrefine.browsing.facets.ScatterplotFacet.ScatterplotFacetConfig.class);

        ReconConfig.registerReconConfig("core", "standard-service", org.openrefine.model.recon.StandardReconConfig.class);

    }
}
