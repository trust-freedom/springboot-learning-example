package com.freedom.springboot.elasticsearch.jest;

import com.freedom.springboot.elasticsearch.jest.bean.Student;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.Update;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * JestClient用法
 * 参考：https://github.com/searchbox-io/Jest/tree/master/jest
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JestTests {

	@Autowired
	JestClient jestClient;

	/**
	 * 索引数据
	 */
	@Test
	public void index() {
		//创建student，使用@JestId指定id字段为documentId
		Student student = new Student();
		student.setId(1);
		student.setName("zhangsan");
		student.setAge(35);
		student.setBirth(new java.sql.Date(System.currentTimeMillis()));

		//创建一个索引
		Index index = new Index.Builder(student)
				                     .index("student")    //索引
				                     .type("highschool")  //type
				                     //.id("1")             //也可以这样指定documentId
				                     .build();

		try {
			//执行
			JestResult result = jestClient.execute(index);

			System.out.println("原始返回json：" + result.getJsonString());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 查询，自己拼json
	 */
	@Test
	public void searchByJson(){
		String json = "{\n" +
				"    \"query\" : {\n" +
				"        \"match\" : {\n" +
				"            \"name\" : \"zhangsan\"\n" +
				"        }\n" +
				"    }\n" +
				"}";

		Search search = new Search.Builder(json)
				.addIndex("student")
				.addType("highschool")
				.build();

		try {
			//执行查询
			SearchResult searchResult = jestClient.execute(search);

			System.out.println("原始返回json：" + searchResult.getJsonString());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 通过SearchSourceBuilder构造查询json
	 * SearchSourceBuilder需要引入elasticsearch依赖
	 */
	@Test
	public void searchBySearchSourceBuilder(){
		//构造查询builder，指定查询条件
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("name", "zhangsan"));

		//构造Search，指定index、type
		Search search = new Search.Builder(searchSourceBuilder.toString())
				.addIndex("student")
				.addType("highschool")
				.build();

		try {
			//执行查询
			SearchResult searchResult = jestClient.execute(search);

			System.out.println("原始返回json：" + searchResult.getJsonString());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 查询结果转换为List
	 * SearchResult.Hit的List 或 指定类型的集合List
	 */
	@Test
	public void searchSourceAsObjectList(){
		//构造查询builder，指定查询条件
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("name", "zhangsan"));

		//构造Search，指定index、type
		Search search = new Search.Builder(searchSourceBuilder.toString())
				.addIndex("student")
				.addType("highschool")
				.build();

		try {
			//执行查询
			SearchResult searchResult = jestClient.execute(search);

			System.out.println("原始返回json：" + searchResult.getJsonString());

			//将查询结果转换为SearchResult.Hit
			List<SearchResult.Hit<Student, Void>> hits = searchResult.getHits(Student.class);
			for(SearchResult.Hit<Student, Void> hit : hits){
				System.out.println("hit.index：" + hit.index);
				System.out.println("hit.type：" + hit.type);
				System.out.println("hit.id：" + hit.id);
				System.out.println("hit.score：" + hit.score);
				System.out.println("hit.source：" + hit.source);
				System.out.println("----------");
			}


			List<Student> students = searchResult.getSourceAsObjectList(Student.class);
			for(Student student : students){
				System.out.println("student：" + student);
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Get获取document
	 * 转换为指定类型
	 */
	@Test
	public void get(){
		Get get = new Get.Builder("student", "1")  //index & documentId
				.type("highschool")
				.build();

		try {
			JestResult result = jestClient.execute(get);

			System.out.println("原始返回json：" + result.getJsonString());

			//转换为Student
			Student student = result.getSourceAsObject(Student.class);
			System.out.println("student：" + student);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 更新
	 */
	@Test
	public void update(){
		String json = "{\n" +
					"    \"doc\" : {\n" +
					"        \"age\" : \"40\"\n" +
					"    }\n" +
					"}";

		Update update = new Update.Builder(json).index("student").type("highschool").id("1").build();

		try {
			JestResult result = jestClient.execute(update);

			System.out.println("原始返回json：" + result.getJsonString());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 删除
	 */
	@Test
	public void delete(){
		Delete delete = new Delete.Builder("1")  //documentId
				.index("student")     //index
				.type("highschool")   //type
				.build();

		try {
			JestResult jestResult = jestClient.execute(delete);

			System.out.println("原始返回json：" + jestResult.getJsonString());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Bulk
	 */
	@Test
	public void bulk(){
		//构建bulk
		Bulk bulk = new Bulk.Builder()
				.defaultIndex("student")
				.defaultType("highschool")
				.addAction(new Index.Builder(new Student(1,"zhangsan",20,new java.sql.Date(System.currentTimeMillis()))).build())
				.addAction(Arrays.asList(  //List of objects can be indexed via bulk api
						new Index.Builder(new Student(2,"lisi",25,new java.sql.Date(System.currentTimeMillis()))).build(),
						new Index.Builder(new Student(3,"wangwu",30,new java.sql.Date(System.currentTimeMillis()))).build()))
				.addAction(new Delete.Builder("1").index("student").type("highschool").build())
				.build();

		try {
			//执行bulk
			JestResult jestResult = jestClient.execute(bulk);

			System.out.println("原始返回json：" + jestResult.getJsonString());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 异步执行rest请求
	 */
	@Test
	public void executeAsync(){
		final CountDownLatch latch = new CountDownLatch(1);

		Student student = new Student();
		student.setId(10);
		student.setName("wangwu");
		student.setAge(27);
		student.setBirth(new java.sql.Date(System.currentTimeMillis()));

		Index index = new Index.Builder(student)
				.index("student")
				.type("highschool")
				.build();

		//异步执行
		jestClient.executeAsync(index, new JestResultHandler<DocumentResult>() {
			@Override
			public void completed(DocumentResult documentResult) {
				System.out.println("原始返回json：" + documentResult.getJsonString());
				latch.countDown();
			}

			@Override
			public void failed(Exception e) {
				e.printStackTrace();
				latch.countDown();
			}
		});

		//等待返回结果，否则主线程直接结束会报错
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
