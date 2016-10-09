package odata4j.Client;

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.exceptions.ODataProducerException;

public class ZJSWL_MM_INVOICE_SRV extends AbstractLog {
	public static String _serviceUrl = "http://222.69.93.22:8080/sap/opu/odata/sap/ZJSWL_MM_INVOICE_SRV/";
	public static final String _USERNAME = "RFC_CALL";
	public static final String _PASSW0RD = "123456";
	public static void main(String[] args) {
//		//ZJSWL_MM_INVOICE_SRV app = new ZJSWL_MM_INVOICE_SRV();
//		// 基本身份验证
//		ODataConsumer c = ODataConsumers.newBuilder(_serviceUrl)
//				.setClientBehaviors(OClientBehaviors.basicAuth(_USERNAME, _PASSW0RD), new SAPCSRFBehaviour())
//				// .setFormatType(FormatType.JSON) json格式传输
//				.build();
//		try {
//			//app.getEntities_filter(c);
//		} catch (ODataProducerException e) {
//			System.out.println("HttpStatus:" + e.getHttpStatus());
//			System.out.println("Message1:" + e.getMessage());
//			System.out.println("Message1:" + e.getOError().getInnerError());
//		}catch(Exception e){
//			System.out.println("Message2:" + e.getMessage());
//		}

	}
	
//	public void getEntities_filter(ODataConsumer c) {
//		// 根据订单描述查询订单数据
//		OEntityKey rowKey1 = OEntityKey.create("Belnr", "5105600101", "Gjahr", "2016");
//		OEntity enty = c.getEntity("InvoiceSet",rowKey1).expand("Nav_Items").execute();
//		reportEntity("PANDH创建的第一个订单：", enty);
//	}
}
