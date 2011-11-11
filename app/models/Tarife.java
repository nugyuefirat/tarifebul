package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class Tarife extends Model implements Cloneable {

	public String tarifeAdi;
	public boolean faturali;
	public boolean bireysel;
	
//	@OneToMany(mappedBy = "tarife")
//	public List<TaahhutIndirimi> taahhutIndirimleri;

	@ManyToOne
	public Operator operator;

	@ManyToOne
	public Kisit kisit;

	public double sabitUcret;

	// SMS Ucretleri
	public double operatorIciSMSUcreti;// TL
	public double operatorDisiSMSUcreti;// TL
	public double uluslararasiSMSUcreti;// TL
	// SMS Hediyeleri
	public int ucretsizOperatorIciSMSSayisi;// adet
	public int ucretsizHerYoneSMSSayisi;// adet
	public int ucretsizUluslararasiSMSSayisi;// adet
	// Konusma Ucretleri
	public double operatorIciAramaUcreti;// TL
	public double operatorDisiAramaUcreti;// TL
	public double evIsAramaUcreti;// TL
	public double uluslararasiAramaUcreti;// TL
	// Konusma Hediyeleri
	public int ucretsizGrupIciDk;// dk
	public int ucretsizTarifeIciDk;// dk
	public int ucretsizOperatorIciDk;// dk
	public int ucretsizHerYoneDk;// dk
	public int ucretsizEvIsDk;// dk
	public int ucretsizGeceDk;// dk
	public int ucretsizGunduzDk;// dk
	public int ucretsizHaftasonuDk;// dk
	public int ucretsizUluslararasiDk;// dk
	// Internet Ucretleri
	public double internetKullanimUcreti; // TL
	// Internet Hediyeleri
	public int ucretsizInternetKullanimMiktari; // MB

	public String aciklama;
	public int ucretlendirmePeriyodu; // Saniye
	public String url;
	public Date gecerlilikTarihi;

	public String toString() {
		return (this.tarifeAdi + "//" + this.aciklama + "//" + this.sabitUcret + "//");
	}

	public Tarife(Tarife t) {
		this(t.tarifeAdi, t.faturali, t.bireysel, t.operator, t.kisit, t.sabitUcret, t.operatorIciSMSUcreti, t.operatorDisiSMSUcreti, t.uluslararasiSMSUcreti, t.ucretsizOperatorIciSMSSayisi, t.ucretsizHerYoneSMSSayisi, t.ucretsizUluslararasiSMSSayisi, t.operatorIciAramaUcreti, t.operatorDisiAramaUcreti, t.evIsAramaUcreti, t.uluslararasiAramaUcreti, t.ucretsizGrupIciDk, t.ucretsizTarifeIciDk, t.ucretsizOperatorIciDk, t.ucretsizHerYoneDk, t.ucretsizEvIsDk, t.ucretsizGeceDk, t.ucretsizGunduzDk, t.ucretsizHaftasonuDk, t.ucretsizUluslararasiDk, t.internetKullanimUcreti, t.ucretsizInternetKullanimMiktari, t.aciklama, t.ucretlendirmePeriyodu, t.url);
				
	}



	public Tarife(String tarifeAdi, boolean faturali, boolean bireysel,
			Operator operator, Kisit kisit, double sabitUcret,
			double operatorIciSMSUcreti, double operatorDisiSMSUcreti,
			double uluslararasiSMSUcreti, int ucretsizOperatorIciSMSSayisi,
			int ucretsizHerYoneSMSSayisi, int ucretsizUluslararasiSMSSayisi,
			double operatorIciAramaUcreti, double operatorDisiAramaUcreti,
			double evIsAramaUcreti, double uluslararasiAramaUcreti,
			int ucretsizGrupIciDk, int ucretsizTarifeIciDk,
			int ucretsizOperatorIciDk, int ucretsizHerYoneDk,
			int ucretsizEvIsDk, int ucretsizGeceDk, int ucretsizGunduzDk,
			int ucretsizHaftasonuDk, int ucretsizUluslararasiDk,
			double internetKullanimUcreti, int ucretsizInternetKullanimMiktari,
			String aciklama, int ucretlendirmePeriyodu, String url) {
		super();
		this.tarifeAdi = tarifeAdi;
		this.faturali = faturali;
		this.bireysel = bireysel;
		this.operator = operator;
		this.kisit = kisit;
		this.sabitUcret = sabitUcret;
		this.operatorIciSMSUcreti = operatorIciSMSUcreti;
		this.operatorDisiSMSUcreti = operatorDisiSMSUcreti;
		this.uluslararasiSMSUcreti = uluslararasiSMSUcreti;
		this.ucretsizOperatorIciSMSSayisi = ucretsizOperatorIciSMSSayisi;
		this.ucretsizHerYoneSMSSayisi = ucretsizHerYoneSMSSayisi;
		this.ucretsizUluslararasiSMSSayisi = ucretsizUluslararasiSMSSayisi;
		this.operatorIciAramaUcreti = operatorIciAramaUcreti;
		this.operatorDisiAramaUcreti = operatorDisiAramaUcreti;
		this.evIsAramaUcreti = evIsAramaUcreti;
		this.uluslararasiAramaUcreti = uluslararasiAramaUcreti;
		this.ucretsizGrupIciDk = ucretsizGrupIciDk;
		this.ucretsizTarifeIciDk = ucretsizTarifeIciDk;
		this.ucretsizOperatorIciDk = ucretsizOperatorIciDk;
		this.ucretsizHerYoneDk = ucretsizHerYoneDk;
		this.ucretsizEvIsDk = ucretsizEvIsDk;
		this.ucretsizGeceDk = ucretsizGeceDk;
		this.ucretsizGunduzDk = ucretsizGunduzDk;
		this.ucretsizHaftasonuDk = ucretsizHaftasonuDk;
		this.ucretsizUluslararasiDk = ucretsizUluslararasiDk;
		this.internetKullanimUcreti = internetKullanimUcreti;
		this.ucretsizInternetKullanimMiktari = ucretsizInternetKullanimMiktari;
		this.aciklama = aciklama;
		this.ucretlendirmePeriyodu = ucretlendirmePeriyodu;
		this.url = url;
	}
	

	
}
