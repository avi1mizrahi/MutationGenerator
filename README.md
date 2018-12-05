# MutationGenerator
### Clone
```
git clone https://github.com/avi1mizrahi/MutationGenerator.git
cd MutationGenerator
```
### Build
```
mvn package
```
### Run
```
java -cp target/MutationGenerator-0.1-shaded.jar MutationGenerator --input-dir ~/java-small/training/ --output-dir out --rename-variable --word2vec-map ~/vecs.txt --num-threads 16
```
or
```
java -cp target/MutationGenerator-0.1-shaded.jar MutationGenerator --input-dir ~/java-small/training/ --output-dir out --flip-binary-expr
```
### Examine
```
meld out/RenameMutator/presto/DiskRange/0.java ~/java-small/training/presto/DiskRange.java
```
