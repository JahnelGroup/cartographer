# Cartographer
An Elasticsearch mapping migration utility. This project was inspired by all the amazing work done by the [Flyway](https://flywaydb.org/) team for their database migration utility. A very similar migration strategy is provided by Cartographer.

## Maven and Gradle
Maven:
```xml
<dependency>
    <groupId>com.jahnelgroup.cartographer</groupId>
    <artifactId>cartographer-spring</artifactId>
    <version>1.0.2</version>
</dependency>
```

Gradle:
```
compile('com.jahnelgroup.cartographer:cartographer-spring:1.0.2')
```

## Example Output
In this example we're adding the color field to the existing *cats* index. The Elasticsearch index already has mappings for *cats* as well as *dogs*. The *dogs* index will remain unchanged because no new migrations were found. All existing migrations are validated with checksums to make sure everything continues to match up. 
```
Created cartographer index cartographer in Elasticsearch.
Starting Elasticsearch mapping migrations.
Found 4 migrations on disk.
Found 1 existing migrations in Elasticsearch for index cats.
Validating index=cats file=cats_V1_init.json version=1
Migrating index=cats file=cats_V2_color.json version=2
Found 2 existing migrations in Elasticsearch for index dogs.
Validating index=dogs file=dogs_V1_init.json version=1
Validating index=dogs file=dogs_V2_weightAndType.json version=2
Success.
```
