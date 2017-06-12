package com.vsf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import com.vsf.S15.RemoveGoods.*;

public class Utiles {


	public static boolean PrimeraLinea (int NumLinea){
		if (NumLinea==1){
			return true;
		}else {
			return false;
		}
	}


	// Devuelve la fecha de text como tipo XMLGregorianCalendar
	public static XMLGregorianCalendar FechaXML(String FechaTxt) throws DatatypeConfigurationException{
		DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
		Date Fecha = null ;
		// Temporal
		//if (FechaTxt.equals("")){
		//	FechaTxt="17540303";
		//}
		//
		try {
			Fecha=dateFormat.parse(FechaTxt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(Fecha);
		XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		date2.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
		date2.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		return date2;
	}

	// Devuelve la fecha de text como tipo XMLGregorianCalendar
	public static XMLGregorianCalendar TimeXML(String FechaTxt) throws DatatypeConfigurationException{
		DateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date Fecha = null ;
		// Temporal
		//if (FechaTxt.equals("")){
		//	FechaTxt="17540303";
		//}
		//
		try {
			Fecha=timeFormat.parse(FechaTxt);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(Fecha);
		XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		date2.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
		date2.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		return date2;
	}
	
	public static BigDecimal DecimalXML(String ImporteTxt){
		// Temporal
		//	if (ImporteTxt.equals("")){
		//		ImporteTxt="0";
		//	}
		//
		BigDecimal Importe = new BigDecimal(ImporteTxt);
		return Importe;
	}

	public static BigInteger IntXML(String ImporteTxt){
		BigInteger Valor = new BigInteger(ImporteTxt);
		return Valor;
	}

	// Comprueba si es un servicio o un dispositivo
	public static boolean EsServicio(String Contenido){
		String auxContenido=Contenido.trim();
		if (auxContenido.equals("S")){
			return true;
		} else{
			return false;
		}
	}
	// Comprueba si es un servicio o un dispositivo
	public static boolean EsDispositivo(String Contenido){
		String auxContenido=Contenido.trim();
		if (auxContenido.equals("D")){
			return true;
		} else{
			return false;
		}
	}

	// Comprueba si es un servicio, un dispositivo, coste o fee
	public static boolean EsCoste(String Contenido){
		String auxContenido=Contenido.trim();
		if (auxContenido.equals("C")){
			return true;
		} else{
			return false;
		}
	}

	
	public static void GeneraRemoveGoodServices ( RemoveGoodsServices s15RemoveGoodServices, String ficheroSalidaXML, String log_file) throws JAXBException, IOException{
		File ficheroXML= new File(ficheroSalidaXML);
		System.out.println("   Extracting file " + ficheroSalidaXML);
		JAXBContext contexto = JAXBContext.newInstance(s15RemoveGoodServices.getClass() );
		Marshaller marshaller = contexto.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
				Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, 
				"http://ifrs15.vodafone.com/events/removegoodsservices_v3 removegoodsservices_v3.xsd");
		marshaller.marshal(s15RemoveGoodServices,ficheroXML);	
		try {
		    String schemaLang = "http://www.w3.org/2001/XMLSchema";
		    SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
		    JAXBSource source = new JAXBSource(contexto, s15RemoveGoodServices);
		    Schema schema = factory.newSchema(new File("src\\xsd_v3\\removegoodsservices_v3.xsd"));
		    Validator validator = schema.newValidator();
		    validator.validate(source);
		} catch (SAXException e) {
			writeFile(log_file, "Error while validating file "+ficheroSalidaXML+" :" + e.getCause() + "\n");
		    //System.out.println(" sax exception :" + e.getCause());
		} catch (Exception ex) {
			writeFile(log_file, "excep :" + ex.getMessage());
		}
	}

	public static boolean CambioContrato(String ContractA, String ContractB){
		if (ContractA.trim().equals(ContractB.trim()))
		{
			return true;
		} else {
			return false;
		}
	}

	public static void addRemoveGoodServiceContractAtrib(RemoveGoodsServiceComplexType Contrato, 
			String f_eventType, String f_eventDate, String f_eventContractID,
			String f_companyCode) throws DatatypeConfigurationException{

		if (f_eventType.length()!=0) Contrato.setEventType(f_eventType);
		if (f_eventDate.length()!=0) Contrato.setEventDate(FechaXML(f_eventDate));
		if (f_eventContractID.length()!=0) Contrato.setEventContractID(f_eventContractID);
		if (f_companyCode.length()!=0) Contrato.setCompanyCode(f_companyCode);
	}
	
	public static void addServiceAtrib(ServiceComplexType Servicio, 
			String f_POB_ID_Unico, String f_POB_removeDate, 
			String f_POB_companyCode) throws DatatypeConfigurationException{
		
		if (f_POB_ID_Unico.length()!=0) Servicio.setServiceID(f_POB_ID_Unico);
		if (f_POB_removeDate.length()!=0) Servicio.setServiceEndDate(FechaXML(f_POB_removeDate));
		if (f_POB_companyCode.length()!=0) Servicio.setCompanyCode(f_POB_companyCode);
	}

	public static void addDeviceAtrib(DeviceComplexType Device, 
			String f_POB_ID_Unico, String f_POB_removeDate, 
			String f_POB_companyCode) throws DatatypeConfigurationException{
	
		if (f_POB_ID_Unico.length()!=0) Device.setDeviceID(f_POB_ID_Unico);
		if (f_POB_companyCode.length()!=0) Device.setCompanyCode(f_POB_companyCode);
		if (f_POB_removeDate.length()!=0) Device.setRemoveDate(FechaXML(f_POB_removeDate));
	}

	public static void addCostAtrib(CostComplexType Cost, 
			String f_POB_ID_Unico, String f_POB_removeDate, 
			String f_POB_companyCode) throws DatatypeConfigurationException{
		
		if (f_POB_ID_Unico.length()!=0) Cost.setCostID(f_POB_ID_Unico);
		if (f_POB_removeDate.length()!=0) Cost.setCostEndDate(FechaXML(f_POB_removeDate));
		if (f_POB_companyCode.length()!=0) Cost.setCompanyCode(f_POB_companyCode);
	}
	
	public static void EscribeHoraFileControl(String ficheroSalidaXML, long TiempoInicial){
		BufferedWriter out = null;
		try {
			//	out=new BufferedWriter(new FileWriter(Ruta+"Control_"+String.valueOf(Sufijo)+".txt", true));
			out=new BufferedWriter(new FileWriter(ficheroSalidaXML+"Control_Ejecucion.txt", true));
			out.write("#### Comienzo: " +String.valueOf(TiempoInicial)+"\r\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out !=null){
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public static long RestaFechas (long TiempoInicial, long TiempoFinal ){
		long Resta;
		Resta=TiempoFinal-TiempoInicial;
		//	return Resta/(1000);
		return Resta;
	}

	public static String EstimaTimepoRestante(long TotalRegistros, int IncLineas, long msecsInc, int TotalLineas, long msecsTotal){
		String Texto="";
		if ((msecsInc!=0 )&&(IncLineas!=0)&&(msecsTotal!=0)&&(TotalLineas!=0)&&(TotalRegistros!=0))
		{
			//		long Restante1=(TotalRegistros/IncLineas/msecsInc);
			if (msecsInc<0.0000001){
				msecsInc=(long) 0.0000001;
			}
			double mseclinea=IncLineas/msecsInc;
			double Totalsecs=TotalRegistros/mseclinea/1000;
			int Totalmins=(int) (Totalsecs/60);
			int TotalHoras=Totalmins/60;

			//		 Texto="     ->Estim. Final: "+String.valueOf(Totalmins)+ " mins ("+String.valueOf(TotalHoras)+" horas)";
			Texto="     ->Estim. Final: "+String.valueOf(Totalmins);

		}
		return Texto;

	}

	public static void AppendFicheroControl (int Fichero, String Ruta, int Sufijo, int Lineas,int Contratos, int Pobs,
			int IncLineas, int IncContratos, int IncPobs, long Tiempo, long msecTranscurridos,
			String Estimado){

		BufferedWriter out = null;
		try {
			//	out=new BufferedWriter(new FileWriter(Ruta+"Control_"+String.valueOf(Sufijo)+".txt", true));
			out=new BufferedWriter(new FileWriter(Ruta+"Control_Ejecucion.txt", true));
			out.write("File: " +String.valueOf(Fichero));
			out.write(" -Lin: " + String.valueOf(Lineas) +"(+"+String.valueOf(IncLineas)+") ");
			out.write(" -Cont: " +String.valueOf(Contratos) +"(+"+String.valueOf(IncContratos)+")");
			out.write(" -Pobs: " +String.valueOf(Pobs) +"(+"+String.valueOf(IncPobs)+") ");
			out.write(" # (msecs): " +String.valueOf(Tiempo) );
			out.write(" # Transcurrido: " +String.valueOf(msecTranscurridos)+" msecs - "+String.valueOf(msecTranscurridos/(1000*60))+" mins" );
			out.write(Estimado +"\r\n" );


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (out !=null){
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


	}


	public static void AddHeaderAttrib(FileHeaderComplexType fileHeader, String consumerType, String countryCode, String eventType,
			Date currentDate, Calendar cal, long l, String sourceEvent, String sourceOpCo) throws DatatypeConfigurationException {
		// TODO Auto-generated method stub
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		DateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String filename=consumerType+"_"+countryCode+"_"+eventType+"_"+dateFormat.format(currentDate)+"_"+l+".xml";
		
		BigInteger seqNumber = BigInteger.valueOf(l);
		
		fileHeader.setFileName(filename);
		fileHeader.setCreatedAt(Utiles.TimeXML(timeFormat.format(cal.getTime())));
		fileHeader.setFileSequenceID(seqNumber);
		fileHeader.setSourceEvent(sourceEvent);
		fileHeader.setSourceOpco(sourceOpCo);
		
	}
	
	
	public static void writeFile(String filename, String line) throws IOException {
		// TODO Auto-generated method stub
	    line = line.replace(";null",";");
	    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
	    fw.write(line);//appends the string to the file
	    fw.close();	
	}

	
	public static HashMap<String, Integer> getSeqID (String seq_file) throws NumberFormatException, IOException{
		
		String linea_seq;
		String[]Campos_seq;
		HashMap<String, Integer> SeqIDs = new HashMap<String, Integer>();
		try {
			BufferedReader reader =	new BufferedReader(new	FileReader(seq_file));
			while((linea_seq = reader.readLine())!=null) {
				Campos_seq = linea_seq.split(";"); // Deconstruyo el registro en campos
				SeqIDs.put(Campos_seq[0], new Integer(Campos_seq[1]));
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File "+seq_file+" not found.");
			//e.printStackTrace();		
		}
		
		return SeqIDs;
		
	}
	
	public static void updateSeqID(String filename, HashMap<String, Integer> oldSeqs, HashMap<String, Integer> newSeqs) throws IOException {
		// TODO Auto-generated method stub
		
	    FileWriter fw = new FileWriter(filename,false); //the true will append the new data
		String header = "/* *************************************************************************************** */\r\n" + 
				"/* [SCRIPT_NAME]     :TWU15Y12.sql                                                         */\r\n" + 
				"/* [CREATOR]         :TERADATA                                                             */\r\n" + 
				"/* [CREATED_DATE]    :20170516                                                             */\r\n" + 
				"/* [CHANGED DATE]    :                                                                     */\r\n" + 
				"/* [DESCRIPTIOND]    :Actualización de IDs de secuencia en la tabla S15_X_SEQUENCEID       */\r\n" + 
				"/* *************************************************************************************** */\r\n" + 
				"\r\n" + 
				".SIDETITLES\r\n" + 
				".REMARK '################################################################################'\r\n" + 
				".REMARK '#            ACTUALIZACION DE LA TABLA S15_X_SEQUENCEID                        #'\r\n" + 
				".REMARK '################################################################################'\r\n" + 
				"  \r\n" + 
				"  ";
		
		String footer = ".IF ERRORCODE= 0  THEN .GOTO CONTINUAR_2\r\n" + 
				".REMARK '################################################################################'\r\n" + 
				".REMARK '#       ERROR EN LA ACTUALIZACION DE LA TABLA S15_X_SEQUENCEID                 #'\r\n" + 
				".REMARK '################################################################################'\r\n" + 
				".QUIT ${ERROR_INSERT}\r\n" + 
				"\r\n" + 
				".LABEL CONTINUAR_2\r\n" + 
				"\r\n" + 
				".REMARK '################################################################################'\r\n" + 
				".REMARK '# PROCESO TWU15Y12 FINALIZADO CORRECTAMENTE                                    #'\r\n" + 
				".REMARK '################################################################################'\r\n" + 
				"\r\n" + 
				".QUIT ${OK}\r\n" + 
				"\r\n" + 
				"/*==============================================================*/\r\n" + 
				"/* End job                                                      */\r\n" + 
				"/*==============================================================*/\r\n" + 
				"  \r\n" + 
				"";
	    
		fw.write(header);
		
	    Date FechaActual=new Date();
	    String modifiedDate= new SimpleDateFormat("yyyyMMdd").format(FechaActual);
	    java.util.Iterator<Entry<String, Integer>> itOldSeqs = oldSeqs.entrySet().iterator();
	    while (itOldSeqs.hasNext()) {
	    	String key = itOldSeqs.next().getKey();
	    	
	    	if (!oldSeqs.get(key).equals(newSeqs.get(key))){
	    		String update="UPDATE ${DBDWH}.S15_X_SEQUENCEID SET ESTADO_REGISTRO='M', FX_FIN_VIGENCIA='"+modifiedDate+
	    				"' WHERE VARNAME='"+key+"' AND FX_FIN_VIGENCIA='35000101' ;\r\n" ;
	    		String insert="INSERT INTO ${DBDWH}.S15_X_SEQUENCEID (VARNAME, VARVALUE, "
	    				+ "FX_INICIO_VIGENCIA, FX_FIN_VIGENCIA, ESTADO_REGISTRO, FX_CARGA)\r\n"
	    				+ "	VALUES	('"+key+"', '"+ newSeqs.get(key).toString() +"' , '" + modifiedDate+ "', '35000101', 'A', '"
	    				+ modifiedDate +"');\r\n\r\n" ;
	    		fw.write(update);
	    		fw.write(insert);
	    	}
	    }
	    fw.write(footer);
	    fw.close();	
	}




}
