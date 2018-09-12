package com.freedom.springboot.elasticsearch.jest;

import com.freedom.springboot.elasticsearch.jest.bean.Student;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootElasticsearchJestApplicationTests {

	@Autowired
	JestClient jestClient;

	/**
	 * 索引数据
	 */
	@Test
	public void index() {
		//创建student
		Student student = new Student();
		student.setId(1);
		student.setName("zhangsan");
		student.setAge(20);
		student.setBirth(new java.sql.Date(System.currentTimeMillis()));

		//创建一个索引
		Index index = new Index.Builder(student).index("student").type("highschool").build();

		try {
			//执行
			jestClient.execute(index);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 查询
	 */
	@Test
	public void search(){
		String json = "{\n" +
				"    \"query\" : {\n" +
				"        \"match\" : {\n" +
				"            \"name\" : \"zhangsan\"\n" +
				"        }\n" +
				"    }\n" +
				"}";

		Search search = new Search.Builder(json).addIndex("student").addType("highschool").build();

		try {
			//执行查询
			SearchResult searchResult = jestClient.execute(search);

			System.out.println(searchResult.getJsonString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
