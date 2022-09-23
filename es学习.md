# 创建单点es

## 创建docker网络

````bash
docker network create es-net
````

## 拉取docker镜像

````bash
docker pull elasticsearch:7.17.2
````

## 运行

````bash
docker run -d \
--name es \
-e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
-e "discovery.type=single-node" \
-v es-data:/usr/share/elasticsearch/data \
-v es-plugins:/usr/share/elasticsearch/plugins \
--privileged \
--network es-net \
-p 9200:9200 \
-p 9300:9300 \
elasticsearch:7.17.2
````

显示如下信息表示安装成功。

````json
{
  "name" : "059b3dcef304",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "8nGExWrGT-CREN6y39fU1A",
  "version" : {
    "number" : "7.17.2",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "de7261de50d90919ae53b0eff9413fd7e5307301",
    "build_date" : "2022-03-28T15:12:21.446567561Z",
    "build_snapshot" : false,
    "lucene_version" : "8.11.1",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
````

# 部署kibana

````bash
docker run -d \
--name kibana \
-e ELASTICSEARCH_HOSTS=http://es:9200 \
--network=es-net \
-p 5601:5601 \
kibana:7.17.2
````

首先第一点，kibana和es必须在同一个网络中才能使用http://es:9200这样访问，其次kibana和es版本需要相同。

# 安装IK分词器

````bash
# 进入容器内部
docker exec -it es /bin/bash

# 在线下载并安装
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.17.2/elasticsearch-analysis-ik-7.17.2.zip

# 退出容器
exit
# 重启容器
docker restart es
````

ik_smart: 词少

ik_max_word: 词多

若是需要扩展ik分词器的词典和停用词，只需进行相应配置（利用config目录的IkAnalyzer.cfg.xml文件添加）即可。

# 操作索引库

## mapping属性

对文档的约束，常见的属性有：

* type：
  * 字符串：text(可进行分词), keyword(精确值，品牌，国家，ip等)
  * 数值：long, integer, short, byte, double, float
  * 布尔：boolean
  * 日期：date
  * 对象：object
* index：是否创建索引，默认为true
* analyzer：使用哪种分词器
* properties：该字段的子字段

## 创建索引库

````elastic
PUT /索引库名
{
	"mapping": {
		"properties": {
			"info": {
				"type": "text",
				"analyzer": "ik-smart"
			},
			"email": {
				"type": "keyword",
				"index" false
			}
			"name": {
				"type": "object",
				"properties": {
					"firstName": {
						"type": "keyword"
					},
					"lastName": {
						"type": "keyword"
					}
				}
			}
		}
	}
}
````

## 查看、删除索引库

````elastic
GET /索引库名
````

````elastic
DELETE /索引库名
````

## 修改索引库

mapping一旦创建**无法修改**，但是可以添加新字段

````elastic
PUT /索引库名/_mapping
{
	"properties": {
		"新字段名": {
			"type": "integer"
		}
	}
}
````

# 文档操作

## 新增文档

````elastic
POST /索引库名/_doc/文档id
{
	"字段1": "值1",
	"字段2": "值2",
	"字段3": {
		"子属性1": "值3",
		"子属性": "值4"
	}
}
````

## 查询文档

````elastic
GET /索引库名/_doc/文档id
````

## 删除文档

````elastic
DELETE /索引库名/_doc/文档id
````

## 修改文档

* 全量修改，删除旧文档，添加新文档（既能修改又能新增，id不存在即为新增）

  ````elastic
  PUT /索引库名/_doc/文档id
  {
  	"字段1": "值1",
  	"字段2": "值2"
  }
  ````

* 局部修改

  ````elastic
  POST /索引库名/update/文档id
  {
  	"doc": {
  		"字段名": "新的值"
  	}
  }
  ````

# RestClient使用

## 准备

````xml
<dependency>
	<groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
</dependency>
````

注意需要覆盖springboot的es版本信息，在该项目的properties属性中添加elasticsearch.version属性进行版本的覆盖

````xml
<properties>
	<java.version>1.8</java.version>
    <elasticsearch.version>7.17.2</elasticsearch.version>
</properties>
````

进行RestHighLevelClient初始化

````java
RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(HttpHost.create("http://47.98.162.202:9200")))
````

## 创建索引库

````java
void testCreateIndex() throws IOException{
    // 创建request对象
    CreateIndexRequest request = new CreateIndexRequest("hotel");
    // 请求参数，MAPPING_TEMPLATE是静态常量字符串(json数据，或者说是DSL语句）DSL语句
    request.source(MAPPING_TEMPLATE, XContentType.JSON);
    // 发起请求
    client.indices().create(request, RequestOptions.DEFAULT); 
}
````

## 删除和判断索引库是否存在

删除

````java
void testDeleteIndex() throws IOException{
    // 创建request对象
    DeleteIndexRequest request = new DeleteIndexRequest("hotel");
    // 发起请求
    client.indices().delete(request, RequestOptions.DEFAULT); 
}
````

判断是否存在

````java
void testExistsIndex() throws IOException{
    // 创建request对象
    GetIndexRequest request = new GetIndexRequest("hotel");
    // 发起请求
    boolean exists = client.indices().exists(request, RequestOptions.DEFAULT); 
    System.out.println(exists)
}
````

## RestClient操作文档

* 添加数据

  ````java
  void testIndexDocument() throws IOException{
      // 创建request对象
      IndexRequest request = new IndexRequest("indexName").id("1");
      // 准备json文档
      request.source("{\"name\": \"Jack\", \"age\": 21}", XContentType.JSON);
      // 发送请求
      client.index(request, RequestOptions.DEFAULT);
  }
  ````

* 查询数据

  ````java
  void testGetDocumentById() throws IOException{
      // 创建request对象
      GetRequest request = new GetRequest("indexName", "1");
      // 发送请求得到结果
      GetResponse response = client.get(request, RequestOptions.DEFAULT);
      // 解析结果
      String json = response.getSourceAsString();
      
      System.out.println(json)
  }
  ````

  注意这里查到的是json格式，需要进行反序列化操作。

* 修改文档（部分更新）

  ````java
  void testUpdateDocument() throws IOException{
      // 创建request对象
      UpdateRequest request = new UpdateRequest("indexName", "1");
      // 准备参数，两个为一对，key value
      request.doc(
      	"age", 18,
          "name", "Rose"
      );
      // 更新文档
      client.update(request, RequestOptions.DEFAULT);
  }
  ````

* 删除文档

  ````java
  void testDeleteDocument() throws IOException{
      // 创建request对象
      DeleteRequest request = new DeleteRequest("indexName", "1");
      // 更新文档
      client.delete(request, RequestOptions.DEFAULT);
  }
  ````

## 批量导入

````java
void testBulk()  throws IOException{
    // 创建request对象
    BulkRequest request = new BulkRequest();
    // 添加需要批量提交的请求
    request.add(new IndexRequest("hotel").id("101").source("json source", XContentType.JSON));
    request.add(new IndexRequest("hotel").id("102").source("json source2", XContentType.JSON));
    // 发起bulk请求
    client.bulk(request, RequestOptions.DEFAULT);
}
````

这里的数据一般是先从数据库中查出来，然后再封装为Request。

 # DSL查询操作

## 查询

* 查询所有match_all

  ````elastic
  GET /indexName/_search
  {
  	"query": {
  		"match_all": {
  		}
  	}
  }
  ````

* 全文检索查询

  * match查询

    ````elastic
    GET /indexName/_search
    {
    	"query": {
    		"match": {
    			"FIELD": "TEXT"
    		}
    	}
    }
    ````

  * multi_match

    ````elastic
    GET /indexName/_search
    {
    	"query": {
    		"multi_match": {
    			"query": "TEXT",
    			"fields": ["FIELD1", "FIELD2"]
    		}
    	}
    }
    ````

* 精确查询

  * term: 根据词条精确值查询

    ````elastci
    GET /indexName/_search
    {
    	"query": {
    		"term": {
    			"FIELD": {
    				"value": "VALUE"
    			}
    		}
    	}
    }
    ````

  * range: 根据值的范围查询

    ````elastic
    GET /indexName/_search
    {
    	"query": {
    		"range": {
    			"FIELD": {
    				"gte": 10,
    				"lte": 20
    			}
    		}
    	}
    }
    ````

* 地理查询

  * geo_bounding_box: 查询geo_point落在某个矩形范围的所有文档

    ````elastic
    GET /indexName/_search
    {
    	"query": {
    		"geo_bounding_box": {
    			"FIELD": {
    				"top_left": {
    					"lat": 31.1,
    					"lon": 121.5
    				},
    				"bottom_right": {
    					"lat": 30.9,
    					"lon": 121.7
    				}
    			}
    		}
    	}
    }
    ````

  * geo_distance: 查询到指定中心点小于某个距离值的所有文档

    ````elastic
    GET /indexName/_search
    {
    	"query": {
    		"geo_distance": {
    			"distance": "15km",
    			"FIELD": "31.21, 121.5"
    		}
    	}
    }
    ````

* 复合查询

  * function_score: 算分函数查询，可以控制文档相关性算分，控制文档排名

    ````elastic
    GET /indexName/_search
    {
    	"query": {
    		"function_score": {
    			"query": {"match": {"all": "外滩"}},
    			"functions": [
    			  {
    			  	"filter": {"term": {"id": "1"}},
    			  	"weight": 10
    			  }
    			],
    			"boost_mode": "multiply"
    		}
    	}
    }
    ````

    最重要的三个东西：filter表示哪些需要加分，function表示如何算分，boost_mode表示与原始分数如何进行计算，默认为乘

  * boolean query: 一个或多个查询子句的组合（must, should, must_not, filter，其中must_not和filter不参与算分）

    ````elastic
    GET /index/_search
    {
    	"query": {
    		"bool": {
    			"must": [
    				{"term": {"FIELD": "VALUE"}},
    				{"term": {"FIELD": "VALUE"}}
    			],
    			"should": [
    				{"term": {"FIELD": "VALUE"}},
    				{"term": {"FIELD": "VALUE"}}
    			],
    			"must_not": [
    				{"range": {"FIELD": {"let": 500}}}
    			],
    			"filter": [
    				{"range": {"FIELD": {"let": 500}}}
    			],
    		}
    	}
    }
    ````

## 排序

````elastic
GET /indexName/_search
{
	"query": {
		"match_all": {}
	},
	"sort": [
		{
			"FIELD": "desc"
		}
	]
}
````

````elastic
GET /indexName/_search
{
	"query": {
		"match_all": {}
	},
	"sort": [
		{
			"_geo_distance": {
				"FIELD": "纬度, 经度",
				"order": "asc",
				"unit": "km"
			}
		}
	]
}
````

排序后就取消了打分

## 分页

````elastic
GET /indexName/_search
{
	"query": {
		"match_all": {}
	},
	"from": 990,	// 开始的位置
	"size": 10,		// 期望获取的分页数
	"sort": [
		{"price": "asc"}
	]
}
````

深度分页问题：

1. 先在每个分片上都排序并查询前n条数据
2. 选出所有节点的结果进行聚合，在内存中重新排序选出前n条
3. 最后从前n条数据中选出从i开始的j条文档

一般不能超出10000条，若是需要超出选择search after(推荐，但需要主页查询)或者 scroll(不推荐)

## 搜索结果高亮处理

````elastic
GET /indexName/_search
{
	"query": {
		"match": {
			"FIELD": "TEXT"
		}
	},
	"highlight": {
		"fields": {
			"FIELD": {	//	指定高亮字段
			"require_field_match": "false",
				"pre_tags": "<em>",	//	标记高亮字段的前置标签
				"post_tags": "</em>"	//	标记标记高亮字段的后置标签
			}
		}
	}
}
````

默认情况下搜索字段必须与高亮子段一致，使用"require_field_match": "false"进行指定。

# RestClient查询文档

````java
void testMatchAll() throws IOException{
    // 准备request
    SearchRequest request = new SearchRequest("hotel");
    // 准备dsl
  request.source().query(QueryBuilders.matchAllQuery());
    // 发送请求
    SearchResponse response = client.search(request, RequestOptions.DEFAULT);
}
````

## 结果解析

````java
// 结果解析
SearchHits searchHits = response.getHits();
// 查询的总条数
long total = searchHits.getTotalHits().value;
// 查询的结果数据
SearchHit[] hits = searchHits.getHits();
for(SearchHit hit : hits){
    // 得到source
    String json = hit.getSourceAsString();
    // 打印
    System.out.println(json);
}
````

## 全文检索

````java
request.source().query(QueryBuilders.matchQuery("all", "如家"));
request.source().query(QueryBuilders.multiMatchQuery("如家", "name", "business"));
````

````java
void testMatchAll() throws IOException{
    // 准备request
    SearchRequest request = new SearchRequest("hotel");
    // 准备dsl
  request.source().query(QueryBuilders.matchQuery("all", "如家"));
    // request.source().query(QueryBuilders.multiMatchQuery("如家", "name", "business"));
    // 发送请求
    SearchResponse response = client.search(request, RequestOptions.DEFAULT);
    // 结果解析
	SearchHits searchHits = response.getHits();
	// 查询的总条数
	long total = searchHits.getTotalHits().value;
	// 查询的结果数据
	SearchHit[] hits = searchHits.getHits();
	for(SearchHit hit : hits){
    	// 得到source
    	String json = hit.getSourceAsString();
    	// 打印
    	System.out.println(json);
	}
}
````

其余查询方式都只有在QueryBuilders里面修改。

## 排序和分页

````java
// 查询
request.source.query(QueryBuilders.matchAllQuery());
// 分页
request.source().from(0).size(5);
// 价格排序
request.source().sort("price", SortOrder.ASC);
````

## 高亮

### 请求构建

````java
request.source.highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));
````

### 结果解析

````java
// 获取source
HotelDoc hotelDoc = JSON.parseObject(hit.getSourceAsString(), HotelDoc.class);
//  处理高亮
Map<String, HighlightField> highlightFields = hit.getHighlightFields();
if(!CollectionUtils.isEmpty(highlightFields)){
    // 获取高亮字段结果
    HighlightField highlightField = highlightFields.get("name");
    if(highlightField != null){
        // 取出高亮结果数组中的第一个就是酒店名称
        String name = highlightField.getFragments()[0].string();
        hotel.setName(name);
    }
}
````

# RestClient实现数据聚合

## 请求构造

````java
request.source().size(0);
request.source().aggregation(
	AggregationBuilders
    	.term("brand_agg")
    	.field("brand")
    .size(20)
);
````

## 结果解析

````java
// 解析聚合结果
Aggregations aggregations = response.getAggregations();
// 根据名称获取聚合结果
Terms brandTerms = aggregations.get("brand_agg");
// 获取桶
List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
// 遍历
for(Terms.Bucket bucket : buckets){
    // 获取key，也就是品牌信息
    String brandName = bucket.getKeyAsString();
    System.out.println(brandName);
}
````

# RestClient实现自动补全

* 安装拼音分词器

  安装到es目录下的plugin目录下

* 自定义分词器

  创建索引时使用拼音分词器，查询时使用ik分词器

* 实现

  ````java
  // 准备参数
  SearchRequest request = new SearchRequest("hotel");
  // 请求参数
  request.source().suggest(new SuggestBuilder().addSuggestion("mySuggestion", SuggestBuilders.completionSuggestion("title").prefix("h").skipDuplicates(true).size(10)));
  // 发送请求
  client.search(request, RequestOptions.DEFAULT);
  // 解析结果
  
  // 处理结果
  Suggest suggest = response.getSuggest();
  // 根据名称获取补全结果
  CompletionSuggestion suggestion = suggest.getSuggestion("mySuggestion");
  // 获取options并遍历
  for(CompletionSuggestion.Entry.Option option : suggestion.getOptions){
      // 获取option中的text，也就是补全的词条
      String text = option.getText().string();
      
      System.out.println(text);
  }
  ````

# 搭建ES集群

单机问题：1. 海量数据存储， 2. 单点故障

解决方法：

1. 将索引库从逻辑上拆分为N个分片，存储到多个节点（解决海量数据问题）
2. 将分片数据在不同节点备份（解决单点故障问题）

## 创建docker-compose文件

````sh
version: '2.2'
services:
  es01:
    image: elasticsearch:7.17.2
    container_name: es01
    enviroment: 
      - node.name=es-01
      - cluster.name=es-docker-cluster
      - discovery.seed_hosts=es02,es03
      -cluster.initial_master_nodes=es01,es01,es03
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    volumes:
      - data01:/usr/share/elasticsearch/data
    ports:
      - 9200:9200
    networks:
      - elastic
  es02:
    image: elasticsearch:7.17.2
    container_name: es02
    enviroment: 
      - node.name=es-02
      - cluster.name=es-docker-cluster
      - discovery.seed_hosts=es01,es03
      - cluster.initial_master_nodes=es01,es01,es03
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    volumes:
      - data02:/usr/share/elasticsearch/data
    ports:
      - 9201:9200
    networks:
      - elastic
  es03:
    image: elasticsearch:7.17.2
    container_name: es03
    enviroment: 
      - node.name=es-03
      - cluster.name=es-docker-cluster
      - discovery.seed_hosts=es01,es02
      -cluster.initial_master_nodes=es01,es01,es03
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    volumes:
      - data03:/usr/share/elasticsearch/data
    ports:
      - 9202:9200
    networks:
      - elastic
````

注意的点：首先- cluster.initial_master_nodes=es01,es01,es03表示从哪些节点中选举产生主节点，其次由于宿主机端口9200被占用，因此后面的使用的是9201以及9202

此外，es在运行时还需要修改一些linux系统权限，所以修改/etc/sysctl.conf文件，添加

````sh
vm.max_map_count=262144
````

然后执行命令使配置生效

````sh
sysctl -p
````

## 集群状态监控

可以使用cerebro进行监控，访问9000端口即可

对于分片和副本，可以在DevTools中执行（也可以在cerebro中）

````json
PUT /decucin
{
    "setting": {
        "number_of_shards": 3,	// 分片数量
        "number_of_replicas": 1	//副本数量
    },
    "mapping": {
        "properties": {
            // mapping映射定义
        }
    }
}
````

## 集群节点信息

|    节点类型     |            配置参数             | 默认值 |                           节点职责                           |
| :-------------: | :-----------------------------: | :----: | :----------------------------------------------------------: |
| master eligible |           node.master           |  true  | 备选主节点：主节点可以管理和记录集群状态、决定分片在哪个节点、处理创建和删除索引库的请求 |
|      data       |            node.data            |  true  |             数据节点：存储数据、搜索、聚合、CRUD             |
|     ingest      |           node.ingest           |  true  |                     数据存储之前的预处理                     |
|  coordination   | 上面三个参数都为false则为此节点 |   无   |    路由请求到其它节点，合并其它节点处理的结果，返回给用户    |

## ES集群脑裂问题

当主节点与其它节点网络故障时可能发生脑裂问题，此时其余备选主节点认为主节点宕机，选举产生新的主节点，选举之后网络恢复，那么此时集群个不同的主节点。

为了避免此问题发生，需要要求选票超过(eligible节点数+1) / 2才能当选为主，因此eligible最好是奇数，对应配置项是discovery.zen.minimum_master_nodes，之后已成为默认配置

## ES集群的分布式存储

shard = hash(_routing) % number_of_shards

* _routing默认是文档的id
* 算法与分片数量有关，因此索引库一旦创建，分片数量不能修改

## ES集群的分布式查询

1. scatter phase: 分散阶段，coordinating node会把请求分发到每一个分片
2. gather phase: 聚集阶段，coordinating node汇总data node的搜索结果，并处理为最终结果集返回

## ES集群的故障转移

主节点宕机会从备选主节点中选出一个作为新的主节点。

主节点会监控集群中节点的状态，如果发现有其它节点宕机，会立即将宕机节点分片数据转移到其它节点，确保数据安全。