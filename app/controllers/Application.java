package controllers;

import play.*;
import play.mvc.*;
import util.DbConnector;
import util.GSonSerializer;
import util.TarifeBulucu;

import java.util.*;

import models.*;

public class Application extends Controller {

	public static void index() {
		// Logger.info("tarifeBulBeginner starts");
		// tarifeBulBeginner(100, 100, 100); //çalışıyor
		// Logger.info("tarifeBulBeginner ends");
//		int yapilanKonusmaSuresi = 200;
//		long konusmaMesajYogunOperatorId = 1;
//		int konusmaYogunZamanId = 1; // gunduz-gece-haftasonu-değişiyor
//		int gonderilenSMSSayisi = 300;
//		long SMSYogunOperatorId = 1; // şimdilik devre dışı
//		int internetKullanimMiktari = 0;
//		long kisitId = 1;
//		long mevcutOperatorId = 1;
//		double mevcutHarcama = 30;
		Logger.info("tarifeBul starts");

//		TarifeBulucu tb = new TarifeBulucu();
//		tb.tarifeBul(yapilanKonusmaSuresi, konusmaMesajYogunOperatorId,
//				konusmaYogunZamanId, gonderilenSMSSayisi, SMSYogunOperatorId,
//				internetKullanimMiktari, kisitId, mevcutOperatorId,
//				mevcutHarcama);
//		Logger.info("tarifeBulBeginner ends");
		
		List<Operator> operatorler= Operator.findAll();
		
		List<Kisit> kisitlar= Kisit.findAll();
		
		render(operatorler,kisitlar);

	}
	
	public static void tarifeBul(int yapilanKonusmaSuresi,
			long konusmaMesajYogunOperatorId, 
			int gonderilenSMSSayisi, 
			int internetKullanimMiktari, long kisitId,
			double mevcutHarcama) {
		TarifeBulucu tb = new TarifeBulucu();
		List<Tarife> tarifeler= tb.tarifeBul(yapilanKonusmaSuresi, konusmaMesajYogunOperatorId,
				gonderilenSMSSayisi, internetKullanimMiktari, kisitId, 
				mevcutHarcama);
		List<Class<?>> classesToSkip = new ArrayList<Class<?>>();
		classesToSkip.add(TaahhutIndirimi.class);
		classesToSkip.add(AktivasyonUcreti.class);
		classesToSkip.add(Kisit.class);
		classesToSkip.add(Operator.class);
		classesToSkip.add(Paket.class);
		renderJSON(GSonSerializer.createGSonBuilder(classesToSkip).toJson(
				tarifeler));
		
	}

}