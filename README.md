# refine-memory-benchmark

Small application to test the amount of memory taken up by an OpenRefine grid with its new architecture.
It consists in two parts:
* a Java application which loads up various OpenRefine projects (to be dropped as an OpenRefine workspace initialized in the `workspace/` subdirectory of this repository), and measures the size of their grid when loaded in memory (in bytes). Those statistics are collected in the `stats.tsv` file
* a Jupyter notebook to analyze the statistics and train a model to predict the memory size of a grid given various characteristics.

## Current results

The size in bytes of a grid can be predicted as: `(85 * columnsNotReconciled + 343 * columnsReconciled + 980) * rows`.

This could be refined by taking more samples, computing the sparsity of the grid (which could then be estimated on a sample of rows), or other features.


MIT license
