# Kotlin-Analysis immutability

Functionality related to immutability analysis in Kotlin projects

## Run the project

#### 1. Download list of repositories

You can use [seart-ghs.si.usi.ch]( https://seart-ghs.si.usi.ch/) (main language: Kotlin) for this purpose. The file has
to be in csv format with ```name``` column (add it if it is not there), containing repositories' names in the following
format: ```username/project_name```

#### 2. Clean duplicated repositories

Run script for cleaning all duplicated repositories with different names:

``` shell script
cd scripts
python3 -m data_collection.clean_duplicates --start-from=0 /path/to/csv_file/results.csv /path/to/cleaned/data/dir
```

You can use ```--save-metadata``` flag to download metadata about all projects. Metadata includes information about
repository's full name, owner, etc. This script makes requests to GitHub API, so you should add your GitHub Token to
environment variables (variable name is ```GITHUB_TOKEN```).

#### 3. Load dataset

Run the following command to download the dataset (cleaned repository csv file may contain junk lines at the top, remove it in some text editor):

``` 
cd scripts
python3 -m data_collection.load_dataset --start-from=0 /path/to/cleaned/data/dir/results.csv /path/to/dataset/dir
```

#### 4. Run analysis
Run the following command to perform the analysis on a dataset. A dataset folder must contain `config.yaml`. Configuration is described in latter sections.
``` 
./gradlew :kotlin-analysis-plugin:cli -Prunner=kotlin-immutability-analysis -Pinput=/path/to/dataset/dir -Poutput=path/to/results/dir
```
### Configuration
Configure analysis using `config.yaml` file in your dataset folder. The configuration must be in the following format:

```
tasks:
    - flag_1: value_1
      flag_2: value_2
      ...
      flag_n: value_n
      
    - flag_1: value_1
      flag_2: value_2
      ...
      flag_n: value_n
     
    ...
    
    - flag_1: value_1
      flag_2: value_2
      ...
      flag_n: value_n
```

Each task can be configured with the following flags:

| flag | value | description |
| ---- | ---- | ---- |
| `treatCollectionsAsMutable` | boolean | Treat standard kotlin collections such as `kotlin.collection.List`, `kotlin.collection.Set` and `kotlin.collections.Map` as mutable. Otherwise, such collections are considered conditionally deeply immutable. |
| `treatLazyAsImmutable` | boolean | Treat properties with `lazy` delegate as immutable.  |
| `analyzeSealedSubclasses` | boolean | Analyze each subclass of a sealed class to find out its immutability. |
| `assumeNastyInheritors` | boolean | Assume that each open class or interface can have mutable subclass. This makes any such class and interface mutable. |
| `assumeGoodGetters` | boolean | Assume that getters do not affect immutability of a property. If this flag is false, any property with getter is considered mutable. |
| `includeTests` | boolean | Include tests to analysis. If this flag is false, the the program tries to determine which modules are belong to tests and exclude it. If modules are resolved correctly, then `WITHOUT_TEST_MODULES` will be written in results. Otherwise, all folders with name `test` will be excludes. In such case, `WITHOUT_TEST_FOLDERS` will be written. |
| `outputFileName` | string | This flag is mandatory. The name of a file to which print results. File should be unique for each task. |

### Results
The result for each task is csv file with columns:

| project | name | type | immutability | tests | containingFile | reasonNumber | reason | infoKeys | infoValues | config_* ...
| ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- |
| Name of a project | Name of an entity | Type of an entity (`CLASS`, `INTERFACE`, `ENUM_CLASS`, ...)  | (`Immutable`, `ShallowImmutable`, `ConditionallyDeeplyImmutable`, `Mutable`) | (`WITH_TESTS`, `WITHOUT_TEST_FOLDERS`, `WITHOUT_TEST_MODULES`) | Path to the file containing such entity | Number of a reason | Immutability reason | Additional reason info key | Additional reason info value | Values of a flags. Each has individual column and its name is prefixed with `config_`. 
