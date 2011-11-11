package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class Operator extends Model {

	public String operatorAdi;

	@OneToMany(mappedBy = "operator")
	public List<Tarife> tarifeler = new ArrayList<Tarife>();

	@OneToMany(mappedBy = "operator")
	public List<Paket> paketler = new ArrayList<Paket>();

	public Operator(String operatorAdi) {
		super();
		this.operatorAdi = operatorAdi;

	}

}
