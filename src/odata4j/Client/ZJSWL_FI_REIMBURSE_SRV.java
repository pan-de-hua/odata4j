package odata4j.Client;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperties;
import org.odata4j.exceptions.ODataProducerException;

public class ZJSWL_FI_REIMBURSE_SRV extends AbstractLog {
	public static String _serviceUrl = "http://222.69.93.22:8080/sap/opu/odata/sap/ZJSWL_FI_REIMBURSE_SRV/";
	public static final String _USERNAME = "RFC_CALL";
	public static final String _PASSW0RD = "123456";
	public static void main(String[] args) {
		ZJSWL_FI_REIMBURSE_SRV app = new ZJSWL_FI_REIMBURSE_SRV();
		// 基本身份验证
		ODataConsumer c = ODataConsumers.newBuilder(_serviceUrl)
				.setClientBehaviors(OClientBehaviors.basicAuth(_USERNAME, _PASSW0RD), new SAPCSRFBehaviour())
				// .setFormatType(FormatType.JSON) json格式传输
				.build();
		try {
			app.createEntity(c);
		} catch (ODataProducerException e) {
			System.out.println("HttpStatus:" + e.getHttpStatus());
			System.out.println("Message1:" + e.getMessage());
			System.out.println("Message1:" + e.getOError().getInnerError());
		}catch(Exception e){
			System.out.println("Message2:" + e.getMessage());
		}

	}
	public void createEntity(ODataConsumer c) {
		// 创建一条报销
		
		OEntity newDocI = c.createEntity("DocumentItemSet")
				.properties(OProperties.string("Buzei", "1"))
				.properties(OProperties.string("Hkont", "5001280000"))
				.properties(OProperties.string("Kostl", "C100010007"))
				.properties(OProperties.string("Dmbtr", "100.00"))
				.get();

		OEntity newDocI2 = c.createEntity("DocumentItemSet")
				.properties(OProperties.string("Buzei", "2"))
				.properties(OProperties.string("Hkont", "5001280000"))
				.properties(OProperties.string("Kostl", "C100010008"))
				.properties(OProperties.string("Dmbtr", "101.00"))
				.get();
		OEntity docItems[] ={newDocI,newDocI2}; 
		OEntity newDocH = c.createEntity("DocumentHeadSet")
				.properties(OProperties.string("Bukrs", "1000"))
				.properties(OProperties.string("Bldat", "20160929"))
				.properties(OProperties.string("Budat", "20160929"))
				.properties(OProperties.string("Bktxt", "费用报销人"))
				.properties(OProperties.string("Xblnr", "OA申请编号"))
				.inline("DocumentItemSet", docItems)
				.execute();

		report("报销凭证创建: " + newDocH);
	}

}
