package odata4j.Client;

import org.joda.time.LocalDateTime;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.OCreateRequest;
import org.odata4j.core.OEntity;
import org.odata4j.core.OFuncs;
import org.odata4j.core.OProperties;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.exceptions.ODataProducerException;
import org.odata4j.format.FormatType;

public class SapOdataTest extends AbstractLog {
	static String serviceUrl = "http://jswldev:8080/sap/opu/odata/sap/ZOATEST_SRV/";

	public static void main(String[] args) {
		SapOdataTest app = new SapOdataTest();
		// 基本身份验证
		ODataConsumer c = ODataConsumers.newBuilder(serviceUrl)
				.setClientBehaviors(OClientBehaviors.basicAuth("RFC_CALL", "123456"), new SAPCSRFBehaviour())
				// .setFormatType(FormatType.JSON) json格式传输
				.build();
		try {

			app.createEntity(c);
		} catch (ODataProducerException e) {
			System.out.println("HttpStatus:" + e.getHttpStatus());
			System.out.println("Message:" + e.getMessage());
		}

	}

	public void getMetadata(ODataConsumer c) {
		reportMetadata(c.getMetadata());
	}

	public void getEntity(ODataConsumer c) {
		// 根据Key值 订单ID取订单条目
		OEntity enty = c.getEntity("OderHeadSet", "1000000000").execute();
		reportEntity("订单: " + enty.getProperty("OderID").getValue(), enty);
	}

	public void getEntities(ODataConsumer c) {
		// 取所有的订单数据
		for (OEntity enty : c.getEntities("OderHeadSet").top(10).execute()) {
			reportEntity("订单: " + enty.getProperty("OderID").getValue(), enty);
		}
	}

	public void getEntities_filter(ODataConsumer c) {
		// 根据订单描述查询订单数据
		OEntity enty = c.getEntities("OderHeadSet").filter("CreatedBy eq 'PANDH'").execute()
				.first();
		reportEntity("PANDH创建的第一个订单：", enty);
	}

	public void createEntity(ODataConsumer c) {
		// 创建一条订单数据
		OEntity newOrderH = c.createEntity("OderHeadSet")
				.properties(OProperties.string("CreatedBy", "PANDH"))
				.properties(OProperties.datetime("CreatedAt", new LocalDateTime()))
				.execute();

		report("新订单: " + newOrderH);
	}

	public void updateEntity(ODataConsumer c) {
		try {
			OEntity OrderH = c.getEntity("OderHeadSet", "1000000000").execute();
			// 更新订单数据
			c.updateEntity(OrderH).properties(OProperties.string("CreatedBy", "ttt")).execute();
			report("newOrder CreatedBy after update: "
					+ c.getEntity("OderHeadSet", "1000000000").execute().getProperty("CreatedBy").getValue());

		} catch (ODataProducerException e) {
			report(e.getMessage());
		}

	}

	public void mergeEntity(ODataConsumer c) {
		// 用merge方法更新订单数据
		c.mergeEntity("Products", 10).properties(OProperties.int32("Rating", 500)).execute();

		report("newProduct rating after merge: "
				+ c.getEntity("Products", 10).execute().getProperty("Rating").getValue());

	}

	public void deleteEntity(ODataConsumer c) {
		// 删除记录
		c.deleteEntity("Products", 10).execute();
		boolean exists = true;
		try {
			c.getEntity("Products", 10).execute();
		} catch (ODataProducerException e) {
			if (e.getHttpStatus().getStatusCode() == 404)
				exists = false;
		}
		report("newProduct " + (exists ? "exists" : "does not exist"));
	}

	public void getEntities_orderBy(ODataConsumer c) {
		// 排序
		report("highest rated product (compute on server): "
				+ c.getEntities("Products").orderBy("Rating desc").top(1).execute().first());
		report("highest rated product (compute on client): " + c.getEntities("Products").execute()
				.orderBy(OFuncs.entityPropertyValue("Rating", Integer.class)).last());

	}
}
