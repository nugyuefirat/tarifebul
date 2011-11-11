package util;

import java.util.ArrayList;
import java.util.List;

import models.Paket;
import models.Tarife;
import play.Logger;

public class TarifeBulucu {
	/**
	 * Girilen parametrelere en uygun tarifelerin listesini render eder
	 * 
	 * @param yapilanKonusmaSuresi
	 *            Aylık ortalama konuşma süresi
	 * @param konusmaMesajYogunOperatorId
	 *            Ağırlıklı olarak arama yapılan operatör id
	 * @param konusmaYogunZamanId
	 *            Ağırlıklı olarak arama yapılan zaman (gündüz, gece, haftasonu,
	 *            karma)
	 * @param gonderilenSMSSayisi
	 *            Aylık ortalama SMS sayısı
	 * @param SMSYogunOperatorId
	 *            Ağırlıklı olarak sms gönderilen operatör id
	 * @param internetKullanimMiktari
	 *            Internet kullanım miktari MB cinsinden
	 * @param kisitlar
	 *            25 yaş altı, kamu personeli, öğrenci
	 * @param mevcutOperatorId
	 *            Kullanıcının mevcut operatörü
	 * @param mevcutHarcama
	 *            Kullanıcının mevcut ortalama iletişim harcaması
	 */
	public static List<Tarife> tarifeBul(int yapilanKonusmaSuresi,
			long konusmaMesajYogunOperatorId, 
			int gonderilenSMSSayisi, 
			int internetKullanimMiktari, long kisitId, 
			double mevcutHarcama) {

		List<Tarife> sonuc = new ArrayList<Tarife>();

		// Kullanıcının yoğun olarak aradığı veya mesaj gönderdiği bir operatör
		// varsa
		// Operatör içi dakikalar ve sms'leri kontrol eden, buna göre paketler
		// alan metot kullanılır
		List<Tarife> sonuclarByOperator = getirTarifelerByOperator(
				yapilanKonusmaSuresi, gonderilenSMSSayisi,
				internetKullanimMiktari, kisitId, 
				mevcutHarcama, konusmaMesajYogunOperatorId);
		// List<Tarife> sonuclarByZaman = new ArrayList<Tarife>();
		// Daha sonra bütün operatörler için her yöne dakika ve sms
		// ihtiyaçlarını karşılayan paketleri bulan metot kullanılır.
		List<Tarife> herYoneSonuclar = getirTarifelerHerYone(
				yapilanKonusmaSuresi, gonderilenSMSSayisi,
				internetKullanimMiktari, kisitId, 
				mevcutHarcama);

		sonuc.addAll(sonuclarByOperator);
		// sonuc.addAll(sonuclarByZaman);
		sonuc.addAll(herYoneSonuclar);

		// SONUÇ: sonuctarifeler listesi ücrete göre sıralanıp gönderilir.
		for (int t = 0; t < sonuc.size(); t++)
			Logger.info(sonuc.get(t).toString());
		
		return sonuc;
	}

	public static List<Tarife> getirTarifelerByOperator(
			int yapilanKonusmaSuresi, int gonderilenSMSSayisi,
			int internetKullanimMiktari, long kisitId, 
			double mevcutHarcama, long konusmaMesajYogunOperatorId) {

		Logger.info("GetirTarifelerByOperator");
		DbConnector db = new DbConnector();

		// sadece yoğun aranan ve mesaj operatörde, kısıtı sağlayan veya kısıt
		// içermeyen tarifeleri getir.
		// sadece kullanıcının aylık harcamasından düşük olan tarifeleri getir.
		// tarifelerin sabit ücretine göre sıralı şekilde
		List<Tarife> tarifeler = db.tarifeGetirByOperator(kisitId,
				mevcutHarcama, konusmaMesajYogunOperatorId);
		List<Tarife> sonucTarifeler = new ArrayList<Tarife>();
		// Logger.info(tarifeler.get(0).toString());
		// Logger.info(tarifeler.get(1).toString());

		// bulunan her bir tarife için yapılacak işlemler

		for (int bt = 0; bt < tarifeler.size(); bt++) {
			Tarife bakilanTarife = tarifeler.get(bt);
			Logger.info("Bakilan Tarife:" + bakilanTarife.toString());
			boolean konusmaYeterli = true;
			boolean SMSYeterli = true;
			boolean internetYeterli = true;

			// adım 1- tarifenin ücretsiz konuşma, sms ve internet'inin
			// yeterliliklerinin kontrol edilmesi
			if (bakilanTarife.ucretsizOperatorIciDk < yapilanKonusmaSuresi)// operator
																			// ici!
				konusmaYeterli = false;

			if (bakilanTarife.ucretsizOperatorIciSMSSayisi < gonderilenSMSSayisi)// operator
																					// ici!
				SMSYeterli = false;

			if (bakilanTarife.ucretsizInternetKullanimMiktari < internetKullanimMiktari)
				internetYeterli = false;
			Logger.info("operator ici konusmaYeterli:" + konusmaYeterli);
			Logger.info("operator ici smsYeterli:" + SMSYeterli);
			Logger.info("internetYeterli:" + internetYeterli);
			if (konusmaYeterli && SMSYeterli && internetYeterli) // tümü yeterli
																	// ise
			{
				sonucTarifeler.add(bakilanTarife);// direkt ekle ve sonraki
													// tarifeye geç
				Logger.info("Tümü yeterli, eklendi, sonraki tarifeye geçiliyor.");
				continue;
			}
			// üçlü yetersizlik operatör içi üçlü paket olmadığından tekli
			// paketlerle gideriliyor
			// her bir yetersizliğin teker teker paketlerle giderilmesi
			Tarife paketliTarife = new Tarife(bakilanTarife);
			if (!SMSYeterli) // SMS yetersiz ise
			{
				int ihtiyac = gonderilenSMSSayisi
						- bakilanTarife.ucretsizOperatorIciSMSSayisi;
				Paket uygulanacakSmsPaketi = db.operatorIciSmsPaketiGetir(
						konusmaMesajYogunOperatorId, bakilanTarife, ihtiyac);
				// burada yeterli en ucuz paketi bulup ekliyor. sonrakileri
				// eklemiyor.
				// yeterli en uygun paket bulunmuş olur. sorguda get first
				// yapılsa
				// burada paket ücreti ile birlikte mevcut harcamayı aşım
				// kontrolü yapmadım
				if (uygulanacakSmsPaketi != null)
					paketliTarife = paketUygula(bakilanTarife,
							uygulanacakSmsPaketi);
				else{
					Logger.info("uygun uygulanacakSmsPaketi bulunamadı.");
					// uygun paket bulunamamış, bu tarifenin ihtiyaçları
					// karşılaması mümkün değil, devam et
					continue;
				}
			}
			if (!konusmaYeterli) // konuşma yetersiz ise
			{
				int ihtiyac = yapilanKonusmaSuresi
						- bakilanTarife.ucretsizOperatorIciDk;
				Paket konusmaPaketi = db.operatorIciKonusmaPaketiGetir(
						konusmaMesajYogunOperatorId, bakilanTarife, ihtiyac);
				// burada yeterli en ucuz paketi bulup ekliyor. sonrakileri
				// eklemiyor.
				// yeterli en uygun paket bulunmuş olur. sorguda get first
				// yapılsa
				// burada paket ücreti ile birlikte mevcut harcamayı aşım
				// kontrolü yapmadım
				if (konusmaPaketi != null)
					paketliTarife = paketUygula(paketliTarife, konusmaPaketi);
				else {
					Logger.info("uygun konusmaPaketi bulunamadı.");
					// uygun paket bulunamamış, bu tarifenin ihtiyaçları
					// karşılaması mümkün değil, devam et
					continue;
				}
			}
			if (!internetYeterli) // SMS yetersiz ise
			{
				int ihtiyac = internetKullanimMiktari
						- bakilanTarife.ucretsizInternetKullanimMiktari;
				Paket internetPaketi = db.internetPaketiGetir(bakilanTarife.operator.getId(),
						bakilanTarife, ihtiyac);
				// burada yeterli en ucuz paketi bulup ekliyor. sonrakileri
				// eklemiyor.
				// yeterli en uygun paket bulunmuş olur. sorguda get first
				// yapılsa
				// burada paket ücreti ile birlikte mevcut harcamayı aşım
				// kontrolü yapmadım
				if (internetPaketi != null)
					paketliTarife = paketUygula(paketliTarife, internetPaketi);
				else
					Logger.info("uygun internetPaketi bulunamadı.");
			}
			sonucTarifeler.add(paketliTarife);
			Logger.info("paketli tarife.tarifeId:" + paketliTarife.getId());
			Logger.info("bakilan tarife.tarifeId:" + bakilanTarife.getId());
			Logger.info("-------------------------------------------------------------");
			// bunların da bir yerlerde tutulması gerekli
			// Aktivasyon Ücreti bulma

			// AktivasyonUcreti au = (AktivasyonUcreti) AktivasyonUcreti.find(
			// "oncekiOperatorId=? and tarifeId=?", mevcutOperatorId,
			// paketliTarife.getId()).first();//calismiyor
			// long aid=1;
			// AktivasyonUcreti au=AktivasyonUcreti.findById(aid); //çalışmıyor
			// if (au != null)
			// {
			// Logger.info("au.tarifeId::", au.tarifeId);
			// Logger.info("au.onceki op:", au.oncekiOperatorId);
			// Logger.info("au.Aktivasyon Ucreti:", au.aktivasyonUcreti);
			// }
			//

			// taahhüt indirimi çalışmıyor
			// // Tarifeye ilişkin taahhüt indirimlerini bulunması
			// // null kontrolü yapılacak
			// long tid=1;
			// TaahhutIndirimi t1=TaahhutIndirimi.findById(tid);
			// Logger.info("t1: "+t1.indirimliUcret);
			//
			// List<TaahhutIndirimi> taahhutIndirimleri = TaahhutIndirimi.find(
			// "tarifeId=?", paketliTarife.getId()).fetch();
			// // bir taahhüt indirimi
			// TaahhutIndirimi ti1 = new TaahhutIndirimi();
			// if (taahhutIndirimleri != null)
			// ti1 = taahhutIndirimleri.get(0);
			//
			// // Yıllık kazancın bulunması
			// double eskiYillikHarcama = mevcutHarcama * 12;
			// // taahhut toplam indirim= indirim süresi* indirim miktarı
			// double taahhutToplamIndirim = (ti1.indirimSuresi)
			// * (paketliTarife.sabitUcret - ti1.indirimliUcret);
			// // yıllık harcama= tarifenin 12 aylık tutarı + aktivasyon ücreti
			// -
			// // taahhüt indirimi (varsa)
			// double yeniYillikHarcama = (paketliTarife.sabitUcret * 12)
			// + au.aktivasyonUcreti - taahhutToplamIndirim;
			// double yillikKazanc = eskiYillikHarcama - yeniYillikHarcama;
			// Logger.info("Yillik kazanç:", yillikKazanc);

		}
		return sonucTarifeler;
	}

	public static List<Tarife> getirTarifelerHerYone(int yapilanKonusmaSuresi,
			int gonderilenSMSSayisi, int internetKullanimMiktari, long kisitId,
			 double mevcutHarcama) {
		Logger.info("GetirTarifelerHerYone");
		DbConnector db = new DbConnector();

		// kısıtı sağlayan veya kısıt içermeyen tarifeleri getir.
		// sadece kullanıcının aylık harcamasından düşük olan tarifeleri getir.
		// tarifelerin sabit ücretine göre sıralı şekilde
		List<Tarife> tarifeler = db.tarifeGetirAll(kisitId, mevcutHarcama);
		List<Tarife> sonucTarifeler = new ArrayList<Tarife>();
		// Logger.info(tarifeler.get(0).toString());
		// Logger.info(tarifeler.get(1).toString());

		// bulunan her bir tarife için yapılacak işlemler

		for (int bt = 0; bt < tarifeler.size(); bt++) {
			Tarife bakilanTarife = tarifeler.get(bt);
			Logger.info("bakilan tarife:" + bakilanTarife.toString());
			boolean konusmaYeterli = true;
			boolean SMSYeterli = true;
			boolean internetYeterli = true;

			// adım 1- tarifenin ücretsiz konuşma, sms ve internet'inin
			// yeterliliklerinin kontrol edilmesi
			if (bakilanTarife.ucretsizHerYoneDk < yapilanKonusmaSuresi)
				konusmaYeterli = false;

			if (bakilanTarife.ucretsizHerYoneSMSSayisi < gonderilenSMSSayisi)
				SMSYeterli = false;

			if (bakilanTarife.ucretsizInternetKullanimMiktari < internetKullanimMiktari)
				internetYeterli = false;
			Logger.info("konusmaYeterli:" + konusmaYeterli);
			Logger.info("smsYeterli:" + SMSYeterli);
			Logger.info("internetYeterli:" + internetYeterli);
			if (konusmaYeterli && SMSYeterli && internetYeterli) // tümü yeterli
																	// ise
			{
				sonucTarifeler.add(bakilanTarife);// direkt ekle ve sonraki
													// tarifeye geç
				Logger.info("Tümü yeterli, sonraki tarifeye geçiliyor.");
				continue;
			}
			if (!konusmaYeterli && !SMSYeterli && !internetYeterli) // tümü
																	// yetersiz
																	// ise
			{
				// 3lü paketleri ara ve uygula
				// Ücretsiz her yöne dakikaları olan, ücretsiz internet
				// kullanımı olan, ücretsiz her yöne sms içeren,
				// tarife ile aynı operatördeki ve tarife faturalı ise faturalı,
				// faturasız ise faturasız tarifeleri bul
				List<Paket> ucluPaketler = db.ucluPaketleriGetir(
						bakilanTarife.operator.getId(), bakilanTarife);

				// paketlerin uygulanması
				for (int i = 0; i < ucluPaketler.size(); i++) {
					Paket uygulanacakPaket = ucluPaketler.get(i);
					Tarife ucluPaketliTarife = new Tarife(bakilanTarife);
					if (uygulanacakPaket != null)
						ucluPaketliTarife = paketUygula(ucluPaketliTarife,
								uygulanacakPaket);
					else
						Logger.info("uygun uclu paket bulunamadı.");
					sonucTarifeler.add(ucluPaketliTarife);
					// burada ucuz ya da pahalı bütün 3lü paketleri ekliyor
				}// for

			}// if
				// her bir yetersizliğin teker teker paketlerle giderilmesi
			Tarife paketliTarife = new Tarife(bakilanTarife); // burada deep
																// copy
			// sorunu olabilir
			if (!SMSYeterli) // SMS yetersiz ise
			{
				int ihtiyac = gonderilenSMSSayisi
						- bakilanTarife.ucretsizHerYoneSMSSayisi;
				Paket uygulanacakSmsPaketi = db.smsPaketiGetir(
						bakilanTarife.operator.getId(), bakilanTarife, ihtiyac);
				// burada yeterli en ucuz paketi bulup ekliyor. sonrakileri
				// eklemiyor.
				// yeterli en uygun paket bulunmuş olur. sorguda get first
				// yapılsa
				// burada paket ücreti ile birlikte mevcut harcamayı aşım
				// kontrolü yapmadım
				if (uygulanacakSmsPaketi != null)
					paketliTarife = paketUygula(bakilanTarife,
							uygulanacakSmsPaketi);
				else{
					Logger.info("uygun uygulanacakSmsPaketi bulunamadı.");
					// uygun paket bulunamamış, bu tarifenin ihtiyaçları
					// karşılaması mümkün değil, devam et
					continue;
				}
			}
			if (!konusmaYeterli) // konuşma yetersiz ise
			{
				int ihtiyac = yapilanKonusmaSuresi
						- bakilanTarife.ucretsizHerYoneDk;
				Paket konusmaPaketi = db.konusmaPaketiGetir(
						bakilanTarife.operator.getId(), bakilanTarife, ihtiyac);
				// burada yeterli en ucuz paketi bulup ekliyor. sonrakileri
				// eklemiyor.
				// yeterli en uygun paket bulunmuş olur. sorguda get first
				// yapılsa
				// burada paket ücreti ile birlikte mevcut harcamayı aşım
				// kontrolü yapmadım
				if (konusmaPaketi != null)
					paketliTarife = paketUygula(paketliTarife, konusmaPaketi);
				else{
					Logger.info("uygun konusmaPaketi bulunamadı.");
					// uygun paket bulunamamış, bu tarifenin ihtiyaçları
					// karşılaması mümkün değil, devam et
					continue;
				}
			}
			if (!internetYeterli) // SMS yetersiz ise
			{
				int ihtiyac = internetKullanimMiktari
						- bakilanTarife.ucretsizInternetKullanimMiktari;
				Paket internetPaketi = db.internetPaketiGetir(
						bakilanTarife.operator.getId(), bakilanTarife, ihtiyac);
				// burada yeterli en ucuz paketi bulup ekliyor. sonrakileri
				// eklemiyor.
				// yeterli en uygun paket bulunmuş olur. sorguda get first
				// yapılsa
				// burada paket ücreti ile birlikte mevcut harcamayı aşım
				// kontrolü yapmadım
				if (internetPaketi != null)
					paketliTarife = paketUygula(paketliTarife, internetPaketi);
				else
					Logger.info("uygun internetPaketi bulunamadı.");
			}
			sonucTarifeler.add(paketliTarife);
			Logger.info("paketli tarife.tarifeId:" + paketliTarife.getId());
			Logger.info("bakilan tarife.tarifeId:" + bakilanTarife.getId());
			Logger.info("----------------------------------------------------------------");
			// bunların da bir yerlerde tutulması gerekli
			// Aktivasyon Ücreti bulma

			// AktivasyonUcreti au = (AktivasyonUcreti) AktivasyonUcreti.find(
			// "oncekiOperatorId=? and tarifeId=?", mevcutOperatorId,
			// paketliTarife.getId()).first();//calismiyor
			// long aid=1;
			// AktivasyonUcreti au=AktivasyonUcreti.findById(aid); //çalışmıyor
			// if (au != null)
			// {
			// Logger.info("au.tarifeId::", au.tarifeId);
			// Logger.info("au.onceki op:", au.oncekiOperatorId);
			// Logger.info("au.Aktivasyon Ucreti:", au.aktivasyonUcreti);
			// }
			//

			// taahhüt indirimi çalışmıyor
			// // Tarifeye ilişkin taahhüt indirimlerini bulunması
			// // null kontrolü yapılacak
			// long tid=1;
			// TaahhutIndirimi t1=TaahhutIndirimi.findById(tid);
			// Logger.info("t1: "+t1.indirimliUcret);
			//
			// List<TaahhutIndirimi> taahhutIndirimleri = TaahhutIndirimi.find(
			// "tarifeId=?", paketliTarife.getId()).fetch();
			// // bir taahhüt indirimi
			// TaahhutIndirimi ti1 = new TaahhutIndirimi();
			// if (taahhutIndirimleri != null)
			// ti1 = taahhutIndirimleri.get(0);
			//
			// // Yıllık kazancın bulunması
			// double eskiYillikHarcama = mevcutHarcama * 12;
			// // taahhut toplam indirim= indirim süresi* indirim miktarı
			// double taahhutToplamIndirim = (ti1.indirimSuresi)
			// * (paketliTarife.sabitUcret - ti1.indirimliUcret);
			// // yıllık harcama= tarifenin 12 aylık tutarı + aktivasyon ücreti
			// -
			// // taahhüt indirimi (varsa)
			// double yeniYillikHarcama = (paketliTarife.sabitUcret * 12)
			// + au.aktivasyonUcreti - taahhutToplamIndirim;
			// double yillikKazanc = eskiYillikHarcama - yeniYillikHarcama;
			// Logger.info("Yillik kazanç:", yillikKazanc);

		}
		return sonucTarifeler;
	}

	public static Tarife paketUygula(Tarife paketUygulanacakTarife,
			Paket uygulanacakPaket) {
		Tarife paketliTarife = new Tarife(paketUygulanacakTarife);// deep copy?

		if (paketUygulanacakTarife.faturali) // faturalıysa ücret üzerine
												// eklenir
			paketliTarife.sabitUcret += uygulanacakPaket.ucret;
		// faturasız ise aylık yükleme şartı ile paketlerin ücretleri
		// kıyaslanmalıdır
		// veya paketlerin bakiyeden düşüleceği belirtilmelidir.
		// paket maliyetleri de tutulabilir tarife ücreti dışında

		paketliTarife.ucretsizHerYoneDk += uygulanacakPaket.ucretsizHerYoneDk;
		paketliTarife.ucretsizHerYoneSMSSayisi += uygulanacakPaket.ucretsizHerYoneSMSSayisi;
		paketliTarife.ucretsizInternetKullanimMiktari += uygulanacakPaket.ucretsizInternetKullanimMiktari;
		paketliTarife.tarifeAdi += " + " + uygulanacakPaket.paketAdi;
		Logger.info("uygulanan paket:" + uygulanacakPaket.paketAdi);
		return paketliTarife;
	}
}
