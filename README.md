![cartographer 1x](https://user-images.githubusercontent.com/26745523/40954629-06db25f6-684b-11e8-99dd-0a1a9aae2a0f.png)
An Elasticsearch mapping migration utility. This project was inspired by all the amazing work done by the [Flyway](https://flywaydb.org/) team for their database migration utility. A very similar migration strategy for Elasticsearch mappings is provided by Cartographer.

## Maven and Gradle
Maven:
```xml
<dependency>
    <groupId>com.jahnelgroup.cartographer</groupId>
    <artifactId>cartographer-spring</artifactId>
    <version>1.0.7</version>
</dependency>
```

Gradle:
```
compile('com.jahnelgroup.cartographer:cartographer-spring:1.0.7')
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

## Cartographer Index
Cartographer keeps track of migrations in Elasticsearch index. The name is configurable but defaults to **cartographer**.

```json
{
    "took": 4,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 4,
        "max_score": 1,
        "hits": [
            {
                "_index": "cartographer",
                "_type": "cartographer",
                "_id": "cats_1",
                "_score": 1,
                "_source": {
                    "documentId": "cats_1",
                    "index": "cats",
                    "filename": "cats_V1_init.json",
                    "version": 1,
                    "description": "init",
                    "checksum": "db95b6e57e1dddca9ca4ef6245535fe7",
                    "timestamp": "2018-05-01T15:07:05-0500",
                    "status": "SUCCESS"
                }
            },
            {
                "_index": "cartographer",
                "_type": "cartographer",
                "_id": "dogs_1",
                "_score": 1,
                "_source": {
                    "documentId": "dogs_1",
                    "index": "dogs",
                    "filename": "dogs_V1_init.json",
                    "version": 1,
                    "description": "init",
                    "checksum": "f6d5ce918b264cd0bfebad2e31101e63",
                    "timestamp": "2018-05-01T15:07:05-0500",
                    "status": "SUCCESS"
                }
            },
            {
                "_index": "cartographer",
                "_type": "cartographer",
                "_id": "cats_2",
                "_score": 1,
                "_source": {
                    "documentId": "cats_2",
                    "index": "cats",
                    "filename": "cats_V2_color.json",
                    "version": 2,
                    "description": "color",
                    "checksum": "d3a281bf4805083e93f13f78a143c3f2",
                    "timestamp": "2018-05-01T15:07:05-0500",
                    "status": "SUCCESS"
                }
            },
            {
                "_index": "cartographer",
                "_type": "cartographer",
                "_id": "dogs_2",
                "_score": 1,
                "_source": {
                    "documentId": "dogs_2",
                    "index": "dogs",
                    "filename": "dogs_V2_weightAndType.json",
                    "version": 2,
                    "description": "weightAndType",
                    "checksum": "5642ed84cbb803d4f50b41c4255fb1fa",
                    "timestamp": "2018-05-01T15:07:05-0500",
                    "status": "SUCCESS"
                }
            }
        ]
    }
}
```
