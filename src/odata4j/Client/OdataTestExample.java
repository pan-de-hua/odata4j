package odata4j.Client;

import org.joda.time.LocalDateTime;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.BasicAuthenticationBehavior;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OFuncs;
import org.odata4j.core.OProperties;
import org.odata4j.exceptions.ODataProducerException;
import org.odata4j.format.FormatType;

public class OdataTestExample extends AbstractLog {
	static String serviceUrl = "http://services.odata.org/V2/(S(blhcfbj12itplabvgbmlhebp))/OData/OData.svc";

	public static void main(String[] args) {
		OdataTestExample app = new OdataTestExample();
		//Ĭ��XML��ʽ����
		// ODataConsumer c = ODataConsumers.create(serviceUrl);
		//json��ʽ����
		//ODataConsumer c = ODataConsumers.newBuilder(serviceUrl).setFormatType(FormatType.JSON).build();
		//���������֤
//		ODataConsumer c = ODataConsumers.newBuilder(serviceUrl)
//		        .setClientBehaviors(OClientBehaviors.basicAuth("user", "password"))
//		        .build();
		 ODataConsumer c = ODataConsumers.newBuilder(serviceUrl)
			        .setClientBehaviors(new BasicAuthenticationBehavior("RFC_CALL", "123456"),new SAPCSRFBehaviour())
			        .build();

		app.getEntities_orderBy(c);
	}

	public void getMetadata(ODataConsumer c) {
		reportMetadata(c.getMetadata());
	}

	public void getEntity(ODataConsumer c) {
		// ����Keyֵ ��ƷIDȡ��Ʒ��Ŀ
		OEntity product = c.getEntity("Products", 1).execute();
		reportEntity("Product: " + product.getProperty("Name").getValue(), product);
	}

	public void getEntities(ODataConsumer c) {
		// ȡ���еĲ�Ʒ����
		for (OEntity product : c.getEntities("Products").top(10).execute()) {
			reportEntity("Product: " + product.getProperty("Name").getValue(), product);
		}
	}

	public void getEntities_filter(ODataConsumer c) {
		// ���ݲ�Ʒ������ѯ��Ʒ����
		OEntity dvdPlayer = c.getEntities("Products").filter("Description eq '1080P Upconversion DVD Player'").execute()
				.first();
		reportEntity("DVD Player", dvdPlayer);
	}

	public void createEntity(ODataConsumer c) {
		// ����һ����Ʒ����
		OEntity newProduct = c.createEntity("Products").properties(OProperties.int32("ID", 10))
				.properties(OProperties.string("Name", "Josta"))
				.properties(OProperties.string("Description", "With guaraná"))
				.properties(OProperties.datetime("ReleaseDate", new LocalDateTime()))
				.properties(OProperties.int32("Rating", 1)).properties(OProperties.decimal("Price", 1.23)).execute();

		report("newProduct: " + newProduct);
	}

	public void updateEntity(ODataConsumer c) {
		OEntity newProduct = c.createEntity("Products").properties(OProperties.int32("ID", 10))
				.properties(OProperties.string("Name", "Josta"))
				.properties(OProperties.string("Description", "With guaraná"))
				.properties(OProperties.datetime("ReleaseDate", new LocalDateTime()))
				.properties(OProperties.int32("Rating", 1)).properties(OProperties.decimal("Price", 1.23)).execute();

		// ���²�Ʒ����
		c.updateEntity(newProduct).properties(OProperties.int32("Rating", 5)).execute();
		report("newProduct rating after update: "
				+ c.getEntity("Products", 10).execute().getProperty("Rating").getValue());

	}

	public void mergeEntity(ODataConsumer c) {
		// ��merge�������²�Ʒ����
		c.mergeEntity("Products", 10).properties(OProperties.int32("Rating", 500)).execute();

		report("newProduct rating after merge: "
				+ c.getEntity("Products", 10).execute().getProperty("Rating").getValue());

	}

	public void deleteEntity(ODataConsumer c) {
		// ɾ����¼
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
		// ����
		report("highest rated product (compute on server): "
				+ c.getEntities("Products").orderBy("Rating desc").top(1).execute().first());
		report("highest rated product (compute on client): " + c.getEntities("Products").execute()
				.orderBy(OFuncs.entityPropertyValue("Rating", Integer.class)).last());

	}
}
