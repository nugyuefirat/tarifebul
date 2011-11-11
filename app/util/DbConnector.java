package util;

import java.util.ArrayList;
import java.util.List;

import models.Paket;
import models.Tarife;
import play.Logger;

public class DbConnector {
	

	/**
	 * 3lü paketleri ara. Ücretsiz her yöne dakikaları olan, ücretsiz internet
	 * kullanımı olan, ücretsiz her yöne sms içeren, tarife ile aynı
	 * operatördeki ve tarife faturalı ise faturalı, faturasız ise faturasız
	 * tarifeleri bul
	 * 
	 * @param operatorId
	 * @param bakilanTarife
	 * @return
	 */
	public static List<Paket> ucluPaketleriGetir(long operatorId,
			Tarife bakilanTarife) {
		List<Paket> ucluPaketler = Paket
				.find("ucretsizHerYoneDk>? and ucretsizInternetKullanimMiktari>? and ucretsizHerYoneSMSSayisi>? and operator_id=? and faturali=? order by ucret",
						0, 0, 0, operatorId, bakilanTarife.faturali)
				.fetch();
		Logger.info("uclu paketler bulundu:" + ucluPaketler.size());
		return ucluPaketler;
	}

	public static Paket operatorIciSmsPaketiGetir(long operatorId,
			Tarife bakilanTarife, int ihtiyac) {
		Paket smsPaket = Paket
				.find("ucretsizHerYoneDk=? and ucretsizInternetKullanimMiktari=? and ucretsizOperatorIciSMSSayisi>=? and operator_id=? and faturali=? order by ucret",
						0, 0, ihtiyac, operatorId, bakilanTarife.faturali)
				.first();
		if(smsPaket!=null)
		Logger.info("sms paket bulundu:" + smsPaket.paketAdi);
		return smsPaket;
	}
	
	public static Paket smsPaketiGetir(long operatorId,
			Tarife bakilanTarife, int ihtiyac) {
		Paket smsPaket = Paket
				.find("ucretsizHerYoneDk=? and ucretsizInternetKullanimMiktari=? and ucretsizHerYoneSMSSayisi>=? and operator_id=? and faturali=? order by ucret",
						0, 0, ihtiyac, operatorId, bakilanTarife.faturali)
				.first();
		if(smsPaket!=null)
		Logger.info("sms paket bulundu:" + smsPaket.paketAdi);
		return smsPaket;
	}

	public static Paket operatorIciKonusmaPaketiGetir(long operatorId,
			Tarife bakilanTarife, int ihtiyac) {
		Paket konusmaPaket = Paket
				.find("ucretsizOperatorIciDk>=? and ucretsizInternetKullanimMiktari=? and ucretsizHerYoneSMSSayisi=? and operator_id=? and faturali=? order by ucret",
						ihtiyac, 0, 0, operatorId, bakilanTarife.faturali)
				.first();
		if(konusmaPaket!=null)
		Logger.info("konusma paketler bulundu:" + konusmaPaket.paketAdi);
		return konusmaPaket;
	}
	
	public static Paket konusmaPaketiGetir(long mevcutOperatorId,
			Tarife bakilanTarife, int ihtiyac) {
		Paket konusmaPaket = Paket
				.find("ucretsizHerYoneDk>=? and ucretsizInternetKullanimMiktari=? and ucretsizHerYoneSMSSayisi=? and operator_id=? and faturali=? order by ucret",
						ihtiyac, 0, 0, mevcutOperatorId, bakilanTarife.faturali)
				.first();
		if(konusmaPaket!=null)
		Logger.info("konusma paketler bulundu:" + konusmaPaket.paketAdi);
		return konusmaPaket;
	}

	public static Paket internetPaketiGetir(long operatorId,
			Tarife bakilanTarife, int ihtiyac) {
		Paket internetPaket = Paket
				.find("ucretsizHerYoneDk=? and ucretsizInternetKullanimMiktari>=? and ucretsizHerYoneSMSSayisi=? and operator_id=? and faturali=? order by ucret",
						0, ihtiyac, 0, operatorId, bakilanTarife.faturali)
				.first();
		if(internetPaket!=null)
		Logger.info("internet paketler bulundu:" + internetPaket.paketAdi);
		return internetPaket;
	}

	/**
	 * kısıtı sağlayan veya kısıt içermeyen tarifeleri getir. sadece
	 * kullanıcının aylık harcamasından düşük olan tarifeleri getir. tarifelerin
	 * sabit ücretine göre sıralı şekilde
	 * 
	 * @param kisitId
	 * @param mevcutHarcama
	 * @return
	 */
	public static List<Tarife> tarifeGetirAll(long kisitId, double mevcutHarcama) {
		List<Tarife> tarifeler = Tarife
				.find("(kisit_id=null or kisit_id=?) and sabitUcret<=? order by sabitUcret",
						kisitId, mevcutHarcama).fetch();
		Logger.info("tarifeler bulundu:" + tarifeler.size());
		return tarifeler;
	}

	/**
	 * sadece belirli bir operator icin kısıtı sağlayan veya kısıt içermeyen
	 * tarifeleri getir. sadece kullanıcının aylık harcamasından düşük olan
	 * tarifeleri getir. tarifelerin sabit ücretine göre sıralı şekilde
	 * 
	 * @param kisitId
	 * @param mevcutHarcama
	 * @param operatorId
	 * @return
	 */
	public static List<Tarife> tarifeGetirByOperator(long kisitId,
			double mevcutHarcama, long operatorId) {
		List<Tarife> tarifeler = Tarife
				.find("(kisit_id=null or kisit_id=?) and sabitUcret<=? and operator_id=? order by sabitUcret",
						kisitId, mevcutHarcama, operatorId).fetch();
		Logger.info("tarifeler bulundu:" + tarifeler.size());
		return tarifeler;
	}
}
