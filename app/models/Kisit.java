package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class Kisit extends Model {

	public String kisitAdi;
	public String kisitAciklama;

	@OneToMany(mappedBy = "kisit")
	public List<Tarife> tarifeler = new ArrayList<Tarife>();

	public Kisit(String kisitAdi, String kisitAciklama) {
		super();
		this.kisitAdi = kisitAdi;
		this.kisitAciklama = kisitAciklama;
	}

}
