package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class Paket extends Model {
	public String paketAdi;
	public boolean faturali;
	
	@ManyToOne
	public Operator operator;
	
	public double ucret;
	
	//SMS Hediyeleri
	public int ucretsizOperatorIciSMSSayisi;// adet
	public int ucretsizHerYoneSMSSayisi;// adet
	public int ucretsizUluslararasiSMSSayisi;// adet
	
	//Konusma Hediyeleri
	public int ucretsizGrupIciDk;//dk
	public int ucretsizTarifeIciDk;//dk
	public int ucretsizOperatorIciDk;//dk
	public int ucretsizHerYoneDk;//dk
	public int ucretsizEvIsDk;//dk
	public int ucretsizGeceDk;//dk
	public int ucretsizGunduzDk;//dk
	public int ucretsizHaftasonuDk;//dk
	public int ucretsizUluslararasiDk;//dk
	
	//Internet Hediyeleri
	public int ucretsizInternetKullanimMiktari; //MB

	public Date gecerlilikTarihi;
	
	
}
