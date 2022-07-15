# SlotMachine-Optaplanner-Core
An extended version of the optaplanner-core library (Version 8.14) that additionally supports local search in a privacy-preserving manner.

In orde to use the project, best to use maven to compile the source with:

``mvn clean install -DskipTests``

The project is then installed to your local maven repository and can be integrated into the dependent system via following dependency in the pom.xml file:

```
<dependency>
  <groupId>org.optaplanner</groupId>
  <artifactId>dke-optaplanner-core</artifactId>
  version>8.14.2-SNAPSHOT</version>
</dependency>
```

For a user guide, we refer to the official Optaplanner [documentation](https://docs.optaplanner.org/latestFinal/optaplanner-docs/html_single/).

In addition to the default features of Optaplanner, this project supports local search algorithms where the neighbourhood is evaluated at once at the end of each iteration by a class that can be implemented in the system that integrates this library by implementing [this](https://github.com/jku-win-dke/SlotMachine-Optaplanner-Core/blob/main/src/main/java/org/optaplanner/core/impl/localsearch/decider/forager/privacypreserving/NeighbourhoodEvaluator.java) interface. In addition, [this]() interface has to be implemented to work in conjunction with the acceptor. The implementing classes have to be referenced in the configuration. For a example configuration of a local search solver using the extended features, see [this](https://github.com/jku-win-dke/SlotMachine-Optaplanner-Core/blob/main/configuration.xml) file.

