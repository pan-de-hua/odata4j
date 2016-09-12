package odata4j.Client;

import org.joda.time.LocalDateTime;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.OCreateRequest;
import org.odata4j.core.OEntities;
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
				.setClientBehaviors(OClientBehaviors.basicAuth("pandh", "passw0rd"), new SAPCSRFBehaviour())
				// .setFormatType(FormatType.JSON) json格式传输
				.build();
		try {
			app.deleteEntity(c);
			//app.createEntity(c);
		} catch (ODataProducerException e) {
			System.out.println("HttpStatus:" + e.getHttpStatus());
			System.out.println("Message:" + e.getMessage());
		}catch(Exception e){
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
		
		OEntity newOrderI = c.createEntity("OderItemSet")
				.properties(OProperties.string("ItemID", "1"))
				.properties(OProperties.string("Material", "123333"))
				.get();

		OEntity newOrderI2 = c.createEntity("OderItemSet")
				.properties(OProperties.string("ItemID", "2"))
				.properties(OProperties.string("Material", "223333"))
				.get();
		OEntity orders[] ={newOrderI,newOrderI2}; 
		OEntity newOrderH = c.createEntity("OderHeadSet")
				.properties(OProperties.string("CreatedBy", "PANDH"))
				.properties(OProperties.datetime("CreatedAt", new LocalDateTime()))
				//.link("OderItemSet", newOrderI)
				.inline("OderItemSet", orders)
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
		c.mergeEntity("OderHeadSet", "1000000037").properties(OProperties.datetime("CreatedAt", new LocalDateTime())).execute();

		report("newProduct rating after merge: "
				+ c.getEntity("OderHeadSet", "1000000037").execute().getProperty("CreatedAt").getValue());

	}

	public void deleteEntity(ODataConsumer c) {
		// 删除记录
		boolean exists = true;
		try {
			String eds = c.getMetadata().getVersion();//DELETE有BUG 不会自动取Metadata 获取CSRF TOKEN,手动获取
			c.deleteEntity("OderHeadSet", "1000000037").execute();
		} catch (ODataProducerException e) {
			report("HttpStatus:"+e.getHttpStatus() + "  "+e.getMessage());
		}

		try {
			c.getEntity("OderHeadSet", "1000000037").execute();
		} catch (ODataProducerException e) {
			if (e.getHttpStatus().getStatusCode() == 404)
				exists = false;
		}
		report("newProduct " + (exists ? "exists" : "does not exist"));
	}

	public void getEntities_orderBy(ODataConsumer c) {
		// 排序
		for (OEntity enty : c.getEntities("OderHeadSet").orderBy("OderID desc").top(10).execute()) {
			reportEntity("订单: " + enty.getProperty("OderID").getValue(), enty);
		}
	}
}
