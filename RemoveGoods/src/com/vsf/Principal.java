package com.vsf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
//import java.util.Vector;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import com.vsf.S15.RemoveGoods.*;
import com.vsf.*;

public class Principal {

	static int NumLinea=0;
	// Indices de los campos al deconstruir la linea del fichero
	// Para el evento/Contrato
	static final int f_eventType			=0;
	static final int f_eventDate			=1;
	static final int f_eventContractID		=2;
	static final int f_companyCode			=3;
	static final int f_TipoElemento			=4;	
	static final int f_POB_ID_Unico			=5;
	static final int f_POB_removeDate		=6;
	static final int f_POB_companyCode		=7;

	static String ID_Fee="";
	static String POB_Code="";

	// Para Windows
	// static final String ficheroSalidaXML="C:\\Users\\cesar.fuentes\\workspace\\Termination\\Termination";
	// static final String ficheroSalidaControl="C:\\Users\\cesar.fuentes\\workspace\\Termination\\";

	// PAra unix
		static final String ficheroSalidaControl="";
		static final String ficheroSalidaXML="removeGoods";

	static int NumFichero =0;
	static int NumContratos=0;
	static int NumContratosXfichero=3;
	static int CtrlNumPOBs=0;
	static int CtrlContratos=0;
	static int AntCtrlNumPOBs=0;
	static int AntCNumLinea=0;
	static int AntNumContratos=0;
	static long TiempoInicial;
	static long TiempoFinal;
	static Date fechaInicio=new Date();
	static Date fechaFin=new Date();
	static long AntTiempo;
	static long TiempoActual;
	static Date AntFecha=new Date();
	static Date FechaActual=new Date();
	static long TiempoTranscurrido;
	static long TotalRegistros=11000000;

	public static void main(String[] args) throws IOException, DatatypeConfigurationException, JAXBException, InstantiationException, IllegalAccessException  {
		// TODO Auto-generated method stub
		String Fichero=args[0];
		String linea;
		String[]Campos;
		// Inicializamos objetos
		RemoveGoodsServices S15RemoveGoodsService = new RemoveGoodsServices();
		RemoveGoodsServicesComplexType lstRemoveGoodServices = new RemoveGoodsServicesComplexType();
		RemoveGoodsServiceComplexType tdRemoveGoodsService = new RemoveGoodsServiceComplexType();
		ServiceListComplexType vListaServ = new ServiceListComplexType(); // Nueva Lista de servicios
		DeviceListComplexType vListaDev = new DeviceListComplexType();// Nueva Lista de servicios
		ServiceComplexType Servicio = new ServiceComplexType();
		DeviceComplexType Device = new DeviceComplexType();

		// Variable para guardar y detectar el cambio de contrato
		String aContratcID="";
		System.out.println("Comenzando generacion XML...");
		NumLinea=1;
		TiempoInicial=fechaInicio.getTime();
		AntTiempo=TiempoInicial;
		// Lectura del Fichero
		try {
			BufferedReader reader =	new BufferedReader(new	FileReader(Fichero));
			while((linea = reader.readLine())!=null) {
				Campos = linea.split(";"); // Deconstruyo el registro en campos
				// Si cambia el contrato, generamos nuevo grupo
				if (!aContratcID.equals(Campos[f_eventContractID].trim())){
					NumContratos ++;
					// Comprueba cuantos contratos se van a mandar en cada fichero. Controla que sea cuando finaliza el evento.
					if (NumContratosXfichero<NumContratos)
					{
						AntNumContratos=CtrlContratos;
						CtrlContratos=AntNumContratos+NumContratos;
						Utiles.GeneraRemoveGoodServices(S15RemoveGoodsService, ficheroSalidaXML, NumFichero);
						NumFichero ++; // Incrementamos para saber el numero de ficheros generados
						NumContratos=1;
						// Escribe el fichero de control de exportacion
						TiempoActual=new Date().getTime();
						long Tiempo=Utiles.RestaFechas(AntTiempo, TiempoActual);
						long msecTranscurridos=Utiles.RestaFechas(TiempoInicial, TiempoActual);
						TotalRegistros=TotalRegistros-NumLinea;
						String Estimado1=Utiles.EstimaTimepoRestante(TotalRegistros,NumLinea-AntCNumLinea,Tiempo,
								NumLinea,msecTranscurridos );
						Utiles.AppendFicheroControl(NumFichero, ficheroSalidaControl, NumFichero, 
								NumLinea, CtrlContratos, CtrlNumPOBs,
								NumLinea-AntCNumLinea,CtrlContratos-AntNumContratos,CtrlNumPOBs-AntCtrlNumPOBs,Tiempo,msecTranscurridos,Estimado1);
						AntCtrlNumPOBs=CtrlNumPOBs;
						AntCNumLinea=NumLinea;
						AntTiempo=TiempoActual;
						S15RemoveGoodsService = new RemoveGoodsServices();
						lstRemoveGoodServices = new RemoveGoodsServicesComplexType();
					}
					tdRemoveGoodsService = new RemoveGoodsServiceComplexType();
					lstRemoveGoodServices.getRemoveGoodsServices().add(tdRemoveGoodsService);
					S15RemoveGoodsService.setData(lstRemoveGoodServices);
					vListaServ = new ServiceListComplexType();
					vListaDev = new DeviceListComplexType();
					tdRemoveGoodsService.setServiceList(vListaServ);// Añadimos la lista al inception
					tdRemoveGoodsService.setDeviceList(vListaDev);// Añadimos la lista al inception
					Utiles.addRemoveGoodServiceContractAtrib(tdRemoveGoodsService, 
							Campos[f_eventType], 
							Campos[f_eventDate], 
							Campos[f_eventContractID],
							Campos[f_companyCode]);
				}
				aContratcID=Campos[f_eventContractID].trim();
				if (Utiles.EsServicio(Campos[f_TipoElemento])){
					Servicio = new ServiceComplexType();
					Utiles.addServiceAtrib(Servicio,
							Campos[f_POB_ID_Unico],
							Campos[f_POB_removeDate],
							Campos[f_POB_companyCode]);
					vListaServ.getService().add(Servicio);
					CtrlNumPOBs ++;	
				} else {
					Device = new DeviceComplexType();
					Utiles.addDeviceAtrib(Device,
							Campos[f_POB_ID_Unico],
							Campos[f_POB_removeDate],
							Campos[f_POB_companyCode]);
					vListaDev.getDevice().add(Device);
					CtrlNumPOBs ++;	
				}
				NumLinea ++;
				///////////////////////////
				/// FIN LECTURA FICHERO //
				///////////////////////////
			} // Fin lectura lineas fichero
			reader.close();
			//NumFichero ++;
			// Cuando acaba con el fichero imprime el resto.
			if (NumContratosXfichero>=NumContratos ){
				Utiles.GeneraRemoveGoodServices(S15RemoveGoodsService, ficheroSalidaXML, NumFichero); // Comentado en pruebas
				// Escribe el fichero de control de exportacion
				AntNumContratos=CtrlContratos;
				CtrlContratos=AntNumContratos+NumContratos;
				TiempoActual=new Date().getTime();
				long Tiempo=Utiles.RestaFechas(AntTiempo, TiempoActual);
				long msecTranscurridos=Utiles.RestaFechas(TiempoInicial, TiempoActual);
				TotalRegistros=TotalRegistros-NumLinea;
				String Estimado1=Utiles.EstimaTimepoRestante(TotalRegistros,NumLinea-AntCNumLinea,Tiempo,
						NumLinea,msecTranscurridos );
				Utiles.AppendFicheroControl(NumFichero, ficheroSalidaControl, NumFichero, 
						NumLinea, CtrlContratos, CtrlNumPOBs,
						NumLinea-AntCNumLinea,CtrlContratos-AntNumContratos,CtrlNumPOBs-AntCtrlNumPOBs,Tiempo,msecTranscurridos,Estimado1);
				AntCtrlNumPOBs=CtrlNumPOBs;
				AntCNumLinea=NumLinea;
				AntNumContratos=NumContratos;
				AntTiempo=TiempoActual;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TiempoFinal=fechaFin.getTime();
		//		Utiles.EscribeHoraFileControl(Fichero, TiempoFinal);
		System.out.println("Finalizada extraccion !!!!");	
	}
}