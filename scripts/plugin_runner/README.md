### Usage

#### Extract methods from dataset

``` 
python3 scripts/plugin_runner/clones_batch_processing.py /path/to/dataset/dir /path/to/extracted_methods/dir 
```

You can use the ```--batch-size n``` option (default value is 300).
Also, there is ```--start-from n``` option to start from batch with a given number.

Known issues:

This process runs IntelliJ Idea plugin and may get stuck for some reason.
If so, kill the process and rerun it with the ```--start-from n``` option (using number of the last processed batch).
